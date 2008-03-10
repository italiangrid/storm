package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;

public class ReleaseFilesOutputData
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

}
