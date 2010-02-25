package component.junit;

import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTestsOnSURL {   
    
    private static Logger log = LoggerFactory.getLogger(AllTestsOnSURL.class);
    
    private static void init() {

        //Logging Set Up
          
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);
 
        int refreshInSeconds = 5;
        Configuration config = Configuration.getInstance();
        String configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
        String configurationFileName = configurationPATH + File.separator + "storm-test.properties";
        config.setConfigReader(new ConfigReader(configurationFileName, refreshInSeconds));

        log.debug(" CONFIGURATION = "+configurationFileName);

    }
    
    public static Test suite() {
        init();
        TestSuite suite= new TestSuite("All JUnit Tests");
        suite.addTest(TSURLTestCase.suite());
        return suite;
    }
    
    public static void main (String[] args) {
        init();
        junit.textui.TestRunner.run (suite());
    }
    
}
