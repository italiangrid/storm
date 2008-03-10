/**
 * This class represents an Exception throws if TSURLINFO is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.srm.types;

import it.grid.storm.srm.types.TSURL;


public class InvalidTSURLReturnStatusAttributeException extends Exception {

	private boolean nullSurl = true;
  
	public InvalidTSURLReturnStatusAttributeException(TSURL surl) {
		nullSurl = (surl==null);
	}
	
	public String toString() {
		return "nullSurl = "+nullSurl;
	}
}
