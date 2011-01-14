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
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.InputData;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 * This class represents the ExtendFileLifeTime Input Data.
 *
 * Authors:
 *     @author = lucamag luca.magnoniATcnaf.infn.it
 *     @author  Alberto Forti
 *
 * @date = Oct 10, 2008
 *
 */
public class ExtendFileLifeTimeInputData extends AbstractInputData
{
    private GridUserInterface  auth            = null;
    private TRequestToken      reqToken        = null;
    private ArrayOfSURLs       arrayOfSURLs    = null;
    private TLifeTimeInSeconds newFileLifetime = null;
    private TLifeTimeInSeconds newPinLifetime  = null;

    public ExtendFileLifeTimeInputData() {}

    public ExtendFileLifeTimeInputData(GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray,
            TLifeTimeInSeconds newFileLifetime, TLifeTimeInSeconds newPinLifetime) {
        this.auth = auth;
        this.reqToken = reqToken;
        this.arrayOfSURLs = surlArray;
        this.newFileLifetime = newFileLifetime;
        this.newPinLifetime = newPinLifetime;
    }

    public TRequestToken getRequestToken()
    {
        return reqToken;
    }

    public void setRequestToken(TRequestToken reqToken)
    {
        this.reqToken = reqToken;
    }

    public GridUserInterface getUser()
    {
        return this.auth;
    }

    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    public void setArrayOfSURLs(ArrayOfSURLs arrayOfSURLs)
    {
        this.arrayOfSURLs = arrayOfSURLs;
    }
    
    public TLifeTimeInSeconds getNewFileLifetime()
    {
        return newFileLifetime;
    }
    
    public void setNewFileLifetime(TLifeTimeInSeconds newFileLifetime)
    {
        this.newFileLifetime = newFileLifetime;
    }
    
    public TLifeTimeInSeconds getNewPinLifetime()
    {
        return newPinLifetime;
    }
    
    public void setNewPinLifetime(TLifeTimeInSeconds newPinLifetime)
    {
        this.newPinLifetime = newPinLifetime;
    }
}
