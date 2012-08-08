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
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToGetOutputData;

/**
 * @author Michele Dibenedetto
 *
 */
public class PrepareToGetRequestConverter extends FileTransferRequestInputConverter 
{

    private static final Logger log = LoggerFactory.getLogger(PrepareToGetRequestConverter.class);
    
//    @Override
//    public InputData convertToInputData(Map inputParam) throws StoRMXmlRpcException
//    {
//        FileTransferInputData ftInputData = (FileTransferInputData) super.convertToInputData(inputParam);
//        PrepareToGetInputData inputData;
//        try
//        {
//            inputData = new PrepareToGetInputData(ftInputData);
//        } catch(IllegalArgumentException e)
//        {
//            log.error("Unable to build PrepareToGetInputData. IllegalArgumentException: " + e.getMessage());
//            throw new StoRMXmlRpcException("Unable to build PrepareToGetInputData");
//        }
//        TLifeTimeInSeconds desiredPinLifetime = TLifeTimeInSeconds.decode(inputParam, TLifeTimeInSeconds.PNAME_PINLIFETIME);
//        if (desiredPinLifetime != null)
//        {
//            if(!desiredPinLifetime.isEmpty())
//            {
//                inputData.setDesiredPinLifetime(desiredPinLifetime);                
//            }
//            else
//            {
//                log.warn("Unable to use the received \'" + TLifeTimeInSeconds.PNAME_PINLIFETIME + "\', interpreted as an empty value");
//            }
//        }
//        TSpaceToken targetSpaceToken = TSpaceToken.decode(inputParam, TSpaceToken.PNAME_SPACETOKEN);
//        if (desiredPinLifetime != null)
//        {
//            if(!desiredPinLifetime.isEmpty())
//            {
//                inputData.setTargetSpaceToken(targetSpaceToken);                
//            }
//            else
//            {
//                log.warn("Unable to use the received \'" + TLifeTimeInSeconds.PNAME_PINLIFETIME + "\', interpreted as an empty value");
//            }
//        }
//        log.debug("PrepareToGetInputData Created!");
//        return inputData;
//    }

    @Override
    public Map convertFromOutputData(OutputData outputData)
    {
        if(!(outputData instanceof PrepareToGetOutputData))
        {
            log.error("Unable to convert from OutputData. Wrong OutputData type: \'" + outputData.getClass().getName() + "\'");
            throw new IllegalArgumentException("Unable to convert from OutputData. Wrong OutputData type: \'" + outputData.getClass().getName() + "\'");
        }
        Map outputParam = super.convertFromOutputData(outputData);
        PrepareToGetOutputData ptgOutputData = (PrepareToGetOutputData) outputData;
        TSizeInBytes fileSize = ptgOutputData.getFileSize();
        TLifeTimeInSeconds remainingPinTime = ptgOutputData.getRemainingPinTime();
        fileSize.encode(outputParam, TSizeInBytes.PNAME_SIZE);
        remainingPinTime.encode(outputParam, TLifeTimeInSeconds.PNAME_PINLIFETIME);
        log.debug("Built output Map: " + outputParam.toString());
        return outputParam;
    }

}
