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
import it.grid.storm.srm.types.TFileStorageType;

/**
 * Class that represents some of the fields in a row in the Persistence Layer:
 * this is all raw data referring to the ReducedPtPChunkData proper, that is
 * String and primitive types.
 * 
 * @author EGRID ICTP
 * @version 1.0
 * @date January, 2007
 */
public class ReducedPtPChunkDataTO {

	private long primaryKey = -1; // ID primary key of record in DB
	private String toSURL = " ";
	private String normalizedStFN = null;
	private Integer surlUniqueID = null;

	private int status = StatusCodeConverter.getInstance().toDB(
		TStatusCode.SRM_REQUEST_QUEUED);
	private String errString = " ";
	private String fileStorageType = FileStorageTypeConverter.getInstance().toDB(
		TFileStorageType.VOLATILE);
	private int fileLifetime = -1;

	public long primaryKey() {

		return primaryKey;
	}

	public void setPrimaryKey(long n) {

		primaryKey = n;
	}

	public String toSURL() {

		return toSURL;
	}

	public void setToSURL(String s) {

		toSURL = s;
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

	public String fileStorageType() {

		return fileStorageType;
	}

	/**
	 * Method that sets the FileStorageType: if it is null nothing gets set. The
	 * deafult value is Volatile.
	 */
	public void setFileStorageType(String s) {

		if (s != null)
			fileStorageType = s;
	}

	public int fileLifetime() {

		return fileLifetime;
	}

	public void setFileLifetime(int n) {

		fileLifetime = n;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(primaryKey);
		sb.append(" ");
		sb.append(toSURL);
		sb.append(" ");
		sb.append(normalizedStFN);
		sb.append(" ");
		sb.append(surlUniqueID);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(errString);
		sb.append(" ");
		sb.append(fileStorageType);
		sb.append(" ");
		return sb.toString();
	}
}
