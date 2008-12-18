/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.TSURL;

public class InvalidMkdirInputAttributeException extends Exception {

    private boolean nullSurl = true;

    public InvalidMkdirInputAttributeException(TSURL surl)
    {
        nullSurl = (surl==null);
    }


    public String toString()
    {
        return "nullSurl = "+nullSurl;
    }
}
