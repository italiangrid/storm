package it.grid.storm.authorization.sources;

/**
 * Class that represents a SoapFault thrown by the ECAR service.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    June 2006
 */
public class ECARServiceException extends Exception {

    private String fault = "";

    public ECARServiceException(String fault) {
        if (fault!=null) this.fault=fault;
    }

    public String toString() {
        return fault;
    }
}
