package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
//import it.grid.storm.srm.types.*;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 * This class represents the Type Converter for ExtendFileLifeTime function.
 * This class receives input datas from xmlrpc call and converts these datas
 * into a StoRM Type that can be used to invoke the ExtendFileLifeTimeManager.
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *     @author Alberto Forti
 *
 * @date = Oct 10, 2008
 *
 */

public class ExtendFileLifeTimeConverter implements Converter
{
    private static final Logger log = Logger.getLogger("dataTransfer");

    public ExtendFileLifeTimeConverter() {}

    /**
     * This method returns a ExtendFileLifeTimeInputData created from the input Hashtable structure
     * of a xmlrpc srmExtendFileLifeTime() v2.2 call.
     * @param inputParam Hashtable containing the input data
     * @return ExtendFileLifeTimeInputData
     */
    public InputData convertToInputData(Map inputParam)
    {
        ExtendFileLifeTimeInputData inputData = null;
        String memberName;

        // Creation of VomsGridUser
        GridUserInterface guser = null;
        guser = VomsGridUser.decode(inputParam);

        // (1) authorizationID (never used)
        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        // (2) TRequestToken requestToken
        TRequestToken requestToken;
        try {
            requestToken = TRequestToken.decode(inputParam, TRequestToken.PNAME_REQUESTOKEN);
            log.debug("requestToken=" + requestToken.toString());
        } catch (InvalidTRequestTokenAttributesException e) {
            requestToken = null;
            log.error("requestToken=NULL");
        }

        // (3) anyURI[] arrayOfSURLs
        ArrayOfSURLs arrayOfSURLs;
        try {
            arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e) {
            log.error("Empty surlArray!");
            arrayOfSURLs = null;
        }

        // (4) int newFileLifetime
        TLifeTimeInSeconds newFileLifetime = TLifeTimeInSeconds.decode(inputParam,
                TLifeTimeInSeconds.PNAME_FILELIFETIME);

        // (5) int newPinLifetime
        TLifeTimeInSeconds newPinLifetime = TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_PINLIFETIME);

        // Creation of ExtendFileLifeTimeInputData structure
        inputData = new ExtendFileLifeTimeInputData(guser, requestToken, arrayOfSURLs, newFileLifetime, newPinLifetime);

        log.debug("ExtendFileLifeTimeInputData Created!");

        return inputData;
    }

    public Hashtable convertFromOutputData(OutputData data)
    {
        log.debug("ExtendFileLifeTimeOutputData - Creation of XMLRPC Output Structure!");

        Hashtable outputParam = new Hashtable();
        ExtendFileLifeTimeOutputData outputData = (ExtendFileLifeTimeOutputData) data;

        // (1) returnStatus
        TReturnStatus returnStatus = outputData.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }

        // (2) arrayOfFileStatuses
        ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses = outputData.getArrayOfFileStatuses();
        if (arrayOfFileStatuses != null)
            arrayOfFileStatuses.encode(outputParam, ArrayOfTSURLLifetimeReturnStatus.PNAME_ARRAYOFFILESTATUSES);

        log.debug("ExtendFileLifeTimeConverter - Sending: " + outputParam.toString());

        // Return global structure.
        return outputParam;
    }
}
