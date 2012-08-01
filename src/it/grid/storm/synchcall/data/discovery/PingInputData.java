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

package it.grid.storm.synchcall.data.discovery;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.InputData;

/**
 * This class is part of the StoRM project.
 * This class represents the Ping Input Data
 * 
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 *
 * @author lucamag
 * @author Alberto Forti
 * @date May 28, 2008
 *
 */

public class PingInputData extends AbstractInputData
{
    private GridUserInterface requestor = null;
    private String authorizationID = null;

    public PingInputData() {
        authorizationID = "";
    }

    public PingInputData(GridUserInterface gridUser, String authorizationID) {
        this.requestor = gridUser;
        if (authorizationID == null)
            this.authorizationID = "";
        else
            this.authorizationID = authorizationID;
    }

    /**
     * Set the Requestor
     * @param gridUser GridUserInterface
     */
    public void setRequestor(GridUserInterface gridUser) {
      this.requestor = gridUser;
    }

    /**
     * Get the Requestor
     * @return GridUserInterface
     */
    public GridUserInterface getRequestor() {
      return this.requestor;
    }

    /**
     * Set the authorizationID.
     * @param authorizationID String
     */
    public void setAuthorizationID(String authorizationID) {
        this.authorizationID = authorizationID;
    }

    /**
     * Get the authorizatioID.
     * @return String
     */
    public String getAuthorizationID() {
        return this.authorizationID;
    }

    public String toString() {
      return "";
    }

    @Override
    public Boolean hasPrincipal()
    {
        return Boolean.TRUE;
    }

    @Override
    public String getPrincipal()
    {
        return this.requestor.getDn();
    }
}
