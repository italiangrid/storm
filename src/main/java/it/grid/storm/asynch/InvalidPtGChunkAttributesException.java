/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.PtGData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exceptin thrown when a PtGChunk is created with any null attribute:
 * GridUser, RequestSummaryData, PtGChunkData or GlobalStatusManager.
 * 
 * @author: EGRID - ICTP Trieste
 * @version: 2.0
 * @date: May 16th, 2005
 */
public class InvalidPtGChunkAttributesException extends InvalidPtGAttributesException {

  /**
   * 
   */
  private static final long serialVersionUID = 754275707315797289L;
  /**
   * true if RequestSummaryData is null
   */
  private final boolean nullRsd;

  /**
   * true if gsm is null
   */
  private final boolean nullGlobalStatusManager;

  /**
   * Constructor that requires the GridUser, RequestSummaryData, PtGChunkData and
   * GlobalStatusManager that caused the exception to be thrown.
   */
  public InvalidPtGChunkAttributesException(GridUserInterface gu, RequestSummaryData rsd,
      PtGData chunkData, GlobalStatusManager gsm) {

    super(gu, chunkData);
    nullRsd = (rsd == null);
    nullGlobalStatusManager = (gsm == null);
  }

  @Override
  public String toString() {

    return String.format(
        "Invalid attributes when creating PtGChunk: "
            + "null-GridUser=%b, null-RequestSumamryData=%b, null-PtGChunkData=%b, "
            + "null-GlobalStatusManager=%b",
        nullGu, nullRsd, nullChunkData, nullGlobalStatusManager);
  }
}
