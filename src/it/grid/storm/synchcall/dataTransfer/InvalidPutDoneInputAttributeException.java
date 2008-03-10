/**
 * This class represents an Exception throws if PutDone input data is not well formed. *
 * @author  Alberto Forti
 * @author  CANF - INFN Bologna
 * @date    Aug 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.srm.types.ArrayOfSURLs;

public class InvalidPutDoneInputAttributeException extends Exception
{

    private boolean nullSurlInfo = true;

    public InvalidPutDoneInputAttributeException(ArrayOfSURLs surlInfo)
    {
        nullSurlInfo = (surlInfo == null);
    }

    public String toString()
    {
        return "nullSurlInfo = " + nullSurlInfo;
    }
}
