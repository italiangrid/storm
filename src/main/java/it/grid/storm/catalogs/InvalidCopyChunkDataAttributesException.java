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

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * This class represents an exceptin thrown when the attributes supplied to the
 * constructor of CopyChunkData are invalid, that is if any of the following is
 * _null_: requestToken, fromsURL, toSURL, lifetime, fileStorageType,
 * spaceToken, overwriteOption, status.
 * 
 * @author EGRID - ICTP Trieste
 * @date September, 2005
 * @version 2.0
 */
public class InvalidCopyChunkDataAttributesException extends Exception {

	private static final long serialVersionUID = 6786154038995023512L;

	// booleans that indicate whether the corresponding variable is null
	private boolean nullRequestToken;
	private boolean nullFromSURL;
	private boolean nullToSURL;
	private boolean nullLifetime;
	private boolean nullFileStorageType;
	private boolean nullSpaceToken;
	private boolean nullOverwriteOption;
	private boolean nullStatus;

	/**
	 * Constructor that requires the attributes that caused the exception to be
	 * thrown.
	 */
	public InvalidCopyChunkDataAttributesException(TRequestToken requestToken,
		TSURL fromSURL, TSURL toSURL, TLifeTimeInSeconds lifetime,
		TFileStorageType fileStorageType, TSpaceToken spaceToken,
		TOverwriteMode overwriteOption, TReturnStatus status) {

		nullRequestToken = requestToken == null;
		nullFromSURL = fromSURL == null;
		nullToSURL = toSURL == null;
		nullLifetime = lifetime == null;
		nullFileStorageType = fileStorageType == null;
		nullSpaceToken = spaceToken == null;
		nullOverwriteOption = overwriteOption == null;
		nullStatus = status == null;
	}

	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("Invalid CopyChunkData attributes: null-requestToken=");
		sb.append(nullRequestToken);
		sb.append("; null-fromSURL=");
		sb.append(nullFromSURL);
		sb.append("; null-toSURL=");
		sb.append(nullToSURL);
		sb.append("; null-lifetime=");
		sb.append(nullLifetime);
		sb.append("; null-filestorageType=");
		sb.append(nullFileStorageType);
		sb.append("; null-spaceToken=");
		sb.append(nullSpaceToken);
		sb.append("; null-overwriteOption=");
		sb.append(nullOverwriteOption);
		sb.append("; null-status=");
		sb.append(nullStatus);
		sb.append(".");
		return sb.toString();
	}
}
