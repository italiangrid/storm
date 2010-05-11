/**************************************************************************
 * This file is part of the StoRM project. Copyright (c) 2003-2009 INFN. All rights reserved. Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in
 * writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 ***********************************************************************/
package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.common.types.StFN;
import it.grid.storm.namespace.naming.NamespaceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Collections;

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

    /**
     * @param subjectGroup
     * @return List<PathACE> : the list contains all the ACE compatible with the requestor subject
     */
    private List<PathACE> getCompatibleACE(String subjectGroup) {
        log.debug("<BestMatch>-compatibleACE: subject='" + subjectGroup + "'");
        ArrayList<PathACE> compatibleACE = new ArrayList<PathACE>();
        if ((pathACL != null) || (!(pathACL.isEmpty()))) {
            for (PathACE pathACE : pathACL) {
                if (pathACE.subjectMatch(subjectGroup)) {
                    log.trace("<BestMatch>-compatibleACE: ACE:'" + pathACE + "' match with subject='" + subjectGroup
                            + "'");
                    compatibleACE.add(pathACE);
                } else {
                    log.trace("<BestMatch>-compatibleACE: ACE:'" + pathACE + "' DOESN'T match with subject='"
                            + subjectGroup + "'");
                }
            }
        } else {
            log.debug("<BestMatch>-compatibleACE: ACL db is empty");
        }
        return compatibleACE;
    }

    /**
     * @param fileName
     * @param compatibleACE
     * @return null if there are not ACE.
     */
    private List<OrderedACE> getOrderedACEs(StFN fileName, List<PathACE> compatibleACE) {
        ArrayList<OrderedACE> bestACEs = new ArrayList<OrderedACE>();
        int distance = 0;
        for (PathACE pathAce : compatibleACE) {
            // Compute distance from Resource target (fileName) and Resource into ACE
            StFN aceStFN = pathAce.getStorageFileName();
            distance = NamespaceUtil.computeDistanceFromPath(aceStFN.getValue(), fileName.getValue());
            bestACEs.add(new OrderedACE(pathAce, distance));

        } // End of cycle
        // Sort the BestACE in base of distance
        Collections.sort(bestACEs);
        return bestACEs;
    }

    @Override
    public String getDescription() {
        String description = "< Best Match Path Authorization Algorithm >";
        return description;
    }

    /**
     * 
     */
    @Override
    public AuthzDecision evaluate(String subject, StFN fileName, SRMFileRequest pathOperation) {
        AuthzDecision result = AuthzDecision.INDETERMINATE;

        // Retrieve the list of compatible ACE
        List<PathACE> compACE = getCompatibleACE(subject);
        // if noone ACE is compatible with the requestor subject
        if ((compACE == null) || (compACE.isEmpty())) {
            return AuthzDecision.NOT_APPLICABLE;
        }

        // Retrieve the best ACE within compatible ones.
        List<OrderedACE> orderedACEs = getOrderedACEs(fileName, compACE);
        log.debug("There are '" + orderedACEs.size() + "' ACEs regarding the subject '" + subject + "'");
        // Print the ACE to evaluate
        int count = 0;
        for (OrderedACE oAce : orderedACEs) {
            log.debug("ACE[" + count + "]=" + oAce.ace + " Distance:" + oAce.distance);
            count++;
        }

        // Retrieve the list of Path Operation needed to authorize the SRM request
        PathAccessMask requestedOps = pathOperation.getSRMPathAccessMask();
        ArrayList<PathOperation> ops = new ArrayList<PathOperation>(requestedOps.getPathOperations());
        log.trace("<Best-Match> Operation to authorize: " + ops);
        HashMap<PathOperation, AuthzDecision> decision = new HashMap<PathOperation, AuthzDecision>();

        String explanation = "Operations to authorize to '" + subject + "' are :" + ops + "\n";
        // Check iterativly every needed Path Operation
        for (PathOperation op : ops) {
            explanation += " op('" + op + "') is ";
            for (OrderedACE oAce : orderedACEs) {
                if (oAce.ace.getPathAccessMask().containsPathOperation(op)) {
                    if (oAce.ace.isPermitAce()) {
                        // Path Operation is PERMIT
                        explanation += "PERMIT, thanks to ACE: '" + oAce + "'\n";
                        log.trace("Path Operation '" + op + "' is PERMIT");
                        decision.put(op, AuthzDecision.PERMIT);
                        break;
                    } else {
                        // Path Operation is DENY
                        explanation += "DENY, thanks to ACE: '" + oAce + "'\n";
                        log.trace("Path Operation '" + op + "' is DENY");
                        decision.put(op, AuthzDecision.DENY);
                        break;
                    }
                }
            }
            if (!(decision.containsKey(op))) {
                decision.put(op, AuthzDecision.INDETERMINATE);
            }
        }

        // Print the decision
        log.debug("Decision explanation : \n --------------" + explanation + "--------------");

        // Make the final results
        // - PERMIT if and only if ALL the permissions are PERMIT
        // - DENY if there is at least one DENY
        // - INDETERMINATE if there is at lease one INDETERMINATE
        if (decision.containsValue(AuthzDecision.DENY)) {
            result = AuthzDecision.DENY;
        } else if (decision.containsValue(AuthzDecision.INDETERMINATE)) {
            result = AuthzDecision.INDETERMINATE;
        } else {
            result = AuthzDecision.PERMIT;
        }
        return result;

    }

    // =========================================

    /**
     * @author ritz
     */
    private class OrderedACE implements Comparable<OrderedACE> {

        private final PathACE ace;
        private final int distance;

        OrderedACE(PathACE ace, int distance) {
            this.ace = ace;
            this.distance = distance;
        }

        // @Override
        public int compareTo(OrderedACE other) {
            int result = -1;
            OrderedACE otherACE = other;
            if (distance < otherACE.distance) {
                result = -1;
            } else if (distance == otherACE.distance) {
                result = 0;
            } else {
                result = 1;
            }
            return result;
        }

        @Override
        public boolean equals(Object other) {
            boolean result = false;
            if (other instanceof OrderedACE) {
                OrderedACE otherACE = (OrderedACE) other;
                if (distance == otherACE.distance) {
                    result = true;
                }
            }
            return result;
        }

        @Override
        public String toString() {
            return "[" + ace.toString() + "]  distance:" + distance;
        }

    }

}
