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

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exception thrown when a RequestSummaryData object is
 * created with any invalid attributes: null TRequestType, null TRequestToken,
 * null VomsGridUser.
 * 
 * @author EGRID - ICTP Trieste
 * @date March 18th, 2005
 * @version 3.0
 */
public class InvalidRequestSummaryDataAttributesException extends Exception {

	private static final long serialVersionUID = -7729349713696058669L;

	// booleans true if the corresponding variablesare null or negative
	private boolean nullRequestType = true;
	private boolean nullRequestToken = true;
	private boolean nullVomsGridUser = true;

	/**
	 * Constructor that requires the attributes that caused the exception to be
	 * thrown.
	 */
	public InvalidRequestSummaryDataAttributesException(TRequestType requestType,
		TRequestToken requestToken, GridUserInterface gu) {

		nullRequestType = (requestType == null);
		nullRequestToken = (requestToken == null);
		nullVomsGridUser = (gu == null);
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Invalid RequestSummaryData attributes exception: ");
		sb.append("nullRequestType=");
		sb.append(nullRequestType);
		sb.append("; nullRequestToken=");
		sb.append(nullRequestToken);
		sb.append("; nullVomsGridUser=");
		sb.append(nullVomsGridUser);
		return sb.toString();
	}
}
