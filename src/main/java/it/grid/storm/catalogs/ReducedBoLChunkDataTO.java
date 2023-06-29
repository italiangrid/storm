/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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

		StringBuilder sb = new StringBuilder();
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
