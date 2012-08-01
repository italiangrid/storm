package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.datatransfer.PrepareToPutOutputData;

public class PrepareToPutRequestCommand implements Command
{

    @Override
    public OutputData execute(InputData inputData)
    {
        // TODO Auto-generated method stub
        return new PrepareToPutOutputData(null, null, null);
    }

}
