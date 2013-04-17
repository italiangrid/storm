/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.authz.path.model;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.common.types.StFN;
import it.grid.storm.namespace.naming.NamespaceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Collections;
import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathAuthzAlgBestMatch extends PathAuthzEvaluationAlgorithm {

	public static PathAuthzEvaluationAlgorithm getInstance() {

		if (instance == null) {
			instance = new PathAuthzAlgBestMatch();
		}
		return instance;
	}

	private PathAuthzAlgBestMatch() {

	}

	protected static final Logger log = AuthzDirector.getLogger();

	@Override
	public String getDescription() {

		String description = "< Best Match Path Authorization Algorithm >";
		return description;
	}

	/**
     * 
     */
	@Override
	public AuthzDecision evaluate(String subject, StFN fileName,
		SRMFileRequest pathOperation, List<PathACE> acl) {

		AuthzDecision result = AuthzDecision.INDETERMINATE;

		List<PathACE> compACE = getCompatibleACE(subject, acl);
		if ((compACE == null) || (compACE.isEmpty())) {
			return AuthzDecision.NOT_APPLICABLE;
		}

		List<OrderedACE> orderedACEs = getOrderedACEs(fileName, compACE);
		log.debug("There are '" + orderedACEs.size()
			+ "' ACEs regarding the subject '" + subject + "'");

		// Retrieve the list of Path Operation needed to authorize the SRM request
		PathAccessMask requestedOps = pathOperation.getSRMPathAccessMask();
		ArrayList<PathOperation> ops = new ArrayList<PathOperation>(
			requestedOps.getPathOperations());
		log.trace("<Best-Match> Operation to authorize: " + ops);
		HashMap<PathOperation, AuthzDecision> decision = new HashMap<PathOperation, AuthzDecision>();

		String explanation = "Operations to authorize to '" + subject + "' are :"
			+ ops + "\n";
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
		log.debug("Decision explanation : \n --------------" + explanation
			+ "--------------");

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

	public AuthzDecision evaluate(String subject, StFN fileName,
		PathOperation op, List<PathACE> acl) {

		// Retrieve the list of compatible ACE
		List<PathACE> compACE = getCompatibleACE(subject, acl);
		// if noone ACE is compatible with the requestor subject
		if ((compACE == null) || (compACE.isEmpty())) {
			return AuthzDecision.NOT_APPLICABLE;
		}

		// Retrieve the best ACE within compatible ones.
		List<OrderedACE> orderedACEs = getOrderedACEs(fileName, compACE);
		log.debug("There are '" + orderedACEs.size()
			+ "' ACEs regarding the subject '" + subject + "'");

		log.trace("<Best-Match> Operation to authorize to '" + subject + "' is : "
			+ op);

		for (OrderedACE oAce : orderedACEs) {
			if (oAce.ace.getPathAccessMask().containsPathOperation(op)) {
				if (oAce.ace.isPermitAce()) {
					log.trace("Path Operation '" + op + "' is PERMIT");
					return AuthzDecision.PERMIT;
				} else {
					log.trace("Path Operation '" + op + "' is DENY");
					return AuthzDecision.DENY;
				}
			}
		}
		return AuthzDecision.INDETERMINATE;
	}

	@Override
	public AuthzDecision evaluateAnonymous(StFN fileName,
		PathOperation pathOperation, LinkedList<PathACE> authzDB) {

		if ((authzDB == null) || (authzDB.isEmpty())) {
			return AuthzDecision.NOT_APPLICABLE;
		}

		// Retrieve the best ACE within compatible ones.
		List<OrderedACE> orderedACEs = getOrderedACEs(fileName, authzDB);
		log.debug("There are '" + orderedACEs.size() + "' ACEs regarding file '"
			+ fileName + "'");

		log.trace("<Best-Match> Operation that needs anonymous authorization is : "
			+ pathOperation);

		for (OrderedACE oAce : orderedACEs) {
			if (oAce.ace.isAllGroupsACE()
				&& oAce.ace.getPathAccessMask().containsPathOperation(pathOperation)) {
				if (oAce.ace.isPermitAce()) {
					log.trace("Path Operation '" + pathOperation + "' is PERMIT");
					return AuthzDecision.PERMIT;
				} else {
					log.trace("Path Operation '" + pathOperation + "' is DENY");
					return AuthzDecision.DENY;
				}
			}
		}
		return AuthzDecision.INDETERMINATE;
	}

	@Override
	public AuthzDecision evaluateAnonymous(StFN fileName,
		SRMFileRequest pathOperation, LinkedList<PathACE> authzDB) {

		if ((authzDB == null) || (authzDB.isEmpty())) {
			return AuthzDecision.NOT_APPLICABLE;
		}

		// Retrieve the best ACE within compatible ones.
		List<OrderedACE> orderedACEs = getOrderedACEs(fileName, authzDB);
		log.debug("There are '" + orderedACEs.size() + "' ACEs regarding file '"
			+ fileName + "'");

		log.trace("<Best-Match> Operation that needs anonymous authorization is : "
			+ pathOperation);
		PathAccessMask requestedOps = pathOperation.getSRMPathAccessMask();
		ArrayList<PathOperation> ops = new ArrayList<PathOperation>(
			requestedOps.getPathOperations());
		HashMap<PathOperation, AuthzDecision> decision = new HashMap<PathOperation, AuthzDecision>();
		for (PathOperation op : ops) {
			for (OrderedACE oAce : orderedACEs) {
				if (oAce.ace.isAllGroupsACE()
					&& oAce.ace.getPathAccessMask().containsPathOperation(op)) {
					if (oAce.ace.isPermitAce()) {
						log.trace("Path Operation '" + pathOperation + "' is PERMIT");
						decision.put(op, AuthzDecision.PERMIT);
					} else {
						log.trace("Path Operation '" + pathOperation + "' is DENY");
						decision.put(op, AuthzDecision.DENY);
					}
				}
			}
		}
		AuthzDecision result;
		if (decision.containsValue(AuthzDecision.DENY)) {
			result = AuthzDecision.DENY;
		} else if (decision.containsValue(AuthzDecision.INDETERMINATE)) {
			result = AuthzDecision.INDETERMINATE;
		} else {
			result = AuthzDecision.PERMIT;
		}
		return result;
	}

	/**
	 * @param subjectGroup
	 * @return List<PathACE> : the list contains all the ACE compatible with the
	 *         requestor subject
	 */
	private List<PathACE> getCompatibleACE(String subjectGroup, List<PathACE> acl) {

		log.debug("<BestMatch>-compatibleACE: subject='" + subjectGroup + "'");
		ArrayList<PathACE> compatibleACE = new ArrayList<PathACE>();
		if ((acl != null) && (!(acl.isEmpty()))) {
			for (PathACE pathACE : acl) {
				if (pathACE.subjectMatch(subjectGroup)) {
					log.trace("<BestMatch>-compatibleACE: ACE:'" + pathACE
						+ "' match with subject='" + subjectGroup + "'");
					compatibleACE.add(pathACE);
				} else {
					log.trace("<BestMatch>-compatibleACE: ACE:'" + pathACE
						+ "' DOESN'T match with subject='" + subjectGroup + "'");
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
	private List<OrderedACE> getOrderedACEs(StFN fileName,
		List<PathACE> compatibleACE) {

		ArrayList<OrderedACE> bestACEs = new ArrayList<OrderedACE>();
		int distance = 0;
		for (PathACE pathAce : compatibleACE) {
			// Compute distance from Resource target (fileName) and Resource into ACE
			StFN aceStFN = pathAce.getStorageFileName();
			distance = NamespaceUtil.computeDistanceFromPath(aceStFN.getValue(),
				fileName.getValue());
			bestACEs.add(new OrderedACE(pathAce, distance));

		} // End of cycle
			// Sort the BestACE in base of distance
		Collections.sort(bestACEs);
		return bestACEs;
	}

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
