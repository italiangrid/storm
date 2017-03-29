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

package it.grid.storm;

import static it.grid.storm.rest.RestService.startServer;
import static it.grid.storm.rest.RestService.stop;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.catalogs.StoRMDataSource;
import it.grid.storm.check.CheckManager;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.check.SimpleCheckManager;
import it.grid.storm.config.ConfigReader;
import it.grid.storm.config.Configuration;
import it.grid.storm.health.HealthDirector;
import it.grid.storm.metrics.StormMetricRegistry;
import it.grid.storm.metrics.StormMetricsReporter;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.startup.Bootstrap;
import it.grid.storm.startup.BootstrapException;
import it.grid.storm.synchcall.SimpleSynchcallDispatcher;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;
import it.grid.storm.xmlrpc.XMLRPCHttpServer;

/**
 * This class represents a StoRM as a whole: it sets the configuration file
 * which contains properties necessary for other classes of StoRM, it sets up
 * logging, as well as the advanced picker.
 * 
 * @author EGRID - ICTP Trieste; INFN - CNAF Bologna @date March 28th,
 * 2005 @version 7.0
 */

public class StoRM {

  private AdvancedPicker picker = null;

  private XMLRPCHttpServer xmlrpcServer = null;

  private static final Logger log = LoggerFactory.getLogger(StoRM.class);

  public static final String DEFAULT_CONFIGURATION_FILE_PATH = "/etc/storm/backend-server/storm.properties";

  // Timer object in charge to call periodically the Space Garbace Collector
  private final Timer GC = new Timer();

  private final ReservedSpaceCatalog spaceCatalog;
  private TimerTask cleaningTask = null;
  private boolean isPickerRunning = false;
  private boolean isXmlrpcServerRunning = false;
  private boolean isRestServerRunning = false;
  private boolean isSpaceGCRunning = false;

  private void loadConfiguration(String configurationPathname, int refresh) {

    if ((configurationPathname == null) || (configurationPathname.equals(""))) {
      configurationPathname = DEFAULT_CONFIGURATION_FILE_PATH;
    }

    log.info("Loading backend configuration from '{}'", configurationPathname);
    log.info("Configuration refresh rate (in secs): {}", refresh);

    Configuration.getInstance()
      .setConfigReader(new ConfigReader(configurationPathname, refresh));

  }

  private void configureLogging() {

    String configurationDir = Configuration.getInstance().configurationDir();
    String logFile = configurationDir + "logging.xml";
    Bootstrap.configureLogging(logFile);
  }

  private void configureMetricsReporting() {
    
    StormMetricRegistry.INSTANCE.getRegistry()
    .timer(SimpleSynchcallDispatcher.SYNCH_CALL_TIMER_NAME);

    final StormMetricsReporter metricsReporter = StormMetricsReporter
      .forRegistry(StormMetricRegistry.INSTANCE.getRegistry()).build();

    metricsReporter.start(1, TimeUnit.MINUTES);

  }

  private void loadNamespaceConfiguration() {

    boolean verboseMode = false; // true generates verbose logging
    boolean testingMode = false; // True if you wants testing namespace
    NamespaceDirector.initializeDirector(verboseMode, testingMode);

  }

  private void loadPathAuthzDBConfiguration() {

    String pathAuthzDBFileName = Configuration.getInstance().configurationDir()
      + "path-authz.db";

    try {
      Bootstrap.initializePathAuthz(pathAuthzDBFileName);
    } catch (BootstrapException e) {
      log.error(
        "Unable to initialize the Path Authorization manager. BootstrapException: "
          + e.getMessage(),
        e);

      throw new RuntimeException(
        "Unable to initialize the Path Authorization manager", e);
    }
  }

  private void configureGridHTTPSPlugin() {

    if (Configuration.getInstance().getGridhttpsEnabled()) {

      log.info("Initializing the https plugin");

      String httpsFactoryName = Configuration.getInstance()
        .getGRIDHTTPSPluginClassName();

      Bootstrap.initializeAclManager(httpsFactoryName,
        LoggerFactory.getLogger(Bootstrap.class));
    }

  }

  private void configureXMLRPCService() {

    try {

      xmlrpcServer = new XMLRPCHttpServer(
        Configuration.getInstance().getXmlRpcServerPort(),
        Configuration.getInstance().getMaxXMLRPCThread());

    } catch (StoRMXmlRpcException e) {

      log.error(e.getMessage(), e);

      throw new RuntimeException(e.getMessage(), e);
    }

  }

  private void performSanityChecks() {

    if (Configuration.getInstance().getSanityCheckEnabled()) {

      CheckManager checkManager = new SimpleCheckManager();
      checkManager.init();
      CheckResponse checkResponse = checkManager.lauchChecks();

      if (checkResponse.isSuccessfull()) {
        log.info("Check suite executed successfully");
      } else {
        if (checkResponse.getStatus().equals(CheckStatus.CRITICAL_FAILURE)) {
          log.error("Storm Check suite is failed for some critical checks!");
          throw new RuntimeException(
            "Storm Check suite is failed for some critical checks! Please check the log for more details");
        } else {
          log.warn(
            "Storm Check suite is failed but not for any critical check. StoRM safely started.");
        }
      }

    } else {
      log.warn(
        "Sanity checks disabled. Unable to determine if the environment is sane");
    }

  }

  /**
   * Public constructor that requires a String containing the complete pathname
   * to the configuration file, as well as the desired refresh rate in seconds
   * for changes in configuration. Beware that by pathname it is meant the
   * complete path starting from root, including the name of the file itself! If
   * pathname is empty or null, then an attempt will be made to read properties
   * off /opt/storm/etc/storm.properties. BEWARE!!! For MS Windows installations
   * this attempt _will_ fail! In any case, failure to read the configuratin
   * file causes StoRM to use hardcoded default values.
   */
  public StoRM(String configurationPathname, int refresh) {

    loadConfiguration(configurationPathname, refresh);

    configureLogging();

    configureMetricsReporting();

    configureStoRMDataSource();

    // Start space catalog
    spaceCatalog = new ReservedSpaceCatalog();

    loadNamespaceConfiguration();

    HealthDirector.initializeDirector(false);

    loadPathAuthzDBConfiguration();

    // Initialize Used Space
    Bootstrap.initializeUsedSpace();

    configureGridHTTPSPlugin();

    // Start the "advanced" picker
    picker = new AdvancedPicker();

    configureXMLRPCService();

    performSanityChecks();

  }

  private void configureStoRMDataSource() {

    StoRMDataSource.init();
  }

  /**
   * Method used to start the picker.
   */
  synchronized public void startPicker() {

    picker.startIt();
    isPickerRunning = true;
  }

  /**
   * Method used to stop the picker.
   */
  synchronized public void stopPicker() {

    picker.stopIt();
    isPickerRunning = false;
  }

  /**
   * @return
   */
  public synchronized boolean pickerIsRunning() {

    return isPickerRunning;
  }

  /**
   * Method used to start xmlrpcServer.
   * 
   * @throws Exception
   */
  synchronized public void startXmlRpcServer() throws Exception {

    xmlrpcServer.start();
    isXmlrpcServerRunning = true;
  }

  /**
   * Method used to stop xmlrpcServer.
   */
  synchronized public void stopXmlRpcServer() {

    xmlrpcServer.stop();
    isXmlrpcServerRunning = false;
  }

  /**
   * @return
   */
  public synchronized boolean xmlRpcServerIsRunning() {

    return isXmlrpcServerRunning;
  }

  /**
   * RESTFul Service Start-up
   */
  synchronized public void startRestServer() throws Exception {

    startServer();
    isRestServerRunning = true;
  }

  /**
   * @throws Exception
   */
  synchronized public void stopRestServer() {

    try {

      stop();

    } catch (Exception e) {

      log.error("Unable to stop internal HTTP Server listening for RESTFul "
        + "services: {}", e.getMessage(), e);
    }

    isRestServerRunning = false;
  }

  /**
   * @return
   */
  public synchronized boolean restServerIsRunning() {

    return isRestServerRunning;
  }

  /**
   * Method use to start the space Garbage Collection Thread.
   */
  synchronized public void startSpaceGC() {

    StoRM.log.debug("Starting Space GC.");
    // Delay time before starting
    long delay = Configuration.getInstance().getCleaningInitialDelay() * 1000;

    // cleaning thread! Set to 1 minute
    // Period of execution of cleaning
    long period = Configuration.getInstance().getCleaningTimeInterval() * 1000;

    // Set to 1 hour
    cleaningTask = new TimerTask() {

      @Override
      public void run() {

        spaceCatalog.purge();
      }
    };
    GC.scheduleAtFixedRate(cleaningTask, delay, period);
    isSpaceGCRunning = true;
    log.debug("Space GC started.");
  }

  /**
     * 
     */
  synchronized public void stopSpaceGC() {

    log.debug("Stopping Space GC.");
    if (cleaningTask != null) {
      cleaningTask.cancel();
      GC.purge();
    }
    log.debug("Space GC stopped.");
    isSpaceGCRunning = false;
  }

  /**
   * @return
   */
  public synchronized boolean spaceGCIsRunning() {

    return isSpaceGCRunning;
  }
}
