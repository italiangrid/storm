/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.catalogs.BoLPersistentChunkData;
import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.griduser.GridUserInterface;

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
