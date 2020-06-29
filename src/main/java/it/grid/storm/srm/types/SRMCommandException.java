package it.grid.storm.srm.types;

public class SRMCommandException extends Exception {

  private static final long serialVersionUID = 1L;

  private final TReturnStatus returnStatus;

  public SRMCommandException(TStatusCode code, String message) {

    super(message);
    this.returnStatus = new TReturnStatus(code, message);
  }

  public TReturnStatus getReturnStatus() {

    return returnStatus;
  }

}