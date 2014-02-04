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

package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.Hashtable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousFileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityFileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferOutputData;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * @author Michele Dibenedetto
 * 
 */
public abstract class FileTransferRequestInputConverter implements Converter {

	static final Logger log = LoggerFactory
		.getLogger(FileTransferRequestInputConverter.class);

	@Override
	public InputData convertToInputData(Map<String, Object> inputParam)
		throws IllegalArgumentException, StoRMXmlRpcException {

		TSURL surl = decodeSURL(inputParam);
		if (surl == null) {
			log.error("Missing mandatory parameter '{}' Unable to build FileTransferInputData" , TSURL.PNAME_SURL);
			throw new IllegalArgumentException("Missing mandatory parameter \'"
				+ TSURL.PNAME_SURL + "\'");
		}
		GridUserInterface user = decodeUser(inputParam);
		TURLPrefix transferProtocols = decodeTransferProtocols(inputParam);
		if (transferProtocols == null) {
			log.error("Missing mandatory parameter '{}' Unable to build FileTransferInputData" , TURLPrefix.PNAME_TURL_PREFIX);
			throw new IllegalArgumentException("Missing mandatory parameter \'"
				+ TURLPrefix.PNAME_TURL_PREFIX + "\'");
		}

		FileTransferInputData inputData;
		try {
			if (user != null) {
				inputData = new IdentityFileTransferInputData(user, surl,
					transferProtocols);
			} else {
				inputData = new AnonymousFileTransferInputData(surl, transferProtocols);
			}
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to build FileTransferInputData. IllegalArgumentException: {}"
					, e.getMessage(),e);
			throw new StoRMXmlRpcException("Unable to build FileTransferInputData");
		}
		TLifeTimeInSeconds desiredPinLifetime = decodeDesiredPinLifetime(inputParam);
		if (desiredPinLifetime != null) {
			inputData.setDesiredPinLifetime(desiredPinLifetime);
		}
		TSpaceToken targetSpaceToken = decodeTargetSpaceToken(inputParam);
		if (targetSpaceToken != null) {
			inputData.setTargetSpaceToken(targetSpaceToken);
		}
		return inputData;
	}

	@Override
	public Map<String, Object> convertFromOutputData(OutputData outputData)
		throws IllegalArgumentException {

		if (outputData == null) {
			log.error("Unable to build an output map. Null argument: outputData={}"
				, outputData);
			throw new IllegalArgumentException(
				"Unable to build a valid output map, null argument");
		}
		if (!(outputData instanceof FileTransferOutputData)) {
			log.error("Unable to convert from OutputData. Wrong OutputData type: '{}'"
				, outputData.getClass().getName());
			throw new IllegalArgumentException(
				"Unable to convert from OutputData. Wrong OutputData type: \'"
					+ outputData.getClass().getName() + "\'");
		}
		FileTransferOutputData ftOutputData = (FileTransferOutputData) outputData;
		TSURL surl = ftOutputData.getSurl();
		TTURL turl = ftOutputData.getTurl();
		TReturnStatus status = ftOutputData.getStatus();
		TRequestToken requestToken = ftOutputData.getRequestToken();
		if (surl == null || surl.isEmpty() || surl.getSURLString().trim().isEmpty()
			|| turl == null || status == null || requestToken == null
			|| requestToken.getValue() == null || requestToken.getValue().isEmpty()) {
			log
				.error("Unable to build a valid output map. Missing mandatory values from FileTransferOutputData: {}"
					, ftOutputData.toString());
			throw new IllegalArgumentException(
				"Unable to build a valid output map from FileTransferOutputData");
		}
		Hashtable<String, Object> outputParam = new Hashtable<String, Object>();
		surl.encode(outputParam, TSURL.PNAME_SURL);
		turl.encode(outputParam, TTURL.PNAME_TURL);
		status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
		outputParam.put(TRequestToken.PNAME_REQUESTOKEN, requestToken.toString());
		log.debug("Built output Map: {}" , outputParam.toString());
		return outputParam;
	}

	protected TSpaceToken decodeTargetSpaceToken(Map<String, Object> inputParam) {

		return TSpaceToken.decode(inputParam, TSpaceToken.PNAME_SPACETOKEN);
	}

	protected TLifeTimeInSeconds decodeDesiredPinLifetime(
		Map<String, Object> inputParam) {

		return TLifeTimeInSeconds.decode(inputParam,
			TLifeTimeInSeconds.PNAME_PINLIFETIME);
	}

	protected TURLPrefix decodeTransferProtocols(Map<String, Object> inputParam)
		throws IllegalArgumentException {

		TURLPrefix transferProtocols = TURLPrefix.decode(inputParam,
			TURLPrefix.PNAME_TURL_PREFIX);
		if (transferProtocols == null) {
			log.error("Missing mandatory parameter '{}' Unable to build FileTransferInputData" , TURLPrefix.PNAME_TURL_PREFIX);
			throw new IllegalArgumentException("Missing mandatory parameter \'"
				+ TURLPrefix.PNAME_TURL_PREFIX + "\'");
		}
		return transferProtocols;
	}

	protected GridUserInterface decodeUser(Map<String, Object> inputParam) {

		return GridUserManager.decode(inputParam);
	}

	protected TSURL decodeSURL(Map<String, Object> inputParam)
		throws IllegalArgumentException {

		TSURL surl = null;
		try {
			surl = TSURL.decode(inputParam, TSURL.PNAME_SURL);
		} catch (InvalidTSURLAttributesException e) {
			log.error("Unable to decode '{}' parameter as TSURL. InvalidTSURLAttributesException: {}" , TSURL.PNAME_SURL
				, e.getMessage(),e);
			throw new IllegalArgumentException("Unable to decode \'"
				+ TSURL.PNAME_SURL + "\' parameter as TSURL");
		}
		return surl;
	}

}
