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
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TTURL;


/**
 * @author Michele Dibenedetto
 *
 */

public abstract class FileTransferData extends SurlRequestData
{

    protected TURLPrefix transferProtocols;
    protected TTURL transferURL;

    public FileTransferData(TSURL toSURL, TURLPrefix transferProtocols,
            TReturnStatus status, TTURL transferURL) throws InvalidFileTransferDataAttributesException, InvalidSurlRequestDataAttributesException
    {
        super(toSURL, status);
        if (transferProtocols == null || transferURL == null)
        {
            throw new InvalidFileTransferDataAttributesException(toSURL, transferProtocols,
                                                                      status, transferURL);
        }
        this.transferProtocols = transferProtocols;
        this.transferURL = transferURL;
    }
    
    /**
     * Method that returns a TURLPrefix containing the transfer protocols desired
     * for this chunk of the srm request.
     */
    public final TURLPrefix getTransferProtocols()
    {
        return transferProtocols;
    }

    /**
     * Method that returns the TURL for this chunk of the srm request.
     */
    public final TTURL getTransferURL()
    {
        return transferURL;
    }

    /**
     * Method used to set the transferURL associated to the SURL of this chunk.
     * If TTURL is null, then nothing gets set!
     */
    public final void setTransferURL(final TTURL turl)
    {
        if (turl != null)
        {
            transferURL = turl;
        }
    }
}
