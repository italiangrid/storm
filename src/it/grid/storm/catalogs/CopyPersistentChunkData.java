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

import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CopyChunkData, that is part of a multifile Copy srm
 * request. It contains data about: the requestToken, the fromSURL, the toSURL,
 * the target fileLifeTime, the target fileStorageType and any available target
 * spaceToken, the target overwriteOption to be applied in case the file already
 * exists, the fileSize of the existing file if any, return status of the file
 * together with its error string.
 *
 * @author  EGRID - ICTP Trieste
 * @date    September, 2005
 * @version 2.0
 */
public class CopyPersistentChunkData  extends CopyData implements PersistentChunkData {
    private static final Logger log = LoggerFactory.getLogger(CopyPersistentChunkData.class);

    /**
     * long representing the primary key for the persistence layer! 
     */
    private long primaryKey = -1;
    
    /**
     * This is the requestToken of the multifile srm request to which this chunk belongs
     */
    private TRequestToken requestToken;

    public CopyPersistentChunkData(TRequestToken requestToken, TSURL fromSURL, TSURL destinationSURL,
            TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken,
            TOverwriteMode overwriteOption, TReturnStatus status)
        throws InvalidCopyPersistentChunkDataAttributesException, InvalidCopyDataAttributesException,
        InvalidSurlRequestDataAttributesException
    {

        super(fromSURL, destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption, status);
        if (requestToken == null)
        {
            throw new InvalidCopyPersistentChunkDataAttributesException(requestToken, fromSURL,
                                                                        destinationSURL, lifetime,
                                                                        fileStorageType, spaceToken,
                                                                        overwriteOption, status);
        }
        this.requestToken = requestToken;
    }

    /**
     * Method used to get the primary key used in the persistence layer!
     */
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
     * Method that returns the requestToken of the srm request to which this chunk belongs.
     */
    public TRequestToken getRequestToken()
    {
        return requestToken;
    }

    @Override
    public long getIdentifier()
    {
        return getPrimaryKey();
    }
    
    @Override
    protected Logger getLog()
    {
        return CopyPersistentChunkData.log;
    }
}
