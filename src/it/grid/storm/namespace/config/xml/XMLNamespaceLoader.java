package it.grid.storm.namespace.config.xml;

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.commons.configuration.*;
import org.apache.commons.logging.*;
import it.grid.storm.namespace.*;
import it.grid.storm.namespace.config.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author Riccardo Zappi
 * @version 1.0
 */
public class XMLNamespaceLoader
    extends Observable implements NamespaceLoader {

    private static Log log = LogFactory.getLog(XMLNamespaceLoader.class);

    public String filename;
    public String path;
    public int refresh; //refresh time in seconds before the configuration is
    //checked for a change in parameters!
    private XMLConfiguration config = null;
    private int delay = 1000; // delay for 5 sec.
    private long period = -1;
    private Timer timer = new Timer();
    private XMLReloadingStrategy xmlStrategy;
    private String namespaceFN = null;
    private boolean verbose = false;
    private String namespaceSchemaURL;

    public boolean schemaValidity = false;

    public XMLNamespaceLoader() {
        //Build the namespaceFileName
        this.namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : "+namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(int refresh) {
        if (refresh < 0) {
            this.refresh = 0;
        }
        else {
            this.refresh = refresh;
        }
        this.namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : "+namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(String filename) {
        this.filename = filename;
        this.namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : "+namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(String path, String filename) {
        this.path = path;
        this.filename = filename;
        this.namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : "+namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(String path, String filename, int refresh, boolean verboseMode) {
        if (refresh < 0) {
            this.refresh = 0;
        }
        else {
            this.refresh = refresh;
        }
        this.path = path;
        this.filename = filename;
        this.namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : "+namespaceSchemaURL);
        this.verbose = verboseMode;
        init(namespaceFN, refresh);
    }

    public void setObserver(Observer obs) {
        this.addObserver(obs);
    }

    public void setNotifyManaged() {
        this.xmlStrategy.notifingPerformed();
        this.config.setReloadingStrategy(this.xmlStrategy);
    }

    public void setVerbosity(boolean verbosity) {
        this.verbose = verbosity;
    }

    /**
     * The setChanged() protected method must overridden to make it public
     */
    public synchronized void setChanged() {
        super.setChanged();
    }

    private void init(String namespaceFileName, int refresh) {
        System.out.println("Reading Namespace configuration file " + namespaceFileName +
                           " and setting refresh rate to " + refresh +
                           " seconds.");

        //create reloading strategy for refresh
        xmlStrategy = new XMLReloadingStrategy();
        period = 3000; //Conversion in millisec.
        log.debug(" Refresh time is " + period + " millisec");
        xmlStrategy.setRefreshDelay(period); //Set to refresh sec the refreshing delay.

        this.namespaceFN = namespaceFileName;

        //specify the properties file and set the reloading strategy for that file
        try {
            config = new XMLConfiguration();
            config.setFileName(namespaceFileName);

            /**
             * Validate the namespace configuration file.. Only with Apache Commons
             * Configuration 1.2+
             *
             * @todo It seems having a problem to load the schema...
             *
             */
            //config.setValidating(true);


            //Validation of Namespace.xml
            log.debug(" ... CHECK of VALIDITY of NAMESPACE Configuration ...");


            schemaValidity = XMLNamespaceLoader.checkValidity(namespaceSchemaURL, namespaceFileName);
            if (! (schemaValidity)) {
                log.error("NAMESPACE IS NOT VALID IN RESPECT OF NAMESPACE SCHEMA! ");
                throw new ConfigurationException("XML is not valid!");
            }
            else {
                log.debug("Namespace is valid in respect of NAMESPACE SCHEMA.");
            }


            // This will throw a ConfigurationException if the XML document does not
            // conform to its DTD.

            config.setReloadingStrategy(xmlStrategy);

            //config.setReloadingStrategy(new FileChangedReloadingStrategy());

            Peeper peeper = new Peeper(this);
            timer.schedule(peeper, delay, period);

            log.debug("Timer initialized");

            config.load();
            log.debug("Namespace Configuration read!");

        }
        catch (ConfigurationException cex) {
            System.err.println("*****************************************************");
            System.err.println("   ATTENTION! Unable to load Namespace Configuration!");
            System.err.println("*****************************************************");
            log.fatal(this, cex);
        }

    }

    private String getNamespaceFileName() {
        return (path + File.separator + filename);
    }

    private String getNamespaceSchemaFileName() {
        return (path + File.separator + "namespace.xsd");
    }

    public Configuration getConfiguration() {
        return config;
    }

    private static boolean checkValidity(String namespaceSchemaURL, String filename) {
        NamespaceValidator validator = new NamespaceValidator();
        boolean valid = validator.validateSchema(namespaceSchemaURL, filename);
        return valid;
    }

    /**
     *
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
    private class Peeper
        extends TimerTask {

        private XMLReloadingStrategy reloadingStrategy;

        private boolean signal;
        private XMLNamespaceLoader observed;

        public Peeper(XMLNamespaceLoader obs) {
            this.observed = obs;
        }

        public void run() {
            //log.debug(" The glange of peeper..");
            reloadingStrategy = (XMLReloadingStrategy) config.getReloadingStrategy();
            if (verbose) {
                File xmlFile = reloadingStrategy.getConfigurationFile();
                log.debug(" Peeper glance on XMLReloadingStrategy bound with FILE : " + xmlFile.getName());
                long lastFileModified = xmlFile.lastModified();
                Date dateFile = new Date(lastFileModified);
                long lastFileModifiedReload = reloadingStrategy.getLastReload();
                reloadingStrategy.reloadingPerformed();
                Date dateReload = new Date(lastFileModifiedReload);
                if (lastFileModifiedReload < lastFileModified) {
                    log.debug("RELOAD NEEDED!");
                    Format formatter = new SimpleDateFormat("HH.mm.ss  dd.MM.yyyy");
                    log.debug(" FILE XML Last Modified : " + formatter.format(dateFile));
                    log.debug(" FILE XML Last RELOAD : " + formatter.format(dateReload));
                }
            }
            boolean changed = reloadingStrategy.reloadingRequired();
            if (changed) {
                log.debug(" NAMESPACE CONFIGURATION is changed ! ");
                log.debug(" ... CHECK of VALIDITY of NAMESPACE Configuration ...");
                boolean valid = XMLNamespaceLoader.checkValidity(namespaceSchemaURL, namespaceFN);
                if (!valid) {
                    log.debug(" Namespace configuration is not reloaded.. Please rectify the error.");
                    schemaValidity = false;
                    reloadingStrategy.notifingPerformed();
                    reloadingStrategy.reloadingPerformed();
                }
                else {
                    log.debug(" ... NAMESPACE Configuration is VALID in respect of Schema Grammar.");
                    log.debug(" ----> RELOADING  ");

                    schemaValidity = true;

                    boolean forceReloading = it.grid.storm.config.Configuration.getInstance().getNamespaceAutomaticReloading();
                    if (forceReloading) {
                        config.reload();
                    }
                    else {
                        log.debug(" ----> RELOAD of namespace don't be executed because NO AUTOMATIC RELOAD is configured.");
                    }
                    reloadingStrategy.reloadingPerformed();
                }
            }

            signal = reloadingStrategy.notifingRequired();
            if ( (signal)) {
                observed.setChanged();
                observed.notifyObservers(" MSG : Namespace is changed!");
                reloadingStrategy.notifingPerformed();
            }

        }

    }

}
