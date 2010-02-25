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

    public String getSpaceAuthzID() {
        return FIXED_ID;
    }

    public void refresh() {

    }

}
