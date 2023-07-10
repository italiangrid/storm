/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.srm.types.TRequestToken;

/**
 * Class that represents a reply to an issued SRMPrepareToPut command. It provides a method to
 * recover the assigned request token.
 *
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2005
 */
public class SRMPrepareToPutReply {

  // TRequestToken assigned during the SRM prepare to put operation
  private TRequestToken requestToken = null;

  /**
   * Constructor that requires the assigned TRequestToken; if it is null, an
   * InvalidPutReplyAttributeException is thrown.
   */
  public SRMPrepareToPutReply(TRequestToken requestToken) throws InvalidPutReplyAttributeException {

    if (requestToken == null) throw new InvalidPutReplyAttributeException();
    this.requestToken = requestToken;
  }

  /** Method that returns the assigned request token. */
  public TRequestToken requestToken() {

    return requestToken;
  }

  public String toString() {

    return "requestToken=" + requestToken;
  }
}
