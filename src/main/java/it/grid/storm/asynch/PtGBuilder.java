/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.persistence.exceptions.InvalidFileTransferDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidPtGDataAttributesException;
import it.grid.storm.persistence.exceptions.InvalidSurlRequestDataAttributesException;
import it.grid.storm.persistence.model.AnonymousPtGData;
import it.grid.storm.persistence.model.IdentityPtGData;
import it.grid.storm.persistence.model.PtGData;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.IdentityInputData;
import it.grid.storm.synchcall.data.datatransfer.FileTransferInputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michele Dibenedetto
 * 
 */
public class PtGBuilder {

  private static Logger log = LoggerFactory.getLogger(PtGBuilder.class);

  private PtGBuilder() {}

  public static PtG build(FileTransferInputData inputData) throws BuilderException {

    TSURL toSURL = inputData.getSurl();
    TLifeTimeInSeconds pinLifetime = inputData.getDesiredPinLifetime();
    TURLPrefix transferProtocols = inputData.getTransferProtocols();
    TDirOption dirOption = TDirOption.makeNotDirectory();
    TSizeInBytes fileSize = TSizeInBytes.makeEmpty();
    TReturnStatus status =
        new TReturnStatus(TStatusCode.SRM_REQUEST_INPROGRESS, "Synchronous request created");

    TTURL transferURL = TTURL.makeEmpty();
    PtGData data;
    try {
      if (inputData instanceof IdentityInputData) {
        data = new IdentityPtGData(((IdentityInputData) inputData).getUser(), toSURL, pinLifetime,
            dirOption, transferProtocols, fileSize, status, transferURL);
      } else {
        data = new AnonymousPtGData(toSURL, pinLifetime, dirOption, transferProtocols, fileSize,
            status, transferURL);
      }
      data.store();
    } catch (InvalidPtGDataAttributesException e) {
      log.error("Unable to build PtGChunkData. " + "InvalidPtGChunkDataAttributesException: {}",
          e.getMessage(), e);
      throw new BuilderException("Error building PtG PtGChunkData. Building failed");
    } catch (InvalidFileTransferDataAttributesException e) {
      log.error(
          "Unable to build PtGChunkData. " + "InvalidFileTransferChunkDataAttributesException: {}",
          e.getMessage(), e);
      throw new BuilderException("Error building PtG PtGChunkData. Building failed");
    } catch (InvalidSurlRequestDataAttributesException e) {
      log.error("Unable to build PtGChunkData. " + "InvalidSurlRequestDataAttributesException: {}",
          e.getMessage(), e);
      throw new BuilderException("Error building PtG PtGChunkData. Building failed");
    }
    return new PtG(data);
  }
}
