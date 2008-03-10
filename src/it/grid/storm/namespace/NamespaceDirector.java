package it.grid.storm.namespace;

import java.io.*;

import org.apache.commons.logging.*;
import it.grid.storm.config.*;
import it.grid.storm.namespace.config.*;
import it.grid.storm.namespace.config.xml.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF and ICTP/eGrid project</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class NamespaceDirector {

    private static final Log log = LogFactory.getLog("namespace");
    private static NamespaceInterface namespaceIstance = null;

    private static int refreshInSeconds = 5; //Default value;
    private static String configurationPATH;
    private static String namespaceConfigFileName;
    private static boolean runningMode = false;
    private static String configurationFileName;
    private Configuration config;
    private static NamespaceLoader loader;
    private static NamespaceParser parser;

    private static boolean initialized = false;

    public static void initializeDirector(boolean verboseMode, boolean testingMode) {

        log.info("NAMESPACE : Inizializating ...");
        Configuration config = Configuration.getInstance();

        if (testingMode) {
            log.info(" ####################### ");
            log.info(" ####  TESTING MODE #### ");
            log.info(" ####################### ");
            runningMode = testingMode;
            configurationPATH = System.getProperty("user.dir") + File.separator + "etc";
            configurationFileName = configurationPATH + File.separator + "storm_test.properties";
            config.setConfigReader(new ConfigReader(configurationFileName, refreshInSeconds));
            namespaceConfigFileName = config.getNamespaceConfigFilename();
            refreshInSeconds = config.getNamespaceConfigRefreshRateInSeconds();
            loader = new XMLNamespaceLoader(configurationPATH, namespaceConfigFileName, refreshInSeconds, false);

            //Check the validity of namespace.
            if (loader instanceof XMLNamespaceLoader) {
                XMLNamespaceLoader xmlLoader = (XMLNamespaceLoader) loader;
                if (! (xmlLoader.schemaValidity)) {
                    //Error into the validity ckeck of namespace
                    System.out.println("Namespace configuration is not conformant with namespae grammar.");
                    System.out.println("Please validate namespace configuration file.");
                    System.exit(0);
                }
            }

        }
        else {
            log.info(" +++++++++++++++++++++++ ");
            log.info("    Production Mode      ");
            log.info(" +++++++++++++++++++++++ ");
            runningMode = testingMode;
            configurationPATH = config.getNamespaceConfigPath(); //Default = "./etc/"
            namespaceConfigFileName = config.getNamespaceConfigFilename(); //Default = "namespace.xml"
            refreshInSeconds = config.getNamespaceConfigRefreshRateInSeconds(); //Default = "3 seconds"
            loader = new XMLNamespaceLoader(configurationPATH, namespaceConfigFileName, refreshInSeconds, verboseMode);

            //Check the validity of namespace.
            if (loader instanceof XMLNamespaceLoader) {
                XMLNamespaceLoader xmlLoader = (XMLNamespaceLoader) loader;
                if (! (xmlLoader.schemaValidity)) {
                    //Error into the validity ckeck of namespace
                    System.out.println("Namespace configuration is not conformant with namespae grammar.");
                    System.out.println("Please validate namespace configuration file.");
                    System.exit(0);
                }
            }

        }

        log.debug("Namespace Configuration PATH : " + configurationPATH);
        log.debug("Namespace Configuration FILENAME : " + namespaceConfigFileName);
        log.debug("Namespace Configuration GLANCE RATE : " + refreshInSeconds);

        parser = new XMLNamespaceParser(loader, verboseMode, testingMode);
        namespaceIstance = new Namespace(parser);

        log.debug("NAMESPACE INITIALIZATION : ... done!");
        initialized = true;

    }

    /**
     *
     * @return Namespace
     */
    public static NamespaceInterface getNamespace() {
        if (! (initialized)) {
            initializeDirector(false, false);
        }
        return namespaceIstance;
    }

    /**
     *
     * @return Namespace
     */
    public static NamespaceInterface getNamespace(boolean verboseMode, boolean testingMode) {
        if (! (initialized)) {
            initializeDirector(verboseMode, testingMode);
        }
        return namespaceIstance;
    }

    /**
     *
     * @return Namespace
     */
    public static NamespaceParser getNamespaceParser(boolean verboseMode, boolean testingMode) {
        if (! (initialized)) {
            initializeDirector(verboseMode, testingMode);
        }
        return parser;
    }

    /**
     *
     * @return Namespace
     */
    public static NamespaceLoader getNamespaceLoader(boolean verboseMode, boolean testingMode) {
        if (! (initialized)) {
            initializeDirector(verboseMode, testingMode);
        }
        return loader;
    }

    public static Log getLogger() {
        return log;
    }

}
