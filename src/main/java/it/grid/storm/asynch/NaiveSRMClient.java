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

import it.grid.storm.common.types.TransferProtocol;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
import org.globus.gsi.gssapi.auth.NoAuthorization;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a first implementation of an SRMClient.
 * 
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date October, 2005
 */
public class NaiveSRMClient implements SRMClient {

	private static Logger log = LoggerFactory.getLogger(NaiveSRMClient.class);

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
	 * This client is said to be naive, because given the reply from an
	 * SRMPrepareToPut, the client checks the srm field of the whole request to
	 * obtain the request token which the remote srm server associated to the
	 * request; it _ignores_ the return status of the whole request; it _ignores_
	 * the status for the specific file requested.
	 * 
	 * This is important to keep in mind, because there can be remote servers that
	 * have specific ways of behaving such as leaving the specific file status
	 * blank until a status for the whole request is completed, etc.
	 */
	public SRMPrepareToPutReply prepareToPut(GridUserInterface gu, TSURL toSURL,
		TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
		TSpaceToken spaceToken, TSizeInBytes filesize, TransferProtocol protocol,
		String description, TOverwriteMode overwriteOption,
		TLifeTimeInSeconds retryTime) throws SRMClientException {

		try {
			srmClientStubs.ISRM _srm = setUpGSI(gu, toSURL);

			// prepare srm request and execute it
			srmClientStubs.SrmPrepareToPutRequest req = new srmClientStubs.SrmPrepareToPutRequest();
			// set authorization ID
			String stubuid = gu.getDn();
			req.setAuthorizationID(stubuid);
			// set description
			req.setUserRequestDescription(description);
			// set array of transfer protocols
			String[] protocolArray = new String[1];
			protocolArray[0] = protocol.toString();
			srmClientStubs.ArrayOfString protocolArrayString = new srmClientStubs.ArrayOfString();
			protocolArrayString.setStringArray(protocolArray);
			srmClientStubs.TTransferParameters stubtp = new srmClientStubs.TTransferParameters();
			stubtp.setArrayOfTransferProtocols(protocolArrayString);
			req.setTransferParameters(stubtp);
			// set Overwrite Option
			req.setOverwriteOption(new WSOverwriteModeConverter()
				.fromStoRM(overwriteOption));
			// set retry time
			req.setDesiredTotalRequestTime(new Integer(new Long(retryTime.value())
				.intValue()));

			// set storage type
			srmClientStubs.TFileStorageType stubfst = new WSFileStorageTypeConverter()
				.fromStoRM(fileStorageType); // ws file storage type!
			req.setDesiredFileStorageType(stubfst);
			// set space token
			req.setTargetSpaceToken(spaceToken.toString());
			// set lifetime
			req.setDesiredFileLifeTime(new Integer(new Long(lifetime.value())
				.intValue()));

			// set request specific info
			// ws put file request!
			srmClientStubs.TPutFileRequest stubpfr = new srmClientStubs.TPutFileRequest();
			// set file size
			org.apache.axis.types.UnsignedLong ulFileSize = new org.apache.axis.types.UnsignedLong(
				filesize.value());
			stubpfr.setExpectedFileSize(ulFileSize);
			// set target SURL
			stubpfr.setTargetSURL(new org.apache.axis.types.URI(toSURL.toString()));

			// set array of requests ws array of put file request!
			srmClientStubs.TPutFileRequest[] stubpfrArray = new srmClientStubs.TPutFileRequest[1];
			stubpfrArray[0] = stubpfr;
			srmClientStubs.ArrayOfTPutFileRequest arrayOfPut = new srmClientStubs.ArrayOfTPutFileRequest(
				stubpfrArray);
			req.setArrayOfFileRequests(arrayOfPut);

			// execute request!
			log.debug("NAIVE SRM CLIENT: sending request {}", arrayOfPut);
			srmClientStubs.SrmPrepareToPutResponse response = _srm
				.srmPrepareToPut(req);
			log.debug("NAIVE SRM CLIENT: received reply {}", response);

			// get TRequestToken
			it.grid.storm.srm.types.TRequestToken requestToken = new WSRequestTokenConverter()
				.fromWS(response.getRequestToken());
			// get ArrayOfTPutRequestFileStatus
			srmClientStubs.ArrayOfTPutRequestFileStatus arrayOfPutFileStatuses = response
				.getArrayOfFileStatuses();
			srmClientStubs.TPutRequestFileStatus[] stubFileStatusArray = arrayOfPutFileStatuses
				.getStatusArray();
			// get specific request
			srmClientStubs.TPutRequestFileStatus stubFileStatus = stubFileStatusArray[0];
			srmClientStubs.TReturnStatus stubStatus = stubFileStatus.getStatus();
			it.grid.storm.srm.types.TReturnStatus retStat = WSReturnStatusConverter
				.getInstance().fromWS(stubStatus);
			return new SRMPrepareToPutReply(requestToken);

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
	 * token returned durign an srmPrepareToPut operation, the GridUser that
	 * issued the command, and the toSURL of the request. An
	 * SRMStatusOfPutRequestReply is returned, but in case the operation fails an
	 * SRMClientException is thrown containing an explanation String.
	 * 
	 * This client is said to be naive, because given the reply from an
	 * SRMStatusOfPutRequest, the client checks the srm field of the specific file
	 * to obtain both the TURL and the status of the request, _ignoring_ the
	 * overall request status.
	 * 
	 * This is important to keep in mind, because there can be remote servers that
	 * have specific ways of behaving such as leaving the specific file status
	 * blank until a status for the whole request is completed, etc.
	 */
	public SRMStatusOfPutRequestReply statusOfPutRequest(
		it.grid.storm.srm.types.TRequestToken rt, GridUserInterface gu,
		it.grid.storm.srm.types.TSURL toSURL) throws SRMClientException {

		try {
			srmClientStubs.ISRM _srm = setUpGSI(gu, toSURL);

			// prepare srm request and execute it
			srmClientStubs.SrmStatusOfPutRequestRequest req = new srmClientStubs.SrmStatusOfPutRequestRequest();
			// set TRequestToken
			req.setRequestToken(rt.toString());
			// set Authorization ID
			req.setAuthorizationID(gu.getDn());
			// set SURL array
			org.apache.axis.types.URI[] stubSurlArray = new org.apache.axis.types.URI[1];
			org.apache.axis.types.URI stubSurl = new org.apache.axis.types.URI(
				toSURL.toString());
			stubSurlArray[0] = stubSurl;
			srmClientStubs.ArrayOfAnyURI arrayOfTSURL = new srmClientStubs.ArrayOfAnyURI(
				stubSurlArray);
			req.setArrayOfTargetSURLs(arrayOfTSURL);

			log.debug("NAIVE SRM CLIENT: invoking status of put with {}", req);
			srmClientStubs.SrmStatusOfPutRequestResponse response = _srm
				.srmStatusOfPutRequest(req);
			log.debug("NAIVE SRM CLIENT: received response {}", response);

			// process response
			srmClientStubs.ArrayOfTPutRequestFileStatus arrayOfPutStatuses = response
				.getArrayOfFileStatuses();
			srmClientStubs.TPutRequestFileStatus[] stubFileStatusArray = arrayOfPutStatuses
				.getStatusArray();
			srmClientStubs.TPutRequestFileStatus stubFileStatus = stubFileStatusArray[0];
			org.apache.axis.types.URI stubTurl = stubFileStatus.getTransferURL();
			srmClientStubs.TReturnStatus stubStatus = stubFileStatus.getStatus();

			it.grid.storm.srm.types.TTURL turl = new WSTurlConverter()
				.fromWS(stubTurl);
			it.grid.storm.srm.types.TReturnStatus retStat = WSReturnStatusConverter
				.getInstance().fromWS(stubStatus);
			return new SRMStatusOfPutRequestReply(turl, retStat);
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
		} // end synchronized!
	}

	/**
	 * Method that returns an SRMPutDoneReply containing TReturnStatus with
	 * TStatusCode.SRM_SUCCESS, and explanation string "DUMMY SUCCESS".
	 */
	public SRMPutDoneReply srmPutDone(TRequestToken rt, GridUserInterface gu,
		TSURL toSURL) throws SRMClientException {

		return null;
	}

	/**
	 * Private auxiliary method that sets up a secure GSI connection for the given
	 * GridUser to the specfied toSURL. The method returns the service interface.
	 */
	private srmClientStubs.ISRM setUpGSI(GridUserInterface gu,
		it.grid.storm.srm.types.TSURL toSURL)
		throws javax.xml.rpc.ServiceException, GlobusCredentialException,
		GSSException, it.grid.storm.srm.types.InvalidTUserIDAttributeException {

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
			log.error("ERROR in NaiveSRMClient! No proxy present for {}", gu.getDn());
		}
		// String containing the proxy seen as an input stream!
		InputStream proxy = new ByteArrayInputStream(((AbstractGridUser) gu)
			.getUserCredentials().getBytes());
		// GSSCredential containing the proxy!
		GSSCredential globusProxy = new GlobusGSSCredentialImpl(
			new GlobusCredential(proxy), GSSCredential.INITIATE_AND_ACCEPT);
		// set the proxy to be used during GSI connection!
		((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_CREDENTIALS, globusProxy); 
		// set the authorization that will be performed by the web service for the
		// supplied credentails!
		((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_AUTHORIZATION,
			NoAuthorization.getInstance());
		// set the supply of both private and public key of credentials
		((Stub) _srm)._setProperty(GSIHTTPTransport.GSI_MODE,
			GSIHTTPTransport.GSI_MODE_NO_DELEG); 

		// set service endpoint address
		String sea = String.format("httpg://%s:%s/", toSURL.sfn().machine(), toSURL
			.sfn().port());
		((Stub) _srm)._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, sea);

		// return the interface
		return _srm;
	}

}
