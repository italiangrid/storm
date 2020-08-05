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

import it.grid.storm.catalogs.RequestData;
import it.grid.storm.griduser.GridUserInterface;

/**
 * @author Michele Dibenedetto
 * 
 */
public class InvalidRequestAttributesException extends Exception {

	private static final long serialVersionUID = 2933131196386843154L;

	/**
	 * true if GridUser is null
	 */
	protected boolean nullGu = true;

	/**
	 * true if PtPChunkData is null
	 */
	protected boolean nullChunkData = true;

	/**
	 * Constructor that requires the GridUser, PtPChunkData and OverallRequest,
	 * that caused the exception to be thrown.
	 */
	public InvalidRequestAttributesException(GridUserInterface gu,
		RequestData chunkData) {

		nullGu = (gu == null);
		nullChunkData = (chunkData == null);
	}

	public String toString() {

		return String.format("Invalid attributes when creating Request: "
			+ "nullGridUser=%b, nullChunkData=%b", nullGu, nullChunkData);
	}
}
