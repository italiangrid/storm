package it.grid.storm.synchcall.data.directory;


import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.synchcall.data.InputData;

public interface RmInputData extends InputData 
{

    /**
     * @return the surlArray
     */
    public ArrayOfSURLs getSurlArray();

}
