/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.RequestSummaryData;

/**
 * Class that represents an Exception thrown when a PtGFeeder could not be created because the
 * supplied RequestSummayData or GridUser or GlobalStatusManager were null.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date July, 2005
 */
public class InvalidPtGFeederAttributesException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final boolean nullRequestSummaryData;
  private final boolean nullGridUser;
  private final boolean nullGlobalStatusManager;

  /**
   * Public constructor that requires the RequestSummaryData and the GridUser that caused the
   * exception to be thrown.
   */
  public InvalidPtGFeederAttributesException(RequestSummaryData rsd, GridUserInterface gu,
      GlobalStatusManager gsm) {

    nullRequestSummaryData = (rsd == null);
    nullGridUser = (gu == null);
    nullGlobalStatusManager = (gsm == null);
  }

  public String toString() {

    return String.format(
        "null-RequestSummaryData=%b; null-GridUser=%b; " + "null-GlobalStatusManager=%b",
        nullRequestSummaryData, nullGridUser, nullGlobalStatusManager);
  }
}
