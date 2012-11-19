package it.grid.storm.synchcall.data.datatransfer;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousReleaseFilesInputData extends AbstractInputData implements ReleaseFilesInputData
{

    private final TRequestToken requestToken;
    private final ArrayOfSURLs arrayOfSURLs;

    public AnonymousReleaseFilesInputData(TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs) throws IllegalArgumentException
    {
        if (requestToken == null || arrayOfSURLs == null)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: requestToken="
                                                       + requestToken + " arrayOfSURLs=" + arrayOfSURLs);
        }
        this.requestToken = requestToken;
        this.arrayOfSURLs = arrayOfSURLs;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.datatransfer.ReleaseFilesInputData#getArrayOfSURLs()
     */
    @Override
    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.datatransfer.ReleaseFilesInputData#getRequestToken()
     */
    @Override
    public TRequestToken getRequestToken()
    {
        return requestToken;
    }

}
