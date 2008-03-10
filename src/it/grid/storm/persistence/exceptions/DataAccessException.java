package it.grid.storm.persistence.exceptions;


/**
 * This exception is used to mark generic failures in persistence layer
 */

public class DataAccessException extends Exception {
    public DataAccessException() {
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
