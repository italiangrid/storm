package it.grid.storm.wrapper;


/**
 * This class represents an Exception throws if SrmLS request it's specified into a regular file , non a valid directory.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

import it.grid.storm.common.types.PFN;

public class InvalidLSRequestException extends Exception {

    private PFN pfn;
    
    public InvalidLSRequestException(PFN pfn) {
        this.pfn = pfn;
    }

    public String toString() {
        return "Error Directory = "+pfn;
    }
}
