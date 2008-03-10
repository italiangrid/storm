package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for TLifeTimeInSeconds
 * is invoked with a null TimeUnit.
 *
 * @author  Ezio Corso
 * @author  EGRID - ICTP Trieste
 * @date    March 23rd, 2005
 * @version 1.0
 */

import it.grid.storm.common.types.TimeUnit;

public class InvalidTLifeTimeAttributeException extends Exception {

    private boolean nullTimeUnit;

    /**
     * Constructor requiring the TimeUnit u that caused the Exception to be thrown.
     */
    public InvalidTLifeTimeAttributeException(TimeUnit u) {
        nullTimeUnit= u==null;
    }

    public String toString() {
        return "Invalid TLifeTime attributes: null-TimeUnit="+nullTimeUnit;
    }
}
