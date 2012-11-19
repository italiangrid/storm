package it.grid.storm.synchcall.data.datatransfer;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;

public interface ReleaseFilesInputData extends InputData 
{

    /**
     * @return the arrayOfSURLs
     */
    public ArrayOfSURLs getArrayOfSURLs();

    /**
     * @return the requestToken
     */
    public TRequestToken getRequestToken();

}
