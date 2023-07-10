/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.directory;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.SRMCommandException;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.directory.RmInputData;
import it.grid.storm.synchcall.data.directory.RmOutputData;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RmException extends SRMCommandException {

  private static final long serialVersionUID = 1L;

  public RmException(TStatusCode code, String message) {

    super(code, message);
  }
}

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008 Company: INFN-CNAF and
 * ICTP/EGRID project
 *
 * @author lucamag
 * @date May 27, 2008
 */
public class RmCommand implements Command {

  private static final String SRM_COMMAND = "srmRm";
  private static final Logger log = LoggerFactory.getLogger(RmCommand.class);
  private final NamespaceInterface namespace;

  public RmCommand() {

    namespace = NamespaceDirector.getNamespace();
  }

  private void checkInputData(InputData data) throws IllegalArgumentException {

    if (data == null) {
      throw new IllegalArgumentException("Invalid input data: NULL");
    }
    if (!(data instanceof RmInputData)) {
      throw new IllegalArgumentException("Invalid input data type");
    }
  }

  private List<TSURL> getSurlArray(RmInputData data) throws RmException {

    if (data.getSurlArray() == null) {
      throw new RmException(TStatusCode.SRM_FAILURE, "Invalid SURL array: NULL");
    }
    if (data.getSurlArray().size() == 0) {
      throw new RmException(TStatusCode.SRM_FAILURE, "Invalid SURL array specified");
    }

    return data.getSurlArray().getArrayList();
  }

  /**
   * Method that provide SrmRm functionality.
   *
   * @param inputData Contains information about input data for rm request.
   * @return RmOutputData Contains output data
   */
  public OutputData execute(InputData data) {

    RmOutputData outputData = null;
    log.debug("SrmRm: Start execution.");
    checkInputData(data);
    outputData = doRm((RmInputData) data);
    log.debug("SrmRm return status: {}", outputData.getStatus());
    printRequestOutcome(outputData.getStatus(), (RmInputData) data);
    return outputData;
  }

  private RmOutputData doRm(RmInputData data) {

    RmOutputData outputData = null;
    TReturnStatus globalStatus = null;
    GridUserInterface user = getUser(data);
    List<TSURL> surls = null;
    boolean atLeastOneSuccess = false;
    boolean atLeastOneFailure = false;
    boolean allUnauthorized = true;
    ArrayOfTSURLReturnStatus arrayOfFileStatus = new ArrayOfTSURLReturnStatus();

    try {
      surls = getSurlArray(data);
    } catch (RmException e) {
      log.error("srmRm: {}", e.getMessage());
      outputData = new RmOutputData(e.getReturnStatus());
      return outputData;
    }
    log.debug("srmRm: Received srmRm on {} surls", surls.size());

    for (TSURL surl : surls) {

      TReturnStatus returnStatus = null;
      try {
        returnStatus = removeFile(surl, user, data);
      } catch (RmException e) {
        log.info("srmRm failed: {}", e.getMessage());
        returnStatus = e.getReturnStatus();
      } finally {
        arrayOfFileStatus.addTSurlReturnStatus(new TSURLReturnStatus(surl, returnStatus));
        atLeastOneSuccess |= returnStatus.isSRM_SUCCESS();
        atLeastOneFailure |= !returnStatus.isSRM_SUCCESS();
        allUnauthorized &=
            returnStatus.getStatusCode().equals(TStatusCode.SRM_AUTHORIZATION_FAILURE);
        printSurlOutcome(returnStatus, data, surl);
      }
    }
    globalStatus = computeGlobalStatus(atLeastOneSuccess, atLeastOneFailure, allUnauthorized);
    outputData = new RmOutputData(globalStatus, arrayOfFileStatus);

    return outputData;
  }

  private TReturnStatus removeFile(TSURL surl, GridUserInterface user, RmInputData inputData)
      throws RmException {

    TReturnStatus returnStatus = null;
    StoRI stori = resolveStoRI(surl, user);
    checkUserAuthorization(stori, user);
    log.debug("srmRm authorized for {} for directory = {}", userToString(user), stori.getPFN());

    SURLStatusManager manager = SURLStatusManagerFactory.newSURLStatusManager();

    LocalFile localFile = stori.getLocalFile();

    if (!localFile.exists()) {

      manager.abortAllGetRequestsForSURL(null, surl, "File does not exist on disk.");
      manager.abortAllPutRequestsForSURL(null, surl, "File does not exist on disk.");
      return new TReturnStatus(TStatusCode.SRM_INVALID_PATH, "File does not exist");
    }

    if (localFile.isDirectory()) {
      return new TReturnStatus(
          TStatusCode.SRM_INVALID_PATH, "The specified file is a directory. Not removed");
    }

    // Get file size before it's removed
    long fileSize = localFile.getSize();

    if (!localFile.delete()) {
      log.warn("srmRm: File not removed!");
      return new TReturnStatus(
          TStatusCode.SRM_AUTHORIZATION_FAILURE, "File not removed, permission denied.");
    }

    manager.abortAllGetRequestsForSURL(null, surl, "File has been removed.");
    manager.abortAllPutRequestsForSURL(null, surl, "File has been removed.");

    returnStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS, "File removed");

    try {
      NamespaceDirector.getNamespace().resolveVFSbyLocalFile(localFile).decreaseUsedSpace(fileSize);
    } catch (NamespaceException e) {
      log.error(e.getMessage());
      returnStatus.extendExplaination("Unable to decrease used space: " + e.getMessage());
    }

    return returnStatus;
  }

  private TReturnStatus computeGlobalStatus(
      boolean atLeastOneSuccess, boolean atLeastOneFailure, boolean allUnauthorized) {

    if (atLeastOneSuccess && !atLeastOneFailure) {
      return new TReturnStatus(TStatusCode.SRM_SUCCESS, "All files removed");
    }
    if (atLeastOneSuccess) {
      return new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Some files were not removed");
    }
    if (allUnauthorized) {
      return new TReturnStatus(
          TStatusCode.SRM_AUTHORIZATION_FAILURE, "User is not authorized to remove any files");
    }
    return new TReturnStatus(TStatusCode.SRM_FAILURE, "No files removed");
  }

  private boolean isAnonymous(GridUserInterface user) {

    return (user == null);
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

  private StoRI resolveStoRI(TSURL surl, GridUserInterface user) throws RmException {

    try {
      return namespace.resolveStoRIbySURL(surl, user);
    } catch (UnapprochableSurlException e) {
      log.error(e.getMessage());
      throw new RmException(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage());
    } catch (NamespaceException e) {
      log.error(e.getMessage());
      throw new RmException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
    } catch (InvalidSURLException e) {
      log.error(e.getMessage());
      throw new RmException(TStatusCode.SRM_INVALID_PATH, e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error(e.getMessage());
      throw new RmException(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
    }
  }

  private void checkUserAuthorization(StoRI stori, GridUserInterface user) throws RmException {

    TSpaceToken token = stori.getVirtualFileSystem().getSpaceToken();
    SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

    boolean isSpaceAuthorized;
    if (isAnonymous(user)) {
      isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.RM);
    } else {
      isSpaceAuthorized = spaceAuth.authorize(user, SRMSpaceRequest.RM);
    }
    if (!isSpaceAuthorized) {
      log.debug("srmRm: User not authorized to perform srmRm on SA: {}", token);
      throw new RmException(
          TStatusCode.SRM_AUTHORIZATION_FAILURE,
          "User not authorized to perform srmRm request on the storage area");
    }
    AuthzDecision decision;
    if (isAnonymous(user)) {
      decision =
          AuthzDirector.getPathAuthz().authorizeAnonymous(SRMFileRequest.RM, stori.getStFN());
    } else {
      decision = AuthzDirector.getPathAuthz().authorize(user, SRMFileRequest.RM, stori);
    }
    if (!decision.equals(AuthzDecision.PERMIT)) {
      log.debug("srmRm: User is not authorized to delete a file");
      throw new RmException(
          TStatusCode.SRM_AUTHORIZATION_FAILURE, "User is not authorized to delete a file");
    }
  }

  private void printSurlOutcome(TReturnStatus status, RmInputData inputData, TSURL surl) {

    CommandHelper.printSurlOutcome(SRM_COMMAND, log, status, inputData, surl);
  }

  private void printRequestOutcome(TReturnStatus status, RmInputData inputData) {

    if (inputData != null) {
      if (inputData.getSurlArray() != null) {
        CommandHelper.printRequestOutcome(
            SRM_COMMAND, log, status, inputData, inputData.getSurlArray().asStringList());
      } else {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
      }
    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }
}
