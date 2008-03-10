/**
 * This class represents an Exception throws if AbortFiles input data is not well formed. *
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;

public class InvalidAbortFilesOutputDataAttributeException extends Exception
{
    private boolean nullSurlStatus = true;

    public InvalidAbortFilesOutputDataAttributeException(ArrayOfTSURLReturnStatus surlStatus)
    {
        nullSurlStatus = (surlStatus == null);
    }

    public String toString()
    {
        return "nullSurlStatusArray = " + nullSurlStatus;
    }
    
}
