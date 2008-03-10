package it.grid.storm;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.*;
import it.grid.storm.asynch.AdvancedPicker;

import it.grid.storm.synchcall.SynchCallServer;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.config.Configuration;
import it.grid.storm.config.ConfigReader;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.health.HealthDirector;

/**
 * This class represents a StoRM as a whole: it sets the configuration file which
 * contains properties necessary for other classes of StoRM, it sets up logging,
 * as well as the advanced picker.
 *
 * @author  EGRID - ICTP Trieste; INFN - CNAF Bologna
 * @date    March 28th, 2005
 * @version 7.0
 */
public class StoRM {
    private AdvancedPicker picker = null; //Picker of StoRM

    private SynchCallServer xmlrpcServer=null;           //Synchronous calls server of StoRM!
    private StringBuffer welcome = welcomeText(); //Text that displays general info about StoRM project
    private StringBuffer defaultConfig = defaultText(); //Text that displays StoRM built in values
    private static final Logger log = Logger.getLogger("stormBoot");
    private final Timer GC = new Timer(); //Timer object in charge to call periodically the Space Garbace Collector


    /**
     * Public constructor that requires a String containing the complete pathname
     * to the configuration file, as well as the desired refresh rate in seconds
     * for changes in configuration.
     *
     * Beware that by pathname it is meant the complete path starting from root,
     * including the name of the file itself!
     *
     * If pathname is empty or null, then an attempt will be made to read properties
     * off /opt/storm/etc/storm.properties. BEWARE!!! For MS Windows installations
     * this attempt _will_ fail!
     *
     * In any case, failure to read the configuratin file causes StoRM to use
     * hardcoded default values.
     */
    public StoRM(String configurationPathname, int refresh) {
        //verifying supplied configurationPathname and print to screen...
        if ((configurationPathname==null) || (configurationPathname=="")) {
            //built-in configuration file to be used if nothing gets specified!
            configurationPathname="/opt/storm/etc/storm.properties";
            System.out.print("This instance of StoRM Backend was invoked without explicitly specifying ");
            System.out.print("a configuration file. Looking for the standard one in ");
            System.out.println(configurationPathname);
        } else {
            //look for given configuration file...
            System.out.print("Looking for configuration file ");
            System.out.println(configurationPathname);
        }
        //load properties from configuration...
        Configuration.getInstance().setConfigReader(new ConfigReader(configurationPathname,refresh));
        //set and print current configuration string...
        StringBuffer currentConfig = new StringBuffer();
        currentConfig.append(Configuration.getInstance().toString());
        System.out.println("\nCurrent configuration:");
        System.out.println(currentConfig.toString());
        //print welcome
        System.out.println();
        System.out.println(welcome.toString());
        //Start logger!
        PropertyConfigurator.configure(Configuration.getInstance().getConfigurationDir()+"log4j.properties");
        //
        log.fatal(welcome.toString()); //log welcome string!
        log.debug(defaultConfig.toString()); //log default values!
        log.info("CurrentConfiguration:\n" + currentConfig.toString()); //log actually used values!

        //Force the loadind and the parsing of Namespace configuration
        boolean verboseMode = false; //true generates verbose logging
        boolean testingMode = false; //True if you wants testing namespace
        NamespaceDirector.initializeDirector(verboseMode, testingMode);

        //Hearthbeat
        HealthDirector.initializeDirector(false);

        //
        this.picker = new AdvancedPicker();
	this.xmlrpcServer = new SynchCallServer();
    }

    /**
     * Auxiliary method that returns a StringBuffer with a welcome text.
     */
    private StringBuffer welcomeText() {
        StringBuffer welcome = new StringBuffer();
        welcome.append("StoRM Backend Server\n\n");
        //
        welcome.append("This is the backend part of an SRM v2.2 implementation, resulting from a ");
        welcome.append("collaboration between The Abdus Salam International Centre for Theoretical ");
        welcome.append("Physics ICTP - EGRID Project; and the Istituto Nazionale di Fisica Nucleare ");
        welcome.append("INFN-CNAF - grid.IT Project.\n\n");
        //
        welcome.append("For support for installations of StoRM in Economics/Finance communities, ");
        welcome.append("please contact:\n");
        welcome.append("The Abdus Salam International Centre for Theoretical Physics\n");
        welcome.append("Scientific Computing Section - EGRID Project\n");
        welcome.append("Strada Costiera, 11\n");
        welcome.append("34016 Trieste\n");
        welcome.append("Italy\n");
        welcome.append("http://www.ictp.it/\n");
        welcome.append("staff@egrid.it\n");
        welcome.append("http://www.egrid.it/\n\n");
        //
        welcome.append("For support for installations of StoRM in Physics communities, ");
        welcome.append("please contact:\n");
        welcome.append("Istituto Nazionale di Fisica Nucleare - CNAF\n");
        welcome.append("Viale Berti Pichat, 6/2\n");
        welcome.append("40127 Bologna\n");
        welcome.append("Italy\n");
        welcome.append("http://www.cnaf.infn.it/\n");
        //
        return welcome;
    }

    /**
     * Auxiliary method that returns a StringBuffer containing a text with
     * StoRM s built in default values.
     */
    private StringBuffer defaultText() {
        StringBuffer defaultValuesText = new StringBuffer();
        defaultValuesText.append("StoRM Backend internal list of default values used if no configuration ");
        defaultValuesText.append("medium is supplied, or if properties in such medium do not override ");
        defaultValuesText.append("internally set ones:\n");
        defaultValuesText.append(Configuration.getInstance().toString());
        return defaultValuesText;
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
     *
     */
    synchronized public void startSpaceGC() {
    	log.debug("Space GC started.");
    	final ReservedSpaceCatalog  spaceCatalog = new ReservedSpaceCatalog();
    	TimerTask cleaningTask = new TimerTask() {
            public void run() {
                spaceCatalog.purge();
            }
        };

        long delay = Configuration.getInstance().getCleaningInitialDelay()*1000;  //Delay time before starting cleaning thread! Set to 1 minute
        long period = Configuration.getInstance().getCleaningTimeInterval()*1000; //Period of execution of cleaning! Set to 1 hour

        GC.scheduleAtFixedRate(cleaningTask,delay,period);

    }

}
