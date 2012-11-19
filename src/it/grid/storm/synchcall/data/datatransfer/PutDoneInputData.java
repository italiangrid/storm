package it.grid.storm.synchcall.data.datatransfer;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.InputData;

public interface PutDoneInputData extends InputData
{

    public TRequestToken getRequestToken();

    public ArrayOfSURLs getArrayOfSURLs();

}
