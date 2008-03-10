/**
 * This class represents the SpaceReservationData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * Number of files progressing, Number of files finished, and whether the request
 * is currently suspended.
 *
 * @author  Magnoni Luca
 * @author  Cnaf -INFN Bologna
 * @date
 * @version 1.0
 */
package it.grid.storm.synchcall.space;

import java.io.Serializable;


import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.griduser.VomsGridUser;

public class GetSpaceMetaDataInputData implements Serializable {

    private VomsGridUser auth = null;
    private ArrayOfTSpaceToken tokenArray = null;

    public GetSpaceMetaDataInputData()
    {

    }


    public GetSpaceMetaDataInputData(VomsGridUser auth, ArrayOfTSpaceToken tokenArray) throws
	    InvalidGetSpaceMetaDataInputAttributeException
    {

	boolean ok = auth!=null&&tokenArray!=null;

	if (!ok) {
	    throw new InvalidGetSpaceMetaDataInputAttributeException(auth, tokenArray);
	}

	this.auth = auth;
	this.tokenArray = tokenArray;

    }


    /**
     * Method that returns GridUser specify in SRM request.

     */

    public VomsGridUser getUser()
    {
	return auth;
    }


    /**
     *
     *
     */
    public void setUser(VomsGridUser user)
    {

	this.auth = user;

    }


    /**
     * Method return token.
     * i
     * n queue.
     */

    public ArrayOfTSpaceToken getSpaceTokenArray()
    {
	return tokenArray;
    }


    public void setSpaceTokenArray(ArrayOfTSpaceToken token_a)
    {
	this.tokenArray = token_a;
    }


    public TSpaceToken getSpaceToken(int index)
    {
	return tokenArray.getTSpaceToken(index);
    }
}
