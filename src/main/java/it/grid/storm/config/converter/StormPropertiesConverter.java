package it.grid.storm.config.converter;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.grid.storm.config.model.Endpoint;
import it.grid.storm.config.model.StormProperties;

public class StormPropertiesConverter {

  /* Configuration file properties */
  private static final String MANAGED_SURLS_KEY = "storm.service.SURL.endpoint";
  private static final String SERVICE_HOSTNAME_KEY = "storm.service.FE-public.hostname";
  private static final String SERVICE_PORT_KEY = "storm.service.port";
  private static final String DB_URL_HOSTNAME_KEY = "storm.service.request-db.host";
  private static final String DB_URL_PROPERTIES_KEY = "storm.service.request-db.properties";
  private static final String DB_USER_NAME_KEY = "storm.service.request-db.username";
  private static final String DB_PASSWORD_KEY = "storm.service.request-db.passwd";
  private static final String BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY =
      "persistence.internal-db.connection-pool.maxActive";
  private static final String BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY =
      "persistence.internal-db.connection-pool.maxWait";
  private static final String XMLRPC_SERVER_PORT_KEY = "synchcall.xmlrpc.unsecureServerPort";
  private static final String XMLRPC_MAX_THREAD_KEY = "synchcall.xmlrpc.maxthread";
  private static final String XMLRPC_MAX_QUEUE_SIZE_KEY = "synchcall.xmlrpc.max_queue_size";
  private static final String REST_SERVICES_PORT_KEY = "storm.rest.services.port";
  private static final String REST_SERVICES_MAX_THREAD = "storm.rest.services.maxthread";
  private static final String REST_SERVICES_MAX_QUEUE_SIZE = "storm.rest.services.max_queue_size";
  private static final String XMLRPC_SECURITY_ENABLED_KEY = "synchcall.xmlrpc.security.enabled";
  private static final String XMLRPC_SECURITY_TOKEN_KEY = "synchcall.xmlrpc.security.token";

  private static final String DISKUSAGE_SERVICE_ENABLED = "storm.service.du.enabled";
  private static final String DISKUSAGE_SERVICE_INITIAL_DELAY = "storm.service.du.delaySecs";
  private static final String DISKUSAGE_SERVICE_TASKS_INTERVAL = "storm.service.du.periodSecs";
  private static final String DISKUSAGE_SERVICE_TASKS_PARALLEL = "storm.service.du.parallelTasks";

  private static final String SANITY_CHECK_ENABLED_KEY = "sanity-check.enabled";

  private static final String LS_MAX_NUMBER_OF_ENTRY_KEY = "synchcall.directoryManager.maxLsEntry";
  private static final String LS_ALL_LEVEL_RECURSIVE_KEY =
      "synchcall.directoryManager.default.AllLevelRecursive";
  private static final String LS_NUM_OF_LEVELS_KEY = "synchcall.directoryManager.default.Levels";
  private static final String LS_OFFSET_KEY = "synchcall.directoryManager.default.Offset";

  private static final String AUTOMATIC_DIRECTORY_CREATION_KEY = "directory.automatic-creation";
  private static final String ENABLE_WRITE_PERM_ON_DIRECTORY_KEY = "directory.writeperm";

  private static final String FILE_DEFAULT_SIZE_KEY = "fileSize.default";
  private static final String FILE_LIFETIME_DEFAULT_KEY = "fileLifetime.default";
  private static final String DEFAULT_OVERWRITE_MODE_KEY = "default.overwrite";
  private static final String DEFAULT_FILE_STORAGE_TYPE_KEY = "default.storagetype";

  private static final String EXTRA_SLASHES_FOR_FILE_TURL_KEY = "extraslashes.file";
  private static final String EXTRA_SLASHES_FOR_RFIO_TURL_KEY = "extraslashes.rfio";
  private static final String EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY = "extraslashes.gsiftp";
  private static final String EXTRA_SLASHES_FOR_ROOT_TURL_KEY = "extraslashes.root";

  private static final String PTG_SKIP_ACL_SETUP = "ptg.skip-acl-setup";

  private static final String HEARTHBEAT_PERIOD_KEY = "health.electrocardiogram.period";
  private static final String PERFORMANCE_GLANCE_TIME_INTERVAL_KEY =
      "health.performance.glance.timeInterval";
  private static final String PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY =
      "health.performance.logbook.timeInterval";
  private static final String PERFORMANCE_MEASURING_KEY = "health.performance.mesauring.enabled";
  private static final String BOOK_KEEPING_ENABLED_KEY = "health.bookkeeping.enabled";

  private static final String PICKING_INITIAL_DELAY_KEY = "asynch.PickingInitialDelay";
  private static final String PICKING_TIME_INTERVAL_KEY = "asynch.PickingTimeInterval";
  private static final String PICKING_MAX_BATCH_SIZE_KEY = "asynch.PickingMaxBatchSize";

  private static final String CORE_POOL_SIZE_KEY = "scheduler.crusher.workerCorePoolSize";
  private static final String MAX_POOL_SIZE_KEY = "scheduler.crusher.workerMaxPoolSize";
  private static final String QUEUE_SIZE_KEY = "scheduler.crusher.queueSize";

  private static final String PTP_CORE_POOL_SIZE_KEY =
      "scheduler.chunksched.ptp.workerCorePoolSize";
  private static final String PTP_MAX_POOL_SIZE_KEY = "scheduler.chunksched.ptp.workerMaxPoolSize";
  private static final String PTP_QUEUE_SIZE_KEY = "scheduler.chunksched.ptp.queueSize";
  private static final String PTG_CORE_POOL_SIZE_KEY =
      "scheduler.chunksched.ptg.workerCorePoolSize";
  private static final String PTG_MAX_POOL_SIZE_KEY = "scheduler.chunksched.ptg.workerMaxPoolSize";
  private static final String PTG_QUEUE_SIZE_KEY = "scheduler.chunksched.ptg.queueSize";
  private static final String BOL_CORE_POOL_SIZE_KEY =
      "scheduler.chunksched.bol.workerCorePoolSize";
  private static final String BOL_MAX_POOL_SIZE_KEY = "scheduler.chunksched.bol.workerMaxPoolSize";
  private static final String BOL_QUEUE_SIZE_KEY = "scheduler.chunksched.bol.queueSize";

  private static final String PIN_LIFETIME_DEFAULT_KEY = "pinLifetime.default";
  private static final String PIN_LIFETIME_MAXIMUM_KEY = "pinLifetime.maximum";

  private static final String CLEANING_INITIAL_DELAY_KEY = "gc.pinnedfiles.cleaning.delay";
  private static final String CLEANING_TIME_INTERVAL_KEY = "gc.pinnedfiles.cleaning.interval";

  private static final String TRANSIT_INITIAL_DELAY_KEY = "transit.delay";
  private static final String TRANSIT_TIME_INTERVAL_KEY = "transit.interval";
  private static final String EXPIRED_INPROGRESS_PTP_TIME_KEY = "expired.inprogress.time";

  private static final String PURGE_BATCH_SIZE_KEY = "purge.size";
  private static final String EXPIRED_REQUEST_TIME_KEY = "expired.request.time";
  private static final String REQUEST_PURGER_DELAY_KEY = "purge.delay";
  private static final String REQUEST_PURGER_PERIOD_KEY = "purge.interval";
  private static final String EXPIRED_REQUEST_PURGING_KEY = "purging";

  private static final String PING_VALUES_PROPERTIES_FILENAME_KEY = "ping-properties.filename";
  private static final String MAX_LOOP_KEY = "abort.maxloop";
  private static final String GPFS_QUOTA_REFRESH_PERIOD_KEY = "info.quota.refresh.period";
  private static final String SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY =
      "server-pool.status-check.timeout";
  private static final String HTTP_TURL_PREFIX = "http.turl_prefix";


  public static StormProperties from(Properties old) throws UnknownHostException {

    StormProperties properties = new StormProperties();
    // version
    properties.version = StormProperties.VERSION;

    // srmEndpoints
    if (old.containsKey(SERVICE_HOSTNAME_KEY) || old.containsKey(MANAGED_SURLS_KEY)) {
      // remove default value
      properties.srmEndpoints.clear();
    }
    if (old.containsKey(SERVICE_HOSTNAME_KEY)) {
      int port = 8444;
      String host = old.getProperty(SERVICE_HOSTNAME_KEY);
      if (old.containsKey(SERVICE_PORT_KEY)) {
        port = Integer.valueOf(old.getProperty(SERVICE_PORT_KEY));
      }
      properties.srmEndpoints.add(Endpoint.build(host, port));
    }
    if (old.containsKey(MANAGED_SURLS_KEY)) {
      String surlsStr = old.getProperty(MANAGED_SURLS_KEY);
      List<String> surls = Arrays.asList(surlsStr.split(","));
      for (String surl : surls) {
        Pattern pattern = Pattern.compile("srm://(.*?)/.*");
        Matcher matcher = pattern.matcher(surl);
        if (matcher.find()) {
          String host = matcher.group(1).split(":")[0];
          int port = Integer.valueOf(matcher.group(1).split(":")[1]);
          Endpoint e = Endpoint.build(host, port);
          if (!properties.srmEndpoints.contains(e)) {
            properties.srmEndpoints.add(e);
          }
        }
      }

      // db
      if (old.containsKey(DB_URL_HOSTNAME_KEY)) {
        properties.db.hostname = String.valueOf(old.getProperty(DB_URL_HOSTNAME_KEY)).trim();
      }
      if (old.containsKey(DB_USER_NAME_KEY)) {
        properties.db.username = String.valueOf(old.getProperty(DB_USER_NAME_KEY)).trim();
      }
      if (old.containsKey(DB_PASSWORD_KEY)) {
        properties.db.password = String.valueOf(old.getProperty(DB_PASSWORD_KEY)).trim();
      }
      if (old.containsKey(DB_URL_PROPERTIES_KEY)) {
        properties.db.properties = String.valueOf(old.getProperty(DB_URL_PROPERTIES_KEY)).trim();
      }
      if (old.containsKey(BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY)) {
        properties.db.pool.size =
            Integer.valueOf(old.getProperty(BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY));
      }
      if (old.containsKey(BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY)) {
        properties.db.pool.maxWaitMillis =
            Integer.valueOf(old.getProperty(BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY));
      }

      // xmlrpc
      if (old.containsKey(XMLRPC_SERVER_PORT_KEY)) {
        properties.xmlrpc.port = Integer.valueOf(old.getProperty(XMLRPC_SERVER_PORT_KEY));
      }
      if (old.containsKey(XMLRPC_MAX_THREAD_KEY)) {
        properties.xmlrpc.maxThreads = Integer.valueOf(old.getProperty(XMLRPC_MAX_THREAD_KEY));
      }
      if (old.containsKey(XMLRPC_MAX_QUEUE_SIZE_KEY)) {
        properties.xmlrpc.maxQueueSize =
            Integer.valueOf(old.getProperty(XMLRPC_MAX_QUEUE_SIZE_KEY));
      }

      // rest
      if (old.containsKey(REST_SERVICES_PORT_KEY)) {
        properties.rest.port = Integer.valueOf(old.getProperty(REST_SERVICES_PORT_KEY));
      }
      if (old.containsKey(REST_SERVICES_MAX_THREAD)) {
        properties.rest.maxThreads = Integer.valueOf(old.getProperty(REST_SERVICES_MAX_THREAD));
      }
      if (old.containsKey(REST_SERVICES_MAX_QUEUE_SIZE)) {
        properties.rest.maxQueueSize =
            Integer.valueOf(old.getProperty(REST_SERVICES_MAX_QUEUE_SIZE));
      }

      // security
      if (old.containsKey(XMLRPC_SECURITY_ENABLED_KEY)) {
        properties.security.enabled = Boolean.valueOf(old.getProperty(XMLRPC_SECURITY_ENABLED_KEY));
      }
      if (old.containsKey(XMLRPC_SECURITY_TOKEN_KEY)) {
        properties.security.token =
            String.valueOf(old.getProperty(XMLRPC_SECURITY_TOKEN_KEY)).trim();
      }

      // du
      if (old.containsKey(DISKUSAGE_SERVICE_ENABLED)) {
        properties.du.enabled = Boolean.valueOf(old.getProperty(DISKUSAGE_SERVICE_ENABLED));
      }
      if (old.containsKey(DISKUSAGE_SERVICE_INITIAL_DELAY)) {
        properties.du.initialDelay =
            Integer.valueOf(old.getProperty(DISKUSAGE_SERVICE_INITIAL_DELAY));
      }
      if (old.containsKey(DISKUSAGE_SERVICE_TASKS_PARALLEL)) {
        properties.du.parallelTasksEnabled =
            Boolean.valueOf(old.getProperty(DISKUSAGE_SERVICE_TASKS_PARALLEL));
      }
      if (old.containsKey(DISKUSAGE_SERVICE_TASKS_INTERVAL)) {
        properties.du.tasksInterval =
            Integer.valueOf(old.getProperty(DISKUSAGE_SERVICE_TASKS_INTERVAL));
      }

      // sanity check
      if (old.containsKey(SANITY_CHECK_ENABLED_KEY)) {
        properties.sanityChecksEnabled = Boolean.valueOf(old.getProperty(SANITY_CHECK_ENABLED_KEY));
      }

      // ls
      if (old.containsKey(LS_MAX_NUMBER_OF_ENTRY_KEY)) {
        properties.synchLs.maxEntries =
            Integer.valueOf(old.getProperty(LS_MAX_NUMBER_OF_ENTRY_KEY));
      }
      if (old.containsKey(LS_ALL_LEVEL_RECURSIVE_KEY)) {
        properties.synchLs.defaultAllLevelRecursive =
            Boolean.valueOf(old.getProperty(LS_ALL_LEVEL_RECURSIVE_KEY));
      }
      if (old.containsKey(LS_NUM_OF_LEVELS_KEY)) {
        properties.synchLs.defaultNumLevels = Short.valueOf(old.getProperty(LS_NUM_OF_LEVELS_KEY));
      }
      if (old.containsKey(LS_OFFSET_KEY)) {
        properties.synchLs.defaultOffset = Short.valueOf(old.getProperty(LS_OFFSET_KEY));
      }

      // directories
      if (old.containsKey(AUTOMATIC_DIRECTORY_CREATION_KEY)) {
        properties.directories.enableAutomaticCreation =
            Boolean.valueOf(old.getProperty(AUTOMATIC_DIRECTORY_CREATION_KEY));
      }
      if (old.containsKey(ENABLE_WRITE_PERM_ON_DIRECTORY_KEY)) {
        properties.directories.enableWritepermOnCreation =
            Boolean.valueOf(old.getProperty(ENABLE_WRITE_PERM_ON_DIRECTORY_KEY));
      }

      // files
      if (old.containsKey(FILE_DEFAULT_SIZE_KEY)) {
        properties.files.defaultSize = Long.valueOf(old.getProperty(FILE_DEFAULT_SIZE_KEY));
      }
      if (old.containsKey(FILE_LIFETIME_DEFAULT_KEY)) {
        properties.files.defaultLifetime = Long.valueOf(old.getProperty(FILE_LIFETIME_DEFAULT_KEY));
      }
      if (old.containsKey(DEFAULT_OVERWRITE_MODE_KEY)) {
        properties.files.defaultOverwrite =
            String.valueOf(old.getProperty(DEFAULT_OVERWRITE_MODE_KEY)).trim();
      }
      if (old.containsKey(DEFAULT_FILE_STORAGE_TYPE_KEY)) {
        properties.files.defaultStoragetype =
            String.valueOf(old.getProperty(DEFAULT_FILE_STORAGE_TYPE_KEY)).trim();
      }

      // extraslashes
      if (old.containsKey(EXTRA_SLASHES_FOR_FILE_TURL_KEY)) {
        properties.extraslashes.file =
            String.valueOf(old.getProperty(EXTRA_SLASHES_FOR_FILE_TURL_KEY)).trim();
      }
      if (old.containsKey(EXTRA_SLASHES_FOR_RFIO_TURL_KEY)) {
        properties.extraslashes.rfio =
            String.valueOf(old.getProperty(EXTRA_SLASHES_FOR_RFIO_TURL_KEY)).trim();
      }
      if (old.containsKey(EXTRA_SLASHES_FOR_ROOT_TURL_KEY)) {
        properties.extraslashes.root =
            String.valueOf(old.getProperty(EXTRA_SLASHES_FOR_ROOT_TURL_KEY)).trim();
      }
      if (old.containsKey(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY)) {
        properties.extraslashes.gsiftp =
            String.valueOf(old.getProperty(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY)).trim();
      }

      // skip ptg acl
      if (old.containsKey(PTG_SKIP_ACL_SETUP)) {
        properties.skipPtgAclSetup = Boolean.valueOf(old.getProperty(PTG_SKIP_ACL_SETUP));
      }

      // hearthbeat
      if (old.containsKey(BOOK_KEEPING_ENABLED_KEY)) {
        properties.hearthbeat.bookkeepingEnabled =
            Boolean.valueOf(old.getProperty(BOOK_KEEPING_ENABLED_KEY));
      }
      if (old.containsKey(PERFORMANCE_MEASURING_KEY)) {
        properties.hearthbeat.performanceMeasuringEnabled =
            Boolean.valueOf(old.getProperty(PERFORMANCE_MEASURING_KEY));
      }
      if (old.containsKey(HEARTHBEAT_PERIOD_KEY)) {
        properties.hearthbeat.period = Integer.valueOf(old.getProperty(HEARTHBEAT_PERIOD_KEY));
      }
      if (old.containsKey(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY)) {
        properties.hearthbeat.performanceLogbookTimeInterval =
            Integer.valueOf(old.getProperty(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY));
      }
      if (old.containsKey(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY)) {
        properties.hearthbeat.performanceGlanceTimeInterval =
            Integer.valueOf(old.getProperty(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY));
      }

      // requests picker
      if (old.containsKey(PICKING_INITIAL_DELAY_KEY)) {
        properties.requestsPickerAgent.delay =
            Integer.valueOf(old.getProperty(PICKING_INITIAL_DELAY_KEY));
      }
      if (old.containsKey(PICKING_TIME_INTERVAL_KEY)) {
        properties.requestsPickerAgent.interval =
            Integer.valueOf(old.getProperty(PICKING_TIME_INTERVAL_KEY));
      }
      if (old.containsKey(PICKING_MAX_BATCH_SIZE_KEY)) {
        properties.requestsPickerAgent.maxFetchedSize =
            Integer.valueOf(old.getProperty(PICKING_MAX_BATCH_SIZE_KEY));
      }

      // requests scheduler
      if (old.containsKey(CORE_POOL_SIZE_KEY)) {
        properties.requestsScheduler.corePoolSize =
            Integer.valueOf(old.getProperty(CORE_POOL_SIZE_KEY));
      }
      if (old.containsKey(MAX_POOL_SIZE_KEY)) {
        properties.requestsScheduler.maxPoolSize =
            Integer.valueOf(old.getProperty(MAX_POOL_SIZE_KEY));
      }
      if (old.containsKey(QUEUE_SIZE_KEY)) {
        properties.requestsScheduler.queueSize = Integer.valueOf(old.getProperty(QUEUE_SIZE_KEY));
      }

      // ptp requests scheduler
      if (old.containsKey(PTP_CORE_POOL_SIZE_KEY)) {
        properties.ptpScheduler.corePoolSize =
            Integer.valueOf(old.getProperty(PTP_CORE_POOL_SIZE_KEY));
      }
      if (old.containsKey(PTP_MAX_POOL_SIZE_KEY)) {
        properties.ptpScheduler.maxPoolSize =
            Integer.valueOf(old.getProperty(PTP_MAX_POOL_SIZE_KEY));
      }
      if (old.containsKey(PTP_QUEUE_SIZE_KEY)) {
        properties.ptpScheduler.queueSize = Integer.valueOf(old.getProperty(PTP_QUEUE_SIZE_KEY));
      }

      // ptg requests scheduler
      if (old.containsKey(PTG_CORE_POOL_SIZE_KEY)) {
        properties.ptgScheduler.corePoolSize =
            Integer.valueOf(old.getProperty(PTG_CORE_POOL_SIZE_KEY));
      }
      if (old.containsKey(PTG_MAX_POOL_SIZE_KEY)) {
        properties.ptgScheduler.maxPoolSize =
            Integer.valueOf(old.getProperty(PTG_MAX_POOL_SIZE_KEY));
      }
      if (old.containsKey(PTG_QUEUE_SIZE_KEY)) {
        properties.ptgScheduler.queueSize = Integer.valueOf(old.getProperty(PTG_QUEUE_SIZE_KEY));
      }

      // bol requests scheduler
      if (old.containsKey(BOL_CORE_POOL_SIZE_KEY)) {
        properties.bolScheduler.corePoolSize =
            Integer.valueOf(old.getProperty(BOL_CORE_POOL_SIZE_KEY));
      }
      if (old.containsKey(BOL_MAX_POOL_SIZE_KEY)) {
        properties.bolScheduler.maxPoolSize =
            Integer.valueOf(old.getProperty(BOL_MAX_POOL_SIZE_KEY));
      }
      if (old.containsKey(BOL_QUEUE_SIZE_KEY)) {
        properties.bolScheduler.queueSize = Integer.valueOf(old.getProperty(BOL_QUEUE_SIZE_KEY));
      }

      // pin lifetime
      if (old.containsKey(PIN_LIFETIME_DEFAULT_KEY)) {
        properties.pinlifetime.defaultValue =
            Integer.valueOf(old.getProperty(PIN_LIFETIME_DEFAULT_KEY));
      }
      if (old.containsKey(PIN_LIFETIME_MAXIMUM_KEY)) {
        properties.pinlifetime.maximum = Integer.valueOf(old.getProperty(PIN_LIFETIME_MAXIMUM_KEY));
      }

      // storage spaces agent
      if (old.containsKey(CLEANING_INITIAL_DELAY_KEY)) {
        properties.expiredSpacesAgent.delay =
            Integer.valueOf(old.getProperty(CLEANING_INITIAL_DELAY_KEY));
      }
      if (old.containsKey(CLEANING_TIME_INTERVAL_KEY)) {
        properties.expiredSpacesAgent.interval =
            Integer.valueOf(old.getProperty(CLEANING_TIME_INTERVAL_KEY));
      }

      // in progress requests agent
      if (old.containsKey(TRANSIT_INITIAL_DELAY_KEY)) {
        properties.inprogressRequestsAgent.delay =
            Integer.valueOf(old.getProperty(TRANSIT_INITIAL_DELAY_KEY));
      }
      if (old.containsKey(TRANSIT_TIME_INTERVAL_KEY)) {
        properties.inprogressRequestsAgent.interval =
            Integer.valueOf(old.getProperty(TRANSIT_TIME_INTERVAL_KEY));
      }
      if (old.containsKey(EXPIRED_INPROGRESS_PTP_TIME_KEY)) {
        properties.inprogressRequestsAgent.ptpExpirationTime =
            Long.valueOf(old.getProperty(EXPIRED_INPROGRESS_PTP_TIME_KEY));
      }

      // completed requests agent
      if (old.containsKey(EXPIRED_REQUEST_PURGING_KEY)) {
        properties.completedRequestsAgent.enabled =
            Boolean.valueOf(old.getProperty(EXPIRED_REQUEST_PURGING_KEY));
      }
      if (old.containsKey(EXPIRED_REQUEST_TIME_KEY)) {
        properties.completedRequestsAgent.purgeAge =
            Long.valueOf(old.getProperty(EXPIRED_REQUEST_TIME_KEY));
      }
      if (old.containsKey(PURGE_BATCH_SIZE_KEY)) {
        properties.completedRequestsAgent.purgeSize =
            Integer.valueOf(old.getProperty(PURGE_BATCH_SIZE_KEY));
      }
      if (old.containsKey(REQUEST_PURGER_DELAY_KEY)) {
        properties.completedRequestsAgent.delay =
            Integer.valueOf(old.getProperty(REQUEST_PURGER_DELAY_KEY));
      }
      if (old.containsKey(REQUEST_PURGER_PERIOD_KEY)) {
        properties.completedRequestsAgent.interval =
            Integer.valueOf(old.getProperty(REQUEST_PURGER_PERIOD_KEY));
      }

      // others
      if (old.containsKey(HTTP_TURL_PREFIX)) {
        properties.httpTurlPrefix = String.valueOf(old.getProperty(HTTP_TURL_PREFIX));
      }
      if (old.containsKey(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY)) {
        properties.serverPoolStatusCheckTimeout =
            Integer.valueOf(old.getProperty(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY));
      }
      if (old.containsKey(MAX_LOOP_KEY)) {
        properties.abortMaxloop = Integer.valueOf(old.getProperty(MAX_LOOP_KEY));
      }
      if (old.containsKey(GPFS_QUOTA_REFRESH_PERIOD_KEY)) {
        properties.infoQuotaRefreshPeriod =
            Integer.valueOf(old.getProperty(GPFS_QUOTA_REFRESH_PERIOD_KEY));
      }
      if (old.containsKey(PING_VALUES_PROPERTIES_FILENAME_KEY)) {
        properties.pingPropertiesFilename =
            String.valueOf(old.getProperty(PING_VALUES_PROPERTIES_FILENAME_KEY)).trim();
      }
    }
    return properties;
  }
}
