/**
 * This class represents the AbortFiles Output Data associated with the SRM request AbortFiles
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.dataTransfer;

import org.apache.log4j.Logger;
import java.util.Vector;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURLReturnStatus;

public class AbortFilesOutputData
{
    private static final Logger log = Logger.getLogger("dataTransfer");
    private TReturnStatus returnStatus = null;
    private ArrayOfTSURLReturnStatus arrayOfFileStatus = null;

    public AbortFilesOutputData()
    {
        this.returnStatus = null;
        this.arrayOfFileStatus = null;
    }

    public static AbortFilesOutputData make(AbortGeneralOutputData filesOutData) {
        //Create an output data from an AbortFiles output data.
        //this.returnStatus = filesOutData.getReturnStatus();
        //this.arrayOfFileStatus = filesOutData.getArrayOfFileStatuses();
        return new AbortFilesOutputData(filesOutData.getReturnStatus(), filesOutData.getArrayOfFileStatuses());
    }
    
    public AbortFilesOutputData(TReturnStatus retStatus, ArrayOfTSURLReturnStatus arrayOfFileStatus)
                    //throws InvalidAbortFilesOutputDataAttributeException
    {
        boolean ok = (arrayOfFileStatus == null);

        if (!ok) {
            ;//throw new InvalidAbortFilesOutputDataAttributeException(arrayOfFileStatus);
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
