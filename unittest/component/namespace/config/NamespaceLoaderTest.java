package component.namespace.config;

import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;
import it.grid.storm.namespace.config.xml.XMLParserUtil;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import component.utiltest.SetUpTest;


public class NamespaceLoaderTest {

    private  XMLNamespaceParser parser;
    private  XMLParserUtil parserUtil;
    private static Logger log = LoggerFactory.getLogger(NamespaceLoaderTest.class);
	
	private void init() {
		
        System.out.println("Using SLF4J and LogBack");
        String unitTestPath = System.getProperty("user.dir") + File.separator + "unittest" + File.separator;
        String logConfigFile = unitTestPath + "logging-test.xml";
        SetUpTest.init(logConfigFile);

        // Set storm.properties file name
        String configurationPath = System.getProperty("user.dir") + File.separator + "etc" + File.separator;
        String configFN = configurationPath + "storm.properties";
        log.debug("Configuration Directory hand setted : "+configurationPath);
        
        // Load the configuration storm.properties
        Configuration.getInstance().setConfigReader(new ConfigReader(configFN, 0));
        
        
        configurationPath = Configuration.getInstance().configurationDir();
        log.debug("Configuration Directory : "+configurationPath);
        String namespaceConfigFileName = Configuration.getInstance().getNamespaceConfigFilename();
        int refreshInSeconds = Configuration.getInstance().getNamespaceConfigRefreshRateInSeconds(); //Default = "3 seconds"

        // PersistenceDirector p;

        NamespaceLoader loader = new XMLNamespaceLoader(configurationPath, namespaceConfigFileName, refreshInSeconds, false);
        parser = new XMLNamespaceParser(loader, false, true);
        parserUtil = new XMLParserUtil(loader.getConfiguration());
			
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NamespaceLoaderTest test = new NamespaceLoaderTest();
		test.init();

	}

}
