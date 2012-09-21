/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.xmlrpc.converter.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidArrayOfTExtraInfoAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmInputData;
import it.grid.storm.synchcall.data.directory.RmOutputData;
import it.grid.storm.synchcall.data.exception.InvalidRmInputAttributeException;
import it.grid.storm.xmlrpc.converter.Converter;
import it.grid.storm.xmlrpc.converter.ParameterDisplayHelper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project.
 *
 * This class represents the Type Converter for Rm function .
 * This class have get an input data from xmlrpc call anc convert it into a
 * StoRM Type that can be used to invoke the RmManager
 *
 * Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */

public class RmConverter implements Converter {
    /**
     * Logger
     */
    private static final Logger log = LoggerFactory.getLogger(RmConverter.class);

    public RmConverter() {
    };

    /**
     * This method return a RmInputData created from input Hashtable structure
     * of an xmlrpc Rm v2.1 call. Rm Input Data can be used to invoke mkdir
     * method of DirectoryFunctionsManager
     */
    public InputData convertToInputData(Map inputParam) {
        log.debug("RmConverter :Call received :Creation of RmdirInputData = "
                + inputParam.size());
        log.debug("RmConverter: Input Structure toString: "
                + ParameterDisplayHelper.display(inputParam));

        RmInputData inputData = null;

        GridUserInterface guser = GridUserManager.decode(inputParam);

        ArrayOfSURLs surlArray = null;
        try {
            surlArray = ArrayOfSURLs.decode(inputParam,
                    ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            inputData = new RmInputData(guser, surlArray);

        } catch (InvalidRmInputAttributeException e) {
            log.debug("Invalid RmInputData Creation!" + e);
        }

        log.debug("RmInputData Created!");
        return inputData;
    }

    public Map convertFromOutputData(OutputData outputData) {

        log
        .debug("RmConverter :Call received :Creation of XMLRPC Output Structure! ");
        // Output structure to return to xmlrpc client
        Map outputParam = new HashMap();
        RmOutputData rmOutputData = (RmOutputData) outputData;
        TReturnStatus status = rmOutputData.getStatus();
        if (status != null) {
            status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }

        ArrayOfTSURLReturnStatus surlArray = rmOutputData.getSurlStatus();
        if (surlArray != null) {
            surlArray.encode(outputParam,
                    ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);
        }

        // Return global structure.
        return outputParam;
    }
}
