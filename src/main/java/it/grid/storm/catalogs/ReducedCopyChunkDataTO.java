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

import it.grid.storm.srm.types.TStatusCode;

/**
 * Class that represents some of the fields in a row in the Persistence Layer:
 * this is all raw data referring to the ReducedCopyChunkData proper, that is
 * String and primitive types.
 * 
 * All other fields are 0 if int, or a white space if String.
 * 
 * @author Michele Dibenedetto
 */
public class ReducedCopyChunkDataTO {

	/* Database table request_Get fields BEGIN */
	private long primaryKey = -1; // ID primary key of record in DB
	private String fromSURL = " ";
	private String normalizedSourceStFN = null;
	private Integer sourceSurlUniqueID = null;
	private String toSURL = " ";
	private String normalizedTargetStFN = null;
	private Integer targetSurlUniqueID = null;
	/* Database table request_Get fields END */

	private int status = StatusCodeConverter.getInstance().toDB(
		TStatusCode.SRM_REQUEST_QUEUED);
	private String errString = " ";

	public long primaryKey() {

		return primaryKey;
	}

	public void setPrimaryKey(long n) {

		primaryKey = n;
	}

	public String fromSURL() {

		return fromSURL;
	}

	public void setFromSURL(String s) {

		fromSURL = s;
	}

	/**
	 * @return the normalizedStFN
	 */
	public String normalizedSourceStFN() {

		return normalizedSourceStFN;
	}

	/**
	 * @param normalizedStFN
	 *          the normalizedStFN to set
	 */
	public void setNormalizedSourceStFN(String normalizedStFN) {

		this.normalizedSourceStFN = normalizedStFN;
	}

	/**
	 * @return the surlUniqueID
	 */
	public Integer sourceSurlUniqueID() {

		return sourceSurlUniqueID;
	}

	/**
	 * @param surlUniqueID
	 *          the surlUniqueID to set
	 */
	public void setSourceSurlUniqueID(Integer surlUniqueID) {

		this.sourceSurlUniqueID = surlUniqueID;
	}

	public String toSURL() {

		return toSURL;
	}

	public void setToSURL(String s) {

		toSURL = s;
	}

	/**
	 * @return the normalizedStFN
	 */
	public String normalizedTargetStFN() {

		return normalizedTargetStFN;
	}

	/**
	 * @param normalizedStFN
	 *          the normalizedStFN to set
	 */
	public void setNormalizedTargetStFN(String normalizedStFN) {

		this.normalizedTargetStFN = normalizedStFN;
	}

	/**
	 * @return the surlUniqueID
	 */
	public Integer targetSurlUniqueID() {

		return targetSurlUniqueID;
	}

	/**
	 * @param surlUniqueID
	 *          the surlUniqueID to set
	 */
	public void setTargetSurlUniqueID(Integer surlUniqueID) {

		this.targetSurlUniqueID = surlUniqueID;
	}

	public int status() {

		return status;
	}

	public void setStatus(int n) {

		status = n;
	}

	public String errString() {

		return errString;
	}

	public void setErrString(String s) {

		errString = s;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(primaryKey);
		sb.append(" ");
		sb.append(fromSURL);
		sb.append(" ");
		sb.append(normalizedSourceStFN);
		sb.append(" ");
		sb.append(sourceSurlUniqueID);
		sb.append(" ");
		sb.append(toSURL);
		sb.append(" ");
		sb.append(normalizedTargetStFN);
		sb.append(" ");
		sb.append(targetSurlUniqueID);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(errString);
		sb.append(" ");
		return sb.toString();
	}
}
