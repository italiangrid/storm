/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.namespace;

import it.grid.storm.srm.types.*;

public class MalformedSURLException
    extends Exception {

    private TSURL surl = null;

    public MalformedSURLException(TSURL surl) {
        this.surl = surl;
    }

    public String toString() {
        return ("Malformed SURL does not contains StFNRoot: " + surl);
    }

}
