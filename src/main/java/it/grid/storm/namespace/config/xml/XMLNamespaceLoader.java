/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.namespace.config.xml;

import it.grid.storm.namespace.NamespaceValidator;
import it.grid.storm.namespace.config.NamespaceLoader;

import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
public class XMLNamespaceLoader extends Observable implements NamespaceLoader {

    private static Logger log = LoggerFactory.getLogger(XMLNamespaceLoader.class);

    public String filename;
    public String path;
    public int refresh; //refresh time in seconds before the configuration is
    //checked for a change in parameters!
    private XMLConfiguration config = null;
    private final int delay = 1000; // delay for 5 sec.
    private long period = -1;
    private final Timer timer = new Timer();
    private XMLReloadingStrategy xmlStrategy;
    private String namespaceFN = null;
    private boolean verbose = false;
    private final String namespaceSchemaURL;

    public boolean schemaValidity = false;

    public XMLNamespaceLoader() {
        //Build the namespaceFileName
        namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(int refresh) {
        if (refresh < 0) {
            this.refresh = 0;
        } else {
            this.refresh = refresh;
        }
        namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : " + namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(String filename) {
        this.filename = filename;
        namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : " + namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(String path, String filename) {
        this.path = path;
        this.filename = filename;
        namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : " + namespaceSchemaURL);
        init(namespaceFN, refresh);
    }

    public XMLNamespaceLoader(String path, String filename, int refresh, boolean verboseMode) {
        if (refresh < 0) {
            this.refresh = 0;
        } else {
            this.refresh = refresh;
        }
        this.path = path;
        this.filename = filename;
        namespaceFN = getNamespaceFileName();
        namespaceSchemaURL = getNamespaceSchemaFileName();
        log.debug("Namespace XSD : " + namespaceSchemaURL);
        verbose = verboseMode;
        init(namespaceFN, refresh);
    }

    public void setObserver(Observer obs) {
        addObserver(obs);
    }

    public void setNotifyManaged() {
        xmlStrategy.notifingPerformed();
        config.setReloadingStrategy(xmlStrategy);
    }

    public void setVerbosity(boolean verbosity) {
        verbose = verbosity;
    }

    /**
     * The setChanged() protected method must overridden to make it public
     */
    @Override
    public synchronized void setChanged() {
        super.setChanged();
    }

    private void init(String namespaceFileName, int refresh) {
        System.out.println("Reading Namespace configuration file " + namespaceFileName
                + " and setting refresh rate to " + refresh + " seconds.");

        //create reloading strategy for refresh
        xmlStrategy = new XMLReloadingStrategy();
        period = 3000; //Conversion in millisec.
        log.debug(" Refresh time is " + period + " millisec");
        xmlStrategy.setRefreshDelay(period); //Set to refresh sec the refreshing delay.

        namespaceFN = namespaceFileName;

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
            if (!(schemaValidity)) {
                log.error("NAMESPACE IS NOT VALID IN RESPECT OF NAMESPACE SCHEMA! ");
                throw new ConfigurationException("XML is not valid!");
            } else {
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

        } catch (ConfigurationException cex) {
            System.err.println("*****************************************************");
            System.err.println("   ATTENTION! Unable to load Namespace Configuration!");
            System.err.println("*****************************************************");
            log.error(toString());
        }

    }
    
    private String getNamespaceFileName() {
    	String configurationDir = it.grid.storm.config.Configuration.getInstance().configurationDir();
    	//Looking for namespace configuration file
    	String namespaceFN = it.grid.storm.config.Configuration.getInstance().getNamespaceConfigFilename();
    	//Build the filename
    	if(configurationDir.charAt(configurationDir.length() -1) != File.separatorChar)
    	{
    	    configurationDir += File.separatorChar;
    	}
    	String namespaceAbsFN = configurationDir + namespaceFN;
    	//Check the namespace conf file accessibility
    	File nsFile = new File(namespaceAbsFN);
    	if (nsFile.exists()) {
    		log.debug("Found the namespace file : "+namespaceAbsFN);
    	} else {
    		log.error("Unable to find the namespace file :"+namespaceAbsFN);
    	}
        return namespaceAbsFN; 
    }

    private String getNamespaceSchemaFileName() {
        String schemaName = it.grid.storm.config.Configuration.getInstance().getNamespaceSchemaFilename();
       
        if (schemaName.equals("Schema UNKNOWN!")) {
        	
        	schemaName = "namespace.xsd";
        	String namespaceFN = getNamespaceFileName();
            File namespaceFile = new File(namespaceFN);
            if (namespaceFile.exists()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(namespaceFN);
                    Element rootElement = doc.getDocumentElement();
                    String tagName = rootElement.getTagName();
                    if (tagName.equals("namespace")) {
                        if (rootElement.hasAttributes()) {
                            String value = rootElement.getAttribute("xsi:noNamespaceSchemaLocation");
                            if ((value != null) && (value.length() > 0)) {
                            	schemaName = value;
                                //log.debug("namespace schema is : " + schemaName);
                            }
                        } else {
                            log.error(namespaceFN + " don't have a valid root element attributes");
                        }
                    } else {
                        log.error(namespaceFN + "  don't have a valid root element.");
                    }

                } catch (ParserConfigurationException e) {
                    log.error("Error while parsing " + namespaceFN + e.getMessage());
                } catch (SAXException e) {
                    log.error("Error while parsing " + namespaceFN  + e.getMessage());
                } catch (IOException e) {
                    log.error("Error while parsing " + namespaceFN  + e.getMessage());
                }
            }	
        }
        
        return schemaName;
        
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
    private class Peeper extends TimerTask {

        private XMLReloadingStrategy reloadingStrategy;

        private boolean signal;
        private final XMLNamespaceLoader observed;

        public Peeper(XMLNamespaceLoader obs) {
            observed = obs;
        }

        @Override
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
                } else {
                    log.debug(" ... NAMESPACE Configuration is VALID in respect of Schema Grammar.");
                    log.debug(" ----> RELOADING  ");

                    schemaValidity = true;

                    boolean forceReloading =
                            it.grid.storm.config.Configuration.getInstance().getNamespaceAutomaticReloading();
                    if (forceReloading) {
                        config.reload();
                    } else {
                        log.debug(" ----> RELOAD of namespace don't be executed because NO AUTOMATIC RELOAD is configured.");
                    }
                    reloadingStrategy.reloadingPerformed();
                }
            }

            signal = reloadingStrategy.notifingRequired();
            if ((signal)) {
                observed.setChanged();
                observed.notifyObservers(" MSG : Namespace is changed!");
                reloadingStrategy.notifingPerformed();
            }

        }

    }

}
