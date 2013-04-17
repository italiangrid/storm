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

/**
 * Class that represents an exception thrown by a GridFTPTransferClient, in case
 * for any reason the operation could not be carried out.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2005
 */
public class GridFTPTransferClientException extends Exception {

	private String explanation = ""; // String containing an explanation of what
																		// went wrong

	/**
	 * Constructor that requires a String with an explanation of what went wrong.
	 */
	public GridFTPTransferClientException(String explanation) {

		if (explanation != null)
			this.explanation = explanation;
	}

	public String toString() {

		return explanation;
	}
}
