package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.synchcall.data.OutputData;
import it.grid.storm.synchcall.data.exception.InvalidReleaseFilesOutputDataAttributeException;

public class ReleaseFilesOutputData implements OutputData
{
    private TReturnStatus returnStatus = null;
    private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

    public ReleaseFilesOutputData()
    {
    }

    public ReleaseFilesOutputData(TReturnStatus retStatus, ArrayOfTSURLReturnStatus arrayOfFileStatus)
                    throws InvalidReleaseFilesOutputDataAttributeException
    {
        boolean ok = (arrayOfFileStatus == null);

        if (!ok) {
            throw new InvalidReleaseFilesOutputDataAttributeException(arrayOfFileStatus);
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

    public boolean isSuccess() {
        // TODO Auto-generated method stub
        return true;
    }

}
