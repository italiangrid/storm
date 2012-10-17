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

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.exception.InvalidReleaseFilesInputAttributeException;

public class ReleaseFilesInputData extends AbstractInputData
{
    private GridUserInterface auth = null;
    private TRequestToken requestToken = null;
    private ArrayOfSURLs arrayOfSURLs = null;

    public ReleaseFilesInputData()
    {
    }

    public ReleaseFilesInputData(GridUserInterface auth, TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs) throws InvalidReleaseFilesInputAttributeException
    {
        boolean ok = !((arrayOfSURLs == null) && (requestToken == null));
        if (!ok)
            throw new InvalidReleaseFilesInputAttributeException(arrayOfSURLs);

        this.auth = auth;
        this.requestToken = requestToken;
        this.arrayOfSURLs = arrayOfSURLs;
    }

    public GridUserInterface getUser()
    {
        return this.auth;
    }

    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }
    
    public TRequestToken getRequestToken()
    {
        return requestToken;
    }

    public void setRequestToken(TRequestToken requestToken)
    {
        this.requestToken = requestToken;
    }
    
    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    public void setArrayOfSURLs(ArrayOfSURLs arrayOfSURLs)
    {
        this.arrayOfSURLs = arrayOfSURLs;
    }
    
    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return this.auth.getDn();
    }
    
}
