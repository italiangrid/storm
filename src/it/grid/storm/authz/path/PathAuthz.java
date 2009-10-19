/**
 * 
 */
package it.grid.storm.authz.path;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.PathAuthzInterface;
import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.model.PathAuthzEvaluationAlgorithm;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.StFN;
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
    private final PathAuthzEvaluationAlgorithm authz;

    public PathAuthz(PathAuthzDB pathAuthzDB) {
        this.pathAuthzDB = pathAuthzDB;
        authz = pathAuthzDB.getAuthorizationAlgorithm();
    }

    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest pathOperation, StoRI stori) {

        AuthzDecision result = AuthzDecision.INDETERMINATE;

        // Retrieve the local group of Requestor
        String groupName = null;
        try {
            int localGroup = guser.getLocalUser().getPrimaryGid();
            EtcGroupReader.getGroupName(localGroup);
        } catch (CannotMapUserException e) {
            log.error("Unable to retrieve the local group for '" + guser + "'");
            groupName = "unknown";
        }

        // Retrieve the StFN from StoRI
        StFN fileName = stori.getStFN();

        result = authz.evaluate(groupName, fileName, pathOperation);

        return result;

    }

    /**
     * Method used to test
     * 
     * @param groupName
     * @param pathOperation
     * @param filename
     * @return
     */
    public AuthzDecision authorizeTest(String groupName, SRMFileRequest pathOperation, StFN filename) {

        AuthzDecision result = AuthzDecision.INDETERMINATE;
        // log.debug("Path Authz DB: " + pathAuthzDB);
        result = authz.evaluate(groupName, filename, pathOperation);

        return result;

    }

}
