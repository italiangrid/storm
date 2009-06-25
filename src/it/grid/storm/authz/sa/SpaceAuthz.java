package it.grid.storm.authz.sa;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

import org.slf4j.Logger;

public abstract class SpaceAuthz implements SpaceAuthzInterface {

    private final Logger log = AuthzDirector.getLogger();

    private AuthzDBInterface authzDB;


    /**
     * @todo: 1) IMPLEMENT AUHTZ ENGINE
     * @todo: 2) IMPLEMENT CACHE
     * @todo: 3) IMPLEMENT PRINCIPAL LIST PERSISTENCE
     * @todo: 4) IMPLEMENT RECALCULATE CACHE
     */


    public SpaceAuthz() {
        super();
    }


    /*
     * 
     */
    public abstract boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp);

    /*
     * 
     */
    public void setAuthzDB(AuthzDBInterface authzDB) {
        this.authzDB = authzDB;
    }


}
