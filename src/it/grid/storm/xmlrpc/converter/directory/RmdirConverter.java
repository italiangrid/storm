package it.grid.storm.xmlrpc.converter.directory;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.srm.types.*;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.InvalidRmdirInputAttributeException;
import it.grid.storm.synchcall.data.directory.RmdirInputData;
import it.grid.storm.synchcall.data.directory.RmdirOutputData;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.griduser.GridUserManager;

/**
 * This class is part of the StoRM project.
 *
 * This class represents the Type Converter for Rmdir function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the RmdirManager
 *
 * Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class RmdirConverter implements Converter {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger("synch_xmlrpc_server");

    public RmdirConverter()
    {
    };

    /**
     * This method return a RmdirInputData created from input Hashtable
     * structure of an xmlrpc Rmdir v2.1 call. Rmdir Input Data can be used to
     * invoke mkdir method of DirectoryFunctionsManager
     */
    public InputData convertToInputData(Map inputParam)
    {
        log.debug("srmRmdir: Converter :Call received :Creation of RmdirInputData = " + inputParam.size());
        log.debug("srmRmdir: Converter: Input Structure toString: " + inputParam.toString());

        RmdirInputData inputData = null;

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
            log.debug("srmRm: ErrorCreating surl: " + e1.toString());
        }

        /* (3) TExtraInfoArray */
        ArrayOfTExtraInfo storageSystemInfo = null;
        try {
            storageSystemInfo = ArrayOfTExtraInfo.decode(inputParam, ArrayOfTExtraInfo.PNAME_STORAGESYSTEMINFO);
        } catch (InvalidArrayOfTExtraInfoAttributeException e1) {
            log.debug("srmRm: Error Creating ExtraInfo:" + e1.toString());
        }

        /* (4) recursive */
        String member_recursive = new String("recursive");
        Boolean recursive = (Boolean) inputParam.get(member_recursive);

        try {
            log.debug("srmRm: Rmdir input data creation...");
            inputData = new RmdirInputData(guser, surl, storageSystemInfo, recursive);
            log.debug("srmRm: Rmdir input data created.");

        } catch (InvalidRmdirInputAttributeException e) {
            log.debug("Invalid RmdirInputData Creation!" + e);
        }

        // Return Space Reservation Data Created
        return inputData;

    }

    /* (non-Javadoc)
     * @see it.grid.storm.xmlrpc.converter.Converter#convertFromOutputData(it.grid.storm.synchcall.data.OutputData)
     */
    public Map convertFromOutputData(OutputData outputData)
    {
        log.debug("srmRm: RmdirConverter :Call received :Creation of XMLRPC Output Structure! ");

        // Output structure to return to xmlrpc client
        Map outputParam = new HashMap();
        RmdirOutputData rmdirOutputData = (RmdirOutputData) outputData;
        TReturnStatus outputStatus =  rmdirOutputData.getStatus();
        outputStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        //Return Output Structure
        return outputParam;
    }
}
