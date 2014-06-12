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
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a BringOnLineChunkData, that is part of a multifile
 * BringOnLine srm request. It contains data about: the requestToken, the
 * fromSURL, the requested lifeTime of pinning, the TDirOption which tells
 * whether the requested SURL is a directory and if it must be recursed at all
 * levels, as well as the desired number of levels to recurse, the desired
 * transferProtocols in order of preference, the fileSize, and the transferURL
 * for the supplied SURL.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLPersistentChunkData extends BoLData implements
	PersistentChunkData {

	private static final Logger log = LoggerFactory
		.getLogger(BoLPersistentChunkData.class);

	/**
	 * long representing the primary key for the persistence layer, in the
	 * status_Put table
	 */
	private long primaryKey = -1;

	/**
	 * This is the requestToken of the multifile srm request to which this chunk
	 * belongs
	 */
	private final TRequestToken requestToken;

	public BoLPersistentChunkData(TRequestToken requestToken, TSURL fromSURL,
		TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix desiredProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL, int deferredStartTime)
		throws InvalidBoLPersistentChunkDataAttributesException,
		InvalidFileTransferDataAttributesException,
		InvalidBoLDataAttributesException,
		InvalidSurlRequestDataAttributesException {

		super(fromSURL, lifeTime, dirOption, desiredProtocols, fileSize, status,
			transferURL, deferredStartTime);
		if (requestToken == null) {
			log.debug("BoLPersistentChunkData: requestToken is null!");
			throw new InvalidBoLPersistentChunkDataAttributesException(requestToken,
				fromSURL, lifeTime, dirOption, desiredProtocols, fileSize, status,
				transferURL);
		}
		this.requestToken = requestToken;
	}

	/**
	 * Method that returns the requestToken of the srm request to which this chunk
	 * belongs.
	 */
	public TRequestToken getRequestToken() {

		return requestToken;
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

	@Override
	public long getIdentifier() {

		return getPrimaryKey();
	}
}
