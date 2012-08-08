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

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;

/**
 * @author Michele Dibenedetto
 *
 */
public class PrepareToPutRequestConverter extends FileTransferRequestInputConverter 
{
    static final String OVERWRITE_MODE_PARAMETER_NAME = "overwriteMode";
    static final Logger log = LoggerFactory.getLogger(PrepareToPutRequestConverter.class);

    @Override
    public InputData convertToInputData(Map inputParam) throws IllegalArgumentException, StoRMXmlRpcException
    {
        FileTransferInputData ftInputData = (FileTransferInputData) super.convertToInputData(inputParam);
        PrepareToPutInputData inputData;
        try
        {
            inputData = new PrepareToPutInputData(ftInputData);
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
        TLifeTimeInSeconds desiredFileLifetime = TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_FILELIFETIME);
        if (desiredFileLifetime != null)
        {
            if(!desiredFileLifetime.isEmpty())
            {
                inputData.setDesiredFileLifetime(desiredFileLifetime);                
            }
            else
            {
                log.warn("Unable to use the received \'" + TLifeTimeInSeconds.PNAME_FILELIFETIME + "\', interpreted as an empty value");
            }
        }
        log.debug("PrepareToPutInputData Created!");
        return inputData;
    }
}
