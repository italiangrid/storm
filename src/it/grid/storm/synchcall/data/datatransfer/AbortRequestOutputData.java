/**
 * This class represents the AbortRequest Output Data associated with the SRM request AbortRequest
 * @author  Magnoni Luca
 * @author  CNAF -INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

public class AbortRequestOutputData implements OutputData
{
    private TReturnStatus returnStatus = null;

    public AbortRequestOutputData()
    {
    }

    public AbortRequestOutputData(TReturnStatus retStatus)
                    //throws InvalidAbortRequestOutputDataAttributeException
    {
        boolean ok = (retStatus == null);

        if (!ok) {
            ;//throw new InvalidAbortRequestOutputDataAttributeException(retStatus);
        }

        this.returnStatus = retStatus;
    }
    
    public static AbortRequestOutputData make(AbortGeneralOutputData generalOutData) {
        //Create an output data from an AbortFiles output data.
        // new AbortRequestOutputData(generalOutData.getReturnStatus());
        return new AbortRequestOutputData(generalOutData.getReturnStatus());
    }
    
    /**
     * Returns the returnStatus field
     * @return TReturnStatus
     */
    public TReturnStatus getReturnStatus()
    {
        return returnStatus;
    }

    /**
     * Set the returnStatus field
     * @param returnStatus
     */
    public void setReturnStatus(TReturnStatus returnStatus)
    {
        this.returnStatus = returnStatus;
    }

    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return false;
    }

    
}
