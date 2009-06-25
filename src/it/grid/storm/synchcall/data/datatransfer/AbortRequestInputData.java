/**
 * This class represents the PutDone Input Data associated with the SRM request PutDone
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.exception.InvalidAbortRequestInputDataAttributeException;

public class AbortRequestInputData extends AbortGeneralInputData
{
    private GridUserInterface auth = null;
    private TRequestToken reqToken = null;
    private String authID = null;
   
    public AbortRequestInputData() {}

    public AbortRequestInputData(GridUserInterface auth, TRequestToken reqToken)
                    throws InvalidAbortRequestInputDataAttributeException
    {
        boolean ok = (!(reqToken== null));
        if (!ok)
            throw new InvalidAbortRequestInputDataAttributeException(reqToken);

        this.auth = auth;
        this.reqToken = reqToken;

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
    
}
