/**
 * This class represents an Exception throws if TUserIDData  is not well formed. * 
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */
package it.grid.storm.srm.types;

public class InvalidTUserIDAttributeException extends Exception {

    private boolean nullString = true;
    private boolean emptyString = true;
      
    public InvalidTUserIDAttributeException(String id) {
        nullString = (id==null);
	emptyString = (id == ""); 
    }

    public String toString() {
        return "nullString = "+nullString+"  EmptyString = "+emptyString;
    }
}
