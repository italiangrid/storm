package it.grid.storm.synchcall.command;

import it.grid.storm.common.OperationType;
import it.grid.storm.synchcall.command.directory.LsCommand;
import it.grid.storm.synchcall.command.directory.MkdirCommand;
import it.grid.storm.synchcall.command.directory.MvCommand;
import it.grid.storm.synchcall.command.directory.RmCommand;
import it.grid.storm.synchcall.command.directory.RmdirCommand;
import it.grid.storm.synchcall.command.discovery.PingCommand;
import it.grid.storm.synchcall.command.space.GetSpaceMetaDataCommand;
import it.grid.storm.synchcall.command.space.GetSpaceTokensCommand;
import it.grid.storm.synchcall.command.space.ReleaseSpaceCommand;
import it.grid.storm.synchcall.command.space.ReserveSpaceCommand;

/**
 * This class is part of the StoRM project.
 * 
 * @author lucamag
 * @date May 27, 2008
 * 
 */

public class CommandFactory  {

    public static Command getCommand(OperationType type) {
        switch(type) {
        case RM: return new RmCommand();
        case RMD: return new RmdirCommand();
        case MKD:  return new MkdirCommand();
        case MV: return new MvCommand();
        case LS: return new LsCommand();
        
        case PNG: return new PingCommand();
        
        case GST: return new GetSpaceTokensCommand();
        case GSM: return new GetSpaceMetaDataCommand();
        case RESSP: return new ReserveSpaceCommand();
        case RELSP: return new ReleaseSpaceCommand();
        
        }
        throw new AssertionError("SimpleCommandFactory: Unknown op: ");

    }


}
