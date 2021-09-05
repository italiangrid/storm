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

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.config.model.OverwriteMode;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.converter.OverwriteModeConverter;
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

        String overwriteModeString = (String) inputParam.get(OVERWRITE_MODE_PARAMETER_NAME);
        if (overwriteModeString != null) {
          TOverwriteMode overwriteMode =
              OverwriteModeConverter.toSTORM(OverwriteMode.valueOf(overwriteModeString));
          if (!overwriteMode.equals(TOverwriteMode.EMPTY)) {
            inputData.setOverwriteMode(overwriteMode);
          } else {
            log.warn("Unable to use the received '{} = {}', interpreted as an empty value",
                OVERWRITE_MODE_PARAMETER_NAME, overwriteModeString);
          }
        }
		log.debug("PrepareToPutInputData Created!");
		return inputData;
	}
}
