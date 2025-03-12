/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.asynch;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.RequestData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidRequestAttributesException extends Exception {

  private static final long serialVersionUID = 2933131196386843154L;

  /**
   * true if GridUser is null
   */
  protected final boolean nullGu;

  /**
   * true if PtPChunkData is null
   */
  protected final boolean nullChunkData;

  /**
   * Constructor that requires the GridUser, PtPChunkData and OverallRequest, that caused the
   * exception to be thrown.
   */
  public InvalidRequestAttributesException(GridUserInterface gu, RequestData chunkData) {

    nullGu = (gu == null);
    nullChunkData = (chunkData == null);
  }

  @Override
  public String toString() {

    return String.format(
        "Invalid attributes when creating Request: " + "nullGridUser=%b, nullChunkData=%b", nullGu,
        nullChunkData);
  }
}
