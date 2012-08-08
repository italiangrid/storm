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


import java.util.Hashtable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferOutputData;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;
import it.grid.storm.xmlrpc.converter.Converter;

/**
 * @author Michele Dibenedetto
 *
 */
public abstract class FileTransferRequestInputConverter implements Converter
{
    static final Logger log = LoggerFactory.getLogger(FileTransferRequestInputConverter.class);
    
    private static final String USER_DN_PARAMETER_NAME = "userDN";


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
                    + "\' Unable to build FileTransferInputData");
            throw new IllegalArgumentException("Missing mandatory parameter \'" + TSURL.PNAME_SURL + "\'");
        }
        GridUserInterface user = GridUserManager.decode(inputParam);
        if (user == null)
        {
            log.error("Missing mandatory parameter \'" + USER_DN_PARAMETER_NAME
                    + "\' Unable to build FileTransferInputData");
            throw new IllegalArgumentException("Missing mandatory parameter \'" + USER_DN_PARAMETER_NAME
                    + "\'");
        }
        TURLPrefix transferProtocols = TURLPrefix.decode(inputParam, TURLPrefix.PNAME_TURL_PREFIX);
        if (transferProtocols == null)
        {
            log.error("Missing mandatory parameter \'" + TURLPrefix.PNAME_TURL_PREFIX
                    + "\' Unable to build FileTransferInputData");
            throw new IllegalArgumentException("Missing mandatory parameter \'" + TURLPrefix.PNAME_TURL_PREFIX
                    + "\'");
        }
        FileTransferInputData inputData;
        try
        {
            inputData = new FileTransferInputData(user, surl, transferProtocols);
        } catch(IllegalArgumentException e)
        {
            log.error("Unable to build PrepareToPutInputData. IllegalArgumentException: " + e.getMessage());
            throw new StoRMXmlRpcException("Unable to build PrepareToPutInputData");
        }
        TLifeTimeInSeconds desiredPinLifetime = TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_PINLIFETIME);
        if (desiredPinLifetime != null)
        {
            if(!desiredPinLifetime.isEmpty())
            {
                inputData.setDesiredPinLifetime(desiredPinLifetime);                
            }
            else
            {
                log.warn("Unable to use the received \'" + TLifeTimeInSeconds.PNAME_PINLIFETIME + "\', interpreted as an empty value");
            }
        }
        TSpaceToken targetSpaceToken = TSpaceToken.decode(inputParam, TSpaceToken.PNAME_SPACETOKEN);
        if (desiredPinLifetime != null)
        {
            if(!desiredPinLifetime.isEmpty())
            {
                inputData.setTargetSpaceToken(targetSpaceToken);                
            }
            else
            {
                log.warn("Unable to use the received \'" + TLifeTimeInSeconds.PNAME_PINLIFETIME + "\', interpreted as an empty value");
            }
        }
        log.debug("FileTransferInputData Created!");
        return inputData;
    }

    @Override
    public Map convertFromOutputData(OutputData outputData) throws IllegalArgumentException
    {
        log.debug("Creation of XMLRPC Output Structure");
        if(!(outputData instanceof FileTransferOutputData))
        {
            log.error("Unable to convert from OutputData. Wrong OutputData type: \'" + outputData.getClass().getName() + "\'");
            throw new IllegalArgumentException("Unable to convert from OutputData. Wrong OutputData type: \'" + outputData.getClass().getName() + "\'");
        }
        FileTransferOutputData ftOutputData = (FileTransferOutputData) outputData;
        TSURL surl = ftOutputData.getSurl();
        TTURL turl = ftOutputData.getTurl();
        TReturnStatus status = ftOutputData.getStatus();
        if(surl == null || turl == null || status == null)
        {
            log.error("Unable to build a valid output map. Missing mandatory values from FileTransferOutputData: " + ftOutputData.toString());
            throw new IllegalArgumentException("Unable to build a valid output map from FileTransferOutputData");
        }
        Hashtable outputParam = new Hashtable();
        surl.encode(outputParam, TSURL.PNAME_SURL);
        turl.encode(outputParam, TTURL.PNAME_TURL);
        status.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        log.debug("Built output Map: " + outputParam.toString());
        return outputParam;
    }

}
