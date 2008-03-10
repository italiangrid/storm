package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for SpaceToken
 * is invoked with a null String.
 *
*/

import it.grid.storm.srm.types.TMetaDataSpace;

public class InvalidArrayOfTMetaDataSpaceAttributeException extends Exception {

    private boolean nullArray;

    public InvalidArrayOfTMetaDataSpaceAttributeException(TMetaDataSpace[] metaDataArray) {
        nullArray = metaDataArray == null; 
    }

    public String toString() {
       return "Invalid TMetaDataArray[]: nullArray = "+nullArray;
    }
}
