package it.grid.storm.srm.types;



/**
 * This class represents an Exception throws if TDirOptionData  is not well formed. * 
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

public class InvalidTDirOptionAttributesException extends Exception {

    private boolean allLevel = true;
    private int numLevel = -1;
      
    public InvalidTDirOptionAttributesException(boolean allLevel, int numLevel) {
        this.allLevel = allLevel;
	this.numLevel = numLevel;
    }

    public String toString() {
        return "Invalid TDirOption: recursion as specified by allLevel is "+allLevel+", but numLevel is set to "+numLevel;
    }
}
