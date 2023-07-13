/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.PtGData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidPtGAttributesException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 6957632945020144458L;
  protected final boolean nullGu; // true if GridUser is null
  protected final boolean nullChunkData; // true if PtGChunkData is null

  /**
   * Constructor that requires the GridUser, RequestSummaryData, PtGChunkData and
   * GlobalStatusManager that caused the exception to be thrown.
   */
  public InvalidPtGAttributesException(GridUserInterface gu, PtGData chunkData) {

    nullGu = (gu == null);
    nullChunkData = (chunkData == null);
  }

  @Override
  public String toString() {

    return String.format(
        "Invalid attributes when creating PtG: " + "null-GridUser=%b, null-PtGChunkData=%b", nullGu,
        nullChunkData);
  }
}
