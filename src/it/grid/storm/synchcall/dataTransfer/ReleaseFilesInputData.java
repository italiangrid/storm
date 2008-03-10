package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;

public class ReleaseFilesInputData
{
    private GridUserInterface auth = null;
    private TRequestToken requestToken = null;
    private ArrayOfSURLs arrayOfSURLs = null;
    private Boolean doRemove = null;

    public ReleaseFilesInputData()
    {
    }

    public ReleaseFilesInputData(GridUserInterface auth, TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs,
                    Boolean doRemove) throws InvalidReleaseFilesInputAttributeException
    {
        boolean ok = !((arrayOfSURLs == null) && (requestToken == null));
        if (!ok)
            throw new InvalidReleaseFilesInputAttributeException(arrayOfSURLs);

        this.auth = auth;
        this.requestToken = requestToken;
        this.arrayOfSURLs = arrayOfSURLs;
        this.doRemove = doRemove;
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
    
    public Boolean getDoRemove()
    {
        return doRemove;
    }

    public void setDoRemove(Boolean doRemove)
    {
        this.doRemove = doRemove;
    }
    
}
