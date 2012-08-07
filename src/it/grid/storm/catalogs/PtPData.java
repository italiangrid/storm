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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.srm.types.TTURL;


/**
 * @author Michele Dibenedetto
 *
 */
public class PtPData extends FileTransferData
{
    static final Logger log = LoggerFactory.getLogger(PtPData.class);
    protected TSpaceToken spaceToken;
    protected TLifeTimeInSeconds pinLifetime;
    protected TLifeTimeInSeconds fileLifetime;
    protected TFileStorageType fileStorageType;
    protected TOverwriteMode overwriteOption;
    protected TSizeInBytes expectedFileSize;

    public PtPData(TSURL toSURL, TLifeTimeInSeconds pinLifetime, TLifeTimeInSeconds fileLifetime,
            TFileStorageType fileStorageType, TSpaceToken spaceToken, TSizeInBytes expectedFileSize,
            TURLPrefix transferProtocols, TOverwriteMode overwriteOption, TReturnStatus status,
            TTURL transferURL) throws InvalidPtPDataAttributesException,
            InvalidFileTransferDataAttributesException, InvalidSurlRequestDataAttributesException
    {
        super(toSURL, transferProtocols, status, transferURL);
        if (pinLifetime == null || fileLifetime == null || spaceToken == null || fileStorageType == null
                || expectedFileSize == null || overwriteOption == null)
        {
            throw new InvalidPtPDataAttributesException(toSURL, pinLifetime, fileLifetime,
                                                             fileStorageType, spaceToken, expectedFileSize,
                                                             transferProtocols, overwriteOption, status,
                                                             transferURL);
        }
        this.spaceToken = spaceToken;
        this.pinLifetime = pinLifetime;
        this.fileLifetime = fileLifetime;
        this.fileStorageType = fileStorageType;
        this.expectedFileSize = expectedFileSize;
        this.overwriteOption = overwriteOption;
    }

    @Override
    protected Logger getLog()
    {
        return PtPData.log;
    }
    
    /**
     * Method that returns the space token supplied for this chunk of the srm request.
     */
    public final TSpaceToken getSpaceToken()
    {
        return spaceToken;
    }
    
    /**
     * Method that returns the requested pin life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds pinLifetime()
    {
        return pinLifetime;
    }

    /**
     * Method that returns the requested file life time for this chunk of the srm request.
     */
    public TLifeTimeInSeconds fileLifetime()
    {
        return fileLifetime;
    }

    /**
     * Method that returns the fileStorageType for this chunk of the srm request.
     */
    public TFileStorageType fileStorageType()
    {
        return fileStorageType;
    }

    /**
     * Method that returns the knownSizeOfThisFile supplied with this chunk of the srm request.
     */
    public TSizeInBytes expectedFileSize()
    {
        return expectedFileSize;
    }

    /**
     * Method that returns the overwriteOption specified in the srm request.
     */
    public TOverwriteMode overwriteOption()
    {
        return overwriteOption;
    }

    /**
     * Method that sets the status of this request to SRM_SPACE_AVAILABLE;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_SPACE_AVAILABLE(String explanation)
    {
        setStatus(TStatusCode.SRM_SPACE_AVAILABLE,explanation);
    }

    /**
     * Method that sets the status of this request to SRM_DUPLICATION_ERROR;
     * it needs the explanation String which describes the situation in greater detail;
     * if a null is passed, then an empty String is used as explanation.
     */
    public void changeStatusSRM_DUPLICATION_ERROR(String explanation)
    {
        setStatus(TStatusCode.SRM_DUPLICATION_ERROR,explanation);
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("PtPChunkData\n");
        sb.append("toSURL="); sb.append(SURL); sb.append("; ");
        sb.append("pinLifetime="); sb.append(pinLifetime); sb.append("; ");
        sb.append("fileLifetime="); sb.append(fileLifetime); sb.append("; ");
        sb.append("fileStorageType="); sb.append(fileStorageType); sb.append("; ");
        sb.append("spaceToken="); sb.append(spaceToken); sb.append("; ");
        sb.append("expectedFileSize="); sb.append(expectedFileSize); sb.append("; ");
        sb.append("transferProtocols="); sb.append(transferProtocols); sb.append("; ");
        sb.append("overwriteOption="); sb.append(overwriteOption); sb.append("; ");
        sb.append("status="); sb.append(status); sb.append("; ");
        sb.append("transferURL="); sb.append(transferURL); sb.append("; ");
        return sb.toString();
    }

    @Override
    public int hashCode()
    {
        int hash = 17;
        hash = 37*hash + SURL.hashCode();
        hash = 37*hash + pinLifetime.hashCode();
        hash = 37*hash + fileLifetime.hashCode();
        hash = 37*hash + fileStorageType.hashCode();
        hash = 37*hash + spaceToken.hashCode();
        hash = 37*hash + expectedFileSize.hashCode();
        hash = 37*hash + transferProtocols.hashCode();
        hash = 37*hash + overwriteOption.hashCode();
        hash = 37*hash + status.hashCode();
        hash = 37*hash + transferURL.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o==this) {
            return true;
        }
        if (!(o instanceof PtPData)) {
            return false;
        }
        PtPData cd = (PtPData) o;
        return SURL.equals(cd.SURL) &&
        pinLifetime.equals(cd.pinLifetime) &&
        fileLifetime.equals(cd.fileLifetime) &&
        fileStorageType.equals(cd.fileStorageType) &&
        spaceToken.equals(cd.spaceToken) &&
        expectedFileSize.equals(cd.expectedFileSize) &&
        transferProtocols.equals(cd.transferProtocols) &&
        overwriteOption.equals(cd.overwriteOption) &&
        status.equals(cd.status) &&
        transferURL.equals(cd.transferURL);
    }
}
