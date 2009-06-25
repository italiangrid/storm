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
 *
 */
public class SpaceFixedAuthz extends SpaceAuthz {

    public SpaceFixedAuthz(AuthzDBFixed fixedAuthzDB) throws AuthzDBReaderException {

    }
    
    ////////TEMPORARY FIX 
    //////// THIS METHOD HAS BEEN ADDED TO MAKE EVERYTHING COMPILE
    /* (non-Javadoc)
     * @see it.grid.storm.authz.sa.SpaceAuthz#authorize(it.grid.storm.griduser.GridUserInterface, it.grid.storm.authz.sa.model.SRMSpaceRequest)
     */
    @Override
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
        // TODO Auto-generated method stub
        return true;
    }

}
