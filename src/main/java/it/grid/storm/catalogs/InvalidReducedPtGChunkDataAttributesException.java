/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

/**
 * This class represents an exceptin thrown when the attributes supplied to the constructor of
 * ReducedPtGChunkData are invalid, that is if any is _null_.
 *
 * @author EGRID - ICTP Trieste
 * @date November, 2006
 * @version 1.0
 */
public class InvalidReducedPtGChunkDataAttributesException extends Exception {

  private static final long serialVersionUID = -7943458526292568164L;

  // booleans that indicate whether the corresponding variable is null
  private boolean nullFromSURL;
  private boolean nullStatus;

  /** Constructor that requires the attributes that caused the exception to be thrown. */
  public InvalidReducedPtGChunkDataAttributesException(TSURL fromSURL, TReturnStatus status) {

    nullFromSURL = fromSURL == null;
    nullStatus = status == null;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Invalid PtGChunkData attributes: null-fromSURL=");
    sb.append(nullFromSURL);
    sb.append("; null-status=");
    sb.append(nullStatus);
    sb.append(".");
    return sb.toString();
  }
}
