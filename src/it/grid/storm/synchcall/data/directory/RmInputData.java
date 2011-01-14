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
 * This class represents the Rm Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.directory;

import java.util.Vector;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.exception.InvalidRmInputAttributeException;

public class RmInputData extends AbstractInputData
{
    private GridUserInterface auth = null;
    ArrayOfSURLs surlarray = null;
    ArrayOfTExtraInfo infoarray = null;

    public RmInputData()
    {
    }

    public RmInputData(GridUserInterface auth, ArrayOfSURLs surlArray, ArrayOfTExtraInfo infoArray) throws InvalidRmInputAttributeException
    {
        boolean ok = (!(surlArray == null));
        if (!ok) throw new InvalidRmInputAttributeException(surlArray);
        
        this.auth = auth;
        this.surlarray = surlArray;
        this.infoarray = infoArray;
    }

    /**
     * Method that SURL specified in SRM request.
     */

    public ArrayOfSURLs getSurlArray()
    {
        return surlarray;
    }

    public void setSurlInfo(ArrayOfSURLs surlArray)
    {
        this.surlarray = surlArray;
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

}
