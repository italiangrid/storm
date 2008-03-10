package it.grid.storm.wrapper;


/**
 * This class represents an Exception throws if SpaceFile Path is not correct.
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date    
 * @version 1.0
 */

public class InvalidAclFormatException extends Exception {

    private String acl;
    
    public InvalidAclFormatException(String acl) {
        this.acl= acl;
    }

    public String toString() {
        return "Error ACL = "+acl;
    }
}
