package it.grid.storm.authz;

import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;

public interface PathAuthzInterface {

    public AuthorizationDecision authorize(GridUserInterface guser, SRMFileRequest srmSpaceOp, StoRI stori);

}
