/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.config.xml;

import static java.io.File.separatorChar;

import java.io.File;
import java.io.IOException;

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

import it.grid.storm.namespace.NamespaceValidator;
import it.grid.storm.namespace.config.NamespaceLoader;

public class XMLNamespaceLoader implements NamespaceLoader {

  private static Logger log = LoggerFactory.getLogger(XMLNamespaceLoader.class);

  public String filename;
  public String path;
  public int refresh; // refresh time in seconds before the configuration is
  // checked for a change in parameters!
  private XMLConfiguration config = null;
  private long period = -1;
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

  public void setNotifyManaged() {

    xmlStrategy.notifingPerformed();
    config.setReloadingStrategy(xmlStrategy);
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

      config.load();
      log.debug("Namespace Configuration read!");

    } catch (ConfigurationException cex) {
      log.error("ATTENTION! Unable to load Namespace Configuration!", cex);
      log.error(toString());
    }

  }

  private String getNamespaceFileName() {

    String configurationDir = it.grid.storm.config.StormConfiguration.getInstance().configurationDir();
    // Looking for namespace configuration file
    String namespaceFN =
        it.grid.storm.config.StormConfiguration.getInstance().getNamespaceConfigFilename();
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
        it.grid.storm.config.StormConfiguration.getInstance().getNamespaceSchemaFilename();

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
}
