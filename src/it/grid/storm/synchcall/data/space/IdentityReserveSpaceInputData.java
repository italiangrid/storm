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
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TRetentionPolicyInfo;
import it.grid.storm.synchcall.data.IdentityInputData;

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

public class IdentityReserveSpaceInputData extends AnonymousReserveSpaceInputData implements Serializable, IdentityInputData
{
    private static final long serialVersionUID = 2840674835389671669L;
    private final GridUserInterface auth;

    public IdentityReserveSpaceInputData(GridUserInterface auth, String spaceTokenAlias,
            TRetentionPolicyInfo retentionPolicyInfo, TSizeInBytes spaceDesired, TSizeInBytes spaceGuaranteed,
            ArrayOfTExtraInfo storageSystemInfo) throws IllegalArgumentException
    {
        super(spaceTokenAlias, retentionPolicyInfo, spaceDesired, spaceGuaranteed, storageSystemInfo);
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
