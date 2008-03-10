package it.grid.storm.common.types;

/**
 * This class represents an exception thrown if a Port is attempted to be built with
 * an int <0 or >65535.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 25th, 2005
 * @version 1.0
 */
public class InvalidPortAttributeException extends Exception {

    private int port;

    /**
     * Constructor requiring the port that caused the exception.
     */
    public InvalidPortAttributeException(int port) {
        this.port = port;
    }

    public String toString() {
        return "Port exceeded limits; supplied port was: "+port;
    }
}
