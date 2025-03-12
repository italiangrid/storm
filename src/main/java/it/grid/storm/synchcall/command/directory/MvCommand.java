/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.directory;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.acl.AclManagerFS;
import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.catalogs.surl.SURLStatusManager;
import it.grid.storm.catalogs.surl.SURLStatusManagerFactory;
import it.grid.storm.filesystem.FilesystemPermission;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
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
import it.grid.storm.synchcall.data.directory.MvInputData;
import it.grid.storm.synchcall.data.directory.MvOutputData;

public class MvCommand extends DirectoryCommand implements Command {

  public static final Logger log = LoggerFactory.getLogger(MvCommand.class);

  private static final String SRM_COMMAND = "SrmMv";
  private final Namespace namespace;

  public MvCommand() {

    namespace = Namespace.getInstance();

  }

  /**
   * Method that provide SrmMv functionality.
   * 
   * @param inputData Contains information about input data for Mv request.
   * @return outputData Contains output data
   */
  public OutputData execute(InputData data) {

    log.debug("srmMv: Start execution.");
    MvOutputData outputData = new MvOutputData();
    MvInputData inputData = (MvInputData) data;

    /**
     * Validate MvInputData. The check is done at this level to separate internal StoRM logic from
     * XMLRPC specific operation.
     */

    if ((inputData == null) || (inputData.getFromSURL() == null)
        || (inputData.getToSURL() == null)) {
      outputData.setStatus(
          CommandHelper.buildStatus(TStatusCode.SRM_FAILURE, "Invalid parameter specified."));
      log.warn("srmMv: Request failed with [status: {}]", outputData.getStatus());

      return outputData;
    }

    TSURL fromSURL = inputData.getFromSURL();

    if (fromSURL.isEmpty()) {
      log.warn("srmMv: unable to perform the operation, empty fromSurl");
      outputData.setStatus(
          CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid fromSURL specified!"));
      printRequestOutcome(outputData.getStatus(), inputData);
      return outputData;
    }

    TSURL toSURL = inputData.getToSURL();

    if (toSURL.isEmpty()) {
      log.error("srmMv: unable to perform the operation, empty toSurl");
      outputData.setStatus(
          CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid toSURL specified!"));
      printRequestOutcome(outputData.getStatus(), inputData);
      return outputData;
    }

    StoRI fromStori = null;
    try {
      if (inputData instanceof IdentityInputData) {
        try {
          fromStori =
              namespace.resolveStoRIbySURL(fromSURL, ((IdentityInputData) inputData).getUser());
        } catch (UnapprochableSurlException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", fromSURL,
              DataHelper.getRequestor(inputData), e.getMessage());

          outputData.setStatus(
              CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (NamespaceException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", fromSURL,
              DataHelper.getRequestor(inputData), e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (InvalidSURLException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", fromSURL,
              DataHelper.getRequestor(inputData), e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
        }
      } else {
        try {
          fromStori = namespace.resolveStoRIbySURL(fromSURL);
        } catch (UnapprochableSurlException e) {
          log.info("srmMv: Unable to build a stori for surl {}. {}", fromSURL, e.getMessage());
          outputData.setStatus(
              CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (NamespaceException e) {
          log.info("srmMv: Unable to build a stori for surl {}. {}", fromSURL, e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (InvalidSURLException e) {
          log.info("srmMv: Unable to build a stori for surl {}. {}", fromSURL, e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        }
      }
    } catch (IllegalArgumentException e) {
      log.warn("srmMv: Unable to build StoRI by SURL: {}. {}", fromSURL, e.getMessage());
      outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_REQUEST,
          "Unable to build StoRI by SURL"));
      printRequestOutcome(outputData.getStatus(), inputData);
      return outputData;
    }

    StoRI toStori = null;;
    try {
      if (inputData instanceof IdentityInputData) {
        try {
          toStori = namespace.resolveStoRIbySURL(toSURL, ((IdentityInputData) inputData).getUser());
        } catch (UnapprochableSurlException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", fromSURL,
              DataHelper.getRequestor(inputData), e.getMessage());
          outputData.setStatus(
              CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (NamespaceException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", fromSURL,
              DataHelper.getRequestor(inputData), e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (InvalidSURLException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", fromSURL,
              DataHelper.getRequestor(inputData), e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        }
      } else {
        try {
          toStori = namespace.resolveStoRIbySURL(toSURL);
        } catch (UnapprochableSurlException e) {
          log.info("srmMv: Unable to build a stori for surl {}. {}", fromSURL, e.getMessage());
          outputData.setStatus(
              CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (NamespaceException e) {
          log.info("srmMv: Unable to build a stori for surl {}. {}", fromSURL, e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (InvalidSURLException e) {
          log.info("srmMv: Unable to build a stori for surl {}. {}", fromSURL, e.getMessage());
          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        }
      }
    } catch (IllegalArgumentException e) {
      log.error("srmMv: Unable to build StoRI by SURL: {}. {}", fromSURL, e.getMessage(), e);
      outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
          "Unable to build StoRI by destination SURL"));
      printRequestOutcome(outputData.getStatus(), inputData);
      return outputData;
    }

    TSpaceToken token = new SpaceHelper().getTokenFromStoRI(log, fromStori);
    SpaceAuthzInterface spaceAuth = AuthzDirector.getSpaceAuthz(token);

    boolean isSpaceAuthorized;
    if (inputData instanceof IdentityInputData) {
      isSpaceAuthorized =
          spaceAuth.authorize(((IdentityInputData) inputData).getUser(), SRMSpaceRequest.MV);
    } else {
      isSpaceAuthorized = spaceAuth.authorizeAnonymous(SRMSpaceRequest.MV);
    }
    if (!isSpaceAuthorized) {
      log.debug("srmMv: User not authorized to perform srmMv on SA: {}", token);
      outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE,
          ": User not authorized to perform srmMv on SA: " + token));
      printRequestOutcome(outputData.getStatus(), inputData);
      return outputData;
    }

    if (fromStori.getLocalFile().getPath().compareTo(toStori.getLocalFile().getPath()) == 0) {
      outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_SUCCESS,
          "Source SURL and target SURL are the same file."));
      printRequestOutcome(outputData.getStatus(), inputData);
      return outputData;
    }

    if (toStori.getLocalFile().exists()) {
      if (toStori.getLocalFile().isDirectory()) {
        try {
          toStori = buildDestinationStoryForFolder(toSURL, fromStori, data);
        } catch (IllegalArgumentException e) {
          log.debug("srmMv : Unable to build StoRI for SURL {}. {}", toSURL, e.getMessage());

          outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR,
              "Unable to build StoRI by SURL"));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (UnapprochableSurlException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", toSURL,
              DataHelper.getRequestor(inputData), e.getMessage());

          outputData.setStatus(
              CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (InvalidTSURLAttributesException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", toSURL,
              DataHelper.getRequestor(inputData), e.getMessage());

          outputData.setStatus(
              CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, "Invalid toSURL specified!"));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (NamespaceException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", toSURL,
              DataHelper.getRequestor(inputData), e.getMessage());

          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        } catch (InvalidSURLException e) {
          log.info("srmMv: Unable to build a stori for surl {} for user {}. {}", toSURL,
              DataHelper.getRequestor(inputData), e.getMessage());

          outputData
            .setStatus(CommandHelper.buildStatus(TStatusCode.SRM_INVALID_PATH, e.getMessage()));
          printRequestOutcome(outputData.getStatus(), inputData);
          return outputData;
        }
      } else {
        log.debug("srmMv : destination SURL {} already exists.", toSURL);
        outputData.setStatus(CommandHelper.buildStatus(TStatusCode.SRM_DUPLICATION_ERROR,
            "destination SURL already exists!"));
        printRequestOutcome(outputData.getStatus(), inputData);
        return outputData;
      }
    }

    AuthzDecision sourceDecision;
    if (inputData instanceof IdentityInputData) {
      sourceDecision = AuthzDirector.getPathAuthz()
        .authorize(((IdentityInputData) inputData).getUser(), SRMFileRequest.MV_source, fromStori,
            toStori);
    } else {
      sourceDecision = AuthzDirector.getPathAuthz()
        .authorizeAnonymous(SRMFileRequest.MV_source, fromStori, toStori);
    }
    AuthzDecision destinationDecision;
    if (inputData instanceof IdentityInputData) {
      destinationDecision = AuthzDirector.getPathAuthz()
        .authorize(((IdentityInputData) inputData).getUser(), SRMFileRequest.MV_dest, fromStori,
            toStori);
    } else {
      destinationDecision = AuthzDirector.getPathAuthz()
        .authorizeAnonymous(SRMFileRequest.MV_dest, fromStori, toStori);
    }
    TReturnStatus returnStatus;
    if ((sourceDecision.equals(AuthzDecision.PERMIT))
        && (destinationDecision.equals(AuthzDecision.PERMIT))) {

      log.debug("SrmMv: Mv authorized for user {}. Source: {}. Target: {}",
          DataHelper.getRequestor(inputData), fromStori.getPFN(), toStori.getPFN());

      returnStatus = manageAuthorizedMV(fromStori, toStori.getLocalFile());
      if (returnStatus.isSRM_SUCCESS()) {
        LocalUser user = null;
        if (inputData instanceof IdentityInputData) {
          try {
            user = ((IdentityInputData) inputData).getUser().getLocalUser();
          } catch (CannotMapUserException e) {
            log.warn("srmMv: user mapping error {}", e.getMessage());

            if (log.isDebugEnabled()) {
              log.error(e.getMessage(), e);
            }

            returnStatus.extendExplaination("unable to set user acls on the destination file");
          }
        }
        if (user != null) {
          setAcl(fromStori, toStori, user);
        } else {
          setAcl(fromStori, toStori);
        }
      } else {
        log.warn("srmMv: <{}> Request for [fromSURL={}; toSURL={}] failed with [status: {}]",
            DataHelper.getRequestor(inputData), fromSURL, toSURL, returnStatus);
      }
    } else {

      String errorMsg = "Authorization error";

      if (sourceDecision.equals(AuthzDecision.PERMIT)) {
        errorMsg = "User is not authorized to create and/or write the destination file";
      } else {
        if (destinationDecision.equals(AuthzDecision.PERMIT)) {
          errorMsg = "User is not authorized to read and/or delete the source file";
        } else {
          errorMsg = "User is neither authorized to read and/or delete the source file "
              + "nor to create and/or write the destination file";
        }
      }

      returnStatus = CommandHelper.buildStatus(TStatusCode.SRM_AUTHORIZATION_FAILURE, errorMsg);
    }
    outputData.setStatus(returnStatus);
    printRequestOutcome(outputData.getStatus(), inputData);
    return outputData;
  }

  private StoRI buildDestinationStoryForFolder(TSURL toSURL, StoRI fromStori, InputData inputData)
      throws IllegalArgumentException, InvalidTSURLAttributesException, UnapprochableSurlException,
      NamespaceException, InvalidSURLException {

    StoRI toStori;
    String toSURLString = toSURL.getSURLString();
    if (!(toSURLString.endsWith("/"))) {
      toSURLString += "/";
    }
    toSURLString += fromStori.getFilename();
    log.debug("srmMv: New toSURL: {}", toSURLString);
    if (inputData instanceof IdentityInputData) {
      toStori = namespace.resolveStoRIbySURL(TSURL.makeFromStringValidate(toSURLString),
          ((IdentityInputData) inputData).getUser());
    } else {
      toStori = namespace.resolveStoRIbySURL(TSURL.makeFromStringValidate(toSURLString));
    }
    return toStori;
  }

  private void setAcl(StoRI oldFileStoRI, StoRI newFileStoRI) {

    try {
      AclManagerFS.getInstance()
        .moveHttpsPermissions(oldFileStoRI.getLocalFile(), newFileStoRI.getLocalFile());
    } catch (IllegalArgumentException e) {
      log.error("Unable to move permissions from the old to the new file.{}", e.getMessage(), e);
    }
  }

  private void setAcl(StoRI oldFileStoRI, StoRI newFileStoRI, LocalUser localUser) {

    setAcl(oldFileStoRI, newFileStoRI);
    if (newFileStoRI.hasJustInTimeACLs()) {
      // JiT
      try {
        AclManagerFS.getInstance()
          .grantHttpsUserPermission(newFileStoRI.getLocalFile(), localUser,
              FilesystemPermission.ReadWrite);
      } catch (IllegalArgumentException e) {
        log.error("Unable to grant user read and write permission on file. {}", e.getMessage(), e);
      }
    } else {
      // AoT
      try {
        AclManagerFS.getInstance()
          .grantHttpsGroupPermission(newFileStoRI.getLocalFile(), localUser,
              FilesystemPermission.ReadWrite);
      } catch (IllegalArgumentException e) {
        log.error("Unable to grant group read and write permission on file. {}", e.getMessage(), e);
      }
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
  private TReturnStatus manageAuthorizedMV(StoRI fromStori, LocalFile toFile) {

    boolean creationDone;

    String explanation = "";
    TStatusCode statusCode = TStatusCode.EMPTY;

    LocalFile fromFile = fromStori.getLocalFile();
    LocalFile toParent = toFile.getParentFile();

    /*
     * Controllare che File sorgente esiste Esiste directory destinazione(che esista e sia
     * directory) Non esiste file deestinazione
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

      SURLStatusManager checker = SURLStatusManagerFactory.newSURLStatusManager();

      if (checker.isSURLBusy(fromStori.getSURL())) {
        log.debug("srmMv request failure: fromSURL is busy.");
        explanation = "There is an active SrmPrepareToPut on from SURL.";
        return CommandHelper.buildStatus(TStatusCode.SRM_FILE_BUSY, explanation);
      }

      /**
       * Check if there is an active SrmPrepareToGet on the source SURL. In that case SrmMv() fails
       * with SRM_FILE_BUSY.
       */

      if (checker.isSURLPinned(fromStori.getSURL())) {
        log.debug(
            "SrmMv: requests fails because the source SURL is being used from other requests.");
        explanation = "There is an active SrmPrepareToGet on from SURL";
        return CommandHelper.buildStatus(TStatusCode.SRM_FILE_BUSY, explanation);
      }

      /**
       * Perform the SrmMv() operation.
       */
      creationDone = fromFile.renameTo(toFile.getPath());

      if (creationDone) {
        log.debug("SrmMv: Request success!");
        explanation = "SURL moved with success";
        statusCode = TStatusCode.SRM_SUCCESS;
      } else {
        log.debug("SrmMv: Requests fails because the path is invalid.");
        explanation = "Invalid path";
        statusCode = TStatusCode.SRM_INVALID_PATH;
      }

    } else {
      if (!sourceExists) { // and it is a file
        log.debug("SrmMv: request fails because the source SURL does not exists!");
        explanation = "Source SURL does not exists!";
        statusCode = TStatusCode.SRM_INVALID_PATH;
      } else {
        if (!targetDirExists) {
          log.debug("SrmMv: request fails because the target directory does not exitts.");
          explanation = "Target directory does not exits!";
          statusCode = TStatusCode.SRM_INVALID_PATH;
        } else {
          if (targetFileExists) {
            log.debug("SrmMv: request fails because the target SURL exists.");
            explanation = "Target SURL exists!";
            statusCode = TStatusCode.SRM_DUPLICATION_ERROR;
          } else {
            log.debug("SrmMv request failure! That is a BUG!");
            explanation = "That is a bug!";
            statusCode = TStatusCode.SRM_INTERNAL_ERROR;
          }
        }
      }
    }

    return CommandHelper.buildStatus(statusCode, explanation);
  }

  private void printRequestOutcome(TReturnStatus status, MvInputData inputData) {

    if (inputData != null) {
      if (inputData.getFromSURL() != null && inputData.getToSURL() != null) {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData, Arrays.asList(
            new String[] {inputData.getFromSURL().toString(), inputData.getFromSURL().toString()}));
      } else {
        CommandHelper.printRequestOutcome(SRM_COMMAND, log, status, inputData);
      }
    } else {
      CommandHelper.printRequestOutcome(SRM_COMMAND, log, status);
    }
  }

}
