package it.grid.storm;

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.config.WelcomeMessage;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.startup.Bootstrap;
import it.grid.storm.tape.recalltable.RecallTableService;
import it.grid.storm.xmlrpc.XMLRPCHttpServer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a StoRM as a whole: it sets the configuration file which contains properties necessary for
 * other classes of StoRM, it sets up logging, as well as the advanced picker.
 * 
 * @author EGRID - ICTP Trieste; INFN - CNAF Bologna
 * @date March 28th, 2005
 * @version 7.0
 */

public class StoRM {

    private AdvancedPicker picker = null; // Picker of StoRM

    private XMLRPCHttpServer xmlrpcServer = null;

    private final String welcome = WelcomeMessage.getWelcomeMessage(); // Text that displays general info about StoRM project

    private static Logger log;

    private final Timer GC = new Timer(); // Timer object in charge to call periodically the Space Garbace Collector

    /**
     * Public constructor that requires a String containing the complete pathname to the configuration file, as well as
     * the desired refresh rate in seconds for changes in configuration. Beware that by pathname it is meant the
     * complete path starting from root, including the name of the file itself! If pathname is empty or null, then an
     * attempt will be made to read properties off /opt/storm/etc/storm.properties. BEWARE!!! For MS Windows
     * installations this attempt _will_ fail! In any case, failure to read the configuratin file causes StoRM to use
     * hardcoded default values.
     */
    public StoRM(String configurationPathname, int refresh) {
        // verifying supplied configurationPathname and print to screen...
        if ((configurationPathname == null) || (configurationPathname == "")) {
            // built-in configuration file to be used if nothing gets specified!
            configurationPathname = "/opt/storm/backend/etc/storm.properties";
            System.out.print("This instance of StoRM Backend was invoked without explicitly specifying ");
            System.out.print("a configuration file. Looking for the standard one in ");
            System.out.println(configurationPathname);
        } else {
            // look for given configuration file...
            System.out.print("Looking for configuration file ");
            System.out.println(configurationPathname);
        }

        // load properties from configuration...
        Configuration.getInstance().setConfigReader(new ConfigReader(configurationPathname, refresh));

        // set and print current configuration string...
        String currentConfig = "\nCurrent configuration:\n" + Configuration.getInstance().toString();
        System.out.println(currentConfig);
        // print welcome
        System.out.println("\n" + welcome);
        
        /**
         * INIT LOGGING COMPONENT
         */
        String configurationDir = Configuration.getInstance().configurationDir();
        String logFile = configurationDir + "logging.xml";
        Bootstrap.initializeLogging(logFile);

        StoRM.log = LoggerFactory.getLogger(StoRM.class);

        //
        log.warn(welcome); // log welcome string!
        log.info(currentConfig); // log actually used values!

        // Force the loadind and the parsing of Namespace configuration
        boolean verboseMode = false; // true generates verbose logging
        boolean testingMode = false; // True if you wants testing namespace
        NamespaceDirector.initializeDirector(verboseMode, testingMode);

        // Hearthbeat
        HealthDirector.initializeDirector(false);

        // Path Authz Initialization
        String pathAuthzDBFileName = configurationDir + "path-authz.db";
        Bootstrap.initializePathAuthz(pathAuthzDBFileName);

        /**
         * RESTFul Service Start-up
         */
        try {
            RecallTableService.start();
        } catch (IOException e) {
            StoRM.log.error("Unable to start internal HTTP Server listening for RESTFul services");
            e.printStackTrace();
        }

        //
        picker = new AdvancedPicker();
        // this.xmlrpcServer = new SynchCallServer();
        xmlrpcServer = new XMLRPCHttpServer();
    }

    /**
     * Method used to start the picker.
     */
    synchronized public void startPicker() {
        picker.startIt();
    }

    /**
     * Method used to stop the picker.
     */
    synchronized public void stopPicker() {
        picker.stopIt();
    }

    /**
     * Method used to start xmlrpcServer.
     */
    synchronized public void startXmlRpcServer() {
        xmlrpcServer.createServer();
    }

    /**
     * Method use to start the space Garbage Collection Thread.
     */
    synchronized public void startSpaceGC() {
        StoRM.log.debug("Space GC started.");
        final ReservedSpaceCatalog spaceCatalog = new ReservedSpaceCatalog();
        TimerTask cleaningTask = new TimerTask() {
            @Override
            public void run() {
                spaceCatalog.purge();
            }
        };

        long delay = Configuration.getInstance().getCleaningInitialDelay() * 1000; // Delay time before starting
        // cleaning thread! Set to 1 minute
        long period = Configuration.getInstance().getCleaningTimeInterval() * 1000; // Period of execution of cleaning!
        // Set to 1 hour

        GC.scheduleAtFixedRate(cleaningTask, delay, period);

    }

}
