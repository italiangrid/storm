/**
 * 
 */
package component.authz;

import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.conf.PathAuthzDBReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.startup.Bootstrap;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
public class PathAuthzTest {

    private static final Logger log = LoggerFactory.getLogger(PathAuthzTest.class);

    public static void main(String[] args) {

        String configurationDir = Configuration.getInstance().getConfigurationDir();
        if ((configurationDir == null) || (configurationDir.length() == 0)) {
            configurationDir = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        }
        String logFile = configurationDir + "logging-test.xml";

        Bootstrap.initializeLogging(logFile);
        PathAuthzTest test = new PathAuthzTest();
        PathAuthzDB pathAuthzDB = test.loadDB("path-authz.db");
        pathAuthzDB.toString();
    }

    /**
     * @param string
     * @return
     */
    private PathAuthzDB loadDB(String pathAuthzDBFileName) {
        PathAuthzDBReader authzDBReader = new PathAuthzDBReader(pathAuthzDBFileName);
        PathAuthzDB result = authzDBReader.getPathAuthzDB();
        return result;
    }

}
