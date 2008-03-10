package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for SpaceToken
 * is invoked with a null String.
 *
*/

import it.grid.storm.srm.types.TSpaceToken;

public class InvalidArrayOfTSpaceTokenAttributeException extends Exception {

    private boolean nullArray;

    public InvalidArrayOfTSpaceTokenAttributeException(TSpaceToken[] tokenArray) {
        nullArray = tokenArray == null; 
    }

    public String toString() {
       return "Invalid SpaceToken[]: nullArray = "+nullArray;
    }
}
