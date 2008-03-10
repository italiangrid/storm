package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for SpaceToken
 * is invoked with a null String.
 *
*/

import it.grid.storm.srm.types.TExtraInfo;

public class InvalidArrayOfTExtraInfoAttributeException extends Exception {

    private boolean nullArray;

    public InvalidArrayOfTExtraInfoAttributeException(Object[] infoArray) {
        nullArray = infoArray == null; 
    }

    public String toString() {
       return "Invalid TExtraInfo[]: nullArray = "+nullArray;
    }
}
