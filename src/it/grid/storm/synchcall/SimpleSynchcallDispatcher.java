package it.grid.storm.synchcall;

import it.grid.storm.common.OperationType;
import it.grid.storm.synchcall.command.Command;
import it.grid.storm.synchcall.command.CommandFactory;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project.
 * 
 * This class contains the logic to process the different synchcall request.
 * This dispatcher simply execute a new request when it's just arrived.
 * A more complex version can have thread pools and more complicated pattern.
 *
 * @author lucamag
 * @date May 27, 2008
 *
 */

public class SimpleSynchcallDispatcher implements SynchcallDispatcher {
    
    
    
    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.SynchcallDispatcher#processRequest(it.grid.storm.health.OperationType, it.grid.storm.synchcall.data.InputData)
     */

    public OutputData processRequest(OperationType type, InputData inputData) {
        
        Command cmd = CommandFactory.getCommand(type);
        return cmd.execute(inputData);
    
        
    }

}
