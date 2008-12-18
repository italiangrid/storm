package it.grid.storm.synchcall.command;

import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;

public interface Command {

    /**
     * Method that provide SrmRm functionality.
     * 
     * @param inputData
     *            Contains information about input data for rm request.
     * @return RmOutputData Contains output data
     */
    public abstract OutputData execute(InputData inputData);

}