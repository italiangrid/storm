/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

public class SRMCommandException extends Exception {

  private static final long serialVersionUID = 1L;

  private final TReturnStatus returnStatus;

  public SRMCommandException(TStatusCode code, String message) {

    super(message);
    this.returnStatus = new TReturnStatus(code, message);
  }

  public TReturnStatus getReturnStatus() {

    return returnStatus;
  }
}
