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

import it.grid.storm.acl.AclManager;
import it.grid.storm.acl.AclManagerFSAndHTTPS;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
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
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.MvInputData;
import it.grid.storm.synchcall.data.directory.MvOutputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project This
 * class implements the SrmMv Command.
 * 
 * @author lucamag
 * @date May 28, 2008
 */

public class MvCommand extends DirectoryCommand implements Command {

    private final NamespaceInterface namespace;
    private final PtPChunkCatalog putCatalog;
    private final PtGChunkCatalog getCatalog;

    public MvCommand() {
        namespace = NamespaceDirector.getNamespace();
        putCatalog = PtPChunkCatalog.getInstance();
        getCatalog = PtGChunkCatalog.getInstance();

    }

    /**
     * Method that provide SrmMv functionality.
     * 
     * @param inputData Contains information about input data for Mv request.
     * @return outputData Contains output data
     */
    public OutputData execute(InputData data)
    {

        log.debug("srmMv: Start execution.");
        MvOutputData outputData = new MvOutputData();
        MvInputData inputData = (MvInputData) data;

        TReturnStatus returnStatus = null;
        TReturnStatus returnStatus_INVALID_PATH = null;
        // Defined here because it is used many times... (exception handling stuff...)
        try
        {
            returnStatus_INVALID_PATH = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "");
        }
        catch (InvalidTReturnStatusAttributeException ex)
        {
            log.error("srmMv: Error creating returnStatus SRM_INVALID_PATH for internal use" + ex);
        }

        /**
         * Validate MvInputData. The check is done at this level to separate internal StoRM logic from xmlrpc
         * specific
         * operation.
         */

        if ((inputData == null) || (inputData.getFromSurl() == null) || (inputData.getToSurl() == null))
        {
            try
            {
                returnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "Invalid parameter specified.");
                log.error("srmMv: Request failed with [status: "
                        + returnStatus.toString() + "]");
            }
            catch (InvalidTReturnStatusAttributeException ex1)
            {
                log.error("srmMv: Request failed. Error creating returnStatus "
                        + ex1);
            }
            outputData.setStatus(returnStatus);
            return outputData;
        }

        /**
         * Check if GridUser in MvInputData is not null, otherwise return with an error message.
         */
        GridUserInterface guser = inputData.getUser();
        if (guser == null)
        {
            log.info("SrmMv: Unable to get user credential.");
            try
            {
                returnStatus = new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
                                                 "Unable to get user credential!");
                log.error("srmMv: Request failed with [status: "
                        + returnStatus.toString() + "]");
            }
            catch (InvalidTReturnStatusAttributeException ex1)
            {
                log.error("srmMv: Request failed. Error creating returnStatus "
                        + ex1);
            }
            outputData.setStatus(returnStatus);
            return outputData;
        }

        // Get fromSURL and toSURL from input structure.
        TSURL fromSURL = inputData.getFromSurl();
        TSURL toSURL = inputData.getToSurl();

        // Create StoRI from SURL
        StoRI fromStori = null;

        if (!fromSURL.isEmpty())
        {
            // Building StoRI representation of SURL within the request.
            try
            {
                fromStori = namespace.resolveStoRIbySURL(fromSURL, guser);
            } catch(IllegalArgumentException e)
            {
                log.error("srmMv: Unable to build StoRI by SURL:[" + fromSURL + "]" + e);
                try
                {
                    outputData.setStatus(new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL"));
                }
                catch (InvalidTReturnStatusAttributeException e1)
                {
                    log.error("srmMv: Request failed. Error creating returnStatus "
                              + e1);
                }
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                          + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
                return outputData;
            }
            catch (NamespaceException ex)
            {
                log.debug("srmMv: Unable to build StoRI by SURL:[" + fromSURL + "]" + ex);
                returnStatus_INVALID_PATH.setExplanation("Invalid fromSURL specified!");
                outputData.setStatus(returnStatus_INVALID_PATH);
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + outputData.getStatus() + "]");
                return outputData;
            }
        }
        else
        {
            returnStatus_INVALID_PATH.setExplanation("Invalid fromSURL specified!");
            outputData.setStatus(returnStatus_INVALID_PATH);
            log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                    + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
            return outputData;
        }

        // Create StoRI from toSURL
        StoRI toStori = null;

        if (!toSURL.isEmpty())
        {
            // Building StoRI representation of toSURL within the request.
            try
            {
                toStori = namespace.resolveStoRIbySURL(toSURL, guser);
            } catch(IllegalArgumentException e)
            {
                log.error("srmMv: Unable to build StoRI by SURL:[" + toSURL + "]" + e);
                try
                {
                    outputData.setStatus(new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL"));
                }
                catch (InvalidTReturnStatusAttributeException e1)
                {
                    log.error("srmMv: Request failed. Error creating returnStatus "
                              + e1);
                }
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                          + "] failed with [status: " + outputData.getStatus() + "]");
                return outputData;
            }
            catch (NamespaceException ex)
            {
                log.debug("srmMv: Unable to build StoRI by SURL:[" + toSURL + "]" + ex);
                returnStatus_INVALID_PATH.setExplanation("Invalid toSURL specified!");
                outputData.setStatus(returnStatus_INVALID_PATH);
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
                return outputData;
            }
        }
        else
        {
            returnStatus_INVALID_PATH.setExplanation("Invalid toSURL specified!");
            outputData.setStatus(returnStatus_INVALID_PATH);
            log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                    + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
            return outputData;
        }

        /**
         * From version 1.4 Add the control for Storage Area using the new authz for space component.
         */

        SpaceHelper sp = new SpaceHelper();
        TSpaceToken token = sp.getTokenFromStoRI(log, fromStori);
        SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

        if (!(spaceAuth.authorize(guser, SRMSpaceRequest.MV)))
        {
            // User not authorized to perform RM request on the storage area
            log.debug("srmMv: User not authorized to perform srmMv request on the storage area: " + token);
            try
            {
                returnStatus = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                                 ": User not authorized to perform srmMv request on the storage area: "
                                                         + token);
                log.error("srmMv: <" + guser + "> Request for [fromSURL:" + fromSURL + "] failed with [status: "
                        + returnStatus.toString() + "]");
            }
            catch (InvalidTReturnStatusAttributeException ex1)
            {
                log.error("srmMv: <" + guser + "> Request for [fromSURL:" + fromSURL
                        + "] failed. Error creating returnStatus " + ex1);
            }
            outputData.setStatus(returnStatus);
            return outputData;
        }

        // Get ACL mode from StoRI (AoT or JiT)
        boolean hasJiTACL = fromStori.hasJustInTimeACLs();

        LocalFile fromFile = fromStori.getLocalFile();
        LocalFile toFile = toStori.getLocalFile();

        // If fromFile and toFile are the same, then return SRM_SUCCESS
        if (fromFile.getPath().compareTo(toFile.getPath()) == 0)
        {
            try
            {
                returnStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
                                                 "Source SURL and target SURL are the same file.");
                log.info("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] successfully done with [status: " + returnStatus_INVALID_PATH.toString() + "]");
            }
            catch (InvalidTReturnStatusAttributeException ex1)
            {
                log.info("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] successfully done. Error creating returnStatus." + ex1);
            }
            outputData.setStatus(returnStatus);
            return outputData;
        }

        // If toFile is a directory then append the name of the file fromFile
        if (toFile.exists())
        {
            if (toFile.isDirectory())
            {
                int lastSlash = fromFile.getPath().lastIndexOf('/');
                String fromFileName = fromFile.getPath().substring(lastSlash + 1);
                String toSURLString = toSURL.getSURLString();
                if (!(toSURLString.endsWith("/")))
                {
                    toSURLString += "/";
                }
                toSURLString += fromFileName;
                log.debug("srmMv: New toSURL: " + toSURLString);
                StoRI toStoriFile = null;
                try
                {
                    TSURL toSURLFile = TSURL.makeFromStringValidate(toSURLString);
                    toStoriFile = namespace.resolveStoRIbySURL(toSURLFile, guser);
                } catch(IllegalArgumentException e)
                {
                    log.debug("srmMv : Unable to build StoRI by SURL '" + toSURL + "'", e);
                    try
                    {
                        outputData.setStatus(new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL"));
                    }
                    catch (InvalidTReturnStatusAttributeException e1)
                    {
                        log.error("srmMv: Request failed. Error creating returnStatus "
                                  + e1);
                    }
                    log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL="
                              + toSURL + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
                    return outputData;
                }
                catch (NamespaceException ex1)
                {
                    log.debug("srmMv : Unable to build StoRI by SURL '" + toSURL + "'", ex1);
                    returnStatus_INVALID_PATH.setExplanation("Invalid toSURL specified!");
                    outputData.setStatus(returnStatus_INVALID_PATH);
                    log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL="
                            + toSURL + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
                    return outputData;
                }
                catch (InvalidTSURLAttributesException ex2)
                {
                    log.error("Unable to create toSURL");
                    returnStatus_INVALID_PATH.setExplanation("Invalid toSURL specified!");
                    outputData.setStatus(returnStatus_INVALID_PATH);
                    log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL="
                            + toSURL + "] failed with [status: " + returnStatus_INVALID_PATH.toString() + "]");
                    return outputData;
                }
                toFile = toStoriFile.getLocalFile();
            }
        }
        /**
         * Construction of AuthZ request
         */
       
        // AuthorizationDecision mvFromAuth = AuthorizationCollector.getInstance().canDelete(user, fromStori);
        // AuthorizationDecision mvToAuth = AuthorizationCollector.getInstance().canCreateNewFile(user, toStori);
        /**
         * 1.5.0 Path Authorization
         */
        AuthzDecision mvAuthz_source =
                AuthzDirector.getPathAuthz().authorize(guser, SRMFileRequest.MV_source, fromStori, toStori);
        AuthzDecision mvAuthz_dest =
                AuthzDirector.getPathAuthz().authorize(guser, SRMFileRequest.MV_dest, fromStori, toStori);
        if ((mvAuthz_source.equals(AuthzDecision.PERMIT)) && (mvAuthz_dest.equals(AuthzDecision.PERMIT)))
        {
            log.debug("SrmMv: Mv authorized for " + guser + " for Source file = " + fromStori.getPFN()
                    + " to Target file =" + toStori.getPFN());
//            returnStatus = manageAuthorizedMV(fromStori, toFile, hasJiTACL);
            returnStatus = manageAuthorizedMV(fromStori, toFile);
            if (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
            {
                log.info("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] successfully done with [status: " + returnStatus.toString() + "]");
                try
                {
                    setAclForHttps(fromStori, toStori, guser.getLocalUser());
                }
                catch (CannotMapUserException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            else
            {
                log.warn("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + returnStatus.toString() + "]");
            }

        }
        else
        {
            String errMess = "Authz failure for unknown reasons";
            boolean srcFailure = false;
            if (!(mvAuthz_source.equals(AuthzDecision.PERMIT)))
            {
                srcFailure = true;
                errMess = "User is not authorized to read and/or delete (needed for Mv) the source file.";
            }
            if (!(mvAuthz_dest.equals(AuthzDecision.PERMIT)))
            {
                if (srcFailure)
                {
                    errMess += "and User is not authorized to create and/or write (needed for Mv) the destination file.";
                }
                else
                {
                    errMess = "User is not authorized to create and/or write (needed for Mv) the destination file.";
                }
            }
            try
            {
                returnStatus = new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, errMess);
                log.warn("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + returnStatus.toString() + "]");
            }
            catch (InvalidTReturnStatusAttributeException ex1)
            {
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed. Error creating returnStatus " + ex1);
            }
        }
        outputData.setStatus(returnStatus);
        return outputData;
    }

    private void setAclForHttps(StoRI oldFileStoRI, StoRI newFileStoRI, LocalUser localUser)
    {
        LocalFile newLocalFile = newFileStoRI.getLocalFile();
        LocalFile oldLocalFile = oldFileStoRI.getLocalFile();
//        FilesystemPermission fp = null;
//        boolean effective = false;
//        try
//        {
//            fp = newLocalFile.getEffectiveUserPermission(localUser);
//        }
//        catch (CannotMapUserException e)
//        {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        AclManager manager = AclManagerFSAndHTTPS.getInstance();
        try
        {
            manager.moveHttpsPermissions(oldLocalFile, newLocalFile);
        }
        catch (IllegalArgumentException e)
        {
            log.error("Unable to move permissions from the old to the new file. IllegalArgumentException: " + e.getMessage());
        }
        if (newFileStoRI.hasJustInTimeACLs()) {
            // JiT
            
            
            //TODO ACL manager
            try
            {
                manager.grantHttpsUserPermission(newLocalFile,localUser, FilesystemPermission.ReadWrite);
            }
            catch (IllegalArgumentException e)
            {
                log.error("Unable to grant user read and write permission on the new file. IllegalArgumentException: " + e.getMessage());
            }
//            localFile.grantUserPermission(localUser, FilesystemPermission.ReadWrite);
//            if (fp != null)
//            {
//                effective = fp.allows(FilesystemPermission.ReadWrite);
//                if (effective) {
                    // ACL was correctly set up! Track JiT!
//                    VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(),
//                                                                 localUser,
//                                                                 FilesystemPermission.Read,
//                                                                 start,
//                                                                 chunkData.pinLifetime());
//                    VolatileAndJiTCatalog.getInstance().trackJiT(fileStoRI.getPFN(),
//                                                                 localUser,
//                                                                 FilesystemPermission.Write,
//                                                                 start,
//                                                                 chunkData.pinLifetime());
//                }
//                else
//                {
//                    PtPChunk.log.error("ATTENTION in PTP CHUNK! The local filesystem has a mask that does not allow ReadWrite User-ACL to be set up on"
//                            + localFile.toString() + "!");
//                }
//            }
//            else
//            {
//                PtPChunk.log.error("ERROR in PTP CHUNK! A ReadWrite User-ACL was set on "
//                        + fileStoRI.getAbsolutePath() + " for user " + localUser.toString()
//                        + " but when subsequently verifying its effectivity, a null ACE was found!");
//            }
        }
        else
        {
            // AoT
            //TODO ACL manager
            try
            {
                manager.grantHttpsGroupPermission(newLocalFile, localUser, FilesystemPermission.ReadWrite);
            }
            catch (IllegalArgumentException e)
            {
                log.error("Unable to grant group read and write permission on the new file. IllegalArgumentException: " + e.getMessage());
            }
//            localFile.grantGroupPermission(localUser, FilesystemPermission.ReadWrite);
//            fp = localFile.getEffectiveGroupPermission(localUser);
//            if (fp != null) {
//                effective = fp.allows(FilesystemPermission.ReadWrite);
//                if (!effective) {
//                    PtPChunk.log.error("ATTENTION in PTP CHUNK! The local filesystem has a mask that does not allow ReadWrite Group-ACL to be set up on"
//                            + localFile.toString() + "!");
//                }
//            } else {
//                PtPChunk.log.error("ERROR in PTP CHUNK! ReadWrite Group-ACL was set on "
//                        + fileStoRI.getAbsolutePath() + " for group " + localUser.toString()
//                        + " but when subsequently verifying its effectivity, a null ACE was found!");
//            }
        }
        
    }

    /**
     * Split PFN , recursive creation is not supported, as reported at page 16 of Srm v2.1 spec.
     * 
     * @param user VomsGridUser
     * @param LocalFile fromFile
     * @param LocalFile toFile
     * @return TReturnStatus
     */
//    private TReturnStatus manageAuthorizedMV(StoRI fromStori, LocalFile toFile, boolean hasJiTACL) {
    private TReturnStatus manageAuthorizedMV(StoRI fromStori, LocalFile toFile) {
        TReturnStatus returnStatus = null;
        boolean creationDone;

        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        LocalFile fromFile = fromStori.getLocalFile();
        LocalFile fromParent = fromFile.getParentFile();
        LocalFile toParent = toFile.getParentFile();

        /*
         * Controllare che File sorgente esiste Esiste directory destinazione(che esista e sia directory) Non esiste
         * file deestinazione
         */

        boolean sourceExists = false;
        boolean targetDirExists = false;
        boolean targetFileExists = false;

        if (fromFile != null) {
            sourceExists = fromFile.exists();
        }

        if (toParent != null) {
            targetDirExists = toParent.exists() && toParent.isDirectory();
        }

        if (toFile != null) {
            targetFileExists = toFile.exists();
        }

        if (sourceExists && targetDirExists && !targetFileExists) {
            // Request on valid from and to file.
            log.debug("srmMv : All Check passed.");

            /**
             * CHECK HERE IN CASO DI EGRID SI VUOLE LA CREAZIONE DINAMICA DI DIRECTORY?
             */

            /**
             * Check if there is an active SrmPrepareToPut on the source SURL. In that case SrmMv() fails with
             * SRM_FILE_BUSY.
             */
            if (putCatalog.isSRM_SPACE_AVAILABLE(fromStori.getSURL())) {
                // There is an active PrepareToPut!
                log.debug("srmMv requests fails because there is a PrepareToPut on the from SURL.");
                failure = true;
                explanation = "There is an active SrmPrepareToPut on from SURL.";
                statusCode = TStatusCode.SRM_FILE_BUSY;

            } else {
                log.debug("srmMv: No PrepareToPut running on from SURL.");
            }

            /**
             * Check if there is an active SrmPrepareToGet on the source SURL. In that case SrmMv() fails with
             * SRM_FILE_BUSY.
             */
            if (getCatalog.isSRM_FILE_PINNED(fromStori.getSURL())) {
                // There is an active PrepareToGet!
                log.debug("SrmMv: requests fails because the source SURL is being used from other requests.");
                failure = true;
                explanation = "There is an active SrmPrepareToGet on from SURL";
                statusCode = TStatusCode.SRM_FILE_BUSY;
            }

            if (failure) {
                // Setting of Return Status
                try {
                    returnStatus = new TReturnStatus(statusCode, explanation);
                } catch (InvalidTReturnStatusAttributeException e) {
                    log.debug("SrmMv: Error creating returnStatus " + e);
                }

                return returnStatus;
            }

            /**
             * Perform the SrmMv() operation.
             */
            creationDone = fromFile.renameTo(toFile.getPath());

            if (creationDone) {
                log.debug("SrmMv: Request success!");
                failure = false;
                explanation = "SURL moved with success";
                statusCode = TStatusCode.SRM_SUCCESS;
            } else {
                log.debug("SrmMv: Requests fails because the path is invalid.");
                failure = true;
                explanation = "Invalid path";
                statusCode = TStatusCode.SRM_INVALID_PATH;
            }

        } else {
            if (!sourceExists) { // and it is a file
                log.debug("SrmMv: request fails because the source SURL does not exists!");
                failure = true;
                explanation = "Source SURL does not exists!";
                statusCode = TStatusCode.SRM_INVALID_PATH;
            } else if (!targetDirExists) {
                log.debug("SrmMv: request fails because the target directory does not exitts.");
                failure = true;
                explanation = "Target directory does not exits!";
                statusCode = TStatusCode.SRM_INVALID_PATH;
            } else if (targetFileExists) {
                log.debug("SrmMv: request fails because the target SURL exists.");
                failure = true;
                explanation = "Target SURL exists!";
                statusCode = TStatusCode.SRM_DUPLICATION_ERROR;
            } else {
                log.debug("SrmMv request failure! That is a BUG!");
                failure = true;
                explanation = "That is a bug!";
                statusCode = TStatusCode.SRM_INTERNAL_ERROR;
            }
        }

        /**
         * VERIFICARE SE I PERMESSI RIMANGONO IMPOSTATI
         */

        // Check if failure occour before try to set up ACL.
        /*
         * if (!failure) { // Add Acces Control List (ACL) in directory created. // ACL allow user to read-write-list
         * the new directory // Call wrapper to set ACL on file created.
         * log.debug("Mv : Adding ACL for directory created : '" + file + "'  " + "group:g_name:rwx");
         * FilesystemPermission fpRW = FilesystemPermission.ReadWrite; FilesystemPermission fpLIST =
         * FilesystemPermission.ListTraverse; // Check if Jit or AoT if (hasJiTACL) { // Jit Case // With JiT Model the
         * ACL for directory is not needed. } else { // AoT Case try { file.grantGroupPermission(user.getLocalUser(),
         * fpRW); file.grantGroupPermission(user.getLocalUser(), fpLIST); } catch (CannotMapUserException ex5) {
         * log.error("Unable to setting up the ACL ", ex5); failure = true; explanation = explanation +
         * " [ADDED : Unable to setting up the ACL ]"; } } if (failure) { // Rollback ... /**
         * @todo: Rollback of failure. } } // failure
         */

        // Setting of Return Status
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.debug("SrmMv: Error creating returnStatus " + e);
        }

        return returnStatus;
    }
}
