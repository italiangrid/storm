package it.grid.storm.asynch;

/**
 * Class that represents an exception thrown when an SRMPrepareToPutReply
 * cannot be created because the supplied TRequestToken is null.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September, 2005
 */
public class InvalidPutReplyAttributeException extends Exception {


    public String toString() {
        return "null TRequestToken";
    }
}
