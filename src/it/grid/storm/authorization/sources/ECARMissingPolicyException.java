package it.grid.storm.authorization.sources;

/**
 * Class that represents a missing policy in ECAR. In particular this exception was
 * thought to be thrown when ECAR replies with a SoapFault about "InvalidPath".
 *
 * @author  EGRID - ICTP Trieste
 * @version 1.0
 * @date    June 2006
 */
public class ECARMissingPolicyException extends Exception {

    private String explanation = "";

    public ECARMissingPolicyException(String explanation) {
        if (explanation!=null) this.explanation=explanation;
    }

    public String toString() {
        return explanation;
    }
}
