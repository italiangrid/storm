/**
 * This class represents an Exception throws if TExtraInfo is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.srm.types;



public class InvalidTExtraInfoAttributeException extends Exception {

	private boolean nullKey = true;
  
	public InvalidTExtraInfoAttributeException(String key) {
		nullKey = (key==null);
	}
	
	public String toString() {
		return "nullKey = "+nullKey;
	}
}
