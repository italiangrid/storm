/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousPrepareToPutInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityPrepareToPutInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PrepareToPutRequestConverter extends
	FileTransferRequestInputConverter {

	static final String OVERWRITE_MODE_PARAMETER_NAME = "overwriteMode";
	static final Logger log = LoggerFactory
		.getLogger(PrepareToPutRequestConverter.class);

	@Override
	public InputData convertToInputData(Map<String, Object> inputParam)
		throws IllegalArgumentException, StoRMXmlRpcException {

		TSURL surl = decodeSURL(inputParam);
		GridUserInterface user = decodeUser(inputParam);
		TURLPrefix transferProtocols = decodeTransferProtocols(inputParam);

		PrepareToPutInputData inputData;
		try {
			if (user != null) {
				inputData = new IdentityPrepareToPutInputData(user, surl,
					transferProtocols);
			} else {
				inputData = new AnonymousPrepareToPutInputData(surl, transferProtocols);
			}
		} catch (IllegalArgumentException e) {
			log
				.error("Unable to build PrepareToPutInputData. IllegalArgumentException: {}"
					, e.getMessage(),e);
			throw new StoRMXmlRpcException("Unable to build PrepareToPutInputData");
		}
		TLifeTimeInSeconds desiredFileLifetime = TLifeTimeInSeconds.decode(
			inputParam, TLifeTimeInSeconds.PNAME_FILELIFETIME);
		if (desiredFileLifetime != null && !desiredFileLifetime.isEmpty()) {
			inputData.setDesiredFileLifetime(desiredFileLifetime);

		}

		TLifeTimeInSeconds desiredPinLifetime = decodeDesiredPinLifetime(inputParam);
		if (desiredPinLifetime != null) {
			inputData.setDesiredPinLifetime(desiredPinLifetime);
		}
		TSpaceToken targetSpaceToken = decodeTargetSpaceToken(inputParam);
		if (targetSpaceToken != null) {
			inputData.setTargetSpaceToken(targetSpaceToken);
		}
		TSizeInBytes fileSize = TSizeInBytes.decode(inputParam,
			TSizeInBytes.PNAME_SIZE);
		if (fileSize != null) {
			inputData.setFileSize(fileSize);
		}

		String overwriteModeString = (String) inputParam
			.get(OVERWRITE_MODE_PARAMETER_NAME);
		if (overwriteModeString != null) {
			TOverwriteMode overwriteMode;
			try {
				overwriteMode = TOverwriteMode.getTOverwriteMode(overwriteModeString);
			} catch (IllegalArgumentException e) {
				log.error("Unable to build TOverwriteMode from '{}'. IllegalArgumentException: {}"
					, overwriteModeString
					, e.getMessage()
					, e);
				throw new StoRMXmlRpcException("Unable to build PrepareToPutInputData");
			}
			if (!overwriteMode.equals(TOverwriteMode.EMPTY)) {
				inputData.setOverwriteMode(overwriteMode);
			} else {
				log
					.warn("Unable to use the received '{}', interpreted as an empty value" , OVERWRITE_MODE_PARAMETER_NAME);
			}
		}
		log.debug("PrepareToPutInputData Created!");
		return inputData;
	}
}
