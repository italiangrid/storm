package it.grid.storm.authz.sa;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;

import it.grid.storm.authz.*;
import it.grid.storm.authz.sa.model.AuthzDB;
import it.grid.storm.authz.sa.conf.FileAuthzDBParser;
import it.grid.storm.authz.sa.conf.FileAuthzDBReader;
import org.apache.commons.logging.Log;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;

public class SpaceAuthz implements SpaceAuthzInterface {

    private final Log log = AuthzDirector.getLogger();

    private AuthzDBInterface authzDB;


    private FileAuthzDBParser authzDBParser;
    private FileAuthzDBReader authzDBReader;
    /**
     * @todo: 1) IMPLEMENT AUHTZ ENGINE
     * @todo: 2) IMPLEMENT CACHE
     * @todo: 3) IMPLEMENT PRINCIPAL LIST PERSISTENCE
     * @todo: 4) IMPLEMENT RECALCULATE CACHE
     */

    public SpaceAuthz(String authzDBname,  boolean autoreload,
                      int refreshTimeInSec, boolean verbose) throws AuthzDBReaderException {
        //Reader
        authzDBReader = new FileAuthzDBReader(authzDBname, verbose);
        authzDBReader.setAutomaticReload(autoreload,refreshTimeInSec);
        authzDBReader.bindDB(authzDBname);
        authzDBReader.loadAuthZDB();
        //Parser
        authzDBParser = new FileAuthzDBParser(this, authzDBReader, verbose);
        //Retrieve from the parser a new instance of AuthzDB
        this.authzDB = authzDBParser.getAuthzDB();
        log.debug("Space Authz Built for '"+authzDBname+"'");
    }

    public SpaceAuthz(AuthzDBInterface authzDB) {
        this.authzDB = authzDB;
    }

    public void setAuthzDB(AuthzDBInterface authzDB) {
        this.authzDB = authzDB;
    }

    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp) {
      return true;
    }

    public void refreshAuthzDB() {
        this.authzDB = authzDBParser.getAuthzDB();
    }

    public void setAuthzDB() {
    }


}
