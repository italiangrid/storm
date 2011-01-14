/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.asynch;

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.PtPChunkData;
import it.grid.storm.griduser.GridUserInterface;


/**
 * This class represents an Exceptin thrown when a PtPChunk is created with
 * any null attribute: GridUser, RequestSummaryData, PtPChunkData or OverallRequest.
 *
 * @author:  EGRID - ICTP Trieste
 * @version: 2.0
 * @date:    June, 2005
 */
public class InvalidPtPChunkAttributesException extends Exception {

    private boolean nullGu = true; //true if GridUser is null
    private boolean nullRsd = true; //true if RequestSummaryData is null
    private boolean nullChunkData = true; //true if PtPChunkData is null
    private boolean nullOverallRequest = true; //true if OverallRequest is null

    /**
     * Constructor that requires the GridUser, RequestSummaryData,
     * PtPChunkData and OverallRequest, that caused the exception to be thrown.
     */
    public InvalidPtPChunkAttributesException(GridUserInterface gu, RequestSummaryData rsd, PtPChunkData chunkData, GlobalStatusManager or) {
        nullGu = (gu==null);
        nullRsd = (rsd==null);
        nullChunkData = (chunkData==null);
        nullOverallRequest = (or==null);
    }

    public String toString() {
        return "Invalid attributes when creating PtPChunk: nullGridUser="+nullGu+", nullRequestSumamryData="+nullRsd+"nullPtPChunkData="+nullChunkData+"nullOverallRequest="+nullOverallRequest;
    }
}
