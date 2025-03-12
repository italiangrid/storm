/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.catalogs.VolatileAndJiTCatalog;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.InvalidTSURLLifetimeReturnStatusAttributeException;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandHelper;
import it.grid.storm.synchcall.command.DataTransferCommand;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData;
import it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeOutputData;
import it.grid.storm.synchcall.data.datatransfer.IdentityExtendFileLifeTimeInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * Authors:
 * 
 * @author=lucamag luca.magnoniATcnaf.infn.it
 * @author Alberto Forti
 * @date = Oct 10, 2008
 */
public class ExtendFileLifeTimeCommand extends DataTransferCommand implements Command {

  private static final Logger log = LoggerFactory.getLogger(ExtendFileLifeTimeCommand.class);
  private static final String SRM_COMMAND = "srmExtendFileLifeTime";

  public ExtendFileLifeTimeCommand() {

  };

  /**
   * Executes an srmExtendFileLifeTime().
   * 
   * @param inputData ExtendFileLifeTimeInputData
   * @return ExtendFileLifeTimeOutputData
   */

  public OutputData execute(InputData data) {

    final String funcName = "ExtendFileLifeTime: ";
    ExtendFileLifeTimeOutputData outputData = new ExtendFileLifeTimeOutputData();
    IdentityExtendFileLifeTimeInputData inputData;
    if (data instanceof IdentityInputData) {
      inputData = (IdentityExtendFileLifeTimeInputData) data;
    } else {
      outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_NOT_SUPPORTED,
          "Anonymous user can not perform" + SRM_COMMAND));
      outputData.setArrayOfFileStatuses(null);
      printRequestOutcome(outputData.getReturnStatus(), (ExtendFileLifeTimeInputData) data);
      return outputData;
    }

    TReturnStatus globalStatus = null;

    ExtendFileLifeTimeCommand.log.debug(funcName + "Started.");

    /****************************** Check for malformed request ******************************/
    if (inputData.getArrayOfSURLs() == null) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Missing mandatory parameter 'arrayOfSURLs'");
    } else if (inputData.getArrayOfSURLs().size() < 1) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Parameter 'arrayOfSURLs': invalid size");
    } else if (!(inputData.getNewPinLifetime().isEmpty())
        && !(inputData.getNewFileLifetime().isEmpty()) && (inputData.getRequestToken() != null)) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Cannot update both FileLifetime and PinLifetime");
    } else if (inputData.getNewPinLifetime().isEmpty()
        && !(inputData.getNewFileLifetime().isEmpty()) && (inputData.getRequestToken() != null)) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Do not specify the request token to update the FileLifetime");
    } else if (!(inputData.getNewPinLifetime().isEmpty())
        && !(inputData.getNewFileLifetime().isEmpty()) && (inputData.getRequestToken() == null)) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Attempt to extend PinLifetime without request token");
    } else if (!(inputData.getNewPinLifetime().isEmpty())
        && inputData.getNewFileLifetime().isEmpty() && (inputData.getRequestToken() == null)) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Attempt to extend PinLifetime without request token");
    }

    if (globalStatus != null) {
      ExtendFileLifeTimeCommand.log.debug(funcName + globalStatus.getExplanation());
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      printRequestOutcome(outputData.getReturnStatus(), inputData);
      return outputData;
    }

    /**********************
     * Check user authentication and authorization
     ******************************/
    GridUserInterface user = inputData.getUser();
    if (user == null) {
      ExtendFileLifeTimeCommand.log.debug(funcName + "The user field is NULL");
      outputData.setReturnStatus(CommandHelper.buildStatus(TStatusCode.SRM_AUTHENTICATION_FAILURE,
          "Unable to get user credential!"));
      printRequestOutcome(outputData.getReturnStatus(), inputData);
      outputData.setArrayOfFileStatuses(null);
      return outputData;
    }

    /**********************************
     * Start to manage the request
     ***********************************/
    ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatus = new ArrayOfTSURLLifetimeReturnStatus();

    if ((inputData.getRequestToken() == null) && (inputData.getNewPinLifetime().isEmpty())) {
      log.debug(funcName + "Extending SURL lifetime...");
      globalStatus = manageExtendSURLLifetime(inputData.getNewFileLifetime(),
          inputData.getArrayOfSURLs(), user, arrayOfFileStatus, inputData.getRequestToken());
    } else {
      log.debug(funcName + "Extending PIN lifetime...");
      try {
        globalStatus = manageExtendPinLifetime(inputData.getRequestToken(),
            inputData.getNewPinLifetime(), inputData.getArrayOfSURLs(), user, arrayOfFileStatus);
      } catch (IllegalArgumentException e) {
        log.error(funcName + "Unexpected IllegalArgumentException: " + e.getMessage());
        globalStatus =
            CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, "Request Failed, retry.");
        outputData.setReturnStatus(globalStatus);
        outputData.setArrayOfFileStatuses(null);
        printRequestOutcome(outputData.getReturnStatus(), inputData);
        return outputData;
      }
    }

    outputData.setReturnStatus(globalStatus);
    outputData.setArrayOfFileStatuses(arrayOfFileStatus);
    printRequestOutcome(outputData.getReturnStatus(), inputData);
    log.debug(funcName + "Finished.");

    return outputData;
  }

  /**
   * Extend the lifetime of a SURL. The parameter details is filled by this method and contains file
   * level information on the execution of the request.
   * 
   * @param newLifetime TLifeTimeInSeconds.
   * @param arrayOfSURLS ArrayOfSURLs.
   * @param guser VomsGridUser.
   * @param arrayOfFileLifetimeStatus . ArrayOfTSURLLifetimeReturnStatus The returned file level
   *        information.
   * @return TReturnStatus. The request status.
   */
  private TReturnStatus manageExtendSURLLifetime(TLifeTimeInSeconds newLifetime,
      ArrayOfSURLs arrayOfSURLS, GridUserInterface guser, ArrayOfTSURLLifetimeReturnStatus details,
      TRequestToken requestToken) {

    if (details == null) {
      ExtendFileLifeTimeCommand.log.debug(
          "Function manageExtendSURLLifetime, class ExtendFileLifeTimeExecutor: parameter details is NULL");
    }
    Namespace namespace = Namespace.getInstance();
    VolatileAndJiTCatalog catalog = VolatileAndJiTCatalog.getInstance();
    boolean requestSuccess = true;
    boolean requestFailure = true;

    // For each requested SURL, try to extend its lifetime.
    for (int i = 0; i < arrayOfSURLS.size(); i++) {
      TSURL surl = arrayOfSURLS.getTSURL(i);
      StoRI stori = null;
      TStatusCode fileStatusCode;
      String fileStatusExplanation;
      try {
        try {
          stori = namespace.resolveStoRIbySURL(surl, guser);
        } catch (IllegalArgumentException e) {
          ExtendFileLifeTimeCommand.log.error("Unable to build StoRI by SURL and user", e);
          fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
          fileStatusExplanation = "Unable to build StoRI by SURL and user";
        } catch (UnapprochableSurlException e) {
          log.info("Unable to build a stori for surl " + surl + " for user " + guser
              + " UnapprochableSurlException: " + e.getMessage());
          fileStatusCode = TStatusCode.SRM_AUTHORIZATION_FAILURE;
          fileStatusExplanation = e.getMessage();
        } catch (NamespaceException e) {
          log.info("Unable to build a stori for surl " + surl + " for user " + guser
              + " NamespaceException: " + e.getMessage());
          fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
          fileStatusExplanation = e.getMessage();
        } catch (InvalidSURLException e) {
          log.info("Unable to build a stori for surl " + surl + " for user " + guser
              + " InvalidSURLException: " + e.getMessage());
          fileStatusCode = TStatusCode.SRM_INVALID_PATH;
          fileStatusExplanation = e.getMessage();
        }
        if (stori != null) {
          LocalFile localFile = stori.getLocalFile();
          if (localFile.exists()) {
            ExtendFileLifeTimeCommand.log.debug(stori.getPFN().toString());
            List<Object> volatileInfo = catalog.volatileInfoOn(stori.getPFN());
            if (volatileInfo.isEmpty()) {
              fileStatusCode = TStatusCode.SRM_SUCCESS;
              fileStatusExplanation = "Nothing to do, SURL is permanent";
              newLifetime = TLifeTimeInSeconds.makeInfinite();
              requestFailure = false;
            } else if (volatileInfo.size() > 2) {
              fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
              fileStatusExplanation = "Found more than one entry.... that's a BUG.";
              // For lifetimes infinite means also unknown
              newLifetime = TLifeTimeInSeconds.makeInfinite();
              requestSuccess = false;
            } else if (isStoRISURLBusy(stori)) {
              fileStatusCode = TStatusCode.SRM_FILE_BUSY;
              fileStatusExplanation =
                  "File status is SRM_SPACE_AVAILABLE. SURL lifetime cannot be extend (try with PIN lifetime)";
              // For lifetimes infinite means also unknown
              newLifetime = TLifeTimeInSeconds.makeInfinite();
              requestSuccess = false;
            } else { // Ok, extend the lifetime of the SURL
              // Update the DB with the new lifetime
              catalog.trackVolatile(stori.getPFN(), (Calendar) volatileInfo.get(0), newLifetime);
              // TODO: return the correct lifetime, i.e. the one which is
              // written to the DB.
              // TLifeTimeInSeconds writtenLifetime = (TLifeTimeInSeconds)
              // volatileInfo.get(1);

              fileStatusCode = TStatusCode.SRM_SUCCESS;
              fileStatusExplanation = "Lifetime extended";
              requestFailure = false;
            }
          } else { // Requested SURL does not exists in the filesystem
            fileStatusCode = TStatusCode.SRM_INVALID_PATH;
            fileStatusExplanation = "File does not exist";
            requestSuccess = false;
          }

          // Set the file level information to be returned.
          TReturnStatus fileStatus = new TReturnStatus(fileStatusCode, fileStatusExplanation);
          if (fileStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
            ExtendFileLifeTimeCommand.log
              .info("srmExtendFileLifeTime: <" + guser + "> Request for [token:" + requestToken
                  + "] for [SURL:" + surl + "] with [lifetime:" + newLifetime
                  + " ] successfully done with: [status:" + fileStatus + "]");
          } else {
            ExtendFileLifeTimeCommand.log.error("srmExtendFileLifeTime: <" + guser
                + "> Request for [token:" + requestToken + "] for [SURL:" + surl
                + "] with [lifetime:" + newLifetime + "] failed with: [status:" + fileStatus + "]");
          }
          TSURLLifetimeReturnStatus lifetimeReturnStatus =
              new TSURLLifetimeReturnStatus(surl, fileStatus, newLifetime, null);
          details.addTSurlReturnStatus(lifetimeReturnStatus);
        }
      } catch (InvalidTSURLLifetimeReturnStatusAttributeException e3) {
        ExtendFileLifeTimeCommand.log
          .debug("Thrown InvalidTSURLLifetimeReturnStatusAttributeException");
      }
    }
    TReturnStatus globalStatus = null;
    // Set global status
    if (requestFailure) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
    } else if (requestSuccess) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
          "All file requests are successfully completed");
    } else {
      globalStatus =
          new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Details are on the file statuses");
    }
    return globalStatus;
  }

  /**
   * Returns true if the status of the SURL of the received StoRI is SRM_SPACE_AVAILABLE, false
   * otherwise. This method queries the DB, therefore pay attention to possible performance issues.
   * 
   * @return boolean
   */
  private boolean isStoRISURLBusy(StoRI element) {

    SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();

    return checker.isSURLBusy(element.getSURL());
  }

  /**
   * Extend the PIN lifetime of a SURL. The parameter details is filled by this method and contains
   * file level information on the execution of the request.
   * 
   * @param requestToken TRequestToken.
   * @param newPINLifetime TLifeTimeInSeconds.
   * @param arrayOfSURLS ArrayOfSURLs.
   * @param guser VomsGridUser.
   * @param details ArrayOfTSURLLifetimeReturnStatus.
   * @return TReturnStatus. The request status.
   * @throws UnknownTokenException
   * @throws IllegalArgumentException
   */
  private TReturnStatus manageExtendPinLifetime(TRequestToken requestToken,
      TLifeTimeInSeconds newPINLifetime, ArrayOfSURLs arrayOfSURLS, GridUserInterface guser,
      ArrayOfTSURLLifetimeReturnStatus details) throws IllegalArgumentException {

    if (details == null) {
      ExtendFileLifeTimeCommand.log.debug(
          "Function manageExtendSURLLifetime, class ExtendFileLifeTimeExecutor: parameter details is NULL");
    }
    TReturnStatus globalStatus = null;
    List<SURLData> requestSURLsList;
    try {
      requestSURLsList = getListOfSURLsInTheRequest(guser, requestToken);
    } catch (UnknownTokenException e4) {
      return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
    } catch (ExpiredTokenException e) {
      return CommandHelper.buildStatus(TStatusCode.SRM_REQUEST_TIMED_OUT, "Request expired");
    } catch (AuthzException e) {
      return CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage());
    }
    if (requestSURLsList.isEmpty()) {
      return CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST, "Invalid request token");
    }
    // Once we have the list of SURLs belonging to the request, we must check
    // that the SURLs given by the user are consistent, that the resulting
    // lifetime could be lower than the one requested (and for this we must read
    // the Volatile table of the DB), that the SURLs are not released, aborted,
    // expired or suspended and so on... therefore the purpose of all that stuff
    // is to return the right information. I mean, no PIN lifetime is
    // effectively extend, in StoRM the TURL corresponds to the SURL.
    boolean requestSuccess = true;
    boolean requestFailure = true;
    TLifeTimeInSeconds PINLifetime;
    TLifeTimeInSeconds dbLifetime = null;
    for (int i = 0; i < arrayOfSURLS.size(); i++) {
      TSURL surl = arrayOfSURLS.getTSURL(i);
      TStatusCode statusOfTheSURL = null;
      TStatusCode fileStatusCode;
      String fileStatusExplanation;
      boolean surlFound = false;
      // Check if the current SURL belongs to the request token
      for (int j = 0; j < requestSURLsList.size(); j++) {
        SURLData surlData = (SURLData) requestSURLsList.get(j);
        if (surl.equals(surlData.surl)) {
          statusOfTheSURL = surlData.statusCode;
          requestSURLsList.remove(j);
          surlFound = true;
          break;
        }
      }
      try {
        if (surlFound) {
          ExtendFileLifeTimeCommand.log.debug("Found SURL: " + surl.getSURLString() + " (status: "
              + statusOfTheSURL.toString() + ")");
          Namespace namespace = Namespace.getInstance();
          StoRI stori = null;
          try {
            stori = namespace.resolveStoRIbySURL(surl, guser);
          } catch (IllegalArgumentException e) {
            log.error("Unable to build StoRI by SURL and user", e);
          } catch (Exception e) {
            log.info(String.format("Unable to build a stori for surl %s for user %s, %s: %s", surl,
                guser, e.getClass().getCanonicalName(), e.getMessage()));
          }
          if (stori != null) {
            LocalFile localFile = stori.getLocalFile();
            if (localFile.exists()) {
              VolatileAndJiTCatalog catalog = VolatileAndJiTCatalog.getInstance();
              List<Object> volatileInfo = catalog.volatileInfoOn(stori.getPFN());

              if ((statusOfTheSURL != TStatusCode.SRM_FILE_PINNED)
                  && (statusOfTheSURL != TStatusCode.SRM_SPACE_AVAILABLE)
                  && (statusOfTheSURL != TStatusCode.SRM_SUCCESS)) {
                fileStatusCode = TStatusCode.SRM_INVALID_REQUEST;
                fileStatusExplanation = "No TURL available";
                PINLifetime = null;
                requestSuccess = false;
              } else if (volatileInfo.size() > 2) {
                fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
                fileStatusExplanation = "Found more than one entry.... that's a BUG.";
                // For lifetimes infinite means also unknown
                PINLifetime = TLifeTimeInSeconds.makeInfinite();
                requestSuccess = false;
              } else { // OK, extend the PIN lifetime.
                // If the status is success the extension will not take place,
                // only in case of empty parameter the current value are
                // returned, otherwaise the request must
                // fail!

                if ((statusOfTheSURL == TStatusCode.SRM_SUCCESS) && (!newPINLifetime.isEmpty())) {

                  fileStatusCode = TStatusCode.SRM_INVALID_REQUEST;
                  fileStatusExplanation = "No TURL available";
                  PINLifetime = null;
                  requestSuccess = false;

                } else {

                  fileStatusCode = TStatusCode.SRM_SUCCESS;

                  if (volatileInfo.isEmpty()) { // SURL is permanent
                    dbLifetime = TLifeTimeInSeconds.makeInfinite();
                  } else {
                    dbLifetime = (TLifeTimeInSeconds) volatileInfo.get(1);
                  }
                  if ((!dbLifetime.isInfinite()) && (newPINLifetime.value() > dbLifetime.value())) {
                    PINLifetime = dbLifetime;
                    fileStatusExplanation =
                        "The requested PIN lifetime is greater than the lifetime of the SURL."
                            + " PIN lifetime is now equal to the lifetime of the SURL.";
                  } else {
                    PINLifetime = newPINLifetime;
                    fileStatusExplanation = "Lifetime extended";
                  }
                  ExtendFileLifeTimeCommand.log.debug("New PIN lifetime is: " + PINLifetime.value()
                      + "(SURL: " + surl.getSURLString() + ")");
                  // TODO: update the RequestSummaryCatalog with the new
                  // pinLifetime
                  // it is better to do it only once after the for loop
                  requestFailure = false;
                }
              }
            } else { // file does not exist in the file system
              fileStatusCode = TStatusCode.SRM_INVALID_PATH;
              fileStatusExplanation = "Invalid path";
              PINLifetime = null;
              requestSuccess = false;

            }
          } else {
            log.error("Unable to build StoRI by SURL and user");
            fileStatusCode = TStatusCode.SRM_INTERNAL_ERROR;
            fileStatusExplanation = "Unable to build StoRI by SURL and user";
            // For lifetimes infinite means also unknown
            PINLifetime = null;
            requestSuccess = false;
          }
        } else { // SURL not found in the DB
          ExtendFileLifeTimeCommand.log.debug("SURL: " + surl.getSURLString() + " NOT FOUND!");
          fileStatusCode = TStatusCode.SRM_INVALID_PATH;
          fileStatusExplanation = "SURL not found in the request";
          PINLifetime = null;
          requestSuccess = false;
        }
        // Set the file level information to be returned.
        TReturnStatus fileStatus = new TReturnStatus(fileStatusCode, fileStatusExplanation);
        if (fileStatus.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
          ExtendFileLifeTimeCommand.log
            .info("srmExtendFileLifeTime: <" + guser + "> Request for [token:" + requestToken
                + "] for [SURL:" + surl + "] with [pinlifetime: " + newPINLifetime
                + "] successfully done with: [status:" + fileStatus.toString() + "]");
        } else {
          ExtendFileLifeTimeCommand.log
            .error("srmExtendFileLifeTime: <" + guser + "> Request for [token:" + requestToken
                + "] for [SURL:" + surl + "] with [pinlifetime: " + newPINLifetime
                + "] failed with: [status:" + fileStatus.toString() + "]");
        }

        TSURLLifetimeReturnStatus lifetimeReturnStatus =
            new TSURLLifetimeReturnStatus(surl, fileStatus, dbLifetime, PINLifetime);
        details.addTSurlReturnStatus(lifetimeReturnStatus);
      } catch (InvalidTSURLLifetimeReturnStatusAttributeException e3) {
        ExtendFileLifeTimeCommand.log
          .debug("Thrown InvalidTSURLLifetimeReturnStatusAttributeException");
      }
    }

    // Set global status
    if (requestFailure) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "All file requests are failed");
    } else if (requestSuccess) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
          "All file requests are successfully completed");
    } else {
      globalStatus =
          new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS, "Details are on the file statuses");
    }
    return globalStatus;
  }

  /**
   * Returns the list of SURLs and statuses (a List of SURLData) belonging to the request identified
   * by the requestToken.
   * 
   * @param requestToken TRequestToken
   * @return List<SURLData>
   * @throws UnknownTokenException
   * @throws IllegalArgumentException
   * @throws ExpiredTokenException
   */
  private List<SURLData> getListOfSURLsInTheRequest(GridUserInterface user,
      TRequestToken requestToken)
      throws IllegalArgumentException, UnknownTokenException, ExpiredTokenException {

    List<SURLData> listOfSURLsInfo = new LinkedList<SURLData>();

    SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();

    Map<TSURL, TReturnStatus> surlStatusMap = checker.getSURLStatuses(user, requestToken);

    if (!(surlStatusMap == null || surlStatusMap.isEmpty())) {
      for (Entry<TSURL, TReturnStatus> surlStatus : surlStatusMap.entrySet()) {
        listOfSURLsInfo
          .add(new SURLData(surlStatus.getKey(), surlStatus.getValue().getStatusCode()));
      }
    }
    return listOfSURLsInfo;
  }

  private void printRequestOutcome(TReturnStatus status, ExtendFileLifeTimeInputData inputData) {

    if (inputData != null) {
      if (inputData.getArrayOfSURLs() != null) {
        if (inputData.getRequestToken() != null) {
          CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
              inputData.getRequestToken(), inputData.getArrayOfSURLs().asStringList());
        } else {
          CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
              inputData.getArrayOfSURLs().asStringList());
        }

      } else {
        if (inputData.getRequestToken() != null) {
          CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData,
              inputData.getRequestToken());
        } else {
          CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
        }
      }

    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }

  private class SURLData {

    public TSURL surl;
    public TStatusCode statusCode;

    public SURLData(TSURL surl, TStatusCode statusCode) {

      this.surl = surl;
      this.statusCode = statusCode;
    }
  }
}
