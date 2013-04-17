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

package it.grid.storm.asynch;

import it.grid.storm.common.types.EndPoint;
import it.grid.storm.common.types.Port;
import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.InvalidTUserIDAttributeException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import javax.xml.rpc.soap.SOAPFaultException;

import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.SimpleProvider;
import org.apache.axis.transport.http.HTTPSender;
import org.globus.axis.transport.GSIHTTPSender;
import org.globus.axis.transport.GSIHTTPTransport;
import org.globus.axis.util.Util;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.gsi.gssapi.auth.HostAuthorization;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import srmClientStubs.ISRM;

/**
 * Class that represents a first implementation of an SRMClient.
 * 
 * @author EGRID - ICTP Trieste
 * @version 3.0
 * @date October, 2005
 */
public class SRM22Client implements SRMClient {

	private static Logger log = LoggerFactory.getLogger(SRM22Client.class);

	/**
	 * Method used to execute an srmPrepareToPut.
	 * 
	 * It will connect to the webservice derived from the toSURL using the
	 * credentials available in GridUser. It requires the lifetime, the
	 * fileStorageType, the spaceToken, the fileSize, the protocol, a description,
	 * the overwriteOption, the retryTime. The method returns an
	 * SRMPrepareToPutReply containing the result of the operation, and throws an
	 * SRMClient exception in case the operation cannot be completed.
	 * 
	 * The client checks the overall status of the request, and provided it is in
	 * either of SRM_REQUEST_QUEUED, SRM_REQUEST_INPROGESS, SRM_SUCCESS,
	 * SRM_PARTIAL_SUCCESS it will return the appropriate SRMPrepareToPutReply.
	 * Any other request level status results in an SRMClientException.
	 * 
	 * If there are any errors in the communication, or the reply received from
	 * the other web service is incomprehensible or malformed, an
	 * SRMClientException is thrown with appropriate error messagges.
	 */
	public SRMPrepareToPutReply prepareToPut(GridUserInterface gu, TSURL toSURL,
		TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
		TSpaceToken spaceToken, TSizeInBytes filesize, TransferProtocol protocol,
		String description, TOverwriteMode overwriteOption,
		TLifeTimeInSeconds retryTime) throws SRMClientException {

		try {
			srmClientStubs.ISRM _srm = setUpGSI(gu, toSURL);

			// prepare srm request and execute it
			srmClientStubs.SrmPrepareToPutRequest req = new srmClientStubs.SrmPrepareToPutRequest(); // srm
																																																// request
			// set description
			log.debug("NAIVE SRM CLIENT; setting description to: " + description);
			req.setUserRequestDescription(description);
			// set array of transfer protocols
			String[] protocolArray = new String[1];
			protocolArray[0] = protocol.toString();
			log
				.debug("NAIVE SRM CLIENT; setting protocol array to: " + protocolArray);
			srmClientStubs.ArrayOfString protocolArrayString = new srmClientStubs.ArrayOfString();
			protocolArrayString.setStringArray(protocolArray);
			srmClientStubs.TTransferParameters stubtp = new srmClientStubs.TTransferParameters();
			stubtp.setArrayOfTransferProtocols(protocolArrayString);
			req.setTransferParameters(stubtp);
			// set Overwrite Option
			log.debug("NAIVE SRM CLIENT; setting overwrite option to: "
				+ overwriteOption);
			req.setOverwriteOption(new WSOverwriteModeConverter()
				.fromStoRM(overwriteOption));
			// set retry time
			Integer dtrt = new Integer(new Long(retryTime.value()).intValue());
			log
				.debug("NAIVE SRM CLIENT; setting desired total request time: " + dtrt);
			req.setDesiredTotalRequestTime(dtrt);

			// set storage type
			srmClientStubs.TFileStorageType stubfst = new WSFileStorageTypeConverter()
				.fromStoRM(fileStorageType); // ws file storage type!
			log.debug("NAIVE SRM CLIENT; setting file storage type to: "
				+ fileStorageType);
			req.setDesiredFileStorageType(stubfst);
			// set space token
			if (!spaceToken.isEmpty()) {
				String st = spaceToken.toString();
				log.debug("NAIVE SRM CLIENT; setting space token to: " + st);
				req.setTargetSpaceToken(st);
			} else {
				log.debug("NAIVE SRM CLIENT; setting space token to null. ");
			}
			// set lifetime
			Integer dflt = new Integer(new Long(lifetime.value()).intValue());
			log.debug("NAIVE SRM CLIENT; setting desired file life time to: " + dflt);
			req.setDesiredFileLifeTime(dflt);

			// set pinLifetime
			Integer dplt = new Integer(Configuration.getInstance()
				.getSRM22ClientPinLifeTime());
			log.debug("NAIVE SRM CLIENT; setting desired pin life time to: " + dplt);
			req.setDesiredPinLifeTime(dplt);

			// set request specific info
			srmClientStubs.TPutFileRequest stubpfr = new srmClientStubs.TPutFileRequest(); // ws
																																											// put
																																											// file
																																											// request!
			// set file size
			long efs = filesize.value();
			log.debug("NAIVE SRM CLIENT; setting expected file size to: " + efs);
			// FIXME here we can have -1, that means the absence of information about
			// filesize. manage this case considering the following method!
			org.apache.axis.types.UnsignedLong ulFileSize = new org.apache.axis.types.UnsignedLong(
				efs);
			stubpfr.setExpectedFileSize(ulFileSize);
			// set target SURL
			String ts = toSURL.toString();
			log.debug("NAIVE SRM CLIENT; setting target SURL to: " + ts);
			stubpfr.setTargetSURL(new org.apache.axis.types.URI(ts));

			// set array of requests
			srmClientStubs.TPutFileRequest[] stubpfrArray = new srmClientStubs.TPutFileRequest[1]; // ws
																																															// array
																																															// of
																																															// put
																																															// file
																																															// request!
			stubpfrArray[0] = stubpfr;
			srmClientStubs.ArrayOfTPutFileRequest arrayOfPut = new srmClientStubs.ArrayOfTPutFileRequest(
				stubpfrArray);
			req.setArrayOfFileRequests(arrayOfPut);

			// execute request!
			log.debug("NAIVE SRM CLIENT: sending request " + arrayOfPut);
			srmClientStubs.SrmPrepareToPutResponse response = _srm
				.srmPrepareToPut(req);
			log.debug("NAIVE SRM CLIENT: received reply " + response);

			// get overall request status
			if (response == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: got a null response!");
			}
			srmClientStubs.TReturnStatus overallRetStat = response.getReturnStatus();
			if (overallRetStat == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null request level TReturnStatus!");
			}
			// check overall request status
			it.grid.storm.srm.types.TStatusCode overallStat = WSReturnStatusConverter
				.getInstance().fromWS(overallRetStat).getStatusCode();
			if ((overallStat != it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_QUEUED)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_PARTIAL_SUCCESS)) {
				// overall status is some kind of failure!
				throw new SRMClientException(
					"srmPrepareToPut on remote machine failed! Request level return status: "
						+ overallStat + ", explanation: " + overallRetStat.getExplanation());
			}
			// get TRequestToken
			String reqTokenString = response.getRequestToken();
			if (reqTokenString == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null TReqeustToken!");
			}
			it.grid.storm.srm.types.TRequestToken requestToken = new WSRequestTokenConverter()
				.fromWS(reqTokenString);
			// return answer
			return new SRMPrepareToPutReply(requestToken);
		} catch (SRMClientException e) {
			throw e; // re-throw any SRMClientException! Otherwiase the general catch
								// at the bottom captures it!
		} catch (it.grid.storm.srm.types.InvalidTUserIDAttributeException e) {
			// GridUser threw an exception! Cannot get credentials!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot get gredentials from GridUser! "
					+ e);
		} catch (GlobusCredentialException e) {
			// Globus credentials cannot be created!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create GlobusCredentials from GridUser! "
					+ e);
		} catch (GSSException e) {
			// GSS credentials cannot be created!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create GSSCredentials from GlobusCredential! "
					+ e);
		} catch (org.apache.axis.types.URI.MalformedURIException ex) {
			// This is a programming bug and should not occur! The URI is malformed!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create URI! " + ex);
		} catch (javax.xml.rpc.ServiceException ex) {
			// Something went wrong with the web service!
			throw new SRMClientException("Web Service problem! " + ex);
		} catch (java.rmi.RemoteException ex) {
			// something went wrong at low level communication with web service!
			throw new SRMClientException("Web Service Communication problem! " + ex);
		} catch (SOAPFaultException e) {
			// The remote service launched an exception! This is a programmatic
			// exception remotely thrown, _not_ an error!
			throw new SRMClientException(
				"The remote web service launched an exception! " + e);
		} catch (WSConversionException e) {
			// The data received from the web service could not be translated into
			// StoRM object model equivalents!
			throw new SRMClientException(
				"There were problems translating between WebService and StoRM object model! "
					+ e);
		} catch (InvalidPutReplyAttributeException e) {
			// This is a programming bug and should not occur! For some reason,
			// correctly translated WebService attributes
			// did not allow the correct creation of an SRMPrepareToPutReply!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create a reply to return! "
					+ e);
		} catch (Exception e) {
			// Unexpected runtime exception! For example, a time out when connectin to
			// the web service!
			throw new SRMClientException(
				"Unexpected runtime error in NaiveSRMClient! " + e);
		} // end synchronized!
	}

	/**
	 * Method used to execute an srmStatusOfPutRequest: it requires the request
	 * token returned during an srmPrepareToPut operation, the GridUser that
	 * issued the command, and the toSURL of the request. An
	 * SRMStatusOfPutRequestReply is returned, but in case the operation fails an
	 * SRMClientException is thrown containing an explanation String.
	 * 
	 * The client checks the overall status, and provided it is in either of
	 * SRM_REQUEST_QUEUED, SRM_REQUEST_INPROGESS, SRM_SUCCESS, SRM_PARTIAL_SUCCESS
	 * it will go and attempt retrieving the specific file status. If it is
	 * successful it will return the appropriate SRMStatusOfPutReply.
	 * 
	 * Any other request level status results in an SRMClientException. If there
	 * are any errors in the communication, or the reply received from the other
	 * web service is incomprehensible or malformed, an SRMClientException is
	 * thrown with appropriate error messagges.
	 */
	public SRMStatusOfPutRequestReply statusOfPutRequest(TRequestToken rt,
		GridUserInterface gu, TSURL toSURL) throws SRMClientException {

		try {
			srmClientStubs.ISRM _srm = setUpGSI(gu, toSURL);

			// prepare srm request and execute it
			srmClientStubs.SrmStatusOfPutRequestRequest req = new srmClientStubs.SrmStatusOfPutRequestRequest();
			// set TRequestToken
			req.setRequestToken(rt.toString());
			// set SURL array
			org.apache.axis.types.URI[] stubSurlArray = new org.apache.axis.types.URI[1];
			org.apache.axis.types.URI stubSurl = new org.apache.axis.types.URI(
				toSURL.toString());
			stubSurlArray[0] = stubSurl;
			srmClientStubs.ArrayOfAnyURI arrayOfTSURL = new srmClientStubs.ArrayOfAnyURI(
				stubSurlArray);
			req.setArrayOfTargetSURLs(arrayOfTSURL);

			// execute request
			log.debug("NAIVE SRM CLIENT: invoking status of put with " + req);
			srmClientStubs.SrmStatusOfPutRequestResponse response = _srm
				.srmStatusOfPutRequest(req);
			log.debug("NAIVE SRM CLIENT: received response " + response);

			// get overall request status
			if (response == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: got a null response!");
			}
			srmClientStubs.TReturnStatus overallRetStat = response.getReturnStatus();
			if (overallRetStat == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null request level TReturnStatus!");
			}
			// check overall request status
			it.grid.storm.srm.types.TStatusCode overallStat = WSReturnStatusConverter
				.getInstance().fromWS(overallRetStat).getStatusCode();
			if ((overallStat != it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_QUEUED)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_PARTIAL_SUCCESS)) {
				// overall status is some kind of failure!
				return new SRMStatusOfPutRequestReply(TTURL.makeEmpty(),
					WSReturnStatusConverter.getInstance().fromWS(overallRetStat));
			}
			// get file level request status
			srmClientStubs.ArrayOfTPutRequestFileStatus arrayOfPutStatuses = response
				.getArrayOfFileStatuses();
			if (arrayOfPutStatuses == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null ArrayOfTPutRequestFileStatus!");
			}
			srmClientStubs.TPutRequestFileStatus[] stubFileStatusArray = arrayOfPutStatuses
				.getStatusArray();
			if ((stubFileStatusArray == null) || (stubFileStatusArray.length == 0)) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null or empty StatusArray! ");
			}
			srmClientStubs.TPutRequestFileStatus stubFileStatus = stubFileStatusArray[0];
			if (stubFileStatus == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null first entry of StatusArray! ");
			}
			// process file level status
			org.apache.axis.types.URI stubTurl = stubFileStatus.getTransferURL();
			srmClientStubs.TReturnStatus stubStatus = stubFileStatus.getStatus();
			it.grid.storm.srm.types.TTURL turl = new WSTurlConverter()
				.fromWS(stubTurl);
			it.grid.storm.srm.types.TReturnStatus retStat = WSReturnStatusConverter
				.getInstance().fromWS(stubStatus);
			// return reply
			return new SRMStatusOfPutRequestReply(turl, retStat);
		} catch (SRMClientException e) {
			// needed to re-throw SRMClientException that otherwise would get caught
			// by generic catch at the bottom!
			throw e;
		} catch (it.grid.storm.srm.types.InvalidTUserIDAttributeException e) {
			// GridUser threw an exception! Cannot get credentials!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot get gredentials from GridUser! "
					+ e);
		} catch (GlobusCredentialException e) {
			// Globus credentials cannot be created!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create GlobusCredentials from GridUser! "
					+ e);
		} catch (GSSException e) {
			// GSS credentials cannot be created!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create GSSCredentials from GlobusCredential! "
					+ e);
		} catch (org.apache.axis.types.URI.MalformedURIException ex) {
			// This is a programming bug and should not occur! The URI is malformed!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create URI! " + ex);
		} catch (javax.xml.rpc.ServiceException ex) {
			// Something went wrong with the web service!
			throw new SRMClientException("Web Service problem! " + ex);
		} catch (java.rmi.RemoteException ex) {
			// something went wrong at low level communication with web service!
			throw new SRMClientException("Communication problem! " + ex);
		} catch (SOAPFaultException e) {
			// the remote service launched an exception! This is a programmatic
			// exception remotely thrown, _not_ an error!
			throw new SRMClientException(
				"The remote web service launched an exception! " + e);
		} catch (WSConversionException e) {
			// the data received fro mthe web service could not be translated into
			// StoRM object model equivalents!
			throw new SRMClientException(
				"The answer received from the WebService cannot be converted to StoRM object model! "
					+ e);
		} catch (InvalidPutStatusAttributesException e) {
			// This is a programming bug and should not occur! For some reason,
			// correctly translated WebService attributes
			// did not allow the correct creation of an SRMStatusOfPutRequestReply!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create a reply to return! "
					+ e);
		} catch (Exception e) {
			// Catch any runtime exceptions, such as a time out!
			throw new SRMClientException(
				"Unexpected runtime error in NaiveSRMClient! " + e);
		}
	}

	/**
	 * Method used to execute an srmPutDone. The reply contains the status for the
	 * specified toSURL; yet if the overall status received differs from
	 * SRM_SUCCESS or SRM_PARTIAL_SUCCESS, the reply will contain _that_ overall
	 * status instead. The overall status is also returned in case the received
	 * array (containing file specific status) is null or has length equals to 0
	 * (zero).
	 */
	public SRMPutDoneReply srmPutDone(TRequestToken rt, GridUserInterface gu,
		TSURL toSURL) throws SRMClientException {

		try {
			srmClientStubs.ISRM _srm = setUpGSI(gu, toSURL);

			// prepare srm request and execute it
			srmClientStubs.SrmPutDoneRequest req = new srmClientStubs.SrmPutDoneRequest();
			// set TRequestToken
			req.setRequestToken(rt.toString());
			// set array of SURLs
			org.apache.axis.types.URI[] stubSurlArray = new org.apache.axis.types.URI[1];
			org.apache.axis.types.URI stubSurl = new org.apache.axis.types.URI(
				toSURL.toString());
			stubSurlArray[0] = stubSurl;
			srmClientStubs.ArrayOfAnyURI arrayOfTSURL = new srmClientStubs.ArrayOfAnyURI(
				stubSurlArray);
			req.setArrayOfSURLs(arrayOfTSURL);

			// execute request
			log.debug("NAIVE SRM CLIENT: invoking srmPutDone with " + req);
			srmClientStubs.SrmPutDoneResponse response = _srm.srmPutDone(req);
			log.debug("NAIVE SRM CLIENT: received response " + response);

			// get overall request status
			if (response == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: got a null response!");
			}
			srmClientStubs.TReturnStatus overallRetStat = response.getReturnStatus();
			if (overallRetStat == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null request level TReturnStatus!");
			}
			// check overall request status
			it.grid.storm.srm.types.TStatusCode overallStat = WSReturnStatusConverter
				.getInstance().fromWS(overallRetStat).getStatusCode();
			if ((overallStat != it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS)
				&& (overallStat != it.grid.storm.srm.types.TStatusCode.SRM_PARTIAL_SUCCESS)) {
				// overall status is some kind of failure!
				return new SRMPutDoneReply(WSReturnStatusConverter.getInstance()
					.fromWS(overallRetStat));
			}
			// get file level request status
			srmClientStubs.ArrayOfTSURLReturnStatus arrayOfFileStatuses = response
				.getArrayOfFileStatuses();
			// ArrayOfTSURLReturnStatus is optional according to the specifications!
			// If indeed it is absent, assume that
			// the web service saved bandwidth by not including a detailed description
			// for each SURL, so the overall
			// status does summarise the status also for the specific SURL! Return the
			// overall status!
			if (arrayOfFileStatuses == null) {
				return new SRMPutDoneReply(WSReturnStatusConverter.getInstance()
					.fromWS(overallRetStat)); /*
																		 * throw new SRMClientException(
																		 * "Unexpected reply from WebService: null ArrayOfTSURLReturnStatus!"
																		 * );
																		 */
			}
			srmClientStubs.TSURLReturnStatus[] stubFileStatusArray = arrayOfFileStatuses
				.getStatusArray();
			if ((stubFileStatusArray == null) || (stubFileStatusArray.length == 0)) {
				return new SRMPutDoneReply(WSReturnStatusConverter.getInstance()
					.fromWS(overallRetStat)); /*
																		 * throw new SRMClientException(
																		 * "Unexpected reply from WebService: null or empty StatusArray! "
																		 * );
																		 */
			}
			srmClientStubs.TSURLReturnStatus stubFileStatus = stubFileStatusArray[0];
			if (stubFileStatus == null) {
				throw new SRMClientException(
					"Unexpected reply from WebService: null first entry of StatusArray! ");
			}
			// process file level satus
			srmClientStubs.TReturnStatus stubStatus = stubFileStatus.getStatus();
			it.grid.storm.srm.types.TReturnStatus retStat = WSReturnStatusConverter
				.getInstance().fromWS(stubStatus);
			// return reply
			return new SRMPutDoneReply(retStat);
		} catch (SRMClientException e) {
			// needed to re-throw SRMClientException that otherwise would get caught
			// by generic catch at the bottom!
			throw e;
		} catch (it.grid.storm.srm.types.InvalidTUserIDAttributeException e) {
			// GridUser threw an exception! Cannot get credentials!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot get gredentials from GridUser! "
					+ e);
		} catch (GlobusCredentialException e) {
			// Globus credentials cannot be created!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create GlobusCredentials from GridUser! "
					+ e);
		} catch (GSSException e) {
			// GSS credentials cannot be created!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create GSSCredentials from GlobusCredential! "
					+ e);
		} catch (org.apache.axis.types.URI.MalformedURIException ex) {
			// This is a programming bug and should not occur! The URI is malformed!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create URI! " + ex);
		} catch (javax.xml.rpc.ServiceException ex) {
			// Something went wrong with the web service!
			throw new SRMClientException("Web Service problem! " + ex);
		} catch (java.rmi.RemoteException ex) {
			// something went wrong at low level communication with web service!
			throw new SRMClientException("Communication problem! " + ex);
		} catch (SOAPFaultException e) {
			// the remote service launched an exception! This is a programmatic
			// exception remotely thrown, _not_ an error!
			throw new SRMClientException(
				"The remote web service launched an exception! " + e);
		} catch (WSConversionException e) {
			// the data received fro mthe web service could not be translated into
			// StoRM object model equivalents!
			throw new SRMClientException(
				"The answer received from the WebService cannot be converted to StoRM object model! "
					+ e);
		} catch (InvalidPutDoneReplyAttributeException e) {
			// This is a programming bug and should not occur! For some reason,
			// correctly translated WebService attributes
			// did not allow the correct creation of an SRMPutDoneReply!
			throw new SRMClientException(
				"Unexpected error in NaiveSRMClient! Cannot create a reply to return! "
					+ e);
		} catch (Exception e) {
			// Catch any runtime exceptions, such as a time out!
			throw new SRMClientException(
				"Unexpected runtime error in NaiveSRMClient! " + e);
		}
	}

	/**
	 * Private auxiliary method that sets up a secure GSI connection for the given
	 * GridUser to the specfied toSURL. The method returns the service interface.
	 */
	private ISRM setUpGSI(GridUserInterface gu, TSURL toSURL)
		throws ServiceException, GlobusCredentialException, GSSException,
		InvalidTUserIDAttributeException {

		// get web service, setting HTTP and HTTPG as transport!
		Util.registerTransport();
		SimpleProvider provider = new SimpleProvider();
		SimpleTargetedChain c = null;
		c = new SimpleTargetedChain(new GSIHTTPSender());
		provider.deployTransport("httpg", c);
		c = new SimpleTargetedChain(new HTTPSender());
		provider.deployTransport("http", c);
		srmClientStubs.SRMService sRMService = new srmClientStubs.SRMServiceLocator(
			provider);
		srmClientStubs.ISRM _srm = sRMService.getsrm();

		// set proxy in stub
		if (((AbstractGridUser) gu).getUserCredentials() == null) {
			log.error("ERROR in NaiveSRMClient! No proxy present for " + gu.getDn());
		}
		InputStream proxy = new ByteArrayInputStream(((AbstractGridUser) gu)
			.getUserCredentials().getBytes()); // String containing the proxy seen as
																					// an input stream!
		GSSCredential globusProxy = new GlobusGSSCredentialImpl(
			new GlobusCredential(proxy), GSSCredential.INITIATE_AND_ACCEPT); // GSSCredential
																																				// containing
																																				// the
																																				// proxy!
		((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_CREDENTIALS, globusProxy); // set
																																								// the
																																								// proxy
																																								// to
																																								// be
																																								// used
																																								// during
																																								// GSI
																																								// connection!
		((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_AUTHORIZATION,
			HostAuthorization.getInstance()); // set the authorization that will be
																				// performed by the web service for the
																				// supplied credentails!
		((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_MODE,
			GSIHTTPTransport.GSI_MODE_FULL_DELEG); // set the supply of both private
																							// and public key of credentials

		// set service endpoint address
		EndPoint ep = toSURL.sfn().endPoint();
		String epString = "";
		if (ep.isEmpty()) {
			epString = "/";
		} else {
			epString = ep.toString();
		}
		Port port = toSURL.sfn().port();
		String portString = "";
		if (!port.isEmpty()) {
			portString = ":" + port.toString();
		}
		String sea = "httpg://" + toSURL.sfn().machine() + portString + epString;
		((Stub) _srm)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, sea);

		// return the interface
		return _srm;
	}
}
