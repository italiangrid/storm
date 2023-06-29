/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * Class that represents an Exception thrown when a PtPFeeder could not be created because the
 * supplied RequestSummayData or GridUser were null.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date June, 2005
 */
public class InvalidPtPFeederAttributesException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final boolean nullRequestSummaryData;
  private final boolean nullGridUser;
  private final boolean nullGlobalStatusManager;

  /**
   * Public constructor that requires the RequestSummaryData, the GridUser and the
   * GlobalStatusManager that caused the exception to be thrown.
   */
  public InvalidPtPFeederAttributesException(RequestSummaryData rsd, GridUserInterface gu,
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
