
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;

import org.apache.log4j.Logger;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This is the Abort executor for a Copy request. 
 *
 *
 * Authors:
 *     @author lucamag luca.magnoniATcnaf.infn.it
 *
 * @date = Oct 10, 2008
 *
 */

public class CopyAbortExecutor implements AbortExecutorInterface {
    
    private static final Logger log = Logger.getLogger("dataTransfer");
    
    public CopyAbortExecutor() {};
    
    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData) {
        
        AbortGeneralOutputData outputData = new AbortGeneralOutputData();
        boolean requestFailure, requestSuccess;
        TReturnStatus globalStatus = null;

        log.info("SrmAbortRequest: Started AbortCopyExecutor");

        return outputData;
        }
}