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

package it.grid.storm.synchcall.command.directory;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.common.SRMConstants;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmdirInputData;
import it.grid.storm.synchcall.data.directory.RmdirOutputData;
import it.grid.storm.synchcall.data.exception.InvalidRmOutputAttributeException;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class RmdirCommand extends DirectoryCommand implements Command {
    private final NamespaceInterface namespace;

    public RmdirCommand() {
        namespace = NamespaceDirector.getNamespace();
    }

    /**
     * Method that provide SrmRmdir functionality.
     * 
     * @param inputData Contains information about input data for Rmdir request.
     *@return TReturnStatus Contains output data
     */
    public OutputData execute(InputData data) {
        log.debug("srmRm: Start execution.");
        TReturnStatus returnStatus = null;

        RmdirInputData inputData = (RmdirInputData) data;
        RmdirOutputData outData = null;

        /**
         * Validate RmdirInputData. The check is done at this level to separate internal StoRM logic from xmlrpc
         * specific operation.
         */

        if ((inputData == null) || ((inputData != null) && (inputData.getSurl() == null))) {
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "Invalid paramter specified.");
                log.info("srmRmdir: <>  Request for [SURL=] failed with [status: " + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmRmdir: <>  Request for [SURL=] failed. Error creating returnStatus " + ex1);
            }
            try {
                outData = new RmdirOutputData(returnStatus);
            } catch (InvalidRmOutputAttributeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return outData;
        }

        /**
         * Check if GridUser in RmdirInputData is not null, otherwise return with an error message.
         */
        GridUserInterface guser = inputData.getUser();
        // Create Input Structure
        if (guser == null) {
            log.debug("srmRm: Unable to get user credential. ");
            try {
                returnStatus =
                        new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE, "Unable to get user credential!");
                log.info("srmRmdir: <>  Request for [SURL=] failed with [status: " + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmRmdir: <>  Request for [SURL=] failed. Error creating returnStatus " + ex1);
            }
            try {
                outData = new RmdirOutputData(returnStatus);
            } catch (InvalidRmOutputAttributeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return outData;
        }

        TSURL surl = inputData.getSurl();
        StoRI stori = null;

        // Check if SURL is not empty
        if (!surl.isEmpty()) {
            try {
                stori = namespace.resolveStoRIbySURL(surl, guser);
            }
            catch (IllegalArgumentException e)
            {
                log.error("srmRmdir: Unable to build StoRI by surl and user " + e);
                try
                {
                    returnStatus = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, "Unable to build a STORI from surl=" + surl
                            + " user=" + guser);
                    log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl + "] failed with [status: "
                             + returnStatus.toString() + "]");
                }
                catch (InvalidTReturnStatusAttributeException ex1)
                {
                    log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl
                              + "] failed. Error creating returnStatus " + ex1);
                }
                try {
                    outData = new RmdirOutputData(returnStatus);
                } catch (InvalidRmOutputAttributeException e2) {
                    log.error("Unable to create the RmdirOutputData from status " + returnStatus + " RmdirOutputData: " + e2);
                }
                return outData;
            } catch (NamespaceException ex) {
                log.debug("srmRm: Unable to build StoRI by SURL : '" + surl + "'" + ex);
                try {
                    returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified");
                    log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl + "] failed with [status: "
                            + returnStatus.toString() + "]");
                } catch (InvalidTReturnStatusAttributeException ex1) {
                    log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl
                            + "] failed. Error creating returnStatus " + ex1);
                }

                try {
                    outData = new RmdirOutputData(returnStatus);
                } catch (InvalidRmOutputAttributeException e) {
                    log.error("Unable to create the RmdirOutputData from status " + returnStatus + " RmdirOutputData: " + e);
                }
                return outData;
            }

        } else {
            // Empty SURL. Error in surl creation.
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified");
                log.info("srmRmdir: <" + guser + ">  Request for [SURL=" + surl + "] failed with [status: "
                        + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl
                        + "] failed. Error creating returnStatus " + ex1);
            }
            try {
                outData = new RmdirOutputData(returnStatus);
            } catch (InvalidRmOutputAttributeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return outData;
        }

        // Check here if recursive flag is not specifed
        // in input parameter.Use default value
        Boolean recursive = inputData.getRecursiveFlag();
        if (recursive == null) {
            recursive = new Boolean(SRMConstants.recursiveFlag);
        }

        // Grid User Identity
        GridUserInterface user = guser;

        /**
         * From version 1.4 Add the control for Storage Area using the new authz for space component.
         */

        SpaceHelper sp = new SpaceHelper();
        TSpaceToken token = sp.getTokenFromStoRI(log, stori);
        SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

        if (!(spaceAuth.authorize(user, SRMSpaceRequest.RMD))) {
            // User not authorized to perform RM request on the storage area
            log.debug("srmRmdir: User not authorized to perform srmRmdir request on the storage area: " + token);
            try {
                returnStatus =
                        new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                          ": User not authorized to perform srmRmdir request on the storage area: "
                                                  + token);
                log.info("srmRmdir: <> Request for [SURL:" + surl + "] failed with [status: "
                        + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmRmdir: <> Request for [SURL=" + surl + "] failed. Error creating returnStatus " + ex1);
            }
            try {
                outData = new RmdirOutputData(returnStatus);
            } catch (InvalidRmOutputAttributeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return outData;

        }

        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        // Maps the VOMS Grid user into Local User.
        LocalUser lUser = null;
        try {
            lUser = user.getLocalUser();
        } catch (CannotMapUserException ex) {
            log.error("Unable to setting up the ACL ");
            failure = true;
            explanation = "RMDIR : Unable to map the user '" + user + "' in a local user";
            statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
        }

        if (!(failure)) { // There is a local user for VOMS user
            // AuthorizationDecision mkdirAuth = AuthorizationCollector.getInstance().canDelete(user, stori);

            /**
             * 1.5.0 Path Authorization
             */
            AuthzDecision rmDirAuthz = AuthzDirector.getPathAuthz().authorize(user, SRMFileRequest.RMD, stori);
            if (rmDirAuthz.equals(AuthzDecision.PERMIT)) {
                log.debug("RMDIR is authorized for " + user + " and the directory = " + stori.getPFN()
                        + " with recursove opt = " + recursive);
                returnStatus = manageAuthorizedRMDIR(lUser, stori, recursive.booleanValue());
                if (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
                    log.info("srmRmdir: <" + guser + ">  Request for [SURL=" + surl
                            + "] successfully done with [status: " + returnStatus.toString() + "]");
                } else {
                    log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl + "] failed with [status: "
                            + returnStatus.toString() + "]");
                }
            } else {
                failure = true;
                explanation = "User is not authorized to delete the directory";
                statusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;

            }
        }

        if (failure) { // Unauthorized access!
            try {
                returnStatus = new TReturnStatus(statusCode, explanation);
                log.info("srmRmdir: <" + guser + ">  Request for [SURL=" + surl + "] failed with [status: "
                        + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmRmdir: <" + guser + ">  Request for [SURL=" + surl
                        + "] failed .Error creating return status." + ex1);
            }
        }
        // Return status
        try {
            outData = new RmdirOutputData(returnStatus);
        } catch (InvalidRmOutputAttributeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return outData;
    }

    /**
     * This method of FileSystem remove file and dir both from file system and from DataBase
     * 
     * @param user VomsGridUser
     * @param stori StoRI
     * @param recursive boolean
     * @return TReturnStatus
     */
    private TReturnStatus manageAuthorizedRMDIR(LocalUser lUser, StoRI stori, boolean recursive) {
        TReturnStatus returnStatus = null;
        boolean dirRemoved;

        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        LocalFile directory = stori.getLocalFile();

        // Check if Directory to Remove exists
        if ((directory.exists()) && (directory.isDirectory())) {

            if (recursive) { // Recursive option
                // All directory and files contained are removed.
                log.debug("RECURSIVE=TRUE! Removing dir with all files included! ");

                boolean purgingResult = deleteDirectoryContent(directory, lUser);

                if (!(purgingResult)) { // There was some problems
                    statusCode = TStatusCode.SRM_FAILURE;
                    explanation = "Unable to delete some files within directory. Permission denied.";
                }
            }
            // Now Directory should be Empty;
            // NON-Recursive Option
            dirRemoved = removeFile(directory, lUser);
            if (!(dirRemoved)) { // There was some problems
                statusCode = TStatusCode.SRM_NON_EMPTY_DIRECTORY;
                explanation = "Directory is not empty";
            } else { // Success!!
                statusCode = TStatusCode.SRM_SUCCESS;
                explanation = "Directory removed with success!";
            }
        } else {
            log.debug("RMDIR : request with invalid directory specified!");
            // ParentDirectory doesn't exists!
            if (!directory.exists()) {
                statusCode = TStatusCode.SRM_INVALID_PATH;
                explanation = "Directory does not exists";
            } else {
                if (!directory.isDirectory()) {
                    statusCode = TStatusCode.SRM_INVALID_PATH;
                    explanation = "Not a directory";
                }
            }
        }
        // Build the ReturnStatus
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException ex1) {
            log.debug("RMDIR : Error creating returnStatus " + ex1);
        }

        return returnStatus;
    }

    private boolean removeFile(LocalFile file, LocalUser lUser) {
        boolean result = false;
        LocalFile[] list;
        if (file.exists()) { // existent file
            if (file.isDirectory()) { // the existent file is a directory
                // Check if directory is empty
                list = file.listFiles();
                if (list.length > 0) { // The directory is not empty!
                    // Produce an error, because only empty directory are
                    // permitted.
                    result = false;
                    log.debug("RMDIR : the target file '" + file + "' is a not-empty directory. ");
                } else { // The target is an empty directory
                    result = removeTarget(file, lUser);
                }
            } else { // the target is a file
                result = removeTarget(file, lUser);
            }
        } else { // The file does not exists
            result = false;
            log.debug("RMDIR : the target file '" + file + "' does not exists! ");
        }
        return result;
    }

    private boolean removeTarget(LocalFile file, LocalUser lUser) {
        boolean result = false;
        // Check Permission
//        FilesystemPermission groupPermission = null;
//        try {
//            groupPermission = file.getGroupPermission(lUser);
//        } catch (CannotMapUserException ex) {
//            /**
//             * @todo : Why this exception?
//             */
//            log.error("WHY THIS? " + ex);
//        }

//        FilesystemPermission userPermission = null;
//        try {
//            userPermission = file.getUserPermission(lUser);
//        } catch (CannotMapUserException ex1) {
//            /**
//             * @todo : Why this exception?
//             */
//            log.debug("WHY THIS? " + ex1);
//        }

        /**
         * @todo this check is not needed here. If Auth source say that user have the right permission. At this level
         *       could happen that a user create a Directory in JiT model, so without ACL, and then want to delete it.
         *       The permission on directory at filesystem level is not setted but the user must have the delete
         *       permission.
         */

        // Check if user or group permission are null to prevent Null Pointer
        boolean canDelete = true;
        /**
         * if(userPermission!=null) canDelete = userPermission.canDelete(); if ((groupPermission!=null)&&(!canDelete))
         * canDelete = groupPermission.canDelete();
         */
        // if ( (userPermission.canDelete()) || (groupPermission.canDelete())) {
        if (canDelete) {
            result = file.delete();
        } else {
            log.debug("RMDIR : Unable to delete the file '" + file + "'. Permission denied.");
        }

        return result;
    }

    /**
     * Recursive function for deleteAll
     */
    private boolean deleteDirectoryContent(LocalFile directory, LocalUser lUser) {
        boolean result = true;
        LocalFile[] list;
        if (directory.exists()) { // existent file
            if (directory.isDirectory()) { // the existent file is a directory
                // Scanning of directory
                list = directory.listFiles();
                if (list.length > 0) { // The directory is not empty
                    for (LocalFile element : list) {
                        // Delete each element within the directory
                        result = result && deleteDirectoryContent(element, lUser);
                        if (element.exists()) {
                            result = result && removeFile(element, lUser);
                        }
                    }
                } else {
                    // The directory is empty and it is deleted by the if in the
                    // for loop above
                }
            } else { // The target is a file
                result = removeFile(directory, lUser);
            }
        }
        return result;
    }

} // End of class
