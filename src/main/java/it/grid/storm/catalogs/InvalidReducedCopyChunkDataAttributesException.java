/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

/**
 * This class represents an exception thrown when the attributes supplied to the constructor of
 * ReducedCopyChunkData are invalid, that is if any of the following is _null_: fromsURL, toSURL,
 * status.
 *
 * @author Michele Dibenedetto
 */
@SuppressWarnings("serial")
public class InvalidReducedCopyChunkDataAttributesException extends Exception {

  // booleans that indicate whether the corresponding variable is null
  private boolean nullFromSURL;
  private boolean nullToSURL;
  private boolean nullStatus;

  /** Constructor that requires the attributes that caused the exception to be thrown. */
  public InvalidReducedCopyChunkDataAttributesException(
      TSURL fromSURL, TSURL toSURL, TReturnStatus status) {

    nullFromSURL = fromSURL == null;
    nullToSURL = toSURL == null;
    nullStatus = status == null;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Invalid CopyChunkData attributes: null-requestToken=");
    sb.append("; null-fromSURL=");
    sb.append(nullFromSURL);
    sb.append("; null-toSURL=");
    sb.append(nullToSURL);
    sb.append("; null-status=");
    sb.append(nullStatus);
    sb.append(".");
    return sb.toString();
  }
}
