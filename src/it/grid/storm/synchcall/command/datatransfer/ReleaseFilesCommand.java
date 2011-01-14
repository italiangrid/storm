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

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGChunkData;
import it.grid.storm.catalogs.ReducedBoLChunkData;
import it.grid.storm.catalogs.ReducedChunkData;
import it.grid.storm.catalogs.ReducedPtGChunkData;
import it.grid.storm.ea.StormEA;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

    private PtGChunkCatalog dbCatalogPtG = PtGChunkCatalog.getInstance();
    private BoLChunkCatalog dbCatalogBoL = BoLChunkCatalog.getInstance();

    private ReleaseFilesOutputData outputData;
    private ReleaseFilesInputData inputData;
    private TReturnStatus globalStatus = null;
    private boolean requestFailure;
    private boolean requestSuccess;

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
        if (inputData == null) {
            requestFailure = true;
        } else if ((inputData.getArrayOfSURLs() == null) && (inputData.getRequestToken() == null)) {
            requestFailure = true;
        }

        if (requestFailure) {
            log.error("ReleaseFiles: Invalid input parameters specified");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
                                                 "Missing mandatory parameters (requestToken or arrayOfSURLs must be specified)");
            } catch (InvalidTReturnStatusAttributeException e) {
                log.warn("dataTransferManger: Error creating returnStatus " + e);
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);

            if (inputData == null) {
                log.error("srmReleaseFiles: <> Request for [token:] [SURL:] failed with [status: "
                        + globalStatus + "]");
            } else {
                log.error("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
                        + inputData.getRequestToken() + "] for [SURL: " + inputData.getArrayOfSURLs()
                        + "] failed with [status: " + globalStatus + "]");
            }

            return outputData;
        }

        /********************** Check user authentication and authorization ******************************/
        GridUserInterface user = inputData.getUser();
        if (user == null) {
            log.debug("ReleaseFiles: the user field is NULL");
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                 "Unable to get user credential!");
            } catch (InvalidTReturnStatusAttributeException e) {
                log.debug("dataTransferManger: Error creating returnStatus " + e);
            }

            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmReleaseFile: <" + inputData.getUser() + "> Request for [token:"
                    + inputData.getRequestToken() + "] for [SURL: " + inputData.getArrayOfSURLs()
                    + "] failed with [status: " + globalStatus + "]");
            return outputData;
        }

        // Mapping of VOMS Grid user into Local User
        @SuppressWarnings("unused")
        LocalUser lUser = null;
        try {
            lUser = user.getLocalUser();
        } catch (CannotMapUserException e) {
            log.debug("ReleaseFiles: Unable to map the user '" + user + "' in a local user.", e);
            try {
                globalStatus = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                                 "Unable to map the user");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                // Nothing to do, it will never be thrown
                log.warn("dataTransferManger: Error creating returnStatus ");
            }
            outputData.setReturnStatus(globalStatus);
            outputData.setArrayOfFileStatuses(null);
            log.error("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
                    + inputData.getRequestToken() + "] for [SURL: " + inputData.getArrayOfSURLs()
                    + "] failed with [status: " + globalStatus + "]");
            return outputData;
        }

        /********************************** Start to manage the request ***********************************/
        TRequestToken requestToken = inputData.getRequestToken();

        // Get the list of candidate SURLs from the DB
        Collection<ReducedChunkData> chunks = getChunks(user, requestToken, inputData.getArrayOfSURLs());

        // "chunks.size()" is an upper bound of the dimension of the list of the returned statuses.
        ArrayOfTSURLReturnStatus surlStatusReturnList = new ArrayOfTSURLReturnStatus(chunks.size());
        Collection<ReducedChunkData> surlToRelease = new LinkedList<ReducedChunkData>();

        retrieveSurlsToRelease(chunks, surlToRelease, surlStatusReturnList);

        // Update the DB
        if (!(surlToRelease.isEmpty())) {
            List<ReducedPtGChunkData> ptgChunksToRelease = new LinkedList<ReducedPtGChunkData>();
            List<ReducedBoLChunkData> bolChunksToRelease = new LinkedList<ReducedBoLChunkData>();

            for (ReducedChunkData chunk : surlToRelease) {
                if (chunk instanceof ReducedPtGChunkData) {
                    ptgChunksToRelease.add((ReducedPtGChunkData) chunk);
                } else {
                    bolChunksToRelease.add((ReducedBoLChunkData) chunk);
                }
            }

            dbCatalogPtG.transitSRM_FILE_PINNEDtoSRM_RELEASED(ptgChunksToRelease, requestToken);
            dbCatalogBoL.transitSRM_SUCCESStoSRM_RELEASED(bolChunksToRelease, requestToken);
        }

        removePinneExtendedAttribute(surlToRelease);

        if (globalStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
            log.info("srmReleaseFiles: <" + user + "> Request for [token:" + inputData.getRequestToken()
                    + "] [SURL:" + inputData.getArrayOfSURLs() + "] successfully done with: [status:"
                    + globalStatus.toString() + "]");
        } else if (globalStatus.getStatusCode().equals(TStatusCode.SRM_PARTIAL_SUCCESS)) {
            log.info("srmReleaseFiles: <" + user + "> Request for [token:" + inputData.getRequestToken()
                    + "] [SURL:" + inputData.getArrayOfSURLs() + "] partially done with: [status:"
                    + globalStatus.toString() + "]");
        } else {
            log.error("srmReleaseFiles: <" + user + "> Request for [token:" + inputData.getRequestToken()
                    + "] [SURL:" + inputData.getArrayOfSURLs() + "] failed with: [status:"
                    + globalStatus.toString() + "]");
        }

        outputData.setReturnStatus(globalStatus);
        outputData.setArrayOfFileStatuses(surlStatusReturnList);

        log.debug("End of ReleaseFiles function");
        return outputData;
    }

    /**
     * Retrieve SURL chunks from the db. <br>
     * If requestToken is not NULL and arrayOfSURLs is NULL then all the PtGChunks or BoLChunks matching the
     * requestToken are returned. If requestToken is not NULL and arrayOfSURLs is not NULL then all the
     * PtGChunks/BoLChunks matching requestToken and arrayOfSURLs are returned. If requestToken is NULL and
     * arrayOfSURLs is not NULL then all the PtGChunks and BoLChunks belonging to the user are returned. If
     * there are no matches then an empty collection is returned. requestToken and arrayOfSURLs cannot be both
     * NULL, in this case an empty collection is returned.
     */
    private Collection<ReducedChunkData> getChunks(GridUserInterface user, TRequestToken requestToken,
            ArrayOfSURLs arrayOfSURLs) {

        Collection<ReducedChunkData> surlChunks;

        if ((requestToken == null) && (arrayOfSURLs == null)) {
            return new LinkedList<ReducedChunkData>();
        }

        if (requestToken != null) {

            surlChunks = dbCatalogPtG.lookupReducedPtGChunkData(requestToken);

            // if the requestToken is not null than it refers to a PtG or to a BoL, not to both of them.
            if (surlChunks.isEmpty()) {
                surlChunks.addAll(dbCatalogBoL.lookupReducedBoLChunkData(requestToken));
            }

        } else {

            surlChunks = dbCatalogPtG.lookupReducedPtGChunkData(user, arrayOfSURLs.getArrayList());
            surlChunks.addAll(dbCatalogBoL.lookupReducedBoLChunkData(user, arrayOfSURLs.getArrayList()));
        }

        return surlChunks;
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
     * Convenient method to build a {@link TReturnStatus} from constants (i.e. getting rid of unneeded
     * try/catch block).
     * 
     * @param statusCode
     * @param explanation
     * @return
     */
    private TSURLReturnStatus getTSURLReturnStatus(TSURL surl, TReturnStatus returnStatus) {

        TSURLReturnStatus status = null;

        try {
            status = new TSURLReturnStatus(surl, returnStatus);
        } catch (InvalidTSURLReturnStatusAttributeException e) {
            // Nothing to do, it will never be thrown
            log.error("ReleaseFiles (BUG): error creatung TSURLReturnStatusElement");
        }

        return status;
    }

    /**
     * Removes the Extended Attribute "pinned" from SURLs belonging to a filesystem with tape support.
     * 
     * @param surlToRelease
     */
    private void removePinneExtendedAttribute(Collection<ReducedChunkData> surlToRelease) {

        // Remove the pin Extended Attribute (for filesystems with tape support)
        for (ReducedChunkData chunk : surlToRelease) {

            try {

                StoRI stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(chunk.fromSURL());

                if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {

                    if (inputData.getRequestToken() == null) {
                        StormEA.removePinned(stori.getAbsolutePath());
                        continue;
                    }

                    if (chunk instanceof PtGChunkData) {

                        if (!dbCatalogPtG.isSRM_FILE_PINNED(chunk.fromSURL())) {
                            if (!dbCatalogBoL.isSRM_FILE_PINNED(chunk.fromSURL())) {
                                StormEA.removePinned(stori.getAbsolutePath());
                            }
                        }
                    } else {

                        if (!dbCatalogBoL.isSRM_FILE_PINNED(chunk.fromSURL())) {
                            if (!dbCatalogPtG.isSRM_FILE_PINNED(chunk.fromSURL())) {
                                StormEA.removePinned(stori.getAbsolutePath());
                            }
                        }
                    }
                }
            } catch (NamespaceException e) {
                log.error("Cannot remove EA \"pinned\" because cannot get StoRI from SURL: "
                        + chunk.fromSURL().toString());
                continue;
            }
        }
    }

    /**
     * Retrieves the list of SURLs (as {@link ReducedChunkData}) e sets the return status of each SURL of the
     * request.
     * 
     * @param chunks the list of candidate surls retrieved from the DB.
     * @param surlToRelease output parameter: the list of SURLs to release.
     * @param surlStatusReturnList output parameter: the list of return status for each SURL of the request.
     */
    private void retrieveSurlsToRelease(Collection<ReducedChunkData> chunks,
            Collection<ReducedChunkData> surlToRelease, ArrayOfTSURLReturnStatus surlStatusReturnList) {

        TRequestToken requestToken = inputData.getRequestToken();
        ArrayOfSURLs arrayOfUserSURLs = inputData.getArrayOfSURLs();

        if (chunks.isEmpty()) {

            // Case 1: no candidate SURLs in the DB. SRM_INVALID_REQUEST or SRM_FAILURE are returned.
            log.debug("No SURLs found in the DB.");

            if (requestToken != null) {

                globalStatus = getTReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");

            } else if (arrayOfUserSURLs != null) {

                globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE,
                                                "None of the specified SURLs was found");

            } else {

                globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "No SURLs found");

            }
        } else {

            requestFailure = true;
            requestSuccess = true;

            if (arrayOfUserSURLs == null) {

                // Case 2: Only requestToken is specified and candidate SURLs were found in the DB.
                //         The status of all the candidate SURLs is returned to the user.
                for (ReducedChunkData chunk : chunks) {

                    TReturnStatus fileLevelStatus;

                    if (chunk.isPinned()) {
                        surlToRelease.add(chunk);
                        requestFailure = false;
                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
                    } else {
                        requestSuccess = false;
                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_FAILURE,
                                                           "Not released because it is not pinned");
                    }
                    TSURLReturnStatus surlStatus = getTSURLReturnStatus(chunk.fromSURL(), fileLevelStatus);
                    if (fileLevelStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                        log.info("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
                                + requestToken + "] for [SURL: " + chunk.fromSURL()
                                + "] successfully done with [status: " + fileLevelStatus + "]");
                    } else {
                        log.error("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
                                + requestToken + "] for [SURL: " + chunk.fromSURL()
                                + "] failed with [status: " + fileLevelStatus + "]");
                    }

                    surlStatusReturnList.addTSurlReturnStatus(surlStatus);
                }

            } else {

                // Case 3: arrayOfUserSURLs is not empty and candidate SURLs were found in the DB.
                //         SURLs present in both arrayOfUserSURLs and surlPtGChunks are the ones
                //         that have to be released.
                //         For the SURLs that are in arrayOfUserSURLs but not in surlPtGChunks a
                //         SRM_INVALID_PATH is returned.
                //         The resulting status for all the SURLs in arrayOfUserSURLs is returned to the user.
                for (int i = 0; i < arrayOfUserSURLs.size(); i++) {
                    TSURL surl = arrayOfUserSURLs.getTSURL(i);
                    boolean surlFound = false;
                    boolean atLeastOneReleased = false;
                    // Each SURL in arrayOfUserSURLs can match with more than one chunk (this can happen
                    // when requestToken is not specified). All the chunks that can be released are
                    // released. If there's at least one chunk that is released then the returned file level
                    // status of the corresponding SURL is SRM_SUCCESS, otherwise SRM_FAILURE. If no chunks
                    // are found for a SURL, then SRM_INVALID_PATH is returned.
                    for (ReducedChunkData chunk : chunks) {

                        if (surl.equals(chunk.fromSURL())) {
                            surlFound = true;
                            if (chunk.isPinned()) {
                                surlToRelease.add(chunk);
                                atLeastOneReleased = true;
                            }
                        }
                    }

                    TReturnStatus fileLevelStatus;
                    if (surlFound) {
                        if (atLeastOneReleased) {
                            requestFailure = false;
                            fileLevelStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
                        } else {
                            requestSuccess = false;
                            fileLevelStatus = getTReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                               "Not released because it is not pinned");
                        }
                    } else {
                        requestSuccess = false;
                        fileLevelStatus = getTReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                           "Invalid SURL or it is a directory (srmReleaseFiles of directories not yet supported)");
                    }

                    if (fileLevelStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                        log.info("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
                                + requestToken + "] for [SURL: " + surl.getSURLString()
                                + "] successfully done with [status: " + fileLevelStatus + "]");
                    } else {
                        log.error("srmReleaseFiles: <" + inputData.getUser() + "> Request for [token:"
                                + requestToken + "] for [SURL: " + surl.getSURLString()
                                + "] failed with [status: " + fileLevelStatus + "]");
                    }

                    TSURLReturnStatus surlStatus = getTSURLReturnStatus(surl, fileLevelStatus);
                    surlStatusReturnList.addTSurlReturnStatus(surlStatus);
                }
            } // end Case 3

            if (requestSuccess) {

                globalStatus = getTReturnStatus(TStatusCode.SRM_SUCCESS, "Files released");

            } else if (requestFailure) {

                globalStatus = getTReturnStatus(TStatusCode.SRM_FAILURE, "No files released");

            } else {

                globalStatus = getTReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                "Check files status for details");
            }
        }
    }
}
