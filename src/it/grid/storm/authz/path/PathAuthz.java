/**
 * 
 */
package it.grid.storm.authz.path;

import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.PathAuthzInterface;
import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.util.userinfo.EtcGroupReader;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathAuthz implements PathAuthzInterface {

    private final Logger log = AuthzDirector.getLogger();
    private PathAuthzDB pathAuthzDB = PathAuthzDB.makeEmpty();

    public PathAuthz(PathAuthzDB pathAuthzDB) {
        this.pathAuthzDB = pathAuthzDB;
    }

    public AuthorizationDecision authorize(GridUserInterface guser, SRMFileRequest srmSpaceOp, StoRI stori) {

        AuthorizationDecision result = AuthorizationDecision.Indeterminate;

        // Retrieve the local group of Requestor
        String groupName = null;
        try {
            int localGroup = guser.getLocalUser().getPrimaryGid();
            EtcGroupReader.getGroupName(localGroup);
        } catch (CannotMapUserException e) {
            log.error("Unable to retrieve the local group for '" + guser + "'");
            groupName = "unknown";
        }

        return result;

    }

}
