package it.grid.storm.synchcall;

/**
 * This class is part of the StoRM project.
 *
 * This class can choose the right dispatcher using configuration parameter etc.
 * 
 * @author lucamag
 * @date May 27, 2008
 *
 */
public class SynchcallDispatcherFactory {
    
    /**
     * @return SynchcallDispatcher
     */
    
    public static SynchcallDispatcher getDispatcher() {
        
        // TODO READ CONFIGURATION AND RETURN THE CORRECT DISPATCHER
        
        return new SimpleSynchcallDispatcher();
    }

}
