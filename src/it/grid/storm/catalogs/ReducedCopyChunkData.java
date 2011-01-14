/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

/**
 * This class represents a ReducedCopyChunkData, that is part of a multifile Copy srm
 * request. It contains data about: the requestToken, the fromSURL, the toSURL,
 * return status of the file together with its error string.
 *
 * @author  Michele Dibenedetto
 */
public class ReducedCopyChunkData implements ChunkData {

    /* long representing the primary key for the persistence layer! */
    private long primaryKey = -1;
    /* SURL from which the srmCopy will get the file */
    private TSURL fromSURL;              
    /* SURL to which the srmCopy will put the file */
    private TSURL toSURL;
    /* Return status for this chunk of request */
    private TReturnStatus status;

	public ReducedCopyChunkData(TSURL fromSURL, TSURL toSURL,
			TReturnStatus status) throws InvalidReducedCopyChunkDataAttributesException {

		if(fromSURL == null || toSURL == null || status == null)
		{
			throw new InvalidReducedCopyChunkDataAttributesException(fromSURL,
				toSURL, status);
		}

		this.fromSURL = fromSURL;
		this.toSURL = toSURL;
		this.status = status;
	}

	/**
	 * Method used to get the primary key used in the persistence layer!
	 */
	public long primaryKey() {

		return primaryKey;
	}

	/**
	 * Method used to set the primary key to be used in the persistence layer!
	 */
	public void setPrimaryKey(long l) {

		primaryKey = l;
	}

	/**
	 * Method that returns the fromSURL of the srm request to which this chunk
	 * belongs.
	 */
	public TSURL fromSURL() {

		return fromSURL;
	}

	/**
	 * Method that returns the toSURL of the srm request to which this chunk
	 * belongs.
	 */
	public TSURL toSURL() {

		return toSURL;
	}

	/**
	 * Method that returns the status for this chunk of the srm request.
	 */
	public TReturnStatus status() {

		return status;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("CopyChunkData\n");
		sb.append("primaryKey=");
		sb.append(primaryKey);
		sb.append("; ");
		sb.append("RequestToken=");
		sb.append("fromSURL=");
		sb.append(fromSURL);
		sb.append("; ");
		sb.append("toSURL=");
		sb.append(toSURL);
		sb.append("; ");
		sb.append("status=");
		sb.append(status);
		sb.append("; ");
		return sb.toString();
	}

	@Override
	public int hashCode() {

		int hash = 17;
		hash = 37 * hash + new Long(primaryKey).hashCode();
		hash = 37 * hash + fromSURL.hashCode();
		hash = 37 * hash + toSURL.hashCode();
		hash = 37 * hash + status.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object o) {

		if(o == this)
		{
			return true;
		}
		if(!(o instanceof ReducedCopyChunkData))
		{
			return false;
		}
		ReducedCopyChunkData cd = (ReducedCopyChunkData) o;
		return (primaryKey == cd.primaryKey) && fromSURL.equals(cd.fromSURL)
			&& toSURL.equals(cd.toSURL) && status.equals(cd.status);
	}
}
