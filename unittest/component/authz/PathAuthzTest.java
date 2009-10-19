/**
 * 
 */
package component.authz;

import it.grid.storm.authz.AuthzDecision;
import it.grid.storm.authz.path.PathAuthz;
import it.grid.storm.authz.path.conf.PathAuthzDB;
import it.grid.storm.authz.path.conf.PathAuthzDBReader;
import it.grid.storm.authz.path.model.SRMFileRequest;
import it.grid.storm.common.types.InvalidStFNAttributeException;
import it.grid.storm.common.types.StFN;
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

    private PathAuthz pathAuthz;

    public static void main(String[] args) {

        String configurationDir = Configuration.getInstance().getConfigurationDir();
        if ((configurationDir == null) || (configurationDir.length() == 0)) {
            configurationDir = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        }
        String logFile = configurationDir + "logging-test.xml";

        Bootstrap.initializeLogging(logFile);
        PathAuthzTest test = new PathAuthzTest();
        PathAuthzDB pathAuthzDB = test.loadDB("path-authz.db");
        log.debug(pathAuthzDB.toString());

        test.pathAuthz = new PathAuthz(pathAuthzDB);

        test.checkAuthz("cms", "PTP", "/cms/test");
        sleep();
        test.checkAuthz("cmsfff", "PTP", "/cms/test");
        sleep();
        test.checkAuthz("cms", "RM", "/cms/test");
        sleep();
        test.checkAuthz("cmsprod", "MD", "/cms/test");
        sleep();
    }

    private static void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void checkAuthz(String localGroup, String srmOp, String stfn) {
        SRMFileRequest srmReq = SRMFileRequest.buildFromString(srmOp);
        StFN storFN = StFN.makeEmpty();
        try {
            storFN = StFN.make(stfn);
        } catch (InvalidStFNAttributeException e) {
            log.error("Error in StFN :" + e);
        }
        log.debug("Request:'" + localGroup + "' ops:'" + srmReq + "' stfn:'" + storFN + "'");
        AuthzDecision esito = pathAuthz.authorizeTest(localGroup, srmReq, storFN);
        log.debug("Esito: " + esito);
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
