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

import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;

/**
 * @author Michele Dibenedetto
 *
 */
public class InvalidCopyPersistentChunkDataAttributesException extends InvalidCopyDataAttributesException
{
    /**
     * 
     */
    private static final long serialVersionUID = 1266996505954208061L;
    private boolean nullRequestToken;
    
    public InvalidCopyPersistentChunkDataAttributesException(TRequestToken requestToken, TSURL SURL, TSURL destinationSURL,
            TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
            TSpaceToken spaceToken, TOverwriteMode overwriteOption, TReturnStatus status)
    {
        super(SURL, destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption, status);
        init(requestToken);
    }
    
    public InvalidCopyPersistentChunkDataAttributesException(TRequestToken requestToken, TSURL SURL, TSURL destinationSURL,
            TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
            TSpaceToken spaceToken, TOverwriteMode overwriteOption, TReturnStatus status, String message)
    {
        super(SURL, destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption, status, message);
        init(requestToken);
    }
    
    public InvalidCopyPersistentChunkDataAttributesException(TRequestToken requestToken, TSURL SURL, TSURL destinationSURL,
            TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
            TSpaceToken spaceToken, TOverwriteMode overwriteOption, TReturnStatus status, Throwable cause)
    {
        super(SURL, destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption, status, cause);
        init(requestToken);
    }
    
    public InvalidCopyPersistentChunkDataAttributesException(TRequestToken requestToken, TSURL SURL, TSURL destinationSURL,
            TLifeTimeInSeconds lifetime, TFileStorageType fileStorageType,
            TSpaceToken spaceToken, TOverwriteMode overwriteOption, TReturnStatus status, String message, Throwable cause)
    {
        super(SURL, destinationSURL, lifetime, fileStorageType, spaceToken, overwriteOption, status, message, cause);
        init(requestToken);
    }
    
    private void init(TRequestToken requestToken)
    {
        nullRequestToken = requestToken == null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("InvalidCopyPersistentChunkDataAttributesException [nullRequestToken=");
        builder.append(nullRequestToken);
        builder.append(", nullDestinationSURL=");
        builder.append(nullDestinationSURL);
        builder.append(", nullLifetime=");
        builder.append(nullLifetime);
        builder.append(", nullFileStorageType=");
        builder.append(nullFileStorageType);
        builder.append(", nullSpaceToken=");
        builder.append(nullSpaceToken);
        builder.append(", nullOverwriteOption=");
        builder.append(nullOverwriteOption);
        builder.append(", nullSURL=");
        builder.append(nullSURL);
        builder.append(", nullStatus=");
        builder.append(nullStatus);
        builder.append("]");
        return builder.toString();
    }
}
