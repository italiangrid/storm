package it.grid.storm.asynch;

import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * Class that represents an exception thrown when an SRMStatusOfPutRequestReply
 * cannot be created because the supplied toTURL or returnStatus are null.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    October, 2005
 */
public class InvalidPutStatusAttributesException extends Exception {

    boolean nullToTURL = false; //boolean indicating whether the supplied TURL is null or not
    boolean nullReturnStatus = false; //boolean indicating whether the supplied TReturnStatus is null or not

    /**
     * Constructor that requires the attributes that caused the exception to be thrown.
     */
    public InvalidPutStatusAttributesException(TTURL toTURL, TReturnStatus returnStatus) {
        nullToTURL = (toTURL==null);
        nullReturnStatus = (returnStatus==null);
    }

    public String toString() {
        return "nullToTURL="+nullToTURL+"; nullReturnStatus="+nullReturnStatus;
    }
}
