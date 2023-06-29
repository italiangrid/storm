/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.AnonymousRmdirInputData;
import it.grid.storm.synchcall.data.directory.IdentityRmdirInputData;
import it.grid.storm.synchcall.data.directory.RmdirInputData;
import it.grid.storm.synchcall.data.directory.RmdirOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project.
 * 
 * This class represents the Type Converter for Rmdir function . This class have
 * get an input data from xmlrpc call anc convert it into a StoRM Type that can
 * be used to invoke the RmdirManager
 * 
 * Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 28, 2008
 * 
 */

public class RmdirConverter implements Converter {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
		.getLogger(RmdirConverter.class);

	public RmdirConverter() {

	};

	/**
	 * This method return a RmdirInputData created from input Hashtable structure
	 * of an xmlrpc Rmdir v2.1 call. Rmdir Input Data can be used to invoke mkdir
	 * method of DirectoryFunctionsManager
	 */
	public InputData convertToInputData(Map inputParam) {

		log
			.debug("srmRmdir: Converter :Call received :Creation of RmdirInputData = {}"
				, inputParam.size());
		log.debug("srmRmdir: Converter: Input Structure toString: {}"
			, ParameterDisplayHelper.display(inputParam));

		/* Creation of VomsGridUser */
		GridUserInterface guser = GridUserManager.decode(inputParam);

		/* (2) directoryPath */
		TSURL surl = null;
		try {
			surl = TSURL.decode(inputParam, TSURL.PNAME_SURL);
		} catch (InvalidTSURLAttributesException e1) {
			log.debug("srmRm: ErrorCreating surl: {}" , e1.toString(),e1);
		}

		/* (4) recursive */
		String member_recursive = new String("recursive");
		Boolean recursive = inputParam.get(member_recursive) == null ? false
			: (Boolean) inputParam.get(member_recursive);
		RmdirInputData inputData;
		if (guser != null) {
			inputData = new IdentityRmdirInputData(guser, surl, recursive);
		} else {
			inputData = new AnonymousRmdirInputData(surl, recursive);
		}
		return inputData;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.xmlrpc.converter.Converter#convertFromOutputData(it.grid.
	 * storm.synchcall.data.OutputData)
	 */
	public Map convertFromOutputData(OutputData outputData) {

		log
			.debug("srmRm: RmdirConverter :Call received :Creation of XMLRPC Output Structure! ");

		// Output structure to return to xmlrpc client
		Map outputParam = new HashMap();
		RmdirOutputData rmdirOutputData = (RmdirOutputData) outputData;
		TReturnStatus outputStatus = rmdirOutputData.getStatus();
		outputStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

		// Return Output Structure
		return outputParam;
	}
}
