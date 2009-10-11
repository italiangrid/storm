/**************************************************************************
 * This file is part of the StoRM project. Copyright (c) 2003-2009 INFN. All rights reserved. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************/
package it.grid.storm.authz.path.model;

import it.grid.storm.authorization.AuthorizationDecision;
import it.grid.storm.common.types.StFN;
import it.grid.storm.namespace.naming.NamespaceUtil;

/**
 * @author zappi
 */
public class PathAuthzAlgBestMatch extends PathAuthzEvaluationAlgorithm {

    /**
     * 
     */
    public PathAuthzAlgBestMatch() {
        super();
        log.debug("Path Authorization Algorithm : Best Match evaluator");
    }

    @Override
    public AuthorizationDecision evaluate(StFN fileName, PathOperation pathOperation) {
        AuthorizationDecision result = AuthorizationDecision.Indeterminate;
        PathACE bestACE = null;
        int minorDistance = Integer.MAX_VALUE;
        for (PathACE pathAce : pathACL) {
            // Compute distance from Resource target (fileName) and Resource into ACE
            StFN aceStFN = pathAce.getStorageFileName();
            int d = NamespaceUtil.computeDistanceFromPath(aceStFN.getValue(), fileName.getValue());
            if (d >= 0) { // Something is compatible in path
                if (d < minorDistance) { // Found a possible ACE candidate!
                    minorDistance = d;
                    bestACE = pathAce;
                }
            }
        } // End of cycle
        if (bestACE != null) { // There is at least one ACE with resource compatible
            if (bestACE.getPathAccessMask().containsPathOperation(pathOperation)) {
                if (bestACE.isPermitAce()) {
                    result = AuthorizationDecision.Permit;
                } else {
                    result = AuthorizationDecision.Deny;
                }
            }
        } else {
            log.debug("Authz Decision is " + result);
        }
        return result;
    }

    @Override
    public String getDescription() {
        String description = "< Best Match Path Authorization Algorithm >";
        return description;
    }

}
