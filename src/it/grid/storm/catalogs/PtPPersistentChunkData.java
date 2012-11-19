/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.grid.storm.catalogs;


import org.slf4j.Logger;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

/**
 * This class represents a PrepareToPutChunkData, that is part of a multifile
 * PrepareToPut srm request. It contains data about: the requestToken, the
 * toSURL, the requested lifeTime of pinning, the requested lifetime of
 * volatile, the requested fileStorageType and any available spaceToken, the
 * expectedFileSize, the desired transferProtocols in order of preference,
 * the overwriteOption to be applied in case the file already exists, the
 * transferURL for the supplied SURL.
 * 
 * @author EGRID - ICTP Trieste
 * @date June, 2005
 * @version 2.0
 */
public class PtPPersistentChunkData extends IdentityPtPData implements PersistentChunkData
{

    /**
     * long representing the primary key for the persistence layer, in the status_Put table
     */
    private long primaryKey = -1; 
   
    /**
     * This is the requestToken of the multifile srm request to
     * which this chunk belongs
     */
    private final TRequestToken requestToken; 

    public PtPPersistentChunkData(GridUserInterface auth, TRequestToken requestToken, TSURL toSURL, TLifeTimeInSeconds pinLifetime,
            TLifeTimeInSeconds fileLifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken,
            TSizeInBytes expectedFileSize, TURLPrefix transferProtocols, TOverwriteMode overwriteOption,
            TReturnStatus status, TTURL transferURL) throws InvalidPtPPersistentChunkDataAttributesException,
        InvalidPtPDataAttributesException, InvalidFileTransferDataAttributesException, InvalidSurlRequestDataAttributesException
    {
        super(auth, toSURL, pinLifetime, fileLifetime, fileStorageType, spaceToken, expectedFileSize,
              transferProtocols, overwriteOption, status, transferURL);
        if (requestToken == null)
        {
            throw new InvalidPtPPersistentChunkDataAttributesException(requestToken, toSURL, pinLifetime,
                                                                       fileLifetime, fileStorageType,
                                                                       spaceToken, expectedFileSize,
                                                                       transferProtocols, overwriteOption,
                                                                       status, transferURL);
        }
        this.requestToken = requestToken;
    }

    /**
     * Method used to get the primary key used in the persistence layer!
     */
    @Override
    public long getPrimaryKey()
    {
        return primaryKey;
    }

    /**
     * Method used to set the primary key to be used in the persistence layer!
     */
    public void setPrimaryKey(long l)
    {
        primaryKey = l;
    }

    /**
     * Method that returns the requestToken of the srm request to which this chunk belongs.
     */
    @Override
    public TRequestToken getRequestToken()
    {
        return requestToken;
    }
    
    @Override
    protected Logger getLog()
    {
        return PtPPersistentChunkData.log; 
    }
    
    @Override
    public long getIdentifier()
    {
        return getPrimaryKey();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (primaryKey ^ (primaryKey >>> 32));
        result = prime * result + ((requestToken == null) ? 0 : requestToken.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!super.equals(obj))
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        PtPPersistentChunkData other = (PtPPersistentChunkData) obj;
        if (primaryKey != other.primaryKey)
        {
            return false;
        }
        if (requestToken == null)
        {
            if (other.requestToken != null)
            {
                return false;
            }
        }
        else
            if (!requestToken.equals(other.requestToken))
            {
                return false;
            }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PtPPersistentChunkData [primaryKey=");
        builder.append(primaryKey);
        builder.append(", requestToken=");
        builder.append(requestToken);
        builder.append(", spaceToken=");
        builder.append(spaceToken);
        builder.append(", pinLifetime=");
        builder.append(pinLifetime);
        builder.append(", fileLifetime=");
        builder.append(fileLifetime);
        builder.append(", fileStorageType=");
        builder.append(fileStorageType);
        builder.append(", overwriteOption=");
        builder.append(overwriteOption);
        builder.append(", expectedFileSize=");
        builder.append(expectedFileSize);
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
}
