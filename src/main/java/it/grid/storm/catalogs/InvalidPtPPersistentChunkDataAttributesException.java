/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TTURL;

/**
 * This class represents an exceptin thrown when the attributes supplied to the
 * constructor of PtPChunkData are invalid, that is if any of the following is
 * _null_: requestToken, toSURL, lifetime, fileStorageType, spaceToken,
 * knownSizeOfThisFile, TURLPrefix transferProtocols, overwriteOption, fileSize,
 * status, transferURL.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 2.0
 */
public class InvalidPtPPersistentChunkDataAttributesException extends
	InvalidPtPDataAttributesException {

	private static final long serialVersionUID = -5117535717125685975L;
	/**
	 * booleans that indicate whether the corresponding variable is null
	 */
	boolean nullRequestToken;

	/**
	 * Constructor that requires the attributes that caused the exception to be
	 * thrown.
	 */
	public InvalidPtPPersistentChunkDataAttributesException(
		TRequestToken requestToken, TSURL toSURL, TLifeTimeInSeconds fileLifetime,
		TLifeTimeInSeconds pinLifetime, TFileStorageType fileStorageType,
		TSpaceToken spaceToken, TSizeInBytes knownSizeOfThisFile,
		TURLPrefix transferProtocols, TOverwriteMode overwriteOption,
		TReturnStatus status, TTURL transferURL) {

		super(toSURL, fileLifetime, pinLifetime, fileStorageType, spaceToken,
			knownSizeOfThisFile, transferProtocols, overwriteOption, status,
			transferURL);
		nullRequestToken = requestToken == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder
			.append("InvalidPtPPersistentChunkDataAttributesException [nullRequestToken=");
		builder.append(nullRequestToken);
		builder.append(", nullSpaceToken=");
		builder.append(nullSpaceToken);
		builder.append(", nullPinLifetime=");
		builder.append(nullPinLifetime);
		builder.append(", nullFileLifetime=");
		builder.append(nullFileLifetime);
		builder.append(", nullFileStorageType=");
		builder.append(nullFileStorageType);
		builder.append(", nullKnownSizeOfThisFile=");
		builder.append(nullKnownSizeOfThisFile);
		builder.append(", nullOverwriteOption=");
		builder.append(nullOverwriteOption);
		builder.append(", nullSURL=");
		builder.append(nullSURL);
		builder.append(", nullTransferProtocols=");
		builder.append(nullTransferProtocols);
		builder.append(", nullStatus=");
		builder.append(nullStatus);
		builder.append(", nullTransferURL=");
		builder.append(nullTransferURL);
		builder.append("]");
		return builder.toString();
	}
}
