/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.BoLPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.scheduler.PersistentRequestChunk;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.tape.recalltable.model.TapeRecallStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that represents a chunk of an srmBringOnLine request: it handles a single file of a
 * multifile/directory-expansion request. StoRM then sends the chunk to a chunk-scheduler. Security
 * checks performed as follows: both in the JiT and AoT approach, policies are checked to see if the
 * Griduser has read rights on the requested SURL. If the AuthorisationCollector replies with an
 * isDeny, then the request fails with SRM_AUTHORIZATION_FAILURE status. If the
 * AuthorisationCollector replies with isIndeterminate, then the request fails with SRM_FAILURE and
 * explanation string "Failure in PolicySource prevented PolicyCollector from establishing access
 * rights! Processing failed!"; a message gets logged as well. If the AuthorisationCollector replies
 * with isNotApplicabale, then the request fails with SRM_FAILURE and error string "No policies
 * found for the requested SURL! Therefore access rights cannot be established! Processing cannot
 * continue!"; a message gets logged as well. If the AuthorisationCollector returns a state for
 * which there is no business logic, then the request fails again with SRM_FAILURE and error string
 * "Unexpected authorization state! Processing failed!"; a message gets logged. If the
 * AuthorisationCollector returns an isPermit, then processing continues as follows: (1) The local
 * file that corresponds to the supplied SURL is determined, together with the local user to whom
 * the grid credentials get mapped; the TURL finally gets constructed. If the local file does not
 * exist the request fails with SRM_INVALID_PATH and corresponding explanation string; if the user
 * cannot be mapped locally, the request fails with SRM_FAILURE and an explanation String which
 * includes the DN used for maping; if there are internal problems constructing the TURL again the
 * request fails with SRM_FAILURE. Appropriate error messages get logged. (2) Traverse permissions
 * get set on all parent directories to allow access to the file. The operation may fail for several
 * reasons: the file or any of the parent directories may have been removed resulting in
 * SRM_INVALID_PATH; StoRM cannot set the requested permissions because a filesystem mask does not
 * allow the permissions to be set up; StoRM may be configured for the wrong filesystem; StoRM has
 * not got the right permissions to manipulate the ACLs on the filesystem; StoRM may have
 * encountered an unexpected error when working with the filesystem. In all these circumstances, the
 * status changes to SRM_FAILURE, together with an appropriate explanation String, and a respective
 * log message. (3) The file size is determined. The operation may fail and hence the request too
 * gets failed, in the following circumstances: the file somehow does not exist, the path to the
 * file is not found, an error while communicating with the underlaying FileSystem, or a JVM
 * SecurityManager forbids such operation. In the first two cases the state changes to
 * SRM_INVALID_PATH, while in the other ones it changes to SRM_FAILURE; proper error strings explain
 * the situation further. Error messages get logged. (3) If AoT acls are in place, then the
 * PinnedFilesCatalog is asked to pinExistingVolatileEntry, that is, it is asked to pin the entry if
 * it is already present thereby extending its lifetime (if it is not present, it just means that
 * the requested file is PERMANENT and there is no need to pin it); status changes to
 * SRM_FILE_PINNED. (4) If it is the JiT that is in place, a ReadACL is first added, the ACL gets
 * tracked by calling the proper method in the PinnedFilesCatalog, and the file gets pinned by
 * invoking pinExistingVolatileEntry; the status changes to SRM_FILE_PINNED. The addition of the ACL
 * could go wrong in several ways: the file may not exist, the ACL could not be set up because there
 * is a mask that does not allow the Read permission to be set, StoRM does not have the permission
 * to manipulate the ACLs, StoRM was not configured for the underlying FileSystem, or there was an
 * unexpected error; in the first case the status changes to SRM_INVALID_PATH, while in all other
 * ones it changes to SRM_FAILURE; corresponding messages get logged.
 * 
 * @author CNAF
 * @date Aug 2009
 * @version 1.0
 */
public class BoLPersistentChunk extends BoL implements PersistentRequestChunk {

  private static Logger log = LoggerFactory.getLogger(BoLPersistentChunk.class);

  /**
   * RequestSummaryData containing all the statistics for the originating srmBringOnLineRequest
   */
  private RequestSummaryData rsd = null;
  /** manager for global status computation */
  private GlobalStatusManager gsm = null;

  /**
   * Constructor requiring the GridUser, the RequestSummaryData and the BoLChunkData about this
   * chunk. If the supplied attributes are null, an InvalidBoLChunkAttributesException is thrown.
   */
  public BoLPersistentChunk(GridUserInterface gu, RequestSummaryData rsd,
      BoLPersistentChunkData chunkData, GlobalStatusManager gsm)
      throws InvalidRequestAttributesException, InvalidPersistentRequestAttributesException {

    super(gu, chunkData);
    if (rsd == null || gsm == null) {
      throw new InvalidPersistentRequestAttributesException(gu, rsd, chunkData, gsm);
    }
    this.rsd = rsd;
    this.gsm = gsm;
  }

  @Override
  public Boolean completeRequest(TapeRecallStatus recallStatus) {

    boolean requestSuccessfull = super.completeRequest(recallStatus);
    persistStatus();
    if (requestSuccessfull) {
      gsm.successfulChunk((BoLPersistentChunkData) requestData);
      log.info("Completed BoL request ({}), file successfully recalled from tape: {}",
          rsd.requestToken(), requestData.getSURL().toString());
    } else {
      gsm.failedChunk((BoLPersistentChunkData) requestData);
      log.error("BoL request ({}), file not recalled from tape: {}", requestData.getRequestToken(),
          requestData.getSURL().toString());
    }
    return requestSuccessfull;
  }

  /**
   * Method that supplies a String describing this BoLChunk - for scheduler Log purposes! It returns
   * the request token and the SURL that was asked for.
   */
  @Override
  public String getName() {

    return String.format("BoLChunk of request %s for SURL %s", rsd.requestToken(),
        requestData.getSURL());
  }

  public String getRequestToken() {

    return rsd.requestToken().toString();
  }

  @Override
  public void updateGlobalStatus() {

    /*
     * If the status is still SRM_REQUEST_INPROGRESS means that the file is on the tape and it's
     * being recalled. The status will be changed to SRM_FILE_PINNED by the function that updates
     * the TapeRecall table putting a SUCCESS in the corresponding row (and the file will have been
     * recalled).
     */
    if (TStatusCode.SRM_REQUEST_INPROGRESS.equals(requestData.getStatus().getStatusCode())) {

      return;
    }
    if (failure) {
      gsm.failedChunk((BoLPersistentChunkData) requestData);
    } else {
      gsm.successfulChunk((BoLPersistentChunkData) requestData);
    }
  }

  @Override
  public void persistStatus() {

    BoLChunkCatalog.getInstance().update((BoLPersistentChunkData) requestData);
  }
}
