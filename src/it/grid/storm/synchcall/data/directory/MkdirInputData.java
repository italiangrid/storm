package it.grid.storm.synchcall.data.directory;


import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.InputData;

public interface MkdirInputData extends InputData 
{

    /**
     * Method that SURL specified in SRM request.
     */

    public TSURL getSurl();

}
