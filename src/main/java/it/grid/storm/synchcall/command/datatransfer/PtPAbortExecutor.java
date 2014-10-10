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

/**
 * This is the Abort executor for a PtP request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Aug 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.PtPPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSURLReturnStatus;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.command.space.ReserveSpaceCommand;
import it.grid.storm.synchcall.data.DataHelper;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortFilesInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;
import it.grid.storm.synchcall.data.datatransfer.AbortInputData;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.SurlStatusManager;
import it.grid.storm.synchcall.surl.UnknownSurlException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PtPAbortExecutor implements AbortExecutorInterface {

  private static final Logger log = LoggerFactory
    .getLogger(PtPAbortExecutor.class);

  // TODO DEFINE THE TIMEOUT FROM CONFIGURATION
  // CMQ sotto i 3 minuti per discorso tcp timeout?

  static Configuration config = Configuration.getInstance();
  private static int maxLoopTimes = PtPAbortExecutor.config.getMaxLoop();

  private NamespaceInterface namespace;

  private ArrayList<TStatusCode> acceptedStatuses = new ArrayList<TStatusCode>();

  public PtPAbortExecutor() {

    acceptedStatuses.clear();
    acceptedStatuses.add(TStatusCode.SRM_SPACE_AVAILABLE);
    acceptedStatuses.add(TStatusCode.SRM_REQUEST_QUEUED);
  };

  public AbortGeneralOutputData doIt(AbortInputData inputData) {

    // Used to delete the physical file
    namespace = NamespaceDirector.getNamespace();

    AbortGeneralOutputData outputData = new AbortGeneralOutputData();
    ArrayOfTSURLReturnStatus arrayOfTSurlRetStatus = new ArrayOfTSURLReturnStatus();

    TReturnStatus globalStatus = null;

    log.debug("srmAbortRequest: Start PtPAbortExecutor...");

    /**
     * 0) Get all Chunk related to the specified request according with user
     * specification (in case of AbortFiles). 1) Wait until the request goes in
     * SRM_SPACE_AVAILABLE status. 2) Rollback. For a PtP request the rollback
     * means to: - PtP without space reservation: Remove the file (if not
     * existing? And if partially wrote?). - PtP with implicit space reservation
     * (size specified): Remove the file.(the space is associated directly to
     * the file.) - PtP with SpaceToken: Recreaete the SpaceFile, update the
     * SpaceCatalog and remove the file. - PtP with SpaceToken and size Recreate
     * the space, update the space file, update the space catalog and remove the
     * files
     */

    /****************** Phase 0 ******************/

    Map<TSURL, TReturnStatus> surlStatusMap;
    try {
      surlStatusMap = SurlStatusManager.getSurlsStatus(inputData
        .getRequestToken());
    } catch (IllegalArgumentException e) {
      log
        .error("Unexpected IllegalArgumentException during SurlStatusManager.getSurlsStatus: "
          + e);
      throw new IllegalStateException("Unexpected IllegalArgumentException: "
        + e.getMessage());
    } catch (UnknownTokenException e) {
      log.debug("PtPAbortExecutor: Request - Invalid request token");
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
        "Invalid request token");
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
        + "> Request for [token:" + inputData.getRequestToken()
        + "] failed with [status: " + globalStatus + "]");
      return outputData;
    } catch (ExpiredTokenException e) {
      log.info("The request is expired: ExpiredTokenException: "
        + e.getMessage());
      globalStatus = new TReturnStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
        "Request expired");
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
        + "> Request for [token:" + inputData.getRequestToken()
        + "] failed with [status: " + globalStatus + "]");
      return outputData;
    }

    if (surlStatusMap.isEmpty()) {
      log.debug("PtPAbortExecutor: Request - Invalid request token");
      globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
        "Invalid request token");
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(null);
      log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
        + "> Request for [token:" + inputData.getRequestToken()
        + "] failed with [status: " + globalStatus + "]");
      return outputData;
    }

    /**
     * Get only the SURL requested in a AbortFile Request Define a new
     * Collection to contains the "epurated" chunk, removing the ones not
     * specified in input request
     */

    if (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES)) {
      log
        .debug("srmAbortFiles: PtPAbortExecutor: Case of AbortFile request. Purge Chunks with SurlArray.");

      /**
       * Get the related Chunk for each SURL in the input SurlArray. If a Surl
       * requested is not founf, the TSURLReturnStatus related is setted to
       * SRM_INVALID_PATH
       */

      ArrayList<TSURL> surlList = extractSurlArray(inputData).getArrayList();
      surlStatusMap.keySet().retainAll(surlList);
      if (!surlStatusMap.keySet().containsAll(surlList)) {
        for (TSURL surl : surlList) {
          if (!surlStatusMap.containsKey(surl)) {
            log
              .debug("srmAbortFiles: Abort: SURL NOT found, invalid Chunk!Set TSURLReturnStatus");
            TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
            surlReturnStatus.setSurl(surl);
            surlReturnStatus.setStatus(new TReturnStatus(
              TStatusCode.SRM_INVALID_PATH,
              "SURL specified does not referes to this request token."));
            log.info("srmAbortFiles: <" + DataHelper.getRequestor(inputData)
              + "> Request for [token:" + inputData.getRequestToken()
              + "] for [SURL:" + surl + "] failed  with [status: "
              + surlReturnStatus.getStatus() + "]");
            arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);
          }
        }
      }
    }

    /********* Check here the new chunks container is not empty! ******/
    if (surlStatusMap.isEmpty()) {
      log
        .debug("Abort Request - No surl specified associated to request token");
      globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
        "All surl specified does not referes to the request token.");
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
      log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
        + "> Request for [token:" + inputData.getRequestToken()
        + "] failed with [status: " + globalStatus + "]");
      return outputData;
    }

    /*********
     * Phase 1 Wait until the request goes in a status different from
     * REQUEST_QUEUED
     ********/

    int chunkAborted = 0;

    // To avoid deadlock
    int counter = 0;
    // NumberOfFailure for globalStatus
    int errorCount = 0;
    // Total initial Size
    int totalSize = surlStatusMap.size();

    while ((chunkAborted < totalSize)
      && (counter < PtPAbortExecutor.maxLoopTimes)) {
      // Increment loop times
      counter++;

      Iterator<Map.Entry<TSURL, TReturnStatus>> iterator = surlStatusMap
        .entrySet().iterator();
      int numOfSurl = 0;
      while (iterator.hasNext()) {
        Map.Entry<TSURL, TReturnStatus> surlStatus = iterator.next();
        numOfSurl++;

        /*
         * Check if any chunk have a status different from
         * SRM_REQUEST_INPROGESS. That means the request execution is finished,
         * and the rollback start.
         */

        if (!(surlStatus.getValue().getStatusCode()
          .equals(TStatusCode.SRM_REQUEST_INPROGRESS))) {
          /*
           * If an EXECUTED CHUNK is found, then it is ABORTED.
           */
          log
            .debug("PtPAbortExecutor: PtPChunk not in IN_PROGRESS state. Ready for Abort.");
          TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();

          /******** Phase (3) of Abort *************/
          log.debug("PtPAbortExecutor: start Phase(3)");

          /*
           * AvancePicker HACK! Due to a thread issue in the
           * advancePicker.abortRequest(...) we have to check here if the chunk
           * has already been aborted frome the picker. In that case, the
           * manageAbort is not needed
           */
          if ((surlStatus.getValue().getStatusCode()
            .equals(TStatusCode.SRM_ABORTED))) {
            // The AdvancedPicker have already aborted the chunk.
            log.debug("PtPAbortExecutor: CHUNK already aborted!");
            surlReturnStatus.setSurl(surlStatus.getKey());
            surlReturnStatus.setStatus(new TReturnStatus(
              TStatusCode.SRM_SUCCESS, "File request successfully aborted."));
          } else {
            log.debug("PtPAbortExecutor:CHUNK to abort.");
            // Chunk not ABORTED. We have to work...
            surlReturnStatus = manageAuthorizedAbort(
              inputData.getRequestToken(), surlStatus.getKey(),
              surlStatus.getValue(), inputData);
          }

          // Remove this chunks from the other to abort.
          iterator.remove();
          if ((surlReturnStatus.getStatus().getStatusCode()
            .equals(TStatusCode.SRM_SUCCESS))) {
            log.info("srmAbortFiles: <" + DataHelper.getRequestor(inputData)
              + "> Request for [token:" + inputData.getRequestToken()
              + "] for SURL " + numOfSurl + " of " + totalSize + " [SURL:"
              + surlStatus.getKey() + "] successfully done  with [status: "
              + surlReturnStatus.getStatus() + "]");
          } else {
            log.info("srmAbortFiles: <" + DataHelper.getRequestor(inputData)
              + "> Request for [token:" + inputData.getRequestToken()
              + "] for SURL " + numOfSurl + " of " + totalSize + " [SURL:"
              + surlStatus.getKey() + "] failed with [status: "
              + surlReturnStatus.getStatus() + "]");
            errorCount++;
          }
          // Add returnStatus
          arrayOfTSurlRetStatus.addTSurlReturnStatus(surlReturnStatus);

          // Increment number of chunk aborted
          chunkAborted++;

        }

      } // while chunk Collection

      if (chunkAborted < totalSize) {
        log.debug("PtPAbortExecutor: Wait for some time...");
        log.debug("Abort: I'm waiting...");
        try {
          long numMillisecondsToSleep = 100; // 1 seconds
          Thread.sleep(numMillisecondsToSleep);
        } catch (InterruptedException e) {

        }

        log.debug("PtPAbortExecutor: Wake up! It's time to work...");
        log.debug("srmAbortRequest: PtPAbortExecutor: refresh surl status");
        try {
          surlStatusMap = SurlStatusManager.getSurlsStatus(
            inputData.getRequestToken(), surlStatusMap.keySet());
        } catch (IllegalArgumentException e) {
          log
            .error("Unexpected IllegalArgumentException during SurlStatusManager.getSurlsStatus: "
              + e);
          throw new IllegalStateException(
            "Unexpected IllegalArgumentException: " + e.getMessage());
        } catch (UnknownTokenException e) {
          log
            .debug("PtPAbortExecutor: Request - Invalid request token, probably it is expired");
          globalStatus = new TReturnStatus(TStatusCode.SRM_INVALID_REQUEST,
            "Expired request token");
          outputData.setReturnStatus(globalStatus);
          outputData.setArrayOfFileStatuses(null);
          log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
            + "> Request for [token:" + inputData.getRequestToken()
            + "] failed with [status: " + globalStatus + "]");
          return outputData;
        } catch (ExpiredTokenException e) {
          log.info("The request is expired: ExpiredTokenException: "
            + e.getMessage());
          globalStatus = new TReturnStatus(TStatusCode.SRM_REQUEST_TIMED_OUT,
            "Request expired");
          outputData.setReturnStatus(globalStatus);
          outputData.setArrayOfFileStatuses(null);
          log.info("srmAbortRequest: <" + DataHelper.getRequestor(inputData)
            + "> Request for [token:" + inputData.getRequestToken()
            + "] failed with [status: " + globalStatus + "]");
          return outputData;
        }
        log.debug("srmAbortRequest: PtPAbortExecutor: refresh done.");
      }

    }

    // LoopTimes Exceeded?
    if (chunkAborted < totalSize) {
      // The ABORT execution is interrupted to prevent a deadlock situation
      log.debug("Abort: Timeout exceeded.");
      globalStatus = new TReturnStatus(TStatusCode.SRM_INTERNAL_ERROR,
        "TimeOut for abort execution exceeded.");
      outputData.setReturnStatus(globalStatus);
      outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
      log
        .error("srmAbortFiles: <"
          + DataHelper.getRequestor(inputData)
          + "> Request for [token:"
          + inputData.getRequestToken()
          + "]"
          + (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES) ? " for [SURL:"
            + extractSurlArray(inputData) + "]"
            : "") + " completed with [status: " + globalStatus + "]");
      return outputData;
    }

    if (errorCount == totalSize) {
      globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE, "Abort failed.");
    } else if (errorCount > 0) {
      if (inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST)) {
        globalStatus = new TReturnStatus(TStatusCode.SRM_FAILURE,
          "Some chunks failed.");
      } else {
        globalStatus = new TReturnStatus(TStatusCode.SRM_PARTIAL_SUCCESS,
          "Some chunks failed.");
      }
    } else {
      globalStatus = new TReturnStatus(TStatusCode.SRM_SUCCESS,
        "Abort request completed.");
      if ((inputData.getType().equals(AbortInputData.AbortType.ABORT_REQUEST))) {
        TReturnStatus requestStatus = new TReturnStatus(
          TStatusCode.SRM_ABORTED, "Request Aborted.");
        RequestSummaryCatalog.getInstance().updateGlobalStatus(
          inputData.getRequestToken(), requestStatus);
      }
    }
    // Set output data
    outputData.setArrayOfFileStatuses(arrayOfTSurlRetStatus);
    outputData.setReturnStatus(globalStatus);
    log
      .info("srmAbortFiles: <"
        + DataHelper.getRequestor(inputData)
        + "> Request for [token:"
        + inputData.getRequestToken()
        + "]"
        + (inputData.getType().equals(AbortInputData.AbortType.ABORT_FILES) ? " for [SURL:"
          + extractSurlArray(inputData) + "]"
          : "") + " completed with [status: " + globalStatus + "]");
    return outputData;

  }

  private ArrayOfSURLs extractSurlArray(AbortInputData inputData) {

    switch (inputData.getType()) {
    case ABORT_REQUEST:
      throw new IllegalStateException(
        "Unable to get SurlArray from an ABORT_REQUEST input data");
    case ABORT_FILES:
      return ((AbortFilesInputData) inputData).getArrayOfSURLs();
    default:
      throw new IllegalStateException("Received an unknown AbortType: "
        + inputData.getType());
    }
  }

  /**
   * 
   * Manage the roll back needed to execute an abort request.
   * 
   * @param chunkData
   *          PtGChunkData
   * @return returnStatus TSURLReturnStatus
   */
  private TSURLReturnStatus manageAuthorizedAbort(TRequestToken token,
    TSURL surl, TReturnStatus status, AbortInputData inputData) {

    boolean failure = false;
    namespace = NamespaceDirector.getNamespace();

    /******************************* Phase (3) ************************************/

    log.debug("PtPAbortExecutor: Phase(3) RollBack start.");

    TSURLReturnStatus surlReturnStatus = new TSURLReturnStatus();
    surlReturnStatus.setSurl(surl);

    StoRI stori = null;
    boolean res = false;

    if (!acceptedStatuses.contains(status.getStatusCode())) {

      // The Chunk is in a SRM_Status from wich an Abort operation is not
      // possible! (SRM_FAILURE or SRM_SUCCESS)
      if (status.getStatusCode().equals(TStatusCode.SRM_SUCCESS)) {
        surlReturnStatus.setStatus(new TReturnStatus(TStatusCode.SRM_FAILURE,
          "Request is in a final status. Abort not allowed."));
      } else {
        surlReturnStatus.setStatus(new TReturnStatus(TStatusCode.SRM_FAILURE,
          "Abort request not executed."));
      }
      return surlReturnStatus;
    }

    log
      .debug("PtPAbortExecutor: The Chunk is in a VALID state for the RollBack action!");
    if (!SurlStatusManager.isPersisted(token).isEmpty()) {
      /*
       * TODO TEMPORARY. catalog direct access needed until a RequestManager
       * storing token and request infos will be available
       */
      Collection<PtPPersistentChunkData> chunksData = PtPChunkCatalog
        .getInstance().lookup(token);
      PtPPersistentChunkData chunkData = null;
      for (PtPPersistentChunkData surlChunkData : chunksData) {
        if (surlChunkData.getSURL().equals(surl)) {
          chunkData = surlChunkData;
          break;
        }
      }
      // Manage Rollback

      if (chunkData == null) {
        throw new IllegalStateException("Unexpected condition. token " + token
          + " stored on the db but no associated surl " + surl + " found");
      }
      if (!(chunkData.getSpaceToken().isEmpty())) {
        TSpaceToken sToken = chunkData.getSpaceToken();
        TReturnStatus reserveSpaceStatus;
        ReserveSpaceCommand rexec = new ReserveSpaceCommand();

        // Space Token specified in the request
        log.debug("PtPAbortExecutor: Space token found.");
        /*
         * We have now two possible cases: 1) With size specified. 2) Without
         * size specified.
         */
        if (chunkData.expectedFileSize().isEmpty()) {
          /*
           * Without fileSize specified. In this case the whole space_file has
           * been associated to the requested file. So the roll back must
           * recreate the Space Catalog entry and update the information on used
           * space.
           */
          reserveSpaceStatus = rexec.resetReservation(sToken);
          if (!reserveSpaceStatus.getStatusCode().equals(
            TStatusCode.SRM_SUCCESS)) {
            // Something goes wrong
            failure = true;
          }
        } else {
          /*
           * With fileSize specified. In this case the spaceFile have to be
           * increased from the free block of the removed put placeholder.
           */
          reserveSpaceStatus = rexec.updateReservation(
            chunkData.getSpaceToken(), chunkData.expectedFileSize(),
            chunkData.getSURL());
          if (!reserveSpaceStatus.getStatusCode().equals(
            TStatusCode.SRM_SUCCESS)) {
            // Something goes wrong
            failure = true;
          }
        }
      }
    }
    // The File Must be removed in any case!
    log.debug("PtPAbortExecutor: No space token found.");
    log.debug("PtPAbortExecutor: Start removing file from the file system...");
    LocalFile fileToRemove = null;
    if (!surl.isEmpty()) {
      try {
        if (inputData instanceof IdentityInputData) {
          stori = namespace.resolveStoRIbySURL(surl,
            ((IdentityInputData) inputData).getUser());
        } else {
          stori = namespace.resolveStoRIbySURL(surl);
        }
      } catch (UnapprochableSurlException e) {
        failure = true;
        if (inputData instanceof IdentityInputData) {
          log
            .info(String
              .format(
                "Unable to build a stori for surl %s for user %s UnapprochableSurlException: %s",
                surl, DataHelper.getRequestor(inputData), e.getMessage()));
        } else {
          log
            .info(String
              .format(
                "Unable to build a stori for surl %s UnapprochableSurlException: %s",
                surl, e.getMessage()));
        }
        try {
          SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
            TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage());
        } catch (UnknownSurlException e1) {
          log
            .error(
              "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
              e1);
        }
        surlReturnStatus.setStatus(new TReturnStatus(
          TStatusCode.SRM_AUTHORIZATION_FAILURE, e.getMessage()));
        return surlReturnStatus;
      } catch (IllegalArgumentException e) {
        log.error("Unable to build StoRI by SURL and user", e);
        try {
          SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
            TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
        } catch (UnknownSurlException e1) {
          log
            .error(
              "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
              e);
        }
        surlReturnStatus.setStatus(new TReturnStatus(
          TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
        return surlReturnStatus;
      } catch (NamespaceException e) {
        failure = true;
        if (inputData instanceof IdentityInputData) {
          log
            .info(String
              .format(
                "Unable to build a stori for surl %s for user %s NamespaceException: %s",
                surl, DataHelper.getRequestor(inputData), e.getMessage()));
        } else {
          log.info(String.format(
            "Unable to build a stori for surl %s NamespaceException: %s", surl,
            e.getMessage()));
        }
        try {
          SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
            TStatusCode.SRM_INTERNAL_ERROR, e.getMessage());
        } catch (UnknownSurlException e1) {
          log
            .error(
              "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
              e1);
        }
        surlReturnStatus.setStatus(new TReturnStatus(
          TStatusCode.SRM_INTERNAL_ERROR, e.getMessage()));
        return surlReturnStatus;
      } catch (InvalidSURLException e) {
        failure = true;
        if (inputData instanceof IdentityInputData) {
          log
            .info(String
              .format(
                "Unable to build a stori for surl %s for user %s InvalidSURLException: %s",
                surl, DataHelper.getRequestor(inputData), e.getMessage()));
        } else {
          log.info(String.format(
            "Unable to build a stori for surl %s InvalidSURLException: %s",
            surl, e.getMessage()));
        }
        try {
          SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
            TStatusCode.SRM_INVALID_PATH, e.getMessage());
        } catch (UnknownSurlException e1) {
          log
            .error(
              "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
              e1);
        }
        surlReturnStatus.setStatus(new TReturnStatus(
          TStatusCode.SRM_INVALID_PATH, e.getMessage()));
        return surlReturnStatus;
      }
    }

    // Get LocalFile and Remove
    fileToRemove = stori.getLocalFile();
    res = fileToRemove.delete();
    res = true;

    log.debug("PtPAbortExecutor: File removed.");

    // This method is used to mark as expired the specified entry
    // in the JitCatalog. In this way the next time the GarbageCollector
    // will wake up, it will remove the entry as expired.
    // The file is already removed, but the garbage collection is
    // smart enought to manage the case.

    // Change status to aborted
    if (failure) {
      // Something goes wrong during the update catalog operation
      try {
        SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
          TStatusCode.SRM_ABORTED, "Request aborted.");
      } catch (UnknownSurlException e) {
        log
          .error(
            "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
            e);
      }
      surlReturnStatus.setStatus(new TReturnStatus(
        TStatusCode.SRM_INTERNAL_ERROR, "Database Not Updated."));
      return surlReturnStatus;
    }

    if (res) {
      try {
        SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
          TStatusCode.SRM_ABORTED, "Request aborted.");
      } catch (UnknownSurlException e) {
        log
          .error(
            "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
            e);
      }
      surlReturnStatus.setStatus(new TReturnStatus(TStatusCode.SRM_SUCCESS,
        "File request successfully aborted."));
      return surlReturnStatus;
    } else {
      try {
        SurlStatusManager.updateStatus(TRequestType.PREPARE_TO_PUT, surl,
          TStatusCode.SRM_ABORTED, "Request aborted.");
      } catch (UnknownSurlException e) {
        log
          .error(
            "Unexpected UnknownSurlException in SurlStatusManager.updateStatus: ",
            e);
      }
      surlReturnStatus.setStatus(new TReturnStatus(
        TStatusCode.SRM_INTERNAL_ERROR, "File not removed."));
      return surlReturnStatus;
    }

  }

}
