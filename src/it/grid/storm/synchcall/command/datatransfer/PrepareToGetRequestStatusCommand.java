package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.synchcall.command.Command;

public class PrepareToGetRequestStatusCommand extends FileTransferRequestStatusCommand implements Command
{
    private static final String SRM_COMMAND = "srmStatusOfGetRequest";
    
    public PrepareToGetRequestStatusCommand() {
    }

    @Override
    protected String getSrmCommand()
    {
        return SRM_COMMAND;
    }

    @Override
    protected TRequestType getRequestType()
    {
        return TRequestType.PREPARE_TO_GET;
    };

}
