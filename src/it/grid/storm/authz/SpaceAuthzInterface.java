package it.grid.storm.authz;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.authz.sa.AuthzDBInterface;

public interface SpaceAuthzInterface {
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp);
    void setAuthzDB(AuthzDBInterface authzDB);
    void refreshAuthzDB();

}
