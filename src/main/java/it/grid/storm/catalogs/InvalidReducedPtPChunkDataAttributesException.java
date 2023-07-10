/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

/**
 * This class represents an exceptin thrown when the attributes supplied to the constructor of
 * ReducedPtPChunkData are invalid, that is if any is _null_.
 *
 * @author EGRID - ICTP Trieste
 * @date January, 2007
 * @version 1.0
 */
public class InvalidReducedPtPChunkDataAttributesException extends Exception {

  private static final long serialVersionUID = 4945626188325362854L;

  // booleans that indicate whether the corresponding variable is null
  private boolean nullToSURL;
  private boolean nullStatus;
  private boolean nullFileStorageType;
  private boolean nullFileLifetime;

  /** Constructor that requires the attributes that caused the exception to be thrown. */
  public InvalidReducedPtPChunkDataAttributesException(
      TSURL toSURL,
      TReturnStatus status,
      TFileStorageType fileStorageType,
      TLifeTimeInSeconds fileLifetime) {

    nullFileStorageType = fileStorageType == null;
    nullToSURL = toSURL == null;
    nullStatus = status == null;
    nullFileLifetime = fileLifetime == null;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Invalid PtPChunkData attributes: null-toSURL=");
    sb.append(nullToSURL);
    sb.append("; null-status=");
    sb.append(nullStatus);
    sb.append("; null-fileStorageType=");
    sb.append(nullFileStorageType);
    sb.append("; null-fileLifetime=");
    sb.append(nullFileLifetime);
    sb.append(".");
    return sb.toString();
  }
}
