/**
 * This class represents the Abort Executor Interface for the SRM request Abort*
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.dataTransfer;

public interface AbortExecutorInterface {

    public AbortGeneralOutputData doIt(AbortGeneralInputData inputData);

}
