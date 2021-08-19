/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.namespace;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.NamespaceParser;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;

import static java.io.File.separatorChar;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

public class NamespaceDirector {

  private static final Logger log = LoggerFactory.getLogger(NamespaceDirector.class);

  private static NamespaceInterface namespaceIstance;
  private static NamespaceLoader loader;
  private static NamespaceParser parser;

  private NamespaceDirector() {}

  public static void init() {

    log.info("NAMESPACE : Initializing ...");
    Configuration config = Configuration.getInstance();
    String configurationDir = config.configurationDir();
    String namespaceFileName = config.getNamespaceConfigFilename();
    String namespaceAbsoluteFilePath = getNamespaceFileAbsolutePath(configurationDir, namespaceFileName);

    log.info(" +++++++++++++++++++++++ ");
    log.info("    Production Mode      ");
    log.info(" +++++++++++++++++++++++ ");

    log.debug("Namespace Configuration PATH : {}", configurationDir);
    log.debug("Namespace Configuration FILENAME : {}", namespaceFileName);

    try {
      loader = new XMLNamespaceLoader(namespaceAbsoluteFilePath);
    } catch (DOMException | ConfigurationException | ParserConfigurationException | SAXException
        | IOException | NamespaceException e) {
      log.error(e.getMessage(), e);
      System.exit(1);
    }

    parser = new XMLNamespaceParser(loader);
    namespaceIstance = new Namespace(parser);

    log.debug("NAMESPACE INITIALIZATION : ... done!");
  }

  private static String getNamespaceFileAbsolutePath(String configurationDir, String namespaceFileName) {

    if (configurationDir.charAt(configurationDir.length() - 1) != separatorChar) {
      configurationDir += Character.toString(separatorChar);
    }
    return configurationDir + namespaceFileName;
  }

  public static NamespaceInterface getNamespace() {

    return namespaceIstance;
  }

  public static NamespaceParser getNamespaceParser() {

    return parser;
  }

  public static NamespaceLoader getNamespaceLoader() {

    return loader;
  }

  public static Logger getLogger() {

    return log;
  }

}
