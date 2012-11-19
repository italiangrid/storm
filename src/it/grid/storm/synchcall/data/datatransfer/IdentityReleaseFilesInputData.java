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
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityReleaseFilesInputData extends AnonymousReleaseFilesInputData implements
        IdentityInputData
{
    private final GridUserInterface auth;

    public IdentityReleaseFilesInputData(GridUserInterface auth, TRequestToken requestToken,
            ArrayOfSURLs arrayOfSURLs) throws IllegalArgumentException
    {
        super(requestToken, arrayOfSURLs);
        if (auth == null)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: auth=" + auth);
        }
        this.auth = auth;
    }

    @Override
    public GridUserInterface getUser()
    {
        return this.auth;
    }

    @Override
    public String getPrincipal()
    {
        return this.auth.getDn();
    }

}
