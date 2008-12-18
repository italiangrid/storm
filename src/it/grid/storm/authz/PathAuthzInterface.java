package it.grid.storm.authz;

import it.grid.storm.authz.sa.model.SRMSpaceRequest;
import it.grid.storm.griduser.GridUserInterface;

public interface PathAuthzInterface {
    
    public boolean authorize(GridUserInterface guser, SRMSpaceRequest srmSpaceOp);
    
}
