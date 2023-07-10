/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TTURL;

/**
 * Class that represents the reply returned from an invocation of SRMStatusOfPutRequest. It supplies
 * methods for quering the toTURL assigned, and the returnStatus of the request.
 *
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September 2005
 */
public class SRMStatusOfPutRequestReply {

  private TTURL toTURL = null; // TTURL as supplied by the invoked server in the
  // SRMStatusOfPutRequest
  private TReturnStatus returnStatus = null; // returnStatus as supplied by the
  // invoked server in the
  // SRMStatusOfPutRequest

  public SRMStatusOfPutRequestReply(TTURL toTURL, TReturnStatus returnStatus)
      throws InvalidPutStatusAttributesException {

    if ((toTURL == null) || (returnStatus == null))
      throw new InvalidPutStatusAttributesException(toTURL, returnStatus);
    this.toTURL = toTURL;
    this.returnStatus = returnStatus;
  }

  /** Method that returns the toTURL that the invoked server assigned to the put request. */
  public TTURL toTURL() {

    return toTURL;
  }

  /** Method that returns the TReturnStatus that the invoked server assigned to the put request. */
  public TReturnStatus returnStatus() {

    return returnStatus;
  }

  public String toString() {

    return "toTURL= " + toTURL + "; returnStatus=" + returnStatus;
  }
}
