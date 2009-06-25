/**
 * 
 */
package component.authz.space;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


/**
 * @author zappi
 *
 */
public class TestFileAuthzDBReader {

    private static Logger log = LoggerFactory.getLogger(TestFileAuthzDBReader.class);

    private static void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
    }

}
