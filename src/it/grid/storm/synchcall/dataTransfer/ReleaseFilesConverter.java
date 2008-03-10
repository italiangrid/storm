package it.grid.storm.synchcall.dataTransfer;

import org.apache.log4j.Logger;

import java.util.Hashtable;
import java.util.Map;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;

public class ReleaseFilesConverter
{
    private static final Logger log = Logger.getLogger("dataTransfer");
    
    /**
     * This method returns a ReleaseFilesInputData created from the input Hashtable structure
     * of an xmlrpc ReleaseFiles v2.2 call.
     * @param inputParam Hashtable containing the input data
     * @return ReleaseFilesInputData
     */
    public ReleaseFilesInputData getReleaseFilesInputData(Map inputParam)
    {
        ReleaseFilesInputData inputData = null;
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
            log.debug("requestToken=NULL");
        }
        
        /* (3) anyURI[] arrayOfSURLs */
        ArrayOfSURLs arrayOfSURLs;
        try {
            arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e) {
            log.debug("Empty surlArray!");
            arrayOfSURLs = null;
        }
        
        /* (4) boolean doRemove */
        String member_doRemove = new String("doRemove");
        Boolean doRemove = (Boolean) inputParam.get(member_doRemove);
        
        try {
            inputData = new ReleaseFilesInputData(guser, requestToken, arrayOfSURLs, doRemove);
        } catch (InvalidReleaseFilesInputAttributeException e) {
            log.debug("Invalid ReleaseFilesInputData Creation!" + e);
        }

        log.debug("ReleaseFilesInputData Created!");

        return inputData;
    }

    public Map getOutputParameter(ReleaseFilesOutputData outputData)
    {
        log.debug("Started ReleaseFilesConverter - Creation of XMLRPC Output Structure!");
        
        Hashtable outputParam = new Hashtable();
        
        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }
        
        /* (2) arrayOfFileStatuses */
        ArrayOfTSURLReturnStatus arrayOfFileStatuses = outputData.getArrayOfFileStatuses();
        if (arrayOfFileStatuses != null) {
            arrayOfFileStatuses.encode(outputParam, ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);
        }
        
        log.debug("ReleaseFilesConverter - Sending: " + outputParam.toString());
        
        return outputParam;
    }
    
}
