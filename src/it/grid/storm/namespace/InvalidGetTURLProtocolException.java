package it.grid.storm.namespace;

/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

import it.grid.storm.common.types.*;

public class InvalidGetTURLProtocolException
    extends Exception {

    private int size = 0;

    public InvalidGetTURLProtocolException(TURLPrefix turls) {
        size = turls.size();
    }

    public String toString() {
        return ("Preifix of Matching Protocol is EMPTY  = " + size);
    }
}
