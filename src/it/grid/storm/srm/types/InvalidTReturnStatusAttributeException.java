package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for TReturnStatus is invoked 
 * with a null TStatusCode.
 *
 * @author  Magnoni Luca
 * @author  CNAF INFN Bologna
 * @date    Avril, 2005
 * @version 1.0
 */

import it.grid.storm.srm.types.TStatusCode;

public class InvalidTReturnStatusAttributeException extends Exception {

    private boolean nullTStatusCode;

    public InvalidTReturnStatusAttributeException(TStatusCode s,String expl) {
        nullTStatusCode = s==null;
    }

    public String toString() {
        return "Invalid TReturnStatus Attributes: nullTStatusCode="+nullTStatusCode;
    }
}
