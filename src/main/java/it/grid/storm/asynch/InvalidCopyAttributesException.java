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

import it.grid.storm.catalogs.CopyData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exceptin thrown when a copyChunk is created with any
 * null attribute: GridUser, RequestSummaryData, CopyChunkData,
 * GlobalStatusManager or with a negative counter n.
 * 
 * @author: EGRID - ICTP Trieste
 * @version: 2.0
 * @date: September, 2005
 */
public class InvalidCopyAttributesException extends
	InvalidRequestAttributesException {

	private static final long serialVersionUID = -4447773252958307986L;
	protected boolean negativeN = true;

	/**
	 * Constructor that requires the GridUser, RequestSummaryData and
	 * CopyChunkData, as well as the int counter, that caused the exception to be
	 * thrown.
	 */
	public InvalidCopyAttributesException(GridUserInterface gu,
		CopyData chunkData, int n) {

		super(gu, chunkData);
		negativeN = (n < 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("InvalidCopyAttributesException [negativeN=");
		builder.append(negativeN);
		builder.append(", nullGu=");
		builder.append(nullGu);
		builder.append(", nullChunkData=");
		builder.append(nullChunkData);
		builder.append("]");
		return builder.toString();
	}
}
