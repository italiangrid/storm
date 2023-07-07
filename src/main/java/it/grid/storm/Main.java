/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm;

import static java.lang.System.exit;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.startup.Bootstrap;
import it.grid.storm.startup.BootstrapException;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static final String DEFAULT_CONFIG_DIR = "/etc/storm/backend-server";
  public static final String DEFAULT_CONFIG_FILE = DEFAULT_CONFIG_DIR + "/storm.properties";
  public static final String DEFAULT_NAMESPACE_FILE = DEFAULT_CONFIG_DIR + "/namespace.xml";
  public static final String DEFAULT_NAMESPACE_SCHEMA_FILE =
      DEFAULT_CONFIG_DIR + "/namespace-1.5.0.xsd";
  public static final String DEFAULT_LOGGING_FILE = DEFAULT_CONFIG_DIR + "/logging.xml";

  private Main() {}

  public static void main(String[] args) {

    log.info("Configure logging from {} ...", DEFAULT_LOGGING_FILE);
    Bootstrap.configureLogging(DEFAULT_LOGGING_FILE);


    log.info("Load configuration from {} ...", DEFAULT_CONFIG_FILE);
    try {
      Configuration.init(DEFAULT_CONFIG_FILE);
    } catch (IOException | ConfigurationException e) {
      log.error(e.getMessage(), e);
      exit(1);
    }

    log.info("Load namespace from {} ...", DEFAULT_NAMESPACE_FILE);
    try {
      Namespace.init(DEFAULT_NAMESPACE_FILE, true);
    } catch (RuntimeException | NamespaceException | ConfigurationException | ParserConfigurationException | SAXException | IOException e) {
      log.error(e.getMessage(), e);
      exit(1);
    }

    StoRM storm = new StoRM(Configuration.getInstance(), Namespace.getInstance());

    try {
      storm.init();
    } catch (BootstrapException e) {
      log.error(e.getMessage(), e);
      exit(1);
    }

    Thread.setDefaultUncaughtExceptionHandler(new StoRMDefaultUncaughtExceptionHandler());

    Runtime.getRuntime().addShutdownHook(new ShutdownHook(storm));

    try {

      storm.startServices();
      log.info("StoRM: Backend services successfully started.");

    } catch (Exception e) {

      log.error("StoRM: error starting storm services: {}", e.getMessage());
      storm.stopServices();
      exit(1);
    }
  }

}
