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
