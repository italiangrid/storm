/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the Type Converter for PutDone function. This class
 * receives input datas from xmlrpc call and converts these datas into a StoRM
 * Type that can be used to invoke the PutDoneManager.
 * 
 * @author Alberto Forti
 * @author CNAF -INFN Bologna
 * @date Aug 2006
 * @version 1.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.datatransfer.AnonymousPutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityPutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutDoneConverter extends ManageFileTransferConverter implements
	Converter {

	private static final Logger log = LoggerFactory
		.getLogger(PutDoneConverter.class);

	public PutDoneConverter() {

	}

	/**
	 * This method returns a PutDoneInputData created from the input Hashtable
	 * structure of an xmlrpc PutDone v2.2 call.
	 * 
	 * @param inputParam
	 *          Hashtable containing the input data
	 * @return PutDoneInputData
	 */
	public InputData convertToInputData(Map<String, Object> inputParam) {

		GridUserInterface guser = GridUserManager.decode(inputParam);

		TRequestToken requestToken;
		try {
			requestToken = TRequestToken.decode(inputParam,
				TRequestToken.PNAME_REQUESTOKEN);
			log.debug("requestToken={}" , requestToken.toString());
		} catch (InvalidTRequestTokenAttributesException e) {
			requestToken = null;
			log.debug("requestToken=NULL" , e);
		}

		ArrayOfSURLs arrayOfSURLs;
		try {
			arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAY_OF_SURLS);
		} catch (InvalidArrayOfSURLsAttributeException e) {
			log.debug("Empty surlArray!",e);
			arrayOfSURLs = null;
		}

		ManageFileTransferRequestFilesInputData inputData;
		if (guser != null) {
			inputData = new IdentityPutDoneInputData(guser, requestToken,
				arrayOfSURLs);
		} else {
			inputData = new AnonymousPutDoneInputData(requestToken, arrayOfSURLs);
		}

		log.debug("PutDoneInputData Created!");

		return inputData;
	}

	@Override
	protected Logger getLogger() {

		return log;
	}

}
