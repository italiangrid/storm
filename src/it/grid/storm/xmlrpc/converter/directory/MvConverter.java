package it.grid.storm.xmlrpc.converter.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.InvalidArrayOfTExtraInfoAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.InvalidMvInputAttributeException;
import it.grid.storm.synchcall.data.directory.MvInputData;
import it.grid.storm.synchcall.data.directory.MvOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * This class represents the Type Converter for SrmMv function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the MvExecutor.
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class MvConverter implements Converter
{
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(MvConverter.class);

    public MvConverter()
    {
    };

    /**
     * This method return a MvInputData created from input Map
     * structure of an xmlrpc SrmMv v2.2 call.
     */
    public InputData convertToInputData(Map inputParam)
    {
        log.debug("SrmMv: Converter :Call received :Creation of MvInputData = " + inputParam.size());
        log.debug("SrmMv: Converter: Input Structure toString: " + inputParam.toString());

        InputData inputData = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        String member_authID = new String("authorizationID");
        String authID = (String) inputParam.get(member_authID);

        /* (2) fromSURL*/
        TSURL fromSURL = null;
        try {
            fromSURL = TSURL.decode(inputParam, TSURL.PNAME_FROMSURL);
        } catch (InvalidTSURLAttributesException e1) {
            log.debug("SrmMv: ErrorCreating surl: " + e1.toString());
        }

        /* (3) toSURL*/
        TSURL toSURL = null;
        try {
            toSURL = TSURL.decode(inputParam, TSURL.PNAME_TOSURL);
        } catch (InvalidTSURLAttributesException e1) {
            log.debug("SrmMv: ErrorCreating surl: " + e1.toString());
        }

        /* (4) TExtraInfoArray */
        ArrayOfTExtraInfo storageSystemInfo = null;
        try {
            storageSystemInfo = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        } catch (InvalidArrayOfTExtraInfoAttributeException e1) {
            log.debug("SrmMv: Error Creating ExtraInfo:" + e1.toString());
        }

        try {
            log.debug("SrmMv: Input data creation...");
            inputData = new MvInputData(guser, fromSURL, toSURL, storageSystemInfo);
            log.debug("Mv input data created.");

        } catch (InvalidMvInputAttributeException e) {
            log.debug("Invalid MvInputData Creation!" + e);
        }

        // Return Space Reservation Data Created
        return inputData;

    }

    public Map convertFromOutputData(OutputData data)
    {
        log.debug("SrmMv: Converter :Call received :Creation of XMLRPC Output Structure! ");
        // Output structure to return to xmlrpc client
        Map outputParam = new HashMap();
        MvOutputData outputData = (MvOutputData) data;
        TReturnStatus status = outputData.getStatus();
        status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        //Return Output Structure
        return outputParam;
    }
}
