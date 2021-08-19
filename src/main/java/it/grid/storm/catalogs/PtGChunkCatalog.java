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

package it.grid.storm.catalogs;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.persistence.converter.PinLifetimeConverter;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.converter.TURLConverter;
import it.grid.storm.persistence.converter.TransferProtocolListConverter;
import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.exceptions.InvalidReducedPtGChunkDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidSurlRequestDataAttributesException;
import it.grid.storm.persistence.impl.mysql.PtGChunkDAOMySql;
import it.grid.storm.persistence.model.PtGChunkDataTO;
import it.grid.storm.persistence.model.PtGPersistentChunkData;
import it.grid.storm.persistence.model.ReducedPtGChunkData;
import it.grid.storm.persistence.model.ReducedPtGChunkDataTO;
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

/**
 * Class that represents StoRMs PtGChunkCatalog: it collects PtGChunkData and provides methods for
 * looking up a PtGChunkData based on TRequestToken, as well as for adding a new entry and removing
 * an existing one.
 * 
 * @author EGRID - ICTP Trieste
 * @date April 26th, 2005
 * @version 4.0
 */
public class PtGChunkCatalog {

  private static final Logger log = LoggerFactory.getLogger(PtGChunkCatalog.class);

  private static PtGChunkCatalog instance;

  public static synchronized PtGChunkCatalog getInstance() {
    if (instance == null) {
      instance = new PtGChunkCatalog();
    }
    return instance;
  }
 
  private final PtGChunkDAO dao;

  /**
   * Private constructor that starts the internal timer needed to periodically check and transit
   * requests whose pinLifetime has expired and are in SRM_FILE_PINNED, to SRM_RELEASED.
   */
  private PtGChunkCatalog() {

    dao = PtGChunkDAOMySql.getInstance();
  }

  /**
   * Method used to update into Persistence a retrieved PtGChunkData. In case any error occurs, the
   * operation does not proceed but no Exception is thrown. Error messages get logged.
   * 
   * Only fileSize, StatusCode, errString and transferURL are updated. Likewise for the request
   * pinLifetime.
   */
  synchronized public void update(PtGPersistentChunkData chunkData) {

    PtGChunkDataTO to = new PtGChunkDataTO();
    /* Primary key needed by DAO Object */
    to.setPrimaryKey(chunkData.getPrimaryKey());
    to.setFileSize(chunkData.getFileSize().value());
    to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
    to.setErrString(chunkData.getStatus().getExplanation());
    to.setTurl(TURLConverter.getInstance().toDB(chunkData.getTransferURL().toString()));
    to.setLifeTime(PinLifetimeConverter.getInstance().toDB(chunkData.getPinLifeTime().value()));
    to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
    to.setSurlUniqueID(Integer.valueOf(chunkData.getSURL().uniqueId()));
    to.setClientDN(chunkData.getUser().getDn());
    if (chunkData.getUser() instanceof AbstractGridUser) {
      if (((AbstractGridUser) chunkData.getUser()).hasVoms()) {
        to.setVomsAttributes(((AbstractGridUser) chunkData.getUser()).getFQANsAsString());
      }

    }
    dao.update(to);
  }

  /**
   * Method that returns a Collection of PtGChunkData Objects matching the supplied TRequestToken.
   * 
   * If any of the data associated to the TRequestToken is not well formed and so does not allow a
   * PtGChunkData Object to be created, then that part of the request is dropped and gets logged,
   * and the processing continues with the next part. All valid chunks get returned: the others get
   * dropped.
   * 
   * If there are no chunks to process then an empty Collection is returned, and a messagge gets
   * logged.
   */
  synchronized public Collection<PtGPersistentChunkData> lookup(TRequestToken rt) {

    Collection<PtGChunkDataTO> chunkTOs = dao.find(rt);
    log.debug("PtG CHUNK CATALOG: retrieved data " + chunkTOs);
    ArrayList<PtGPersistentChunkData> list = new ArrayList<PtGPersistentChunkData>();
    if (chunkTOs.isEmpty()) {
      log.warn("PtG CHUNK CATALOG! No chunks found in persistence for " + "specified request: {}",
          rt);
      return list;
    }
    PtGPersistentChunkData chunk;
    for (PtGChunkDataTO chunkTO : chunkTOs) {
      chunk = makeOne(chunkTO, rt);
      if (chunk == null) {
        continue;
      }
      list.add(chunk);
      if (isComplete(chunkTO)) {
        continue;
      }
      try {
        dao.updateIncomplete(this.completeTO(chunkTO, chunk));
      } catch (InvalidReducedPtGChunkDataAttributesException e) {
        log.warn(
            "PtG CHUNK CATALOG! unable to add missing informations on DB " + "to the request: {}",
            e.getMessage());
      }
    }
    log.debug("PtG CHUNK CATALOG: returning " + list);
    return list;
  }

  /**
   * Generates a PtGChunkData from the received PtGChunkDataTO
   * 
   * @param chunkDataTO
   * @param rt
   * @return
   */
  private PtGPersistentChunkData makeOne(PtGChunkDataTO chunkDataTO, TRequestToken rt) {

    StringBuilder errorSb = new StringBuilder();
    TSURL fromSURL = null;
    try {
      fromSURL = TSURL.makeFromStringValidate(chunkDataTO.fromSURL());
    } catch (InvalidTSURLAttributesException e) {
      errorSb.append(e);
    }
    if (chunkDataTO.normalizedStFN() != null) {
      fromSURL.setNormalizedStFN(chunkDataTO.normalizedStFN());
    }
    if (chunkDataTO.surlUniqueID() != null) {
      fromSURL.setUniqueID(chunkDataTO.surlUniqueID().intValue());
    }
    // lifeTime
    TLifeTimeInSeconds lifeTime = null;
    try {
      long pinLifeTime = PinLifetimeConverter.getInstance().toStoRM(chunkDataTO.lifeTime());
      // Check for max value allowed
      long max = Configuration.getInstance().getPinLifetimeMaximum();
      if (pinLifeTime > max) {
        log.warn("PinLifeTime is greater than the max value allowed."
            + " Drop the value to the max = {} seconds", max);
        pinLifeTime = max;
      }
      lifeTime = TLifeTimeInSeconds.make((pinLifeTime), TimeUnit.SECONDS);
    } catch (IllegalArgumentException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // dirOption
    TDirOption dirOption = null;
    try {
      dirOption = new TDirOption(chunkDataTO.dirOption(), chunkDataTO.allLevelRecursive(),
          chunkDataTO.numLevel());
    } catch (InvalidTDirOptionAttributesException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // transferProtocols
    TURLPrefix transferProtocols =
        TransferProtocolListConverter.toSTORM(chunkDataTO.protocolList());
    if (transferProtocols.size() == 0) {
      errorSb
        .append("\nEmpty list of TransferProtocols or could " + "not translate TransferProtocols!");
      /* fail construction of PtGChunkData! */
      transferProtocols = null;
    }
    // fileSize
    TSizeInBytes fileSize = null;
    try {
      fileSize = TSizeInBytes.make(chunkDataTO.fileSize(), SizeUnit.BYTES);
    } catch (InvalidTSizeAttributesException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // status
    TReturnStatus status = null;
    TStatusCode code = StatusCodeConverter.getInstance().toSTORM(chunkDataTO.status());
    if (code == TStatusCode.EMPTY) {
      errorSb.append("\nRetrieved StatusCode was not recognised: " + chunkDataTO.status());
    } else {
      status = new TReturnStatus(code, chunkDataTO.errString());
    }
    GridUserInterface gridUser = null;
    try {
      if (chunkDataTO.vomsAttributes() != null && !chunkDataTO.vomsAttributes().trim().equals("")) {
        gridUser = GridUserManager.makeVOMSGridUser(chunkDataTO.clientDN(),
            chunkDataTO.vomsAttributesArray());
      } else {
        gridUser = GridUserManager.makeGridUser(chunkDataTO.clientDN());
      }

    } catch (IllegalArgumentException e) {
      log.error("Unexpected error on voms grid user creation." + " IllegalArgumentException: {}",
          e.getMessage(), e);
    }
    // transferURL
    /*
     * whatever is read is just meaningless because PtG will fill it in!!! So create an Empty TTURL
     * by default! Vital to avoid problems with unknown DPM NULL/EMPTY logic policy!
     */
    TTURL transferURL = TTURL.makeEmpty();
    // make PtGChunkData
    PtGPersistentChunkData aux = null;
    try {
      aux = new PtGPersistentChunkData(gridUser, rt, fromSURL, lifeTime, dirOption,
          transferProtocols, fileSize, status, transferURL);
      aux.setPrimaryKey(chunkDataTO.primaryKey());
    } catch (InvalidSurlRequestDataAttributesException e) {
      dao.fail(chunkDataTO);
      log.warn("PtG CHUNK CATALOG! Retrieved malformed PtG chunk data from "
          + "persistence. Dropping chunk from request {}", rt);
      log.warn(e.getMessage(), e);
      log.warn(errorSb.toString());
    }
    // end...
    return aux;
  }

  /**
   * 
   * Adds to the received PtGChunkDataTO the normalized StFN and the SURL unique ID taken from the
   * PtGChunkData
   * 
   * @param chunkTO
   * @param chunk
   */
  private void completeTO(ReducedPtGChunkDataTO chunkTO, final ReducedPtGChunkData chunk) {

    chunkTO.setNormalizedStFN(chunk.fromSURL().normalizedStFN());
    chunkTO.setSurlUniqueID(Integer.valueOf(chunk.fromSURL().uniqueId()));
  }

  /**
   * 
   * Creates a ReducedPtGChunkDataTO from the received PtGChunkDataTO and completes it with the
   * normalized StFN and the SURL unique ID taken from the PtGChunkData
   * 
   * @param chunkTO
   * @param chunk
   * @return
   * @throws InvalidReducedPtGChunkDataAttributesException
   */
  private ReducedPtGChunkDataTO completeTO(PtGChunkDataTO chunkTO,
      final PtGPersistentChunkData chunk) throws InvalidReducedPtGChunkDataAttributesException {

    ReducedPtGChunkDataTO reducedChunkTO = this.reduce(chunkTO);
    this.completeTO(reducedChunkTO, this.reduce(chunk));
    return reducedChunkTO;
  }

  /**
   * Creates a ReducedPtGChunkData from the data contained in the received PtGChunkData
   * 
   * @param chunk
   * @return
   * @throws InvalidReducedPtGChunkDataAttributesException
   */
  private ReducedPtGChunkData reduce(PtGPersistentChunkData chunk)
      throws InvalidReducedPtGChunkDataAttributesException {

    ReducedPtGChunkData reducedChunk = new ReducedPtGChunkData(chunk.getSURL(), chunk.getStatus());
    reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
    return reducedChunk;
  }

  /**
   * Creates a ReducedPtGChunkDataTO from the data contained in the received PtGChunkDataTO
   * 
   * @param chunkTO
   * @return
   */
  private ReducedPtGChunkDataTO reduce(PtGChunkDataTO chunkTO) {

    ReducedPtGChunkDataTO reducedChunkTO = new ReducedPtGChunkDataTO();
    reducedChunkTO.setPrimaryKey(chunkTO.primaryKey());
    reducedChunkTO.setFromSURL(chunkTO.fromSURL());
    reducedChunkTO.setNormalizedStFN(chunkTO.normalizedStFN());
    reducedChunkTO.setSurlUniqueID(chunkTO.surlUniqueID());
    reducedChunkTO.setStatus(chunkTO.status());
    reducedChunkTO.setErrString(chunkTO.errString());
    return reducedChunkTO;
  }

  /**
   * Checks if the received PtGChunkDataTO contains the fields not set by the front end but required
   * 
   * @param chunkTO
   * @return
   */
  private boolean isComplete(PtGChunkDataTO chunkTO) {

    return (chunkTO.normalizedStFN() != null) && (chunkTO.surlUniqueID() != null);
  }

  /**
   * Method used to add into Persistence a new entry. The supplied PtGChunkData gets the primary key
   * changed to the value assigned in Persistence.
   * 
   * This method is intended to be used by a recursive PtG request: the parent request supplies a
   * directory which must be expanded, so all new children requests resulting from the files in the
   * directory are added into persistence.
   * 
   * So this method does _not_ add a new SRM prepare_to_get request into the DB!
   * 
   * The only children data written into the DB are: sourceSURL, TDirOption, statusCode and
   * explanation.
   * 
   * In case of any error the operation does not proceed, but no Exception is thrown! Proper
   * messages get logged by underlaying DAO.
   */
  synchronized public void addChild(PtGPersistentChunkData chunkData) {

    PtGChunkDataTO to = new PtGChunkDataTO();
    /* needed for now to find ID of request! Must be changed soon! */
    to.setRequestToken(chunkData.getRequestToken().toString());
    to.setFromSURL(chunkData.getSURL().toString());
    to.setNormalizedStFN(chunkData.getSURL().normalizedStFN());
    to.setSurlUniqueID(Integer.valueOf(chunkData.getSURL().uniqueId()));

    to.setAllLevelRecursive(chunkData.getDirOption().isAllLevelRecursive());
    to.setDirOption(chunkData.getDirOption().isDirectory());
    to.setNumLevel(chunkData.getDirOption().getNumLevel());
    to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
    to.setErrString(chunkData.getStatus().getExplanation());
    to.setClientDN(chunkData.getUser().getDn());
    if (chunkData.getUser() instanceof AbstractGridUser) {
      if (((AbstractGridUser) chunkData.getUser()).hasVoms()) {
        to.setVomsAttributes(((AbstractGridUser) chunkData.getUser()).getFQANsAsString());
      }

    }
    /* add the entry and update the Primary Key field! */
    dao.addChild(to);
    /* set the assigned PrimaryKey! */
    chunkData.setPrimaryKey(to.primaryKey());
  }

  public void updateStatus(TRequestToken requestToken, TSURL surl, TStatusCode statusCode,
      String explanation) {

    dao.updateStatus(requestToken, new int[] {surl.uniqueId()}, new String[] {surl.rawSurl()},
        statusCode, explanation);
  }

  public void updateFromPreviousStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode, String explanation) {

    dao.updateStatusOnMatchingStatus(requestToken, expectedStatusCode, newStatusCode, explanation);
  }

}
