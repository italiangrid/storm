/**
 * This class represents the Type Converter for Rm function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the RmManager
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

import it.grid.storm.griduser.Fqan;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;

public class RmConverter
{
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public RmConverter()
    {
    };

    /** This method return a RmInputData
     * created from input Hashtable structure of an xmlrpc Rm v2.1 call.
     *  Rm Input Data can be used to invoke mkdir method of DirectoryFunctionsManager
     */
    public RmInputData getRmInputData(Map inputParam)
    {
        log.debug("RmConverter :Call received :Creation of RmdirInputData = " + inputParam.size());
        log.debug("RmConverter: Input Structure toString: " + inputParam.toString());

        /* Creationd of RmInputData, INPUT STRUCTURE for */
        /* DirectoryFuncionsManager !			  */
        RmInputData inputData = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = VomsGridUser.decode(inputParam);

        //Inutile
        String member_authID = new String("authorizationID");
        String authID = (String) inputParam.get(member_authID);

        //TSURL ARRAY
        ArrayOfSURLs surlArray = null;
        try {
            surlArray = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //Creation of surlInfoArray
        ArrayOfTExtraInfo infoArray = null;
        try {
            infoArray = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        } catch (InvalidArrayOfTExtraInfoAttributeException e1) {
            // TODO Auto-generated catch block
            log.debug("RmConverter: storageSystemInfo not specified.");
            //e1.printStackTrace();
        }

        //Creation of RmInputData structure
        try {
            inputData = new RmInputData(guser, surlArray, infoArray);

        } catch (InvalidRmInputAttributeException e) {
            log.debug("Invalid RmInputData Creation!" + e);
        }

        log.debug("RmInputData Created!");
        return inputData;
    }

    public Map getOutputParameter(RmOutputData outputData)
    {

        log.debug("RmConverter :Call received :Creation of XMLRPC Output Structure! ");
        //Output structure to return to xmlrpc client
        Map outputParam = new HashMap();

        TReturnStatus status = outputData.getStatus();
        if (status != null) status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        ArrayOfTSURLReturnStatus surlArray = outputData.getSurlStatus();
        if (surlArray != null) surlArray.encode(outputParam, ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);

        //Return global structure.
        return outputParam;
    }
}
