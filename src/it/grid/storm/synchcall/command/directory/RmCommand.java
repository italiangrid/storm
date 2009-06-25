package it.grid.storm.synchcall.command.directory;

import it.grid.storm.authorization.AuthorizationCollector;
import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.VirtualFSInterface;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmInputData;
import it.grid.storm.synchcall.data.directory.RmOutputData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 27, 2008
 *
 */

public class RmCommand implements Command {

    private final Logger log = LoggerFactory.getLogger(RmCommand.class);
    private final String funcName = "srmRm";
    private final NamespaceInterface namespace;
    private final PtGChunkCatalog getcatalog;
    private final PtPChunkCatalog putcatalog;

    public RmCommand() {
        namespace = NamespaceDirector.getNamespace();
        getcatalog = PtGChunkCatalog.getInstance();
        putcatalog = PtPChunkCatalog.getInstance();
    }

    /**
     * Method that provide SrmRm functionality.
     * 
     * @param inputData
     *            Contains information about input data for rm request.
     * @return RmOutputData Contains output data
     */
    public OutputData execute(InputData inputDataGeneric) {
        log.debug("SrmRm: Dir Manager Rm start!");

        TReturnStatus globalStatus = null;

        // ReturnStructure
        RmOutputData outputData = new RmOutputData();
        RmInputData inputData = (RmInputData) inputDataGeneric;

        /**
         * Validate RmInputData. The check is done at this level to separate
         * internal StoRM logic from xmlrpc specific operation.
         */
        if ((inputData == null)
                || ((inputData != null) && (inputData.getSurlArray() == null))) {
            log.debug("srmRm : Invalid input parameter specified");
            try {
                globalStatus = new TReturnStatus(
                        TStatusCode.SRM_INVALID_REQUEST,
                "arrayOfSURLs is empty");
                log
                .error("srmRm: <>  Request for [SURL=] failed with [status: "
                        + globalStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log
                .error("srmRm: <>  Request for [SURL=] failed. Error creating returnStatus "
                        + ex1);

            }
            outputData.setStatus(globalStatus);
            outputData.setSurlStatus(null);

            return outputData;
        }

        /**
         * Check if GridUser in RMInputData is not null, otherwise return with
         * an error message.
         */
        GridUserInterface user = inputData.getUser();
        if (user == null) {
            log.debug("srmRm: Unable to get user credential. ");
            try {
                globalStatus = new TReturnStatus(
                        TStatusCode.SRM_AUTHENTICATION_FAILURE,
                "Unable to get user credential");
                log.error("srmRm: <> Request for [SURL:] failed with [status: "
                        + globalStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log
                .error("srmRm: <> Request for [SURL:] failed. Error creating returnStatus "
                        + ex1);
            }

            outputData.setStatus(globalStatus);
            outputData.setSurlStatus(null);

            return outputData;
        }

        log.debug("srmRm: INPUT data not null");

        // SURL structure.
        ArrayOfSURLs surlArray = inputData.getSurlArray();
        ArrayOfTSURLReturnStatus arrayOfFileStatus = new ArrayOfTSURLReturnStatus();

        boolean globalFailure = false;
        String explanation = "done";
        TStatusCode statusCode = TStatusCode.EMPTY;

        // Maps the VOMS Grid user into Local User.
        LocalUser lUser = null;
        try {
            log.debug("srmRm: Trying to get user Mapping.");
            lUser = user.getLocalUser();
        } catch (CannotMapUserException ex) {
            // Anybody requests will be processed!
            log.debug("srmRm: Unable to map the user '" + user
                    + "' in a local user.", ex);
            globalFailure = true;
            explanation = "Unable to map the user '" + user
            + "' in a local user.";
            statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
        }

        log.debug("srmRm: User Mapping.");

        int numberOfFiles = surlArray.size();

        if (!(globalFailure)) {
            log.debug("srmRm: DirManager: Rm: SURLVectorSize: "
                    + surlArray.size());
            StoRI stori = null;
            TReturnStatus returnStatus = null;

            boolean partialSuccess = false;
            globalFailure = true;
            for (int i = 0; i < numberOfFiles; i++) {
                // Get Surl to delete
                TSURL surl = surlArray.getTSURL(i);
                log.debug("srmRm: DirManager: Rm: SURL: " + surl);
                TSURLReturnStatus fileStatus = new TSURLReturnStatus();
                // AddSurl into TSURLReturnStatus struct
                fileStatus.setSurl(surl);

                boolean failure = false;
                // Validate TSURL
                if (surl.isEmpty()) {
                    log.debug("srmRm: Malformed SURL passed from converter ");
                    try {
                        returnStatus = new TReturnStatus(
                                TStatusCode.SRM_INVALID_PATH, "Invalid SURL");
                        log.error("srmRm: <" + user + "> Removing SURL " + i
                                + " of " + numberOfFiles
                                + " [SURL:] failed with [status: "
                                + returnStatus.toString() + "]");
                    } catch (InvalidTReturnStatusAttributeException ex1) {
                        log
                        .error("srmRm: <"
                                + user
                                + "> Removing SURL "
                                + i
                                + " of "
                                + numberOfFiles
                                + " [SURL:] failed. Error creating returnStatus "
                                + ex1);
                    }
                    failure = true;
                } else {
                    // Creation of StoRI
                    try {
                        stori = namespace.resolveStoRIbySURL(surl, user);
                    } catch (NamespaceException ex) {
                        log.debug("SrmRm: Unable to build StoRI by PFN " + ex);
                        try {
                            returnStatus = new TReturnStatus(
                                    TStatusCode.SRM_INVALID_PATH,
                            "Invalid SURL specified!");
                            log.error("srmRm: <" + user + "> Removing SURL "
                                    + i + " of " + numberOfFiles
                                    + " [SURL:] failed with [status: "
                                    + returnStatus.toString() + "]");
                        } catch (InvalidTReturnStatusAttributeException ex1) {
                            log
                            .error("srmRm: <"
                                    + user
                                    + "> Removing SURL "
                                    + i
                                    + " of "
                                    + numberOfFiles
                                    + " [SURL:] failed. Error creating returnStatus "
                                    + ex1);
                        }
                        failure = true;
                    }
                }


                if(!failure) {

                    /**
                     * From version 1.4
                     * Add the control for Storage Area
                     * using the new authz for space component.
                     */

                    SpaceHelper sp = new SpaceHelper();
                    TSpaceToken token = sp.getTokenFromStoRI(log, stori);
                    SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

                    if( ! (spaceAuth.authorize(user, SRMSpaceRequest.RM)) ) {
                        //User not authorized to perform RM request on the storage area
                        log.debug("srmRm: User not authorized to perform srmRm request on the storage area: "+token);
                        try {
                            globalStatus = new TReturnStatus(
                                    TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                    ": User not authorized to perform srmRm request on the storage area: " +token);
                            log.error("srmRm: <> Request for [SURL:+] failed with [status: "
                                    + globalStatus.toString() + "]");
                        } catch (InvalidTReturnStatusAttributeException ex1) {
                            log.error("srmRm: <> Request for [SURL:] failed. Error creating returnStatus " + ex1);
                        }

                        outputData.setStatus(globalStatus);
                        outputData.setSurlStatus(null);

                        return outputData;
                    }
                }


                if (!failure) {
                    AuthorizationDecision deleteAuth = AuthorizationCollector
                    .getInstance().canDelete(user, stori);

                    if ((deleteAuth != null) && (deleteAuth.isPermit())) {

                        log.debug("srmRm: authorized for " + user
                                + " for file = " + stori.getPFN());

                        //Prior to delete the file get the actual file size to update properly the DB

                        LocalFile localElement = stori.getLocalFile();
                        long fileSize = 0;
                        if(localElement.exists()) {
                            fileSize = localElement.getExactSize();
                        }

                        returnStatus = manageAuthorizedRM(lUser, surl, stori);
                        if (returnStatus.getStatusCode() == TStatusCode.SRM_SUCCESS) {
                            globalFailure = false;
                            log.info("srmRm: <"
                                    + user
                                    + "> Removing SURL "
                                    + i
                                    + " of "
                                    + numberOfFiles
                                    + " [SURL:] successfully done with [status: "
                                    + returnStatus.toString() + "]");

                            /**
                             * If Storage Area hard limit is enabled, update space on DB
                             */
                            try {
                                VirtualFSInterface fs = stori.getVirtualFileSystem();
                                if ( (fs!=null) && ( fs.getProperties().isOnlineSpaceLimited()) ){
                                    SpaceHelper sh = new SpaceHelper();
                                    //Update the used space into Database
                                    sh.increaseFreeSpaceForSA(log, funcName, user, surl, fileSize);
                                }
                            } catch (NamespaceException e) {
                                log.warn(funcName+"Not able to build the virtual fs properties for checking Storage Area size enforcement!");
                            }




                        } else {
                            partialSuccess = true;
                        }

                        fileStatus.setStatus(returnStatus);

                        log.debug("srmRm:  of (" + stori.getPFN()
                                + ") authorized was done. File Status is : "
                                + returnStatus);
                        failure = false;
                    } else {

                        failure = true;
                        explanation = "User is not authorized to delete the file";
                        try {
                            returnStatus = new TReturnStatus(statusCode, explanation);
                            log.error("srmRm: <"+user+"> Removing SURL "+i+" of "+numberOfFiles+" [SURL:] failed with [status: "+ returnStatus.toString()+"]");
                        } catch (InvalidTReturnStatusAttributeException ex1) {
                            log.error("srmRm: <"+user+"> Removing SURL "+i+" of "+numberOfFiles+" [SURL:] Error creating returnStatus " + ex1);
                        }


                    }

                    if (failure) {
                        try {
                            returnStatus = new TReturnStatus(statusCode,
                                    explanation);
                        } catch (InvalidTReturnStatusAttributeException ex1) {
                            log.debug("RmDir : Error creating returnStatus "
                                    + ex1);
                        }
                    }
                } else {
                    partialSuccess = true;
                }

                // add Status into TSURLReturnStatus
                fileStatus.setStatus(returnStatus);

                // Add TSURLReturnStatus into global structure
                arrayOfFileStatus.addTSurlReturnStatus(fileStatus);

            } // for

            if (globalFailure) {
                statusCode = TStatusCode.SRM_FAILURE;
                explanation = "No files removed";
            } else if (partialSuccess) {
                statusCode = TStatusCode.SRM_PARTIAL_SUCCESS;
                explanation = "Some files were not removed";
            } else {
                statusCode = TStatusCode.SRM_SUCCESS;
                explanation = "All files removed";
            }

        }

        // Set Global Status
        try {
            globalStatus = new TReturnStatus(statusCode, explanation);
            log.debug("srmRm: RM of N=" + numberOfFiles
                    + " files is now complete.");
            if (globalStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                log.info("srmRm: <" + user + "> Request for SURLs [SURL:"
                        + surlArray + "] successfully done with [status: "
                        + globalStatus.toString() + "]");
            } else {
                log.error("srmRm: <" + user + "> Request for SURLs [SURL:"
                        + surlArray + "] failed with [status: "
                        + globalStatus.toString() + "]");
            }
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("srmRm: <" + user + "> Request for SURLs [SURL:"
                    + surlArray + "] failed. Error creating returnStatus " + e);
        }

        outputData.setStatus(globalStatus);
        outputData.setSurlStatus(arrayOfFileStatus);

        return outputData;
    }

    /**
     * 
     * @param user
     *            VomsGridUser
     * @param stori
     *            StoRI
     * @return TReturnStatus
     */
    private TReturnStatus manageAuthorizedRM(LocalUser lUser, TSURL surl,
            StoRI stori) {
        TReturnStatus returnStatus = null;
        boolean fileRemoved;
        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        LocalFile file = stori.getLocalFile();

        if (!(file.exists())) {
            // The file does not exists!
            failure = true;
            statusCode = TStatusCode.SRM_INVALID_PATH;
            explanation = "File does not exists";
        } else if ((file.isDirectory())) {
            // The file exists but it is a directory!
            failure = true;
            statusCode = TStatusCode.SRM_INVALID_PATH;
            explanation = "The specified file is a directory. Not removed";
        } else {

            /**
             * If there are SrmPrepareToPut active on the SURL specified change
             * the SRM_STATUS from SRM_SPACE_AVAILABLE to SRM_ABORTED
             */
            putcatalog.transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(surl,
            "File Removed by a SrmRm()");

            /**
             * If there are SrmPrepareToGet active on the SURL specified change
             * the SRM_STATUS from SRM_FILE_PINNED to SRM_ABORTED
             */
            // getcatalog.transitSRM_FILE_PINNEDtoSRM_ABORTED(surl,"File Removed
            // by a SrmRm()");
            // The file exists and it is not a directory
            fileRemoved = removeTarget(file, lUser);

            if (!(fileRemoved)) {
                // Deletion failed for not enough permission.
                failure = true;
                statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
                explanation = "File not removed, permission denied.";
            } else { // File removed with success from underlying file system
                // Remove file entry from Persistence

                /**
                 * @todo: Remove file entry from Persistence Check if the
                 *        specified SURL is associated to a certain space Token,
                 *        in that case remove it and update the used space.
                 */

                statusCode = TStatusCode.SRM_SUCCESS;
                explanation = "File removed";
            }
        }

        // Build the ReturnStatus
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            log.debug("srmRm: Error creating returnStatus " + ex1);
        }

        return returnStatus;
    }

    private boolean removeTarget(LocalFile file, LocalUser lUser) {
        boolean result = false;
        // Check Permission
        FilesystemPermission groupPermission = null;
        try {
            groupPermission = file.getGroupPermission(lUser);
        } catch (CannotMapUserException ex) {
            /**
             * @todo : Why this exception?
             */
            log.debug("srmRm: WHY THIS? " + ex);
        }

        FilesystemPermission userPermission = null;
        try {
            userPermission = file.getUserPermission(lUser);
        } catch (CannotMapUserException ex1) {
            /**
             * @todo : Why this exception?
             */
            log.debug("srmRm: WHY THIS? " + ex1);
        }

        /**
         * Same situation in Rmdir This check is really needed here? The check
         * can not be done at this level. In Jit no ACE on file are setted!!! In
         * any case add check for null permission.
         */

        // Check if user or group permission are null to prevent Null Pointer
        boolean canDelete = true;

        /*
         * if(userPermission!=null) { canDelete = userPermission.canDelete();
         * log.debug("removeTarget:userP:"+userPermission.canDelete()); }
         * 
         * if ((groupPermission!=null)&&(!canDelete)) {
         * log.debug("removeTarget:groupP:"+groupPermission.canDelete());
         * canDelete = groupPermission.canDelete(); }
         */

        // if ( (userPermission.canDelete()) || (groupPermission.canDelete())) {
        if (canDelete) {
            result = file.delete();
        } else {
            log.debug("srmRm : Unable to delete the file '" + file
                    + "'. Permission denied.");
        }
        return result;
    }
}
