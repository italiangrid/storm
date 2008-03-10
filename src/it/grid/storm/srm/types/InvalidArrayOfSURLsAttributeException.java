/**
 * This class represents an Exception throws if TExtraInfo is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.srm.types;

import java.util.List;
import java.util.Vector;



public class InvalidArrayOfSURLsAttributeException extends Exception {

	private boolean nullArray = true;
  
	public InvalidArrayOfSURLsAttributeException(List array) {
		nullArray = (array==null);
	}
	
	public String toString() {
		return "surlList = "+nullArray;
	}
}
