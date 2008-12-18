package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.TReturnStatus;

/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidReleaseSpaceOutputDataAttributesException extends Exception {

    private boolean nullStatus = true;

    public InvalidReleaseSpaceOutputDataAttributesException(TReturnStatus status)
    {
	nullStatus = (status==null);
    }


    public String toString()
    {
	return " nullStatus = "+nullStatus;
    }
}
