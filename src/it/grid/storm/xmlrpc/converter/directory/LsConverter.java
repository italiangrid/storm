package it.grid.storm.xmlrpc.converter.directory;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.InvalidLSInputDataAttributeException;
import it.grid.storm.synchcall.data.directory.LSInputData;
import it.grid.storm.synchcall.data.directory.LSOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * This class is part of the StoRM project.
 * 
 * This class represents the Type Converter for LS function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the LSManager
 * 
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class LsConverter implements Converter
{
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public LsConverter()
    {
    };

    /** This method return a LSInputData created from input Hashtable structure of an xmlrpc spaceReservation v2.1 call.
     *  SpaceResData can be used to invoke LS method of Directory Functions Manager
     */
    public InputData convertToInputData(Map inputParam)
    {
        log.debug("SrmLs: LSConverter :Call received :Creation of SpaceResData = " + inputParam.size());
        log.debug("SrmLs: LSConverter: Input Structure toString: " + inputParam.toString());

        /* Creation of LSInputData*/
        LSInputData inputData = null;

        // Member name definition for inputParam struct , from SRM V2.2
        String member_fullDL = new String("fullDetailedList");
        String member_allLR = new String("allLevelRecursive");
        String member_numOL = new String("numOfLevels");
        String member_offset = new String("offset");
        String member_count = new String("count");

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        String member_authID = new String("authorizationID");
        String authID = (String) inputParam.get(member_authID);

        /* (2) anyURI[] arrayOfSURLs */
        ArrayOfSURLs surlArray = null;
        try {
            surlArray = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e2) {
            log.debug("SrmLs: Empty surlArray found!");
            surlArray = null;
        }

        /* (3) TExtraInfo[] storageSystemInfo */
        ArrayOfTExtraInfo infoArray = null;
        try {
            infoArray = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        } catch (InvalidArrayOfTExtraInfoAttributeException e1) {
            log.debug("SrmLs: Empty infoArray found!");
            infoArray = null;
        }

        /* (4) FileStorageType */
        TFileStorageType fileStorageType = TFileStorageType.decode(inputParam, TFileStorageType.PNAME_FILESTORAGETYPE);
        log.debug("fileType: " + fileStorageType);

        /* (5) fullDetailedList */
        Boolean fullDL = (Boolean) inputParam.get(member_fullDL);
        log.debug("fullDetailedList: " + fullDL);

        /* (6) allLevelRecursive */
        Boolean allLR = (Boolean) inputParam.get(member_allLR);
        log.debug("allLevelRecursive: " + allLR);

        /* (7) numOfLevels */
        Integer numOL = (Integer) inputParam.get(member_numOL);
        log.debug("numOfLevels: " + numOL);

        /* (8) offset */
        Integer offset = (Integer) inputParam.get(member_offset);
        log.debug("offset: " + offset);

        /* (9) count */
        Integer count = (Integer) inputParam.get(member_count);
        log.debug("count: " + count);

        // Creation of input structure used for Directory Manager invokation
        try {
            inputData = new LSInputData(guser, surlArray, infoArray, fileStorageType, fullDL, allLR, numOL, offset, count);
        } catch (InvalidLSInputDataAttributeException e) {
            log.debug("SrmLs: Error Creating LSInputData! " + e);
        }

        return inputData;
    }

    public Hashtable convertFromOutputData(OutputData data)
    {
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
        if (requestToken != null) outputParam.put("requestToken", requestToken.toString());

        /* (3) ArrayOfTMetaDataPathDetail details*/
        ArrayOfTMetaDataPathDetail details = outputData.getDetails();
        if (details != null) details.encode(outputParam, ArrayOfTMetaDataPathDetail.PNAME_DETAILS);

        // Return global structure.
        return outputParam;
    }
}
