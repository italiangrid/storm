package it.grid.storm.asynch;

/**
 * Class that represents an Exception thrown when OverallRequest was attempted to be
 * created with a null TRequestToken.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    September, 2006
 */
public class InvalidOverallRequestAttributeException extends Exception {

    public String toString() {
        return "Null TRequestToken supplied!";
    }
}
