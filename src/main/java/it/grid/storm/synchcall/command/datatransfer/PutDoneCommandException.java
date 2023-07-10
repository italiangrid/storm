/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;

public class PutDoneCommandException extends Exception {

  private static final long serialVersionUID = 1L;

  private TReturnStatus status;

  public PutDoneCommandException(TReturnStatus status) {

    super(String.format("%s: %s", status.getStatusCode().getValue(), status.getExplanation()));
    this.status = status;
  }

  public PutDoneCommandException(TReturnStatus status, Throwable cause) {

    super(
        String.format("%s: %s", status.getStatusCode().getValue(), status.getExplanation()), cause);
    this.status = status;
  }

  public TReturnStatus getReturnStatus() {
    return status;
  }
}
