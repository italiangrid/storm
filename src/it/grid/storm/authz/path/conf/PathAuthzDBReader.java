/**
 * 
 */
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.AuthzException;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.authz.path.model.PathAuthzAlgBestMatch;
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

    // private final String algorithmName = null;
    // private final boolean setAlgorithm = false;

    private static enum LineType {
        COMMENT, ALGORITHM_NAME, PATH_ACE, OTHER
    }

    public PathAuthzDBReader(String filename) {
        log.info("Path Authorization : Inizializating ...");

        // Check if filename is an absolute name
        if (!(existsAuthzDBFile(filename))) {
            // The path-authz.db does not exist.
            // Maybe filename is not an absolute name
            // Try to build the default path
            Configuration config = Configuration.getInstance();
            // configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
            String configurationPATH = config.getNamespaceConfigPath();
            if (configurationPATH.length() == 0) {
                String userDir = System.getProperty("user.dir");
                log.debug("Unable to found the configuration path. Assume: '" + userDir + "'");
                configurationPATH = userDir + File.separator + "etc";
            }
            authzDBFilename = configurationPATH + File.separator + filename;
        } else {
            authzDBFilename = filename;
        }
        log.debug("Loading Path Authz DB : '" + authzDBFilename + "'.");
        pathAuthzDB = loadPathAuthzDB();
        log.info("Path Authz DB ('" + pathAuthzDB.getPathAuthzDBID() + "') loaded.");
        log.info(pathAuthzDB.toString());
    }

    public void refreshPathAuthzDB() {
        log.debug("<PathAuthzDBReader> Start refreshing.");
        pathAuthzDB = loadPathAuthzDB();
        log.debug("<PathAuthzDBReader> End refreshing.");
        log.info("Path Authz DB ('" + pathAuthzDB.getPathAuthzDBID() + "') RE-loaded.");
        log.info(pathAuthzDB.toString());
    }

    public PathAuthzDB getPathAuthzDB() {
        return pathAuthzDB;
    }

    /**************************
     * Private BUILDERs helper
     **************************/

    /**
     * @return
     */
    private PathAuthzDB loadPathAuthzDB() {
        PathAuthzDB result = null;
        if (!(existsAuthzDBFile(authzDBFilename))) {
            log.debug("Path Authz DB does not exists. Use the default Path Authz DB.");
            // Load the default Path Authz DB
            result = PathAuthzDB.makeEmpty();

        } else {
            log.debug("Parsing the Path Authz DB ...");
            result = parsePathAuthzDB();
        }
        // Check validity of parsed Path Authz DB
        boolean validPathAuthz = checkValidity(result);
        if (validPathAuthz) {
            log.info("Path Authz DB contains '" + result.getACLSize() + "' path ACE.");
        } else {
            // Load the default Path Authz DB
            result = PathAuthzDB.makeEmpty();
            log.warn("Path Authz DB seems not valid. Loaded the default one! ");
        }
        return result;
    }

    /**
     * @param pathAuthzDB2
     * @return
     * @todo insert further validation tests.
     */
    private boolean checkValidity(PathAuthzDB authzDBread) {
        boolean result = true;
        if (authzDBread.getPathAuthzDBID().equals(PathAuthzDB.UNDEF)) {
            return false;
        }
        return result;
    }

    /**
     * @return
     */
    private PathAuthzDB parsePathAuthzDB() {
        PathAuthzDB result = new PathAuthzDB();
        try {
            BufferedReader in = new BufferedReader(new FileReader(authzDBFilename));
            String str;
            while ((str = in.readLine()) != null) {
                ParseLineResults parsedLine;

                parsedLine = parseLine(str);
                switch (parsedLine.type) {
                    case COMMENT:
                        log.debug("comment line  : " + parsedLine.getComment());
                        break;
                    case ALGORITHM_NAME:
                        try {
                            result.setPathAuthzEvaluationAlgorithm(parsedLine.getAlgorithmName());
                            log.debug("algorithm name: " + parsedLine.getAlgorithmName());
                        } catch (AuthzException e) {
                            log.warn("Unable to set Algorithm: '" + parsedLine.getAlgorithmName() + "'");
                            result.setPathAuthzEvaluationAlgorithm(new PathAuthzAlgBestMatch());
                            log.warn("StoRM will use the default 'Alg. Best Match'");
                        }

                        break;
                    case PATH_ACE:
                        result.addPathACE(parsedLine.getPathAce());
                        log.debug("path ace      : " + parsedLine.getPathAce());
                        break;
                    case OTHER:
                        log.debug("something was wrong in '" + str + "'");
                        break;
                }
            }
            in.close();
            result.setPathAuthzDBID(authzDBFilename);
        } catch (IOException e) {
            log.error("Error while reading Path Authz DB '" + authzDBFilename + "'");
            result.setPathAuthzDBID("I/O error");
        }
        return result;
    }

    /**
     * @param str
     * @return ParseLineResults
     * @throws AuthzException
     */
    private ParseLineResults parseLine(String pathACEString) {
        ParseLineResults result = null;
        if (pathACEString.startsWith(PathACE.COMMENT)) {
            // COMMENT LINE
            result = new ParseLineResults(LineType.COMMENT);
            result.setComment(pathACEString);
        } else {
            if (pathACEString.startsWith(PathACE.ALGORITHM)) {
                // EVALUATION ALGORITHM
                if (pathACEString.contains("=")) {
                    String algName = pathACEString.substring(pathACEString.indexOf("=") + 1);
                    result = new ParseLineResults(LineType.ALGORITHM_NAME);
                    result.setAlgorithmName(algName.trim());
                }
            } else {
                // Check if it is an empty line
                if (pathACEString.trim().length() == 0) {
                    result = new ParseLineResults(LineType.COMMENT);
                    result.setComment("");
                } else {
                    // SUPPOSE ACE Line
                    try {
                        PathACE ace = PathACE.buildFromString(pathACEString);
                        result = new ParseLineResults(LineType.PATH_ACE);
                        result.setPathAce(ace);
                    } catch (AuthzException e) {
                        log.error("Something of inexiplicable in the line " + pathACEString);
                        log.error(" - explanation: " + e.getMessage());
                        result = new ParseLineResults(LineType.OTHER);
                    }

                }
            }
        }
        return result;
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

    private class ParseLineResults {
        private final LineType type;
        private String comment = null;
        private String algorithmName = null;
        private PathACE pathAce = null;

        /**
         * @param
         */
        public ParseLineResults(LineType type) {
            this.type = type;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setAlgorithmName(String algName) {
            algorithmName = algName;
        }

        public void setPathAce(PathACE ace) {
            pathAce = ace;
        }

        public String getComment() {
            return comment;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public PathACE getPathAce() {
            return pathAce;
        }

    }

}
