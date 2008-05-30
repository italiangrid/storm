/**
 * This class represents the Rm Output Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

public class RmdirOutputData implements OutputData {

    private TReturnStatus returnStatus = null;

    public RmdirOutputData() {

    }

    public RmdirOutputData(TReturnStatus retStatus)
            throws InvalidRmOutputAttributeException {
     
        this.returnStatus = retStatus;
       
    }

    /**
     * Method that return Status.
     */

    public TReturnStatus getStatus() {
        return returnStatus;
    }

    /**
     * Set ReturnStatus
     * 
     */
    public void setStatus(TReturnStatus retStat) {
        this.returnStatus = retStat;
    }

  
    /*
     * (non-Javadoc)
     * 
     * @see it.grid.storm.synchcall.data.OutputData#isSuccess()
     */
    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return true;
    }

  
}
