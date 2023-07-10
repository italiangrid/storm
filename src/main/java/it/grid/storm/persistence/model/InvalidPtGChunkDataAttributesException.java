/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStorageSystemInfo;
import it.grid.storm.srm.types.TTURL;

/**
 * This class represents an exceptin thrown when the attributes supplied to the constructor of
 * PtGChunkData are invalid, that is if any of the following is _null_: requestToken, fromSURL,
 * storageSystemInfo, lifeTime, fileStorageType, spaceToken, numOfLevels, TURLPrefix
 * transferProtocols, fileSize, status, estimatedWaitTimeOnQueue, estimatedProcessingTime,
 * transferURL, remainingPinTime.
 *
 * @author EGRID - ICTP Trieste
 * @date March 23rd, 2005
 * @version 2.0
 */
public class InvalidPtGChunkDataAttributesException extends Exception {

  // booleans that indicate whether the corresponding variable is null
  private boolean nullRequestToken;
  private boolean nullFromSURL;
  private boolean nullStorageSystemInfo;
  private boolean nullLifeTime;
  private boolean nullFileStorageType;
  private boolean nullSpaceToken;
  private boolean nullDirOption;
  private boolean nullTransferProtocols;
  private boolean nullFileSize;
  private boolean nullStatus;
  private boolean nullEstimatedWaitTimeOnQueue;
  private boolean nullEstimatedProcessingTime;
  private boolean nullTransferURL;
  private boolean nullRemainingPinTime;

  /** Constructor that requires the attributes that caused the exception to be thrown. */
  public InvalidPtGChunkDataAttributesException(
      TRequestToken requestToken,
      TSURL fromSURL,
      TStorageSystemInfo storageSystemInfo,
      TLifeTimeInSeconds lifeTime,
      TFileStorageType fileStorageType,
      TSpaceToken spaceToken,
      TDirOption dirOption,
      TURLPrefix transferProtocols,
      TSizeInBytes fileSize,
      TReturnStatus status,
      TLifeTimeInSeconds estimatedWaitTimeOnQueue,
      TLifeTimeInSeconds estimatedProcessingTime,
      TTURL transferURL,
      TLifeTimeInSeconds remainingPinTime) {

    nullRequestToken = requestToken == null;
    nullFromSURL = fromSURL == null;
    nullStorageSystemInfo = storageSystemInfo == null;
    nullLifeTime = lifeTime == null;
    nullFileStorageType = fileStorageType == null;
    nullSpaceToken = spaceToken == null;
    nullDirOption = dirOption == null;
    nullTransferProtocols = transferProtocols == null;
    nullFileSize = fileSize == null;
    nullStatus = status == null;
    nullEstimatedWaitTimeOnQueue = estimatedWaitTimeOnQueue == null;
    nullEstimatedProcessingTime = estimatedProcessingTime == null;
    nullTransferURL = transferURL == null;
    nullRemainingPinTime = remainingPinTime == null;
  }

  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Invalid PtGChunkData attributes: null-requestToken=");
    sb.append(nullRequestToken);
    sb.append("; nul-fromSURL=");
    sb.append(nullFromSURL);
    sb.append("; null-storageSystemInfo=");
    sb.append(nullStorageSystemInfo);
    sb.append("; null-lifeTime=");
    sb.append(nullLifeTime);
    sb.append("; null-filestorageType=");
    sb.append(nullFileStorageType);
    sb.append("; null-spaceToken=");
    sb.append(nullSpaceToken);
    sb.append("; null-dirOption=");
    sb.append(nullDirOption);
    sb.append("; null-transferProtocols=");
    sb.append(nullTransferProtocols);
    sb.append("; null-fileSize=");
    sb.append(nullFileSize);
    sb.append("; null-status=");
    sb.append(nullStatus);
    sb.append("; null-estimatedWaitTimeOnQueue=");
    sb.append(nullEstimatedWaitTimeOnQueue);
    sb.append("; null-estimatedProcessingTime=");
    sb.append(nullEstimatedProcessingTime);
    sb.append("; null-transferURL=");
    sb.append(nullTransferURL);
    sb.append("; null-remainingPinTime=");
    sb.append(nullRemainingPinTime);
    sb.append(".");
    return sb.toString();
  }
}
