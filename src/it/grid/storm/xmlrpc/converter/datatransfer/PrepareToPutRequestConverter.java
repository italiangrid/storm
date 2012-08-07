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

package it.grid.storm.xmlrpc.converter.datatransfer;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutOutputData;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * @author Michele Dibenedetto
 *
 */
public class PrepareToPutRequestConverter implements Converter
{
    private static final String USER_DN_PARAMETER_NAME = "userDN";
    private static final String OVERWRITE_MODE_PARAMETER_NAME = "overwriteMode";
    private static final Logger log = LoggerFactory.getLogger(PrepareToPutRequestConverter.class);
    
    @Override
    public InputData convertToInputData(Map inputParam) throws IllegalArgumentException, StoRMXmlRpcException
    {
        TSURL surl = null;
        try
        {
            surl = TSURL.decode(inputParam, TSURL.PNAME_SURL);
        } catch(InvalidTSURLAttributesException e)
        {
            log.error("Unable to decode \'" + TSURL.PNAME_SURL
                    + "\' parameter as TSURL. InvalidTSURLAttributesException: " + e.getMessage());
            throw new IllegalArgumentException("Unable to decode \'" + TSURL.PNAME_SURL
                    + "\' parameter as TSURL");
        }
        if (surl == null)
        {
            log.error("Missing mandatory parameter \'" + TSURL.PNAME_SURL
                    + "\' Unable to build PrepareToPutInputData");
            throw new IllegalArgumentException("Missing mandatory parameter \'" + TSURL.PNAME_SURL + "\'");
        }
        GridUserInterface user = GridUserManager.decode(inputParam);
        if (user == null)
        {
            log.error("Missing mandatory parameter \'" + USER_DN_PARAMETER_NAME
                    + "\' Unable to build PrepareToPutInputData");
            throw new IllegalArgumentException("Missing mandatory parameter \'" + USER_DN_PARAMETER_NAME
                    + "\'");
        }
        TURLPrefix transferProtocols = TURLPrefix.decode(inputParam, TURLPrefix.PNAME_TURL_PREFIX);
        if (transferProtocols == null)
        {
            log.error("Missing mandatory parameter \'" + TURLPrefix.PNAME_TURL_PREFIX
                    + "\' Unable to build PrepareToPutInputData");
            throw new IllegalArgumentException("Missing mandatory parameter \'" + TURLPrefix.PNAME_TURL_PREFIX
                    + "\'");
        }
        PrepareToPutInputData inputData;
        try
        {
            inputData = new PrepareToPutInputData(user, surl, transferProtocols);
        } catch(IllegalArgumentException e)
        {
            log.error("Unable to build PrepareToPutInputData. IllegalArgumentException: " + e.getMessage());
            throw new StoRMXmlRpcException("Unable to build PrepareToPutInputData");
        }
        TSizeInBytes fileSize = TSizeInBytes.decode(inputParam, TSizeInBytes.PNAME_SIZE);
        if (fileSize != null)
        {
            if(!fileSize.isEmpty())
            {
                inputData.setFileSize(fileSize);                
            }
            else
            {
                log.warn("Unable to use the received \'" + TSizeInBytes.PNAME_SIZE + "\', interpreted as an empty value");
            }
        }

        String overwriteModeString = (String) inputParam.get(OVERWRITE_MODE_PARAMETER_NAME);
        if (overwriteModeString != null)
        {
            TOverwriteMode overwriteMode;
            try
            {
                overwriteMode = TOverwriteMode.getTOverwriteMode(overwriteModeString);
            } catch(IllegalArgumentException e)
            {
                log.error("Unable to build TOverwriteMode from \'" + overwriteModeString + "\'. IllegalArgumentException: " + e.getMessage());
                throw new StoRMXmlRpcException("Unable to build PrepareToPutInputData");
            }
            if(!overwriteMode.equals(TOverwriteMode.EMPTY))
            {
                inputData.setOverwriteMode(overwriteMode);                
            }
            else
            {
                log.warn("Unable to use the received \'" + OVERWRITE_MODE_PARAMETER_NAME + "\', interpreted as an empty value");
            }
        }
        log.debug("PrepareToPutInputData Created!");
        return inputData;
    }

    @Override
    public Map convertFromOutputData(OutputData outputData) throws IllegalArgumentException
    {
        log.debug("Creation of XMLRPC Output Structure");
        Hashtable outputParam = new Hashtable();
        if(!(outputData instanceof PrepareToPutOutputData))
        {
            log.error("Unable to convert from OutputData. Wrong OutputData type: \'" + outputData.getClass().getName() + "\'");
            throw new IllegalArgumentException("Unable to convert from OutputData. Wrong OutputData type: \'" + outputData.getClass().getName() + "\'");
        }
        PrepareToPutOutputData ptpOutputData = (PrepareToPutOutputData) outputData;
        TSURL surl = ptpOutputData.getSurl();
        TTURL turl = ptpOutputData.getTurl();
        TReturnStatus status = ptpOutputData.getStatus();
        if(surl == null || turl == null || status == null)
        {
            log.error("Unable to build a valid output map. Missing mandatory values from PrepareToPutOutputData: " + ptpOutputData.toString());
            throw new IllegalArgumentException("Unable to build a valid output map from PrepareToPutOutputData");
        }
        surl.encode(outputParam, TSURL.PNAME_SURL);
        turl.encode(outputParam, TTURL.PNAME_TURL);
        status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        log.debug("Built output Map: " + outputParam.toString());
        return outputParam;
    }

}
