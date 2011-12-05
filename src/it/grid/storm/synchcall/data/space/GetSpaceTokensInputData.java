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

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.AbstractInputData;

/**
 * This class is part of the StoRM project.
 * Copyright: Copyright (c) 2008 
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * GetSpaceTokens request input data.
 *
 * @author lucamag
 * @author Alberto Forti
 * @date May 29, 2008
 *
 */


public class GetSpaceTokensInputData extends AbstractInputData
{
    private GridUserInterface auth = null;
    private String spaceTokenAlias = null;

    public GetSpaceTokensInputData() {}

    public GetSpaceTokensInputData(GridUserInterface auth, String spaceTokenAlias)
    {
        this.auth = auth;
        this.spaceTokenAlias = spaceTokenAlias;

    }

    /**
     * Returns VomsGridUser specified in SRM request.
     */

    public GridUserInterface getUser()
    {
        return auth;
    }

    /**
     * Sets VomsGridUser
     */
    public void setUser(GridUserInterface user)
    {
        this.auth = user;
    }

    /**
     * Returns spaceTokenAlias
     */
    public String getSpaceTokenAlias()
    {
        return spaceTokenAlias;
    }

    /**
     * Sets spaceTokenAlias
     */
    public void setSpaceTokenAlias(String spaceTokenAlias)
    {
        this.spaceTokenAlias = spaceTokenAlias;
    }
}
