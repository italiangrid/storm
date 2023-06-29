/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTMetaDataPathDetail;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.AnonymousLSInputData;
import it.grid.storm.synchcall.data.directory.IdentityLSInputData;
import it.grid.storm.synchcall.data.directory.LSInputData;
import it.grid.storm.synchcall.data.directory.LSOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class represents the Type Converter for LS function . This class have
 * get an input data from xmlrpc call anc convert it into a StoRM Type that can
 * be used to invoke the LSManager
 * 
 * 
 * Authors:
 * 
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * 
 * @date = Oct 9, 2008
 * 
 */

public class LsConverter implements Converter {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(LsConverter.class);

	public LsConverter() {

	};

	/**
	 * This method return a LSInputData created from input Hashtable structure of
	 * an xmlrpc spaceReservation v2.1 call. SpaceResData can be used to invoke LS
	 * method of Directory Functions Manager
	 */
	public InputData convertToInputData(Map inputParam) {

		log.debug("SrmLs: LSConverter :Call received :Creation of SpaceResData = {}"
			, inputParam.size());
		log.debug("SrmLs: LSConverter: Input Structure toString: {}"
			, ParameterDisplayHelper.display(inputParam));

		// Member name definition for inputParam struct , from SRM V2.2
		String member_fullDL = new String("fullDetailedList");
		String member_allLR = new String("allLevelRecursive");
		String member_numOL = new String("numOfLevels");
		String member_offset = new String("offset");
		String member_count = new String("count");

		/* Creation of VomsGridUser */
		GridUserInterface guser = GridUserManager.decode(inputParam);

		/* (2) anyURI[] arrayOfSURLs */
		ArrayOfSURLs surlArray = null;
		try {
			surlArray = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAY_OF_SURLS);
		} catch (InvalidArrayOfSURLsAttributeException e2) {
			log.debug("SrmLs: Empty surlArray found!",e2);
			surlArray = null;
		}

		TFileStorageType fileStorageType = TFileStorageType.decode(inputParam,
			TFileStorageType.PNAME_FILESTORAGETYPE);
		log.debug("fileType: {}" , fileStorageType);

		/* (5) fullDetailedList */
		Boolean fullDL = (Boolean) inputParam.get(member_fullDL);
		log.debug("fullDetailedList: {}" , fullDL);

		/* (6) allLevelRecursive */
		Boolean allLR = (Boolean) inputParam.get(member_allLR);
		log.debug("allLevelRecursive: {}" , allLR);

		/* (7) numOfLevels */
		Integer numOL = (Integer) inputParam.get(member_numOL);
		log.debug("numOfLevels: {}" , numOL);

		/* (8) offset */
		Integer offset = (Integer) inputParam.get(member_offset);
		log.debug("offset: {}" , offset);

		/* (9) count */
		Integer count = (Integer) inputParam.get(member_count);
		log.debug("count: {}" , count);

		LSInputData inputData;
		if (guser != null) {
			inputData = new IdentityLSInputData(guser, surlArray, fileStorageType,
				fullDL, allLR, numOL, offset, count);
		} else {
			inputData = new AnonymousLSInputData(surlArray, fileStorageType, fullDL,
				allLR, numOL, offset, count);
		}
		return inputData;
	}

	public Hashtable convertFromOutputData(OutputData data) {

		// Creation of new Hashtable to return
		Hashtable outputParam = new Hashtable();
		LSOutputData outputData = (LSOutputData) data;

		/* (1) TReturnStatus */
		TReturnStatus globStatus = outputData.getStatus();
		if (globStatus != null) {
			globStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
		}

		/* (2) TRequestToken */
		TRequestToken requestToken = outputData.getRequestToken();
		if (requestToken != null) {
			outputParam.put("requestToken", requestToken.toString());
		}

		/* (3) ArrayOfTMetaDataPathDetail details */
		ArrayOfTMetaDataPathDetail details = outputData.getDetails();
		if (details != null) {
			details.encode(outputParam, ArrayOfTMetaDataPathDetail.PNAME_DETAILS);
		}

		// Return global structure.
		return outputParam;
	}
}
