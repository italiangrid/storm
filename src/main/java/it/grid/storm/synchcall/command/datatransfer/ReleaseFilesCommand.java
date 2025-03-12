/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.ea.StormEA;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferOutputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.ManageFileTransferRequestInputData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReleaseFilesCommand extends DataTransferCommand implements Command {

  private static final Logger log = LoggerFactory.getLogger(ReleaseFilesCommand.class);

  private static final String SRM_COMMAND = "srmReleaseFiles";

  private static final EnumSet<TStatusCode> PINNED_OR_SUCCESS =
      EnumSet.of(TStatusCode.SRM_SUCCESS, TStatusCode.SRM_FILE_PINNED);

  public ReleaseFilesCommand() {

  }

  public TRequestToken getTokenFromInputData(InputData inputData) {

    if (inputDataHasToken(inputData)) {
      return ((ManageFileTransferRequestInputData) inputData).getRequestToken();
    }

    return null;
  }

  public List<TSURL> getSURLListFromInputData(InputData inputData) {

    if (inputDataHasSURLArray(inputData)) {
      return ((ManageFileTransferFilesInputData) inputData).getArrayOfSURLs().getArrayList();
    }
    return null;
  }

  private List<String> toStringList(List<TSURL> surls) {

    List<String> ls = new ArrayList<String>();
    for (TSURL s : surls)
      ls.add(s.getSURLString());
    return ls;
  }

  public boolean validInputData(InputData inputData) {

    return (inputData instanceof ManageFileTransferRequestFilesInputData)
        || (inputData instanceof ManageFileTransferFilesInputData)
        || (inputData instanceof ManageFileTransferRequestInputData);
  }

  public boolean inputDataHasToken(InputData inputData) {

    return (inputData instanceof ManageFileTransferRequestFilesInputData)
        || (inputData instanceof ManageFileTransferRequestInputData);
  }

  public boolean inputDataHasSURLArray(InputData inputData) {

    return (inputData instanceof ManageFileTransferRequestFilesInputData)
        || (inputData instanceof ManageFileTransferFilesInputData);
  }

  public OutputData handleNullInputData(InputData inputData) {

    log.error("ReleaseFiles: Invalid input parameters specified: inputData=" + inputData);

    ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(
        CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Empty request parametes"));

    logRequestOutcome(outputData.getReturnStatus(), inputData);

    return outputData;

  }

  public OutputData handleInvalidRequest(InputData in, IllegalArgumentException e) {

    log.warn(e.getMessage(), e);

    ManageFileTransferOutputData outputData = new ManageFileTransferOutputData(
        CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "Internal error: " + e.getMessage()));

    logRequestOutcome(outputData.getReturnStatus(), in);

    return outputData;
  }

  public OutputData handleNoSURLsFound(InputData in) {

    log.info("No SURLs found in the DB. Request failed");

    TReturnStatus returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
        "No SURLs found matching user, input request token or list of SURLs.");

    logRequestOutcome(returnStatus, in);

    return new ManageFileTransferOutputData(returnStatus);

  }

  private boolean isAnonymousRequest(InputData inputData) {

    return !(inputData instanceof IdentityInputData);
  }

  /**
   * Does a ReleaseFiles. Used to release pins on the previously requested "copies" (or "state") of
   * the SURL. This function normally follows a srmPrepareToGet or srmBringOnline functions.
   */
  public OutputData execute(InputData inputData) {

    log.debug("Started ReleaseFiles");

    if (inputData == null) {

      return handleNullInputData(inputData);

    }

    if (!validInputData(inputData)) {
      throw new IllegalArgumentException(
          "Release files: invalid argument type: " + inputData.getClass());
    }

    Map<TSURL, TReturnStatus> surlStatuses = null;

    SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();

    TRequestToken token = getTokenFromInputData(inputData);

    GridUserInterface user = null;

    if (!isAnonymousRequest(inputData)) {
      user = ((IdentityInputData) inputData).getUser();
    }

    try {

      if (token == null) {
        surlStatuses = checker.getPinnedSURLsForUser(user, getSURLListFromInputData(inputData));

      } else {

        surlStatuses = checker.getSURLStatuses(user, getTokenFromInputData(inputData),
            getSURLListFromInputData(inputData));
      }

    } catch (AuthzException e) {
      return handleAuthzError(inputData, e);
    } catch (IllegalArgumentException e) {

      return handleInvalidRequest(inputData, e);

    }

    if (surlStatuses.isEmpty()) {
      return handleNoSURLsFound(inputData);
    }

    ArrayOfTSURLReturnStatus surlReturnStatuses =
        prepareSurlsReturnStatus(surlStatuses, getSURLListFromInputData(inputData));

    List<TSURL> surlToRelease = extractSurlToRelease(surlReturnStatuses);

    if (surlToRelease.isEmpty()) {
      TReturnStatus returnStatus =
          CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "No files released");

      logRequestOutcome(returnStatus, inputData);

      return new ManageFileTransferOutputData(returnStatus, surlReturnStatuses);
    }

    if (token == null) {
      checker.releaseSURLs(user, surlToRelease);
    } else {
      checker.releaseSURLs(token, surlToRelease);
    }

    removePinneExtendedAttribute(surlToRelease);

    TReturnStatus returnStatus = buildStatus(inputData, surlReturnStatuses);

    logRequestOutcome(returnStatus, inputData);

    return new ManageFileTransferOutputData(returnStatus, surlReturnStatuses);
  }

  private OutputData handleAuthzError(InputData inputData, AuthzException e) {

    log.error(e.getMessage());

    TReturnStatus returnStatus =
        CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage());

    logRequestOutcome(returnStatus, inputData);

    return new ManageFileTransferOutputData(returnStatus);
  }

  private TReturnStatus buildStatus(InputData inputData,
      ArrayOfTSURLReturnStatus surlReturnStatuses) {

    boolean atLeastOneReleased = false;
    boolean atLeastOneFailure = false;

    for (TSURLReturnStatus returnStatus : surlReturnStatuses.getArray()) {

      printSurlOutcome(returnStatus, inputData);

      if (returnStatus.getStatus().getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {

        atLeastOneReleased = true;

      } else {

        atLeastOneFailure = true;

      }
    }

    if (atLeastOneReleased) {
      if (atLeastOneFailure) {
        return CommandHelper.buildStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
            "Check files status for details");
      } else {
        return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS, "Files released");
      }
    } else {
      return CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "No files released");
    }
  }

  private ArrayOfTSURLReturnStatus prepareSurlsReturnStatus(Map<TSURL, TReturnStatus> statuses,
      List<TSURL> surlsInRequest) {

    ArrayOfTSURLReturnStatus surlReturnStatuses = new ArrayOfTSURLReturnStatus(statuses.size());

    Collection<TSURL> surls;

    if (surlsInRequest != null) {
      surls = surlsInRequest;
    } else {
      surls = statuses.keySet();
    }

    for (TSURL surl : surls) {

      TReturnStatus returnStatus;
      TReturnStatus rs = statuses.get(surl);

      if (rs.getStatusCode() != null) {
        returnStatus = prepareStatus(rs.getStatusCode());

      } else {
        returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid SURL");
      }

      surlReturnStatuses.addTSurlReturnStatus(CommandHelper.buildStatus(surl, returnStatus));
    }

    return surlReturnStatuses;
  }

  private TReturnStatus prepareStatus(TStatusCode status) {

    if (PINNED_OR_SUCCESS.contains(status)) {
      return CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS, "Released");
    }

    return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH,
        "Not released because it is not pinned");
  }

  private List<TSURL> extractSurlToRelease(ArrayOfTSURLReturnStatus surlReturnStatuses) {

    LinkedList<TSURL> surlToRelease = new LinkedList<TSURL>();

    for (TSURLReturnStatus returnStatus : surlReturnStatuses.getArray()) {

      if (TStatusCode.SRM_SUCCESS.equals(returnStatus.getStatus().getStatusCode())) {

        surlToRelease.add(returnStatus.getSurl());
      }
    }

    return surlToRelease;
  }

  /**
   * Removes the Extended Attribute "pinned" from SURLs belonging to a filesystem with tape support.
   * 
   * @param surlToRelease
   */
  private void removePinneExtendedAttribute(List<TSURL> surlToRelease) {

    for (TSURL surl : surlToRelease) {

      StoRI stori;

      try {

        stori = Namespace.getInstance().resolveStoRIbySURL(surl);

      } catch (Throwable e) {

        log.warn(String.format("UNEXPECTED: Unable to build a stori for surl %s: %s", surl,
            e.getMessage()));

        continue;
      }

      if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {

        StormEA.removePinned(stori.getAbsolutePath());
      }
    }
  }

  private void printSurlOutcome(TSURLReturnStatus surlStatus, InputData inputData) {

    CommandHelper.printSurlOutcome(SRM_COMMAND, log, surlStatus.getStatus(), inputData,
        surlStatus.getSurl());
  }

  protected void logRequestOutcome(TReturnStatus status, InputData id) {

    TRequestToken token = getTokenFromInputData(id);
    List<TSURL> surls = getSURLListFromInputData(id);

    if (surls == null) {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, id, token);
    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, id, token, toStringList(surls));
    }
  }
}
