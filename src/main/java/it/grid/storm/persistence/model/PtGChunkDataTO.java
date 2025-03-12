/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.model;

import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.common.types.TURLPrefix;
import java.sql.Timestamp;
import java.util.List;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.converter.TransferProtocolListConverter;

/**
 * Class that represents a row in the Persistence Layer: this is all raw data
 * referring to the PtGChunkData proper, that is, String and primitive types.
 * 
 * Each field is initialized with default values as per SRM 2.2 specification:
 * protocolList GSIFTP dirOption false status SRM_REQUEST_QUEUED
 * 
 * All other fields are 0 if int, or a white space if String.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date June 2005
 */
public class PtGChunkDataTO {

	private static final String FQAN_SEPARATOR = "#";
	/* Database table request_Get fields BEGIN */
	private long primaryKey = -1; // ID primary key of record in DB
	private boolean dirOption; // initialised in constructor
	private String fromSURL = " ";
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
	private String turl = " ";
	private Timestamp timeStamp;
	private String clientDN = null;
	private String vomsAttributes = null;

	public PtGChunkDataTO() {

		TURLPrefix protocolPreferences = new TURLPrefix();
		protocolPreferences.addProtocol(Protocol.GSIFTP);
		this.protocolList = TransferProtocolListConverter.toDB(protocolPreferences);
		this.status = StatusCodeConverter.getInstance().toDB(
			TStatusCode.SRM_REQUEST_QUEUED);
		this.dirOption = false;
		//
		this.allLevelRecursive = false;
		this.numLevel = 0;
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

	public String fromSURL() {

		return fromSURL;
	}

	public void setFromSURL(String s) {

		fromSURL = s;
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
	 * @param sURLUniqueID
	 *          the sURLUniqueID to set
	 */
	public void setSurlUniqueID(Integer sURLUniqueID) {

		this.surlUniqueID = sURLUniqueID;
	}

	/**
	 * @return the sURLUniqueID
	 */
	public Integer surlUniqueID() {

		return surlUniqueID;
	}

	public int lifeTime() {

		return lifetime;
	}

	public void setLifeTime(int n) {

		lifetime = n;
	}

	public boolean dirOption() {

		return dirOption;
	}

	public void setDirOption(boolean b) {

		dirOption = b;
	}

	public boolean allLevelRecursive() {

		return allLevelRecursive;
	}

	public void setAllLevelRecursive(boolean b) {

		allLevelRecursive = b;
	}

	public int numLevel() {

		return numLevel;
	}

	public void setNumLevel(int n) {

		numLevel = n;
	}

	public List<String> protocolList() {

		return protocolList;
	}

	public void setProtocolList(List<String> l) {

		if ((l != null) && (!l.isEmpty()))
			protocolList = l;
	}

	public long fileSize() {

		return filesize;
	}

	public void setFileSize(long n) {

		filesize = n;
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

	public String turl() {

		return turl;
	}

	public void setTurl(String s) {

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
		sb.append(" ");
		sb.append(turl);
		return sb.toString();
	}

}
