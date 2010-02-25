package it.grid.storm.authz;

import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;

public interface PathAuthzInterface {

    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest srmPathOp, StoRI stori);

    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest srmPathOp, StoRI storiSource, StoRI storiDest);

    void setAuthzDB(PathAuthzDB pathAuthzDB);

    void refresh();

}
