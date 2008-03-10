/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.directory;

import it.grid.storm.srm.types.ArrayOfSURLs;

import java.util.Vector;

public class InvalidRmInputAttributeException extends Exception {

    private boolean nullSurlInfo = true;

    public InvalidRmInputAttributeException(ArrayOfSURLs surl)
    {
        nullSurlInfo = (surl==null);
    }


    public String toString()
    {
        return "nullSurlInfo = "+nullSurlInfo;
    }
}
