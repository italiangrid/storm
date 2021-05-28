/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.model.PersistentChunkData;
import it.grid.storm.persistence.model.RequestSummaryData;

/**
 * This class represents an Exceptin thrown when a PtPChunk is created with any null attribute:
 * GridUser, RequestSummaryData, PtPChunkData or OverallRequest.
 * 
 * @author: EGRID - ICTP Trieste
 * @version: 2.0
 * @date: June, 2005
 */
public class InvalidPersistentRequestAttributesException extends InvalidRequestAttributesException {

  /**
   * 
   */
  private static final long serialVersionUID = 2359138715273364531L;

  /**
   * true if RequestSummaryData is null
   */
  private final boolean nullRsd;

  /**
   * true if OverallRequest is null
   */
  protected final boolean nullGsm;

  /**
   * Constructor that requires the GridUser, RequestSummaryData, PtPChunkData and OverallRequest,
   * that caused the exception to be thrown.
   */
  public InvalidPersistentRequestAttributesException(GridUserInterface gu, RequestSummaryData rsd,
      PersistentChunkData chunkData, GlobalStatusManager gsm) {

    super(gu, chunkData);
    nullRsd = (rsd == null);
    nullGsm = (gsm == null);
  }

  @Override
  public String toString() {

    return String.format(
        "Invalid attributes when creating Chunk: "
            + "nullGridUser=%b, nullRequestSumamryData=%b, nullChunkData=%b, " + "nullGsm=%b",
        nullGu, nullRsd, nullChunkData, nullGsm);
  }
}
