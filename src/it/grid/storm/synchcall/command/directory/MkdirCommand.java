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
import it.grid.storm.config.Configuration;
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
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;

import java.util.Arrays;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class MkdirCommand extends DirectoryCommand implements Command {

    private static final String SRM_COMMAND = "SrmMkdir";
    private final NamespaceInterface namespace;

    public MkdirCommand() {
        namespace = NamespaceDirector.getNamespace();
    }

    /**
     * Method that provide SrmMkdir functionality.
     * 
     * @param inputData Contains information about input data for Mkdir request.
     * @return TReturnStatus Contains output data
     */
    public OutputData execute(InputData data) {

        log.debug("SrmMkdir: Start execution.");
        TReturnStatus returnStatus = null;
        MkdirInputData inputData = (MkdirInputData) data;
        MkdirOutputData outData = null;

        /**
         * Validate MkdirInputData. The check is done at this level to separate internal StoRM logic from xmlrpc
         * specific operation.
         */

        if ((inputData == null) || ((inputData != null) && (inputData.getSurl() == null))) {
            returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "Invalid parameter specified.");
            printRequestOutcome(returnStatus, inputData);
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        TSURL surl = inputData.getSurl();

        StoRI stori = null;
        if (!surl.isEmpty())
        {
            try
            {
                if (inputData instanceof IdentityInputData)
                {
                    stori = namespace.resolveStoRIbySURL(surl, ((IdentityInputData) inputData).getUser());
                }
                else
                {
                    stori = namespace.resolveStoRIbySURL(surl);
                }
            } catch(IllegalArgumentException e)
            {
                log.error("Unable to get surl's stori. IllegalArgumentException: " + e.getMessage());
                returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
                                                         "Unable to get StoRI for surl");
                printRequestOutcome(returnStatus, inputData);
                outData = new MkdirOutputData(returnStatus);
                return outData;
            } catch(NamespaceException e)
            {
                log.error("Unable to get surl's stori. NamespaceException: " + e.getMessage());
                boolean fitsSomewhere;
                try
                {
                    if (inputData instanceof IdentityInputData)
                    {
                        fitsSomewhere = namespace.isStfnFittingSomewhere(surl.toString(),
                                                                         ((IdentityInputData) inputData).getUser());
                    }
                    else
                    {
                        fitsSomewhere = namespace.isStfnFittingSomewhere(surl.toString());
                    }
                } catch(NamespaceException e1)
                {
                    log.error("Unable to check if surl fits somewhere. NamespaceException: " + e1.getMessage());
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
                                                             "Unable to check if surl fits somewhere");
                    printRequestOutcome(returnStatus, inputData);
                    outData = new MkdirOutputData(returnStatus);
                    return outData;
                }
                if (fitsSomewhere)
                {
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_NOT_SUPPORTED,
                                                             "Invalid SURL specified, the SAPath is incomplete!");
                    printRequestOutcome(returnStatus, inputData);
                }
                else
                {
                    returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
                                                             "Invalid SURL specified!");
                    printRequestOutcome(returnStatus, inputData);
                }
                outData = new MkdirOutputData(returnStatus);
                return outData;
            }
        }
        else
        {
            returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified!");
            printRequestOutcome(returnStatus, inputData);
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        TSpaceToken token = new SpaceHelper().getTokenFromStoRI(log, stori);
        SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

        boolean isSpaceAuthorized;
        if (inputData instanceof IdentityInputData)
        {
            isSpaceAuthorized = spaceAuth.authorize(((IdentityInputData) inputData).getUser(), SRMSpaceRequest.MD);
        }
        else
        {
            isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.MD);
        }
        if (!isSpaceAuthorized)
        {
            // User not authorized to perform RM request on the storage area
            log.debug("srmMkdir: User not authorized to perform srmMkdir request on the storage area: "
                    + token);
            returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                                     ": User not authorized to perform srmMkdir request on the storage area: "
                                                             + token);
            printRequestOutcome(returnStatus, inputData);
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        /**
         * 1.5.0 Path Authorization
         */
        AuthzDecision decision;
        if (inputData instanceof IdentityInputData)
        {
            decision = AuthzDirector.getPathAuthz().authorize(((IdentityInputData) inputData).getUser(), SRMFileRequest.MD, stori);
        }
        else
        {
            decision = AuthzDirector.getPathAuthz().authorizeAnonymous(SRMFileRequest.MD, stori.getStFN());
        }
        if (decision.equals(AuthzDecision.PERMIT))
        {
            log.debug("srmMkdir authorized for " + DataHelper.getRequestor(inputData) + " for directory = " + stori.getPFN());

            returnStatus = manageAuthorizedMKDIR(stori, data);
        }
        else
        {
            returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
            "User is not authorized to make a new directory");
        }
        printRequestOutcome(returnStatus, inputData);
        outData = new MkdirOutputData(returnStatus);
        return outData;
    }
    
    /**
     * Split PFN , recursive creation is not supported, as reported at page 16 of Srm v2.1 spec.
     * @param stori 
     * 
     * @param user VomsGridUser
     * @param stori StoRI
     * @param data 
     * @return TReturnStatus
     */
    private TReturnStatus manageAuthorizedMKDIR(StoRI stori, InputData data)
    {
        TReturnStatus returnStatus = createFolder(stori.getLocalFile());
        if (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS))
        {
            manageAcl(stori, data, returnStatus);
        }
        return returnStatus;
    }
    
    private TReturnStatus createFolder(LocalFile file)
    {
        LocalFile parent = file.getParentFile();
        if (parent != null)
        {
            log.debug("Mkdir : Parent of '" + file + "' exists");
            if (!file.exists())
            {
                if (file.mkdir())
                {
                    log.debug("SrmMkdir: Request success!");
                    return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
                                                     "Directory created with success");
                }
                else
                {
                    if (file.exists())
                    { /*
                       * Race condition, the directory has been created by another mkdir or
                       * a PtP running concurrently.
                       */
                        log.debug("SrmMkdir: Request fails because it specifies an existent file or directory.");
                        return CommandHelper.buildStatus(TStatusCode.SRM_DUPLICATION_ERROR,
                                                         "The given SURL identifies an existing file or directory");
                    }
                    else
                    {
                        log.debug("SrmMkdir: Request fails because the path is invalid.");
                        return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid path");
                    }
                }
            }
            else
            {
                if (file.isDirectory())
                {
                    log.debug("SrmMkdir: Request fails because it specifies an existent directory.");
                    return CommandHelper.buildStatus(TStatusCode.SRM_DUPLICATION_ERROR,
                                                     "Directory specified exists!");
                }
                else
                {
                    log.debug("SrmMkdir: Request fails because the path specified is an existent file.");
                    return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
                                                     "Path specified exists as a file");
                }
            }
        }
        else
        {
            log.debug("SrmMkdir: Request fails because it specifies an invalid parent directory.");
            return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
                                             "Parent directory does not exists. Recursive directory creation Not Allowed");
        }
    }
    
    private void manageAcl(StoRI stori, InputData inputData, TReturnStatus returnStatus)
    {
        FilesystemPermission permission;
        if (Configuration.getInstance().getEnableWritePermOnDirectory())
        {
            permission = FilesystemPermission.ListTraverseWrite;
        }
        else
        {
            permission = FilesystemPermission.ListTraverse;
        }
        if (inputData instanceof IdentityInputData)
        {
            setAcl(((IdentityInputData) inputData).getUser(), stori.getLocalFile(), stori.hasJustInTimeACLs(), permission, returnStatus);
            manageDefaultACL(stori, permission, returnStatus);
        }
        else
        {
            manageDefaultACL(stori, permission, returnStatus);
            setHttpsServiceAcl(stori.getLocalFile(), permission, returnStatus);
        }
    }
    
    private void setAcl(GridUserInterface user, LocalFile file, boolean hasJiTACL, FilesystemPermission permission, TReturnStatus returnStatus)
    {
        /*
         * Add Acces Control List (ACL) in directory created.
         * ACL allow user to read-write-list the new directory
         * Call wrapper to set ACL on file created.
         */
        log.debug("SrmMkdir: Adding ACL for directory created : '" + file + "'  " + "group:g_name:--x");

        /*
         * Set permission on directory
         * In case of local auth source enable also write
         */
        if (hasJiTACL)
        {
            // Jit Case
            // With JiT Model the ACL for directory is not needed.
        }
        else
        {
            try
            {
                if (user.getLocalUser() == null)
                {
                    log.warn("SrmMkdir: Unable to setting up the ACL. LocalUser il null!");
                    returnStatus.extendExplaination("Unable to setting up the ACL");
                }
                else
                {
                    try
                    {
                        AclManagerFSAndHTTPS.getInstance().grantGroupPermission(file, user.getLocalUser(), permission);
                    } catch(IllegalArgumentException e)
                    {
                        log.error("Unable to grant user permission on the created folder. IllegalArgumentException: "
                                + e.getMessage());
                        returnStatus.extendExplaination("Unable to grant group permission on the created folder");
                    }
                }
            } catch(CannotMapUserException e)
            {
                log.warn("SrmMkdir: Unable to setting up the ACL.CannotMapUserException: " + e.getMessage());
                returnStatus.extendExplaination("Unable to setting up the ACL");
            }
        }
    }

    private void manageDefaultACL(StoRI stori, FilesystemPermission permission, TReturnStatus returnStatus)
    {
        VirtualFSInterface vfs = stori.getVirtualFileSystem();
        DefaultACL dacl = vfs.getCapabilities().getDefaultACL();
        if ((dacl != null) && (!dacl.isEmpty()))
        {
            for (ACLEntry ace : dacl.getACL())
            {
                /*
                 * TODO ATTENTION: here we never set the acl contained in the ACE, we just add xr or
                 * xrw in respect to getEnableWritePermOnDirectory
                 */
                log.debug("Adding DefaultACL for the gid: " + ace.getGroupID() + " with permission: "
                        + ace.getFilePermissionString());
                LocalUser user = new LocalUser(ace.getGroupID(), ace.getGroupID());
                try
                {
                    AclManagerFSAndHTTPS.getInstance().grantGroupPermission(stori.getLocalFile(), user, permission);
                } catch(IllegalArgumentException e)
                {
                    log.error("Unable to grant group permission on the created folder to user " + user + " . IllegalArgumentException: "
                            + e.getMessage());
                    returnStatus.extendExplaination("Errors setting default acls");
                }
            }
        }
    }

    private void setHttpsServiceAcl(LocalFile file, FilesystemPermission permission, TReturnStatus returnStatus)
    {
        log.debug("SrmMkdir: Adding default ACL for directory created : '" + file + "'  " + permission);
        try
        {
            AclManagerFSAndHTTPS.getInstance().grantHttpsServiceGroupPermission(file, permission);
        } catch(IllegalArgumentException e)
        {
            log.error("Unable to grant user permission on the created folder. IllegalArgumentException: "
                    + e.getMessage());
            returnStatus.extendExplaination("Unable to grant group permission on the created folder");
        }
    }

    private void printRequestOutcome(TReturnStatus status, MkdirInputData inputData)
    {
        if(inputData != null)
        {
            if(inputData.getSurl() != null)
            {
                CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData, Arrays.asList(inputData.getSurl().toString()));    
            }
            else
            {
                CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
            }
                
        }
        else
        {
            CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
        }
    }
}
