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

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStorageSystemInfo;
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
 * constructor of PtPChunkData are invalid, that is if any of the following is _null_:
 * requestToken, toSURL, lifetime, fileStorageType, spaceToken, knownSizeOfThisFile,
 * TURLPrefix transferProtocols, overwriteOption, fileSize, status, transferURL.
 *
 * @author  EGRID - ICTP Trieste
 * @date    June, 2005
 * @version 2.0
 */
public class InvalidPtPChunkDataAttributesException extends Exception {

    //booleans that indicate whether the corresponding variable is null
    private boolean nullRequestToken;
    private boolean nullToSURL;
    private boolean nullPinLifetime;
    private boolean nullFileLifetime;
    private boolean nullFileStorageType;
    private boolean nullSpaceToken;
    private boolean nullKnownSizeOfThisFile;
    private boolean nullTransferProtocols;
    private boolean nullOverwriteOption;
    private boolean nullStatus;
    private boolean nullTransferURL;

    /**
     * Constructor that requires the attributes that caused the exception
     * to be thrown.
     */
    public InvalidPtPChunkDataAttributesException(TRequestToken requestToken, TSURL toSURL, TLifeTimeInSeconds fileLifetime, TLifeTimeInSeconds pinLifetime, TFileStorageType fileStorageType,
        TSpaceToken spaceToken, TSizeInBytes knownSizeOfThisFile, TURLPrefix transferProtocols, TOverwriteMode overwriteOption, TReturnStatus status,
        TTURL transferURL) {

        nullRequestToken = requestToken==null;
        nullToSURL = toSURL==null;
        nullPinLifetime = pinLifetime==null;
        nullFileLifetime = fileLifetime==null;
        nullFileStorageType = fileStorageType==null;
        nullSpaceToken = spaceToken==null;
        nullKnownSizeOfThisFile = knownSizeOfThisFile==null;
        nullTransferProtocols = transferProtocols==null;
        nullOverwriteOption = overwriteOption==null;
        nullStatus = status==null;
        nullTransferURL = transferURL==null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid PtPChunkData attributes: null-requestToken="); sb.append(nullRequestToken);
        sb.append("; nul-toSURL="); sb.append(nullToSURL);
        sb.append("; null-pinLifetime="); sb.append(nullPinLifetime);
        sb.append("; null-fileLifetime="); sb.append(nullFileLifetime);
        sb.append("; null-filestorageType="); sb.append(nullFileStorageType);
        sb.append("; null-spaceToken="); sb.append(nullSpaceToken);
        sb.append("; null-knownSizeOfThisFile="); sb.append(nullKnownSizeOfThisFile);
        sb.append("; null-transferProtocols="); sb.append(nullTransferProtocols);
        sb.append("; null-overwriteOption="); sb.append(nullOverwriteOption);
        sb.append("; null-status="); sb.append(nullStatus);
        sb.append("; null-transferURL="); sb.append(nullTransferURL);
        sb.append(".");
        return sb.toString();
    }
}
