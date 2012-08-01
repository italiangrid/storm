/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.synchcall.data.space;

import java.io.Serializable;


import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.exception.InvalidGetSpaceMetaDataInputAttributeException;

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

public class GetSpaceMetaDataInputData extends AbstractInputData implements Serializable {

    private GridUserInterface auth = null;
    
    private ArrayOfTSpaceToken tokenArray = null;

    public GetSpaceMetaDataInputData()
    {

    }


    public GetSpaceMetaDataInputData(GridUserInterface auth, ArrayOfTSpaceToken tokenArray) throws
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

    public GridUserInterface getUser()
    {
	return auth;
    }


    /**
     *
     *
     */
    public void setUser(GridUserInterface user)
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

    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return this.auth.getDn();
    }
}
