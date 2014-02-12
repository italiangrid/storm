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
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a PrepareToGetChunkData, that is part of a multifile
 * PrepareToGet srm request. It contains data about: the requestToken, the
 * fromSURL, the requested lifeTime of pinning, the TDirOption which tells
 * whether the requested SURL is a directory and if it must be recursed at all
 * levels, as well as the desired number of levels to recurse, the desired
 * transferProtocols in order of preference, the fileSize, and the transferURL
 * for the supplied SURL.
 * 
 * @author EGRID - ICTP Trieste
 * @date March 21st, 2005
 * @version 3.0
 */
public class PtGPersistentChunkData extends IdentityPtGData implements
	PersistentChunkData {

	private static final Logger log = LoggerFactory
		.getLogger(PtGPersistentChunkData.class);

	/**
	 * long representing the primary key for the persistence layer, in the
	 * status_Get table
	 */
	private long primaryKey = -1;

	/**
	 * This is the requestToken of the multifile srm request to which this chunk
	 * belongs
	 */
	private TRequestToken requestToken;

	/**
	 * @param requestToken
	 * @param fromSURL
	 * @param lifeTime
	 * @param dirOption
	 * @param desiredProtocols
	 * @param fileSize
	 * @param status
	 * @param transferURL
	 * @throws InvalidPtGDataAttributesException
	 */
	public PtGPersistentChunkData(GridUserInterface auth,
		TRequestToken requestToken, TSURL fromSURL, TLifeTimeInSeconds lifeTime,
		TDirOption dirOption, TURLPrefix desiredProtocols, TSizeInBytes fileSize,
		TReturnStatus status, TTURL transferURL)
		throws InvalidPtGDataAttributesException,
		InvalidPtGDataAttributesException,
		InvalidFileTransferDataAttributesException,
		InvalidSurlRequestDataAttributesException {

		super(auth, fromSURL, lifeTime, dirOption, desiredProtocols, fileSize,
			status, transferURL);
		if (requestToken == null) {
			log.debug("PtGPersistentChunkData: requestToken is null!");
			throw new InvalidPtGPersistentChunkDataAttributesException(requestToken,
				fromSURL, lifeTime, dirOption, desiredProtocols, fileSize, status,
				transferURL);
		}

		this.requestToken = requestToken;
	}

	/**
	 * Method used to get the primary key used in the persistence layer!
	 */
	@Override
	public long getPrimaryKey() {

		return primaryKey;
	}

	/**
	 * Method used to set the primary key to be used in the persistence layer!
	 */
	public void setPrimaryKey(long l) {

		primaryKey = l;
	}

	/**
	 * Method that returns the requestToken of the srm request to which this chunk
	 * belongs.
	 */
	@Override
	public TRequestToken getRequestToken() {

		return requestToken;
	}

	/**
	 * Method that sets the status of this request to SRM_FILE_PINNED; it needs
	 * the explanation String which describes the situation in greater detail; if
	 * a null is passed, then an empty String is used as explanation.
	 */
	public void changeStatusSRM_FILE_PINNED(String explanation) {

		setStatus(TStatusCode.SRM_FILE_PINNED, explanation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (primaryKey ^ (primaryKey >>> 32));
		result = prime * result
			+ ((requestToken == null) ? 0 : requestToken.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PtGPersistentChunkData other = (PtGPersistentChunkData) obj;
		if (primaryKey != other.primaryKey) {
			return false;
		}
		if (requestToken == null) {
			if (other.requestToken != null) {
				return false;
			}
		} else if (!requestToken.equals(other.requestToken)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PtGPersistentChunkData [primaryKey=");
		builder.append(primaryKey);
		builder.append(", requestToken=");
		builder.append(requestToken);
		builder.append(", pinLifeTime=");
		builder.append(pinLifeTime);
		builder.append(", dirOption=");
		builder.append(dirOption);
		builder.append(", fileSize=");
		builder.append(fileSize);
		builder.append(", transferProtocols=");
		builder.append(transferProtocols);
		builder.append(", SURL=");
		builder.append(SURL);
		builder.append(", status=");
		builder.append(status);
		builder.append(", transferURL=");
		builder.append(transferURL);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public long getIdentifier() {

		return getPrimaryKey();
	}
}
