package it.grid.storm.synchcall.space.quota;

public class QuotaException extends RuntimeException {

  public QuotaException() {
    super();
  }

  public QuotaException(String message) {
    super(message);
  }

  public QuotaException(String message, Throwable cause) {
    super(message, cause);
  }

  public QuotaException(Throwable cause) {
    super(cause);
  }

}
