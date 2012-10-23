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

import it.grid.storm.catalogs.ReducedChunkData;
import it.grid.storm.ea.StormEA;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLReturnStatusAttributeException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
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

    private ReleaseFilesOutputData outputData;
    private ReleaseFilesInputData inputData;
    private TReturnStatus globalStatus = null;
    private boolean requestFailure;
    private boolean requestSuccess;
    
    private static final String UNKNOWN_USER = "unknown";

    public ReleaseFilesCommand() {}

    /**
     * Does a ReleaseFiles. Used to release pins on the previously requested "copies" (or "state") of the
     * SURL. This function normally follows a srmPrepareToGet or srmBringOnline functions.
     */
    public OutputData execute(InputData data) {

        outputData = new ReleaseFilesOutputData();
        inputData = (ReleaseFilesInputData) data;

        log.debug("Started ReleaseFiles function");

        /******************** Check for malformed request: missing mandatory input parameters ****************/
        requestFailure = false;
        if (inputData == null
                || ((inputData.getArrayOfSURLs() == null || inputData.getArrayOfSURLs().size() == 0) && inputData.getRequestToken() == null))
        {
            requestFailure = true;
            log.error("ReleaseFiles: Invalid input parameters specified");
            globalStatus = buildStatus(TStatusCode.SRM_INVALID_REQUEST,
                                       "Missing mandatory parameters (requestToken or arrayOfSURLs must be specified)");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            if (inputData == null)
            {
                log.error("srmReleaseFiles: Requestfailed with [status: " + globalStatus + "]");
            }
            else
            {
                printRequestOutcome(globalStatus, inputData);
            }
            return outputData;
        }

        /********************** Check user authentication and authorization ******************************/
        GridUserInterface user = inputData.getUser();
        if (user == null)
        {
            log.debug("ReleaseFiles: the user field is NULL");
            globalStatus = buildStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                       "Unable to get user credential!");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(globalStatus, inputData);
            return outputData;
        }

        /********************************** Start to manage the request ***********************************/
        TRequestToken requestToken = inputData.getRequestToken();

        Map<TSURL, TReturnStatus> surlStastuses;
        try
        {
            surlStastuses = getSurlsStatus(user, requestToken, inputData.getArrayOfSURLs());
        } catch(IllegalArgumentException e)
        {
            log.warn("Unexpected IllegalArgumentException in getSurlsStatus: " + e);
            globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "Internal error. Unexpected IllegalArgumentException in getSurlsStatus");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(globalStatus, inputData);
            return outputData;
        } catch(RequestUnknownException e)
        {
            log.info("No surls status available. RequestUnknownException: " + e.getMessage());
            globalStatus = getTReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token and surls");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(globalStatus, inputData);
            return outputData;
        } catch(UnknownTokenException e)
        {
            log.info("No surls status available. UnknownTokenException: " + e.getMessage());
            globalStatus = getTReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(globalStatus, inputData);
            return outputData;
        } catch(ExpiredTokenException e)
        {
            log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
            globalStatus = buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(globalStatus, inputData);
            return outputData;
        }
        if (surlStastuses.isEmpty())
        {
            // Case 1: no candidate SURLs in the DB. SRM_INVALID_REQUEST or SRM_FAILURE are returned.
            log.info("No SURLs found in the DB. Request failed");
            if (requestToken != null)
            {
                globalStatus = getTReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
            }
            else
            {
                if (inputData.getArrayOfSURLs() != null)
                {

                    globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE,
                                                    "None of the specified SURLs was found");
                }
                else
                {
                    //impossible
                    globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "Internal error. Precondition check mismatch");
                }
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            printRequestOutcome(globalStatus, inputData);
            return outputData;
        }
        ArrayOfTSURLReturnStatus surlReturnStatuses = retrieveSurlsToRelease(surlStastuses, requestToken, inputData.getArrayOfSURLs());

        List<TSURL> surlToRelease = extractSurlToRelease(surlReturnStatuses);
        if (!(surlToRelease.isEmpty()))
        {
            try
            {
                expireSurls(surlToRelease, requestToken);
            } catch(IllegalArgumentException e)
            {
                log.warn("Unexpected IllegalArgumentException in expireSurls: " + e);
                globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "Internal error. Unexpected IllegalArgumentException in expireSurls");
                outputData.setReturnStatus(globalStatus);
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(globalStatus, inputData);
                return outputData;
            } catch(UnknownTokenException e)
            {
                log.warn("Unexpected RequestUnknownException in expireSurls: " + e);
                globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "Internal error. Unexpected RequestUnknownException in expireSurls");
                outputData.setReturnStatus(globalStatus);
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(globalStatus, inputData);
                return outputData;
            } catch(ExpiredTokenException e)
            {
                log.info("The request is expired: ExpiredTokenException: " + e.getMessage());
                globalStatus = buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
                outputData.setReturnStatus(globalStatus);
                outputData.setArrayOfFileStatuses(null);
                printRequestOutcome(globalStatus, inputData);
                return outputData;
            }
            
            
//            List<ReducedPtGChunkData> ptgChunksToRelease = new LinkedList<ReducedPtGChunkData>();
//            List<ReducedBoLChunkData> bolChunksToRelease = new LinkedList<ReducedBoLChunkData>();

//            for (TSURL surl : surlToRelease)
//            {
//                if (chunk instanceof ReducedPtGChunkData)
//                {
//                    ptgChunksToRelease.add((ReducedPtGChunkData) chunk);
//                }
//                else
//                {
//                    bolChunksToRelease.add((ReducedBoLChunkData) chunk);
//                }
//            }
// dbCatalogPtG.transitSRM_FILE_PINNEDtoSRM_RELEASED(ptgChunksToRelease, requestToken);
// dbCatalogBoL.transitSRM_SUCCESStoSRM_RELEASED(bolChunksToRelease, requestToken);
//            SurlStatusManager.checkAndUpdateStatus(requestToken, extractSurlList(ptgChunksToRelease),
//                                                   TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_RELEASED);
//            SurlStatusManager.checkAndUpdateStatus(requestToken, extractSurlList(bolChunksToRelease),
//                                                   TStatusCode.SRM_SUCCESS, TStatusCode.SRM_RELEASED);
        }

        removePinneExtendedAttribute(surlToRelease);
        printRequestOutcome(globalStatus, inputData);
        outputData.setReturnStatus(globalStatus);
        outputData.setArrayOfFileStatuses(surlReturnStatuses);
        log.debug("End of ReleaseFiles function");
        return outputData;
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

    private void expireSurls(List<TSURL> surlToRelease, TRequestToken requestToken) throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException
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

    private Map<TSURL, TReturnStatus> getSurlsStatus(GridUserInterface user, TRequestToken requestToken,
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

    /**
     * Convenient method to build a {@link TReturnStatus} from constants (i.e. getting rid of unneeded
     * try/catch block).
     * 
     * @param statusCode
     * @param explanation
     * @return
     */
    private TReturnStatus getTReturnStatus(TStatusCode statusCode, String explanation) {

        TReturnStatus status = null;
        try {
            status = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.error("ReleaseFiles (BUG): Error creating returnStatus (probably a BUG)");
        }

        return status;
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

    /**
     * Retrieves the list of SURLs (as {@link ReducedChunkData}) e sets the return status of each SURL of the
     * request.
     * 
     * @param surlStastuses the list of candidate surls retrieved from the DB.
     * @param surlToRelease output parameter: the list of SURLs to release.
     * @param surlStatusReturnList output parameter: the list of return status for each SURL of the request.
     */
    private ArrayOfTSURLReturnStatus retrieveSurlsToRelease(Map<TSURL, TReturnStatus> surlStastuses,
            TRequestToken requestToken, ArrayOfSURLs arrayOfUserSURLs)
    {
        ArrayOfTSURLReturnStatus surlReturnStatuses = new ArrayOfTSURLReturnStatus(surlStastuses.size());
        requestFailure = true;
        requestSuccess = true;
        if (arrayOfUserSURLs == null || arrayOfUserSURLs.size() == 0)
        {
            for (Entry<TSURL, TReturnStatus> surlStatus : surlStastuses.entrySet())
            {

                TReturnStatus fileLevelStatus;
                if (TStatusCode.SRM_FILE_PINNED.equals(surlStatus.getValue().getStatusCode()))
                {
                    requestFailure = false;
                    fileLevelStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
                }
                else
                {
                    requestSuccess = false;
                    fileLevelStatus = getTReturnStatus(TStatusCode.SRM_FAILURE,
                                                       "Not released because it is not pinned");
                }
                printFileOutcome(fileLevelStatus, inputData);
                surlReturnStatuses.addTSurlReturnStatus(buildSURLStatus(surlStatus.getKey(), fileLevelStatus));
            }
        }
        else
        {
            for(TSURL surl : arrayOfUserSURLs.getArrayList())
            {
//                boolean surlFound = false;
//                boolean atLeastOneReleased = false;
                TReturnStatus fileLevelStatus;
                TReturnStatus status = surlStastuses.get(surl);
                if(status != null)
                {
//                    surlFound = true;
                    if(TStatusCode.SRM_FILE_PINNED.equals(status.getStatusCode()))
                    {
//                        atLeastOneReleased = true;
                        requestFailure = false;
                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
                    }
                    else
                    {
                        requestSuccess = false;
                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                           "Not released because it is not pinned");
                    }
                }
                else
                {
                    requestSuccess = false;
                    fileLevelStatus = getTReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                       "Invalid SURL or it is a directory (srmReleaseFiles of directories not yet supported)");
                }
                printFileOutcome(fileLevelStatus, inputData);
                surlReturnStatuses.addTSurlReturnStatus(buildSURLStatus(surl, fileLevelStatus));
//                    for (ReducedChunkData chunk : surlStastuses)
//                    {
//
//                        if (surl.equals(chunk.fromSURL()))
//                        {
//                            surlFound = true;
//                            if (chunk.isPinned())
//                            {
//                                surlToRelease.add(chunk);
//                                atLeastOneReleased = true;
//                            }
//                        }
//                    }

//                TReturnStatus fileLevelStatus;
//                if (surlFound)
//                {
//                    if (atLeastOneReleased)
//                    {
//                        requestFailure = false;
//                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
//                    }
//                    else
//                    {
//                        requestSuccess = false;
//                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_INVALID_PATH,
//                                                           "Not released because it is not pinned");
//                    }
//                }
//                else
//                {
//                    requestSuccess = false;
//                    fileLevelStatus = getTReturnStatus(TStatusCode.SRM_INVALID_PATH,
//                                                       "Invalid SURL or it is a directory (srmReleaseFiles of directories not yet supported)");
//                }

//                    if (fileLevelStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
//                    {
//                        log.info("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
//                                + requestToken + "] for [SURL: " + surl.getSURLString()
//                                + "] successfully done with [status: " + fileLevelStatus + "]");
//                    }
//                    else
//                    {
//                        log.error("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
//                                + requestToken + "] for [SURL: " + surl.getSURLString()
//                                + "] failed with [status: " + fileLevelStatus + "]");
//                    }

//                    TSURLReturnStatus surlStatus = buildSURLStatus(surl, fileLevelStatus);
//                    surlStatusReturnList.addTSurlReturnStatus(surlStatus);
                }
            } // end Case 3

            if (requestSuccess)
            {

                globalStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Files released");

            }
            else
                if (requestFailure)
                {

                    globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "No files released");

                }
                else
                {

                    globalStatus = getTReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                    "Check files status for details");
                }
//        }
        return surlReturnStatuses;
    }
    
   

    /**
     * Convenient method to build a {@link TReturnStatus} from constants (i.e. getting rid of unneeded
     * try/catch block).
     * 
     * @param statusCode
     * @param explanation
     * @return
     */
    private TSURLReturnStatus buildSURLStatus(TSURL surl, TReturnStatus returnStatus) {

        TSURLReturnStatus status = null;

        try {
            status = new TSURLReturnStatus(surl, returnStatus);
        } catch (InvalidTSURLReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.error("ReleaseFiles (BUG): error creatung TSURLReturnStatusElement");
        }

        return status;
    }
    
    private static TReturnStatus buildStatus(TStatusCode statusCode, String explaination) throws IllegalArgumentException, IllegalStateException
    {
        if(statusCode == null)
        {
            throw new IllegalArgumentException("Unable to build the status, null arguments: statusCode=" + statusCode);
        }
        try
        {
            return new TReturnStatus(statusCode, explaination);
        } catch(InvalidTReturnStatusAttributeException e1)
        {
            // Never thrown
            throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException " +
                    "in building TReturnStatus: " + e1.getMessage());
        }
    }
    
    private static void printRequestOutcome(TReturnStatus status, ReleaseFilesInputData data)
    {
        String requestingUser;
        if(data.getUser() != null)
        {
            requestingUser = data.getUser().getDn();
        }
        else
        {
            requestingUser = UNKNOWN_USER;
        }
        if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
        {
            log.info("srmReleaseFiles: user <" + requestingUser + "> Request for [token:" + data.getRequestToken()
                    + "] for [SURL:" + data.getArrayOfSURLs() + "] successfully done with: [status:"
                    + status + "]");
        }
        else
        {
            if (status.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS))
            {
                log.info("srmReleaseFiles: user <" + requestingUser + "> Request for [token:" + data.getRequestToken()
                        + "] for [SURL:" + data.getArrayOfSURLs() + "] partially done with: [status:"
                        + status + "]");
            }
            else
            {
                log.error("srmReleaseFiles: user <" + requestingUser + "> Request for [token:" + data.getRequestToken()
                        + "] for [SURL:" + data.getArrayOfSURLs() + "] failed with: [status:"
                        + status + "]");
            }
        }
    }
    
    private void printFileOutcome(TReturnStatus status, ReleaseFilesInputData data)
    {
        String requestingUser;
        if(data.getUser() != null)
        {
            requestingUser = data.getUser().getDn();
        }
        else
        {
            requestingUser = UNKNOWN_USER;
        }
        if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
        {
            log.info("srmReleaseFiles: user <" + requestingUser + "> Request for [token:" + data.getRequestToken()
                    + "] for [SURL:" + data.getArrayOfSURLs() + "] successfully done with: [status:"
                    + status + "]");
        }
        else
        {
            log.error("srmReleaseFiles: user <" + requestingUser + "> Request for [token:" + data.getRequestToken()
                    + "] for [SURL:" + data.getArrayOfSURLs() + "] failed with: [status:"
                    + status + "]");
        }
    }
}
