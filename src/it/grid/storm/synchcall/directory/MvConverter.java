/**
 * This class represents the Type Converter for SrmMv function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the MvExecutor.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.directory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.grid.storm.common.SRMConstants;
import it.grid.storm.griduser.Fqan;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;
import it.grid.storm.griduser.GridUserManager;

public class MvConverter
{
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public MvConverter()
    {
    };

    /**
     * This method return a MvInputData created from input Map
     * structure of an xmlrpc SrmMv v2.2 call.
    */
    public MvInputData getMvInputData(Map inputParam)
    {
        log.debug("SrmMv: Converter :Call received :Creation of MvInputData = " + inputParam.size());
        log.debug("SrmMv: Converter: Input Structure toString: " + inputParam.toString());

        MvInputData inputData = null;

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

    public Map getOutputParameter(MvOutputData outputData)
    {
        log.debug("SrmMv: Converter :Call received :Creation of XMLRPC Output Structure! ");
        // Output structure to return to xmlrpc client
        Map outputParam = new HashMap();
        TReturnStatus status = outputData.getStatus();
        status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        //Return Output Structure
        return outputParam;
    }
}
