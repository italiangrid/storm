package it.grid.storm.common.types;

/**
 * This class represents an Exception thrown when attempting to create a StFN
 * with a null or empty String, or with a String that does not begin a /.
 *
 * @author  EGRID ICTP - CNAF Bologna
 * @version 2.0
 * @date    March 2005
 */
public class InvalidStFNAttributeException extends Exception {

    private boolean nullName; //boolean true if the supplied String is null
    private boolean emptyName; //boolean true if the supplied String is empty
    private boolean noBeginningSlash=false; //boolean true if the supplied String does not begin with a /
    private boolean hasDot=false; //boolean true is string contains a .

    /**
     * Constructor requiring the String that caused the exception to be thrown.
     */
    public InvalidStFNAttributeException(String name) {
        this.nullName = (name==null);
        this.emptyName = (name.equals(""));
        if (!nullName && !emptyName) {
            noBeginningSlash = (name.charAt(0)!='/');
            hasDot = (name.indexOf("..")!=-1);
        }
    }

    public String toString() {
        return "Invalid StFN Attributes: nullName="+nullName+"; emptyName="+emptyName+"; doesn't beginning with slash="+noBeginningSlash+"; has dots="+hasDot;
    }
}
