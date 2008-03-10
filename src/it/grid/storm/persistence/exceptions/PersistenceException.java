package it.grid.storm.persistence.exceptions;

/**
 * This exception is used to mark generic failures in persistence layer
 *
 */

public class PersistenceException  extends Exception {
  public PersistenceException() {
    super();
  }

  public PersistenceException(String message) {
      super(message);
  }

  public PersistenceException(String message, Throwable cause) {
      super(message, cause);
  }

  public PersistenceException(Throwable cause) {
      super(cause);
    }

}
