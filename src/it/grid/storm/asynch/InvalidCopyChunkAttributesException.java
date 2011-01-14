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
import it.grid.storm.catalogs.CopyChunkData;
import it.grid.storm.griduser.GridUserInterface;


/**
 * This class represents an Exceptin thrown when a copyChunk is created with
 * any null attribute: GridUser, RequestSummaryData, CopyChunkData,
 * GlobalStatusManager or with a negative counter n.
 *
 * @author:  EGRID - ICTP Trieste
 * @version: 2.0
 * @date:    September, 2005
 */
public class InvalidCopyChunkAttributesException extends Exception {

    private boolean nullGu = true; //true if GridUser is null
    private boolean nullRsd = true; //true if RequestSummaryData is null
    private boolean nullChunkData = true; //true if CopyChunkData is null
    private boolean negativeN = true; //true if counter is negative
    private boolean nullGlobalStatusManager = true;

    /**
     * Constructor that requires the GridUser, RequestSummaryData and
     * CopyChunkData, as well as the int counter, that caused the exception
     * to be thrown.
     */
    public InvalidCopyChunkAttributesException(GridUserInterface gu, RequestSummaryData rsd, CopyChunkData chunkData, int n, GlobalStatusManager gsm) {
        nullGu = (gu==null);
        nullRsd = (rsd==null);
        nullChunkData = (chunkData==null);
        negativeN = (n<0);
        nullGlobalStatusManager = (gsm==null);
    }

    public String toString() {
        return "Invalid attributes when creating CopyChunk: nullGridUser="+nullGu+", nullRequestSummaryData="+nullRsd+", nullCopyChunkData="+nullChunkData+", negativeN="+negativeN+", nullGlobalStatusManager="+nullGlobalStatusManager;
    }
}
