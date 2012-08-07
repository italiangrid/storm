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

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
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
 * @author  EGRID - ICTP Trieste
 * @date    March 21st, 2005
 * @version 3.0
 */
public class PtGData extends FileTransferData {
    private static final Logger log = LoggerFactory.getLogger(PtGData.class);

    /** requested lifetime of TURL: it is the pin time! */
    protected TLifeTimeInSeconds pinLifeTime;
    /** specifies if the request regards a directory and related info */
    protected TDirOption dirOption;
    /** size of file */
    protected TSizeInBytes fileSize;

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
    public PtGData(TSURL SURL, TLifeTimeInSeconds lifeTime, TDirOption dirOption,
            TURLPrefix desiredProtocols, TSizeInBytes fileSize, TReturnStatus status, TTURL transferURL)
        throws InvalidPtGDataAttributesException, InvalidFileTransferDataAttributesException, InvalidSurlRequestDataAttributesException
    {
        super(SURL, desiredProtocols, status, transferURL);
        if (lifeTime == null || dirOption == null || fileSize == null)
        {
            throw new InvalidPtGDataAttributesException(SURL, lifeTime, dirOption, desiredProtocols,
                                                             fileSize, status, transferURL);

        }
        this.pinLifeTime = lifeTime;
        this.dirOption = dirOption;
        this.fileSize = fileSize;
    }

    /**
     * Method that returns the requested pin life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds getPinLifeTime() {
        return pinLifeTime;
    }

    /**
     * Method that returns the dirOption specified in the srm request.
     */
    public TDirOption getDirOption() {
        return dirOption;
    }

    /**
     * Method that returns the file size for this chunk of the srm request.
     */
    public TSizeInBytes getFileSize() {
        return fileSize;
    }

    /**
     * Method used to set the size of the file corresponding to the
     * requested SURL. If the supplied TSizeInByte is null, then nothing
     * gets set!
     */
    public void setFileSize(TSizeInBytes size) {
        if (size!=null) {
            fileSize = size;
        }
    }

    /**
     * Method that sets the status of this request to SRM_FILE_PINNED;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_FILE_PINNED(String explanation) {
        setStatus(TStatusCode.SRM_FILE_PINNED,explanation);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PtGChunkData [pinLifeTime=");
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



    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((dirOption == null) ? 0 : dirOption.hashCode());
        result = prime * result + ((fileSize == null) ? 0 : fileSize.hashCode());
        result = prime * result + ((pinLifeTime == null) ? 0 : pinLifeTime.hashCode());
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
        PtGData other = (PtGData) obj;
        if (dirOption == null)
        {
            if (other.dirOption != null)
            {
                return false;
            }
        }
        else
            if (!dirOption.equals(other.dirOption))
            {
                return false;
            }
        if (fileSize == null)
        {
            if (other.fileSize != null)
            {
                return false;
            }
        }
        else
            if (!fileSize.equals(other.fileSize))
            {
                return false;
            }
        if (pinLifeTime == null)
        {
            if (other.pinLifeTime != null)
            {
                return false;
            }
        }
        else
            if (!pinLifeTime.equals(other.pinLifeTime))
            {
                return false;
            }
        return true;
    }

    @Override
    protected Logger getLog()
    {
        return PtGData.log; 
    }
}
