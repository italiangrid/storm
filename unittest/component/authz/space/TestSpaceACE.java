/**
 * 
 */
package component.authz.space;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.conf.SpaceACETextParser;
import it.grid.storm.authz.sa.model.EGEEFQANPattern;
import it.grid.storm.authz.sa.model.SpaceACE;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


/**
 * @author zappi
 *
 */
public class TestSpaceACE {

    private final static String AceDN_String = "ace.1=dn:/DC=ch/DC=cern/OU=Organic Units/OU=Users/CN=elanciot/CN=576215/CN=Elisa Lanciotti:DURWSCP:ALLOW";
    private final String AceFQAN_String = "ace.3=fqan:EVERYONE:RQ:ALLOW";
    private static Logger log = LoggerFactory.getLogger(TestSpaceACE.class);

    private static void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        init();
        try {
            SpaceACE sa = SpaceACETextParser.parse(AceDN_String);
            log.debug(sa.toString());
            EGEEFQANPattern fp = new EGEEFQANPattern("/vo/ciccio");
            log.debug(fp.toString());
        } catch (AuthzDBReaderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
