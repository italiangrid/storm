/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

/**
 * 
 */
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.authz.path.model.PathAuthzAlgBestMatch;
import it.grid.storm.authz.path.model.PathAuthzEvaluationAlgorithm;
import it.grid.storm.authz.path.model.PathOperation;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.StFN;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zappi
 */
public class PathAuthzDB {

	public final static String MOCK_ID = "mock-PathAuthzDB";

	private final PathAuthzEvaluationAlgorithm DEFAULT_ALGORITHM = PathAuthzAlgBestMatch
		.getInstance();

	private final String pathAuthzDBID;
	private final PathAuthzEvaluationAlgorithm evaluationAlg;
	private final LinkedList<PathACE> authzDB = new LinkedList<PathACE>();

	public PathAuthzDB(String pathAuthzDBID,
		PathAuthzEvaluationAlgorithm algorithm, List<PathACE> aces) {

		this.pathAuthzDBID = pathAuthzDBID;
		this.evaluationAlg = algorithm;
		this.authzDB.addAll(aces);
	}

	public PathAuthzDB(String pathAuthzDBID, List<PathACE> aces) {

		this.pathAuthzDBID = pathAuthzDBID;
		this.evaluationAlg = DEFAULT_ALGORITHM;
		this.authzDB.addAll(aces);
	}

	/**
	 * Empty constructor. Use it only if there is not
	 */
	public PathAuthzDB() {

		this.pathAuthzDBID = MOCK_ID;
		this.evaluationAlg = DEFAULT_ALGORITHM;
		this.authzDB.add(PathACE.PERMIT_ALL);
	}

	public void addPathACE(PathACE pathAce) {

		authzDB.add(pathAce);
	}

	public List<PathACE> getACL() {

		return authzDB;
	}

	public String getPathAuthzDBID() {

		return pathAuthzDBID;
	}

	@Override
	public String toString() {

		String result = "=== Path Authorizaton DataBase === \n";
		result += "path-authz.db Name: '" + pathAuthzDBID + "'\n";
		result += PathACE.ALGORITHM + "=" + this.evaluationAlg.getClass() + "\n \n";
		int count = 0;
		for (PathACE ace : authzDB) {
			result += "ace[" + count + "]: " + ace.toString() + "\n";
			count++;
		}
		return result;
	}

	public AuthzDecision evaluate(String groupName, StFN fileStFN,
		SRMFileRequest pathOperation) {

		return evaluationAlg.evaluate(groupName, fileStFN, pathOperation, authzDB);
	}

	public AuthzDecision evaluate(String groupName, StFN fileStFN,
		PathOperation pathOperation) {

		return evaluationAlg.evaluate(groupName, fileStFN, pathOperation, authzDB);
	}

	public AuthzDecision evaluateAnonymous(StFN fileStFN,
		PathOperation pathOperation) {

		return evaluationAlg.evaluateAnonymous(fileStFN, pathOperation, authzDB);
	}

	public AuthzDecision evaluateAnonymous(StFN fileStFN,
		SRMFileRequest pathOperation) {

		return evaluationAlg.evaluateAnonymous(fileStFN, pathOperation, authzDB);
	}

}
