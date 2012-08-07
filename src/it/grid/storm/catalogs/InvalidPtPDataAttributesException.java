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


import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.srm.types.TFileStorageType;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TOverwriteMode;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.srm.types.TTURL;

/**
 * @author Michele Dibenedetto
 */
public class InvalidPtPDataAttributesException extends InvalidFileTransferDataAttributesException
{

    /**
     * 
     */
    private static final long serialVersionUID = 1051060981188652979L;
    protected boolean nullSpaceToken;
    protected boolean nullPinLifetime;
    protected boolean nullFileLifetime;
    protected boolean nullFileStorageType;
    protected boolean nullKnownSizeOfThisFile;
    protected boolean nullOverwriteOption;

    public InvalidPtPDataAttributesException(TSURL toSURL, TLifeTimeInSeconds fileLifetime,
            TLifeTimeInSeconds pinLifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken,
            TSizeInBytes knownSizeOfThisFile, TURLPrefix transferProtocols, TOverwriteMode overwriteOption,
            TReturnStatus status, TTURL transferURL)
    {
        super(toSURL, transferProtocols, status, transferURL);
        init(spaceToken, fileLifetime, pinLifetime, fileStorageType, knownSizeOfThisFile, overwriteOption);
    }

    public InvalidPtPDataAttributesException(TSURL toSURL, TLifeTimeInSeconds fileLifetime,
            TLifeTimeInSeconds pinLifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken,
            TSizeInBytes knownSizeOfThisFile, TURLPrefix transferProtocols, TOverwriteMode overwriteOption,
            TReturnStatus status, TTURL transferURL, String message)
    {
        super(toSURL, transferProtocols, status, transferURL, message);
        init(spaceToken, fileLifetime, pinLifetime, fileStorageType, knownSizeOfThisFile, overwriteOption);
    }

    public InvalidPtPDataAttributesException(TSURL toSURL, TLifeTimeInSeconds fileLifetime,
            TLifeTimeInSeconds pinLifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken,
            TSizeInBytes knownSizeOfThisFile, TURLPrefix transferProtocols, TOverwriteMode overwriteOption,
            TReturnStatus status, TTURL transferURL, Throwable cause)
    {
        super(toSURL, transferProtocols, status, transferURL, cause);
        init(spaceToken, fileLifetime, pinLifetime, fileStorageType, knownSizeOfThisFile, overwriteOption);
    }

    public InvalidPtPDataAttributesException(TSURL toSURL, TLifeTimeInSeconds fileLifetime,
            TLifeTimeInSeconds pinLifetime, TFileStorageType fileStorageType, TSpaceToken spaceToken,
            TSizeInBytes knownSizeOfThisFile, TURLPrefix transferProtocols, TOverwriteMode overwriteOption,
            TReturnStatus status, TTURL transferURL, String message, Throwable cause)
    {
        super(toSURL, transferProtocols, status, transferURL, message, cause);
        init(spaceToken, fileLifetime, pinLifetime, fileStorageType, knownSizeOfThisFile, overwriteOption);
    }

    private void init(TSpaceToken spaceToken,TLifeTimeInSeconds fileLifetime, TLifeTimeInSeconds pinLifetime,
            TFileStorageType fileStorageType, TSizeInBytes knownSizeOfThisFile, TOverwriteMode overwriteOption)
    {
        nullSpaceToken = spaceToken == null;
        nullPinLifetime = pinLifetime == null;
        nullFileLifetime = fileLifetime == null;
        nullFileStorageType = fileStorageType == null;
        nullKnownSizeOfThisFile = knownSizeOfThisFile == null;
        nullOverwriteOption = overwriteOption == null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("InvalidPtPDataAttributesException [nullSpaceToken=");
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
