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
import it.grid.storm.synchcall.data.directory.InvalidMkdirInputAttributeException;
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;
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
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class MkdirConverter implements Converter
{
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(MkdirConverter.class);

    public MkdirConverter()
    {
    };

    /**
     * This method return a MkdirInputData created from input Hashtable
     * structure of an xmlrpc Mkdir v2.1 call. Mkdir Input Data can be used to
     * invoke mkdir method of DirectoryFunctionsManager
     */
    public InputData convertToInputData(Map inputParam)
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

    public Map convertFromOutputData(OutputData outputData)
    {
        log.debug("SrmMkdir: Converter :Call received :Creation of XMLRPC Output Structure! ");

        Map outputParam = new HashMap();

        MkdirOutputData odata = (MkdirOutputData) outputData;
        TReturnStatus outputStatus = odata.getStatus();

        outputStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);

        // Return Output Structure
        return outputParam;

    }
}
