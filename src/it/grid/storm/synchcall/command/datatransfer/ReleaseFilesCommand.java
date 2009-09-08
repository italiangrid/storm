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

    public ReleaseFilesCommand() {}

    /**
     * Does a ReleaseFiles. Used to release pins on the previously requested "copies" (or "state") of the
     * SURL. This function normally follows a srmPrepareToGet or srmBringOnline functions.
     */
    public OutputData execute(InputData data) {

        ReleaseFilesOutputData outputData = new ReleaseFilesOutputData();
        ReleaseFilesInputData inputData = (ReleaseFilesInputData) data;
        TReturnStatus globalStatus = null;
        boolean requestFailure;
        boolean requestSuccess;

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
        ArrayOfSURLs arrayOfUserSURLs = inputData.getArrayOfSURLs();
        TRequestToken requestToken = inputData.getRequestToken();

        // Get the list of candidate SURLs from the DB
        Collection<ReducedChunkData> chunks = getSurlPtGChunks(user, requestToken, arrayOfUserSURLs);

        // Build the ArrayOfTSURLReturnStatus matching the SURLs requested by
        // the user and the list of candidate SURLs found in the db.
        ArrayOfTSURLReturnStatus surlStatusReturnList = null;
        Collection<ReducedChunkData> surlToRelease = new LinkedList<ReducedChunkData>();
        try {
            if (chunks.isEmpty()) {
                // Case 1: no candidate SURLs in the DB. SRM_INVALID_REQUEST or SRM_FAILURE are returned.
                log.debug("No SURLs found in the DB.");
                if (requestToken != null) {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
                } else if (arrayOfUserSURLs != null) {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
                                                     "None of the specified SURLs was found");
                } else {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "No SURLs found");
                }
            } else {
                requestFailure = true;
                requestSuccess = true;
                // "surlPtGChunks.size()" is an upper bound of the dimension of the list of the returned statuses.
                surlStatusReturnList = new ArrayOfTSURLReturnStatus(chunks.size());
                if (arrayOfUserSURLs == null) {

                    // Case 2: Only requestToken is specified and candidate SURLs were found in the DB.
                    //         The status of all the candidate SURLs is returned to the user.
                    for (ReducedChunkData chunk : chunks) {

                        TReturnStatus fileLevelStatus;

                        if (chunk.status().getStatusCode() == TStatusCode.SRM_FILE_PINNED) {
                            surlToRelease.add(chunk);
                            requestFailure = false;
                            fileLevelStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
                        } else {
                            requestSuccess = false;
                            fileLevelStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
                                                                "Not released because it is not pinned");
                        }
                        TSURLReturnStatus surlStatus = new TSURLReturnStatus(chunk.fromSURL(),
                                                                             fileLevelStatus);
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
                        for (ReducedChunkData ptgSURL : chunks) {

                            if (surl.equals(ptgSURL.fromSURL())) {
                                surlFound = true;
                                if (ptgSURL.status().getStatusCode() == TStatusCode.SRM_FILE_PINNED) {
                                    surlToRelease.add(ptgSURL);
                                    atLeastOneReleased = true;
                                }
                            }
                        }

                        TReturnStatus fileLevelStatus;
                        if (surlFound) {
                            if (atLeastOneReleased) {
                                requestFailure = false;
                                fileLevelStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Released");
                            } else {
                                requestSuccess = false;
                                fileLevelStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
                                                                    "Not released because it is not pinned");
                            }
                        } else {
                            requestSuccess = false;
                            fileLevelStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH,
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

                        TSURLReturnStatus surlStatus = new TSURLReturnStatus(surl, fileLevelStatus);
                        surlStatusReturnList.addTSurlReturnStatus(surlStatus);
                    }
                } // end Case 3

                if (requestSuccess) {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "Files released");

                } else if (requestFailure) {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "No files released");
                } else {
                    globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
                                                     "Check files status for details");
                }
            }
        } catch (InvalidTReturnStatusAttributeException ex1) {
            // Nothing to do, it will never be thrown
            log.warn("dataTransferManger: Error creating returnStatus ");
        } catch (InvalidTSURLReturnStatusAttributeException ex2) {
            // Nothing to do, it will never be thrown
            log.error("ReleaseFiles: error creatung TSURLReturnStatusElement");
        }

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
            dbCatalogBoL.transitSRM_FILE_PINNEDtoSRM_RELEASED(bolChunksToRelease, requestToken);
        }

        // Remove the pin Extended Attribute (for filesystems with tape support)
        for (ReducedChunkData chunk : surlToRelease) {

            try {

                StoRI stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(chunk.fromSURL());

                if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {
                    
                    if (requestToken == null) {
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
    private Collection<ReducedChunkData> getSurlPtGChunks(GridUserInterface user, TRequestToken requestToken,
            ArrayOfSURLs arrayOfSURLs) {

        Collection<ReducedChunkData> surlChunks;

        if ((requestToken == null) && (arrayOfSURLs == null)) {
            return new LinkedList<ReducedChunkData>();
        }

        if (requestToken != null) {

            surlChunks = dbCatalogPtG.lookupReducedPtGChunkData(requestToken);

            if (surlChunks.isEmpty()) {
                surlChunks.addAll(dbCatalogBoL.lookupReducedBoLChunkData(requestToken));
            }

        } else {

            surlChunks = dbCatalogPtG.lookupReducedPtGChunkData(user, arrayOfSURLs.getArrayList());
            surlChunks.addAll(dbCatalogBoL.lookupReducedBoLChunkData(user, arrayOfSURLs.getArrayList()));
        }

        return surlChunks;
    }
}
