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

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidArrayOfSURLsAttributeException;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneInputData;
import it.grid.storm.synchcall.data.datatransfer.PutDoneOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PutDoneConverter implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(PutDoneConverter.class);

    public PutDoneConverter() {}

    /**
     * This method returns a PutDoneInputData created from the input Hashtable structure
     * of an xmlrpc PutDone v2.2 call.
     * @param inputParam Hashtable containing the input data
     * @return PutDoneInputData
     */
    public InputData convertToInputData(Map inputParam)
    {
        /* Creation of VomsGridUser */
        GridUserInterface guser = null;
        guser = GridUserManager.decode(inputParam);
        //guser = VomsGridUser.decode(inputParam);

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

        PutDoneInputData inputData = null;
        try {
            inputData = new PutDoneInputData(guser, requestToken, arrayOfSURLs);
        } catch (IllegalArgumentException e) {
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
