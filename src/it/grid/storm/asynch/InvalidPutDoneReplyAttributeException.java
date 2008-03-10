package it.grid.storm.asynch;

import it.grid.storm.srm.types.TTURL;
import it.grid.storm.srm.types.TReturnStatus;

/**
 * Class that represents an exception thrown when an SRMPutDoneReply
 * cannot be created because the supplied TReturnStatus is null.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    August, 2006
 */
public class InvalidPutDoneReplyAttributeException extends Exception {

    public String toString() {
        return "null supplied TReturnStatus";
    }
}
