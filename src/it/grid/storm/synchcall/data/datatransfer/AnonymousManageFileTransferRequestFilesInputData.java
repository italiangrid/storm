package it.grid.storm.synchcall.data.datatransfer;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;

public class AnonymousManageFileTransferRequestFilesInputData extends AnonymousManageFileTransferFilesInputData  implements ManageFileTransferRequestFilesInputData
{

    private final TRequestToken requestToken;
    public AnonymousManageFileTransferRequestFilesInputData(TRequestToken requestToken, ArrayOfSURLs arrayOfSURLs) throws IllegalArgumentException
    {
        super(arrayOfSURLs);
        if (requestToken == null)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: requestToken="
                                                       + requestToken);
        }
        this.requestToken = requestToken;
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
