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
    private PathAuthzEvaluationAlgorithm authz;

    public PathAuthz(PathAuthzDB pathAuthzDB) {
        this.pathAuthzDB = pathAuthzDB;
        authz = pathAuthzDB.getAuthorizationAlgorithm();
        authz.setACL(pathAuthzDB.getACL());
    }

    public void setAuthzDB(PathAuthzDB pathAuthzDB) {
        this.pathAuthzDB = pathAuthzDB;
        authz = pathAuthzDB.getAuthorizationAlgorithm();
        authz.setACL(pathAuthzDB.getACL());
    }

    public void refresh() {
        authz = pathAuthzDB.getAuthorizationAlgorithm();
        authz.setACL(pathAuthzDB.getACL());
        // Eventually, clear the cache
        // @todo
    }

    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest pathOperation, StoRI stori) {

        AuthzDecision result = AuthzDecision.INDETERMINATE;

        // Retrieve the local group of Requestor
        String groupName = null;
        try {
            int localGroup = guser.getLocalUser().getPrimaryGid();
            groupName = EtcGroupReader.getGroupName(localGroup);
        } catch (CannotMapUserException e) {
            log.error("Unable to retrieve the local group for '" + guser + "'");
            groupName = "unknown";
        }

        // Retrieve the StFN from StoRI
        StFN fileName = stori.getStFN();
        log.debug("<PathAuthz> Compute authorization for groupName:'" + groupName + "', filename:'" + fileName
                + "', pathOperation:'" + pathOperation + "'");

        result = authz.evaluate(groupName, fileName, pathOperation);

        log.info("<PathAuthz>: " + groupName + " is " + result + " to perform " + pathOperation + " on " + fileName);
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

    /*
     */
    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest pathOperation, StoRI storiSource,
            StoRI storiDest) {

        AuthzDecision result = AuthzDecision.INDETERMINATE;

        // Retrieve the local group of Requestor
        String groupName = null;
        try {
            int localGroup = guser.getLocalUser().getPrimaryGid();
            groupName = EtcGroupReader.getGroupName(localGroup);
        } catch (CannotMapUserException e) {
            log.error("Unable to retrieve the local group for '" + guser + "'");
            groupName = "unknown";
        }

        // Retrieve the StFN source from StoRI
        StFN sourceFileName = storiSource.getStFN();
        // Retrieve the StFN destination from StoRI
        StFN destFileName = storiDest.getStFN();

        switch (pathOperation) {
            case CPfrom:
                AuthzDecision fromDec = authz.evaluate(groupName, sourceFileName, SRMFileRequest.CPfrom);
                AuthzDecision toDec = authz.evaluate(groupName, destFileName, SRMFileRequest.CPto);
                if (fromDec.equals(AuthzDecision.PERMIT) && (toDec.equals(AuthzDecision.PERMIT))) {
                    result = AuthzDecision.PERMIT;
                } else if (fromDec.equals(AuthzDecision.DENY) || (toDec.equals(AuthzDecision.DENY))) {
                    result = AuthzDecision.DENY;
                } else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE) || (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
                    result = AuthzDecision.NOT_APPLICABLE;
                }
                break;
            case CPto:
                fromDec = authz.evaluate(groupName, sourceFileName, SRMFileRequest.CPfrom);
                toDec = authz.evaluate(groupName, destFileName, SRMFileRequest.CPto);
                if (fromDec.equals(AuthzDecision.PERMIT) && (toDec.equals(AuthzDecision.PERMIT))) {
                    result = AuthzDecision.PERMIT;
                } else if (fromDec.equals(AuthzDecision.DENY) || (toDec.equals(AuthzDecision.DENY))) {
                    result = AuthzDecision.DENY;
                } else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE) || (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
                    result = AuthzDecision.NOT_APPLICABLE;
                }
                break;
            case MV_dest:
                fromDec = authz.evaluate(groupName, sourceFileName, SRMFileRequest.MV_source);
                toDec = authz.evaluate(groupName, destFileName, SRMFileRequest.MV_dest);
                if (fromDec.equals(AuthzDecision.PERMIT) && (toDec.equals(AuthzDecision.PERMIT))) {
                    result = AuthzDecision.PERMIT;
                } else if (fromDec.equals(AuthzDecision.DENY) || (toDec.equals(AuthzDecision.DENY))) {
                    result = AuthzDecision.DENY;
                } else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE) || (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
                    result = AuthzDecision.NOT_APPLICABLE;
                }
                break;
            case MV_source:
                fromDec = authz.evaluate(groupName, sourceFileName, SRMFileRequest.MV_source);
                toDec = authz.evaluate(groupName, destFileName, SRMFileRequest.MV_dest);
                if (fromDec.equals(AuthzDecision.PERMIT) && (toDec.equals(AuthzDecision.PERMIT))) {
                    result = AuthzDecision.PERMIT;
                } else if (fromDec.equals(AuthzDecision.DENY) || (toDec.equals(AuthzDecision.DENY))) {
                    result = AuthzDecision.DENY;
                } else if (fromDec.equals(AuthzDecision.NOT_APPLICABLE) || (toDec.equals(AuthzDecision.NOT_APPLICABLE))) {
                    result = AuthzDecision.NOT_APPLICABLE;
                }
                break;
            default:
                break;
        }
        return result;
    }

}
