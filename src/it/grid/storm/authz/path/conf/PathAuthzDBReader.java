/**
 * 
 */
package it.grid.storm.authz.path.conf;

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;

/**
 * @author zappi
 *
 */
public class PathAuthzDBReader {

    private final Logger log = AuthzDirector.getLogger();

    private String authzDBFilename;
    private PathAuthzDB pathAuthzDB;
    
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
        log.info("Path Authz DB now contains '" + pathAuthzDB.getSize() + "' path ACE.");  
    }
    
    private PathAuthzDB parsePathAuthzDB() {
        PathAuthzDB result = new PathAuthzDB();
        try {
            BufferedReader in = new BufferedReader(new FileReader(authzDBFilename));
            String str;
            while ((str = in.readLine()) != null) {
                PathACE ace = parseLine(str);
                result.addPathACE(ace);
            }
            in.close();
        } catch (IOException e) {
            log.error("Error while reading Path Authz DB '" + authzDBFilename + "'");
        }
        return result;
    }

    /**
     * @param str
     * @return
     */
    private PathACE parseLine(String str) {
        // TODO Auto-generated method stub
        return null;
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
