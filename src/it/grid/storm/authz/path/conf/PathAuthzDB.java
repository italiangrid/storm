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

    private final Logger log = AuthzDirector.getLogger();

    private final static String DEFAULT_ALGORITHM = PathAuthzAlgBestMatch.class.getName();

    private String authzAlgorithm = null;
    private PathAuthzEvaluationAlgorithm evaluationAlg = null;
    private final List<PathACE> authzDB = new ArrayList<PathACE>();

    // =========== CONSTRUCTORs ============

    /**
     * @return
     */
    public static PathAuthzDB makeEmpty() {
        PathAuthzDB result = new PathAuthzDB();
        result.setPathAuthzEvaluationAlgorithm(new PathAuthzAlgBestMatch());
        result.addPathACE(PathACE.PERMIT_ALL);
        return result;
    }

    public PathAuthzDB() {
    }

    void setPathAuthzEvaluationAlgorithm(String authzClassName) throws AuthzException {
        authzAlgorithm = authzClassName;
        Class<?> authzAlgClass = null;
        try {
            Class thisClass = PathAuthzAlgBestMatch.class;
            String thisClassName = thisClass.getName();
            log.debug("This class name  = " + thisClassName);
            authzAlgClass = Class.forName(authzClassName);

        } catch (ClassNotFoundException e) {
            log.error("Unable to load the Path Authz Algorithm Class '" + authzClassName + "'\n" + e);

            // Manage the exceptional case (Use the default Algorithm)
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
                    log.error("The Class '" + authzClassName
                            + "' is not a valid Path Authz Evaluation Algorithm");
                    // Manage the exceptional case (Use the default Algorithm)
                }
            } catch (InstantiationException e) {
                log.error("Unable to instantiate the Path Authz Algorithm Class '" + authzClassName + "'; "
                        + e.getMessage());
                // Manage the exceptional case (Use the default Algorithm)
            } catch (IllegalAccessException e) {
                log.error("Unable to instantiate the Path Authz Algorithm Class '" + authzClassName + "'; "
                        + e.getMessage());
                // Manage the exceptional case (Use the default Algorithm)
            }
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

    @Override
    public String toString() {
        String result = "=== Path Authorizaton DataBase === \n";
        result += PathACE.ALGORITHM + "=" + authzAlgorithm + "\n \n";
        for (PathACE ace : authzDB) {
            result += ace.toString() + "\n";
        }
        return result;
    }

}
