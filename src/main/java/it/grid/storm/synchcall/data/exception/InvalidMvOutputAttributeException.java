/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.TReturnStatus;

public class InvalidMvOutputAttributeException extends Exception {

  private boolean nullStat = true;

  public InvalidMvOutputAttributeException(TReturnStatus stat) {

    nullStat = (stat == null);
  }

  public String toString() {

    return "nullStatus = " + nullStat;
  }
}
