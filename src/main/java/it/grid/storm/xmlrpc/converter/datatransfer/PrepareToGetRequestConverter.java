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
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToGetOutputData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PrepareToGetRequestConverter extends
	FileTransferRequestInputConverter {

	private static final Logger log = LoggerFactory
		.getLogger(PrepareToGetRequestConverter.class);

	@Override
	public Map<String, Object> convertFromOutputData(OutputData outputData) {

		if (!(outputData instanceof PrepareToGetOutputData)) {
			log.error("Unable to convert from OutputData. Wrong OutputData type: '{}'"
				, outputData.getClass().getName());
			throw new IllegalArgumentException(
				"Unable to convert from OutputData. Wrong OutputData type: \'"
					+ outputData.getClass().getName() + "\'");
		}
		Map<String, Object> outputParam = super.convertFromOutputData(outputData);
		PrepareToGetOutputData ptgOutputData = (PrepareToGetOutputData) outputData;
		ptgOutputData.getFileSize().encode(outputParam, TSizeInBytes.PNAME_SIZE);
		ptgOutputData.getRemainingPinTime().encode(outputParam,
			TLifeTimeInSeconds.PNAME_PINLIFETIME);
		log.debug("Built output Map: {}" , outputParam.toString());
		return outputParam;
	}

}
