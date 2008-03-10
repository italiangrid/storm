package it.grid.storm.namespace;

/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

import it.grid.storm.common.types.*;

public class InvalidGetTURLNullPrefixAttributeException
    extends Exception {

    private boolean isNullPrefixOfProtocol = false;

    public InvalidGetTURLNullPrefixAttributeException(TURLPrefix turls) {
        isNullPrefixOfProtocol = (turls.size() == 0);
    }

    public String toString() {
        return ("Preifix of Protocol Specified is EMPTY  = " + isNullPrefixOfProtocol);
    }
}
