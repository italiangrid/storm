
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
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
public class ExtendFileLifeTimeInputData implements InputData
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
