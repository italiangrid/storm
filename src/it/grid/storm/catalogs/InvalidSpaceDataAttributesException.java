package it.grid.storm.catalogs;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSpaceToken;

/**
 * This class represents an Exception throws if SpaceResData is not well formed. * 
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

public class InvalidSpaceDataAttributesException extends Exception {

    private boolean nullAuth = true;
    private boolean nullType = true;
    private boolean nullToken = true;
    private boolean negSpaceDes = true;
    private boolean negSpaceGuar = true;
    
	public InvalidSpaceDataAttributesException(GridUserInterface guser) {
		nullAuth = (guser==null);
	}
	public InvalidSpaceDataAttributesException(TSpaceToken token) {
		nullToken = (token==null);
	}


	public String toString() {
		return "null-Auth="+nullAuth+"nullToken="+nullToken;
	}

}
