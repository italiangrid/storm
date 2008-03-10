
/**
 * This class represents an Exception throws if SpaceToken is not correct.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

package it.grid.storm.wrapper;

import it.grid.storm.srm.types.TSpaceToken;

public class UnknownSpaceToken extends Exception {

    private TSpaceToken token;
    
    public UnknownSpaceToken(TSpaceToken token) {
        this.token = token;
    }

    public String toString() {
       return "UnknowTOKEN: : = "+token;
    }
}
