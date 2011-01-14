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
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.authz.path.model.PathAuthzAlgBestMatch;
import it.grid.storm.authz.path.model.PathAuthzEvaluationAlgorithm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathAuthzDB {

    public final static String UNDEF = "undef-PathAuthzDB";

    private final Logger log = AuthzDirector.getLogger();

    private final static String DEFAULT_ALGORITHM = PathAuthzAlgBestMatch.class.getName();

    private String pathAuthzDBID = "not-defined";
    private String authzAlgorithm = null;
    private PathAuthzEvaluationAlgorithm evaluationAlg = null;
    private final List<PathACE> authzDB = new ArrayList<PathACE>();

    // =========== CONSTRUCTORs ============

    /**
     * @return
     */
    public static PathAuthzDB makeEmpty() {
        PathAuthzDB result = new PathAuthzDB();
        result.setPathAuthzDBID("default-PathAuthzDB");
        result.setPathAuthzEvaluationAlgorithm(new PathAuthzAlgBestMatch());
        result.addPathACE(PathACE.PERMIT_ALL);
        return result;
    }

    /**
     * @param string
     */
    void setPathAuthzDBID(String pathAuthzDBID) {
        this.pathAuthzDBID = pathAuthzDBID;

    }

    /**
     * Empty constructor. Use it only if there is a file DB
     */
    public PathAuthzDB() {
        pathAuthzDBID = UNDEF;
    }

    /**
     * @param authzClassName
     * @throws AuthzException
     */
    void setPathAuthzEvaluationAlgorithm(String authzClassName) throws AuthzException {
        authzAlgorithm = authzClassName;
        Class<?> authzAlgClass = null;
        try {
            authzAlgClass = Class.forName(authzClassName);
        } catch (ClassNotFoundException e) {
            log.error("Unable to load the Path Authz Algorithm Class '" + authzClassName + "'\n" + e);
            // Manage the exceptional case (Use the default Algorithm)
            setDefaultPathAuthzEvaluationAlgorithm();
        }
        if (authzAlgClass != null) {
            Object authzAlgInstance = null;
            try {
                authzAlgInstance = authzAlgClass.newInstance();
                if (authzAlgInstance instanceof PathAuthzEvaluationAlgorithm) {
                    // ** SET the Algorithm **
                    evaluationAlg = (PathAuthzEvaluationAlgorithm) authzAlgInstance;
                    log.debug("Found a valid Path Authz Evaluation Algorithm.");
                    log.debug(" It implements the algorithm : " + evaluationAlg.getDescription());
                } else {
                    log.error("The Class '" + authzClassName + "' is not a valid Path Authz Evaluation Algorithm class");
                    // Manage the exceptional case (Use the default Algorithm)
                    setDefaultPathAuthzEvaluationAlgorithm();
                }
            } catch (InstantiationException e) {
                log.error("Unable to instantiate the Path Authz Algorithm Class '" + authzClassName + "'; "
                        + e.getMessage());
                // Manage the exceptional case (Use the default Algorithm)
                setDefaultPathAuthzEvaluationAlgorithm();
            } catch (IllegalAccessException e) {
                log.error("Unable to instantiate the Path Authz Algorithm Class '" + authzClassName + "'; "
                        + e.getMessage());
                // Manage the exceptional case (Use the default Algorithm)
                setDefaultPathAuthzEvaluationAlgorithm();
            }
        }
    }

    private void setDefaultPathAuthzEvaluationAlgorithm() {
        try {
            setPathAuthzEvaluationAlgorithm(DEFAULT_ALGORITHM);
        } catch (AuthzException e) {
            log.error("Unable to instantiate the DEFAULT algortihm :" + DEFAULT_ALGORITHM);
        }
    }

    public void setPathAuthzEvaluationAlgorithm(PathAuthzEvaluationAlgorithm evAlg) {
        evaluationAlg = evAlg;
    }

    public void addPathACE(PathACE pathAce) {
        authzDB.add(pathAce);
    }

    // ============= INFORMATIONALs ========

    public PathAuthzEvaluationAlgorithm getAuthorizationAlgorithm() {
        return evaluationAlg;
    }

    public int getACLSize() {
        return authzDB.size();
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
        result += PathACE.ALGORITHM + "=" + authzAlgorithm + "\n \n";
        int count = 0;
        for (PathACE ace : authzDB) {
            result += "ace[" + count + "]: " + ace.toString() + "\n";
            count++;
        }
        return result;
    }

}
