package it.grid.storm.synchcall.data.space;

import java.io.Serializable;


import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.InputData;
import it.grid.storm.griduser.VomsGridUser;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the SpaceReservationData associated with the SRM request, that is
 * it contains info about: UserID, spaceType, SizeDesired, SizeGuaranteed,ecc.
 * Number of files progressing, Number of files finished, and whether the request
 * is currently suspended.
 * 
 * @author lucamag
 * @date May 29, 2008
 *
 */

public class GetSpaceMetaDataInputData implements Serializable, InputData {

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
