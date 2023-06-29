/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.catalogs.InvalidSurlRequestDataAttributesException;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtGPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.RequestSummaryData;
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
 * This class represents a PrepareToGet Feeder: the Feeder that will handle the srmPrepareToGet
 * statements. It chops a multifile request, and for each part it checks whether the dir option is
 * set and expands the directory as necessary.
 * 
 * If the request contains nothing to process, an error message gets logged, the number of queued
 * requests is decreased, and the number of finished requests is increased.
 * 
 * If the single part of the request has dirOption NOT set, then the number of queued requests is
 * decreased, the number of progressing requests is increased, the status of that chunk is changed
 * to SRM_REQUEST_INPROGRESS; the chunk is given to the scheduler for handling. In case the
 * scheduler cannot accept the chunk for any reason, a messagge with the requestToken and the chunk
 * s data is logged, status of the chunk passes to SRM_ABORTED, and at the end the counters are such
 * that the queued-requests is decreased while the finished-requests is increased.
 * 
 * If the single part of the request DOES have a dirOption set, then it is considered as an
 * expansion job and it gets handled now! So the number of queued requests is decreased and that for
 * progressing ones is increased, while the status is set to SRM_REQUEST_INPROGRESS. Each newly
 * expanded file gets handled as though it were part of the multifile request WITHOUT the dirOption
 * set, so it goes through the same steps as mentioned earlier on; notice that a new entry in the
 * persistence system is created, and the total number of files in this request is updated. Finally
 * the status of this expansion request is set to SRM_DONE, the number of progressing requests is
 * decreased and the number of finished requests is increased.
 * 
 * At the beginning of the expansion stage, some anomalous situations are considered and handled as
 * follows:
 * 
 * (0) In case of internal errors, they get logged and the expansion request gets failed: the status
 * changes to SRM_FAILURE, number of progressing is decreased, number of finished is increased.
 * 
 * (1) The expanded directory is empty: the request is set to SRM_SUCCESS with an explanatory String
 * saying so. The number of progressing is decreased, and the number of finished is increased.
 * 
 * (2) The directory does not exist: status set to SRM_INVALID_PATH; number of progressing is
 * decresed; number of finished is increased.
 * 
 * (3) Attempting to expand a file: status set to SRM_INVALID_PATH; number of progressing is
 * decreased; number of finished is increased.
 * 
 * (4) No rights to directory: status set to SRM_AUTHORIZATION_FAILURE; number of progressing is
 * decreased; number of finished is increased.
 * 
 * 
 * @author EGRID - ICTP Trieste
 * @date March 21st, 2005
 * @version 4.0
 */
public final class PtGFeeder implements Delegable {

  private static Logger log = LoggerFactory.getLogger(PtGFeeder.class);
  /* RequestSummaryData this PtGFeeder refers to. */
  private RequestSummaryData rsd = null;
  /* Overall request status. */
  private GlobalStatusManager gsm = null;

  /**
   * Public constructor requiring the RequestSummaryData to which this PtGFeeder refers, as well as
   * the GridUser. If null objects are supplied, an InvalidPtGFeederAttributesException is thrown.
   * 
   * @param rsd
   * @throws InvalidPtGFeederAttributesException
   */
  public PtGFeeder(RequestSummaryData rsd) throws InvalidPtGFeederAttributesException {

    if (rsd == null) {
      throw new InvalidPtGFeederAttributesException(null, null, null);
    }
    if (rsd.gridUser() == null) {
      throw new InvalidPtGFeederAttributesException(rsd, null, null);
    }
    try {
      this.rsd = rsd;
      gsm = new GlobalStatusManager(rsd.requestToken());
    } catch (InvalidOverallRequestAttributeException e) {
      log.error(
          "ATTENTION in PtGFeeder! Programming bug when creating " + "GlobalStatusManager! {}",
          e.getMessage(), e);
      throw new InvalidPtGFeederAttributesException(rsd, null, null);
    }
  }

  /**
   * This method splits a multifile request as well as exapanding recursive ones; it then creates
   * the necessary tasks and loads them into the PtG chunk scheduler.
   */
  public void doIt() {

    log.debug("PtGFeeder: pre-processing {}", rsd.requestToken());
    // Get all parts in request
    Collection<PtGPersistentChunkData> chunks =
        PtGChunkCatalog.getInstance().lookup(rsd.requestToken());
    if (chunks.isEmpty()) {
      log.warn("ATTENTION in PtGFeeder! This SRM PtG request contained nothing " + "to process! {}",
          rsd.requestToken());
      RequestSummaryCatalog.getInstance()
        .failRequest(rsd, "This SRM Get request contained nothing to process!");
    } else {
      manageChunks(chunks);
      log.debug("PtGFeeder: finished pre-processing {}", rsd.requestToken());
    }
  }

  /**
   * Private method that handles the Collection of chunks associated with the srm command!
   */
  private void manageChunks(Collection<PtGPersistentChunkData> chunks) {

    log.debug("PtGFeeder - number of chunks in request: {}", chunks.size());
    for (PtGPersistentChunkData chunkData : chunks) {
      gsm.addChunk(chunkData); // add chunk for global status
      // consideration
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
        log.warn("PtGFeeder: srmPtG contract violation! fromSURL does not"
            + "correspond to this machine!");
        log.warn("Request: {}", rsd.requestToken());
        log.warn("Chunk: {}", chunkData);

        chunkData.changeStatusSRM_FAILURE(
            "SRM protocol violation!" + " Cannot do an srmPtG of a SURL that is not local!");

        /* update persistence!!! */
        PtGChunkCatalog.getInstance().update(chunkData);
        /* inform global status computation of the chunk s failure */
        gsm.failedChunk(chunkData);
      }
    }
    /*
     * no more chunks need to be cosidered for the overall status computation
     */
    gsm.finishedAdding();
  }

  /**
   * Private method that handles the case of dirOption NOT set!
   * 
   * @param auxChunkData
   */
  private void manageNotDirectory(PtGPersistentChunkData auxChunkData) {

    log.debug("PtGFeeder - scheduling... ");
    /* change status of this chunk to being processed! */
    auxChunkData
      .changeStatusSRM_REQUEST_INPROGRESS("srmPrepareToGet " + "chunk is being processed!");
    PtGChunkCatalog.getInstance().update(auxChunkData);
    try {
      /* hand it to scheduler! */
      SchedulerFacade.getInstance()
        .chunkScheduler()
        .schedule(new PtGPersistentChunk(rsd, auxChunkData, gsm));
      log.debug("PtGFeeder - chunk scheduled.");
    } catch (InvalidPersistentRequestAttributesException e) {
      log.error("UNEXPECTED ERROR in PtGFeeder! Chunk could not be created!");
      log.error(e.getMessage(), e);
      log.error("Request: {}", rsd.requestToken());
      log.error("Chunk: {}", auxChunkData);

      auxChunkData.changeStatusSRM_FAILURE(
          "StoRM internal error does" + " not allow this chunk to be processed!");

      PtGChunkCatalog.getInstance().update(auxChunkData);
      gsm.failedChunk(auxChunkData);
    } catch (InvalidRequestAttributesException e) {
      log.error("UNEXPECTED ERROR in PtGFeeder! Chunk could not be created!");
      log.error(e.getMessage(), e);
      log.error("Request: {}", rsd.requestToken());
      log.error("Chunk: {}", auxChunkData);

      auxChunkData.changeStatusSRM_FAILURE(
          "StoRM internal error does" + " not allow this chunk to be processed!");

      PtGChunkCatalog.getInstance().update(auxChunkData);
      gsm.failedChunk(auxChunkData);
    } catch (SchedulerException e) {
      /* Internal error of scheduler! */
      log.error("UNEXPECTED ERROR in ChunkScheduler! Chunk could not be scheduled!");
      log.error(e.getMessage(), e);
      log.error("Request: {}", rsd.requestToken());
      log.error("Chunk: {}", auxChunkData);

      auxChunkData.changeStatusSRM_FAILURE(
          "StoRM internal scheduler " + "error prevented this chunk from being processed!");

      PtGChunkCatalog.getInstance().update(auxChunkData);
      gsm.failedChunk(auxChunkData);
    }
  }

  /**
   * Private method that handles the case of a PtGChunkData having dirOption set!
   * 
   * @param chunkData
   */
  private void manageIsDirectory(PtGPersistentChunkData chunkData) {

    log.debug("PtGFeeder - pre-processing Directory chunk...");
    /* Change status of this chunk to being processed! */
    chunkData.changeStatusSRM_REQUEST_INPROGRESS("srmPrepareToGet " + "chunk is being processed!");

    TSURL surl = chunkData.getSURL();
    String user = DataHelper.getRequestor(chunkData);

    /* update persistence!!! */
    PtGChunkCatalog.getInstance().update(chunkData);

    /* Build StoRI for current chunk */
    StoRI stori = null;
    try {
      stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl, chunkData.getUser());
    } catch (IllegalArgumentException e) {
      log.error(
          "Unable to build a stori for surl {} for user {}. " + "IllegalArgumentException: {}",
          surl, user, e.getMessage(), e);
      chunkData.changeStatusSRM_INTERNAL_ERROR(e.getMessage());
    } catch (UnapprochableSurlException e) {
      log.info(
          "Unable to build a stori for surl {} for user {}. " + "UnapprochableSurlException: {}",
          surl, user, e.getMessage());
      chunkData.changeStatusSRM_AUTHORIZATION_FAILURE(e.getMessage());
    } catch (NamespaceException e) {
      log.error("Unable to build a stori for surl {} for user {}. " + "NamespaceException: {}",
          surl, user, e.getMessage(), e);
      chunkData.changeStatusSRM_INTERNAL_ERROR(e.getMessage());
    } catch (InvalidSURLException e) {
      log.info("Unable to build a stori for surl {} for user {}. " + "InvalidSURLException: {}",
          surl, user, e.getMessage());
      chunkData.changeStatusSRM_INVALID_PATH(e.getMessage());
    } finally {
      if (stori == null) {
        // failed!
        PtGChunkCatalog.getInstance().update(chunkData);
        gsm.failedChunk(chunkData);
        return;
      }
    }

    Collection<StoRI> storiChildren = null;

    try {

      storiChildren = stori.getChildren(chunkData.getDirOption());

    } catch (InvalidDescendantsEmptyRequestException e) {

      log.debug(
          "ATTENTION in PtGFeeder! PtGFeeder received request to expand " + "empty directory.");
      /*
       * The expanded directory was empty, anyway a request on a Directory is considered done
       * whether there is somethig to expand or not!
       */
      chunkData.changeStatusSRM_FILE_PINNED("BEWARE! srmPrepareToGet with "
          + "dirOption set: it referred to a directory that was empty!");
      PtGChunkCatalog.getInstance().update(chunkData);
      gsm.successfulChunk(chunkData);
      return;

    } catch (InvalidDescendantsPathRequestException e) {

      log.debug("ATTENTION in PtGFeeder! PtGFeeder received request"
          + " to expand non-existing directory.");
      // Attempting to expand non existent directory!
      chunkData.changeStatusSRM_INVALID_PATH(
          "srmPrepareToGet with dirOption " + "set: it referred to a non-existent directory!");
      PtGChunkCatalog.getInstance().update(chunkData);
      gsm.failedChunk(chunkData);
      return;

    } catch (InvalidDescendantsFileRequestException e) {

      log.debug("ATTENTION in PtGFeeder! PtGFeeder received request to " + "expand a file.");
      // Attempting to expand a file!
      chunkData.changeStatusSRM_INVALID_PATH(
          "srmPrepareToGet with dirOption " + "set: a file was asked to be expanded!");
      PtGChunkCatalog.getInstance().update(chunkData);
      gsm.failedChunk(chunkData);
      return;

    }

    log.debug("PtGFeeder - Number of children in parent: {}", storiChildren.size());

    TDirOption notDir = null;

    try {

      notDir = new TDirOption(false, false, 0);

    } catch (InvalidTDirOptionAttributesException e) {

      log.error(
          "UNEXPECTED ERROR in PtGFeeder! Could not create TDirOption "
              + "specifying non-expansion!\n{}\nRequest: {}\nChunk: {}",
          e.getMessage(), rsd.requestToken(), chunkData, e);

      chunkData.changeStatusSRM_FAILURE(
          "srmPrepareToGet with dirOption set:" + " expansion failure due to internal error!");
      PtGChunkCatalog.getInstance().update(chunkData);
      gsm.failedChunk(chunkData);
      return;
    }

    PtGPersistentChunkData childData;
    for (StoRI storiChild : storiChildren) {
      try {
        childData = new PtGPersistentChunkData(chunkData.getUser(), chunkData.getRequestToken(),
            storiChild.getSURL(), chunkData.getPinLifeTime(), notDir,
            chunkData.getTransferProtocols(), chunkData.getFileSize(), chunkData.getStatus(),
            chunkData.getTransferURL());
        /* fill in new db row and set the PrimaryKey of ChildData! */
        PtGChunkCatalog.getInstance().addChild(childData);
        log.debug("PtGFeeder - added child data: {}", childData);

        /* add chunk for global status consideration */
        gsm.addChunk(childData);
        /* manage chunk */
        manageNotDirectory(childData);
      } catch (InvalidSurlRequestDataAttributesException e) {
        log.error(
            "ERROR in PtGFeeder! While expanding recursive request,"
                + " it was not possible to create a new PtGPersistentChunkData! {}",
            e.getMessage(), e);
      }
    }
    log.debug("PtGFeeder - expansion completed.");
    /*
     * A request on a Directory is considered done whether there is something to expand or not!
     */
    chunkData.changeStatusSRM_FILE_PINNED(
        "srmPrepareToGet with dirOption " + "set: request successfully expanded!");
    PtGChunkCatalog.getInstance().update(chunkData);
    gsm.successfulChunk(chunkData);
  }

  /**
   * Method used by chunk scheduler for internal logging; it returns the request id of This
   * PtGFeeder!
   */
  public String getName() {

    return "PtGFeeder of request: " + rsd.requestToken();
  }
}
