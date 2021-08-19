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

package it.grid.storm.namespace.config.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.NamespaceValidator;
import it.grid.storm.namespace.config.NamespaceLoader;

public class XMLNamespaceLoader extends Observable implements NamespaceLoader {

  private static Logger log = LoggerFactory.getLogger(XMLNamespaceLoader.class);

  private static final String ROOT_ELEMENT = "namespace";
  private static final String XML_SCHEMA_ATTRIBUTE = "xsi:noNamespaceSchemaLocation";

  private final String namespaceAbsoluteFilePath;
  private final String namespaceSchemaURL;

  private XMLConfiguration config;

  public XMLNamespaceLoader(String namespaceFilePath)
      throws DOMException, ParserConfigurationException, SAXException, IOException,
      NamespaceException, ConfigurationException {

    File namespace = new File(namespaceFilePath);
    if (!namespace.exists()) {
      throw new FileNotFoundException("Namespace file '" + namespaceFilePath + "' not found!");
    }
    namespaceAbsoluteFilePath = namespaceFilePath;
    namespaceSchemaURL = getNamespaceSchemaUrlFromNamespaceFile();
    log.debug("Namespace XSD : {}", namespaceSchemaURL);

    if (checkValidity(namespaceAbsoluteFilePath, namespaceSchemaURL)) {
      log.debug("Namespace file '{}' is valid in respect of namespace schema '{}'.",
          namespaceAbsoluteFilePath, namespaceSchemaURL);
    } else {
      String errorMessage = String.format("Namespace %s is NOT VALID in respect of %s schema.",
          namespaceAbsoluteFilePath, namespaceSchemaURL);
      log.error(errorMessage);
      throw new NamespaceException(errorMessage);
    }

    init();
  }

  private void init() throws ConfigurationException {

    config = new XMLConfiguration();
    config.setFileName(namespaceAbsoluteFilePath);

    log.debug("Timer initialized");

    config.load();
    log.debug("Namespace Configuration read!");
  }

  private String getNamespaceSchemaUrlFromNamespaceFile() throws DOMException,
      ParserConfigurationException, SAXException, IOException, NamespaceException {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(namespaceAbsoluteFilePath);
    Element rootElement = doc.getDocumentElement();
    String tagName = rootElement.getTagName();
    if (!ROOT_ELEMENT.equals(tagName)) {
      String errorMessage = String.format("Invalid root for %s: 'namespace' element not found",
          namespaceAbsoluteFilePath);
      log.error(errorMessage);
      throw new NamespaceException(errorMessage);
    }
    if (!rootElement.hasAttributes()) {
      String errorMessage =
          String.format("Invalid root for %s: no attributes found", namespaceAbsoluteFilePath);
      log.error(errorMessage);
      throw new NamespaceException(errorMessage);
    }
    String value = rootElement.getAttribute(XML_SCHEMA_ATTRIBUTE);
    if (value == null || value.isEmpty()) {
      String errorMessage = String.format("Invalid root for %s: attribute %s not found",
          namespaceAbsoluteFilePath, XML_SCHEMA_ATTRIBUTE);
      log.error(errorMessage);
      throw new NamespaceException(errorMessage);
    }
    return value;
  }

  public Configuration getConfiguration() {

    return config;
  }

  private boolean checkValidity(String filename, String namespaceSchemaURL) {

    NamespaceValidator validator = new NamespaceValidator();
    return validator.validateSchema(namespaceSchemaURL, filename);
  }

}
