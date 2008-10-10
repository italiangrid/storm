/**
 * This class represents the PutDone Input Data associated with the SRM request PutDone
 * @author  Alberto Forti
 * @author  CNAF -INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.exception.InvalidPutDoneInputAttributeException;

public class PutDoneInputData implements InputData
{
    private GridUserInterface auth = null;
    private TRequestToken reqToken = null;
    private ArrayOfSURLs arrayOfSURLs = null;

    public PutDoneInputData() {}

    public PutDoneInputData(GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray)
                    throws InvalidPutDoneInputAttributeException
    {
        boolean ok = (!(surlArray == null));
        if (!ok)
            throw new InvalidPutDoneInputAttributeException(surlArray);

        this.auth = auth;
        this.reqToken = reqToken;
        this.arrayOfSURLs = surlArray;
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
}
