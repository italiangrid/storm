/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.converter.PinLifetimeConverter;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.converter.TransferProtocolListConverter;
import it.grid.storm.persistence.dao.BoLChunkDAO;
import it.grid.storm.persistence.exceptions.InvalidReducedBoLChunkDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidSurlRequestDataAttributesException;
import it.grid.storm.persistence.impl.mysql.BoLChunkDAOMySql;
import it.grid.storm.persistence.model.BoLChunkDataTO;
import it.grid.storm.persistence.model.BoLPersistentChunkData;
import it.grid.storm.persistence.model.ReducedBoLChunkData;
import it.grid.storm.persistence.model.ReducedBoLChunkDataTO;
import it.grid.storm.srm.types.InvalidTDirOptionAttributesException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

public class BoLChunkCatalog {

  private static final Logger log = LoggerFactory.getLogger(BoLChunkCatalog.class);

  private final BoLChunkDAO dao;

  private static BoLChunkCatalog instance;

  public static synchronized BoLChunkCatalog getInstance() {
    if (instance == null) {
      instance = new BoLChunkCatalog();
    }
    return instance;
  }

  /**
   * Private constructor that starts the internal timer needed to periodically check and transit
   * requests whose pinLifetime has expired and are in SRM_FILE_PINNED, to SRM_RELEASED.
   */
  private BoLChunkCatalog() {

    dao = BoLChunkDAOMySql.getInstance();
  }

  /**
   * Method that returns a Collection of BoLChunkData Objects matching the supplied TRequestToken.
   * 
   * If any of the data associated to the TRequestToken is not well formed and so does not allow a
   * BoLChunkData Object to be created, then that part of the request is dropped and gets logged,
   * and the processing continues with the next part. All valid chunks get returned: the others get
   * dropped.
   * 
   * If there are no chunks to process then an empty Collection is returned, and a message gets
   * logged.
   */
  synchronized public Collection<BoLPersistentChunkData> lookup(TRequestToken rt) {

    Collection<BoLChunkDataTO> chunkCollection = dao.find(rt);
    log.debug("BoL CHUNK CATALOG: retrieved data {}", chunkCollection);
    List<BoLPersistentChunkData> list = new ArrayList<BoLPersistentChunkData>();

    if (chunkCollection.isEmpty()) {
      log.warn("BoL CHUNK CATALOG! No chunks found in persistence for specified request: {}", rt);
      return list;
    }

    BoLPersistentChunkData chunk;
    for (BoLChunkDataTO chunkTO : chunkCollection) {
      chunk = makeOne(chunkTO, rt);
      if (chunk == null) {
        continue;
      }
      list.add(chunk);
      if (isComplete(chunkTO)) {
        continue;
      }
      try {
        dao.updateIncomplete(completeTO(chunkTO, chunk));
      } catch (InvalidReducedBoLChunkDataAttributesException e) {
        log.warn(
            "BoL CHUNK CATALOG! unable to add missing informations on DB " + "to the request: {}",
            e.getMessage());
      }
    }
    log.debug("BoL CHUNK CATALOG: returning " + list);
    return list;
  }

  /**
   * Generates a BoLChunkData from the received BoLChunkDataTO
   * 
   * @param auxTO
   * @param rt
   * @return
   */
  private BoLPersistentChunkData makeOne(BoLChunkDataTO auxTO, TRequestToken rt) {

    StringBuilder errorSb = new StringBuilder();
    TSURL fromSURL = null;
    try {
      fromSURL = TSURL.makeFromStringValidate(auxTO.getFromSURL());
    } catch (InvalidTSURLAttributesException e) {
      errorSb.append(e);
    }
    if (auxTO.normalizedStFN() != null) {
      fromSURL.setNormalizedStFN(auxTO.normalizedStFN());
    }
    if (auxTO.sulrUniqueID() != null) {
      fromSURL.setUniqueID(auxTO.sulrUniqueID().intValue());
    }
    // lifeTime
    TLifeTimeInSeconds lifeTime = null;
    try {
      long pinLifeTime = PinLifetimeConverter.getInstance().toStoRM(auxTO.getLifeTime());
      // Check for max value allowed
      long max = Configuration.getInstance().getPinLifetimeMaximum();
      if (pinLifeTime > max) {
        log.warn("PinLifeTime is greater than the max value allowed. "
            + "Drop the value to the max = {} seconds", max);
        pinLifeTime = max;
      }
      lifeTime = TLifeTimeInSeconds.make(pinLifeTime, TimeUnit.SECONDS);
    } catch (IllegalArgumentException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // dirOption
    TDirOption dirOption = null;
    try {
      dirOption =
          new TDirOption(auxTO.getDirOption(), auxTO.getAllLevelRecursive(), auxTO.getNumLevel());
    } catch (InvalidTDirOptionAttributesException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // transferProtocols
    TURLPrefix transferProtocols = TransferProtocolListConverter.toSTORM(auxTO.getProtocolList());
    if (transferProtocols.size() == 0) {
      errorSb
        .append("\nEmpty list of TransferProtocols or" + " could not translate TransferProtocols!");
      /* fail construction of BoLChunkData! */
      transferProtocols = null;
    }
    // fileSize
    TSizeInBytes fileSize = null;
    try {
      fileSize = TSizeInBytes.make(auxTO.getFileSize());
    } catch (InvalidTSizeAttributesException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // status
    TReturnStatus status = null;
    TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.getStatus());
    if (code == TStatusCode.EMPTY) {
      errorSb.append("\nRetrieved StatusCode was not recognised: " + auxTO.getStatus());
    } else {
      status = new TReturnStatus(code, auxTO.getErrString());
    }
    // transferURL
    /*
     * whatever is read is just meaningless because BoL will fill it in!!! So create an Empty TTURL
     * by default! Vital to avoid problems with unknown DPM NULL/EMPTY logic policy!
     */
    TTURL transferURL = TTURL.makeEmpty();
    // make BoLChunkData
    BoLPersistentChunkData aux = null;
    try {
      aux = new BoLPersistentChunkData(rt, fromSURL, lifeTime, dirOption, transferProtocols,
          fileSize, status, transferURL, auxTO.getDeferredStartTime());
      aux.setPrimaryKey(auxTO.getPrimaryKey());
    } catch (InvalidSurlRequestDataAttributesException e) {
      dao.updateStatus(auxTO, SRM_FAILURE, "Request is malformed!");
      log.warn("BoL CHUNK CATALOG! Retrieved malformed BoL "
          + "chunk data from persistence. Dropping chunk from request {}", rt);
      log.warn(e.getMessage(), e);
      log.warn(errorSb.toString());
    }
    // end...
    return aux;
  }

  /**
   * 
   * Adds to the received BoLChunkDataTO the normalized StFN and the SURL unique ID taken from the
   * BoLChunkData
   * 
   * @param chunkTO
   * @param chunk
   */
  private void completeTO(ReducedBoLChunkDataTO chunkTO, final ReducedBoLChunkData chunk) {

    chunkTO.setNormalizedStFN(chunk.fromSURL().normalizedStFN());
    chunkTO.setSurlUniqueID(Integer.valueOf(chunk.fromSURL().uniqueId()));
  }

  /**
   * 
   * Creates a ReducedBoLChunkDataTO from the received BoLChunkDataTO and completes it with the
   * normalized StFN and the SURL unique ID taken from the PtGChunkData
   * 
   * @param chunkTO
   * @param chunk
   * @return
   * @throws InvalidReducedBoLChunkDataAttributesException
   */
  private ReducedBoLChunkDataTO completeTO(BoLChunkDataTO chunkTO,
      final BoLPersistentChunkData chunk) throws InvalidReducedBoLChunkDataAttributesException {

    ReducedBoLChunkDataTO reducedChunkTO = this.reduce(chunkTO);
    this.completeTO(reducedChunkTO, this.reduce(chunk));
    return reducedChunkTO;
  }

  /**
   * Creates a ReducedBoLChunkData from the data contained in the received BoLChunkData
   * 
   * @param chunk
   * @return
   * @throws InvalidReducedBoLChunkDataAttributesException
   */
  private ReducedBoLChunkData reduce(BoLPersistentChunkData chunk)
      throws InvalidReducedBoLChunkDataAttributesException {

    ReducedBoLChunkData reducedChunk = new ReducedBoLChunkData(chunk.getSURL(), chunk.getStatus());
    reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
    return reducedChunk;
  }

  /**
   * Creates a ReducedBoLChunkDataTO from the data contained in the received BoLChunkDataTO
   * 
   * @param chunkTO
   * @return
   */
  private ReducedBoLChunkDataTO reduce(BoLChunkDataTO chunkTO) {

    ReducedBoLChunkDataTO reducedChunkTO = new ReducedBoLChunkDataTO();
    reducedChunkTO.setPrimaryKey(chunkTO.getPrimaryKey());
    reducedChunkTO.setFromSURL(chunkTO.getFromSURL());
    reducedChunkTO.setNormalizedStFN(chunkTO.normalizedStFN());
    reducedChunkTO.setSurlUniqueID(chunkTO.sulrUniqueID());
    reducedChunkTO.setStatus(chunkTO.getStatus());
    reducedChunkTO.setErrString(chunkTO.getErrString());
    return reducedChunkTO;
  }

  /**
   * Checks if the received BoLChunkDataTO contains the fields not set by the front end but required
   * 
   * @param chunkTO
   * @return
   */
  private boolean isComplete(BoLChunkDataTO chunkTO) {

    return (chunkTO.normalizedStFN() != null) && (chunkTO.sulrUniqueID() != null);
  }

  /**
   * Method used to update into Persistence a retrieved BoLChunkData. In case any error occurs, the
   * operation does not proceed but no Exception is thrown. Error messages get logged.
   * 
   * Only fileSize, StatusCode, errString and transferURL are updated. Likewise for the request
   * pinLifetime.
   */
  synchronized public void update(BoLPersistentChunkData cd) {

    BoLChunkDataTO to = new BoLChunkDataTO();
    /* Primary key needed by DAO Object */
    to.setPrimaryKey(cd.getPrimaryKey());
    to.setFileSize(cd.getFileSize().value());
    to.setStatus(StatusCodeConverter.getInstance().toDB(cd.getStatus().getStatusCode()));
    to.setErrString(cd.getStatus().getExplanation());
    to.setLifeTime(PinLifetimeConverter.getInstance().toDB(cd.getLifeTime().value()));
    to.setNormalizedStFN(cd.getSURL().normalizedStFN());
    to.setSurlUniqueID(Integer.valueOf(cd.getSURL().uniqueId()));
    dao.update(to);
  }

  /**
   * Method used to add into Persistence a new entry. The supplied BoLChunkData gets the primary key
   * changed to the value assigned in Persistence.
   * 
   * This method is intended to be used by a recursive BoL request: the parent request supplies a
   * directory which must be expanded, so all new children requests resulting from the files in the
   * directory are added into persistence.
   * 
   * So this method does _not_ add a new SRM prepare_to_get request into the DB!
   * 
   * The only children data written into the DB are: sourceSURL, TDirOption, statusCode and
   * explanation.
   * 
   * In case of any error the operation does not proceed, but no Exception is thrown! Proper
   * messages get logged by underlying DAO.
   */
  synchronized public void addChild(BoLPersistentChunkData chunkData) {

    BoLChunkDataTO to = new BoLChunkDataTO();
    // needed for now to find ID of request! Must be changed soon!
    to.setRequestToken(chunkData.getRequestToken().toString());
    to.setFromSURL(chunkData.getSURL().toString());
    to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
    to.setSurlUniqueID(Integer.valueOf(chunkData.getSURL().uniqueId()));

    to.setAllLevelRecursive(chunkData.getDirOption().isAllLevelRecursive());
    to.setDirOption(chunkData.getDirOption().isDirectory());
    to.setNumLevel(chunkData.getDirOption().getNumLevel());
    to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
    to.setErrString(chunkData.getStatus().getExplanation());
    to.setDeferredStartTime(chunkData.getDeferredStartTime());

    /* add the entry and update the Primary Key field */
    dao.addChild(to);
    chunkData.setPrimaryKey(to.getPrimaryKey());
  }

  public void updateFromPreviousStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode, String explanation) {

    dao.updateStatusOnMatchingStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
  }

}
