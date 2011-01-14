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

package it.grid.storm.authz.sa.test;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

import org.slf4j.Logger;

public class MockSpaceAuthz implements SpaceAuthzInterface {

    private static final String MOCK_ID = "mock-space-authz";
    private final Logger log = AuthzDirector.getLogger();

    public MockSpaceAuthz() {
    }

    /**
     * authorize
     * 
     * @param guser GridUserInterface
     * @param srmSpaceOp SRMSpaceRequest
     * @return boolean
     */
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
        log.debug("MOCK Space Authz : Authorize = Always TRUE");
        return true;
    }

    /**
     * setAuthzDB
     * 
     * @param authzDB AuthzDBInterface
     */
    public void setAuthzDB(AuthzDBInterface authzDB) {
        log.debug("MOCK Space Authz : Set Authz DB :D ");
    }

    public void refresh() {
        log.debug("MOCK Space Authz : Refresh DB : ;) ");
    }

    public String getSpaceAuthzID() {
        return MOCK_ID;
    }
}
