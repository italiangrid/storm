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

/**
 * This class represents the Rmdir Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.exception.InvalidRmdirInputAttributeException;

public class RmdirInputData extends AbstractInputData {
    
    private GridUserInterface auth = null;
    private TSURL surl = null;
    private ArrayOfTExtraInfo storageSystemInfo = null;
    private Boolean recursive;

    public RmdirInputData()
    {
    }

    public RmdirInputData(GridUserInterface auth, TSURL surl, ArrayOfTExtraInfo storageSystemInfo, Boolean recursive)
                    throws InvalidRmdirInputAttributeException
    {
        boolean ok = (surl != null);
        if (!ok) throw new InvalidRmdirInputAttributeException(surl);

        this.auth = auth;
        this.surl = surl;
        this.storageSystemInfo = storageSystemInfo;
        this.recursive = recursive;
    }

    /**
     * Method that SURL specified in SRM request.
     */

    public TSURL getSurl()
    {
        return surl;
    }

    public void setSurl(TSURL surl)
    {
        this.surl = surl;
    }

    /**
     * Set User
     */
    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    /**
     * get User
     */
    public GridUserInterface getUser()
    {
        return this.auth;
    }

    /**
     * Get RecursiveFlag
     */
    public Boolean getRecursiveFlag()
    {
        return recursive;
    }

    /**
     * Set RecursiveFlag
     */
    public void setRecursiveFlag(Boolean flag)
    {
        this.recursive = flag;
    }

    /**
     * Get RecursiveFlag
     */
    public ArrayOfTExtraInfo getStorageSystemInfo()
    {
        return storageSystemInfo;
    }

    /**
     * Set RecursiveFlag
     */
    public void setStorageSystemInfo(ArrayOfTExtraInfo storageSystemInfo)
    {
        this.storageSystemInfo = storageSystemInfo;
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
