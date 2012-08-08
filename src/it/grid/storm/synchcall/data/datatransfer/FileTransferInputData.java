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

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.AbstractInputData;

/**
 * @author Michele Dibenedetto
 *
 */
public class FileTransferInputData extends AbstractInputData
{

    protected final GridUserInterface user;
    protected final TSURL surl;
    protected final TURLPrefix transferProtocols;
    private TLifeTimeInSeconds desiredPinLifetime;
    private TSpaceToken targetSpaceToken;
    /**
     * Forbidden
     */
    @SuppressWarnings("unused")
    private FileTransferInputData() 
    {
        throw new IllegalAccessError("No arguments constructor is forbidden");
    }
    
    /**
     * @param user
     * @param surl
     * @param transferProtocols
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public FileTransferInputData(GridUserInterface user, TSURL surl, TURLPrefix transferProtocols) throws IllegalArgumentException, IllegalStateException
    {
        if(user == null || surl == null || transferProtocols == null)
        {
            throw new IllegalArgumentException("Unable to create PrepareToPutInputData. Received nul parameters: user = "
                                                       + user + " , surl = " + surl + " , transferProtocols = " + transferProtocols);
        }
        this.surl = surl;
        this.user = user;
        this.transferProtocols = transferProtocols;
        this.targetSpaceToken = TSpaceToken.makeEmpty();
    }
    
    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return user.getDn();
    }

    /**
     * @return the user
     */
    public GridUserInterface getUser()
    {
        return user;
    }

    /**
     * @return the surl
     */
    public TSURL getSurl()
    {
        return surl;
    }

    /**
     * @return the transferProtocols
     */
    public TURLPrefix getTransferProtocols()
    {
        return transferProtocols;
    }
    
    /**
     * @param desiredPinLifetime
     */
    public void setDesiredPinLifetime(TLifeTimeInSeconds desiredPinLifetime)
    {
        this.desiredPinLifetime = desiredPinLifetime;
    }
    
    public TLifeTimeInSeconds getDesiredPinLifetime()
    {
        return desiredPinLifetime;
    }

    public void setTargetSpaceToken(TSpaceToken targetSpaceToken)
    {
        this.targetSpaceToken = targetSpaceToken;
    }

    public TSpaceToken getTargetSpaceToken()
    {
        return targetSpaceToken;
    }
}
