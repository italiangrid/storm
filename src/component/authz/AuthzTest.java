package component.authz;

import org.apache.commons.logging.LogFactory;
import component.namespace.config.AdHocTest;
import org.apache.commons.logging.Log;
import java.io.File;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.commons.logging.impl.Log4JLogger;
import it.grid.storm.authz.sa.model.EGEEFQANMatchingRule;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.AuthzDirector;

public class AuthzTest {

    private static Log log = LogFactory.getLog(AuthzTest.class);

    private void init() {

      boolean jdk14Logger = (log instanceof Jdk14Logger);
      boolean log4jlog = (log instanceof Log4JLogger);

      if (jdk14Logger) {
        System.out.println("Using Jdk14Logger = " + jdk14Logger);
      }
      if (log4jlog) {
        System.out.println("Using Log14Logger = " + log4jlog);
        String logConfigFile = System.getProperty("user.dir") + File.separator +
            "unittest" + File.separator + "log4j_for_testing.properties";
        System.out.println("config file = " + logConfigFile);
        PropertyConfigurator.configure(logConfigFile);
      }
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
        log.error("AHH" + ex);
      }
    }

    public void testEGEEFQAN_MR(String fqanRE) {
      try {
        EGEEFQANMatchingRule fqanMR = new EGEEFQANMatchingRule(fqanRE);
        log.debug("fqanMR = " );

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


