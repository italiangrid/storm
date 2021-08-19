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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.common.types.TimeUnit;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.AbstractGridUser;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.persistence.converter.FileLifetimeConverter;
import it.grid.storm.persistence.converter.FileStorageTypeConverter;
import it.grid.storm.persistence.converter.OverwriteModeConverter;
import it.grid.storm.persistence.converter.PinLifetimeConverter;
import it.grid.storm.persistence.converter.SizeInBytesIntConverter;
import it.grid.storm.persistence.converter.SpaceTokenStringConverter;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.converter.TURLConverter;
import it.grid.storm.persistence.converter.TransferProtocolListConverter;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.exceptions.InvalidFileTransferDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidPtPDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidPtPPersistentChunkDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidReducedPtPChunkDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidSurlRequestDataAttributesException;
import it.grid.storm.persistence.impl.mysql.PtPChunkDAOMySql;
import it.grid.storm.persistence.model.PtPChunkDataTO;
import it.grid.storm.persistence.model.PtPPersistentChunkData;
import it.grid.storm.persistence.model.ReducedPtPChunkData;
import it.grid.storm.persistence.model.ReducedPtPChunkDataTO;
import it.grid.storm.srm.types.InvalidTRequestTokenAttributesException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.InvalidTSpaceTokenAttributesException;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

/**
 * Class that represents StoRMs PtPChunkCatalog: it collects PtPChunkData and provides methods for
 * looking up a PtPChunkData based on TRequestToken, as well as for updating data into persistence.
 * Methods are also supplied to evaluate if a SURL is in SRM_SPACE_AVAILABLE state, and to transit
 * expired SURLs in SRM_SPACE_AVAILABLE state to SRM_FILE_LIFETIME_EXPIRED.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 3.0
 */
public class PtPChunkCatalog {

  private static final Logger log = LoggerFactory.getLogger(PtPChunkCatalog.class);

  private static PtPChunkCatalog instance;

  public static synchronized PtPChunkCatalog getInstance() {
    if (instance == null) {
      instance = new PtPChunkCatalog();
    }
    return instance;
  }

  private final PtPChunkDAO dao;

  private PtPChunkCatalog() {
    dao = PtPChunkDAOMySql.getInstance();
  }

  /**
   * Method used to update into Persistence a retrieved PtPChunkData.
   */
  public synchronized void update(PtPPersistentChunkData chunkData) {

    PtPChunkDataTO to = new PtPChunkDataTO();
    /* Primary key needed by DAO Object */
    to.setPrimaryKey(chunkData.getPrimaryKey());
    to.setStatus(StatusCodeConverter.getInstance().toDB(chunkData.getStatus().getStatusCode()));
    to.setErrString(chunkData.getStatus().getExplanation());
    to.setTransferURL(TURLConverter.getInstance().toDB(chunkData.getTransferURL().toString()));
    to.setPinLifetime(PinLifetimeConverter.getInstance().toDB(chunkData.pinLifetime().value()));
    to.setFileLifetime(FileLifetimeConverter.getInstance().toDB(chunkData.fileLifetime().value()));
    to.setFileStorageType(FileStorageTypeConverter.getInstance().toDB(chunkData.fileStorageType()));
    to.setOverwriteOption(OverwriteModeConverter.getInstance().toDB(chunkData.overwriteOption()));
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
   * Method that returns a Collection of PtPChunkData Objects matching the supplied TRequestToken.
   * If any of the data associated to the TRequestToken is not well formed and so does not allow a
   * PtPChunkData Object to be created, then that part of the request is dropped, gets logged and an
   * attempt is made to write in the DB that the chunk was malformed; the processing continues with
   * the next part. Only the valid chunks get returned. If there are no chunks to process then an
   * empty Collection is returned, and a message gets logged. NOTE! Chunks in SRM_ABORTED status are
   * NOT returned! This is important because this method is intended to be used by the Feeders to
   * fetch all chunks in the request, and aborted chunks should not be picked up for processing!
   */
  public synchronized Collection<PtPPersistentChunkData> lookup(final TRequestToken rt) {

    Collection<PtPChunkDataTO> chunkTOs = dao.find(rt);
    log.debug("PtPChunkCatalog: retrieved data {}", chunkTOs);
    return buildChunkDataList(chunkTOs);
  }

  /**
   * Private method used to create a PtPChunkData object, from a PtPChunkDataTO and TRequestToken.
   * If a chunk cannot be created, an error messagge gets logged and an attempt is made to signal in
   * the DB that the chunk is malformed.
   */
  private PtPPersistentChunkData makeOne(PtPChunkDataTO auxTO, TRequestToken rt) {

    StringBuilder errorSb = new StringBuilder();
    // toSURL
    TSURL toSURL = null;
    try {
      toSURL = TSURL.makeFromStringValidate(auxTO.toSURL());
    } catch (InvalidTSURLAttributesException e) {
      errorSb.append(e);
    }
    if (auxTO.normalizedStFN() != null) {
      toSURL.setNormalizedStFN(auxTO.normalizedStFN());
    }
    if (auxTO.surlUniqueID() != null) {
      toSURL.setUniqueID(auxTO.surlUniqueID().intValue());
    }
    // pinLifetime
    TLifeTimeInSeconds pinLifetime = null;
    try {
      long pinLifeTime = PinLifetimeConverter.getInstance().toStoRM(auxTO.pinLifetime());
      // Check for max value allowed
      long max = Configuration.getInstance().getPinLifetimeMaximum();
      if (pinLifeTime > max) {
        log.warn("PinLifeTime is greater than the max value allowed. Drop the "
            + "value to the max = {} seconds", max);
        pinLifeTime = max;
      }
      pinLifetime = TLifeTimeInSeconds.make(pinLifeTime, TimeUnit.SECONDS);
    } catch (IllegalArgumentException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // fileLifetime
    TLifeTimeInSeconds fileLifetime = null;
    try {
      fileLifetime = TLifeTimeInSeconds
        .make(FileLifetimeConverter.getInstance().toStoRM(auxTO.fileLifetime()), TimeUnit.SECONDS);
    } catch (IllegalArgumentException e) {
      errorSb.append("\n");
      errorSb.append(e);
    }
    // fileStorageType
    TFileStorageType fileStorageType =
        FileStorageTypeConverter.getInstance().toSTORM(auxTO.fileStorageType());
    if (fileStorageType == TFileStorageType.EMPTY) {
      errorSb.append("\nTFileStorageType could not be translated from "
          + "its String representation! String: " + auxTO.fileStorageType());
      // Use the default value defined in Configuration.
      fileStorageType = TFileStorageType
        .getTFileStorageType(Configuration.getInstance().getDefaultFileStorageType());
      errorSb.append("\nUsed the default TFileStorageType as defined " + "in StoRM config.: "
          + fileStorageType);
    }
    // expectedFileSize
    //
    // WARNING! A converter is used because the DB uses 0 for empty, whereas
    // StoRM object model does allow a 0 size! Since this is an optional
    // field
    // in the SRM specs, null must be converted explicitly to Empty
    // TSizeInBytes
    // because it is indeed well formed!
    TSizeInBytes expectedFileSize = null;
    TSizeInBytes emptySize = TSizeInBytes.makeEmpty();
    long sizeTranslation = SizeInBytesIntConverter.getInstance().toStoRM(auxTO.expectedFileSize());
    if (emptySize.value() == sizeTranslation) {
      expectedFileSize = emptySize;
    } else {
      try {
        expectedFileSize = TSizeInBytes.make(auxTO.expectedFileSize(), SizeUnit.BYTES);
      } catch (InvalidTSizeAttributesException e) {
        errorSb.append("\n");
        errorSb.append(e);
      }
    }
    // spaceToken!
    //
    // WARNING! A converter is still needed because of DB logic for missing
    // SpaceToken makes use of NULL, whereas StoRM object model does not
    // allow
    // for null! It makes use of a specific Empty type.
    //
    // Indeed, the SpaceToken field is optional, so a request with a null
    // value
    // for the SpaceToken field in the DB, _is_ well formed!
    TSpaceToken spaceToken = null;
    TSpaceToken emptyToken = TSpaceToken.makeEmpty();
    /**
     * convert empty string representation of DPM into StoRM representation;
     */
    String spaceTokenTranslation =
        SpaceTokenStringConverter.getInstance().toStoRM(auxTO.spaceToken());
    if (emptyToken.toString().equals(spaceTokenTranslation)) {
      spaceToken = emptyToken;
    } else {
      try {
        spaceToken = TSpaceToken.make(spaceTokenTranslation);
      } catch (InvalidTSpaceTokenAttributesException e) {
        errorSb.append("\n");
        errorSb.append(e);
      }
    }
    // overwriteOption!
    TOverwriteMode overwriteOption =
        OverwriteModeConverter.getInstance().toSTORM(auxTO.overwriteOption());
    if (overwriteOption == TOverwriteMode.EMPTY) {
      errorSb.append("\nTOverwriteMode could not be translated "
          + "from its String representation! String: " + auxTO.overwriteOption());
      overwriteOption = null;
    }
    // transferProtocols
    TURLPrefix transferProtocols = TransferProtocolListConverter.toSTORM(auxTO.protocolList());
    if (transferProtocols.size() == 0) {
      errorSb
        .append("\nEmpty list of TransferProtocols " + "or could not translate TransferProtocols!");
      transferProtocols = null; // fail construction of PtPChunkData!
    }
    // status
    TReturnStatus status = null;
    TStatusCode code = StatusCodeConverter.getInstance().toSTORM(auxTO.status());
    if (code == TStatusCode.EMPTY) {
      errorSb.append("\nRetrieved StatusCode was not recognised: " + auxTO.status());
    } else {
      status = new TReturnStatus(code, auxTO.errString());
    }
    GridUserInterface gridUser = null;
    try {
      if (auxTO.vomsAttributes() != null && !auxTO.vomsAttributes().trim().equals("")) {
        gridUser = GridUserManager.makeVOMSGridUser(auxTO.clientDN(), auxTO.vomsAttributesArray());
      } else {
        gridUser = GridUserManager.makeGridUser(auxTO.clientDN());
      }

    } catch (IllegalArgumentException e) {
      log.error("Unexpected error on voms grid user creation. " + "IllegalArgumentException: {}",
          e.getMessage(), e);
    }

    // transferURL
    /**
     * whatever is read is just meaningless because PtP will fill it in!!! So create an Empty TTURL
     * by default! Vital to avoid problems with unknown DPM NULL/EMPTY logic policy!
     */
    TTURL transferURL = TTURL.makeEmpty();
    // make PtPChunkData
    PtPPersistentChunkData aux = null;
    try {
      aux = new PtPPersistentChunkData(gridUser, rt, toSURL, pinLifetime, fileLifetime,
          fileStorageType, spaceToken, expectedFileSize, transferProtocols, overwriteOption, status,
          transferURL);
      aux.setPrimaryKey(auxTO.primaryKey());
    } catch (InvalidPtPPersistentChunkDataAttributesException e) {
      dao.fail(auxTO);
      log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
          + " from persistence. Dropping chunk from request: {}", rt);
      log.warn(e.getMessage(), e);
      log.warn(errorSb.toString());
    } catch (InvalidPtPDataAttributesException e) {
      dao.fail(auxTO);
      log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
          + " from persistence. Dropping chunk from request: {}", rt);
      log.warn(e.getMessage(), e);
      log.warn(errorSb.toString());
    } catch (InvalidFileTransferDataAttributesException e) {
      dao.fail(auxTO);
      log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
          + " from persistence. Dropping chunk from request: {}", rt);
      log.warn(e.getMessage(), e);
      log.warn(errorSb.toString());
    } catch (InvalidSurlRequestDataAttributesException e) {
      dao.fail(auxTO);
      log.warn("PtP CHUNK CATALOG! Retrieved malformed PtP chunk data"
          + " from persistence. Dropping chunk from request: {}", rt);
      log.warn(e.getMessage(), e);
      log.warn(errorSb.toString());
    }
    // end...
    return aux;
  }

  /**
   * 
   * Adds to the received PtPChunkDataTO the normalized StFN and the SURL unique ID taken from the
   * PtPChunkData
   * 
   * @param chunkTO
   * @param chunk
   */
  private void completeTO(ReducedPtPChunkDataTO chunkTO, final ReducedPtPChunkData chunk) {

    chunkTO.setNormalizedStFN(chunk.toSURL().normalizedStFN());
    chunkTO.setSurlUniqueID(Integer.valueOf(chunk.toSURL().uniqueId()));
  }

  /**
   * 
   * Creates a ReducedPtGChunkDataTO from the received PtGChunkDataTO and completes it with the
   * normalized StFN and the SURL unique ID taken from the PtGChunkData
   * 
   * @param chunkTO
   * @param chunk
   * @return
   * @throws InvalidReducedPtPChunkDataAttributesException
   */
  private ReducedPtPChunkDataTO completeTO(PtPChunkDataTO chunkTO,
      final PtPPersistentChunkData chunk) throws InvalidReducedPtPChunkDataAttributesException {

    ReducedPtPChunkDataTO reducedChunkTO = this.reduce(chunkTO);
    this.completeTO(reducedChunkTO, this.reduce(chunk));
    return reducedChunkTO;
  }

  /**
   * Creates a ReducedPtPChunkData from the data contained in the received PtPChunkData
   * 
   * @param chunk
   * @return
   * @throws InvalidReducedPtPChunkDataAttributesException
   */
  private ReducedPtPChunkData reduce(PtPPersistentChunkData chunk)
      throws InvalidReducedPtPChunkDataAttributesException {

    ReducedPtPChunkData reducedChunk = new ReducedPtPChunkData(chunk.getSURL(), chunk.getStatus(),
        chunk.fileStorageType(), chunk.fileLifetime());
    reducedChunk.setPrimaryKey(chunk.getPrimaryKey());
    return reducedChunk;
  }

  /**
   * Creates a ReducedPtPChunkDataTO from the data contained in the received PtPChunkDataTO
   * 
   * @param chunkTO
   * @return
   */
  private ReducedPtPChunkDataTO reduce(PtPChunkDataTO chunkTO) {

    ReducedPtPChunkDataTO reducedChunkTO = new ReducedPtPChunkDataTO();
    reducedChunkTO.setPrimaryKey(chunkTO.primaryKey());
    reducedChunkTO.setToSURL(chunkTO.toSURL());
    reducedChunkTO.setNormalizedStFN(chunkTO.normalizedStFN());
    reducedChunkTO.setSurlUniqueID(chunkTO.surlUniqueID());
    reducedChunkTO.setStatus(chunkTO.status());
    reducedChunkTO.setErrString(chunkTO.errString());
    return reducedChunkTO;
  }

  /**
   * Checks if the received PtPChunkDataTO contains the fields not set by the front end but required
   * 
   * @param chunkTO
   * @return
   */
  private boolean isComplete(PtPChunkDataTO chunkTO) {

    return (chunkTO.normalizedStFN() != null) && (chunkTO.surlUniqueID() != null);
  }

  public Collection<PtPPersistentChunkData> lookupPtPChunkData(TSURL surl, GridUserInterface user) {

    return lookupPtPChunkData((List<TSURL>) Arrays.asList(new TSURL[] {surl}), user);
  }

  private Collection<PtPPersistentChunkData> lookupPtPChunkData(List<TSURL> surls,
      GridUserInterface user) {

    int[] surlsUniqueIDs = new int[surls.size()];
    String[] surlsArray = new String[surls.size()];
    int index = 0;
    for (TSURL tsurl : surls) {
      surlsUniqueIDs[index] = tsurl.uniqueId();
      surlsArray[index] = tsurl.rawSurl();
      index++;
    }
    Collection<PtPChunkDataTO> chunkDataTOs = dao.find(surlsUniqueIDs, surlsArray, user.getDn());
    log.debug("PtP CHUNK CATALOG: retrieved data {}", chunkDataTOs);
    return buildChunkDataList(chunkDataTOs);
  }

  private Collection<PtPPersistentChunkData> buildChunkDataList(
      Collection<PtPChunkDataTO> chunkDataTOs) {

    Collection<PtPPersistentChunkData> list = Lists.newArrayList();
    PtPPersistentChunkData chunk;
    for (PtPChunkDataTO chunkTO : chunkDataTOs) {
      chunk = makeOne(chunkTO);
      if (chunk == null) {
        continue;
      }
      list.add(chunk);
      if (isComplete(chunkTO)) {
        continue;
      }
      try {
        dao.updateIncomplete(completeTO(chunkTO, chunk));
      } catch (InvalidReducedPtPChunkDataAttributesException e) {
        log.warn(
            "PtG CHUNK CATALOG! unable to add missing informations on " + "DB to the request: {}",
            e.getMessage());
      }
    }
    log.debug("PtPChunkCatalog: returning {}\n\n", list);
    return list;
  }

  private PtPPersistentChunkData makeOne(PtPChunkDataTO chunkTO) {

    try {
      return makeOne(chunkTO, new TRequestToken(chunkTO.requestToken(), chunkTO.timeStamp()));
    } catch (InvalidTRequestTokenAttributesException e) {
      throw new IllegalStateException(
          "Unexpected InvalidTRequestTokenAttributesException in TRequestToken: " + e);
    }
  }

  public int updateStatus(TRequestToken requestToken, TSURL surl, TStatusCode statusCode,
      String explanation) {

    return dao.updateStatus(requestToken, new int[] {surl.uniqueId()},
        new String[] {surl.rawSurl()}, statusCode, explanation);
  }

  public int updateFromPreviousStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode, String explanation) {

    return dao.updateStatusOnMatchingStatus(requestToken, expectedStatusCode, newStatusCode,
        explanation);
  }

  public int updateFromPreviousStatus(TRequestToken requestToken, List<TSURL> surlList,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode) {

    int[] surlsUniqueIDs = new int[surlList.size()];
    String[] surls = new String[surlList.size()];
    int index = 0;
    for (TSURL tsurl : surlList) {
      surlsUniqueIDs[index] = tsurl.uniqueId();
      surls[index] = tsurl.rawSurl();
      index++;
    }
    return dao.updateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls, expectedStatusCode,
        newStatusCode);
  }

}
