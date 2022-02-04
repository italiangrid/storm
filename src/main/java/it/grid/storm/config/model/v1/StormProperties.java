package it.grid.storm.config.model.v1;

public class StormProperties {

  /* Configuration file properties */
  public static final String MANAGED_SURLS_KEY = "storm.service.SURL.endpoint";
  public static final String SERVICE_DEFAULT_PORTS = "storm.service.SURL.default-ports";
  public static final String SERVICE_HOSTNAME_KEY = "storm.service.FE-public.hostname";
  public static final String SERVICE_PORT_KEY = "storm.service.port";
  public static final String DB_URL_HOSTNAME_KEY = "storm.service.request-db.host";
  public static final String DB_URL_PROPERTIES_KEY = "storm.service.request-db.properties";
  public static final String DB_USER_NAME_KEY = "storm.service.request-db.username";
  public static final String DB_PASSWORD_KEY = "storm.service.request-db.passwd";
  public static final String BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY =
      "persistence.internal-db.connection-pool.maxActive";
  public static final String BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY =
      "persistence.internal-db.connection-pool.maxWait";
  public static final String XMLRPC_SERVER_PORT_KEY = "synchcall.xmlrpc.unsecureServerPort";
  public static final String XMLRPC_MAX_THREAD_KEY = "synchcall.xmlrpc.maxthread";
  public static final String XMLRPC_MAX_QUEUE_SIZE_KEY = "synchcall.xmlrpc.max_queue_size";
  public static final String REST_SERVICES_PORT_KEY = "storm.rest.services.port";
  public static final String REST_SERVICES_MAX_THREAD = "storm.rest.services.maxthread";
  public static final String REST_SERVICES_MAX_QUEUE_SIZE = "storm.rest.services.max_queue_size";
  public static final String XMLRPC_SECURITY_ENABLED_KEY = "synchcall.xmlrpc.security.enabled";
  public static final String XMLRPC_SECURITY_TOKEN_KEY = "synchcall.xmlrpc.security.token";

  public static final String DISKUSAGE_SERVICE_ENABLED = "storm.service.du.enabled";
  public static final String DISKUSAGE_SERVICE_INITIAL_DELAY = "storm.service.du.delaySecs";
  public static final String DISKUSAGE_SERVICE_TASKS_INTERVAL = "storm.service.du.periodSecs";
  public static final String DISKUSAGE_SERVICE_TASKS_PARALLEL = "storm.service.du.parallelTasks";

  public static final String SANITY_CHECK_ENABLED_KEY = "sanity-check.enabled";

  public static final String LS_MAX_NUMBER_OF_ENTRY_KEY = "synchcall.directoryManager.maxLsEntry";
  public static final String LS_ALL_LEVEL_RECURSIVE_KEY =
      "synchcall.directoryManager.default.AllLevelRecursive";
  public static final String LS_NUM_OF_LEVELS_KEY = "synchcall.directoryManager.default.Levels";
  public static final String LS_OFFSET_KEY = "synchcall.directoryManager.default.Offset";

  public static final String AUTOMATIC_DIRECTORY_CREATION_KEY = "directory.automatic-creation";
  public static final String ENABLE_WRITE_PERM_ON_DIRECTORY_KEY = "directory.writeperm";

  public static final String FILE_DEFAULT_SIZE_KEY = "fileSize.default";
  public static final String FILE_LIFETIME_DEFAULT_KEY = "fileLifetime.default";
  public static final String DEFAULT_OVERWRITE_MODE_KEY = "default.overwrite";
  public static final String DEFAULT_FILE_STORAGE_TYPE_KEY = "default.storagetype";

  public static final String EXTRA_SLASHES_FOR_FILE_TURL_KEY = "extraslashes.file";
  public static final String EXTRA_SLASHES_FOR_RFIO_TURL_KEY = "extraslashes.rfio";
  public static final String EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY = "extraslashes.gsiftp";
  public static final String EXTRA_SLASHES_FOR_ROOT_TURL_KEY = "extraslashes.root";

  public static final String PTG_SKIP_ACL_SETUP = "ptg.skip-acl-setup";

  public static final String HEARTHBEAT_PERIOD_KEY = "health.electrocardiogram.period";
  public static final String PERFORMANCE_GLANCE_TIME_INTERVAL_KEY =
      "health.performance.glance.timeInterval";
  public static final String PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY =
      "health.performance.logbook.timeInterval";
  public static final String PERFORMANCE_MEASURING_KEY = "health.performance.mesauring.enabled";
  public static final String BOOK_KEEPING_ENABLED_KEY = "health.bookkeeping.enabled";

  public static final String PICKING_INITIAL_DELAY_KEY = "asynch.PickingInitialDelay";
  public static final String PICKING_TIME_INTERVAL_KEY = "asynch.PickingTimeInterval";
  public static final String PICKING_MAX_BATCH_SIZE_KEY = "asynch.PickingMaxBatchSize";

  public static final String CORE_POOL_SIZE_KEY = "scheduler.crusher.workerCorePoolSize";
  public static final String MAX_POOL_SIZE_KEY = "scheduler.crusher.workerMaxPoolSize";
  public static final String QUEUE_SIZE_KEY = "scheduler.crusher.queueSize";

  public static final String PTP_CORE_POOL_SIZE_KEY = "scheduler.chunksched.ptp.workerCorePoolSize";
  public static final String PTP_MAX_POOL_SIZE_KEY = "scheduler.chunksched.ptp.workerMaxPoolSize";
  public static final String PTP_QUEUE_SIZE_KEY = "scheduler.chunksched.ptp.queueSize";
  public static final String PTG_CORE_POOL_SIZE_KEY = "scheduler.chunksched.ptg.workerCorePoolSize";
  public static final String PTG_MAX_POOL_SIZE_KEY = "scheduler.chunksched.ptg.workerMaxPoolSize";
  public static final String PTG_QUEUE_SIZE_KEY = "scheduler.chunksched.ptg.queueSize";
  public static final String BOL_CORE_POOL_SIZE_KEY = "scheduler.chunksched.bol.workerCorePoolSize";
  public static final String BOL_MAX_POOL_SIZE_KEY = "scheduler.chunksched.bol.workerMaxPoolSize";
  public static final String BOL_QUEUE_SIZE_KEY = "scheduler.chunksched.bol.queueSize";

  public static final String PIN_LIFETIME_DEFAULT_KEY = "pinLifetime.default";
  public static final String PIN_LIFETIME_MAXIMUM_KEY = "pinLifetime.maximum";

  public static final String CLEANING_INITIAL_DELAY_KEY = "gc.pinnedfiles.cleaning.delay";
  public static final String CLEANING_TIME_INTERVAL_KEY = "gc.pinnedfiles.cleaning.interval";

  public static final String TRANSIT_INITIAL_DELAY_KEY = "transit.delay";
  public static final String TRANSIT_TIME_INTERVAL_KEY = "transit.interval";
  public static final String EXPIRED_INPROGRESS_PTP_TIME_KEY = "expired.inprogress.time";

  public static final String PURGE_BATCH_SIZE_KEY = "purge.size";
  public static final String EXPIRED_REQUEST_TIME_KEY = "expired.request.time";
  public static final String REQUEST_PURGER_DELAY_KEY = "purge.delay";
  public static final String REQUEST_PURGER_PERIOD_KEY = "purge.interval";
  public static final String EXPIRED_REQUEST_PURGING_KEY = "purging";

  public static final String PING_VALUES_PROPERTIES_FILENAME_KEY = "ping-properties.filename";
  public static final String MAX_LOOP_KEY = "abort.maxloop";
  public static final String GPFS_QUOTA_REFRESH_PERIOD_KEY = "info.quota.refresh.period";
  public static final String SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY =
      "server-pool.status-check.timeout";
  public static final String HTTP_TURL_PREFIX = "http.turl_prefix";

}
