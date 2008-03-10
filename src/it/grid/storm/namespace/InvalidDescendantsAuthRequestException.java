package it.grid.storm.namespace;

import it.grid.storm.griduser.*;
import it.grid.storm.srm.types.*;

/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidDescendantsAuthRequestException
    extends Exception {

    private TDirOption dirOption;
    private GridUserInterface gUser;

    public InvalidDescendantsAuthRequestException(TDirOption dirOption, GridUserInterface gUser) {
        this.dirOption = dirOption;
        this.gUser = gUser;
    }

    public String toString() {
        return ("Request not Authorized for user: " + gUser);
    }
}
