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
 * this is all raw data referring to the ReducedBoLChunkData proper, that is
 * String and primitive types.
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date November, 2006
 */
public class ReducedBoLChunkDataTO {

	private long primaryKey = -1; // ID primary key of record in DB
	private String fromSURL = " ";
	private String normalizedStFN = null;
	private Integer surlUniqueID = null;

	private int status = StatusCodeConverter.getInstance().toDB(
		TStatusCode.SRM_REQUEST_QUEUED);
	private String errString = " ";

	public String errString() {

		return errString;
	}

	public String fromSURL() {

		return fromSURL;
	}

	public long primaryKey() {

		return primaryKey;
	}

	public void setErrString(String s) {

		errString = s;
	}

	public void setFromSURL(String s) {

		fromSURL = s;
	}

	public void setPrimaryKey(long n) {

		primaryKey = n;
	}

	public void setStatus(int n) {

		status = n;
	}

	public int status() {

		return status;
	}

	/**
	 * @param normalizedStFN
	 *          the normalizedStFN to set
	 */
	public void setNormalizedStFN(String normalizedStFN) {

		this.normalizedStFN = normalizedStFN;
	}

	/**
	 * @return the normalizedStFN
	 */
	public String normalizedStFN() {

		return normalizedStFN;
	}

	/**
	 * @param surlUniqueID
	 *          the sURLUniqueID to set
	 */
	public void setSurlUniqueID(Integer surlUniqueID) {

		this.surlUniqueID = surlUniqueID;
	}

	/**
	 * @return the sURLUniqueID
	 */
	public Integer surlUniqueID() {

		return surlUniqueID;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(primaryKey);
		sb.append(" ");
		sb.append(fromSURL);
		sb.append(" ");
		sb.append(normalizedStFN);
		sb.append(" ");
		sb.append(surlUniqueID);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(errString);
		sb.append(" ");
		return sb.toString();
	}
}
