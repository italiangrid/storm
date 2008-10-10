/**
 * This class represents the ExtendFileLifeTime Output Data.
 * @author  Alberto Forti
 * @author  CNAF-INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfTSURLLifetimeReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;

public class ExtendFileLifeTimeOutputData implements OutputData 
{
    private TReturnStatus                    returnStatus        = null;
    private ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses = null;

    public ExtendFileLifeTimeOutputData() {
        this.returnStatus = null;
        this.arrayOfFileStatuses = null;
    }

    public ExtendFileLifeTimeOutputData(TReturnStatus retStatus, ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses) {
        this.returnStatus = retStatus;
        this.arrayOfFileStatuses = arrayOfFileStatuses;
    }

    /**
     * Returns the returnStatus field.
     * @return TReturnStatus
     */
    public TReturnStatus getReturnStatus()
    {
        return returnStatus;
    }
    /**
     * Set the returnStatus field.
     * @param returnStatus TReturnStatus
     */
    public void setReturnStatus(TReturnStatus returnStatus)
    {
        this.returnStatus = returnStatus;
    }

    /**
     * Returns the arrayOfFileStatuses field.
     * @return ArrayOfTSURLLifetimeReturnStatus
     */
    public ArrayOfTSURLLifetimeReturnStatus getArrayOfFileStatuses()
    {
        return arrayOfFileStatuses;
    }
    /**
     * Set the arrayOfFileStatuses field.
     * @param arrayOfFileStatuses
     */
    public void setArrayOfFileStatuses(ArrayOfTSURLLifetimeReturnStatus arrayOfFileStatuses)
    {
        this.arrayOfFileStatuses = arrayOfFileStatuses;
    }

    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return true;
    }
}
