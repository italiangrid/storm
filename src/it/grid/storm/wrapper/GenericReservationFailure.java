
/**
 * This class represents an Exception throws if SpaceToken is not correct.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

package it.grid.storm.wrapper;

import it.grid.storm.srm.types.TSpaceToken;

public class GenericReservationFailure extends Exception {

        
    public GenericReservationFailure() {
       
    }

    public String toString() {
       return "Failure during RESERVATION!!!";
    }
}
