package it.grid.storm.synchcall.data.space;

import it.grid.storm.griduser.VomsGridUser;
import it.grid.storm.synchcall.data.InputData;

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


public class GetSpaceTokensInputData implements InputData
{
    private VomsGridUser auth = null;
    private String spaceTokenAlias = null;

    public GetSpaceTokensInputData() {}

    public GetSpaceTokensInputData(VomsGridUser auth, String spaceTokenAlias)
    {
        this.auth = auth;
        this.spaceTokenAlias = spaceTokenAlias;

    }

    /**
     * Returns VomsGridUser specified in SRM request.
     */

    public VomsGridUser getUser()
    {
        return auth;
    }

    /**
     * Sets VomsGridUser
     */
    public void setUser(VomsGridUser user)
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
