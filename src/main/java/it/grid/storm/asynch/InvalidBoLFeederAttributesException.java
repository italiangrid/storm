/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.RequestSummaryData;

/**
 * Class that represents an Exception thrown when a BoLFeeder could not be created because the
 * supplied RequestSummayData or GridUser or GlobalStatusManager were null.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug, 2009
 */
public class InvalidBoLFeederAttributesException extends Exception {

  private static final long serialVersionUID = -5043656524831277137L;

  private final boolean nullRequestSummaryData;
  private final boolean nullGridUser;
  private final boolean nullGlobalStatusManager;

  /**
   * Public constructor that requires the RequestSummaryData and the GridUser that caused the
   * exception to be thrown.
   */
  public InvalidBoLFeederAttributesException(RequestSummaryData rsd, GridUserInterface gu,
      GlobalStatusManager gsm) {

    nullRequestSummaryData = (rsd == null);
    nullGridUser = (gu == null);
    nullGlobalStatusManager = (gsm == null);
  }

  @Override
  public String toString() {

    return String.format(
        "null-RequestSummaryData=%b; null-GridUser=%b; " + "null-GlobalStatusManager=%b",
        nullRequestSummaryData, nullGridUser, nullGlobalStatusManager);
  }
}
