/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;

/**
 * This class represents an exceptin thrown when the attributes supplied to the constructor of
 * PtGChunkData are invalid, that is if any of the following is _null_: requestToken, fromSURL,
 * lifeTime, numOfLevels, transferProtocols, fileSize, status, transferURL.
 *
 * @author EGRID - ICTP Trieste
 * @date March 23rd, 2005
 * @version 3.0
 */
public class InvalidPtGDataAttributesException extends InvalidFileTransferDataAttributesException {

  private static final long serialVersionUID = -3484929474636108262L;
  // booleans that indicate whether the corresponding variable is null
  protected boolean nullLifeTime;
  protected boolean nullDirOption;
  protected boolean nullFileSize;

  /** Constructor that requires the attributes that caused the exception to be thrown. */
  public InvalidPtGDataAttributesException(
      TSURL fromSURL,
      TLifeTimeInSeconds lifeTime,
      TDirOption dirOption,
      TURLPrefix transferProtocols,
      TSizeInBytes fileSize,
      TReturnStatus status,
      TTURL transferURL) {

    super(fromSURL, transferProtocols, status, transferURL);
    nullLifeTime = lifeTime == null;
    nullDirOption = dirOption == null;
    nullFileSize = fileSize == null;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();
    builder.append("InvalidPtGChunkDataAttributesException [nullLifeTime=");
    builder.append(nullLifeTime);
    builder.append(", nullDirOption=");
    builder.append(nullDirOption);
    builder.append(", nullFileSize=");
    builder.append(nullFileSize);
    builder.append(", nullSURL=");
    builder.append(nullSURL);
    builder.append(", nullTransferProtocols=");
    builder.append(nullTransferProtocols);
    builder.append(", nullStatus=");
    builder.append(nullStatus);
    builder.append(", nullTransferURL=");
    builder.append(nullTransferURL);
    builder.append("]");
    return builder.toString();
  }
}
