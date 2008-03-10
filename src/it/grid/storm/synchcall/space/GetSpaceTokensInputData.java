/**
 * GetSpaceTokens request input data.
 *
 * @author  Alberto Forti
 * @author  CNAF -INFN Bologna
 * @date    November 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.space;

import it.grid.storm.griduser.VomsGridUser;

public class GetSpaceTokensInputData
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
