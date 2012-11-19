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
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * @author Michele Dibenedetto
 *
 */
public class IdentityFileTransferInputData extends AnonymousFileTransferInputData implements IdentityInputData
{

    protected final GridUserInterface user;
    
    /**
     * @param user
     * @param surl
     * @param transferProtocols
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public IdentityFileTransferInputData(GridUserInterface user, TSURL surl, TURLPrefix transferProtocols) throws IllegalArgumentException, IllegalStateException
    {
        super(surl, transferProtocols);
        if (user == null)
        {
            throw new IllegalArgumentException(
                                               "Unable to create the object. Received nul parameters: user = "
                                                       + user);
        }
        this.user = user;
    }
    
    @Override
    public GridUserInterface getUser()
    {
        return user;
    }
    
    @Override
    public String getPrincipal()
    {
        return user.getDn();
    }

}
