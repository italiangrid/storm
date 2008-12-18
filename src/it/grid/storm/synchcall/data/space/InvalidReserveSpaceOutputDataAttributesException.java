package it.grid.storm.synchcall.data.space;

import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;

/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidReserveSpaceOutputDataAttributesException extends Exception {

    private boolean nullType = true;
    private boolean negSpaceDes = true;
    private boolean negSpaceGuar = true;
    private boolean lifetime = true;
    private boolean nullToken = true;
    private boolean nullStatus = true;

    public InvalidReserveSpaceOutputDataAttributesException(TSizeInBytes spaceTotal, TSpaceToken spaceToken,
	    TReturnStatus status)
    {
	negSpaceGuar = (spaceTotal==null);
	nullToken = (spaceToken==null);
	nullStatus = (status==null);
    }


    public String toString()
    {
	return "null-TotalSpace = "+negSpaceGuar+"- nullToken = "+nullToken+"- nullStatus = "+nullStatus;
    }
}
