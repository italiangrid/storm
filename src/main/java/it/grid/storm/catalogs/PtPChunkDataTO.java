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
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TStatusCode;

import java.sql.Timestamp;
import java.util.List;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data
 * referring to the PtPChunkData proper, that is, String and primitive types.
 * 
 * Each field is initialized with default values as per SRM 2.2 specification:
 * protocolList GSIFTP fileStorageType VOLATILE overwriteMode NEVER status
 * SRM_REQUEST_QUEUED
 * 
 * All other fields are 0 if int, or a white space if String.
 * 
 * @author EGRID ICTP
 * @version 2.0
 * @date June 2005
 */
public class PtPChunkDataTO {

	private static final String FQAN_SEPARATOR = "#";
	/* Database table request_Get fields BEGIN */
	private long primaryKey = -1; // ID primary key of status_Put record in DB
	private String toSURL = " ";
	private long expectedFileSize = 0;
	private String normalizedStFN = null;
	private Integer surlUniqueID = null;
	/* Database table request_Get fields END */

	private String requestToken = " ";
	private int pinLifetime = -1;
	private int fileLifetime = -1;
	private String fileStorageType = null; // initialised in constructor
	private String spaceToken = " ";
	private List<String> protocolList = null; // initialised in constructor
	private String overwriteOption = null; // initialised in constructor
	private int status; // initialised in constructor
	private String errString = " ";
	private String turl = " ";
	private Timestamp timeStamp = null;

	private String clientDN = null;
	private String vomsAttributes = null;


	public PtPChunkDataTO() {

		this.fileStorageType = FileStorageTypeConverter.getInstance().toDB(
			TFileStorageType.getTFileStorageType(Configuration.getInstance()
				.getDefaultFileStorageType()));
		TURLPrefix protocolPreferences = new TURLPrefix();
		protocolPreferences.addProtocol(Protocol.GSIFTP);
		this.protocolList = TransferProtocolListConverter.toDB(protocolPreferences);
		this.overwriteOption = OverwriteModeConverter.getInstance().toDB(
			TOverwriteMode.NEVER);
		this.status = StatusCodeConverter.getInstance().toDB(
			TStatusCode.SRM_REQUEST_QUEUED);
	}

	public long primaryKey() {

		return primaryKey;
	}

	public void setPrimaryKey(long n) {

		primaryKey = n;
	}

	public String requestToken() {

		return requestToken;
	}

	public void setRequestToken(String s) {

		requestToken = s;
	}

	public Timestamp timeStamp() {

		return timeStamp;
	}

	public void setTimeStamp(Timestamp timeStamp) {

		this.timeStamp = timeStamp;
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
	public String normalizedStFN() {

		return normalizedStFN;
	}

	/**
	 * @param normalizedStFN
	 *          the normalizedStFN to set
	 */
	public void setNormalizedStFN(String normalizedStFN) {

		this.normalizedStFN = normalizedStFN;
	}

	/**
	 * @return the surlUniqueID
	 */
	public Integer surlUniqueID() {

		return surlUniqueID;
	}

	/**
	 * @param surlUniqueID
	 *          the surlUniqueID to set
	 */
	public void setSurlUniqueID(Integer surlUniqueID) {

		this.surlUniqueID = surlUniqueID;
	}

	public int pinLifetime() {

		return pinLifetime;
	}

	public void setPinLifetime(int n) {

		pinLifetime = n;
	}

	public int fileLifetime() {

		return fileLifetime;
	}

	public void setFileLifetime(int n) {

		fileLifetime = n;
	}

	public String fileStorageType() {

		return fileStorageType;
	}

	/**
	 * Method that sets the FileStorageType: if it is null nothing gets set. The
	 * deafult value is Permanent.
	 */
	public void setFileStorageType(String s) {

		if (s != null)
			fileStorageType = s;
	}

	public String spaceToken() {

		return spaceToken;
	}

	public void setSpaceToken(String s) {

		spaceToken = s;
	}

	public long expectedFileSize() {

		return expectedFileSize;
	}

	public void setExpectedFileSize(long l) {

		expectedFileSize = l;
	}

	public List<String> protocolList() {

		return protocolList;
	}

	public void setProtocolList(List<String> l) {

		if ((l != null) && (!l.isEmpty()))
			protocolList = l;
	}

	public String overwriteOption() {

		return overwriteOption;
	}

	/**
	 * Method that sets the OverwriteMode: if it is null nothing gets set. The
	 * deafult value is Never.
	 */
	public void setOverwriteOption(String s) {

		if (s != null)
			overwriteOption = s;
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

	public String transferURL() {

		return turl;
	}

	public void setTransferURL(String s) {

		turl = s;
	}

	public String clientDN() {

		return clientDN;
	}

	public void setClientDN(String s) {

		clientDN = s;
	}

	public String vomsAttributes() {

		return vomsAttributes;
	}

	public void setVomsAttributes(String s) {

		vomsAttributes = s;
	}

	public void setVomsAttributes(String[] fqaNsAsString) {

		vomsAttributes = "";
		for (int i = 0; i < fqaNsAsString.length; i++) {
			vomsAttributes += fqaNsAsString[i];
			if (i < fqaNsAsString.length - 1) {
				vomsAttributes += FQAN_SEPARATOR;
			}
		}

	}

	public String[] vomsAttributesArray() {

		return vomsAttributes.split(FQAN_SEPARATOR);
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(primaryKey);
		sb.append(" ");
		sb.append(requestToken);
		sb.append(" ");
		sb.append(toSURL);
		sb.append(" ");
		sb.append(normalizedStFN);
		sb.append(" ");
		sb.append(surlUniqueID);
		sb.append(" ");
		sb.append(pinLifetime);
		sb.append(" ");
		sb.append(fileLifetime);
		sb.append(" ");
		sb.append(fileStorageType);
		sb.append(" ");
		sb.append(spaceToken);
		sb.append(" ");
		sb.append(expectedFileSize);
		sb.append(" ");
		sb.append(protocolList);
		sb.append(" ");
		sb.append(overwriteOption);
		sb.append(" ");
		sb.append(status);
		sb.append(" ");
		sb.append(errString);
		sb.append(" ");
		sb.append(turl);
		return sb.toString();
	}

}
