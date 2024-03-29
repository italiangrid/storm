/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.synchcall.command.directory;

import static it.grid.storm.filesystem.FilesystemPermission.ListTraverse;
import static it.grid.storm.filesystem.FilesystemPermission.ListTraverseWrite;
import static it.grid.storm.srm.types.TStatusCode.SRM_AUTHORIZATION_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_DUPLICATION_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_INTERNAL_ERROR;
import static it.grid.storm.srm.types.TStatusCode.SRM_INVALID_PATH;
import static it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmAuthorizationFailure;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmFailure;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmInternalError;
import static it.grid.storm.synchcall.command.directory.MkdirException.srmInvalidPath;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.acl.AclManager;
import it.grid.storm.acl.AclManagerFS;
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
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.namespace.model.ACLEntry;
import it.grid.storm.namespace.model.DefaultACL;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.srm.types.SRMCommandException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DirectoryCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.MkdirInputData;
import it.grid.storm.synchcall.data.directory.MkdirOutputData;

class MkdirException extends SRMCommandException {

  private static final long serialVersionUID = 1L;

  private MkdirException(TStatusCode code, String message) {

    super(code, message);
  }

  public static MkdirException srmInvalidPath(String message) {
    return new MkdirException(SRM_INVALID_PATH, message);
  }

  public static MkdirException srmDuplicationError(String message) {
    return new MkdirException(SRM_DUPLICATION_ERROR, message);
  }

  public static MkdirException srmInternalError(String message) {
    return new MkdirException(SRM_INTERNAL_ERROR, message);
  }

  public static MkdirException srmFailure(String message) {
    return new MkdirException(SRM_FAILURE, message);
  }

  public static MkdirException srmAuthorizationFailure(String message) {
    return new MkdirException(SRM_AUTHORIZATION_FAILURE, message);
  }
}


/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 */

public class MkdirCommand extends DirectoryCommand implements Command {

  public static final Logger log = LoggerFactory.getLogger(MkdirCommand.class);

  private static final String SRM_COMMAND = "SrmMkdir";

  private final NamespaceInterface namespace;
  private final Configuration configuration;
  private final AclManager aclManager;

  public MkdirCommand() {

    namespace = NamespaceDirector.getNamespace();
    configuration = Configuration.getInstance();
    aclManager = AclManagerFS.getInstance();
  }

  /**
   * Method that provide SrmMkdir functionality.
   * 
   * @param inputData Contains information about input data for Mkdir request.
   * @return MkdirOutputData Contains output data
   */
  public OutputData execute(InputData data) {

    MkdirOutputData outputData = null;
    log.debug("SrmMkdir: Start execution.");
    checkInputData(data);
    outputData = doMkdir((MkdirInputData) data);
    log.debug("srmMkdir return status: {}", outputData.getStatus());
    printRequestOutcome(outputData.getStatus(), (MkdirInputData) data);
    return outputData;
  }

  private MkdirOutputData doMkdir(MkdirInputData data) {

    TReturnStatus returnStatus = null;
    try {
      TSURL surl = getSURL(data);
      GridUserInterface user = getUser(data);
      StoRI stori = resolveStoRI(surl, user);
      checkUserAuthorization(stori, user);
      log.debug("srmMkdir authorized for {} for directory = {}", userToString(user),
          stori.getPFN());
      returnStatus = createFolder(stori.getLocalFile());
      if (returnStatus.isSRM_SUCCESS()) {
        log.debug("srmMkdir: updating used space info ...");
        increaseUsedSpaceInfo(stori.getLocalFile());
        log.debug("srmMkdir: managing ACL ...");
        manageAcl(stori, user);
      }
    } catch (MkdirException e) {
      log.error("srmMkdir: {}", e.getMessage());
      returnStatus = e.getReturnStatus();
    }
    log.debug("srmMkdir return status: {}", returnStatus);
    return new MkdirOutputData(returnStatus);
  }

  private TReturnStatus createFolder(LocalFile file) {

    LocalFile parent = file.getParentFile();
    log.debug("srmMkdir: Parent directory is {}.", parent);
    if (parent != null) {
      if (!parent.exists()) {
        return new TReturnStatus(SRM_INVALID_PATH,
            "Parent directory does not exists. Recursive directory creation Not Allowed");
      }
      log.debug("srmMkdir: Parent directory {} exists.", parent);
    } else {
      srmInvalidPath("Null parent directory");
    }
    if (!file.mkdir()) {
      if (file.isDirectory()) {
        log.debug("srmMkdir: The specified path is an existent directory.");
        return new TReturnStatus(SRM_DUPLICATION_ERROR, "Path exists and it's a directory.");
      }
      log.debug("srmMkdir: The specified path is an existent file.");
      return new TReturnStatus(SRM_INVALID_PATH, "Path specified exists as a file.");
    }
    log.debug("SrmMkdir: Request success!");
    return new TReturnStatus(SRM_SUCCESS, "Directory created with success");
  }

  private void checkInputData(InputData data) {

    if (data == null) {
      throw new IllegalArgumentException("Invalid input data: NULL");
    }
    if (!(data instanceof MkdirInputData)) {
      throw new IllegalArgumentException("Invalid input data type");
    }
  }

  private TSURL getSURL(MkdirInputData data) throws MkdirException {

    TSURL surl = data.getSurl();
    if (surl == null) {
      throw srmFailure("SURL specified is NULL");
    }
    if (surl.isEmpty()) {
      throw srmFailure("SURL specified is empty");
    }
    return surl;
  }

  private StoRI resolveStoRI(TSURL surl, GridUserInterface user) throws MkdirException {

    try {
      return namespace.resolveStoRIbySURL(surl, user);
    } catch (UnapprochableSurlException e) {
      throw srmAuthorizationFailure(e.getMessage());
    } catch (IllegalArgumentException | NamespaceException e) {
      throw srmInternalError(e.getMessage());
    } catch (InvalidSURLException e) {
      throw srmInvalidPath(e.getMessage());
    }
  }

  private boolean isAnonymous(GridUserInterface user) {

    return (user == null);
  }

  private void checkUserAuthorization(StoRI stori, GridUserInterface user) throws MkdirException {

    TSpaceToken token = stori.getVirtualFileSystem().getSpaceToken();
    SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

    boolean isSpaceAuthorized;
    if (isAnonymous(user)) {
      isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.MD);
    } else {
      isSpaceAuthorized = spaceAuth.authorize(user, SRMSpaceRequest.MD);
    }
    if (!isSpaceAuthorized) {
      String msg =
          format("User not authorized to perform srmMkdir request on the storage area: %s", token);
      log.debug("srmMkdir:{}", msg);
      throw srmAuthorizationFailure(msg);
    }

    AuthzDecision decision;
    if (isAnonymous(user)) {
      decision =
          AuthzDirector.getPathAuthz().authorizeAnonymous(SRMFileRequest.MD, stori.getStFN());
    } else {
      decision = AuthzDirector.getPathAuthz().authorize(user, SRMFileRequest.MD, stori);
    }
    if (!decision.equals(AuthzDecision.PERMIT)) {
      String msg = "User is not authorized to make a new directory";
      log.debug("srmMkdir: {}", msg);
      throw srmAuthorizationFailure(msg);
    }
  }

  private String userToString(GridUserInterface user) {

    return isAnonymous(user) ? "anonymous" : user.getDn();
  }

  private GridUserInterface getUser(InputData data) {

    if (data instanceof IdentityInputData) {
      return ((IdentityInputData) data).getUser();
    }
    return null;
  }

  private boolean increaseUsedSpaceInfo(LocalFile dir) {

    try {
      return namespace.resolveVFSbyLocalFile(dir).increaseUsedSpace(dir.getSize());
    } catch (NamespaceException e) {
      log.error("srmMkdir: Unable to increase used space info [{}]", e.getMessage());
      return false;
    }
  }

  private void manageAcl(StoRI stori, GridUserInterface user) {

    FilesystemPermission permission =
        configuration.getEnableWritePermOnDirectory() ? ListTraverseWrite : ListTraverse;

    try {
      if (isAnonymous(user)) {
        manageDefaultACL(stori.getLocalFile(), permission);
        setHttpsServiceAcl(stori.getLocalFile(), permission);
      } else {
        setAcl(user, stori.getLocalFile(), stori.hasJustInTimeACLs(), permission);
        manageDefaultACL(stori.getLocalFile(), permission);
      }
    } catch (NamespaceException | CannotMapUserException e) {
      log.error("srmMkdir: Unable to set ACL [{}]", e.getMessage());
    }
  }

  private void setAcl(GridUserInterface user, LocalFile file, boolean hasJiTACL,
      FilesystemPermission permission) throws CannotMapUserException {

    /*
     * Add Acces Control List (ACL) in directory created. ACL allow user to read-write-list the new
     * directory Call wrapper to set ACL on file created.
     */
    log.debug("SrmMkdir: Adding ACL for directory '{}' group:g_name:--x", file);

    /*
     * Set permission on directory In case of local auth source enable also write
     */
    if (hasJiTACL) {
      // Jit Case: with JiT Model the ACL for directory is not needed.
      return;
    }
    LocalUser localUser = user.getLocalUser();
    if (localUser == null) {
      log.warn("SrmMkdir: Unable to setting up the ACL. LocalUser is null!");
      throw new CannotMapUserException("ACL setup error. Invalid local user: null.");
    }
    aclManager.grantGroupPermission(file, localUser, permission);
  }

  private void manageDefaultACL(LocalFile dir, FilesystemPermission permission)
      throws NamespaceException {

    VirtualFS vfs = namespace.resolveVFSbyLocalFile(dir);
    DefaultACL dacl = vfs.getCapabilities().getDefaultACL();
    if ((dacl == null) || (dacl.isEmpty())) {
      log.debug("srmMkdir: default acl NULL or empty");
      return;
    }
    for (ACLEntry ace : dacl.getACL()) {
      log.debug("Adding DefaultACL for the gid: {} with permission: {}", ace.getGroupID(),
          ace.getFilePermissionString());

      LocalUser user = new LocalUser(ace.getGroupID(), ace.getGroupID());
      aclManager.grantGroupPermission(dir, user, permission);
    }
  }

  private void setHttpsServiceAcl(LocalFile file, FilesystemPermission permission) {

    log.debug("SrmMkdir: Adding default ACL for directory {}: {}", file, permission);
    aclManager.grantHttpsServiceGroupPermission(file, permission);
  }

  private void printRequestOutcome(TReturnStatus status, MkdirInputData inputData) {

    if (inputData != null) {
      if (inputData.getSurl() != null) {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
            Lists.newArrayList(inputData.getSurl().toString()));
      } else {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
      }
    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }
}
