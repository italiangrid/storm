package it.grid.storm.synchcall.space;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class ReserveSpaceException extends Exception {
  public ReserveSpaceException() {
    super();
  }


  public ReserveSpaceException(String message) {
    super(message);
  }


  public ReserveSpaceException(String message, Throwable cause) {
    super(message, cause);
  }


  public ReserveSpaceException(Throwable cause) {
    super(cause);
  }

}
