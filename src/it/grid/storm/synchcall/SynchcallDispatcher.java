package it.grid.storm.synchcall;

import it.grid.storm.common.OperationType;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.synchcall.data.OutputData;

/**
 * This class is part of the StoRM project.
 * 
 * This class contains the logic to process the different synchcall request.
 * This dispatcher simply execute a new request when it's just arrived.
 * A more complex version can have thread pools and more complicated pattern.
 * 
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * @author lucamag
 * @date May 28, 2008
 *
 */
public interface SynchcallDispatcher {

    /**
     * @param type Type of the SRM request to execute.
     * @param inputData InputDaata contining input information.
     * @return outputData OutputData containing outputData.
     */

    public abstract OutputData processRequest(OperationType type,
            InputData inputData);

}