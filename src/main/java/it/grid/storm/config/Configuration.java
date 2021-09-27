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

package it.grid.storm.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import it.grid.storm.config.converter.StormPropertiesConversionException;
import it.grid.storm.config.converter.StormPropertiesConverter;
import it.grid.storm.config.model.v2.OverwriteMode;
import it.grid.storm.config.model.v2.StorageType;
import it.grid.storm.config.model.v2.StormProperties;
import it.grid.storm.namespace.model.Authority;

public class Configuration {

  private static Configuration instance = null;

  private static final Logger log = LoggerFactory.getLogger(Configuration.class);
  private static final JavaPropsMapper mapper = new JavaPropsMapper();

  private File configFile;
  private StormProperties properties;


  public static void init(String filePath) throws IOException {
    instance = new Configuration(filePath);
  }

  private Configuration(String filePath) throws IOException {

    configFile = new File(filePath);
    loadConfiguration();
  }

  private void loadConfiguration() throws IOException {

    try {
      properties = mapper.readerFor(StormProperties.class).readValue(configFile);
    } catch (JsonMappingException e) {
      log.error("Malformed configuration file: {}", e.getMessage());
      properties = null;
    }
    if (properties == null) {
      log.warn("It seems that '{}' is not compliant with this StoRM version.", configFile);
      File configTarget = new File(configFile + ".new");
      log.info("Converting your configuration into {} ...", configTarget);
      try {
        StormPropertiesConverter.convert(configFile, configTarget);
      } catch (IOException | StormPropertiesConversionException e) {
        log.error(e.getMessage());
        throw new RuntimeException("Unable to load configuration!");
      }
      log.warn("The automatic convertion has been done.");
      log.warn("Pleas check the generated configuration and properly update your '{}'", configFile);
      log.info("Loading configuration from {} ...", configTarget);
      try {
        properties = mapper.readerFor(StormProperties.class).readValue(configTarget);
      } catch (JsonMappingException e) {
        log.error("Malformed configuration file: {}", e.getMessage());
        throw new RuntimeException("Unable to load configuration!");
      }
    }
  }

  public synchronized static Configuration getInstance() {

    return instance;
  }

  public String getVersion() {

    return properties.version;
  }

  public File getConfigurationDir() {

    return configFile.getParentFile();
  }

  /**
   * The published host of SRM service. It's used also to initialize a SURL starting from the SFN.
   */
  public String getSrmServiceHostname() {

    return getManagedSrmEndpoints().get(0).getServiceHostname();
  }

  public List<Authority> getManagedSrmEndpoints() {

    return properties.srmEndpoints.stream()
      .map(e -> new Authority(e.host, e.port))
      .collect(Collectors.toList());
  }

  /**
   * The published port of SRM service. It's used also to initialize a SURL starting from the SFN.
   */
  public int getSrmServicePort() {

    return getManagedSrmEndpoints().get(0).getServicePort();
  }

  /**
   * Get database host
   */
  public String getDbHostname() {

    return properties.db.hostname;
  }

  /**
   * Get database URL's sub-name
   */
  public int getDbPort() {

    return properties.db.port;
  }

  /**
   * Get database username.
   */
  public String getDbUsername() {

    return properties.db.username;
  }

  /**
   * Get database password.
   */
  public String getDbPassword() {

    return properties.db.password;
  }

  /**
   * Get database connection properties
   */
  public String getDbProperties() {

    return properties.db.properties;
  }

  /**
   * Sets the maximum total number of idle and borrows connections that can be active at the same
   * time. Use a negative value for no limit.
   */
  public int getDbPoolSize() {

    return properties.db.pool.size;
  }

  /**
   * Sets the minimum number of idle connections in the pool.
   */
  public int getDbPoolMinIdle() {

    return properties.db.pool.minIdle;
  }

  /**
   * Sets the MaxWaitMillis property. Use -1 to make the pool wait indefinitely.
   */
  public int getDbPoolMaxWaitMillis() {

    return properties.db.pool.maxWaitMillis;
  }

  /**
   * This property determines whether or not the pool will validate objects before they are borrowed
   * from the pool.
   */
  public boolean isDbPoolTestOnBorrow() {

    return properties.db.pool.testOnBorrow;
  }

  /**
   * This property determines whether or not the idle object evictor will validate connections.
   */
  public boolean isDbPoolTestWhileIdle() {

    return properties.db.pool.testWhileIdle;
  }

  /**
   * Method used to retrieve the PORT where RESTful services listen (like the Recall Table service)
   */
  public int getRestServicesPort() {

    return properties.rest.port;
  }

  public int getRestServicesMaxThreads() {

    return properties.rest.maxThreads;
  }

  public int getRestServicesMaxQueueSize() {

    return properties.rest.maxQueueSize;
  }

  public boolean isSanityCheckEnabled() {

    return properties.sanityChecksEnabled;
  }

  /**
   * Get max number of XMLRPC threads into for the XMLRPC server.
   */
  public int getXmlrpcMaxThreads() {

    return properties.xmlrpc.maxThreads;
  }

  public int getXmlrpcMaxQueueSize() {

    return properties.xmlrpc.maxQueueSize;
  }

  public int getXmlRpcServerPort() {

    return properties.xmlrpc.port;
  }

  public Boolean isSecurityEnabled() {

    return properties.security.enabled;
  }

  public String getSecurityToken() {

    return properties.security.token;
  }

  public boolean isDiskUsageServiceEnabled() {

    return properties.du.enabled;
  }

  public int getDiskUsageServiceInitialDelay() {

    return properties.du.initialDelay;
  }

  public long getDiskUsageServiceTasksInterval() {

    return properties.du.tasksInterval;
  }

  public boolean isDiskUsageServiceTasksParallel() {

    return properties.du.parallelTasksEnabled;
  }

  public String getNamespaceConfigFilename() {

    return "namespace.xml";
  }

  /**
   * Used by PinnedFilesCatalog to get the initial delay in _seconds_ before starting the cleaning
   * thread.
   */
  public long getExpiredSpacesAgentInitialDelay() {

    return properties.expiredSpacesAgent.delay;
  }

  /**
   * Used by PinnedFilesCatalog to get the cleaning time interval, in _seconds_.
   */
  public long getExpiredSpacesAgentInterval() {

    return properties.expiredSpacesAgent.interval;
  }

  public long getFileDefaultSize() {

    return properties.files.defaultSize;
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the default fileLifetime to use when a volatile
   * entry is being added/updated, but the user specified a non positive value. Measured in
   * _seconds_.
   */
  public long getFileLifetimeDefault() {

    return properties.files.defaultLifetime;
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the minimum pinLifetime allowed, when a jit is
   * being added/updated, but the user specified a lower one. This method is also used by the
   * PinLifetimeConverter to translate a NULL/0/negative value to a default one. Measured in
   * _seconds_.
   */
  public long getPinLifetimeDefault() {

    return properties.pinlifetime.defaultValue;
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the maximum pinLifetime allowed, when a jit is
   * being added/updated, but the user specified a higher one. Measured in _seconds_.
   */
  public long getPinLifetimeMaximum() {

    return properties.pinlifetime.maximum;
  }

  /**
   * Method used by PtPChunkCatalog to get the initial delay in _seconds_ before starting the
   * transiting thread.
   */
  public long getInProgressAgentInitialDelay() {

    return properties.inprogressRequestsAgent.delay;
  }

  /**
   * Method used by PtPChunkCatalog to get the transiting time interval, in _seconds_.
   */
  public long getInProgressAgentInterval() {

    return properties.inprogressRequestsAgent.interval;
  }

  /**
   * Method used by AdvancedPicker to get the initial delay before starting to pick data from the
   * DB, in _seconds_.
   */
  public long getRequestsPickerAgentInitialDelay() {

    return properties.requestsPickerAgent.delay;
  }

  /**
   * Method used by AdvancedPicker to get the time interval of successive pickings, in _seconds_.
   */
  public long getRequestsPickerAgentInterval() {

    return properties.requestsPickerAgent.interval;
  }

  /**
   * Method used by RequestSummaryDAO to establish the maximum number of requests to retrieve with
   * each polling.
   */
  public int getRequestsPickerAgentMaxFetchedSize() {

    return properties.requestsPickerAgent.maxFetchedSize;
  }

  /**
   * Method used by the Synch Component to set the maximum number of entries to return for the srmLs
   * functionality.
   */
  public int getLsMaxNumberOfEntry() {

    return properties.synchLs.maxEntries;
  }

  /**
   * Default value for the parameter "allLevelRecursive" of the LS request.
   */
  public boolean isLsDefaultAllLevelRecursive() {

    return properties.synchLs.defaultAllLevelRecursive;
  }

  /**
   * Default value for the parameter "numOfLevels" of the LS request.
   */
  public short getLsDefaultNumOfLevels() {

    return properties.synchLs.defaultNumLevels;
  }

  /**
   * Default value for the parameter "offset" of the LS request.
   */
  public short getLsDefaultOffset() {

    return properties.synchLs.defaultOffset;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Core Poolsize for the
   * srmPrepareToPut management. Scheduler component uses a thread pool. Scheduler pool will
   * automatically adjust the pool size according to the bounds set by corePoolSize and
   * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if other worker
   * threads are idle. If there are more than corePoolSize but less than maximumPoolSize threads
   * running, a new thread will be created only if the queue is full. By setting corePoolSize and
   * maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize - the number of
   * threads to keep in the pool, even if they are idle.
   */
  public int getPtPCorePoolSize() {

    return properties.ptpScheduler.corePoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Max Pool Size for the
   * srmPrepareToPut management. Scheduler component uses a thread pool. Scheduler pool will
   * automatically adjust the pool size according to the bounds set by corePoolSize and
   * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if other worker
   * threads are idle. If there are more than corePoolSize but less than maximumPoolSize threads
   * running, a new thread will be created only if the queue is full. By setting corePoolSize and
   * maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize - the maximum number
   * of threads to allow in the pool.
   */
  public int getPtPMaxPoolSize() {

    return properties.ptpScheduler.maxPoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the srmPrepareToPut
   * management. If no value is found in the configuration medium, then the default value is
   * returned instead. Scheduler hold a blocking priority queue used to transfer and hols submitted
   * tasks. The use of this queue interacts with pool sizing: - If fewer than corePoolSize threads
   * are running, the Scheduler always prefers adding a new thread rather than queuing. - If
   * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather
   * than adding a new thread. - If a request cannot be queued, a new thread is created unless this
   * would exceed maxPoolSize, in which case, the task will be rejected. QueueSize - The initial
   * capacity for this priority queue used for holding tasks before they are executed. The queue
   * will hold only the Runnable tasks submitted by the execute method.
   */
  public int getPtPQueueSize() {

    return properties.ptpScheduler.queueSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Core Pool Size for the
   * srmPrepareToGet management. If no value is found in the configuration medium, then the default
   * value is returned instead. Scheduler component uses a thread pool. Scheduler pool will
   * automatically adjust the pool size according to the bounds set by corePoolSize and
   * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if other worker
   * threads are idle. If there are more than corePoolSize but less than maximumPoolSize threads
   * running, a new thread will be created only if the queue is full. By setting corePoolSize and
   * maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize - the number of
   * threads to keep in the pool, even if they are idle.
   */
  public int getPtGCorePoolSize() {

    return properties.ptgScheduler.corePoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Max Pool Size for the
   * srmPrepareToGet management. If no value is found in the configuration medium, then the default
   * value is returned instead. Scheduler component uses a thread pool. Scheduler pool will
   * automatically adjust the pool size according to the bounds set by corePoolSize and
   * maximumPoolSize. When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if other worker
   * threads are idle. If there are more than corePoolSize but less than maximumPoolSize threads
   * running, a new thread will be created only if the queue is full. By setting corePoolSize and
   * maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize - the maximum number
   * of threads to allow in the pool.
   */
  public int getPtGMaxPoolSize() {

    return properties.ptgScheduler.maxPoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the srmPrepareToGet
   * management. If no value is found in the configuration medium, then the default value is
   * returned instead. Scheduler hold a blocking priority queue used to transfer and hols submitted
   * tasks. The use of this queue interacts with pool sizing: - If fewer than corePoolSize threads
   * are running, the Scheduler always prefers adding a new thread rather than queuing. - If
   * corePoolSize or more threads are running, the Scheduler always prefers queuing a request rather
   * than adding a new thread. - If a request cannot be queued, a new thread is created unless this
   * would exceed maxPoolSize, in which case, the task will be rejected. QueueSize - The initial
   * capacity for this priority queue used for holding tasks before they are executed. The queue
   * will hold only the Runnable tasks submitted by the execute method.
   */
  public int getPtGQueueSize() {

    return properties.ptgScheduler.queueSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Core Pool Size for the
   * srmBoL management. If no value is found in the configuration medium, then the default value is
   * returned instead. Scheduler component uses a thread pool. Scheduler pool will automatically
   * adjust the pool size according to the bounds set by corePoolSize and maximumPoolSize. When a
   * new task is submitted in method execute, and fewer than corePoolSize threads are running, a new
   * thread is created to handle the request, even if other worker threads are idle. If there are
   * more than corePoolSize but less than maximumPoolSize threads running, a new thread will be
   * created only if the queue is full. By setting corePoolSize and maximumPoolSize the same, you
   * create a fixed-size thread pool. corePoolSize - the number of threads to keep in the pool, even
   * if they are idle.
   */
  public int getBoLCorePoolSize() {

    return properties.bolScheduler.corePoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Max Pool Size for the
   * srmBoL management. If no value is found in the configuration medium, then the default value is
   * returned instead. Scheduler component uses a thread pool. Scheduler pool will automatically
   * adjust the pool size according to the bounds set by corePoolSize and maximumPoolSize. When a
   * new task is submitted in method execute, and fewer than corePoolSize threads are running, a new
   * thread is created to handle the request, even if other worker threads are idle. If there are
   * more than corePoolSize but less than maximumPoolSize threads running, a new thread will be
   * created only if the queue is full. By setting corePoolSize and maximumPoolSize the same, you
   * create a fixed-size thread pool. maxPoolSize - the maximum number of threads to allow in the
   * pool.
   */
  public int getBoLMaxPoolSize() {

    return properties.bolScheduler.maxPoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the srmBoL management. If no
   * value is found in the configuration medium, then the default value is returned instead.
   * Scheduler hold a blocking priority queue used to transfer and hols submitted tasks. The use of
   * this queue interacts with pool sizing: - If fewer than corePoolSize threads are running, the
   * Scheduler always prefers adding a new thread rather than queuing. - If corePoolSize or more
   * threads are running, the Scheduler always prefers queuing a request rather than adding a new
   * thread. - If a request cannot be queued, a new thread is created unless this would exceed
   * maxPoolSize, in which case, the task will be rejected. QueueSize - The initial capacity for
   * this priority queue used for holding tasks before they are executed. The queue will hold only
   * the Runnable tasks submitted by the execute method.
   */
  public int getBoLQueueSize() {

    return properties.bolScheduler.queueSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Core Pool Size for the
   * Crusher. If no value is found in the configuration medium, then the default value is returned
   * instead. Scheduler component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize. When a new task is
   * submitted in method execute, and fewer than corePoolSize threads are running, a new thread is
   * created to handle the request, even if other worker threads are idle. If there are more than
   * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only
   * if the queue is full. By setting corePoolSize and maximumPoolSize the same, you create a
   * fixed-size thread pool. corePoolSize - the number of threads to keep in the pool, even if they
   * are idle.
   */
  public int getCorePoolSize() {

    return properties.requestsScheduler.corePoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Max Pool Size for the
   * Crusher. If no value is found in the configuration medium, then the default value is returned
   * instead. Scheduler component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize. When a new task is
   * submitted in method execute, and fewer than corePoolSize threads are running, a new thread is
   * created to handle the request, even if other worker threads are idle. If there are more than
   * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only
   * if the queue is full. By setting corePoolSize and maximumPoolSize the same, you create a
   * fixed-size thread pool. maxPoolSize - the maximum number of threads to allow in the pool.
   */
  public int getMaxPoolSize() {

    return properties.requestsScheduler.maxPoolSize;
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the Crusher. If no value is
   * found in the configuration medium, then the default value is returned instead. Scheduler hold a
   * blocking priority queue used to transfer and hols submitted tasks. The use of this queue
   * interacts with pool sizing: - If fewer than corePoolSize threads are running, the Scheduler
   * always prefers adding a new thread rather than queuing. - If corePoolSize or more threads are
   * running, the Scheduler always prefers queuing a request rather than adding a new thread. - If a
   * request cannot be queued, a new thread is created unless this would exceed maxPoolSize, in
   * which case, the task will be rejected. QueueSize - The initial capacity for this priority queue
   * used for holding tasks before they are executed. The queue will hold only the Runnable tasks
   * submitted by the execute method.
   */
  public int getQueueSize() {

    return properties.requestsScheduler.queueSize;
  }

  /**
   * Method used by PtPChunk to find out if missing local directories should be created
   * automatically or not. SRM 2.2 specification forbids automatic creation.
   */
  public boolean isAutomaticDirectoryCreationEnabled() {

    return properties.directories.enableAutomaticCreation;
  }

  /**
   * Enable write permission on new created directory for LocalAuthorizationSource usage.
   * 
   * @return false by default, otherwise what is specified in the properties
   */
  public boolean isDirectoryWritePermOnCreationEnabled() {

    return properties.directories.enableWritepermOnCreation;
  }

  /**
   * Method used by TOverwriteModeConverter to establish the default OverwriteMode to use.
   */
  public OverwriteMode getDefaultOverwriteMode() {

    return OverwriteMode.valueOf(properties.files.defaultOverwrite);
  }

  /**
   * Method used by FileStorageTypeConverter to establish the default TFileStorageType to use.
   */
  public StorageType getDefaultFileStorageType() {

    return StorageType.valueOf(properties.files.defaultStoragetype);
  }

  /**
   * Method used by RequestSummaryDAO to establish the batch size for removing expired requests.
   */
  public int getCompletedRequestsAgentPurgeSize() {

    return properties.completedRequestsAgent.purgeSize;
  }

  /**
   * Method used by RequestSummaryDAO to establish the time that must be elapsed for considering a
   * request expired. The time measure specified in the configuration medium is in _days_. The value
   * returned by this method, is expressed in _seconds_.
   */
  public long getCompletedRequestsAgentPurgeAge() {

    return properties.completedRequestsAgent.purgeAge;
  }

  /**
   * Method used by RequestSummaryCatalog to establish the initial delay before starting the purging
   * thread, in _seconds_.
   */
  public int getCompletedRequestsAgentDelay() {

    return properties.completedRequestsAgent.delay;
  }

  /**
   * Method used by RequestSummaryCatalog to establish the time interval in _seconds_ between
   * successive purging checks.
   */
  public int getCompletedRequestsAgentPeriod() {

    return properties.completedRequestsAgent.interval;
  }

  /**
   * Method used by RequestSummaryCatalog to establish if the purging of expired requests should be
   * enabled or not. If no value is found in the configuration medium, then the default one is used
   * instead. key="purging"; default value=true
   */
  public boolean isCompletedRequestsAgentEnabled() {

    return properties.completedRequestsAgent.enabled;
  }

  public long getInProgressPtpExpirationTime() {

    return properties.inprogressRequestsAgent.ptpExpirationTime;
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForFileTURL() {

    return properties.extraslashes.file;
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForRFIOTURL() {

    return properties.extraslashes.rfio;
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForGsiFTPTURL() {

    return properties.extraslashes.gsiftp;
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForRootTURL() {

    return properties.extraslashes.root;
  }

  /**
   * Method used by Ping Executor to retrieve the Properties File Name where the properties
   * <key,value> are stored.
   */
  public String getPingValuesPropertiesFilename() {

    return properties.pingPropertiesFilename;
  }

  public int getHearthbeatPeriod() {

    return properties.hearthbeat.period;
  }

  public int getHearthbeatPerformanceGlanceTimeInterval() {

    return properties.hearthbeat.performanceGlanceTimeInterval;
  }

  public int getHearthbeatPerformanceLogbookTimeInterval() {

    return properties.hearthbeat.performanceLogbookTimeInterval;
  }

  public boolean isHearthbeatPerformanceMeasuringEnabled() {

    return properties.hearthbeat.performanceMeasuringEnabled;
  }

  public boolean isHearthbeatBookkeepingEnabled() {

    return properties.hearthbeat.bookkeepingEnabled;
  }

  public int getMaxLoop() {

    return properties.abortMaxloop;
  }

  public String getGridUserMapperClassname() {

    return "it.grid.storm.griduser.StormLcmapsJNAMapper";
  }

  public String getRetryValueKey() {

    return "retry-value";
  }

  public String getStatusKey() {

    return "status";
  }

  public String getTaskoverKey() {

    return "first";
  }

  public int getGPFSQuotaRefreshPeriod() {

    return properties.infoQuotaRefreshPeriod;
  }

  public long getServerPoolStatusCheckTimeout() {

    return properties.serverPoolStatusCheckTimeout;
  }

  public boolean isSkipPtgACLSetup() {

    return properties.skipPtgAclSetup;
  }

  public String getHTTPTURLPrefix() {

    return properties.httpTurlPrefix;
  }

  public int getNetworkAddressCacheTtl() {

    return 0;
  }

  public int getNetworkAddressCacheNegativeTtl() {

    return 0;
  }

}
