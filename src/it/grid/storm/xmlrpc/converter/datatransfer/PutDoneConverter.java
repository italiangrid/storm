/**
 * This class represents the Type Converter for PutDone function.
 * This class receives input datas from xmlrpc call and converts these datas
 * into a StoRM Type that can be used to invoke the PutDoneManager.
 *
 * @author  Alberto Forti
 * @author  CNAF -INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */
package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneOutputData;
import it.grid.storm.synchcall.data.exception.InvalidPutDoneInputAttributeException;



import it.grid.storm.xmlrpc.converter.Converter;

public class PutDoneConverter implements Converter
{
    private static final Logger log = Logger.getLogger("dataTransfer");

    public PutDoneConverter() {}

    /**
     * This method returns a PutDoneInputData created from the input Hashtable structure
     * of an xmlrpc PutDone v2.2 call.
     * @param inputParam Hashtable containing the input data
     * @return PutDoneInputData
     */
    public InputData convertToInputData(Map inputParam)
    {
        PutDoneInputData inputData = null;
        String memberName;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        memberName = new String("authorizationID");
        String authID = (String) inputParam.get(memberName);

        /* (2) TRequestToken requestToken */
        TRequestToken requestToken;
        try {
            requestToken = TRequestToken.decode(inputParam, TRequestToken.PNAME_REQUESTOKEN);
            log.debug("requestToken=" + requestToken.toString());
        } catch (InvalidTRequestTokenAttributesException e) {
            requestToken = null;
            log.debug("requestToken=NULL" + e);
        }

        /* (3) anyURI[] arrayOfSURLs */
        ArrayOfSURLs arrayOfSURLs;
        try {
            arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e) {
            log.debug("Empty surlArray!");
            arrayOfSURLs = null;
        }

        // Creation of PutDoneInputData structure
        try {
            inputData = new PutDoneInputData(guser, requestToken, arrayOfSURLs);
        } catch (InvalidPutDoneInputAttributeException e) {
            log.debug("Invalid PutDoneInputData Creation!" + e);
        }

        log.debug("PutDoneInputData Created!");

        return inputData;
    }

    public Map convertFromOutputData(OutputData absData)
    {
        log.debug("PutDoneConverter - Creation of XMLRPC Output Structure!");
           
        Map outputParam = new HashMap();
        PutDoneOutputData outputData = (PutDoneOutputData) absData; 
        
        
        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }
        
        /* (2) arrayOfFileStatuses */
        ArrayOfTSURLReturnStatus arrayOfFileStatuses = outputData. getArrayOfFileStatuses();
        if (arrayOfFileStatuses != null) {
            arrayOfFileStatuses.encode(outputParam, ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);
        }
        
        log.debug("PutDoneConverter - Sending: " + outputParam.toString());

        // Return global structure.
        return outputParam;
    }
}
