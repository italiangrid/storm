/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.config.xml;

import it.grid.storm.namespace.NamespaceValidator;
import it.grid.storm.namespace.config.NamespaceLoader;

import static java.io.File.separatorChar;

import java.io.File;
import java.io.IOException;
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

public class XMLNamespaceLoader extends Observable implements NamespaceLoader {

  private static Logger log = LoggerFactory.getLogger(XMLNamespaceLoader.class);

  public String filename;
  public String path;
  public int refresh; // refresh time in seconds before the configuration is
  // checked for a change in parameters!
  private XMLConfiguration config = null;
  private final int delay = 1000; // delay for 5 sec.
  private long period = -1;
  private final Timer timer = new Timer();
  private XMLReloadingStrategy xmlStrategy;
  private String namespaceFN = null;
  private final String namespaceSchemaURL;

  public boolean schemaValidity = false;

  public XMLNamespaceLoader() {

    // Build the namespaceFileName
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
    log.debug("Namespace XSD : {}", namespaceSchemaURL);
    init(namespaceFN, refresh);
  }

  public XMLNamespaceLoader(String filename) {

    this.filename = filename;
    namespaceFN = getNamespaceFileName();
    namespaceSchemaURL = getNamespaceSchemaFileName();
    log.debug("Namespace XSD : {}", namespaceSchemaURL);
    init(namespaceFN, refresh);
  }

  public XMLNamespaceLoader(String path, String filename) {

    this.path = path;
    this.filename = filename;
    namespaceFN = getNamespaceFileName();
    namespaceSchemaURL = getNamespaceSchemaFileName();
    log.debug("Namespace XSD : {}", namespaceSchemaURL);
    init(namespaceFN, refresh);
  }

  public XMLNamespaceLoader(String path, String filename, int refresh) {

    if (refresh < 0) {
      this.refresh = 0;
    } else {
      this.refresh = refresh;
    }
    this.path = path;
    this.filename = filename;
    namespaceFN = getNamespaceFileName();
    namespaceSchemaURL = getNamespaceSchemaFileName();
    log.debug("Namespace XSD : {}", namespaceSchemaURL);
    init(namespaceFN, refresh);
  }

  public void setObserver(Observer obs) {

    addObserver(obs);
  }

  public void setNotifyManaged() {

    xmlStrategy.notifingPerformed();
    config.setReloadingStrategy(xmlStrategy);
  }

  /**
   * The setChanged() protected method must overridden to make it public
   */
  @Override
  public synchronized void setChanged() {

    super.setChanged();
  }

  private void init(String namespaceFileName, int refresh) {

    log.info("Reading Namespace configuration file {} and setting refresh rate to {} seconds.",
        namespaceFileName, refresh);

    // create reloading strategy for refresh
    xmlStrategy = new XMLReloadingStrategy();
    period = 3000; // Conversion in millisec.
    log.debug(" Refresh time is {} millisec", period);
    xmlStrategy.setRefreshDelay(period); // Set to refresh sec the refreshing delay.

    namespaceFN = namespaceFileName;

    // specify the properties file and set the reloading strategy for that file
    try {
      config = new XMLConfiguration();
      config.setFileName(namespaceFileName);

      // Validation of Namespace.xml
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

      Peeper peeper = new Peeper(this);
      timer.schedule(peeper, delay, period);

      log.debug("Timer initialized");

      config.load();
      log.debug("Namespace Configuration read!");

    } catch (ConfigurationException cex) {
      log.error("ATTENTION! Unable to load Namespace Configuration!", cex);
      log.error(toString());
    }

  }

  private String getNamespaceFileName() {

    String configurationDir = it.grid.storm.config.Configuration.getInstance().configurationDir();
    // Looking for namespace configuration file
    String namespaceFN =
        it.grid.storm.config.Configuration.getInstance().getNamespaceConfigFilename();
    // Build the filename
    if (configurationDir.charAt(configurationDir.length() - 1) != separatorChar) {
      configurationDir += Character.toString(separatorChar);
    }
    String namespaceAbsFN = configurationDir + namespaceFN;
    // Check the namespace conf file accessibility
    File nsFile = new File(namespaceAbsFN);
    if (nsFile.exists()) {
      log.debug("Found the namespace file : {}", namespaceAbsFN);
    } else {
      log.error("Unable to find the namespace file : {}", namespaceAbsFN);
    }
    return namespaceAbsFN;
  }

  private String getNamespaceSchemaFileName() {

    String schemaName =
        it.grid.storm.config.Configuration.getInstance().getNamespaceSchemaFilename();

    if ("Schema UNKNOWN!".equals(schemaName)) {

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
          if ("namespace".equals(tagName)) {
            if (rootElement.hasAttributes()) {
              String value = rootElement.getAttribute("xsi:noNamespaceSchemaLocation");
              if ((value != null) && (value.length() > 0)) {
                schemaName = value;
              }
            } else {
              log.error("{} don't have a valid root element attributes", namespaceFN);
            }
          } else {
            log.error("{} don't have a valid root element.", namespaceFN);
          }

        } catch (ParserConfigurationException | SAXException | IOException e) {
          log.error("Error while parsing {}: {}", namespaceFN, e.getMessage(), e);
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
    return validator.validateSchema(namespaceSchemaURL, filename);
  }

  /**
   * 
   * <p>
   * Title:
   * </p>
   * 
   * <p>
   * Description:
   * </p>
   * 
   * <p>
   * Copyright: Copyright (c) 2006
   * </p>
   * 
   * <p>
   * Company: INFN-CNAF and ICTP/eGrid project
   * </p>
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

      // log.debug(" The glange of peeper..");
      reloadingStrategy = (XMLReloadingStrategy) config.getReloadingStrategy();
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
            log.debug(
                " ----> RELOAD of namespace don't be executed because NO AUTOMATIC RELOAD is configured.");
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
