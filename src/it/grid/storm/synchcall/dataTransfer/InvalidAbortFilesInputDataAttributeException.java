/**
 * This class represents an Exception throws if AbortFiles input data is not well formed. *
 * @author  Magnoni Luca
 * @author  CNAF - INFN Bologna
 * @date    Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.dataTransfer;

import it.grid.storm.srm.types.ArrayOfSURLs;

public class InvalidAbortFilesInputDataAttributeException extends Exception
{

    private boolean nullSurlInfo = true;

    public InvalidAbortFilesInputDataAttributeException(ArrayOfSURLs surlInfo)
    {
        nullSurlInfo = (surlInfo == null);
    }

    public String toString()
    {
        return "nullSurlInfo = " + nullSurlInfo;
    }
}
