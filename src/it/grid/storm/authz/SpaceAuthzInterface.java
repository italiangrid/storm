package it.grid.storm.authz;

import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

public interface SpaceAuthzInterface {

    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp);

    void setAuthzDB(AuthzDBInterface authzDB);

    void refresh();

    public String getSpaceAuthzID();

}
