package it.grid.storm.wrapper;


/**
 * This class represents an Exception throws if SpaceFile Path is not correct.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

public class InvalidSpaceResExecException extends Exception {

    private String path;
    
    public InvalidSpaceResExecException(String path) {
        this.path = path;
    }

    public String toString() {
        return "Error Path = "+path;
    }
}
