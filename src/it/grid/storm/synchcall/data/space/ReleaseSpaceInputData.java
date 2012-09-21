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


import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.exception.InvalidReleaseSpaceAttributesException;
import it.grid.storm.griduser.GridUserInterface;

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



public class ReleaseSpaceInputData extends AbstractInputData {

    private GridUserInterface gUser = null;
    private TSpaceToken spaceToken = null;
    private boolean forceFileRelease = false;

    public ReleaseSpaceInputData(GridUserInterface auth, TSpaceToken spaceToken, /*ArrayOfTExtraInfo storageInfo,*/
			    boolean forceFileRelease) throws InvalidReleaseSpaceAttributesException
    {

    	boolean ok = auth!=null&&spaceToken!=null;
    	if (!ok) {
    	    throw new InvalidReleaseSpaceAttributesException(auth, spaceToken);
    	}
    	this.gUser = auth;
    	this.spaceToken = spaceToken;
    	this.forceFileRelease = forceFileRelease;

    }


    /**
     * Method that returns UserID specify in SRM request.
     */
    public GridUserInterface getUser()
    {
        	return this.gUser;
    }


    /**
     * Method that returns the SpaceToken.
     */
    public TSpaceToken getSpaceToken()
    {
        return spaceToken;
    }


    public boolean getForceFileRelease() {
       return forceFileRelease;
    }

    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return this.gUser.getDn();
    }

}
