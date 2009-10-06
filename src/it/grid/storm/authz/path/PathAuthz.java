/**
 * 
 */
package it.grid.storm.authz.path;

import it.grid.storm.authz.PathAuthzInterface;
import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;

/**
 * @author zappi
 *
 */
public class PathAuthz implements PathAuthzInterface {

    private PathAuthzDB pathAuthzDB = PathAuthzDB.makeEmpty();

    public PathAuthz() {
    }

    public PathAuthz(PathAuthzDB pathAuthzDB) {
        this.pathAuthzDB = pathAuthzDB;
    }
    
    
    
    
    /*
     * (non-Javadoc)
     * @see it.grid.storm.authz.PathAuthzInterface#authorize(it.grid.storm.griduser.GridUserInterface,
     * it.grid.storm.authz.path.model.SRMFileRequest, it.grid.storm.namespace.StoRI)
     */
    public boolean authorize(GridUserInterface guser, SRMFileRequest srmSpaceOp, StoRI stori) {
        // TODO Auto-generated method stub
        return false;
    }

    
}
