/**
 * This class represents an Exception throws if SpaceResData is not well formed. *
 * @author  Magnoni Luca
 * @author  Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.space;


import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.griduser.VomsGridUser;

public class InvalidGetSpaceMetaDataInputAttributeException extends Exception {

    private boolean nullUser = true;
    private boolean nullToken = true;

    public InvalidGetSpaceMetaDataInputAttributeException(VomsGridUser user, ArrayOfTSpaceToken tokenArray)
    {
	nullToken = (tokenArray==null);
	nullUser = (user==null);
    }


    public String toString()
    {
	return "nullTokenArray = "+nullToken+"- nullUser = "+nullUser;
    }
}
