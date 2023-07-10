/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.BoLPersistentChunkData;
import it.grid.storm.catalogs.InvalidSurlRequestDataAttributesException;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.InvalidDescendantsEmptyRequestException;
import it.grid.storm.namespace.InvalidDescendantsFileRequestException;
import it.grid.storm.namespace.InvalidDescendantsPathRequestException;
import it.grid.storm.namespace.InvalidSURLException;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.UnapprochableSurlException;
import it.grid.storm.scheduler.Delegable;
import it.grid.storm.scheduler.SchedulerException;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.DataHelper;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a BringOnLine Feeder: the Feeder that will handle the srmBringOnLine
 * statements. It chops a multifile request, and for each part it checks whether the dir option is
 * set and expands the directory as necessary.
 *
 * <p>If the request contains nothing to process, an error message gets logged, the number of queued
 * requests is decreased, and the number of finished requests is increased.
 *
 * <p>If the single part of the request has dirOption NOT set, then the number of queued requests is
 * decreased, the number of progressing requests is increased, the status of that chunk is changed
 * to SRM_REQUEST_INPROGRESS; the chunk is given to the scheduler for handling. In case the
 * scheduler cannot accept the chunk for any reason, a message with the requestToken and the chunk s
 * data is logged, status of the chunk passes to SRM_ABORTED, and at the end the counters are such
 * that the queued-requests is decreased while the finished-requests is increased.
 *
 * <p>If the single part of the request DOES have a dirOption set, then it is considered as an
 * expansion job and it gets handled now! So the number of queued requests is decreased and that for
 * progressing ones is increased, while the status is set to SRM_REQUEST_INPROGRESS. Each newly
 * expanded file gets handled as though it were part of the multifile request WITHOUT the dirOption
 * set, so it goes through the same steps as mentioned earlier on; notice that a new entry in the
 * persistence system is created, and the total number of files in this request is updated. Finally
 * the status of this expansion request is set to SRM_DONE, the number of progressing requests is
 * decreased and the number of finished requests is increased.
 *
 * <p>At the beginning of the expansion stage, some anomalous situations are considered and handled
 * as follows:
 *
 * <p>(0) In case of internal errors, they get logged and the expansion request gets failed: the
 * status changes to SRM_FAILURE, number of progressing is decreased, number of finished is
 * increased.
 *
 * <p>(1) The expanded directory is empty: the request is set to SRM_SUCCESS with an explanatory
 * String saying so. The number of progressing is decreased, and the number of finished is
 * increased.
 *
 * <p>(2) The directory does not exist: status set to SRM_INVALID_PATH; number of progressing is
 * decresed; number of finished is increased.
 *
 * <p>(3) Attempting to expand a file: status set to SRM_INVALID_PATH; number of progressing is
 * decreased; number of finished is increased.
 *
 * <p>(4) No rights to directory: status set to SRM_AUTHORIZATION_FAILURE; number of progressing is
 * decreased; number of finished is increased.
 *
 * @author CNAF
 * @date Aug, 2009
 * @version 1.0
 */
public final class BoLFeeder implements Delegable {

  private static Logger log = LoggerFactory.getLogger(BoLFeeder.class);
  /** RequestSummaryData this BoLFeeder refers to. */
  private RequestSummaryData rsd = null;
  /** GridUser for this BoLFeeder. */
  private GridUserInterface gu = null;
  /** Overall request status. */
  private GlobalStatusManager gsm = null;

  /**
   * Public constructor requiring the RequestSummaryData to which this BoLFeeder refers, as well as
   * the GridUser. If null objects are supplied, an InvalidBoLFeederAttributesException is thrown.
   */
  public BoLFeeder(RequestSummaryData rsd) throws InvalidBoLFeederAttributesException {

    if (rsd == null) {
      throw new InvalidBoLFeederAttributesException(null, null, null);
    }
    if (rsd.gridUser() == null) {
      throw new InvalidBoLFeederAttributesException(rsd, null, null);
    }
    try {

      gu = rsd.gridUser();
      this.rsd = rsd;
      gsm = new GlobalStatusManager(rsd.requestToken());

    } catch (InvalidOverallRequestAttributeException e) {

      log.error(e.getMessage(), e);
      throw new InvalidBoLFeederAttributesException(rsd, gu, null);
    }
  }

  /**
   * This method splits a multifile request as well as exapanding recursive ones; it then creates
   * the necessary tasks and loads them into the BoL chunk scheduler.
   */
  public void doIt() {

    log.debug("BoLFeeder: pre-processing {}", rsd.requestToken());
    // Get all parts in request
    Collection<BoLPersistentChunkData> chunks =
        BoLChunkCatalog.getInstance().lookup(rsd.requestToken());
    if (chunks.isEmpty()) {
      log.warn(
          "ATTENTION in BoLFeeder! This SRM BoL request contained nothing to process! "
              + rsd.requestToken());
      RequestSummaryCatalog.getInstance()
          .failRequest(rsd, "This SRM Get request contained nothing to process!");
    } else {
      manageChunks(chunks);
      log.debug("BoLFeeder: finished pre-processing {}", rsd.requestToken());
    }
  }

  /** Private method that handles the Collection of chunks associated with the srm command! */
  private void manageChunks(Collection<BoLPersistentChunkData> chunks) {

    log.debug("BoLFeeder - number of chunks in request: {}", chunks.size());
    for (BoLPersistentChunkData chunkData : chunks) {
      /* add chunk for global status consideration */
      gsm.addChunk(chunkData);
      if (TSURL.isValid(chunkData.getSURL())) {
        /*
         * fromSURL corresponds to This installation of StoRM: go on with processing!
         */
        if (chunkData.getDirOption().isDirectory()) {
          /* expand the directory and manage the children! */
          manageIsDirectory(chunkData);
        } else {
          /* manage the request directly without any expansion */
          manageNotDirectory(chunkData);
        }
      } else {
        /*
         * fromSURL does _not_ correspond to this installation of StoRM: fail chunk!
         */
        log.warn(
            "BoLFeeder: srmBoL contract violation! fromSURL does not "
                + "correspond to this machine!\n Request: {}\n Chunk: {}",
            rsd.requestToken(),
            chunkData);

        chunkData.changeStatusSRM_FAILURE(
            "SRM protocol violation! " + "Cannot do an srmBoL of a SURL that is not local!");

        BoLChunkCatalog.getInstance().update(chunkData);
        /* inform global status computation of the chunk failure */
        gsm.failedChunk(chunkData);
      }
    }
    /*
     * no more chunks need to be considered for the overall status computation
     */
    gsm.finishedAdding();
  }

  /** Private method that handles the case of dirOption NOT set! */
  private void manageNotDirectory(BoLPersistentChunkData auxChunkData) {

    log.debug("BoLFeeder - scheduling... ");
    auxChunkData.changeStatusSRM_REQUEST_INPROGRESS("srmBringOnLine chunk is being processed!");
    BoLChunkCatalog.getInstance().update(auxChunkData);
    try {
      /* hand it to scheduler! */
      SchedulerFacade.getInstance()
          .chunkScheduler()
          .schedule(new BoLPersistentChunk(gu, rsd, auxChunkData, gsm));
      log.debug("BoLFeeder - chunk scheduled.");
    } catch (InvalidRequestAttributesException e) {
      /*
       * for some reason gu, rsd or auxChunkData may be null! This should not be so!
       */
      log.error(
          "UNEXPECTED ERROR in BoLFeeder! Chunk could not be " + "created!\n{}", e.getMessage(), e);
      log.error("Request: {}" + rsd.requestToken());
      log.error("Chunk: {}" + auxChunkData);

      auxChunkData.changeStatusSRM_FAILURE(
          "StoRM internal error does" + " not allow this chunk to be processed!");

      BoLChunkCatalog.getInstance().update(auxChunkData);
      gsm.failedChunk(auxChunkData);
    } catch (SchedulerException e) {
      /* Internal error of scheduler! */
      log.error(
          "UNEXPECTED ERROR in ChunkScheduler! Chunk could not be " + "scheduled!\n{}",
          e.getMessage(),
          e);
      log.error("Request: {}", rsd.requestToken());
      log.error("Chunk: {}", auxChunkData);

      auxChunkData.changeStatusSRM_FAILURE(
          "StoRM internal scheduler " + "error prevented this chunk from being processed!");

      BoLChunkCatalog.getInstance().update(auxChunkData);
      gsm.failedChunk(auxChunkData);
    }
  }

  /** Private method that handles the case of a BoLChunkData having dirOption set! */
  private void manageIsDirectory(BoLPersistentChunkData chunkData) {

    log.debug("BoLFeeder - pre-processing Directory chunk...");
    chunkData.changeStatusSRM_REQUEST_INPROGRESS("srmBringOnLine chunk " + "is being processed!");
    BoLChunkCatalog.getInstance().update(chunkData);

    TSURL surl = chunkData.getSURL();
    String user = DataHelper.getRequestor(chunkData);

    StoRI stori = null;
    try {
      stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl, gu);
    } catch (IllegalArgumentException e) {
      log.error(
          "Unable to build a stori for surl {} for user {}. " + "IllegalArgumentException: {}",
          surl,
          user,
          e.getMessage(),
          e);
      chunkData.changeStatusSRM_INTERNAL_ERROR(e.getMessage());
    } catch (UnapprochableSurlException e) {
      log.info(
          "Unable to build a stori for surl {} for user {}. " + "UnapprochableSurlException: {}",
          surl,
          user,
          e.getMessage());
      chunkData.changeStatusSRM_AUTHORIZATION_FAILURE(e.getMessage());
    } catch (NamespaceException e) {
      log.error(
          "Unable to build a stori for surl {} for user {}. " + "NamespaceException: {}",
          surl,
          user,
          e.getMessage(),
          e);
      chunkData.changeStatusSRM_INTERNAL_ERROR(e.getMessage());
    } catch (InvalidSURLException e) {
      log.info(
          "Unable to build a stori for surl {} for user {}. " + "InvalidSURLException: {}",
          surl,
          user,
          e.getMessage());
      chunkData.changeStatusSRM_INVALID_PATH(e.getMessage());
    } finally {
      if (stori == null) {
        // failed:
        BoLChunkCatalog.getInstance().update(chunkData);
        gsm.failedChunk(chunkData);
        return;
      }
    }

    try {

      /* Collection of children! */
      Collection<StoRI> storiChildren = stori.getChildren(chunkData.getDirOption());
      log.debug("BoLFeeder - Number of children in parent: {}", storiChildren.size());

      TDirOption notDir = new TDirOption(false, false, 0);
      BoLPersistentChunkData childData;

      for (StoRI storiChild : storiChildren) {
        try {
          childData =
              new BoLPersistentChunkData(
                  chunkData.getRequestToken(),
                  storiChild.getSURL(),
                  chunkData.getLifeTime(),
                  notDir,
                  chunkData.getTransferProtocols(),
                  chunkData.getFileSize(),
                  chunkData.getStatus(),
                  chunkData.getTransferURL(),
                  chunkData.getDeferredStartTime());

          /* fill in new db row and set the PrimaryKey of ChildData! */
          BoLChunkCatalog.getInstance().addChild(childData);

          log.debug("BoLFeeder - added child data: {}", childData);
          /* add chunk for global status consideration */
          gsm.addChunk(childData);

          manageNotDirectory(childData);
        } catch (InvalidSurlRequestDataAttributesException e) {
          /*
           * For some reason it was not possible to create a BoLChunkData: it is a programme bug!!!
           * It should not occur!!! Log it and skip to the next one!
           */
          log.error(
              "ERROR in BoLFeeder! While expanding recursive request, "
                  + "it was not possible to create a new BoLChunkData! {}",
              e.getMessage(),
              e);
        }
      }
      log.debug("BoLFeeder - expansion completed.");

      chunkData.changeStatusSRM_SUCCESS(
          "srmBringOnLine with dirOption" + " set: request successfully expanded!");

      BoLChunkCatalog.getInstance().update(chunkData);
      gsm.successfulChunk(chunkData);

    } catch (InvalidTDirOptionAttributesException e) {

      /* Could not create TDirOption that specifies no-expansion! */
      chunkData.changeStatusSRM_FAILURE(
          "srmBringOnLine with dirOption set:" + " expansion failure due to internal error!");
      BoLChunkCatalog.getInstance().update(chunkData);

      log.error(
          "UNEXPECTED ERROR in BoLFeeder! Could not create TDirOption "
              + "specifying non-expansion!\n{}",
          e.getMessage(),
          e);
      log.error("Request: {}", rsd.requestToken());
      log.error("Chunk: {}", chunkData);
      gsm.failedChunk(chunkData);

    } catch (InvalidDescendantsEmptyRequestException e) {

      chunkData.changeStatusSRM_SUCCESS(
          "BEWARE! srmBringOnLine with "
              + "dirOption set: it referred to a directory that was empty!");

      BoLChunkCatalog.getInstance().update(chunkData);

      log.debug(
          "ATTENTION in BoLFeeder! BoLFeeder received request " + "to expand empty directory.");
      gsm.successfulChunk(chunkData);

    } catch (InvalidDescendantsPathRequestException e) {

      /* Attempting to expand non existent directory! */
      chunkData.changeStatusSRM_INVALID_PATH(
          "srmBringOnLine with dirOption " + "set: it referred to a non-existent directory!");

      BoLChunkCatalog.getInstance().update(chunkData);

      log.debug(
          "ATTENTION in BoLFeeder! BoLFeeder received request to expand "
              + "non-existing directory.");
      gsm.failedChunk(chunkData);

    } catch (InvalidDescendantsFileRequestException e) {

      /* Attempting to expand a file! */
      chunkData.changeStatusSRM_INVALID_PATH(
          "srmBringOnLine with dirOption " + "set: a file was asked to be expanded!");

      BoLChunkCatalog.getInstance().update(chunkData);

      log.debug("ATTENTION in BoLFeeder! BoLFeeder received request to expand " + "a file.");
      gsm.failedChunk(chunkData);
    }
  }

  /**
   * Method used by chunk scheduler for internal logging; it returns the request id of This
   * BoLFeeder!
   */
  public String getName() {

    return "BoLFeeder of request: " + rsd.requestToken();
  }
}
