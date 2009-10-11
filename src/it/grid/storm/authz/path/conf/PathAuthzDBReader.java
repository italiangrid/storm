/**
 * 
 */
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;

/**
 * @author zappi
 */
public class PathAuthzDBReader {

    private final Logger log = AuthzDirector.getLogger();

    private final String authzDBFilename;
    private PathAuthzDB pathAuthzDB;
    private String algorithmName = null;

    public PathAuthzDBReader(String filename) {
        log.info("Path Authorization : Inizializating ...");
        Configuration config = Configuration.getInstance();
        // configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
        String configurationPATH = config.getNamespaceConfigPath();
        authzDBFilename = configurationPATH + File.separator + filename;
        log.debug("Loading Path Authz DB : '" + authzDBFilename + "'.");
        loadPathAuthzDB();
    }

    private void loadPathAuthzDB() {
        if (!(existsAuthzDBFile(authzDBFilename))) {
            log.debug("Path Authz DB does not exists. Use the default Path Authz DB.");
            // Load the default Path Authz DB
            pathAuthzDB = PathAuthzDB.makeEmpty();

        } else {
            log.debug("Parsing the Path Authz DB ...");
            pathAuthzDB = parsePathAuthzDB();
        }
        log.info("Path Authz DB contains '" + pathAuthzDB.getACLSize() + "' path ACE.");
    }

    private PathAuthzDB parsePathAuthzDB() {
        PathAuthzDB result = new PathAuthzDB();
        try {
            BufferedReader in = new BufferedReader(new FileReader(authzDBFilename));
            String str;
            while ((str = in.readLine()) != null) {
                PathACE ace = null;
                try {
                    ace = parseLine(str);
                    if (ace != null) {
                        result.addPathACE(ace);
                    } else {
                        // Found a comment line or algorithm definition.
                        if (algorithmName != null) {
                            log.debug("Evaluation Algorithm name: " + algorithmName);
                            result.setPathAuthzEvaluationAlgorithm(algorithmName);
                        }
                    }

                } catch (AuthzException e) {
                    log.debug("No ACE line found");
                }
            }
            in.close();
        } catch (IOException e) {
            log.error("Error while reading Path Authz DB '" + authzDBFilename + "'");
        }
        return result;
    }

    /**
     * @param str
     * @return PathACE if the line parsed is a valid PathACE, null if the line is a comment or other special lines
     * @throws AuthzException
     */
    private PathACE parseLine(String pathACEString) throws AuthzException {
        PathACE result = null;
        if (pathACEString.startsWith(PathACE.COMMENT)) {
            // COMMENT LINE
            log.debug("Skipped the comment line: " + pathACEString);
        } else {
            if (pathACEString.startsWith(PathACE.ALGORITHM)) {
                // EVALUATION ALGORITHM
                if (pathACEString.contains("=")) {
                    String algName = pathACEString.substring(pathACEString.indexOf("="));
                    algorithmName = algName.trim();
                }

            } else {
                // SUPPOSE ACE Line
                result = PathACE.buildFromString(pathACEString);
            }
        }
        return result;
    }

    public void refreshPathAuthzDB() {
        loadPathAuthzDB();
    }

    public PathAuthzDB getPathAuthzDB() {
        return pathAuthzDB;
    }

    /***********************************************
     * UTILITY Methods
     */

    private boolean existsAuthzDBFile(String fileName) {
        boolean exists = (new File(fileName)).exists();
        if (!(exists)) {
            log.warn("The AuthzDB File '" + fileName + "' does not exists");
        }
        return exists;
    }

}
