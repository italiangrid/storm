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
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project This
 * class implements the SrmMv Command.
 * 
 * @author lucamag
 * @date May 28, 2008
 */

public class MvCommand extends DirectoryCommand implements Command {

    private final NamespaceInterface namespace;

    public MvCommand() {
        namespace = NamespaceDirector.getNamespace();

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

        /**
         * Validate MvInputData. The check is done at this level to separate internal StoRM logic from xmlrpc
         * specific
         * operation.
         */

        if ((inputData == null) || (inputData.getFromSurl() == null) || (inputData.getToSurl() == null))
        {
            outputData.setStatus(buildStatus(TStatusCode.SRM_FAILURE, "Invalid parameter specified."));
            log.warn("srmMv: Request failed with [status: " + outputData.getStatus() + "]");
            return outputData;
        }

        /**
         * Check if GridUser in MvInputData is not null, otherwise return with an error message.
         */
        GridUserInterface guser = inputData.getUser();
        if (guser == null)
        {
            log.info("SrmMv: Unable to get user credential.");
            outputData.setStatus(buildStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
            "Unable to get user credential!"));
            log.info("srmMv: Request failed with [status: "
                     + outputData.getStatus() + "]");
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
                log.info("srmMv: Unable to build StoRI by SURL:[" + fromSURL + "]" + e);
                outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Unable to build StoRI by SURL"));
                log.info("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                          + "] failed with [status: " + outputData.getStatus() + "]");
                return outputData;
            }
            catch (NamespaceException ex)
            {
                log.debug("srmMv: Unable to build StoRI by SURL:[" + fromSURL + "]" + ex);
                outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid fromSURL specified!"));
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + outputData.getStatus() + "]");
                return outputData;
            }
        }
        else
        {
            outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_PATH,"Invalid fromSURL specified!"));
            log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                    + "] failed with [status: " + outputData.getStatus() + "]");
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
                outputData.setStatus(buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL"));
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                          + "] failed with [status: " + outputData.getStatus() + "]");
                return outputData;
            }
            catch (NamespaceException ex)
            {
                log.debug("srmMv: Unable to build StoRI by SURL:[" + toSURL + "]" + ex);
                outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid toSURL specified!"));
                log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + outputData.getStatus() + "]");
                return outputData;
            }
        }
        else
        {
            outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid toSURL specified!"));
            log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                    + "] failed with [status: " + outputData.getStatus() + "]");
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
            outputData.setStatus(buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                             ": User not authorized to perform srmMv request on the storage area: "
                                             + token));
            return outputData;
        }

        // Get ACL mode from StoRI (AoT or JiT)

        LocalFile fromFile = fromStori.getLocalFile();
        LocalFile toFile = toStori.getLocalFile();

        // If fromFile and toFile are the same, then return SRM_SUCCESS
        if (fromFile.getPath().compareTo(toFile.getPath()) == 0)
        {
            outputData.setStatus(buildStatus(TStatusCode.SRM_SUCCESS,
            "Source SURL and target SURL are the same file."));
            log.info("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                     + "] successfully done with [status: " + outputData.getStatus() + "]");
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
                    outputData.setStatus(buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Unable to build StoRI by SURL"));
                    log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL="
                              + toSURL + "] failed with [status: " + outputData.getStatus() + "]");
                    return outputData;
                }
                catch (NamespaceException ex1)
                {
                    log.debug("srmMv : Unable to build StoRI by SURL '" + toSURL + "'", ex1);
                    outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid toSURL specified!"));
                    log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL="
                            + toSURL + "] failed with [status: " + outputData.getStatus() + "]");
                    return outputData;
                }
                catch (InvalidTSURLAttributesException ex2)
                {
                    log.error("Unable to create toSURL");
                    outputData.setStatus(buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid toSURL specified!"));
                    log.error("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL="
                            + toSURL + "] failed with [status: " + outputData.getStatus() + "]");
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
        TReturnStatus returnStatus = null;
        if ((mvAuthz_source.equals(AuthzDecision.PERMIT)) && (mvAuthz_dest.equals(AuthzDecision.PERMIT)))
        {
            log.debug("SrmMv: Mv authorized for " + guser + " for Source file = " + fromStori.getPFN()
                    + " to Target file =" + toStori.getPFN());
//            returnStatus = manageAuthorizedMV(fromStori, toFile, hasJiTACL);
            returnStatus = manageAuthorizedMV(fromStori, toFile);
            if (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
            {
                log.info("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] successfully done with [status: " + returnStatus + "]");
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
                returnStatus = buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, errMess);
                log.warn("srmMv: <" + guser + "> Request for [fromSURL=" + fromSURL + "; toSURL=" + toSURL
                        + "] failed with [status: " + returnStatus + "]");
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
        boolean creationDone;

        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        LocalFile fromFile = fromStori.getLocalFile();
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
            TReturnStatus surlStatus = null;
            try
            {
                surlStatus = SurlStatusManager.getSurlsStatus(fromStori.getSURL());
            } catch(IllegalArgumentException e)
            {
                throw new IllegalStateException("Unexpected IllegalArgumentException in SurlStatusManager.getSurlsStatus: " + e);
            } catch(UnknownSurlException e)
            {
                log.debug("Surl " + fromStori.getSURL()
                        + " not stored, surl is not busy. UnknownSurlException: " + e.getMessage());
            }
            if(TStatusCode.SRM_SPACE_AVAILABLE.equals(surlStatus))
            {
                // There is an active PrepareToPut!
                log.debug("srmMv requests fails because there is a PrepareToPut on the from SURL.");
                explanation = "There is an active SrmPrepareToPut on from SURL.";
                statusCode = TStatusCode.SRM_FILE_BUSY;
                return buildStatus(statusCode, explanation);

            } else {
                log.debug("srmMv: No PrepareToPut running on from SURL.");
            }

            /**
             * Check if there is an active SrmPrepareToGet on the source SURL. In that case SrmMv() fails with
             * SRM_FILE_BUSY.
             */
           
//            if (getCatalog.isSRM_FILE_PINNED(fromStori.getSURL())) {
            if(TStatusCode.SRM_FILE_BUSY.equals(surlStatus))
            {
                // There is an active PrepareToGet!
                log.debug("SrmMv: requests fails because the source SURL is being used from other requests.");
                explanation = "There is an active SrmPrepareToGet on from SURL";
                statusCode = TStatusCode.SRM_FILE_BUSY;
                return buildStatus(statusCode, explanation);
            }


            /**
             * Perform the SrmMv() operation.
             */
            creationDone = fromFile.renameTo(toFile.getPath());

            if (creationDone)
            {
                log.debug("SrmMv: Request success!");
                explanation = "SURL moved with success";
                statusCode = TStatusCode.SRM_SUCCESS;
            }
            else
            {
                log.debug("SrmMv: Requests fails because the path is invalid.");
                explanation = "Invalid path";
                statusCode = TStatusCode.SRM_INVALID_PATH;
            }

        }
        else
        {
            if (!sourceExists)
            { // and it is a file
                log.debug("SrmMv: request fails because the source SURL does not exists!");
                explanation = "Source SURL does not exists!";
                statusCode = TStatusCode.SRM_INVALID_PATH;
            }
            else
            {
                if (!targetDirExists)
                {
                    log.debug("SrmMv: request fails because the target directory does not exitts.");
                    explanation = "Target directory does not exits!";
                    statusCode = TStatusCode.SRM_INVALID_PATH;
                }
                else
                {
                    if (targetFileExists)
                    {
                        log.debug("SrmMv: request fails because the target SURL exists.");
                        explanation = "Target SURL exists!";
                        statusCode = TStatusCode.SRM_DUPLICATION_ERROR;
                    }
                    else
                    {
                        log.debug("SrmMv request failure! That is a BUG!");
                        explanation = "That is a bug!";
                        statusCode = TStatusCode.SRM_INTERNAL_ERROR;
                    }
                }
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

        return buildStatus(statusCode, explanation);
    }
    
    
    private static TReturnStatus buildStatus(TStatusCode statusCode, String explaination)throws IllegalArgumentException, IllegalStateException
    {
        if(statusCode == null)
        {
            throw new IllegalArgumentException("Unable to build the status, null arguments: statusCode=" + statusCode);
        }
        try
        {
            return new TReturnStatus(statusCode, explaination);
        } catch(InvalidTReturnStatusAttributeException e)
        {
            // Never thrown
            throw new IllegalStateException("Unexpected InvalidTReturnStatusAttributeException "
                    + "in building TReturnStatus: " + e.getMessage());
        }
}
}
