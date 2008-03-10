package it.grid.storm.asynch;

/**
 * Class that represents an exception thrown if the class specified in the config
 * file for the SRMClient, cannot be instantiated.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September 2005
 */
public class NoSRMClientFoundException extends Exception {

    private String explanation = ""; //String containing an explanation of the error

    /**
     * Constructor that requires a String explaining the error.
     */
    public NoSRMClientFoundException(String explanation) {
        if (explanation!=null) this.explanation = explanation;
    }

    public String toString() {
        return explanation;
    }
}
