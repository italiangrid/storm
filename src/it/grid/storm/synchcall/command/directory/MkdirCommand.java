package it.grid.storm.synchcall.command.directory;

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
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;

import java.util.List;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class MkdirCommand extends DirectoryCommand implements Command {

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
            // Something was wrong..
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "Invalid parameter specified.");
                log.error("srmMkdir: <> [SURL=] Request failed with [status: " + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmMkdir: <> [SURL=] Request failed. Error creating returnStatus " + ex1);
            }
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        /**
         * Check if GridUser in MkdirInputData is not null, otherwise return with an error message.
         */
        GridUserInterface guser = inputData.getUser();
        if (guser == null) {
            // Something was wrong..
            log.info("srmMkdir: Unable to get user credential. ");
            try {
                returnStatus =
                        new TReturnStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE, "Unable to get user credential!");
                log.error("srmMkdir: <> [SURL=] Request failed with [status: " + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmMkdir: <> [SURL=] Request failed. Error creating returnStatus " + ex1);
            }
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        // Get SURL from input structure.
        TSURL surl = inputData.getSurl();

        // Create StoRI from SURL
        StoRI stori = null;

        if (!surl.isEmpty()) {
            // Building StoRI representation of SURL within the request.
            try {
                stori = namespace.resolveStoRIbySURL(surl, guser);
            } catch (NamespaceException ex) {
                // Something was wrong ....
                log.debug("srmMkdir: <" + guser + "> Unable to build StoRI by SURL: " + ex);
                try {
                    // log.info("srmMkdir: >>>> stfnpath: "+ newsurl.getStFN());
                    if (namespace.isStfnFittingSomewhere(surl.toString(), guser)) {
                        returnStatus =
                                new TReturnStatus(TStatusCode.SRM_NOT_SUPPORTED,
                                                  "Invalid SURL specified, the SAPath is incomplete!");
                        log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl + "'] failed with: [status:"
                                + returnStatus.toString() + "]");
                    } else {
                        returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified!");
                        log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl + "'] failed with: [status:"
                                + returnStatus.toString() + "]");
                    }

                } catch (InvalidTReturnStatusAttributeException ex1) {
                    log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl
                            + "'] failed. Error creating returnStatus " + ex1);
                } catch (NamespaceException e) {
                    log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl
                            + "'] failed. Error checking if SURL fits in SAPath" + e);
                }
                outData = new MkdirOutputData(returnStatus);
                return outData;
            }
        } else {
            // Something was wrong..
            try {
                returnStatus = new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL specified!");
                log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl + "'] failed with: [status:"
                        + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl
                        + "'] failed. Error creating returnStatus " + ex1);
            }
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        // Get ACL mode from StoRI (AoT or JiT)
        boolean hasJiTACL = stori.hasJustInTimeACLs();

        LocalFile file = stori.getLocalFile();

        /**
         * Construction of AuthZ request
         */

        /**
         * From version 1.4 Add the control for Storage Area using the new authz for space component.
         */

        SpaceHelper sp = new SpaceHelper();
        TSpaceToken token = sp.getTokenFromStoRI(log, stori);
        SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

        if (!(spaceAuth.authorize(guser, SRMSpaceRequest.MD))) {
            // User not authorized to perform RM request on the storage area
            log.debug("srmMkdir: User not authorized to perform srmMkdir request on the storage area: " + token);
            try {
                returnStatus =
                        new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                          ": User not authorized to perform srmMkdir request on the storage area: "
                                                  + token);
                log.error("srmMkdir: <> Request for [SURL:" + surl + "] failed with [status: "
                        + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmMkdir: <> Request for [SURL:" + surl + "] failed. Error creating returnStatus " + ex1);
            }
            outData = new MkdirOutputData(returnStatus);
            return outData;
        }

        // AuthorizationDecision mkdirAuth = AuthorizationCollector.getInstance().canMakeDirectory(guser, stori);
        /**
         * 1.5.0 Path Authorization
         */
        AuthzDecision mkdirAuthz = AuthzDirector.getPathAuthz().authorize(guser, SRMFileRequest.MD, stori);
        if (mkdirAuthz.equals(AuthzDecision.PERMIT)) {
            log.debug("srmMkdir authorized for " + guser + " for directory = " + stori.getPFN());

            // ### DO THE WORK !
            returnStatus = manageAuthorizedMKDIR(guser, file, hasJiTACL);

            if (returnStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {

                // #!! Created the directory with SUCCESS!
                log.info("srmMkdir: <" + guser + "> Request for [SURL:'" + surl + "'] successfully done with: [status:"
                        + returnStatus.toString() + "]");

                // ///////////////////////
                // //Manage Default ACL
                // ///////////////////////

                FilesystemPermission fp = null;
                if (Configuration.getInstance().getEnableWritePermOnDirectory()) {
                    fp = FilesystemPermission.ListTraverseWrite;
                } else {
                    fp = FilesystemPermission.ListTraverse;
                }

                VirtualFSInterface vfs = stori.getVirtualFileSystem();
                DefaultACL dacl = null;
                try {
                    dacl = vfs.getCapabilities().getDefaultACL();
                } catch (NamespaceException e) {
                    log.error("Unable to retrieve the Default ACL: " + e.getMessage());
                }
                if ((dacl != null) && (!dacl.isEmpty())) {
                    // There are Default ACLs to set into the file
                    List<ACLEntry> dacl_list = dacl.getACL();
                    for (ACLEntry ace : dacl_list) {
                        log.debug("Adding DefaultACL for the gid: " + ace.getGroupID() + " with permission: "
                                + ace.getFilePermissionString());
                        LocalUser u = new LocalUser(ace.getGroupID(), ace.getGroupID());
                        try {
                            file.grantGroupPermission(u, fp);
                        } catch (CannotMapUserException e) {
                            // TODO Auto-generated catch block
                            log.error("Error adding default ACL for the gid: " + ace.getGroupID()
                                    + " with permission: " + ace.getFilePermissionString());
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl + "'] failed with: [status:"
                        + returnStatus.toString() + "]");
            }

        } else {
            try {
                returnStatus =
                        new TReturnStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
                                          "User is not authorized to make a new directory");
                log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl + "'] failed with: [status:"
                        + returnStatus.toString() + "]");
            } catch (InvalidTReturnStatusAttributeException ex1) {
                log.error("srmMkdir: <" + guser + "> Request for [SURL:'" + surl
                        + "'] failed. Error creating returnStatus " + ex1);
            }
        }
        outData = new MkdirOutputData(returnStatus);
        return outData;
    }

    /**
     * Split PFN , recursive creation is not supported, as reported at page 16 of Srm v2.1 spec.
     * 
     * @param user VomsGridUser
     * @param stori StoRI
     * @return TReturnStatus
     */
    private TReturnStatus manageAuthorizedMKDIR(GridUserInterface user, LocalFile file, boolean hasJiTACL) {
        TReturnStatus returnStatus = null;
        boolean creationDone;

        boolean failure = false;
        String explanation = "";
        TStatusCode statusCode = TStatusCode.EMPTY;

        LocalFile parent = file.getParentFile();
        if (parent != null) {
            log.debug("Mkdir : Parent of '" + file + "' exists");
            if (!file.exists()) {

                creationDone = file.mkdir();

                if (creationDone) {
                    
                    log.debug("SrmMkdir: Request success!");
                    failure = false;
                    explanation = "Directory created with success";
                    statusCode = TStatusCode.SRM_SUCCESS;
                    
                } else {
                    
                    if (file.exists()) { // Race condition, the directory has been created by another mkdir or
                                         // a PtP running concurrently.
                        log.debug("SrmMkdir: Request fails because it specifies an existent file or directory.");
                        failure = true;
                        explanation = "The given SURL identifies an existing file or directory";
                        statusCode = TStatusCode.SRM_DUPLICATION_ERROR;
                        
                    } else {
                        
                        log.debug("SrmMkdir: Request fails because the path is invalid.");
                        failure = true;
                        explanation = "Invalid path";
                        statusCode = TStatusCode.SRM_INVALID_PATH;
                        
                    }
                }
            } else { // Directory exists!
                if (!file.isDirectory()) { // and it is a file
                    log.debug("SrmMkdir: Request fails because the path specified is an existent file.");
                    failure = true;
                    explanation = "Path specified exists as a file";
                    statusCode = TStatusCode.SRM_INVALID_PATH;
                }
                if (file.isDirectory()) {
                    log.debug("SrmMkdir: Request fails because it specifies an existent directory.");
                    failure = true;
                    explanation = "Directory specified exists!";
                    statusCode = TStatusCode.SRM_DUPLICATION_ERROR;
                }
            }
        } else {
            log.debug("SrmMkdir: Request fails because it specifies an invalid parent directory.");
            failure = true;
            explanation = "Parent directory does not exists. Recursive directory creation Not Allowed";
            statusCode = TStatusCode.SRM_INVALID_PATH;
        }

        // Check if failure occour before try to set up ACL.
        if (!failure) {
            // Add Acces Control List (ACL) in directory created.
            // ACL allow user to read-write-list the new directory
            // Call wrapper to set ACL on file created.
            log.debug("SrmMkdir: Adding ACL for directory created : '" + file + "'  " + "group:g_name:--x");

            // Set permission on directory
            // In case of local auth source enable also write
            FilesystemPermission fpLIST = null;
            if (Configuration.getInstance().getEnableWritePermOnDirectory()) {
                fpLIST = FilesystemPermission.ListTraverseWrite;
            } else {
                fpLIST = FilesystemPermission.ListTraverse;
            }

            // Check if Jit or AoT
            if (hasJiTACL) {
                // Jit Case
                // With JiT Model the ACL for directory is not needed.
            } else {
                // AoT Case
                try {
                    file.grantGroupPermission(user.getLocalUser(), fpLIST);
                } catch (CannotMapUserException ex5) {
                    log.info("SrmMkdir: Unable to setting up the ACL " + ex5);
                    failure = true;
                    explanation = explanation + " [ Unable to setting up the ACL ]";
                }
            }

            if (failure) {
                // Rollback ...
                log.warn("Error while trying to set ACL for group of requestor.");
                /**
                 * @todo: Rollback of failure.
                 */
            }

        } // failure

        // Setting of Return Status
        try {
            returnStatus = new TReturnStatus(statusCode, explanation);
        } catch (InvalidTReturnStatusAttributeException e) {
            log.error("SrmMkdir: Error creating returnStatus " + e);
        }

        return returnStatus;
    }
}
