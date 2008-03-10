package it.grid.storm.authorization.sources;

/**
 * Class that represent an error condition when communicating with the ECAR service.
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    June 2006
 */
public class ECARClientException extends Exception {

    private String explanation = "";

    public ECARClientException(String explanation) {
        if (explanation!=null) this.explanation=explanation;
    }

    public String toString() {
        return explanation;
    }
}
