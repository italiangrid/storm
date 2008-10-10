package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;

public class InvalidReleaseFilesOutputDataAttributeException extends Exception
{
    private boolean nullSurlStatus = true;
    
    public InvalidReleaseFilesOutputDataAttributeException(ArrayOfTSURLReturnStatus surlStatus)
    {
        nullSurlStatus = (surlStatus == null);
    }

    public String toString()
    {
        return "nullSurlStatusArray = " + nullSurlStatus;
    }

}
