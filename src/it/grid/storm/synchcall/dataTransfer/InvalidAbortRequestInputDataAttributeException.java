/**
 * This class represents an Exception throws if Abort Request input data is not well formed. *
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.srm.types.TRequestToken;

public class InvalidAbortRequestInputDataAttributeException extends Exception
{

    private boolean nullTokenInfo = true;

    public InvalidAbortRequestInputDataAttributeException(TRequestToken token)
    {
        nullTokenInfo = (token == null);
    }

    public String toString()
    {
        return "nullTokenInfo = " + nullTokenInfo;
    }
}
