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

package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ReleaseFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ReleaseFilesOutputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * 
 * Authors:
 * 
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * @author Alberto Forti
 * 
 * @date = Oct 10, 2008
 * 
 */

public class ReleaseFilesCommand extends DataTransferCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(ReleaseFilesCommand.class);

    private static final String SRM_COMMAND = "srmReleaseFiles";

    public ReleaseFilesCommand() {}

    /**
     * Does a ReleaseFiles. Used to release pins on the previously requested "copies" (or "state") of the
     * SURL. This function normally follows a srmPrepareToGet or srmBringOnline functions.
     */
    public OutputData execute(InputData data) {

        
        ReleaseFilesOutputData outputData = new ReleaseFilesOutputData();
        ReleaseFilesInputData inputData = (ReleaseFilesInputData) data;

        log.debug("Started ReleaseFiles function");

        /******************** Check for malformed request: missing mandatory input parameters ****************/
        if (inputData == null
                || ((inputData.getArrayOfSURLs() == null || inputData.getArrayOfSURLs().size() == 0) && inputData.getRequestToken() == null))
        {
            log.error("ReleaseFiles: Invalid input parameters specified");
            outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                                 "Missing mandatory parameters (requestToken or arrayOfSURLs must be specified)"));
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(outputData.getReturnStatus(), inputData);
            return outputData;
        }

        TRequestToken requestToken = inputData.getRequestToken();
        Map<TSURL, TReturnStatus> surlStastuses;
        try
        {
            surlStastuses = getSurlsStatus(requestToken, inputData.getArrayOfSURLs());
        } catch(IllegalArgumentException e)
        {
            log.warn("Unexpected IllegalArgumentException in getSurlsStatus: " + e);
            outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, 
                                                                 "Internal error. Unablr to determine current SURL status"));
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(outputData.getReturnStatus(), inputData);
            return outputData;
        } catch(RequestUnknownException e)
        {
            log.info("No surls status available. RequestUnknownException: " + e.getMessage());
            outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token and surls"));
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(outputData.getReturnStatus(), inputData);
            return outputData;
        } catch(UnknownTokenException e)
        {
            log.info("No surls status available. UnknownTokenException: " + e.getMessage());
            outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token"));
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(outputData.getReturnStatus(), inputData);
            return outputData;
        } catch(ExpiredTokenException e)
        {
            log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
            outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired"));
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(outputData.getReturnStatus(), inputData);
            return outputData;
        }
        if (surlStastuses.isEmpty())
        {
            // Case 1: no candidate SURLs in the DB. SRM_INVALID_REQUEST or SRM_FAILURE are returned.
            log.info("No SURLs found in the DB. Request failed");
            TReturnStatus returnStatus;
            if (requestToken != null)
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            }
            else
            {
                if (inputData.getArrayOfSURLs() != null)
                {

                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
                                                    "None of the specified SURLs was found");
                }
                else
                {
                    //impossible
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "Internal error. Precondition check mismatch");
                }
            }
            outputData.setReturnStatus(returnStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(returnStatus, inputData);
            return outputData;
        }
        ArrayOfTSURLReturnStatus surlReturnStatuses;
        if (inputData.getArrayOfSURLs() == null || inputData.getArrayOfSURLs().size() == 0)
        {
            surlReturnStatuses = prepareSurlsReturnStatus(surlStastuses, requestToken);
        }
        else
        {
            surlReturnStatuses = prepareSurlsReturnStatus(surlStastuses, requestToken,
                                                          inputData.getArrayOfSURLs());
        }
        List<TSURL> surlToRelease = extractSurlToRelease(surlReturnStatuses);
        if (!surlToRelease.isEmpty())
        {
            try
            {
                expireSurls(surlToRelease, requestToken);
            } catch(IllegalArgumentException e)
            {
                log.warn("Unexpected IllegalArgumentException in expireSurls: " + e);
                outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Internal error. Unable to transit SURLs to SRM_RELEASED status"));
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(outputData.getReturnStatus(), inputData);
                return outputData;
            } catch(UnknownTokenException e)
            {
                log.warn("Unexpected RequestUnknownException in expireSurls: " + e);
                outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Internal error. Unable to transit SURLs to SRM_RELEASED status"));
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(outputData.getReturnStatus(), inputData);
                return outputData;
            } catch(ExpiredTokenException e)
            {
                log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
                outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired"));
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(outputData.getReturnStatus(), inputData);
                return outputData;
            } catch(UnknownSurlException e)
            {
                log.warn("Unexpected UnknownSurlException in expireSurls: " + e);
                outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Internal error. Unable to transit SURLs to SRM_RELEASED status"));
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(outputData.getReturnStatus(), inputData);
                return outputData;
            }
        }

        removePinneExtendedAttribute(surlToRelease);
        
        boolean atLeastOneSuccess = false;
        boolean atLeastOneFailure = false;
        for (TSURLReturnStatus returnStatus : surlReturnStatuses.getArray())
        {
            printSurlOutcome(returnStatus, inputData);
            if (returnStatus.getStatus().isSRM_SUCCESS())
            {
                atLeastOneSuccess = true;

            }
            else
            {
                atLeastOneFailure = true;
            }
        }
        TReturnStatus returnStatus;
        if (atLeastOneSuccess)
        {
            if (atLeastOneFailure)
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                         "Check files status for details");
            }
            else
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS, "Files released");
            }

        }
        else
        {
            returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "No files released");
        }
        printRequestOutcome(returnStatus, inputData);
        outputData.setReturnStatus(returnStatus);
        outputData.setArrayOfFileStatuses(surlReturnStatuses);
        log.debug("End of ReleaseFiles function");
        return outputData;
    }

    private Map<TSURL, TReturnStatus> getSurlsStatus(TRequestToken requestToken,
            ArrayOfSURLs arrayOfSURLs) throws RequestUnknownException, IllegalArgumentException, UnknownTokenException, ExpiredTokenException
    {
        if ((requestToken == null) && (arrayOfSURLs == null))
        {
            throw new IllegalArgumentException("unable to get the statuses, null arguments: requestToken="
                    + requestToken + " arrayOfSURLs=" + arrayOfSURLs);
        }
        Map<TSURL, TReturnStatus> surlsStatuses = null;
        if (requestToken != null)
        {
            surlsStatuses = SurlStatusManager.getSurlsStatus(requestToken, arrayOfSURLs.getArrayList());
            if (surlsStatuses.isEmpty())
            {
                log.info("No one of the requested surls found for the provided token");
                throw new RequestUnknownException("No one of the requested surls found for the provided token");
            }
        }
        else
        {
            surlsStatuses = new HashMap<TSURL, TReturnStatus>();
            for(TSURL surl : arrayOfSURLs.getArrayList())
            {
                try
                {
                    surlsStatuses.put(surl, SurlStatusManager.getSurlsStatus(surl));
                } catch(IllegalArgumentException e)
                {
                    throw new IllegalStateException("Unexpected IllegalArgumentException in getSurlsStatus: "
                            + e);
                } catch(UnknownSurlException e)
                {
                    log.info("Requested surl " + surl + " is unknown");
                }
            }
            if (surlsStatuses.isEmpty())
            {
                log.info("No one of the requested surls found for the provided token");
                throw new RequestUnknownException("No one of the requested surls found for the provided token");
            }
        }
        return surlsStatuses;
    }

    private ArrayOfTSURLReturnStatus prepareSurlsReturnStatus(Map<TSURL, TReturnStatus> surlStastuses,
            TRequestToken requestToken)
    {
        ArrayOfTSURLReturnStatus surlReturnStatuses = new ArrayOfTSURLReturnStatus(surlStastuses.size());
        for (Entry<TSURL, TReturnStatus> surlStatus : surlStastuses.entrySet())
        {

            TReturnStatus returnStatus;
            if (TStatusCode.SRM_FILE_PINNED.equals(surlStatus.getValue().getStatusCode()))
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS, "Released");
            }
            else
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE,
                                                   "Not released because it is not pinned");
            }
            surlReturnStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(surlStatus.getKey(), returnStatus));
        }
        return surlReturnStatuses;
    }
    
    private ArrayOfTSURLReturnStatus prepareSurlsReturnStatus(Map<TSURL, TReturnStatus> surlStastuses,
            TRequestToken requestToken, ArrayOfSURLs arrayOfUserSURLs)
    {
        ArrayOfTSURLReturnStatus surlReturnStatuses = new ArrayOfTSURLReturnStatus(surlStastuses.size());
        for(TSURL surl : arrayOfUserSURLs.getArrayList())
        {
            TReturnStatus returnStatus;
            TReturnStatus status = surlStastuses.get(surl);
            if(status != null)
            {
                if(TStatusCode.SRM_FILE_PINNED.equals(status.getStatusCode()))
                {
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS, "Released");
                }
                else
                {
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
                                                       "Not released because it is not pinned");
                }
            }
            else
            {
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
                                                   "Invalid SURL");
            }
            surlReturnStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(surl, returnStatus));
        }
        return surlReturnStatuses;
    }
    
    private List<TSURL> extractSurlToRelease(ArrayOfTSURLReturnStatus surlReturnStatuses)
    {
        LinkedList<TSURL> surlToRelease = new LinkedList<TSURL>();
        for(TSURLReturnStatus returnStatus : surlReturnStatuses.getArray())
        {
            if(TStatusCode.SRM_SUCCESS.equals(returnStatus.getStatus().getStatusCode()))
            {
                surlToRelease.add(returnStatus.getSurl());
            }
        }
        return surlToRelease;
    }

    private void expireSurls(List<TSURL> surlToRelease, TRequestToken requestToken) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException, UnknownSurlException
    {
        if(requestToken != null)
        {
            SurlStatusManager.checkAndUpdateStatus(requestToken, surlToRelease,
                                                   TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_RELEASED);
            SurlStatusManager.checkAndUpdateStatus(requestToken, surlToRelease,
                                                   TStatusCode.SRM_SUCCESS, TStatusCode.SRM_RELEASED);
        }
        else
        {
            for(TSURL surl : surlToRelease)
            {
                Map<TRequestToken, TReturnStatus> tokenStatusMap = SurlStatusManager.getSurlCurrentStatuses(surl);
                for(Entry<TRequestToken, TReturnStatus> tokenStatus : tokenStatusMap.entrySet())
                {
                    if(TStatusCode.SRM_FILE_PINNED.equals(tokenStatus.getValue().getStatusCode()))
                    {
                        SurlStatusManager.checkAndUpdateStatus(tokenStatus.getKey(), surlToRelease,
                                                               TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_RELEASED);
                    }
                    else
                    {
                        if(TStatusCode.SRM_SUCCESS.equals(tokenStatus.getValue().getStatusCode()))
                        {
                            SurlStatusManager.checkAndUpdateStatus(tokenStatus.getKey(), surlToRelease,
                                                                   TStatusCode.SRM_SUCCESS, TStatusCode.SRM_RELEASED);
                        }
                    }
                }
            }    
        }
    }

    /**
     * Removes the Extended Attribute "pinned" from SURLs belonging to a filesystem with tape support.
     * 
     * @param surlToRelease
     */
    private void removePinneExtendedAttribute(List<TSURL> surlToRelease)
    {
        for (TSURL surl : surlToRelease)
        {
            try
            {
                StoRI stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
                if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled())
                {
                    StormEA.removePinned(stori.getAbsolutePath());
                }
            } catch(NamespaceException e)
            {
                log.error("Cannot remove EA \"pinned\" because cannot get StoRI from SURL: " + surl);
                continue;
            }
        }
    }

    private static void printRequestOutcome(TReturnStatus status, ReleaseFilesInputData inputData)
    {
        if (inputData != null)
        {
            if (inputData.getArrayOfSURLs() != null)
            {
                if (inputData.getRequestToken() != null)
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
                                                      inputData.getRequestToken(),
                                                      inputData.getArrayOfSURLs().asStringList());
                }
                else
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
                                                      inputData.getArrayOfSURLs().asStringList());
                }

            }
            else
            {
                if (inputData.getRequestToken() != null)
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
                                                      inputData.getRequestToken());
                }
                else
                {
                    CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
                }
            }

        }
        else
        {
            CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
        }
    }
    
    private void printSurlOutcome(TSURLReturnStatus surlStatus, ReleaseFilesInputData inputData)
    {
        CommandHelper.printSurlOutcome(SRM_COMMAND, log, surlStatus.getStatus(), inputData, surlStatus.getSurl());   
    }
}
