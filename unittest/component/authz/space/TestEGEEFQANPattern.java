package component.authz.space;

import it.grid.storm.authz.sa.conf.AuthzDBReaderException;
import it.grid.storm.authz.sa.model.EGEEFQANPattern;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


public class TestEGEEFQANPattern {

    private static Logger log = LoggerFactory.getLogger(TestEGEEFQANPattern.class);

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
        EGEEFQANPattern fpat;
        try {
            fpat = new EGEEFQANPattern("");
            log.debug(fpat.toString());
        } catch (AuthzDBReaderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
