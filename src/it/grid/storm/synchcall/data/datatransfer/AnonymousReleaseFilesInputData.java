package it.grid.storm.synchcall.data.datatransfer;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousReleaseFilesInputData extends AbstractInputData implements ReleaseFilesInputData
{

    protected final ArrayOfSURLs arrayOfSURLs;

    public AnonymousReleaseFilesInputData(ArrayOfSURLs arrayOfSURLs)
    {
        if (arrayOfSURLs == null || arrayOfSURLs.size() == 0)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: " +
            		"arrayOfSURLs=" + arrayOfSURLs);
        }
        this.arrayOfSURLs = arrayOfSURLs;
    }

    @Override
    public ArrayOfSURLs getArrayOfSURLs()
    {
        return arrayOfSURLs;
    }

}
