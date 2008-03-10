package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when TMetaDataSpace retrince an invalid constructor
 * attributes.
 *
 * @author  Magnoni Luca
 * @author  CNAF INFN Bologna
 * @date    Avril 2005
 * @version 1.0
 */


public class InvalidTMetaDataSpaceAttributeException extends Exception {

    private boolean nullToken;

    /**
     * Constructor that requires the SizeUnit that aused the exception to be thrown.
     */
    public InvalidTMetaDataSpaceAttributeException(TSpaceToken token) {
        nullToken = token==null;
    }

    public String toString() {
        return "Ivalid TMetaDataSpace Attributes: nullSizeUnit="+nullToken;
    }
}
