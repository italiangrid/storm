
package component.griduser;

import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.griduser.CannotMapUserException;
import it.grid.storm.griduser.FQAN;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.griduser.GridUserManager;
import it.grid.storm.griduser.LocalUser;
import it.grid.storm.griduser.VomsGridUser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.authz.AuthzTest;
import component.utiltest.SetUpTest;

public class GridUserTest {
    private static Logger log = LoggerFactory.getLogger(AuthzTest.class);

    private void init() {

        //Logging Set Up
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);

        int refreshInSeconds = 5;
        Configuration config = Configuration.getInstance();
        String configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
        String configurationFileName = configurationPATH + File.separator + "storm_test.properties";
        config.setConfigReader(new ConfigReader(configurationFileName, refreshInSeconds));

        log.debug(" CONFIGURATION = "+configurationFileName);
        log.debug("UserMapper Classname = "+config.getGridUserMapperClassname());

    }


    public Map createInputParam(String userDN, Object[] userFQANS) {
        Map result = new HashMap();
        result.put("userDN",userDN);
        result.put("userFQANS",userFQANS);
        return result;
    }

    public Object[] createUserFQANS(FQAN[] fqans) {
        Object[] result = new Object[fqans.length];
        for (int i = 0; i < fqans.length; i++) {
            result[i] = fqans[i].toString();
        }
        return result;
    }

    public static void main(String[] args) {
        GridUserTest test = new GridUserTest();
        test.init();
        String userDN = "/C=IT/O=INFN/OU=Personal Certificate/L=CNAF/CN=Luca Magnoni";
        FQAN fqan0 = new FQAN("/dteam/Role=NULL/Capability=NULL");
        FQAN fqan1 = new FQAN("/dteam/italy/Role=NULL/Capability=NULL");
        FQAN fqan2 = new FQAN("/dteam/italy/INFN-CNAF/Role=NULL/Capability=NULL");
        FQAN[] fqans = {fqan0, fqan1, fqan2};
        Object[] userFQANS = test.createUserFQANS(fqans);
        log.debug("userDN    = " + userDN);
        log.debug("userFQANS = " + userFQANS);
        Map inputParam = test.createInputParam(userDN,userFQANS);
        log.debug("InputParam = "+inputParam);
        GridUserInterface requestor = GridUserManager.decode(inputParam);
        String gridUserMapperClassname = GridUserManager.getMapperClassName();
        log.debug("Grid USer Mapper Classname = "+gridUserMapperClassname);
        log.debug("Requestor = "+requestor);
        if (requestor instanceof VomsGridUser) {
            log.debug("VOMS Grid User found");
            VomsGridUser vUser = (VomsGridUser) requestor;
            log.debug("FQAN in String[] = "+vUser.getFQANsString().length);
            for (int i = 0; i < vUser.getFQANsString().length; i++) {
                log.debug("FQAN in String["+i+"] = "+vUser.getFQANsString()[i]);
            }
            log.debug("FQAN in List<String> = "+vUser.getFQANsList());
            try {
                LocalUser lu = vUser.getLocalUser();
                log.debug("Local User = "+lu);
            }
            catch (CannotMapUserException ex) {
                log.error("ERR:"+ex);
            }
        }
    }
}

