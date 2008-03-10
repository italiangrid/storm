package it.grid.storm.asynch;

/**
 * Class that represents an exception thrown by a GridFTPTransferClient, in case
 * for any reason the operation could not be carried out.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September, 2005
 */
public class GridFTPTransferClientException extends Exception {

    private String explanation = ""; //String containing an explanation of what went wrong

    /**
     * Constructor that requires a String with an explanation of what went wrong.
     */
    public GridFTPTransferClientException(String explanation) {
        if (explanation!=null) this.explanation = explanation;
    }

    public String toString() {
        return explanation;
    }
}
