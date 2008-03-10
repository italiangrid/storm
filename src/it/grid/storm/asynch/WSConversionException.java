package it.grid.storm.asynch;

/**
 * Class that represens an exception thrown when the conversion between WS types
 * and StoRM Object Model types, fails for some reason.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    October, 2005
 */
public class WSConversionException extends Exception {

    private String explanation=""; //String containing the reason for the exception

    /**
     * Constructor that requires a String describing the reason for the exception.
     */
    public WSConversionException(String explanation) {
        if (explanation!=null) this.explanation=explanation;
    }

    public String toString() {
        return explanation;
    }
}
