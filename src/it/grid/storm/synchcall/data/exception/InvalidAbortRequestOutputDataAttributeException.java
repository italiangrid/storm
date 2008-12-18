/**
 * This class represents an Exception throws if AbortRequest output data is not well formed. *
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    AUG 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.TReturnStatus;

public class InvalidAbortRequestOutputDataAttributeException extends Exception
{
    private boolean nullStatus = true;

    public InvalidAbortRequestOutputDataAttributeException(TReturnStatus retStatus)
    {
        nullStatus = (retStatus == null);
    }

    public String toString()
    {
        return "nullStatus = " + nullStatus;
    }
    
}
