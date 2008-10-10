/**
 * This class represents an Exception throws if PutDone input data is not well formed. *
 * @author  Alberto Forti
 * @author  CNAF - INFN Bologna
 * @date    AUG 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;

public class InvalidPutDoneOutputAttributeException extends Exception
{
    private boolean nullSurlStatus = true;

    public InvalidPutDoneOutputAttributeException(ArrayOfTSURLReturnStatus surlStatus)
    {
        nullSurlStatus = (surlStatus == null);
    }

    public String toString()
    {
        return "nullSurlStatusArray = " + nullSurlStatus;
    }
    
}
