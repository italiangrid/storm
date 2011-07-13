/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
import it.grid.storm.namespace.util.userinfo.LocalGroups;

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
        return authorize(guser, pathOperation, stori.getStFN()); 
    }
    
    public AuthzDecision authorize(GridUserInterface guser, SRMFileRequest pathOperation, StFN fileStFN) {

        AuthzDecision result = AuthzDecision.INDETERMINATE;

        // Retrieve the local group of Requestor
        String groupName = null;
        try {
            int localGroup = guser.getLocalUser().getPrimaryGid();
            groupName = LocalGroups.getGroupName(localGroup);
        } catch (CannotMapUserException e) {
            log.error("Unable to retrieve the local group for '" + guser + "'");
            groupName = "unknown";
        }

        log.debug("<PathAuthz> Compute authorization for groupName:'" + groupName + "', filename:'" + fileStFN
                + "', pathOperation:'" + pathOperation + "'");

        result = authz.evaluate(groupName, fileStFN, pathOperation);

        log.info("<PathAuthz>: " + groupName + " is " + result + " to perform " + pathOperation + " on " + fileStFN);
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
            groupName = LocalGroups.getGroupName(localGroup);
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
