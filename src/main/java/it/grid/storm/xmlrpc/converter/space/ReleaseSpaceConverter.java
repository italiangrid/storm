/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.space;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.space.AnonymousReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.IdentityReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceInputData;
import it.grid.storm.synchcall.data.space.ReleaseSpaceOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the Type Converter for ReleaseSpace function . This
 * class have get an input data from xmlrpc call anc convert it into a StoRM
 * Type that can be used to invoke the ReleaseSpaceManager
 * 
 * @author Magnoni Luca
 * @author Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

public class ReleaseSpaceConverter implements Converter {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
		.getLogger(ReleaseSpaceConverter.class);

	public ReleaseSpaceConverter() {

	};

	/**
	 * This method return a ReleaseSpaceInputData created from input Hashtable
	 * structure of an xmlrpc releaseSpace v2.1 call. ReleaseSpaceInputData can be
	 * used to invoke ReleaseSpace Manager
	 */

	public InputData convertToInputData(Map inputParam) {

		log
			.debug("ReleaseSpaceConverter :Call received :Creation of SpaceResData = {}"
				, inputParam.size());
		log.debug("ReleaseSpaceConverter: Input Structure toString: {}"
			, ParameterDisplayHelper.display(inputParam));

		GridUserInterface guser = GridUserManager.decode(inputParam);

		TSpaceToken spaceToken = TSpaceToken.decode(inputParam,
			TSpaceToken.PNAME_SPACETOKEN);

		Boolean force = (Boolean) inputParam.get("forceFileRelease");
		if (force == null) {
			force = new Boolean(false);
		}

		ReleaseSpaceInputData inputData;
		if (guser != null) {
			inputData = new IdentityReleaseSpaceInputData(guser, spaceToken,
				force.booleanValue());
		} else {
			inputData = new AnonymousReleaseSpaceInputData(spaceToken,
				force.booleanValue());
		}
		return inputData;

	}

	public Map convertFromOutputData(OutputData data) {

		log
			.debug("releaseSpaceConverter :Call received :Creation of XMLRPC Output Structure! ");
		ReleaseSpaceOutputData outputData = (ReleaseSpaceOutputData) data;

		// Creation of new Hashtable to return
		Map outputParam = new HashMap();

		TReturnStatus returnStatus = outputData.getStatus();
		returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

		// Return output Parameter structure
		return outputParam;
	}
}
