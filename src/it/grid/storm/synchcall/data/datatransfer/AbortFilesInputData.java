/**
 * This class represents the general Abort Input Data associated with the SRM request Abort
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.exception.InvalidAbortFilesInputDataAttributeException;

public class AbortFilesInputData implements InputData
{
    private GridUserInterface auth = null;
    private TRequestToken reqToken = null;
    private ArrayOfSURLs arrayOfSURLs = null;

    public AbortFilesInputData() {}

    public AbortFilesInputData(GridUserInterface auth, TRequestToken reqToken, ArrayOfSURLs surlArray)
                    throws InvalidAbortFilesInputDataAttributeException
    {
        boolean ok = (!(surlArray == null));
        if (!ok)
            throw new InvalidAbortFilesInputDataAttributeException(surlArray);

        this.auth = auth;
        this.reqToken = reqToken;
        this.arrayOfSURLs = surlArray;
    }
    
    public AbortFilesInputData(AbortRequestInputData requestInputData) {
        //Create an AbortFiles data from an AbortRequest data
        //In this case the SURLArray MUST BE null.
        this.auth = requestInputData.getUser();
        this.reqToken = requestInputData.getRequestToken();
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
