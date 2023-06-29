/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.catalogs.AnonymousPtPData;
import it.grid.storm.catalogs.IdentityPtPData;
import it.grid.storm.catalogs.InvalidFileTransferDataAttributesException;
import it.grid.storm.catalogs.InvalidPtPDataAttributesException;
import it.grid.storm.catalogs.InvalidSurlRequestDataAttributesException;
import it.grid.storm.catalogs.PtPData;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutInputData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PtPBuilder {

  private static Logger log = LoggerFactory.getLogger(PtPBuilder.class);

  private PtPBuilder() {}

  public static PtP build(PrepareToPutInputData inputData) throws BuilderException {

    TSURL toSURL = inputData.getSurl();
    TLifeTimeInSeconds pinLifetime = inputData.getDesiredPinLifetime();
    TLifeTimeInSeconds fileLifetime = inputData.getDesiredFileLifetime();
    TFileStorageType fileStorageType = TFileStorageType
      .getTFileStorageType(Configuration.getInstance().getDefaultFileStorageType());
    TSpaceToken spaceToken = inputData.getTargetSpaceToken();
    TSizeInBytes expectedFileSize = inputData.getFileSize();
    TURLPrefix transferProtocols = inputData.getTransferProtocols();
    TOverwriteMode overwriteOption = TOverwriteMode.ALWAYS;
    TReturnStatus status =
        new TReturnStatus(TStatusCode.SRM_REQUEST_QUEUED, "Synchronous request created");
    TTURL transferURL = TTURL.makeEmpty();
    PtPData data;
    try {
      if (inputData instanceof IdentityInputData) {
        data = new IdentityPtPData(((IdentityInputData) inputData).getUser(), toSURL, pinLifetime,
            fileLifetime, fileStorageType, spaceToken, expectedFileSize, transferProtocols,
            overwriteOption, status, transferURL);
      } else {
        data = new AnonymousPtPData(toSURL, pinLifetime, fileLifetime, fileStorageType, spaceToken,
            expectedFileSize, transferProtocols, overwriteOption, status, transferURL);
      }
      data.store();
    } catch (InvalidPtPDataAttributesException e) {
      log.error("Unable to build PtPChunkData. " + "InvalidPtPChunkDataAttributesException: {}",
          e.getMessage());
      throw new BuilderException("Error building PtP PtPChunkData. Building failed");
    } catch (InvalidFileTransferDataAttributesException e) {
      log.error(
          "Unable to build PtPChunkData. " + "InvalidFileTransferChunkDataAttributesException: {}",
          e.getMessage());
      throw new BuilderException("Error building PtP PtPChunkData. Building failed");
    } catch (InvalidSurlRequestDataAttributesException e) {
      log.error("Unable to build PtPChunkData. " + "InvalidSurlRequestDataAttributesException: ",
          e.getMessage());
      throw new BuilderException("Error building PtP PtPChunkData. Building failed");
    }
    try {
      return new PtP(data);
    } catch (InvalidRequestAttributesException e) {
      log.error("Unable to build PtP. " + "InvalidRequestAttributesException: {}", e.getMessage());
      throw new BuilderException("Error building PtP. Building failed");
    }
  }
}
