/**
 * This class represents an Exception throws if ReleaseFiles input data is not well formed. *
 * @author  Alberto Forti
 * @author  CNAF - INFN Bologna
 * @date    AUG 2006
 * @version 1.0
 */
package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.ArrayOfSURLs;

public class InvalidReleaseFilesInputAttributeException extends Exception
{
    private boolean nullArrayOfSURLs = true;
    
    public InvalidReleaseFilesInputAttributeException(ArrayOfSURLs arrayOfSURLs)
    {
        nullArrayOfSURLs = (arrayOfSURLs == null);
    }
    
    public String toString()
    {
        return "nullArrayOfSURLs = " + nullArrayOfSURLs;
    }

}
