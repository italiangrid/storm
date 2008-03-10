package it.grid.storm.wrapper;


/**
 * This class represents an Exception throws if SpaceFile Path is not correct.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

public class InvalidExecCommandException extends Exception {

    private String path;
    
    public InvalidExecCommandException(String path) {
        this.path = path;
    }

    public String toString() {
        return "Error Path = "+path;
    }
}
