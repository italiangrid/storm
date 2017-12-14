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
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TTURL;

/**
 * This class represents an exceptin thrown when the attributes supplied to the
 * constructor of BoLChunkData are invalid, that is if any of the following is
 * _null_: requestToken, fromSURL, lifeTime, numOfLevels, transferProtocols,
 * fileSize, status, transferURL.
 * 
 * @author CNAF
 * @date Aug 2009
 * @version 1.0
 */
public class InvalidBoLChunkDataAttributesException extends Exception {

	private static final long serialVersionUID = 5657310881067434280L;

	// booleans that indicate whether the corresponding variable is null
	private boolean nullRequestToken;
	private boolean nullFromSURL;
	private boolean nullLifeTime;
	private boolean nullDirOption;
	private boolean nullTransferProtocols;
	private boolean nullFileSize;
	private boolean nullStatus;
	private boolean nullTransferURL;

	/**
	 * Constructor that requires the attributes that caused the exception to be
	 * thrown.
	 */
	public InvalidBoLChunkDataAttributesException(TRequestToken requestToken,
		TSURL fromSURL, TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix transferProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL) {

		nullRequestToken = requestToken == null;
		nullFromSURL = fromSURL == null;
		nullLifeTime = lifeTime == null;
		nullDirOption = dirOption == null;
		nullTransferProtocols = transferProtocols == null;
		nullFileSize = fileSize == null;
		nullStatus = status == null;
		nullTransferURL = transferURL == null;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Invalid BoLChunkData attributes: null-requestToken=");
		sb.append(nullRequestToken);
		sb.append("; nul-fromSURL=");
		sb.append(nullFromSURL);
		sb.append("; null-lifeTime=");
		sb.append(nullLifeTime);
		sb.append("; null-dirOption=");
		sb.append(nullDirOption);
		sb.append("; null-transferProtocols=");
		sb.append(nullTransferProtocols);
		sb.append("; null-fileSize=");
		sb.append(nullFileSize);
		sb.append("; null-status=");
		sb.append(nullStatus);
		sb.append("; null-transferURL=");
		sb.append(nullTransferURL);
		sb.append(".");
		return sb.toString();
	}
}
