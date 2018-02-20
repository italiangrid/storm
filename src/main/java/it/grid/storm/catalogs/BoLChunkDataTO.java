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

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.TStatusCode;

import java.sql.Timestamp;
import java.util.List;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data
 * referring to the BoLChunkData proper, that is, String and primitive types.
 * 
 * Each field is initialized with default values as per SRM 2.2 specification:
 * protocolList GSIFTP dirOption false status SRM_REQUEST_QUEUED
 * 
 * All other fields are 0 if int, or a white space if String.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLChunkDataTO {

	/* Database table request_Bol fields BEGIN */
	private long primaryKey = -1; // ID primary key of record in DB
	private String fromSURL = " ";
	private boolean dirOption; // initialised in constructor
	private String normalizedStFN = null;
	private Integer surlUniqueID = null;
	/* Database table request_Get fields END */

	private String requestToken = " ";
	private int lifetime = 0;
	private boolean allLevelRecursive; // initialised in constructor
	private int numLevel; // initialised in constructor
	private List<String> protocolList = null; // initialised in constructor
	private long filesize = 0;
	private int status; // initialised in constructor
	private String errString = " ";
	private int deferredStartTime = -1;
	private Timestamp timeStamp = null;

	public BoLChunkDataTO() {

		TURLPrefix protocolPreferences = new TURLPrefix();
		protocolPreferences.addProtocol(Protocol.GSIFTP);
		this.protocolList = TransferProtocolListConverter.toDB(protocolPreferences);
		this.status = StatusCodeConverter.getInstance().toDB(
			TStatusCode.SRM_REQUEST_QUEUED);
		this.dirOption = false;
		this.allLevelRecursive = false;
		this.numLevel = 0;
	}

	public boolean getAllLevelRecursive() {

		return allLevelRecursive;
	}

	public int getDeferredStartTime() {

		return deferredStartTime;
	}

	public boolean getDirOption() {

		return dirOption;
	}

	public String getErrString() {

		return errString;
	}

	public long getFileSize() {

		return filesize;
	}

	public String getFromSURL() {

		return fromSURL;
	}

	public int getLifeTime() {

		return lifetime;
	}

	public int getNumLevel() {

		return numLevel;
	}

	public long getPrimaryKey() {

		return primaryKey;
	}

	public List<String> getProtocolList() {

		return protocolList;
	}

	public String getRequestToken() {

		return requestToken;
	}

	public Timestamp getTimeStamp() {

		return timeStamp;
	}

	public int getStatus() {

		return status;
	}

	public void setAllLevelRecursive(boolean b) {

		allLevelRecursive = b;
	}

	public void setDeferredStartTime(int deferredStartTime) {

		this.deferredStartTime = deferredStartTime;
	}

	public void setDirOption(boolean b) {

		dirOption = b;
	}

	public void setErrString(String s) {

		errString = s;
	}

	public void setFileSize(long n) {

		filesize = n;
	}

	public void setFromSURL(String s) {

		fromSURL = s;
	}

	public void setLifeTime(int n) {

		lifetime = n;
	}

	public void setNumLevel(int n) {

		numLevel = n;
	}

	public void setPrimaryKey(long n) {

		primaryKey = n;
	}

	public void setProtocolList(List<String> l) {

		if ((l != null) && (!l.isEmpty())) {
			protocolList = l;
		}
	}

	public void setRequestToken(String s) {

		requestToken = s;
	}

	public void setTimeStamp(Timestamp timeStamp) {

		this.timeStamp = timeStamp;
	}

	public void setStatus(int n) {

		status = n;
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
	public Integer sulrUniqueID() {

		return surlUniqueID;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(primaryKey);
		sb.append(" ");
		sb.append(requestToken);
		sb.append(" ");
		sb.append(fromSURL);
		sb.append(" ");
		sb.append(normalizedStFN);
		sb.append(" ");
		sb.append(surlUniqueID);
		sb.append(" ");
		sb.append(lifetime);
		sb.append(" ");
		sb.append(dirOption);
		sb.append(" ");
		sb.append(allLevelRecursive);
		sb.append(" ");
		sb.append(numLevel);
		sb.append(" ");
		sb.append(protocolList);
		sb.append(" ");
		sb.append(filesize);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(errString);
		return sb.toString();
	}
}
