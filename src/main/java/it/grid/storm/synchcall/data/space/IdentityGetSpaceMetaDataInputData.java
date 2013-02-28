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
import it.grid.storm.synchcall.data.IdentityInputData;

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

public class IdentityGetSpaceMetaDataInputData extends AnonymousGetSpaceMetaDataInputData implements Serializable, IdentityInputData {

    /**
     * 
     */
    private static final long serialVersionUID = -7823169083758886055L;
    private final GridUserInterface auth;
    
    public IdentityGetSpaceMetaDataInputData(GridUserInterface auth, ArrayOfTSpaceToken tokenArray)
        throws IllegalArgumentException
    {

        super(tokenArray);
        if (auth == null)
        {
            throw new IllegalArgumentException("Unable to create the object, invalid arguments: auth=" + auth);
        }
        this.auth = auth;
    }

    @Override
    public GridUserInterface getUser()
    {
        return auth;
    }

    @Override
    public String getPrincipal()
    {
        return this.auth.getDn();
    }
}
