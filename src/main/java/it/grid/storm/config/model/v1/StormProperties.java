package it.grid.storm.config.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StormProperties {

  @JsonProperty("storm.service.SURL.endpoint")
  public String managedSurls;

  @JsonProperty("storm.service.SURL.default-ports")
  public String defaultPorts;

  @JsonProperty("storm.service.FE-public.hostname")
  public String srmPublicHostname;

  @JsonProperty("storm.service.port")
  public int srmPublicPort;

  @JsonProperty("storm.service.request-db.host")
  public String dbHostname;

  @JsonProperty("storm.service.request-db.properties")
  public String dbProperties;

  @JsonProperty("storm.service.request-db.username")
  public String dbUsername;

  @JsonProperty("storm.service.request-db.passwd")
  public String dbPassword;

  @JsonProperty("asynch.db.ReconnectPeriod")
  public int dbReconnectPeriod;

  @JsonProperty("asynch.db.DelayPeriod")
  public int dbDelayPeriod;

  @JsonProperty("gc.pinnedfiles.cleaning.delay")
  public int gcPinnedfilesCleaningDelay;

  @JsonProperty("gc.pinnedfiles.cleaning.interval")
  public int gcPinnedfilesCleaningInterval;

  @JsonProperty("fileSize.default")
  public long filesizeDefault;

  @JsonProperty("fileLifetime.default")
  public long fileLifetimeDefault;

  @JsonProperty("pinLifetime.default")
  public long pinLifetimeDefault;

  @JsonProperty("pinLifetime.maximum")
  public long pinLifetimeMaximum;

  @JsonProperty("transit.delay")
  public int expiredRequestsAgentDelay;

  @JsonProperty("transit.interval")
  public int expiredRequestsAgentInterval;

  @JsonProperty("asynch.PickingInitialDelay")
  public int requestsPickerAgentDelay;

  @JsonProperty("asynch.PickingTimeInterval")
  public int requestsPickerAgentInterval;

  @JsonProperty("asynch.PickingMaxBatchSize")
  public int requestsPickerAgentMaxFetchedSize;

  @JsonProperty("synchcall.xmlrpc.maxthread")
  public int xmlrpcMaxThreads;

  @JsonProperty("synchcall.xmlrpc.max_queue_size")
  public int xmlrpcMaxQueueSize;

  @JsonProperty("persistence.internal-db.connection-pool.maxActive")
  public int dbPoolMaxActiveConnections;

  @JsonProperty("persistence.internal-db.connection-pool.maxWait")
  public int dbPoolMaxWait;

  @JsonProperty("synchcall.xmlrpc.unsecureServerPort")
  public int xmlrpcPort;

  @JsonProperty("synchcall.directoryManager.maxLsEntry")
  public int synchLsMaxEntries;

  @JsonProperty("synchcall.directoryManager.default.AllLevelRecursive")
  public boolean synchLsDefaultAllLevelRecursive;

  @JsonProperty("synchcall.directoryManager.default.Levels")
  public int synchLsDefaultNumLevels;

  @JsonProperty("synchcall.directoryManager.default.Offset")
  public int synchLsDefaultOffset;

  @JsonProperty("scheduler.chunksched.ptp.workerCorePoolSize")
  public int ptpSchedulerCorePoolSize;

  @JsonProperty("scheduler.chunksched.ptp.workerMaxPoolSize")
  public int ptpSchedulerMaxPoolSize;

  @JsonProperty("scheduler.chunksched.ptp.queueSize")
  public int ptpSchedulerQueueSize;

  @JsonProperty("scheduler.chunksched.ptg.workerCorePoolSize")
  public int ptgSchedulerCorePoolSize;

  @JsonProperty("scheduler.chunksched.ptg.workerMaxPoolSize")
  public int ptgSchedulerMaxPoolSize;

  @JsonProperty("scheduler.chunksched.ptg.queueSize")
  public int ptgSchedulerQueueSize;

  @JsonProperty("scheduler.chunksched.bol.workerCorePoolSize")
  public int bolSchedulerCorePoolSize;

  @JsonProperty("scheduler.chunksched.bol.workerMaxPoolSize")
  public int bolSchedulerMaxPoolSize;

  @JsonProperty("scheduler.chunksched.bol.queueSize")
  public int bolSchedulerQueueSize;

  @JsonProperty("scheduler.crusher.workerCorePoolSize")
  public int requestsSchedulerCorePoolSize;

  @JsonProperty("scheduler.crusher.workerMaxPoolSize")
  public int requestsSchedulerMaxPoolSize;

  @JsonProperty("scheduler.crusher.queueSize")
  public int requestsSchedulerQueueSize;

  @JsonProperty("namespace.filename")
  public String namespaceFilename;

  @JsonProperty("namespace.schema.filename")
  public String namespaceSchemaFilename;

  @JsonProperty("namespace.refreshrate")
  public int namespaceRefreshRate;

  @JsonProperty("namespace.automatic-config-reload")
  public boolean namespaceAutomaticConfigReload;

  @JsonProperty("directory.automatic-creation")
  public boolean directoriesAutomaticCreation;

  @JsonProperty("default.overwrite")
  public String defaultOverwriteMode;

  @JsonProperty("default.storagetype")
  public String defaultFileStorageType;

  @JsonProperty("purge.size")
  public int completedRequestsAgentPurgeSize;

  @JsonProperty("expired.request.time")
  public long completedRequestsAgentPurgeAge;

  @JsonProperty("expired.inprogress.time")
  public long inProgressRequestsAgentPtpExpirationTime;

  @JsonProperty("purge.delay")
  public int completedRequestsAgentPurgeDelay;

  @JsonProperty("purge.interval")
  public int completedRequestsAgentInterval;

  @JsonProperty("purging")
  public boolean completedRequestsAgentEnabled;

  @JsonProperty("extraslashes.file")
  public String extraslashesFile;
  
  @JsonProperty("extraslashes.rfio")
  public String extraslashesRfio;
  
  @JsonProperty("extraslashes.gsiftp")
  public String extraslashesGsiftp;

  @JsonProperty("extraslashes.root")
  public String extraslashesRoot;

  @JsonProperty("ping-properties.filename")
  public String pingPropertiesFilename;

  @JsonProperty("health.electrocardiogram.period")
  public int hearthbeatPeriod;

  @JsonProperty("health.performance.glance.timeInterval")
  public int hearthbeatPerformanceGlanceTimeInterval;

  @JsonProperty("health.performance.logbook.timeInterval")
  public int hearthbeatPerformanceLogbookTimeInterval;

  @JsonProperty("health.performance.mesauring.enabled")
  public boolean hearthbeatPerformanceMeasuringEnabled;

  @JsonProperty("health.bookkeeping.enabled")
  public boolean hearthbeatBookKeepingEnabled;

  @JsonProperty("directory.writeperm")
  public boolean enabledWritePermOnDirectory;

  @JsonProperty("abort.maxloop")
  public int abortMaxLoop;

  @JsonProperty("storm.rest.services.port")
  public int restServicesPort;

  @JsonProperty("storm.rest.services.maxthread")
  public int restServicesMaxThreads;

  @JsonProperty("storm.rest.services.max_queue_size")
  public int restServicesMaxQueueSize;

  @JsonProperty("info.quota.refresh.period")
  public int gpfsQuotaRefreshPeriod;

  @JsonProperty("server-pool.status-check.timeout")
  public long serverPoolStatusCheckTimeout;

  @JsonProperty("sanity-check.enabled")
  public boolean sanityCheckEnabled;

  @JsonProperty("synchcall.xmlrpc.security.enabled")
  public boolean securityEnabled;

  @JsonProperty("synchcall.xmlrpc.security.token")
  public String securityTokenValue;

  @JsonProperty("ptg.skip-acl-setup")
  public boolean ptgSkipAclSetup;

  @JsonProperty("http.turl_prefix")
  public String httpTurlPrefix;

  @JsonProperty("storm.service.du.enabled")
  public boolean diskUsageServiceEnabled;

  @JsonProperty("storm.service.du.delaySecs")
  public int diskUsageServiceInitialDelay;

  @JsonProperty("storm.service.du.periodSecs")
  public int diskUsageServiceTasksInterval;

  @JsonProperty("storm.service.du.parallelTasks")
  public boolean diskUsageServiceParallelTasksEnabled;

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StormProperties [managedSurls=");
    builder.append(managedSurls);
    builder.append(", defaultPorts=");
    builder.append(defaultPorts);
    builder.append(", srmPublicHostname=");
    builder.append(srmPublicHostname);
    builder.append(", srmPublicPort=");
    builder.append(srmPublicPort);
    builder.append(", dbHostname=");
    builder.append(dbHostname);
    builder.append(", dbProperties=");
    builder.append(dbProperties);
    builder.append(", dbUsername=");
    builder.append(dbUsername);
    builder.append(", dbPassword=");
    builder.append(dbPassword);
    builder.append(", dbReconnectPeriod=");
    builder.append(dbReconnectPeriod);
    builder.append(", dbDelayPeriod=");
    builder.append(dbDelayPeriod);
    builder.append(", gcPinnedfilesCleaningDelay=");
    builder.append(gcPinnedfilesCleaningDelay);
    builder.append(", gcPinnedfilesCleaningInterval=");
    builder.append(gcPinnedfilesCleaningInterval);
    builder.append(", filesizeDefault=");
    builder.append(filesizeDefault);
    builder.append(", fileLifetimeDefault=");
    builder.append(fileLifetimeDefault);
    builder.append(", pinLifetimeDefault=");
    builder.append(pinLifetimeDefault);
    builder.append(", pinLifetimeMaximum=");
    builder.append(pinLifetimeMaximum);
    builder.append(", expiredRequestsAgentDelay=");
    builder.append(expiredRequestsAgentDelay);
    builder.append(", expiredRequestsAgentInterval=");
    builder.append(expiredRequestsAgentInterval);
    builder.append(", requestsPickerAgentDelay=");
    builder.append(requestsPickerAgentDelay);
    builder.append(", requestsPickerAgentInterval=");
    builder.append(requestsPickerAgentInterval);
    builder.append(", requestsPickerAgentMaxFetchedSize=");
    builder.append(requestsPickerAgentMaxFetchedSize);
    builder.append(", xmlrpcMaxThreads=");
    builder.append(xmlrpcMaxThreads);
    builder.append(", xmlrpcMaxQueueSize=");
    builder.append(xmlrpcMaxQueueSize);
    builder.append(", dbPoolMaxActiveConnections=");
    builder.append(dbPoolMaxActiveConnections);
    builder.append(", dbPoolMaxWait=");
    builder.append(dbPoolMaxWait);
    builder.append(", xmlrpcPort=");
    builder.append(xmlrpcPort);
    builder.append(", synchLsMaxEntries=");
    builder.append(synchLsMaxEntries);
    builder.append(", synchLsDefaultAllLevelRecursive=");
    builder.append(synchLsDefaultAllLevelRecursive);
    builder.append(", synchLsDefaultNumLevels=");
    builder.append(synchLsDefaultNumLevels);
    builder.append(", synchLsDefaultOffset=");
    builder.append(synchLsDefaultOffset);
    builder.append(", ptpSchedulerCorePoolSize=");
    builder.append(ptpSchedulerCorePoolSize);
    builder.append(", ptpSchedulerMaxPoolSize=");
    builder.append(ptpSchedulerMaxPoolSize);
    builder.append(", ptpSchedulerQueueSize=");
    builder.append(ptpSchedulerQueueSize);
    builder.append(", ptgSchedulerCorePoolSize=");
    builder.append(ptgSchedulerCorePoolSize);
    builder.append(", ptgSchedulerMaxPoolSize=");
    builder.append(ptgSchedulerMaxPoolSize);
    builder.append(", ptgSchedulerQueueSize=");
    builder.append(ptgSchedulerQueueSize);
    builder.append(", bolSchedulerCorePoolSize=");
    builder.append(bolSchedulerCorePoolSize);
    builder.append(", bolSchedulerMaxPoolSize=");
    builder.append(bolSchedulerMaxPoolSize);
    builder.append(", bolSchedulerQueueSize=");
    builder.append(bolSchedulerQueueSize);
    builder.append(", requestsSchedulerCorePoolSize=");
    builder.append(requestsSchedulerCorePoolSize);
    builder.append(", requestsSchedulerMaxPoolSize=");
    builder.append(requestsSchedulerMaxPoolSize);
    builder.append(", requestsSchedulerQueueSize=");
    builder.append(requestsSchedulerQueueSize);
    builder.append(", namespaceFilename=");
    builder.append(namespaceFilename);
    builder.append(", namespaceSchemaFilename=");
    builder.append(namespaceSchemaFilename);
    builder.append(", namespaceRefreshRate=");
    builder.append(namespaceRefreshRate);
    builder.append(", namespaceAutomaticConfigReload=");
    builder.append(namespaceAutomaticConfigReload);
    builder.append(", directoriesAutomaticCreation=");
    builder.append(directoriesAutomaticCreation);
    builder.append(", defaultOverwriteMode=");
    builder.append(defaultOverwriteMode);
    builder.append(", defaultFileStorageType=");
    builder.append(defaultFileStorageType);
    builder.append(", completedRequestsAgentPurgeSize=");
    builder.append(completedRequestsAgentPurgeSize);
    builder.append(", completedRequestsAgentPurgeAge=");
    builder.append(completedRequestsAgentPurgeAge);
    builder.append(", inProgressRequestsAgentPtpExpirationTime=");
    builder.append(inProgressRequestsAgentPtpExpirationTime);
    builder.append(", completedRequestsAgentPurgeDelay=");
    builder.append(completedRequestsAgentPurgeDelay);
    builder.append(", completedRequestsAgentInterval=");
    builder.append(completedRequestsAgentInterval);
    builder.append(", completedRequestsAgentEnabled=");
    builder.append(completedRequestsAgentEnabled);
    builder.append(", extraslashesFile=");
    builder.append(extraslashesFile);
    builder.append(", extraslashesRfio=");
    builder.append(extraslashesRfio);
    builder.append(", extraslashesGsiftp=");
    builder.append(extraslashesGsiftp);
    builder.append(", extraslashesRoot=");
    builder.append(extraslashesRoot);
    builder.append(", pingPropertiesFilename=");
    builder.append(pingPropertiesFilename);
    builder.append(", hearthbeatPeriod=");
    builder.append(hearthbeatPeriod);
    builder.append(", hearthbeatPerformanceGlanceTimeInterval=");
    builder.append(hearthbeatPerformanceGlanceTimeInterval);
    builder.append(", hearthbeatPerformanceLogbookTimeInterval=");
    builder.append(hearthbeatPerformanceLogbookTimeInterval);
    builder.append(", hearthbeatPerformanceMeasuringEnabled=");
    builder.append(hearthbeatPerformanceMeasuringEnabled);
    builder.append(", hearthbeatBookKeepingEnabled=");
    builder.append(hearthbeatBookKeepingEnabled);
    builder.append(", enabledWritePermOnDirectory=");
    builder.append(enabledWritePermOnDirectory);
    builder.append(", abortMaxLoop=");
    builder.append(abortMaxLoop);
    builder.append(", restServicesPort=");
    builder.append(restServicesPort);
    builder.append(", restServicesMaxThreads=");
    builder.append(restServicesMaxThreads);
    builder.append(", restServicesMaxQueueSize=");
    builder.append(restServicesMaxQueueSize);
    builder.append(", gpfsQuotaRefreshPeriod=");
    builder.append(gpfsQuotaRefreshPeriod);
    builder.append(", serverPoolStatusCheckTimeout=");
    builder.append(serverPoolStatusCheckTimeout);
    builder.append(", sanityCheckEnabled=");
    builder.append(sanityCheckEnabled);
    builder.append(", securityEnabled=");
    builder.append(securityEnabled);
    builder.append(", securityTokenValue=");
    builder.append(securityTokenValue);
    builder.append(", ptgSkipAclSetup=");
    builder.append(ptgSkipAclSetup);
    builder.append(", httpTurlPrefix=");
    builder.append(httpTurlPrefix);
    builder.append(", diskUsageServiceEnabled=");
    builder.append(diskUsageServiceEnabled);
    builder.append(", diskUsageServiceInitialDelay=");
    builder.append(diskUsageServiceInitialDelay);
    builder.append(", diskUsageServiceTasksInterval=");
    builder.append(diskUsageServiceTasksInterval);
    builder.append(", diskUsageServiceParallelTasksEnabled=");
    builder.append(diskUsageServiceParallelTasksEnabled);
    builder.append("]");
    return builder.toString();
  }

  
  
}
