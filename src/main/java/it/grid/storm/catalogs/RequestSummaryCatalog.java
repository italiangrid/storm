/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.catalogs.timertasks.RequestsGarbageCollector;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Class that represents the RequestSummaryCatalog of StoRM. The rows in the
 * catalog are called RequestSummaryData. Methods are provided to: look up newly
 * added requests as identified by their SRM_REQUEST_QUEUED status, to update
 * the global status of the request, and to fail a request with SRM_FAILURE.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date April 26th, 2005
 */
public class RequestSummaryCatalog {

	private static final Logger log = LoggerFactory
		.getLogger(RequestSummaryCatalog.class);
	/** Only instance of RequestSummaryCatalog for StoRM! */
	private static RequestSummaryCatalog cat = new RequestSummaryCatalog();
	/** WARNING!!! TO BE MODIFIED WITH FACTORY!!! */
	private final RequestSummaryDAO dao = RequestSummaryDAO.getInstance();
	/** timer thread that will run a task to clean */
	private Timer clock = null;
	/** configuration instance **/
	private final Configuration config = Configuration.getInstance();

	private RequestSummaryCatalog() {

		clock = new Timer();
		
		clock.schedule(
			new RequestsGarbageCollector(clock,
				config.getRequestPurgerPeriod() * 1000),
			config.getRequestPurgerDelay() * 1000);
	}

	/**
	 * Method that returns the only instance of RequestSummaryCatalog present in
	 * StoRM.
	 */
	public static RequestSummaryCatalog getInstance() {

		return RequestSummaryCatalog.cat;
	}

	/**
	 * Method in charge of retrieving RequestSummaryData associated to new
	 * requests, that is those found in SRM_REQUETS_QUEUED global status; such
	 * requests then transit into SRM_SUCCESS. The actual number of fetched
	 * requests depends on the configured ceiling.
	 * 
	 * If no new request is found, an empty Collection is returned. if a request
	 * is malformed, then that request is failed and an attempt is made to signal
	 * such occurrence in the DB. Only correctly formed requests are returned.
	 */
	synchronized public Collection<RequestSummaryData> fetchNewRequests(
		int capacity) {

		List<RequestSummaryData> list = Lists.newArrayList();
		
		Collection<RequestSummaryDataTO> c = dao.findNew(capacity);
		if (c == null || c.isEmpty()) {
			return list;
		}
		int fetched = c.size();
		log.debug("REQUEST SUMMARY CATALOG: {} new requests picked up.", fetched);
		for (RequestSummaryDataTO auxTO : c) {
			RequestSummaryData aux = null;
			try {
				aux = makeOne(auxTO);
			} catch (IllegalArgumentException e) {
				log.error("REQUEST SUMMARY CATALOG: Failure while performing makeOne "
					+ "operation. IllegalArgumentException: {}", e.getMessage(), e);
				continue;
			}
			if (aux != null) {
				log.debug("REQUEST SUMMARY CATALOG: {} associated to {} included "
					+ "for processing", aux.requestToken(), aux.gridUser().getDn());
				list.add(aux);
			}
		}
		int ret = list.size();
		if (ret < fetched) {
			log.warn("REQUEST SUMMARY CATALOG: including {} requests for processing, "
				+ "since the dropped ones were malformed!", ret);
		} else {
			log.debug("REQUEST SUMMARY CATALOG: including for processing all {} "
				+ "requests.", ret);
		}
		if (!list.isEmpty()) {
			log.debug("REQUEST SUMMARY CATALOG: returning {}\n\n", list);
		}
		return list;
	}

	/**
	 * Private method used to create a RequestSummaryData object, from a
	 * RequestSummaryDataTO. If a chunk cannot be created, an error messagge gets
	 * logged and an attempt is made to signal in the DB that the request is
	 * malformed.
	 */
	private RequestSummaryData makeOne(RequestSummaryDataTO to)
		throws IllegalArgumentException {

		TRequestType auxrtype = RequestTypeConverter.getInstance().toSTORM(
			to.requestType());
		if (auxrtype == TRequestType.EMPTY) {
			StringBuilder sb = new StringBuilder();
			sb.append("TRequestType could not be created from its String representation ");
			sb.append(to.requestType());
			sb.append("\n");
			log.warn(sb.toString());
			throw new IllegalArgumentException(
				"Invalid TRequestType in the provided RequestSummaryDataTO");
		}
		TRequestToken auxrtoken;
		try {
			auxrtoken = new TRequestToken(to.requestToken(), to.timestamp());
		} catch (InvalidTRequestTokenAttributesException e) {
			log.warn("Unable to create TRequestToken from RequestSummaryDataTO. "
					+ "InvalidTRequestTokenAttributesException: {}", e.getMessage());
			throw new IllegalArgumentException(
				"Unable to create TRequestToken from RequestSummaryDataTO.");
		}
		GridUserInterface auxgu;

		try {
			auxgu = loadVomsGridUser(to.clientDN(), to.vomsAttributes());
		} catch (MalformedGridUserException e) {
			StringBuilder sb = new StringBuilder();
			sb.append("VomsGridUser could not be created from DN String ");
			sb.append(to.clientDN());
			sb.append(" voms attributes String ");
			sb.append(to.vomsAttributes());
			sb.append(" and from request token String ");
			sb.append(to.requestToken());
			log.warn("{}. MalformedGridUserException: {}", sb.toString(), e.getMessage());
			throw new IllegalArgumentException(
				"Unable to load Voms Grid User from RequestSummaryDataTO. "
					+ "MalformedGridUserException: " + e.getMessage());
		}
		RequestSummaryData data = null;
		try {
			data = new RequestSummaryData(auxrtype, auxrtoken, auxgu);
			data.setPrimaryKey(to.primaryKey());
		} catch (InvalidRequestSummaryDataAttributesException e) {
			dao.failRequest(to.primaryKey(), "The request data is malformed!");
			log.warn("REQUEST SUMMARY CATALOG! Unable to create RequestSummaryData. "
					+ "InvalidRequestSummaryDataAttributesException: {}", e.getMessage(), e);
			throw new IllegalArgumentException("Unable to reate RequestSummaryData");
		}
		TReturnStatus status = null;
		if (to.getStatus() != null) {
			TStatusCode code = StatusCodeConverter.getInstance().toSTORM(to.getStatus());
			if (code == TStatusCode.EMPTY) {
				log.warn("RequestSummaryDataTO retrieved StatusCode was not "
					+ "recognised: {}", to.getStatus());
			} else {
				status = new TReturnStatus(code, to.getErrstring());
			}
		}
		data.setUserToken(to.getUserToken());
		data.setRetrytime(to.getRetrytime());
		if (to.getPinLifetime() != null) {
			data.setPinLifetime(TLifeTimeInSeconds.make(PinLifetimeConverter
				.getInstance().toStoRM(to.getPinLifetime()), TimeUnit.SECONDS));
		}
		data.setSpaceToken(to.getSpaceToken());
		data.setStatus(status);
		data.setErrstring(to.getErrstring());
		data.setRemainingTotalTime(to.getRemainingTotalTime());
		data.setNbreqfiles(to.getNbreqfiles());
		data.setNumOfCompleted(to.getNumOfCompleted());
		if (to.getFileLifetime() != null) {
			data.setFileLifetime(TLifeTimeInSeconds.make(to.getFileLifetime(),
				TimeUnit.SECONDS));
		}

		data.setDeferredStartTime(to.getDeferredStartTime());
		data.setNumOfWaiting(to.getNumOfWaiting());
		data.setNumOfFailed(to.getNumOfFailed());
		data.setRemainingDeferredStartTime(to.getRemainingDeferredStartTime());
		return data;
	}

	/**
	 * Private method that holds the logic for creating a VomsGridUser from
	 * persistence and to load any available Proxy. For the moment the VOMS
	 * attributes present in persistence are NOT loaded!
	 */
	private GridUserInterface loadVomsGridUser(String dn, String fqansString) throws MalformedGridUserException {

		log.debug("load VomsGridUser for dn='{}' and fqansString='{}'", dn, fqansString);

		if (dn == null) {
		    throw new MalformedGridUserException("Invalid null DN");
		}
		if (fqansString == null || fqansString.isEmpty()) {
		  return GridUserManager.makeGridUser(dn);
		}

		FQAN[] fqans = new FQAN[fqansString.split("#").length];
		int i = 0;
		for (String fqan: fqansString.split("#")) {
		  fqans[i++] = new FQAN(fqan);
		}
		try {
			return GridUserManager.makeVOMSGridUser(dn, fqans);
		} catch (IllegalArgumentException e) {
			log.error("Unexpected error on voms grid user creation. "
				+ "IllegalArgumentException: {}", e.getMessage(), e);
			throw new MalformedGridUserException(e.getMessage());
		}
	}

	/**
	 * Method used to update the global status of a request identified by
	 * TRequestToken, to the supplied TReturnStatus. In case of any exception
	 * nothing happens.
	 */
	synchronized public void updateGlobalStatus(TRequestToken rt,
		TReturnStatus status) {

		dao.updateGlobalStatus(rt.toString(), StatusCodeConverter.getInstance()
			.toDB(status.getStatusCode()), status.getExplanation());
	}

	public void updateFromPreviousGlobalStatus(TRequestToken requestToken,
		TStatusCode expectedStatusCode, TStatusCode newStatusCode,
		String explanation) {

		dao.updateGlobalStatusOnMatchingGlobalStatus(requestToken,
			expectedStatusCode, newStatusCode, explanation);
	}

	/**
	 * Method used to update the global status of a request identified by
	 * TRequestToken, to the supplied TReturnStatus. The pin lifetime and the file
	 * lifetime are updated in order to start the countdown from the moment the
	 * status is updated. In case of any exception nothing happens.
	 */
	synchronized public void updateGlobalStatusPinFileLifetime(TRequestToken rt,
		TReturnStatus status) {

		dao.updateGlobalStatusPinFileLifetime(rt.toString(), StatusCodeConverter
			.getInstance().toDB(status.getStatusCode()), status.getExplanation());
	}

	/**
	 * Method used to change the global status of the supplied request to
	 * SRM_FAILURE, as well as that of each single chunk in the request. If the
	 * request type is not supported by the logic, only the global status is
	 * updated and an error log gets written warning of the unsupported business
	 * logic.
	 * 
	 * If the supplied RequestSummaryData is null, nothing gets done; if any DB
	 * error occurs, no exception gets thrown but proper messagges get logged.
	 */
	synchronized public void failRequest(RequestSummaryData rsd,
		String explanation) {

		if (rsd != null) {
			TRequestType rtype = rsd.requestType();
			if (rtype == TRequestType.PREPARE_TO_GET) {
				dao.failPtGRequest(rsd.primaryKey(), explanation);
			} else if (rtype == TRequestType.PREPARE_TO_PUT) {
				dao.failPtPRequest(rsd.primaryKey(), explanation);
			} else if (rtype == TRequestType.COPY) {
				dao.failCopyRequest(rsd.primaryKey(), explanation);
			} else {
				dao.failRequest(rsd.primaryKey(), explanation);
			}
		}
	}

	/**
	 * Method used to abort a request that has not yet been fetched for
	 * processing; if the status of the request associated to the supplied request
	 * token tok is different from SRM_REQUEST_QUEUED, then nothing takes place;
	 * likewise if the supplied token does not correspond to any request, or if it
	 * is null.
	 */
	synchronized public void abortRequest(TRequestToken rt) {

		if (rt != null) {
			dao.abortRequest(rt.toString());
		}
	}

	/**
	 * Method used to abort a request that has not yet been fetched for
	 * processing; abort is only applied to those SURLs of the request specified
	 * in the Collection; if the status of the request associated to the supplied
	 * request token is different from SRM_REQUEST_QUEUED, then nothing takes
	 * place; likewise if the supplied token does not correspond to any request,
	 * if it is null, if the Collection is null, or the Collection does not
	 * contain TSURLs.
	 */
	synchronized public void abortChunksOfRequest(TRequestToken rt,
		Collection<TSURL> c) {

		if ((rt != null) && (c != null) && (!c.isEmpty())) {
			try {
				ArrayList<String> aux = new ArrayList<String>();
				for (TSURL tsurl : c) {
					aux.add(tsurl.toString());
				}
				dao.abortChunksOfRequest(rt.toString(), aux);
			} catch (ClassCastException e) {
				log.error("REQUEST SUMMARY CATALOG! Unexpected error in "
					+ "abortChunksOfRequest: the supplied Collection did not contain "
					+ "TSURLs! Error: {}", e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to abort a request that HAS been fetched for processing; abort
	 * is only applied to those SURLs of the request specified in the Collection;
	 * if the status of the request associated to the supplied request token is
	 * different from SRM_REQUEST_INPROGRESS, then nothing takes place; likewise
	 * if the supplied token does not correspond to any request, if it is null, if
	 * the Collection is null, or the Collection does not contain TSURLs.
	 */
	synchronized public void abortChunksOfInProgressRequest(TRequestToken rt,
		Collection<TSURL> tsurls) {

		if ((rt != null) && (tsurls != null) && (!tsurls.isEmpty())) {
			try {
				List<String> aux = new ArrayList<String>();
				for (TSURL tsurl : tsurls) {
					aux.add(tsurl.toString());
				}
				dao.abortChunksOfInProgressRequest(rt.toString(), aux);
			} catch (ClassCastException e) {
				log.error("REQUEST SUMMARY CATALOG! Unexpected error in "
					+ "abortChunksOfInProgressRequest: the supplied Collection did not "
					+ "contain TSURLs! Error: {}", e.getMessage());
			}
		}
	}

	synchronized public RequestSummaryData find(TRequestToken requestToken)
		throws IllegalArgumentException {

		if (requestToken == null || requestToken.toString().trim().isEmpty()) {
			throw new IllegalArgumentException(
				"Unable to perform find, illegal arguments: requestToken="
					+ requestToken);
		}
		RequestSummaryDataTO to = dao.find(requestToken.toString());
		if (to != null) {
			try {
				RequestSummaryData data = makeOne(to);
				if (data != null) {
					log.debug("REQUEST SUMMARY CATALOG: {} associated to {} retrieved", 
						data.requestToken(), data.gridUser().getDn());
					return data;
				}
			} catch (IllegalArgumentException e) {
				log.error("REQUEST SUMMARY CATALOG; Failure performing makeOne operation. "
					+ "IllegalArgumentException: {}", e.getMessage(), e);
			}
		} else {
			log.debug("REQUEST SUMMARY CATALOG: {} token not found", requestToken);
		}
		return null;
	}

	/**
	 * Method that returns the TRequestType associated to the request with the
	 * supplied TRequestToken. If no request exists with that token, or the type
	 * cannot be established from the DB, or the supplied token is null, then an
	 * EMPTY TRequestType is returned.
	 */
	synchronized public TRequestType typeOf(TRequestToken rt) {

		TRequestType result = TRequestType.EMPTY;
		String type = null;
		if (rt != null) {
			type = dao.typeOf(rt.toString());
			if (type != null && !type.isEmpty())
				result = RequestTypeConverter.getInstance().toSTORM(type);
		}
		return result;
	}

	/**
	 * Method used to abort a request that HAS been fetched for processing; if the
	 * status of the request associated to the supplied request token tok is
	 * different from SRM_REQUEST_INPROGRESS, then nothing takes place; likewise
	 * if the supplied token does not correspond to any request, or if it is null.
	 */
	synchronized public void abortInProgressRequest(TRequestToken rt) {

		if (rt != null) {
			dao.abortInProgressRequest(rt.toString());
		}
	}

}
