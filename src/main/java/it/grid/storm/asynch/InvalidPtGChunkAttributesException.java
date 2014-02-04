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

import it.grid.storm.catalogs.RequestSummaryData;
import it.grid.storm.catalogs.PtGData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exceptin thrown when a PtGChunk is created with any
 * null attribute: GridUser, RequestSummaryData, PtGChunkData or
 * GlobalStatusManager.
 * 
 * @author: EGRID - ICTP Trieste
 * @version: 2.0
 * @date: May 16th, 2005
 */
public class InvalidPtGChunkAttributesException extends
	InvalidPtGAttributesException {

	/**
     * 
     */
	private static final long serialVersionUID = 754275707315797289L;
	/**
	 * true if RequestSummaryData is null
	 */
	private boolean nullRsd = true;

	/**
	 * true if gsm is null
	 */
	private boolean nullGlobalStatusManager = true;

	/**
	 * Constructor that requires the GridUser, RequestSummaryData, PtGChunkData
	 * and GlobalStatusManager that caused the exception to be thrown.
	 */
	public InvalidPtGChunkAttributesException(GridUserInterface gu,
		RequestSummaryData rsd, PtGData chunkData, GlobalStatusManager gsm) {

		super(gu, chunkData);
		nullRsd = (rsd == null);
		nullGlobalStatusManager = (gsm == null);
	}

	public String toString() {

		return String.format("Invalid attributes when creating PtGChunk: "
			+ "null-GridUser=%b, null-RequestSumamryData=%b, null-PtGChunkData=%b, "
			+ "null-GlobalStatusManager=%b", nullGu, nullRsd, nullChunkData, 
			nullGlobalStatusManager);
	}
}
