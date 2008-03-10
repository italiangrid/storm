/**
 * This class represents the PutDone Output Data associated with the SRM request PutDone
 * * @author  Alberto Forti
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;

public class PutDoneOutputData
{
    private TReturnStatus returnStatus = null;
    private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

    public PutDoneOutputData()
    {
        this.returnStatus = null;
        this.arrayOfFileStatus = null;
    }

    public PutDoneOutputData(TReturnStatus retStatus, ArrayOfTSURLReturnStatus arrayOfFileStatus)
                    throws InvalidPutDoneOutputAttributeException
    {
        boolean ok = (arrayOfFileStatus == null);

        if (!ok) {
            throw new InvalidPutDoneOutputAttributeException(arrayOfFileStatus);
        }

        this.returnStatus = retStatus;
        this.arrayOfFileStatus = arrayOfFileStatus;
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

    /**
     * Returns the arrayOfFileStatuses field
     * @return TSURLReturnStatus
     */
    public ArrayOfTSURLReturnStatus getArrayOfFileStatuses()
    {
        return arrayOfFileStatus;
    }

    /**
     * Set the arrayOfFileStatuses field
     * @param arrayOfFileStatuses
     */
    public void setArrayOfFileStatuses(ArrayOfTSURLReturnStatus arrayOfFileStatuses)
    {
        this.arrayOfFileStatus = arrayOfFileStatuses;
    }
}
