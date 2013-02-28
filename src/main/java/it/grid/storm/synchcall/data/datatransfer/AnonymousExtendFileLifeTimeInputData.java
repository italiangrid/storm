package it.grid.storm.synchcall.data.datatransfer;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousExtendFileLifeTimeInputData extends AbstractInputData implements ExtendFileLifeTimeInputData
{

    private final TRequestToken requestToken;
    private final ArrayOfSURLs arrayOfSURLs;
    private final TLifeTimeInSeconds newFileLifetime;
    private final TLifeTimeInSeconds newPinLifetime;

    public AnonymousExtendFileLifeTimeInputData(TRequestToken requestToken, ArrayOfSURLs surlArray,
            TLifeTimeInSeconds newFileLifetime, TLifeTimeInSeconds newPinLifetime) throws IllegalArgumentException
    {
        if (requestToken == null || surlArray == null || newFileLifetime == null || newPinLifetime == null)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: requestToken="
                    + requestToken + " surlArray=" + surlArray + " newFileLifetime=" + newFileLifetime
                    + " newPinLifetime=" + newPinLifetime);
        }
        this.requestToken = requestToken;
        this.arrayOfSURLs = surlArray;
        this.newFileLifetime = newFileLifetime;
        this.newPinLifetime = newPinLifetime;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData#getReqToken()
     */
    @Override
    public TRequestToken getRequestToken()
    {
        return requestToken;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData#getArrayOfSURLs()
     */
    @Override
    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData#getNewFileLifetime()
     */
    @Override
    public TLifeTimeInSeconds getNewFileLifetime()
    {
        return newFileLifetime;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.datatransfer.ExtendFileLifeTimeInputData#getNewPinLifetime()
     */
    @Override
    public TLifeTimeInSeconds getNewPinLifetime()
    {
        return newPinLifetime;
    }

}
