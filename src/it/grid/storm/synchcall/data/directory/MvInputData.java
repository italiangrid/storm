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

package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTExtraInfo;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.AbstractInputData;
import it.grid.storm.synchcall.data.exception.InvalidMvInputAttributeException;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 *
 * This class represents the Mv Input Data associated with the SRM request, that is
 * it contains info about: ...,ecc.
 * @author lucamag
 * @date May 28, 2008
 *
 */
public class MvInputData extends AbstractInputData {
    private GridUserInterface auth = null;
    private TSURL fromSURL = null;
    private TSURL toSURL = null;

    public MvInputData() {
    }

    public MvInputData(GridUserInterface auth, TSURL fromSURL, TSURL toSURL)
            throws InvalidMvInputAttributeException 
    {
        boolean ok = (fromSURL != null)&&(toSURL != null);
        if (!ok) {
            throw new InvalidMvInputAttributeException(toSURL, fromSURL);
        }
        this.auth = auth;
        this.fromSURL = fromSURL;
        this.toSURL = toSURL;
    }

    /**
     * Method that get/set SURL specified in SRM request.
     */

    public TSURL getFromSurl() {
        return fromSURL;
    }

    public TSURL getToSurl() {
        return toSURL;
    }

    public void setSurlInfo(TSURL surl) {
        this.fromSURL = surl;
    }

    /**
     * Set User
     */
    public void setUser(GridUserInterface user) {
        this.auth = user;
    }

    /**
     * get User
     */
    public GridUserInterface getUser() {
        return this.auth;
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
