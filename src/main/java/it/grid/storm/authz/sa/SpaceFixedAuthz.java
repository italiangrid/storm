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

/**
 * 
 */
package it.grid.storm.authz.sa;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.model.AuthzDBFixed;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

/**
 * @author zappi
 */
public class SpaceFixedAuthz extends SpaceAuthz {

    private static final String FIXED_ID = "fixed-space-authz";

    public SpaceFixedAuthz(AuthzDBFixed fixedAuthzDB) throws AuthzDBReaderException {

    }

    @Override
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
        // @todo : implement the simple algorithm.
        return true;
    }
    
    @Override
    public boolean authorizeAnonymous(SRMSpaceRequest srmSpaceOp)
    {
        // TODO Auto-generated method stub
        return true;
    }

    public String getSpaceAuthzID() {
        return FIXED_ID;
    }

    public void refresh() {

    }

}
