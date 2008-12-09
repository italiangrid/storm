/**
 * This class represents the Type Converter for Mkdir function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the MkdirManager
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

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;
import it.grid.storm.griduser.GridUserManager;

public class MkdirConverter
{
    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public MkdirConverter()
    {
    };

    /**
     * This method return a MkdirInputData created from input Hashtable
     * structure of an xmlrpc Mkdir v2.1 call. Mkdir Input Data can be used to
     * invoke mkdir method of DirectoryFunctionsManager
     */
    public MkdirInputData getMkdirInputData(Map inputParam)
    {

        log.debug("SrmMkdir: Converter :Call received :Creation of MkdirInputData = " + inputParam.size());
        log.debug("SrmMkdir: Converter: Input Structure toString: " + inputParam.toString());

        MkdirInputData inputData = null;

        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

        /* (1) authorizationID (never used) */
        String member_authID = new String("authorizationID");
        String authID = (String) inputParam.get(member_authID);

        /* (2) directoryPath */
        TSURL surl = null;
        try {
            surl = TSURL.decode(inputParam, TSURL.PNAME_SURL);
        } catch (InvalidTSURLAttributesException e1) {
            log.debug("SrmMkdir: ErrorCreating surl: " + e1.toString());
        }

        /* TExtraInfoArray */
        ArrayOfTExtraInfo extraInfoArray = null;
        try {
            extraInfoArray = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        } catch (InvalidArrayOfTExtraInfoAttributeException e1) {
            log.debug("SrmMkdir: Error Creating ExtraInfo:" + e1.toString());
        }

        // Creation of MkdirInputData
        try {
            inputData = new MkdirInputData(guser, surl, extraInfoArray);
        } catch (InvalidMkdirInputAttributeException e) {
            log.debug("SrmMkdir: Invalid Mkdir data creation!" + e);
        }

        return inputData;
    }

    public Map getOutputParameter(TReturnStatus outputStatus)
    {
        log.debug("SrmMkdir: Converter :Call received :Creation of XMLRPC Output Structure! ");

        Map outputParam = new HashMap();

        outputStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        // Return Output Structure
        return outputParam;

    }
}
