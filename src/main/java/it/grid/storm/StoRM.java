/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm;

import static it.grid.storm.metrics.StormMetricRegistry.METRIC_REGISTRY;
import static java.lang.String.valueOf;
import static java.security.Security.setProperty;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.grid.storm.asynch.AdvancedPicker;
import it.grid.storm.catalogs.ReservedSpaceCatalog;
import it.grid.storm.catalogs.executors.RequestFinalizerService;
import it.grid.storm.catalogs.timertasks.RequestsGarbageCollector;
import it.grid.storm.check.CheckManager;
import it.grid.storm.check.CheckResponse;
import it.grid.storm.check.CheckStatus;
import it.grid.storm.check.SimpleCheckManager;
import it.grid.storm.check.sanity.filesystem.SupportedFSType;
import it.grid.storm.config.Configuration;
import it.grid.storm.health.HealthMonitor;
import it.grid.storm.info.du.DiskUsageService;
import it.grid.storm.metrics.StormMetricsReporter;
import it.grid.storm.namespace.Namespace;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.model.Property;
import it.grid.storm.namespace.model.Property.SizeUnitType;
import it.grid.storm.namespace.model.Quota;
import it.grid.storm.namespace.model.VirtualFS;
import it.grid.storm.rest.RestServer;
import it.grid.storm.space.SpaceHelper;
import it.grid.storm.space.gpfsquota.GPFSFilesetQuotaInfo;
import it.grid.storm.space.gpfsquota.GPFSQuotaManager;
import it.grid.storm.space.gpfsquota.GetGPFSFilesetQuotaInfoCommand;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.startup.Bootstrap;
import it.grid.storm.startup.BootstrapException;
import it.grid.storm.synchcall.SimpleSynchcallDispatcher;
import it.grid.storm.util.GPFSSizeHelper;
import it.grid.storm.xmlrpc.StoRMXmlRpcException;
import it.grid.storm.xmlrpc.XMLRPCHttpServer;

public class StoRM {

  private static final Logger log = LoggerFactory.getLogger(StoRM.class);

  private AdvancedPicker picker;
  private XMLRPCHttpServer xmlrpcServer;

  // Timer object in charge to call periodically the Space Garbage Collector
  private final Timer gc;
  private TimerTask cleaningTask;
  private boolean isSpaceGCRunning;

  /*
   * Agent in charge of transit expired ptg/ptp/bol requests to final statuses
   */
  private RequestFinalizerService expiredAgent;
  private boolean isExpiredAgentRunning;

  /* Requests Garbage Collector */
  private final Timer rgc;
  private TimerTask rgcTask;
  private boolean isRequestGCRunning;
  
  private boolean isDiskUsageServiceEnabled;
  private DiskUsageService duService;

  private boolean isPickerRunning;
  private boolean isXmlrpcServerRunning;

  private boolean isRestServerRunning;
  private RestServer restServer;

  private final Configuration config;
  private final ReservedSpaceCatalog spaceCatalog;
  private final Namespace namespace;

  public StoRM(Configuration config, Namespace namespace) {

    this.config = config;
    this.namespace = namespace;
    this.spaceCatalog = ReservedSpaceCatalog.getInstance();

    this.picker = new AdvancedPicker();
    this.isPickerRunning = false;

    this.isXmlrpcServerRunning = false;

    this.isRestServerRunning = false;

    this.gc = new Timer();
    this.isSpaceGCRunning = false;
    this.isExpiredAgentRunning = false;

    this.rgc = new Timer();
    this.isRequestGCRunning = false;

    this.isDiskUsageServiceEnabled = false;
  }

  public void init() throws BootstrapException {

    configureIPv6();

    handleTotalOnlineSizeFromGPFSQuota();

    updateSA();

    configureSecurity();

    configureMetricsReporting();

    HealthMonitor.init();

    loadPathAuthzDBConfiguration();

    Bootstrap.initializeUsedSpace();

    configureXMLRPCService();

    configureRestService();

    configureDiskUsageService();

    performSanityChecks();

  }

  private void configureIPv6() {

    log.debug("java.net.preferIPv6Addresses is {}", System.getProperty("java.net.preferIPv6Addresses"));
    System.setProperty("java.net.preferIPv6Addresses", String.valueOf(config.getPreferIPv6Addresses()));
    log.info("java.net.preferIPv6Addresses is {}", System.getProperty("java.net.preferIPv6Addresses"));
  }

  private void handleTotalOnlineSizeFromGPFSQuota() {

    namespace.getAllDefinedVFS().forEach(storageArea -> {
      if (SupportedFSType.parseFS(storageArea.getFSType()) == SupportedFSType.GPFS) {
        Quota quota = storageArea.getCapabilities().getQuota();
        if (quota != null && quota.getEnabled()) {

          GPFSFilesetQuotaInfo quotaInfo = getGPFSQuotaInfo(storageArea);
          if (quotaInfo != null) {
            updateTotalOnlineSizeFromGPFSQuota(storageArea, quotaInfo);
          }
        }
      }
    });
  }

  private GPFSFilesetQuotaInfo getGPFSQuotaInfo(VirtualFS storageArea) {

    GetGPFSFilesetQuotaInfoCommand cmd = new GetGPFSFilesetQuotaInfoCommand(storageArea);

    try {
      return cmd.call();
    } catch (Throwable t) {
      log.warn(
          "Cannot get quota information out of GPFS. Using the TotalOnlineSize in namespace.xml "
              + "for Storage Area {}. Reason: {}",
          storageArea.getAliasName(), t.getMessage());
      return null;
    }
  }

  private void updateTotalOnlineSizeFromGPFSQuota(VirtualFS storageArea,
      GPFSFilesetQuotaInfo quotaInfo) {

    long gpfsTotalOnlineSize = GPFSSizeHelper.getBytesFromKIB(quotaInfo.getBlockSoftLimit());
    Property newProperties = Property.from(storageArea.getProperties());
    try {
      newProperties.setTotalOnlineSize(SizeUnitType.BYTE.getTypeName(), gpfsTotalOnlineSize);
      storageArea.setProperties(newProperties);
      log.warn("TotalOnlineSize as specified in namespace.xml will be ignored "
          + "since quota is enabled on the GPFS {} Storage Area.", storageArea.getAliasName());
    } catch (NamespaceException e) {
      log.warn(
          "Cannot get quota information out of GPFS. Using the TotalOnlineSize in namespace.xml "
              + "for Storage Area {}.",
          storageArea.getAliasName(), e);
    }
  }

  private void updateSA() {

    SpaceHelper spaceHelp = new SpaceHelper();
    log.debug("Updating Space Catalog with Storage Area defined within NAMESPACE");
    namespace.getAllDefinedVFS().forEach(vfs ->{

      String vfsAliasName = vfs.getAliasName();
      log.debug(" Considering VFS : {}", vfsAliasName);
      String aliasName = vfs.getSpaceTokenDescription();
      if (aliasName == null) {
        // Found a VFS without the optional element Space Token Description
        log.debug(
            "XMLNamespaceParser.UpdateSA() : Found a VFS ('{}') without space-token-description. "
                + "Skipping the Update of SA",
            vfsAliasName);
      } else {
        TSizeInBytes onlineSize = vfs.getProperties().getTotalOnlineSize();
        String spaceFileName = vfs.getRootPath();
        TSpaceToken spaceToken = spaceHelp.createVOSA_Token(aliasName, onlineSize, spaceFileName);
        vfs.setSpaceToken(spaceToken);

        log.debug(" Updating SA ('{}'), token:'{}', onlineSize:'{}', spaceFileName:'{}'", aliasName,
            spaceToken, onlineSize, spaceFileName);
      }

    });
    spaceHelp.purgeOldVOSA_token();
    log.debug("Updating Space Catalog... DONE!!");

  }

  private void configureSecurity() {

    int cacheTtl = config.getNetworkAddressCacheTtl();
    log.debug("Setting networkaddress.cache.ttl to {}", cacheTtl);
    setProperty("networkaddress.cache.ttl", valueOf(cacheTtl));

    int cacheNegativeTtl = config.getNetworkAddressCacheNegativeTtl();
    log.debug("Setting networkaddress.cache.negative.ttl to {}", cacheNegativeTtl);
    setProperty("networkaddress.cache.negative.ttl", valueOf(cacheNegativeTtl));
  }

  private void configureMetricsReporting() {

    METRIC_REGISTRY.getRegistry().timer(SimpleSynchcallDispatcher.SYNCH_CALL_TIMER_NAME);

    final StormMetricsReporter metricsReporter =
        StormMetricsReporter.forRegistry(METRIC_REGISTRY.getRegistry()).build();

    metricsReporter.start(1, TimeUnit.MINUTES);

  }

  private void loadPathAuthzDBConfiguration() throws BootstrapException {

    String pathAuthzDBFileName = config.configurationDir() + "path-authz.db";

    Bootstrap.initializePathAuthz(pathAuthzDBFileName);
  }

  private void configureXMLRPCService() throws BootstrapException {

    try {

      xmlrpcServer = new XMLRPCHttpServer(config.getXmlRpcServerPort(), config.getXMLRPCMaxThread(),
          config.getXMLRPCMaxQueueSize());

    } catch (StoRMXmlRpcException e) {

      throw new BootstrapException(e.getMessage(), e);
    }

  }

  private void performSanityChecks() throws BootstrapException {

    if (config.getSanityCheckEnabled()) {

      CheckManager checkManager = new SimpleCheckManager();
      checkManager.init();
      CheckResponse checkResponse = null;
      try {
        checkResponse = checkManager.lauchChecks();
      } catch (Exception e) {
        throw new BootstrapException(e);
      }

      if (checkResponse.isSuccessfull()) {
        log.info("Check suite executed successfully");
      } else {
        if (checkResponse.getStatus().equals(CheckStatus.CRITICAL_FAILURE)) {
          log.error("Storm Check suite is failed for some critical checks!");
          throw new BootstrapException(
              "Storm Check suite is failed for some critical checks! Please check the log for more details");
        } else {
          log.warn(
              "Storm Check suite is failed but not for any critical check. StoRM safely started.");
        }
      }

    } else {
      log.warn("Sanity checks disabled. Unable to determine if the environment is sane");
    }

  }

  /**
   * Method used to start the picker.
   */
  public synchronized void startPicker() {

    if (isPickerRunning) {
      log.debug("Picker is already running");
      return;
    }
    picker.startIt();
    isPickerRunning = true;
  }

  /**
   * Method used to stop the picker.
   */
  public synchronized void stopPicker() {

    if (!isPickerRunning) {
      log.debug("Picker is not running");
      return;
    }
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
  public synchronized void startXmlRpcServer() {

    if (isXmlrpcServerRunning) {
      log.debug("XMLRPC server is already running");
      return;
    }
    xmlrpcServer.start();
    isXmlrpcServerRunning = true;
  }

  /**
   * Method used to stop xmlrpcServer.
   */
  public synchronized void stopXmlRpcServer() {

    if (!isXmlrpcServerRunning) {
      log.debug("XMLRPC server is not running");
      return;
    }

    xmlrpcServer.stop();
    isXmlrpcServerRunning = false;
  }

  private void configureRestService() {

    int restServicePort = Configuration.getInstance().getRestServicesPort();
    boolean isTokenEnabled = Configuration.getInstance().getXmlRpcTokenEnabled();
    String token = Configuration.getInstance().getXmlRpcToken();
    int maxThreads = Configuration.getInstance().getRestServicesMaxThreads();
    int maxQueueSize = Configuration.getInstance().getRestServicesMaxQueueSize();

    restServer = new RestServer(restServicePort, maxThreads, maxQueueSize, isTokenEnabled, token);
  }

  /**
   * RESTFul Service Start-up
   */
  public synchronized void startRestServer() throws Exception {

    if (isRestServerRunning) {
      log.debug("Rest Server is already running");
      return;
    }

    restServer.start();
    isRestServerRunning = true;
  }

  /**
   * @throws Exception
   */
  public synchronized void stopRestServer() {

    if (isRestServerRunning) {
      log.debug("Rest Server is not running.");
      return;
    }

    try {

      restServer.start();
      isRestServerRunning = false;

    } catch (Exception e) {

      log.error("Unable to stop internal HTTP Server listening for RESTFul services: {}",
          e.getMessage(), e);
    }
  }

  /**
   * Method use to start the space Garbage Collection Thread.
   */
  public synchronized void startSpaceGC() {

    if (isSpaceGCRunning) {
      log.debug("Space Garbage Collector is already running");
      return;
    }

    log.debug("Starting Space Garbage Collector ...");
    // Delay time before starting
    long delay = config.getCleaningInitialDelay() * 1000;

    // cleaning thread! Set to 1 minute
    // Period of execution of cleaning
    long period = config.getCleaningTimeInterval() * 1000;

    // Set to 1 hour
    cleaningTask = new TimerTask() {

      @Override
      public void run() {

        spaceCatalog.purge();
      }
    };
    gc.scheduleAtFixedRate(cleaningTask, delay, period);
    isSpaceGCRunning = true;
    log.debug("Space Garbage Collector started.");
  }

  /**
   * 
   */
  public synchronized void stopSpaceGC() {

    if (!isSpaceGCRunning) {
      log.debug("Space Garbage Collector is not running.");
      return;
    }

    log.debug("Stopping Space Garbage Collector.");
    if (cleaningTask != null) {
      cleaningTask.cancel();
      gc.purge();
    }
    log.debug("Space Garbage Collector stopped.");
    isSpaceGCRunning = false;
  }

  /**
   * @return
   */
  public synchronized boolean spaceGCIsRunning() {

    return isSpaceGCRunning;
  }

  public synchronized void startExpiredAgent() {

    if (isExpiredAgentRunning) {
      log.debug("Expired Agent is already running.");
      return;
    }

    log.debug("Starting Expired Agent.");
    expiredAgent = new RequestFinalizerService(config);
    isExpiredAgentRunning = true;
    log.debug("Expired Agent started.");
  }

  public synchronized void stopExpiredAgent() {

    if (!isExpiredAgentRunning) {
      log.debug("Expired Agent is not running.");
      return;
    }

    log.debug("Stopping Expired Agent.");
    if (expiredAgent != null) {
      expiredAgent.stop();
    }
    log.debug("Expired Agent stopped.");
    isExpiredAgentRunning = false;
  }

  public synchronized boolean isExpiredAgentRunning() {

    return isExpiredAgentRunning;
  }

  private void configureDiskUsageService() {

    isDiskUsageServiceEnabled = config.getDiskUsageServiceEnabled();

    Namespace namespace = Namespace.getInstance();
    List<VirtualFS> quotaEnabledVfs = namespace.getVFSWithQuotaEnabled();
    List<VirtualFS> sas = namespace.getAllDefinedVFS()
      .stream()
      .filter(vfs -> !quotaEnabledVfs.contains(vfs))
      .collect(Collectors.toList());

    if (config.getDiskUsageServiceTasksParallel()) {
      duService = DiskUsageService.getScheduledThreadPoolService(sas);
    } else {
      duService = DiskUsageService.getSingleThreadScheduledService(sas);
    }
    duService.setDelay(config.getDiskUsageServiceInitialDelay());
    duService.setPeriod(config.getDiskUsageServiceTasksInterval());
  }

  /**
   * Starts the internal timer needed to periodically compute the disk usage for each storage area
   * configured and update the relative value into storm database.
   */
  public synchronized void startDiskUsageService() {

    if (isDiskUsageServiceEnabled) {

      log.info("Starting DiskUsage Service (delay: {}s, period: {}s)", duService.getDelay(),
          duService.getPeriod());

      duService.start();

      log.info("DiskUsage Service started.");

    } else {

      log.info("DiskUsage Service is disabled.");

    }
  }

  public synchronized void stopDiskUsageService() {

    if (isDiskUsageServiceEnabled) {

      log.debug("Stopping DiskUsage Service.");

      duService.stop();

      log.debug("DiskUsage Service stopped.");

    } else {

      log.info("DiskUsage Service is not running.");

    }
  }

  public synchronized void startRequestGarbageCollector() {

    if (isRequestGCRunning) {
      log.debug("Requests Garbage Collector is already running.");
      return;
    }

    /* Delay time before starting cleaning thread */
    final long delay = config.getCleaningInitialDelay() * 1000L;
    /* Period of execution of cleaning */
    final long period = config.getCleaningTimeInterval() * 1000L;

    log.debug("Starting Requests Garbage Collector .");
    rgcTask = new RequestsGarbageCollector(rgc, period);
    rgc.schedule(rgcTask, delay);
    isRequestGCRunning = true;
    log.debug("Requests Garbage Collector started.");
  }

  public synchronized void stopRequestGarbageCollector() {

    if (!isRequestGCRunning) {
      log.debug("Requests Garbage Collector is not running.");
      return;
    }

    log.debug("Stopping Requests Garbage Collector.");
    if (rgcTask != null) {
      rgcTask.cancel();
    }
    log.debug("Requests Garbage Collector stopped.");
    isRequestGCRunning = false;
  }

  public void startServices() throws Exception {

    startPicker();
    startXmlRpcServer();
    startRestServer();
    startSpaceGC();
    startExpiredAgent();
    startRequestGarbageCollector();
    startDiskUsageService();
  }

  public void stopServices() {

    stopPicker();
    stopXmlRpcServer();
    stopRestServer();
    stopSpaceGC();
    stopExpiredAgent();
    stopRequestGarbageCollector();
    stopDiskUsageService();

    GPFSQuotaManager.INSTANCE.shutdown();
  }
}
