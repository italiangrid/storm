/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.catalogs.PersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exceptin thrown when a PtPChunk is created with any null attribute:
 * GridUser, RequestSummaryData, PtPChunkData or OverallRequest.
 *
 * @author: EGRID - ICTP Trieste
 * @version: 2.0
 * @date: June, 2005
 */
public class InvalidPersistentRequestAttributesException extends InvalidRequestAttributesException {

  /** */
  private static final long serialVersionUID = 2359138715273364531L;

  /** true if RequestSummaryData is null */
  private final boolean nullRsd;

  /** true if OverallRequest is null */
  protected final boolean nullGsm;

  /**
   * Constructor that requires the GridUser, RequestSummaryData, PtPChunkData and OverallRequest,
   * that caused the exception to be thrown.
   */
  public InvalidPersistentRequestAttributesException(
      GridUserInterface gu,
      RequestSummaryData rsd,
      PersistentChunkData chunkData,
      GlobalStatusManager gsm) {

    super(gu, chunkData);
    nullRsd = (rsd == null);
    nullGsm = (gsm == null);
  }

  @Override
  public String toString() {

    return String.format(
        "Invalid attributes when creating Chunk: "
            + "nullGridUser=%b, nullRequestSumamryData=%b, nullChunkData=%b, "
            + "nullGsm=%b",
        nullGu, nullRsd, nullChunkData, nullGsm);
  }
}
