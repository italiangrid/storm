/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents StoRMs PtGChunkCatalog: it collects PtGChunkData and
 * provides methods for looking up a PtGChunkData based on TRequestToken, as
 * well as for adding a new entry and removing an existing one.
 * 
 * @author EGRID - ICTP Trieste
 * @date April 26th, 2005
 * @version 4.0
 */
@SuppressWarnings("unused")
public class PtGChunkCatalog {

	private static final Logger log = LoggerFactory
		.getLogger(PtGChunkCatalog.class);

	/* Only instance of PtGChunkCatalog present in StoRM! */
	private static final PtGChunkCatalog cat = new PtGChunkCatalog();
	private final PtGChunkDAO dao = PtGChunkDAO.getInstance();

	/*
	 * Timer object in charge of transiting expired requests from SRM_FILE_PINNED
	 * to SRM_RELEASED!
	 */
	private final Timer transiter = new Timer();
	/* Delay time before starting cleaning thread! */
	private final long delay = Configuration.getInstance()
		.getTransitInitialDelay() * 1000;
	/* Period of execution of cleaning! */
	private final long period = Configuration.getInstance()
		.getTransitTimeInterval() * 1000;

	/**
	 * Private constructor that starts the internal timer needed to periodically
	 * check and transit requests whose pinLifetime has expired and are in
	 * SRM_FILE_PINNED, to SRM_RELEASED.
	 */
	private PtGChunkCatalog() {

		TimerTask transitTask = new TimerTask() {

			@Override
			public void run() {

				transitExpiredSRM_FILE_PINNED();
			}
		};
		transiter.scheduleAtFixedRate(transitTask, delay, period);
	}

	/**
	 * Method that returns the only instance of PtGChunkCatalog available.
	 */
	public static PtGChunkCatalog getInstance() {

		return cat;
	}

	/**
	 * Method used to update into Persistence a retrieved PtGChunkData. In case
	 * any error occurs, the operation does not proceed but no Exception is
	 * thrown. Error messages get logged.
	 * 
	 * Only fileSize, StatusCode, errString and transferURL are updated. Likewise
	 * for the request pinLifetime.
	 */
	synchronized public void update(PtGPersistentChunkData chunkData) {

		PtGChunkDataTO to = new PtGChunkDataTO();
		/* Primary key needed by DAO Object */
		to.setPrimaryKey(chunkData.getPrimaryKey());
		to.setFileSize(chunkData.getFileSize().value());
		to.setStatus(StatusCodeConverter.getInstance().toDB(
			chunkData.getStatus().getStatusCode()));
		to.setErrString(chunkData.getStatus().getExplanation());
		to.setTurl(TURLConverter.getInstance().toDB(
			chunkData.getTransferURL().toString()));
		to.setLifeTime(PinLifetimeConverter.getInstance().toDB(
			chunkData.getPinLifeTime().value()));
		to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
		to.setSurlUniqueID(new Integer(chunkData.getSURL().uniqueId()));
		to.setClientDN(chunkData.getUser().getDn());
		if (chunkData.getUser() instanceof AbstractGridUser) {
			if (((AbstractGridUser) chunkData.getUser()).hasVoms()) {
				to.setVomsAttributes(((AbstractGridUser) chunkData.getUser())
					.getFQANsAsString());
			}

		}
		dao.update(to);
	}

	/**
	 * Refresh method. THIS IS A WORK IN PROGRESS!!!! This method have to synch
	 * the ChunkData information with the database status intended as the status
	 * code and the TURL
	 * 
	 * @param auxTO
	 * @param PtGChunkData
	 *          inputChunk
	 * @return PtGChunkData outputChunk
	 */
	synchronized public PtGPersistentChunkData refreshStatus(
		PtGPersistentChunkData inputChunk) {

		PtGChunkDataTO chunkDataTO = dao.refresh(inputChunk.getPrimaryKey());

		log.debug("PtG CHUNK CATALOG: retrieved data " + chunkDataTO);
		if (chunkDataTO == null) {
			log.warn("PtG CHUNK CATALOG! Empty TO found in persistence for specified "
				+ "request: {}", inputChunk.getPrimaryKey());
			return inputChunk;
		}
		
		/*
		 * In this first version the only field updated is the Status. Once
		 * updated, the new status is rewritten into the input ChunkData
		 */

		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(chunkDataTO.status());
		if (code != TStatusCode.EMPTY) {
			status = new TReturnStatus(code, chunkDataTO.errString());
		}
		inputChunk.setStatus(status);
		TTURL turl = null;
		try {
			turl = TTURL.makeFromString(chunkDataTO.turl());
		} catch (InvalidTTURLAttributesException e) {
			log.info("PtGChunkCatalog (FALSE-ERROR-in-abort-refresh-status?):"
				+ " built a TURL with protocol NULL (retrieved from the DB..)");
		}
		inputChunk.setTransferURL(turl);
		return inputChunk;
	}

	/**
	 * Method that returns a Collection of PtGChunkData Objects matching the
	 * supplied TRequestToken.
	 * 
	 * If any of the data associated to the TRequestToken is not well formed and
	 * so does not allow a PtGChunkData Object to be created, then that part of
	 * the request is dropped and gets logged, and the processing continues with
	 * the next part. All valid chunks get returned: the others get dropped.
	 * 
	 * If there are no chunks to process then an empty Collection is returned, and
	 * a messagge gets logged.
	 */
	synchronized public Collection<PtGPersistentChunkData> lookup(TRequestToken rt) {

		Collection<PtGChunkDataTO> chunkTOs = dao.find(rt);
		log.debug("PtG CHUNK CATALOG: retrieved data " + chunkTOs);
		ArrayList<PtGPersistentChunkData> list = new ArrayList<PtGPersistentChunkData>();
		if (chunkTOs.isEmpty()) {
			log.warn("PtG CHUNK CATALOG! No chunks found in persistence for "
				+ "specified request: {}", rt);
			return list;
		}
		PtGPersistentChunkData chunk;
		for (PtGChunkDataTO chunkTO : chunkTOs) {
			chunk = makeOne(chunkTO, rt);
			if (chunk == null) {
				continue;
			}
			list.add(chunk);
			if (isComplete(chunkTO)) {
				continue;
			}
			try {
				dao.updateIncomplete(this.completeTO(chunkTO, chunk));
			} catch (InvalidReducedPtGChunkDataAttributesException e) {
				log.warn("PtG CHUNK CATALOG! unable to add missing informations on DB "
					+ "to the request: {}", e.getMessage());
			}
		}
		log.debug("PtG CHUNK CATALOG: returning " + list);
		return list;
	}

	/**
	 * Generates a PtGChunkData from the received PtGChunkDataTO
	 * 
	 * @param chunkDataTO
	 * @param rt
	 * @return
	 */
	private PtGPersistentChunkData makeOne(PtGChunkDataTO chunkDataTO,
		TRequestToken rt) {

		StringBuffer errorSb = new StringBuffer();
		TSURL fromSURL = null;
		try {
			fromSURL = TSURL.makeFromStringValidate(chunkDataTO.fromSURL());
		} catch (InvalidTSURLAttributesException e) {
			errorSb.append(e);
		}
		if (chunkDataTO.normalizedStFN() != null) {
			fromSURL.setNormalizedStFN(chunkDataTO.normalizedStFN());
		}
		if (chunkDataTO.surlUniqueID() != null) {
			fromSURL.setUniqueID(chunkDataTO.surlUniqueID().intValue());
		}
		// lifeTime
		TLifeTimeInSeconds lifeTime = null;
		try {
			long pinLifeTime = PinLifetimeConverter.getInstance().toStoRM(
				chunkDataTO.lifeTime());
			// Check for max value allowed
			long max = Configuration.getInstance().getPinLifetimeMaximum();
			if (pinLifeTime > max) {
				log.warn("PinLifeTime is greater than the max value allowed."
					+ " Drop the value to the max = {} seconds", max);
				pinLifeTime = max;
			}
			lifeTime = TLifeTimeInSeconds.make((pinLifeTime), TimeUnit.SECONDS);
		} catch (IllegalArgumentException e) {
			errorSb.append("\n");
			errorSb.append(e);
		}
		// dirOption
		TDirOption dirOption = null;
		try {
			dirOption = new TDirOption(chunkDataTO.dirOption(),
				chunkDataTO.allLevelRecursive(), chunkDataTO.numLevel());
		} catch (InvalidTDirOptionAttributesException e) {
			errorSb.append("\n");
			errorSb.append(e);
		}
		// transferProtocols
		TURLPrefix transferProtocols = TransferProtocolListConverter
			.toSTORM(chunkDataTO.protocolList());
		if (transferProtocols.size() == 0) {
			errorSb.append("\nEmpty list of TransferProtocols or could "
				+ "not translate TransferProtocols!");
			/* fail construction of PtGChunkData! */
			transferProtocols = null;
		}
		// fileSize
		TSizeInBytes fileSize = null;
		try {
			fileSize = TSizeInBytes.make(chunkDataTO.fileSize(), SizeUnit.BYTES);
		} catch (InvalidTSizeAttributesException e) {
			errorSb.append("\n");
			errorSb.append(e);
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(
			chunkDataTO.status());
		if (code == TStatusCode.EMPTY) {
			errorSb.append("\nRetrieved StatusCode was not recognised: "
				+ chunkDataTO.status());
		} else {
			status = new TReturnStatus(code, chunkDataTO.errString());
		}
		GridUserInterface gridUser = null;
		try {
			if (chunkDataTO.vomsAttributes() != null
				&& !chunkDataTO.vomsAttributes().trim().equals("")) {
				gridUser = GridUserManager.makeVOMSGridUser(chunkDataTO.clientDN(),
					chunkDataTO.vomsAttributesArray());
			} else {
				gridUser = GridUserManager.makeGridUser(chunkDataTO.clientDN());
			}

		} catch (IllegalArgumentException e) {
			log.error("Unexpected error on voms grid user creation."
				+ " IllegalArgumentException: {}", e.getMessage(), e);
		}
		// transferURL
		/*
		 * whatever is read is just meaningless because PtG will fill it in!!! So
		 * create an Empty TTURL by default! Vital to avoid problems with unknown
		 * DPM NULL/EMPTY logic policy!
		 */
		TTURL transferURL = TTURL.makeEmpty();
		// make PtGChunkData
		PtGPersistentChunkData aux = null;
		try {
			aux = new PtGPersistentChunkData(gridUser, rt, fromSURL, lifeTime,
				dirOption, transferProtocols, fileSize, status, transferURL);
			aux.setPrimaryKey(chunkDataTO.primaryKey());
		} catch (InvalidSurlRequestDataAttributesException e) {
			dao.signalMalformedPtGChunk(chunkDataTO);
			log.warn("PtG CHUNK CATALOG! Retrieved malformed PtG chunk data from "
				+ "persistence. Dropping chunk from request {}", rt);
			log.warn(e.getMessage(), e);
			log.warn(errorSb.toString());
		}
		// end...
		return aux;
	}

	/**
	 * 
	 * Adds to the received PtGChunkDataTO the normalized StFN and the SURL unique
	 * ID taken from the PtGChunkData
	 * 
	 * @param chunkTO
	 * @param chunk
	 */
	private void completeTO(ReducedPtGChunkDataTO chunkTO,
		final ReducedPtGChunkData chunk) {

		chunkTO.setNormalizedStFN(chunk.fromSURL().normalizedStFN());
		chunkTO.setSurlUniqueID(new Integer(chunk.fromSURL().uniqueId()));
	}

	/**
	 * 
	 * Creates a ReducedPtGChunkDataTO from the received PtGChunkDataTO and
	 * completes it with the normalized StFN and the SURL unique ID taken from the
	 * PtGChunkData
	 * 
	 * @param chunkTO
	 * @param chunk
	 * @return
	 * @throws InvalidReducedPtGChunkDataAttributesException
	 */
	private ReducedPtGChunkDataTO completeTO(PtGChunkDataTO chunkTO,
		final PtGPersistentChunkData chunk)
		throws InvalidReducedPtGChunkDataAttributesException {

		ReducedPtGChunkDataTO reducedChunkTO = this.reduce(chunkTO);
		this.completeTO(reducedChunkTO, this.reduce(chunk));
		return reducedChunkTO;
	}

	/**
	 * Creates a ReducedPtGChunkData from the data contained in the received
	 * PtGChunkData
	 * 
	 * @param chunk
	 * @return
	 * @throws InvalidReducedPtGChunkDataAttributesException
	 */
	private ReducedPtGChunkData reduce(PtGPersistentChunkData chunk)
		throws InvalidReducedPtGChunkDataAttributesException {

		ReducedPtGChunkData reducedChunk = new ReducedPtGChunkData(chunk.getSURL(),
			chunk.getStatus());
		reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
		return reducedChunk;
	}

	/**
	 * Creates a ReducedPtGChunkDataTO from the data contained in the received
	 * PtGChunkDataTO
	 * 
	 * @param chunkTO
	 * @return
	 */
	private ReducedPtGChunkDataTO reduce(PtGChunkDataTO chunkTO) {

		ReducedPtGChunkDataTO reducedChunkTO = new ReducedPtGChunkDataTO();
		reducedChunkTO.setPrimaryKey(chunkTO.primaryKey());
		reducedChunkTO.setFromSURL(chunkTO.fromSURL());
		reducedChunkTO.setNormalizedStFN(chunkTO.normalizedStFN());
		reducedChunkTO.setSurlUniqueID(chunkTO.surlUniqueID());
		reducedChunkTO.setStatus(chunkTO.status());
		reducedChunkTO.setErrString(chunkTO.errString());
		return reducedChunkTO;
	}

	/**
	 * Checks if the received PtGChunkDataTO contains the fields not set by the
	 * front end but required
	 * 
	 * @param chunkTO
	 * @return
	 */
	private boolean isComplete(PtGChunkDataTO chunkTO) {

		return (chunkTO.normalizedStFN() != null)
			&& (chunkTO.surlUniqueID() != null);
	}

	/**
	 * Checks if the received ReducedPtGChunkDataTO contains the fields not set by
	 * the front end but required
	 * 
	 * @param reducedChunkTO
	 * @return
	 */
	private boolean isComplete(ReducedPtGChunkDataTO reducedChunkTO) {

		return (reducedChunkTO.normalizedStFN() != null)
			&& (reducedChunkTO.surlUniqueID() != null);
	}

	/**
	 * Method that returns a Collection of ReducedPtGChunkData Objects associated
	 * to the supplied TRequestToken.
	 * 
	 * If any of the data retrieved for a given chunk is not well formed and so
	 * does not allow a ReducedPtGChunkData Object to be created, then that chunk
	 * is dropped and gets logged, while processing continues with the next one.
	 * All valid chunks get returned: the others get dropped.
	 * 
	 * If there are no chunks associated to the given TRequestToken, then an empty
	 * Collection is returned and a message gets logged.
	 */
	synchronized public Collection<ReducedChunkData> lookupReducedPtGChunkData(
		TRequestToken rt) {

		Collection<ReducedPtGChunkDataTO> reducedChunkDataTOs = dao.findReduced(rt
			.getValue());
		log.debug("PtG CHUNK CATALOG: retrieved data {}", reducedChunkDataTOs);
		ArrayList<ReducedChunkData> list = new ArrayList<ReducedChunkData>();
		if (reducedChunkDataTOs.isEmpty()) {
			log.debug("PtG CHUNK CATALOG! No chunks found in persistence for {}", rt);
		} else {
			ReducedPtGChunkData reducedChunkData = null;
			for (ReducedPtGChunkDataTO reducedChunkDataTO : reducedChunkDataTOs) {
				reducedChunkData = makeOneReduced(reducedChunkDataTO);
				if (reducedChunkData != null) {
					list.add(reducedChunkData);
					if (!this.isComplete(reducedChunkDataTO)) {
						this.completeTO(reducedChunkDataTO, reducedChunkData);
						dao.updateIncomplete(reducedChunkDataTO);
					}
				}
			}
			log.debug("PtG CHUNK CATALOG: returning {}", list);
		}
		return list;
	}

	public Collection<ReducedChunkData> lookupReducedPtGChunkData(
		TRequestToken requestToken, Collection<TSURL> surls) {

		int[] surlsUniqueIDs = new int[surls.size()];
		String[] surlsArray = new String[surls.size()];
		int index = 0;
		for (TSURL tsurl : surls) {
			surlsUniqueIDs[index] = tsurl.uniqueId();
			surlsArray[index] = tsurl.rawSurl();
			index++;
		}
		Collection<ReducedPtGChunkDataTO> chunkDataTOCollection = dao.findReduced(
			requestToken, surlsUniqueIDs, surlsArray);
		log.debug("PtG CHUNK CATALOG: retrieved data {}", chunkDataTOCollection);
		return buildReducedChunkDataList(chunkDataTOCollection);
	}

	public Collection<PtGPersistentChunkData> lookupPtGChunkData(TSURL surl,
		GridUserInterface user) {

		return lookupPtGChunkData(Arrays.asList(new TSURL[] { surl }), user);
	}

	public Collection<PtGPersistentChunkData> lookupPtGChunkData(TSURL surl) {

		return lookupPtGChunkData(Arrays.asList(new TSURL[] { surl }));
	}

	public Collection<PtGPersistentChunkData> lookupPtGChunkData(
		List<TSURL> surls, GridUserInterface user) {

		int[] surlsUniqueIDs = new int[surls.size()];
		String[] surlsArray = new String[surls.size()];
		int index = 0;
		for (TSURL tsurl : surls) {
			surlsUniqueIDs[index] = tsurl.uniqueId();
			surlsArray[index] = tsurl.rawSurl();
			index++;
		}
		Collection<PtGChunkDataTO> chunkDataTOCollection = dao.find(surlsUniqueIDs,
			surlsArray, user.getDn());
		log.debug("PtG CHUNK CATALOG: retrieved data {}", chunkDataTOCollection);
		return buildChunkDataList(chunkDataTOCollection);
	}

	public Collection<PtGPersistentChunkData> lookupPtGChunkData(List<TSURL> surls) {

		int[] surlsUniqueIDs = new int[surls.size()];
		String[] surlsArray = new String[surls.size()];
		int index = 0;
		for (TSURL tsurl : surls) {
			surlsUniqueIDs[index] = tsurl.uniqueId();
			surlsArray[index] = tsurl.rawSurl();
			index++;
		}
		Collection<PtGChunkDataTO> chunkDataTOCollection = dao.find(surlsUniqueIDs,
			surlsArray);
		log.debug("PtG CHUNK CATALOG: retrieved data {}", chunkDataTOCollection);
		return buildChunkDataList(chunkDataTOCollection);
	}

	private Collection<PtGPersistentChunkData> buildChunkDataList(
		Collection<PtGChunkDataTO> chunkDataTOCollection) {

		ArrayList<PtGPersistentChunkData> list = new ArrayList<PtGPersistentChunkData>();
		PtGPersistentChunkData chunk;
		for (PtGChunkDataTO chunkTO : chunkDataTOCollection) {
			chunk = makeOne(chunkTO);
			if (chunk == null) {
				continue;
			}
			list.add(chunk);
			if (isComplete(chunkTO)) {
				continue;
			}
			try {
				dao.updateIncomplete(this.completeTO(chunkTO, chunk));
			} catch (InvalidReducedPtGChunkDataAttributesException e) {
				log.warn("PtG CHUNK CATALOG! unable to add missing informations on "
					+ "DB to the request: ", e.getMessage());
			}
		}
		return list;
	}

	private PtGPersistentChunkData makeOne(PtGChunkDataTO chunkTO) {

		try {
			return makeOne(chunkTO,
				new TRequestToken(chunkTO.requestToken(), chunkTO.timeStamp()));
		} catch (InvalidTRequestTokenAttributesException e) {
			throw new IllegalStateException(
				"Unexpected InvalidTRequestTokenAttributesException in TRequestToken: "
					+ e);
		}
	}

	/**
	 * Method that returns a Collection of ReducedPtGChunkData Objects matching
	 * the supplied GridUser and Collection of TSURLs. If any of the data
	 * retrieved for a given chunk is not well formed and so does not allow a
	 * ReducedPtGChunkData Object to be created, then that chunk is dropped and
	 * gets logged, while processing continues with the next one. All valid chunks
	 * get returned: the others get dropped. If there are no chunks associated to
	 * the given GridUser and Collection of TSURLs, then an empty Collection is
	 * returned and a message gets logged.
	 */
	synchronized public Collection<ReducedChunkData> lookupReducedPtGChunkData(
		GridUserInterface gu, Collection<TSURL> tsurlCollection) {

		int[] surlsUniqueIDs = new int[tsurlCollection.size()];
		String[] surls = new String[tsurlCollection.size()];
		int index = 0;
		for (TSURL tsurl : tsurlCollection) {
			surlsUniqueIDs[index] = tsurl.uniqueId();
			surls[index] = tsurl.rawSurl();
			index++;
		}
		Collection<ReducedPtGChunkDataTO> chunkDataTOCollection = dao.findReduced(
			gu.getDn(), surlsUniqueIDs, surls);
		log.debug("PtG CHUNK CATALOG: retrieved data {}", chunkDataTOCollection);
		return buildReducedChunkDataList(chunkDataTOCollection);
	}

	private Collection<ReducedChunkData> buildReducedChunkDataList(
		Collection<ReducedPtGChunkDataTO> chunkDataTOCollection) {

		ArrayList<ReducedChunkData> list = new ArrayList<ReducedChunkData>();
		ReducedPtGChunkData reducedChunkData;
		for (ReducedPtGChunkDataTO reducedChunkDataTO : chunkDataTOCollection) {
			reducedChunkData = makeOneReduced(reducedChunkDataTO);
			if (reducedChunkData != null) {
				list.add(reducedChunkData);
				if (!isComplete(reducedChunkDataTO)) {
					completeTO(reducedChunkDataTO, reducedChunkData);
					dao.updateIncomplete(reducedChunkDataTO);
				}
			}
		}
		log.debug("PtG CHUNK CATALOG: returning {}",list);
		return list;
	}

	/**
	 * 
	 * 
	 * @param reducedChunkDataTO
	 * @return
	 */
	private ReducedPtGChunkData makeOneReduced(
		ReducedPtGChunkDataTO reducedChunkDataTO) {

		StringBuffer errorSb = new StringBuffer();
		// fromSURL
		TSURL fromSURL = null;
		try {
			fromSURL = TSURL.makeFromStringValidate(reducedChunkDataTO.fromSURL());
		} catch (InvalidTSURLAttributesException e) {
			errorSb.append(e);
		}
		if (reducedChunkDataTO.normalizedStFN() != null) {
			fromSURL.setNormalizedStFN(reducedChunkDataTO.normalizedStFN());
		}
		if (reducedChunkDataTO.surlUniqueID() != null) {
			fromSURL.setUniqueID(reducedChunkDataTO.surlUniqueID().intValue());
		}
		// status
		TReturnStatus status = null;
		TStatusCode code = StatusCodeConverter.getInstance().toSTORM(
			reducedChunkDataTO.status());
		if (code == TStatusCode.EMPTY) {
			errorSb.append("\nRetrieved StatusCode was not recognised: "
				+ reducedChunkDataTO.status());
		} else {
			status = new TReturnStatus(code, reducedChunkDataTO.errString());
		}
		// make ReducedPtGChunkData
		ReducedPtGChunkData aux = null;
		try {
			aux = new ReducedPtGChunkData(fromSURL, status);
			aux.setPrimaryKey(reducedChunkDataTO.primaryKey());
		} catch (InvalidReducedPtGChunkDataAttributesException e) {
			log.warn("PtG CHUNK CATALOG! Retrieved malformed Reduced PtG chunk "
				+ "data from persistence: dropping reduced chunk...");
			log.warn(e.getMessage(), e);
			log.warn(errorSb.toString());
		}
		// end...
		return aux;
	}

	/**
	 * Method used to add into Persistence a new entry. The supplied PtGChunkData
	 * gets the primary key changed to the value assigned in Persistence.
	 * 
	 * This method is intended to be used by a recursive PtG request: the parent
	 * request supplies a directory which must be expanded, so all new children
	 * requests resulting from the files in the directory are added into
	 * persistence.
	 * 
	 * So this method does _not_ add a new SRM prepare_to_get request into the DB!
	 * 
	 * The only children data written into the DB are: sourceSURL, TDirOption,
	 * statusCode and explanation.
	 * 
	 * In case of any error the operation does not proceed, but no Exception is
	 * thrown! Proper messages get logged by underlaying DAO.
	 */
	synchronized public void addChild(PtGPersistentChunkData chunkData) {

		PtGChunkDataTO to = new PtGChunkDataTO();
		/* needed for now to find ID of request! Must be changed soon! */
		to.setRequestToken(chunkData.getRequestToken().toString());
		to.setFromSURL(chunkData.getSURL().toString());
		to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
		to.setSurlUniqueID(new Integer(chunkData.getSURL().uniqueId()));

		to.setAllLevelRecursive(chunkData.getDirOption().isAllLevelRecursive());
		to.setDirOption(chunkData.getDirOption().isDirectory());
		to.setNumLevel(chunkData.getDirOption().getNumLevel());
		to.setStatus(StatusCodeConverter.getInstance().toDB(
			chunkData.getStatus().getStatusCode()));
		to.setErrString(chunkData.getStatus().getExplanation());
		to.setClientDN(chunkData.getUser().getDn());
		if (chunkData.getUser() instanceof AbstractGridUser) {
			if (((AbstractGridUser) chunkData.getUser()).hasVoms()) {
				to.setVomsAttributes(((AbstractGridUser) chunkData.getUser())
					.getFQANsAsString());
			}

		}
		/* add the entry and update the Primary Key field! */
		dao.addChild(to);
		/* set the assigned PrimaryKey! */
		chunkData.setPrimaryKey(to.primaryKey());
	}

	/**
	 * Method used to add into Persistence a new entry. The supplied PtGChunkData
	 * gets the primary key changed to the value assigned in the Persistence. The
	 * method requires the GridUser to whom associate the added request.
	 * 
	 * This method is intended to be used by an srmCopy request in push mode which
	 * implies a local srmPtG. The only fields from PtGChunkData that are
	 * considered are: the requestToken, the sourceSURL, the pinLifetime, the
	 * dirOption, the protocolList, the status and error string.
	 * 
	 * So this method _adds_ a new SRM prepare_to_get request into the DB!
	 * 
	 * In case of any error the operation does not proceed, but no Exception is
	 * thrown! The underlaying DAO logs proper error messagges.
	 */
	synchronized public void add(PtGPersistentChunkData chunkData,
		GridUserInterface gu) {

		PtGChunkDataTO to = new PtGChunkDataTO();
		to.setRequestToken(chunkData.getRequestToken().toString());
		to.setFromSURL(chunkData.getSURL().toString());
		to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
		to.setSurlUniqueID(new Integer(chunkData.getSURL().uniqueId()));

		to.setLifeTime(new Long(chunkData.getPinLifeTime().value()).intValue());
		to.setAllLevelRecursive(chunkData.getDirOption().isAllLevelRecursive());
		to.setDirOption(chunkData.getDirOption().isDirectory());
		to.setNumLevel(chunkData.getDirOption().getNumLevel());
		to.setProtocolList(TransferProtocolListConverter.toDB(chunkData
			.getTransferProtocols()));
		to.setStatus(StatusCodeConverter.getInstance().toDB(
			chunkData.getStatus().getStatusCode()));
		to.setErrString(chunkData.getStatus().getExplanation());

		to.setClientDN(chunkData.getUser().getDn());
		if (chunkData.getUser() instanceof AbstractGridUser) {
			if (((AbstractGridUser) chunkData.getUser()).hasVoms()) {
				to.setVomsAttributes(((AbstractGridUser) chunkData.getUser())
					.getFQANsAsString());
			}

		}

		dao.addNew(to, gu.getDn()); // add the entry and update the Primary Key
																// field!
		chunkData.setPrimaryKey(to.primaryKey()); // set the assigned PrimaryKey!
	}

	/**
	 * Method used to establish if in Persistence there is a PtGChunkData working
	 * on the supplied SURL, and whose state is SRM_FILE_PINNED, in which case
	 * true is returned. In case none are found or there is any problem, false is
	 * returned. This method is intended to be used by srmMv.
	 */
	synchronized public boolean isSRM_FILE_PINNED(TSURL surl) {

		return (dao.numberInSRM_FILE_PINNED(surl.uniqueId()) > 0);

	}

	/**
	 * Method used to transit the specified Collection of ReducedPtGChunkData from
	 * SRM_FILE_PINNED to SRM_RELEASED. Chunks in any other starting state are not
	 * transited. In case of any error nothing is done, but proper error messages
	 * get logged by the underlaying DAO.
	 */
	synchronized public void transitSRM_FILE_PINNEDtoSRM_RELEASED(
		Collection<ReducedPtGChunkData> chunks, TRequestToken token) {

		if (chunks == null || chunks.isEmpty()) {
			return;
		}
		long[] primaryKeys = new long[chunks.size()];
		int index = 0;
		for (ReducedPtGChunkData chunkData : chunks) {
			if (chunkData != null) {
				primaryKeys[index] = chunkData.primaryKey();
				index++;
			}

		}
		dao.transitSRM_FILE_PINNEDtoSRM_RELEASED(primaryKeys, token);
		for (ReducedPtGChunkData chunkData : chunks) {
			if (chunkData != null) {
				primaryKeys[index] = chunkData.primaryKey();
				index++;
			}
		}
	}

	/**
	 * Method used to force transition to SRM_RELEASED from SRM_FILE_PINNED, of
	 * all PtG Requests whose pinLifetime has expired and the state still has not
	 * been changed (a user forgot to run srmReleaseFiles)!
	 */
	synchronized public void transitExpiredSRM_FILE_PINNED() {

		List<TSURL> expiredSurls = dao.transitExpiredSRM_FILE_PINNED();
	}

	public void updateStatus(TRequestToken requestToken, TSURL surl,
		TStatusCode statusCode, String explanation) {

		dao.updateStatus(requestToken, new int[] { surl.uniqueId() },
			new String[] { surl.rawSurl() }, statusCode, explanation);
	}

	public void updateFromPreviousStatus(TSURL surl,
		TStatusCode expectedStatusCode, TStatusCode newStatusCode,
		String explanation) {

		dao.updateStatusOnMatchingStatus(new int[] { surl.uniqueId() },
			new String[] { surl.rawSurl() }, expectedStatusCode, newStatusCode,
			explanation);
		
	}
	
	public void updateFromPreviousStatus(TRequestToken requestToken,
		TStatusCode expectedStatusCode, TStatusCode newStatusCode,
		String explanation) {

		dao.updateStatusOnMatchingStatus(requestToken, expectedStatusCode,
			newStatusCode, explanation);
	}

	public void updateFromPreviousStatus(TRequestToken requestToken,
		List<TSURL> surlList, TStatusCode expectedStatusCode,
		TStatusCode newStatusCode) {

		int[] surlsUniqueIDs = new int[surlList.size()];
		String[] surls = new String[surlList.size()];
		int index = 0;
		for (TSURL tsurl : surlList) {
			surlsUniqueIDs[index] = tsurl.uniqueId();
			surls[index] = tsurl.rawSurl();
			index++;
		}
		dao.updateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls,
			expectedStatusCode, newStatusCode);
	}

}
