/**
 * This class represents the Abort Executor Interface for the SRM request Abort*
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.command.datatransfer;

import it.grid.storm.synchcall.data.datatransfer.AbortGeneralInputData;
import it.grid.storm.synchcall.data.datatransfer.AbortGeneralOutputData;

public interface AbortExecutorInterface {

    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData);

}
