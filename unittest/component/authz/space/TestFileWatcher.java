/**
 * 
 */
package component.authz.space;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.conf.FileAuthzDBWatcher;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


/**
 * @author zappi
 *
 */
public class TestFileWatcher {

    private static Logger log = LoggerFactory.getLogger(TestFileWatcher.class);


    private static void init() {
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
    }


    public void test() {
        FileAuthzDBWatcher fw;
        try {
            fw = new FileAuthzDBWatcher(2000, "");
            String unitTestPath = System.getProperty("user.dir")+ File.separator + "etc" + File.separator;
            fw.watchAuthzDBFile(unitTestPath+"sa1-lhcb.authz");
            fw.startWatching();
            log.debug(fw.toString());
        } catch (AuthzDBReaderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        init();
        TestFileWatcher test = new TestFileWatcher();
        test.test();
    }

}
