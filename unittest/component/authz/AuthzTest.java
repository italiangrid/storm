package component.authz;


import it.grid.storm.authz.sa.model.EGEEFQANPattern;
import it.grid.storm.griduser.FQAN;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;

public class AuthzTest {

    private static Logger log = LoggerFactory.getLogger(AuthzTest.class);

    private void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
    }

    public void testFQAN(String fqanStr) {
        try {
            FQAN fqan = new FQAN(fqanStr);
            log.debug("FQAN = " + fqan);
            log.debug("FQAN VO         = " + fqan.getVo());
            log.debug("FQAN Group      = " + fqan.getGroup());
            log.debug("FQAN SubGroup   = " + fqan.getSubGroup());
            log.debug("FQAN Role       = " + fqan.getRole());
            log.debug("FQAN Capability = " + fqan.getCapability());
        }
        catch (Exception ex) {
            log.error("AHH! " + ex);
        }
    }

    public void testEGEEFQAN_MR(String fqanRE) {
        try {
            EGEEFQANPattern fqanMR = new EGEEFQANPattern(fqanRE);
            log.debug("fqanMR = "+fqanMR);

        }
        catch (Exception ex) {
            log.error("AHH" + ex);
        }
    }


    public static void main(String[] args) {
        AuthzTest test = new AuthzTest();
        test.init();

        test.testFQAN("/atlas/ciccio/Role=sgm");
        test.testFQAN("/atlas/ciccio");
        test.testFQAN("Role=sgm");

        test.testEGEEFQAN_MR("");

    }

}


