package it.grid.storm.asynch;

/**
 * Class that represents an exception thrown when the internal client could not
 * carry out the invoked SRM operation.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September, 2005
 */
public class SRMClientException extends Exception {

    private String explanation=""; //String that explains what went wrong with srm client operation

    public SRMClientException() {}

    /**
     * Public constructor that requires the String explaining the reason for the srm failure.
     */
    public SRMClientException(String explanation) {
        if (explanation!=null) this.explanation = explanation;
    }

    public String toString() {
        return explanation;
    }
}
