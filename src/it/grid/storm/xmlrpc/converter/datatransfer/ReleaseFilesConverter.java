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
import it.grid.storm.synchcall.data.datatransfer.AnonymousReleaseFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityReleaseFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ReleaseFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ReleaseFilesOutputData;
import it.grid.storm.xmlrpc.converter.Converter;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseFilesConverter implements Converter
{
    private static final Logger log = LoggerFactory.getLogger(ReleaseFilesConverter.class);

    /**
     * This method returns a ReleaseFilesInputData created from the input Hashtable structure
     * of an xmlrpc ReleaseFiles v2.2 call.
     * @param inputParam Hashtable containing the input data
     * @return ReleaseFilesInputData
     */
    public InputData convertToInputData(Map inputParam)
    {

        GridUserInterface guser = GridUserManager.decode(inputParam);

        /* (2) TRequestToken requestToken */
        TRequestToken requestToken;
        try {
            requestToken = TRequestToken.decode(inputParam, TRequestToken.PNAME_REQUESTOKEN);
            log.debug("requestToken=" + requestToken.toString());
        } catch (InvalidTRequestTokenAttributesException e) {
            requestToken = null;
            log.debug("requestToken=NULL");
        }

        /* (3) anyURI[] arrayOfSURLs */
        ArrayOfSURLs arrayOfSURLs;
        try {
            arrayOfSURLs = ArrayOfSURLs.decode(inputParam, ArrayOfSURLs.ARRAYOFSURLS);
        } catch (InvalidArrayOfSURLsAttributeException e) {
            log.debug("Empty surlArray!");
            arrayOfSURLs = null;
        }

        ReleaseFilesInputData inputData;
        if(guser != null)
        {
            inputData = new IdentityReleaseFilesInputData(guser, requestToken, arrayOfSURLs);
        }
        else
        {
            inputData = new AnonymousReleaseFilesInputData(requestToken, arrayOfSURLs);
        }
        return inputData;
    }

    public Map convertFromOutputData(OutputData data)
    {
        log.debug("Started ReleaseFilesConverter - Creation of XMLRPC Output Structure!");

        Hashtable outputParam = new Hashtable();
        ReleaseFilesOutputData outputData = (ReleaseFilesOutputData) data;
        /* (1) returnStatus */
        TReturnStatus returnStatus = outputData.getReturnStatus();
        if (returnStatus != null) {
            returnStatus.encode(outputParam, TReturnStatus.PNAME_RETURNSTATUS);
        }

        /* (2) arrayOfFileStatuses */
        ArrayOfTSURLReturnStatus arrayOfFileStatuses = outputData.getArrayOfFileStatuses();
        if (arrayOfFileStatuses != null) {
            arrayOfFileStatuses.encode(outputParam, ArrayOfTSURLReturnStatus.PNAME_ARRAYOFFILESTATUSES);
        }

        log.debug("ReleaseFilesConverter - Sending: " + outputParam.toString());

        return outputParam;
    }

}
