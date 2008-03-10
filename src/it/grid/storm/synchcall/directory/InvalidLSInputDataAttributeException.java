/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.directory;

import it.grid.storm.srm.types.ArrayOfSURLs;

import java.util.Vector;

public class InvalidLSInputDataAttributeException extends Exception {

    private boolean nullArray = true;

//	public InvalidLSInputDataAttributeException(TSURLInfo[] array) {
    public InvalidLSInputDataAttributeException(ArrayOfSURLs array)
    {
	nullArray = (array==null);
    }


    public String toString()
    {
	return "nullArray = "+nullArray;
    }
}
