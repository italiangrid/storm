package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidPutDoneOutputAttributeException;

/**
 *
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 *
 * This class represents the PutDone Output Data associated with the SRM request PutDone
 *
 * Authors:
 *     @author=lucamag luca.magnoniATcnaf.infn.it
 *     @author  Alberto Forti
 *
 * @date = Oct 10, 2008
 *
 */

public class PutDoneOutputData implements OutputData
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

    /* (non-Javadoc)
     * @see it.grid.storm.synchcall.data.OutputData#isSuccess()
     */
    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return true;
    }
}
