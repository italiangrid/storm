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

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortRequestOutputData;
import it.grid.storm.synchcall.data.exception.InvalidAbortRequestInputDataAttributeException;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbortRequestConverter implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(AbortRequestConverter.class);

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
        AbortRequestOutputData outputData =  AbortRequestOutputData.make((AbortGeneralOutputData)data);

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
