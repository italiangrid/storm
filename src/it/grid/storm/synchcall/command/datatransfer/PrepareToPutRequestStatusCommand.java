package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.synchcall.command.Command;

public class PrepareToPutRequestStatusCommand extends FileTransferRequestStatusCommand implements Command
{
    private static final String SRM_COMMAND = "srmStatusOfPutRequest";
    
    public PrepareToPutRequestStatusCommand() {
    }

    @Override
    protected String getSrmCommand()
    {
        return SRM_COMMAND;
    }

    @Override
    protected TRequestType getRequestType()
    {
        return TRequestType.PREPARE_TO_PUT;
    };

}
