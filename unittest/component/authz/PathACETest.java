/**
 * 
 */
package component.authz;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.authz.path.model.PathACE;
import it.grid.storm.config.Configuration;
import it.grid.storm.startup.Bootstrap;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 */
public class PathACETest {

    private static final Logger log = LoggerFactory.getLogger(PathACETest.class);

    private static void initLogging() {
        String configurationDir = Configuration.getInstance().getConfigurationDir();
        if ((configurationDir == null) || (configurationDir.length() == 0)) {
            configurationDir = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        }
        String logFile = configurationDir + "logging-test.xml";

        Bootstrap.initializeLogging(logFile);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        initLogging();
        PathACE pathAce = new PathACE();
        try {
            pathAce.setLocalGroupName("@ALL@");
        } catch (AuthzException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.debug("PathACE : " + pathAce);
        pathAce.subjectMatch("ciccio");
    }

}
