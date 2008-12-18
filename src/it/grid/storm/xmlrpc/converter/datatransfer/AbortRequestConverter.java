/**
 * This class represents the Type Converter for AbortRequest function.
 * This class receives input datas from xmlrpc call and converts these datas
 * into a StoRM Type that can be used to invoke the AbortManager.
 *
 * @author  Magnoni Luca
 * @author  CNAF-INFN Bologna
 * @date    Jan 2007
 * @version 1.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.HashMap;
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
import it.grid.storm.synchcall.data.datatransfer.AbortRequestInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestOutputData;
import it.grid.storm.synchcall.data.exception.InvalidAbortRequestInputDataAttributeException;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.griduser.GridUserManager;

public class AbortRequestConverter implements Converter
{
    private static final Logger log = Logger.getLogger("dataTransfer");

    public AbortRequestConverter() {}

    /**
     * This method returns a AbortRequest data created from the input Hashtable structure
     * of a xmlrpc srmAbortRequest() v2.2 call.
     * @param inputParam Hashtable containing the input data
     * @return AbortRequestInputData
     */
    public InputData convertToInputData(Map inputParam)
    {
        AbortRequestInputData inputData = null;
        String memberName;


        // Creation of VomsGridUser
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

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
            log.debug("requestToken=NULL");
        }

        log.debug("AbortRequestInputData Created!");
        try {
            inputData = new AbortRequestInputData(guser, requestToken);
        } catch (InvalidAbortRequestInputDataAttributeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return inputData;
    }

    public Map convertFromOutputData(OutputData data)
    {
        log.debug("AbortRequestOutputData - Creation of XMLRPC Output Structure!");

        Map outputParam = new HashMap();
        AbortRequestOutputData outputData =  (AbortRequestOutputData) data;

        // (1) returnStatus
        TReturnStatus returnStatus = outputData.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }
        log.debug("AbortRequestConverter - Sending: " + outputParam.toString());

        // Return global structure.
        return outputParam;
    }
}
