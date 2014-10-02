package it.grid.storm.srm.types;

public class SRMCommandException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private TReturnStatus returnStatus;

  public SRMCommandException(TStatusCode code, String message) {

    super(message);
    this.returnStatus = new TReturnStatus(code, message);
  }

  public TReturnStatus getReturnStatus() {

    return returnStatus;
  }

}