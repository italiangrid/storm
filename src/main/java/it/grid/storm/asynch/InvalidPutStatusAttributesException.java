/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TTURL;

/**
 * Class that represents an exception thrown when an SRMStatusOfPutRequestReply cannot be created
 * because the supplied toTURL or returnStatus are null.
 *
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date October, 2005
 */
public class InvalidPutStatusAttributesException extends Exception {

  /** */
  private static final long serialVersionUID = 1L;

  // boolean indicating whether the supplied TURL is null or not
  private final boolean nullToTURL;

  // boolean indicating whether the supplied TReturnStatus is null or not
  private final boolean nullReturnStatus;

  /** Constructor that requires the attributes that caused the exception to be thrown. */
  public InvalidPutStatusAttributesException(TTURL toTURL, TReturnStatus returnStatus) {

    nullToTURL = (toTURL == null);
    nullReturnStatus = (returnStatus == null);
  }

  @Override
  public String toString() {

    return String.format("nullToTURL=%b; nullReturnStatus=%b", nullToTURL, nullReturnStatus);
  }
}
