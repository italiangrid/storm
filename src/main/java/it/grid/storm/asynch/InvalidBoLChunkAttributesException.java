/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.BoLPersistentChunkData;
import it.grid.storm.persistence.model.RequestSummaryData;

/**
 * This class represents an Exception thrown when a BoLChunk is created with any null attribute:
 * GridUser, RequestSummaryData, BoLChunkData or GlobalStatusManager.
 * 
 * @author: CNAF
 * @version: 1.0
 * @date: Aug 2009
 */
public class InvalidBoLChunkAttributesException extends Exception {

  private static final long serialVersionUID = 2320080131526579634L;

  private final boolean nullGu; // true if GridUser is null
  private final boolean nullRsd; // true if RequestSummaryData is null
  private final boolean nullChunkData; // true if BoLChunkData is null
  private final boolean nullGlobalStatusManager; // true if gsm is null

  /**
   * Constructor that requires the GridUser, RequestSummaryData, BoLChunkData and
   * GlobalStatusManager that caused the exception to be thrown.
   */
  public InvalidBoLChunkAttributesException(GridUserInterface gu, RequestSummaryData rsd,
      BoLPersistentChunkData chunkData, GlobalStatusManager gsm) {

    nullGu = (gu == null);
    nullRsd = (rsd == null);
    nullChunkData = (chunkData == null);
    nullGlobalStatusManager = (gsm == null);
  }

  @Override
  public String toString() {

    return String.format(
        "Invalid attributes when creating BoLChunk: "
            + "nullGridUser=%b; nullRequestSumamryData=%b; nullBoLChunkData=%b; "
            + "nullGlobalStatusManager=%b",
        nullGu, nullRsd, nullChunkData, nullGlobalStatusManager);
  }
}
