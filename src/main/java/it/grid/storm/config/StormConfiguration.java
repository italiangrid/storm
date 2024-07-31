/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.config;

import static it.grid.storm.config.ConfigurationDefaults.AUTOMATIC_DIRECTORY_CREATION;
import static it.grid.storm.config.ConfigurationDefaults.BOL_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOOK_KEEPING_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.CLEANING_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.CLEANING_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_PASSWORD;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MAXWAITMILLIS;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TESTONBORROW;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TESTWHILEIDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_URL_HOSTNAME;
import static it.grid.storm.config.ConfigurationDefaults.DB_URL_PORT;
import static it.grid.storm.config.ConfigurationDefaults.DB_URL_PROPERTIES;
import static it.grid.storm.config.ConfigurationDefaults.DB_USER_NAME;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_FILE_STORAGE_TYPE;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_OVERWRITE_MODE;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.ENABLE_WRITE_PERM_ON_DIRECTORY;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_INPROGRESS_BOL_TIME;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_INPROGRESS_PTP_TIME;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_REQUEST_PURGING;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_REQUEST_TIME;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_FILE_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_GSIFTP_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_RFIO_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_ROOT_TURL;
import static it.grid.storm.config.ConfigurationDefaults.FILE_DEFAULT_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.FILE_LIFETIME_DEFAULT;
import static it.grid.storm.config.ConfigurationDefaults.GPFS_QUOTA_REFRESH_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.GRIDFTP_TIME_OUT;
import static it.grid.storm.config.ConfigurationDefaults.GRID_USER_MAPPER_CLASSNAME;
import static it.grid.storm.config.ConfigurationDefaults.HEARTHBEAT_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.JAVA_NET_PREFERIPV6ADDRESSES;
import static it.grid.storm.config.ConfigurationDefaults.LS_ALL_LEVEL_RECURSIVE;
import static it.grid.storm.config.ConfigurationDefaults.LS_MAX_NUMBER_OF_ENTRY;
import static it.grid.storm.config.ConfigurationDefaults.LS_NUM_OF_LEVELS;
import static it.grid.storm.config.ConfigurationDefaults.LS_OFFSET;
import static it.grid.storm.config.ConfigurationDefaults.MAX_LOOP;
import static it.grid.storm.config.ConfigurationDefaults.MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_GLANCE_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_LOGBOOK_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_MEASURING;
import static it.grid.storm.config.ConfigurationDefaults.PICKING_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.PICKING_MAX_BATCH_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PICKING_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PING_VALUES_PROPERTIES_FILENAME;
import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_DEFAULT;
import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_MAXIMUM;
import static it.grid.storm.config.ConfigurationDefaults.PTG_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.PTP_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTP_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTP_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTP_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.PURGE_BATCH_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS;
import static it.grid.storm.config.ConfigurationDefaults.REQUEST_PURGER_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.REQUEST_PURGER_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_THREAD;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_PORT;
import static it.grid.storm.config.ConfigurationDefaults.SANITY_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;
import static it.grid.storm.config.ConfigurationDefaults.SRM_SERVICE_PORT;
import static it.grid.storm.config.ConfigurationDefaults.STORMBEISAM_POOL_MAXTOTAL;
import static it.grid.storm.config.ConfigurationDefaults.STORMBEISAM_POOL_MINIDLE;
import static it.grid.storm.config.ConfigurationDefaults.STORMDB_POOL_MAXTOTAL;
import static it.grid.storm.config.ConfigurationDefaults.STORMDB_POOL_MINIDLE;
import static it.grid.storm.config.ConfigurationDefaults.SYNCHRONOUS_QUOTA_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.TRANSIT_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.TRANSIT_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_THREAD;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SECURITY_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SERVER_PORT;
import static it.grid.storm.info.du.DiskUsageService.DEFAULT_INITIAL_DELAY;
import static it.grid.storm.info.du.DiskUsageService.DEFAULT_TASKS_INTERVAL;
import static it.grid.storm.info.du.DiskUsageService.DEFAULT_TASKS_PARALLEL;
import static java.lang.System.getProperty;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Singleton holding all configuration values that any other object in the StoRM backend reads from
 * configuration files, databases, etc. Implements a 'get<something>' method for each value that
 * should be looked up this way. In fact, this is a "read-only" class. If no value is specified in
 * the configuration medium, a default one is used instead; some properties may hold several comma
 * separated values without any white spaces in-between; the name of the property in the
 * configuration medium, default values, as well as the option of holding multiple values, is
 * specified in each method comment.
 */

public class StormConfiguration {

  public static final String DEFAULT_STORM_CONFIG_FILE =
      "/etc/storm/backend-server/storm.properties";

  private static Logger log = LoggerFactory.getLogger(StormConfiguration.class);

  private final ConfigReader cr;

  private static StormConfiguration instance;

  /* System properties */
  public static final String CONFIG_FILE_PATH = "storm.configuration.file";

  /* Configuration file properties */
  private static final String MANAGED_SURLS_KEY = "storm.service.SURL.endpoint";
  private static final String MANAGED_SURL_DEFAULT_PORTS_KEY = "storm.service.SURL.default-ports";
  private static final String SERVICE_HOSTNAME_KEY = "storm.service.FE-public.hostname";
  private static final String SERVICE_PORT_KEY = "storm.service.port";
  private static final String DB_URL_HOSTNAME_LEGACY_KEY = "storm.service.request-db.host";
  private static final String DB_URL_PORT_LEGACY_KEY = "storm.service.request-db.port";
  private static final String DB_URL_PROPERTIES_LEGACY_KEY = "storm.service.request-db.properties";
  private static final String DB_USER_NAME_LEGACY_KEY = "storm.service.request-db.username";
  private static final String DB_PASSWORD_LEGACY_KEY = "storm.service.request-db.passwd";

  private static final String DB_URL_HOSTNAME_KEY = "storm.service.db.host";
  private static final String DB_URL_PORT_KEY = "storm.service.db.port";
  private static final String DB_URL_PROPERTIES_KEY = "storm.service.db.properties";
  private static final String DB_USER_NAME_KEY = "storm.service.db.username";
  private static final String DB_PASSWORD_KEY = "storm.service.db.password";

  private static final String DB_POOL_MAXWAITMILLIS_KEY = "storm.service.db.pool.maxWaitMillis";
  private static final String DB_POOL_TESTONBORROW_KEY = "storm.service.db.pool.testOnBorrow";
  private static final String DB_POOL_TESTWHILEIDLE_KEY = "storm.service.db.pool.testWhileIdle";
  private static final String STORMDB_POOL_MAXTOTAL_KEY = "storm.service.db.pool.stormdb.maxTotal";
  private static final String STORMDB_POOL_MINIDLE_KEY = "storm.service.db.pool.stormdb.minIdle";
  private static final String STORMBEISAM_POOL_MAXTOTAL_KEY =
      "storm.service.db.pool.stormbeisam.maxTotal";
  private static final String STORMBEISAM_POOL_MINIDLE_KEY =
      "storm.service.db.pool.stormbeisam.minIdle";

  private static final String CLEANING_INITIAL_DELAY_KEY = "gc.pinnedfiles.cleaning.delay";
  private static final String CLEANING_TIME_INTERVAL_KEY = "gc.pinnedfiles.cleaning.interval";
  private static final String FILE_DEFAULT_SIZE_KEY = "fileSize.default";
  private static final String FILE_LIFETIME_DEFAULT_KEY = "fileLifetime.default";
  private static final String PIN_LIFETIME_DEFAULT_KEY = "pinLifetime.default";
  private static final String PIN_LIFETIME_MAXIMUM_KEY = "pinLifetime.maximum";
  private static final String TRANSIT_INITIAL_DELAY_KEY = "transit.delay";
  private static final String TRANSIT_TIME_INTERVAL_KEY = "transit.interval";
  private static final String PICKING_INITIAL_DELAY_KEY = "asynch.PickingInitialDelay";
  private static final String PICKING_TIME_INTERVAL_KEY = "asynch.PickingTimeInterval";
  private static final String PICKING_MAX_BATCH_SIZE_KEY = "asynch.PickingMaxBatchSize";
  private static final String XMLRPC_MAX_THREAD_KEY = "synchcall.xmlrpc.maxthread";
  private static final String XMLRPC_MAX_QUEUE_SIZE_KEY = "synchcall.xmlrpc.max_queue_size";
  private static final String LIST_OF_DEFAULT_SPACE_TOKEN_KEY = "storm.service.defaultSpaceTokens";
  private static final String XMLRPC_SERVER_PORT_KEY = "synchcall.xmlrpc.unsecureServerPort";
  private static final String LS_MAX_NUMBER_OF_ENTRY_KEY = "synchcall.directoryManager.maxLsEntry";
  private static final String LS_ALL_LEVEL_RECURSIVE_KEY =
      "synchcall.directoryManager.default.AllLevelRecursive";
  private static final String LS_NUM_OF_LEVELS_KEY = "synchcall.directoryManager.default.Levels";
  private static final String LS_OFFSET_KEY = "synchcall.directoryManager.default.Offset";
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
  private static final String CORE_POOL_SIZE_KEY = "scheduler.crusher.workerCorePoolSize";
  private static final String MAX_POOL_SIZE_KEY = "scheduler.crusher.workerMaxPoolSize";
  private static final String QUEUE_SIZE_KEY = "scheduler.crusher.queueSize";
  private static final String GRIDFTP_TIME_OUT_KEY = "asynch.srmcopy.gridftp.timeout";
  private static final String AUTOMATIC_DIRECTORY_CREATION_KEY = "directory.automatic-creation";
  private static final String DEFAULT_OVERWRITE_MODE_KEY = "default.overwrite";
  private static final String DEFAULT_FILE_STORAGE_TYPE_KEY = "default.storagetype";
  private static final String PURGE_BATCH_SIZE_KEY = "purge.size";
  private static final String EXPIRED_REQUEST_TIME_KEY = "expired.request.time";
  private static final String EXPIRED_INPROGRESS_BOL_TIME_KEY = "expired.inprogress.bol.time";
  private static final String EXPIRED_INPROGRESS_PTP_TIME_KEY = "expired.inprogress.ptp.time";
  private static final String REQUEST_PURGER_DELAY_KEY = "purge.delay";
  private static final String REQUEST_PURGER_PERIOD_KEY = "purge.interval";
  private static final String EXPIRED_REQUEST_PURGING_KEY = "purging";
  private static final String EXTRA_SLASHES_FOR_FILE_TURL_KEY = "extraslashes.file";
  private static final String EXTRA_SLASHES_FOR_RFIO_TURL_KEY = "extraslashes.rfio";
  private static final String EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY = "extraslashes.gsiftp";
  private static final String EXTRA_SLASHES_FOR_ROOT_TURL_KEY = "extraslashes.root";
  private static final String PING_VALUES_PROPERTIES_FILENAME_KEY = "ping-properties.filename";
  private static final String HEARTHBEAT_PERIOD_KEY = "health.electrocardiogram.period";
  private static final String PERFORMANCE_GLANCE_TIME_INTERVAL_KEY =
      "health.performance.glance.timeInterval";
  private static final String PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY =
      "health.performance.logbook.timeInterval";
  private static final String PERFORMANCE_MEASURING_KEY = "health.performance.mesauring.enabled";
  private static final String BOOK_KEEPING_ENABLED_KEY = "health.bookkeeping.enabled";
  private static final String ENABLE_WRITE_PERM_ON_DIRECTORY_KEY = "directory.writeperm";
  private static final String MAX_LOOP_KEY = "abort.maxloop";
  private static final String GRID_USER_MAPPER_CLASSNAME_KEY = "griduser.mapper.classname";
  private static final String AUTHZ_DB_PATH_KEY = "authzdb.path";
  private static final String REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS_KEY = "authzdb.refreshrate";
  private static final String REST_SERVICES_PORT_KEY = "storm.rest.services.port";
  private static final String REST_SERVICES_MAX_THREAD_KEY = "storm.rest.services.maxthread";
  private static final String REST_SERVICES_MAX_QUEUE_SIZE_KEY =
      "storm.rest.services.max_queue_size";
  private static final String STORM_PROPERTIES_VERSION_KEY = "storm.properties.version";
  private static final String SYNCHRONOUS_QUOTA_CHECK_ENABLED_KEY = "info.quota-check.enabled";
  private static final String GPFS_QUOTA_REFRESH_PERIOD_KEY = "info.quota.refresh.period";
  private static final String SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY =
      "server-pool.status-check.timeout";
  private static final String SANITY_CHECK_ENABLED_KEY = "sanity-check.enabled";
  private static final String XMLRPC_SECURITY_ENABLED_KEY = "synchcall.xmlrpc.security.enabled";
  private static final String XMLRPC_SECURITY_TOKEN_KEY = "synchcall.xmlrpc.security.token";
  private static final String PTG_SKIP_ACL_SETUP_KEY = "ptg.skip-acl-setup";
  private static final String PTP_SKIP_ACL_SETUP_KEY = "ptp.skip-acl-setup";
  private static final String HTTP_TURL_PREFIX = "http.turl_prefix";
  private static final String NETWORKADDRESS_CACHE_TTL = "networkaddress.cache.ttl";
  private static final String NETWORKADDRESS_CACHE_NEGATIVE_TTL =
      "networkaddress.cache.negative.ttl";

  public static final String DISKUSAGE_SERVICE_ENABLED_KEY = "storm.service.du.enabled";
  private static final String DISKUSAGE_SERVICE_INITIAL_DELAY_KEY = "storm.service.du.delaySecs";
  private static final String DISKUSAGE_SERVICE_TASKS_INTERVAL_KEY = "storm.service.du.periodSecs";
  private static final String DISKUSAGE_SERVICE_TASKS_PARALLEL_KEY =
      "storm.service.du.parallelTasks";

  private static final String JAVA_NET_PREFERIPV6ADDRESSES_KEY = "java.net.preferIPv6Addresses";

  public static void init(String filePath) throws IOException, ConfigurationException {
    instance = new StormConfiguration(filePath);
  }

  private StormConfiguration(String filePath) throws IOException, ConfigurationException {
    cr = new ConfigReader(filePath);
  }

  /**
   * Returns the sole instance of the Configuration class.
   */
  public static StormConfiguration getInstance() {

    return StormConfiguration.instance;
  }

  /**
   * Method that returns the directory holding the configuration file. The methods that make use of
   * it are uncertain... must be found soon!!! Beware that the configuration directory is implicit
   * in the complete pathname to the configuration file supplied in the command line when starting
   * StoRM BE.
   */
  public String configurationDir() {

    return cr.configurationDirectory();
  }

  /**
   * getNamespaceConfigPath
   * 
   * @return String
   */
  public String namespaceConfigPath() {

    return String.format("%s%setc", getProperty("user.dir"), File.separator);
  }

  /**
   * MANDATORY CONFIGURATION PARAMETER! Define the SURL end-points.
   * 
   * @return String[]
   */
  public String[] getManagedSURLs() {

    String[] defaultValue = {"UNDEFINED_SERVICE_ENDPOINT"};
    if (!cr.getConfiguration().containsKey(MANAGED_SURLS_KEY)) {
      return defaultValue;
    }
    return cr.getConfiguration().getStringArray(MANAGED_SURLS_KEY);
  }

  /**
   * @return
   */
  public Integer[] getManagedSurlDefaultPorts() {

    Integer[] portsArray;
    if (!cr.getConfiguration().containsKey(MANAGED_SURL_DEFAULT_PORTS_KEY)) {
      portsArray = new Integer[] {ConfigurationDefaults.SRM_SERVICE_PORT};
    } else {
      // load from external source
      String[] portString = cr.getConfiguration().getStringArray(MANAGED_SURL_DEFAULT_PORTS_KEY);
      ArrayList<Integer> ports = new ArrayList<>();
      for (String port : portString) {
        ports.add(Integer.parseInt(port.trim()));
      }
      portsArray = ports.toArray(new Integer[0]);
    }
    return portsArray;
  }

  /**
   * @return String
   */
  public String getServiceHostname() {

    String hostname = cr.getConfiguration().getString(SERVICE_HOSTNAME_KEY);
    if (hostname == null) {
      log.error("Hostname not defined! Please set '{}' property", SERVICE_HOSTNAME_KEY);
      throw new IllegalArgumentException(SERVICE_HOSTNAME_KEY + " not set!");
    }
    return hostname;
  }

  public int getServicePort() {

    return cr.getConfiguration().getInt(SERVICE_PORT_KEY, SRM_SERVICE_PORT);
  }

  public String getDbUsername() {

    return cr.getConfiguration()
      .getString(DB_USER_NAME_KEY,
          cr.getConfiguration().getString(DB_USER_NAME_LEGACY_KEY, DB_USER_NAME));
  }

  public String getDbPassword() {

    return cr.getConfiguration()
      .getString(DB_PASSWORD_KEY,
          cr.getConfiguration().getString(DB_PASSWORD_LEGACY_KEY, DB_PASSWORD));
  }

  public String getDbHostname() {

    return cr.getConfiguration()
      .getString(DB_URL_HOSTNAME_KEY,
          cr.getConfiguration().getString(DB_URL_HOSTNAME_LEGACY_KEY, DB_URL_HOSTNAME));
  }

  public String getDbProperties() {

    return cr.getConfiguration()
      .getString(DB_URL_PROPERTIES_KEY,
          cr.getConfiguration().getString(DB_URL_PROPERTIES_LEGACY_KEY, DB_URL_PROPERTIES));
  }

  public int getDbPort() {

    return cr.getConfiguration()
      .getInt(DB_URL_PORT_KEY, cr.getConfiguration().getInt(DB_URL_PORT_LEGACY_KEY, DB_URL_PORT));
  }

  /**
   * Sets the MaxWaitMillis property. Use -1 to make the pool wait indefinitely.
   */
  public int getDbPoolMaxWaitMillis() {

    return cr.getConfiguration().getInt(DB_POOL_MAXWAITMILLIS_KEY, DB_POOL_MAXWAITMILLIS);
  }

  /**
   * This property determines whether or not the pool will validate objects before they are borrowed
   * from the pool.
   */
  public boolean isDbPoolTestOnBorrow() {

    return cr.getConfiguration().getBoolean(DB_POOL_TESTONBORROW_KEY, DB_POOL_TESTONBORROW);
  }

  /**
   * This property determines whether or not the idle object evictor will validate connections.
   */
  public boolean isDbPoolTestWhileIdle() {

    return cr.getConfiguration().getBoolean(DB_POOL_TESTWHILEIDLE_KEY, DB_POOL_TESTWHILEIDLE);
  }

  public int getStormDbPoolSize() {

    return cr.getConfiguration().getInt(STORMDB_POOL_MAXTOTAL_KEY, STORMDB_POOL_MAXTOTAL);
  }

  public int getStormDbPoolMinIdle() {

    return cr.getConfiguration().getInt(STORMDB_POOL_MINIDLE_KEY, STORMDB_POOL_MINIDLE);
  }

  public int getStormBeIsamPoolSize() {

    return cr.getConfiguration().getInt(STORMBEISAM_POOL_MAXTOTAL_KEY, STORMBEISAM_POOL_MAXTOTAL);
  }

  public int getStormBeIsamPoolMinIdle() {

    return cr.getConfiguration().getInt(STORMBEISAM_POOL_MINIDLE_KEY, STORMBEISAM_POOL_MINIDLE);
  }

  /**
   * Method used by PinnedFilesCatalog to get the initial delay in _seconds_ before starting the
   * cleaning thread. If no value is found in the configuration medium, then the default value is
   * returned instead. key="pinnedfiles.cleaning.delay"; default value=10;
   */
  public long getCleaningInitialDelay() {

    return cr.getConfiguration().getLong(CLEANING_INITIAL_DELAY_KEY, CLEANING_INITIAL_DELAY);
  }

  /**
   * Method used by PinnedFilesCatalog to get the cleaning time interval, in _seconds_. If no value
   * is found in the configuration medium, then the default value is returned instead.
   * key="pinnedfiles.cleaning.interval"; default value=300; Keep in mind that 300 seconds = 5
   * minutes.
   */
  public long getCleaningTimeInterval() {

    return cr.getConfiguration().getLong(CLEANING_TIME_INTERVAL_KEY, CLEANING_TIME_INTERVAL);
  }

  /**
   * Get the default file size
   * 
   * @return
   */
  public long getFileDefaultSize() {

    return cr.getConfiguration().getLong(FILE_DEFAULT_SIZE_KEY, FILE_DEFAULT_SIZE);
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the default fileLifetime to use when a volatile
   * entry is being added/updated, but the user specified a non positive value. Measured in
   * _seconds_. If no value is found in the configuration medium, then the default value is returned
   * instead. key="fileLifetime.default"; default value=3600;
   */
  public long getFileLifetimeDefault() {

    return cr.getConfiguration().getLong(FILE_LIFETIME_DEFAULT_KEY, FILE_LIFETIME_DEFAULT);
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the minimum pinLifetime allowed, when a jit is
   * being added/updated, but the user specified a lower one. This method is also used by the
   * PinLifetimeConverter to translate a NULL/0/negative value to a default one. Measured in
   * _seconds_. If no value is found in the configuration medium, then the default value is returned
   * instead. key="pinLifetime.minimum"; default value=259200;
   */
  public long getPinLifetimeDefault() {

    return cr.getConfiguration().getLong(PIN_LIFETIME_DEFAULT_KEY, PIN_LIFETIME_DEFAULT);
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the maximum pinLifetime allowed, when a jit is
   * being added/updated, but the user specified a higher one. Measured in _seconds_. If no value is
   * found in the configuration medium, then the default value is returned instead.
   * key="pinLifetime.maximum"; default value=1814400 (21 days);
   */
  public long getPinLifetimeMaximum() {

    return cr.getConfiguration().getLong(PIN_LIFETIME_MAXIMUM_KEY, PIN_LIFETIME_MAXIMUM);
  }

  /**
   * Method used by PtPChunkCatalog to get the initial delay in _seconds_ before starting the
   * transiting thread. If no value is found in the configuration medium, then the default value is
   * returned instead. key="transit.delay"; default value=10;
   */
  public long getTransitInitialDelay() {

    return cr.getConfiguration().getLong(TRANSIT_INITIAL_DELAY_KEY, TRANSIT_INITIAL_DELAY);
  }

  /**
   * Method used by PtPChunkCatalog to get the transiting time interval, in _seconds_. If no value
   * is found in the configuration medium, then the default value is returned instead.
   * key="transit.interval"; default value=300; Keep in mind that 300 seconds = 5 minutes.
   */
  public long getTransitTimeInterval() {

    return cr.getConfiguration().getLong(TRANSIT_TIME_INTERVAL_KEY, TRANSIT_TIME_INTERVAL);
  }

  /**
   * Method used by AdvancedPicker to get the initial delay before starting to pick data from the
   * DB, in _seconds_. If no value is found in the configuration medium, then the default value is
   * returned instead. key="asynch.PickingInitialDelay"; default value=1;
   */
  public long getPickingInitialDelay() {

    return cr.getConfiguration().getLong(PICKING_INITIAL_DELAY_KEY, PICKING_INITIAL_DELAY);
  }

  /**
   * Method used by AdvancedPicker to get the time interval of successive pickings, in _seconds_. If
   * no value is found in the configuration medium, then the default value is returned instead.
   * key="asynch.PickingTimeInterval"; default value=15;
   */
  public long getPickingTimeInterval() {

    return cr.getConfiguration().getLong(PICKING_TIME_INTERVAL_KEY, PICKING_TIME_INTERVAL);
  }

  /**
   * Method used by RequestSummaryDAO to establish the maximum number of requests to retrieve with
   * each polling. If no value is found in the configuration medium, then the default value is
   * returned instead. key="asynch.PickingMaxBatchSize"; default value=100;
   */
  public int getPickingMaxBatchSize() {

    return cr.getConfiguration().getInt(PICKING_MAX_BATCH_SIZE_KEY, PICKING_MAX_BATCH_SIZE);
  }

  /**
   * Get max number of XMLRPC threads into for the XMLRPC server.
   */
  public int getXMLRPCMaxThread() {

    int res = cr.getConfiguration().getInt(XMLRPC_MAX_THREAD_KEY, XMLRPC_MAX_THREAD);
    return res <= 0 ? XMLRPC_MAX_THREAD : res;
  }

  public int getXMLRPCMaxQueueSize() {

    int res = cr.getConfiguration().getInt(XMLRPC_MAX_QUEUE_SIZE_KEY, XMLRPC_MAX_QUEUE_SIZE);
    return res <= 0 ? XMLRPC_MAX_QUEUE_SIZE : res;
  }

  /**
   * Get Default Space Tokens
   * 
   * @return
   */
  public List<String> getListOfDefaultSpaceToken() {

    if (cr.getConfiguration().containsKey(LIST_OF_DEFAULT_SPACE_TOKEN_KEY)) {

      String[] namesArray = cr.getConfiguration().getStringArray(LIST_OF_DEFAULT_SPACE_TOKEN_KEY);
      if (namesArray != null) {
        return Arrays.asList(namesArray);
      }
    }
    return Lists.newArrayList();
  }

  /**
   * Method used by the Synch Component to set the binding port for the _unsecure_ xmlrpc server in
   * the BE. If no value is found in the configuration medium, then the default value is returned
   * instead. key="synchcall.xmlrpc.unsecureServerPort"; default value=8080;
   */
  public int getXmlRpcServerPort() {

    return cr.getConfiguration().getInt(XMLRPC_SERVER_PORT_KEY, XMLRPC_SERVER_PORT);
  }

  /**
   * Method used by the Synch Component to set the maximum number of entries to return for the srmLs
   * functionality. If no value is found in the configuration medium, then the default value is
   * returned instead. key="synchcall.directoryManager.maxLsEntry"; default value=500;
   * 
   * @return int
   */
  public int getLSMaxNumberOfEntry() {

    return cr.getConfiguration().getInt(LS_MAX_NUMBER_OF_ENTRY_KEY, LS_MAX_NUMBER_OF_ENTRY);
  }

  /**
   * Default value for the parameter "allLevelRecursive" of the LS request.
   * 
   * @return boolean
   */
  public boolean getLSallLevelRecursive() {

    return cr.getConfiguration().getBoolean(LS_ALL_LEVEL_RECURSIVE_KEY, LS_ALL_LEVEL_RECURSIVE);
  }

  /**
   * Default value for the parameter "numOfLevels" of the LS request.
   * 
   * @return int
   */
  public int getLSnumOfLevels() {

    return cr.getConfiguration().getInt(LS_NUM_OF_LEVELS_KEY, LS_NUM_OF_LEVELS);
  }

  /**
   * Default value for the parameter "offset" of the LS request.
   * 
   * @return int
   */
  public int getLSoffset() {

    return cr.getConfiguration().getInt(LS_OFFSET_KEY, LS_OFFSET);
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
   * threads to keep in the pool, even if they are idle. If no value is found in the configuration
   * medium, then the default value is returned instead.
   * key="scheduler.chunksched.ptp.workerCorePoolSize"; default value=50;
   */
  public int getPtPCorePoolSize() {

    return cr.getConfiguration().getInt(PTP_CORE_POOL_SIZE_KEY, PTP_CORE_POOL_SIZE);
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
   * of threads to allow in the pool. If no value is found in the configuration medium, then the
   * default value is returned instead. key="scheduler.chunksched.ptp.workerMaxPoolSize"; default
   * value=100;
   */
  public int getPtPMaxPoolSize() {

    return cr.getConfiguration().getInt(PTP_MAX_POOL_SIZE_KEY, PTP_MAX_POOL_SIZE);
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
   * key="scheduler.chunksched.ptp.queueSize"; default value=100;
   */
  public int getPtPQueueSize() {

    return cr.getConfiguration().getInt(PTP_QUEUE_SIZE_KEY, PTP_QUEUE_SIZE);
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
   * key="scheduler.chunksched.ptg.workerCorePoolSize"; default value=50;
   */
  public int getPtGCorePoolSize() {

    return cr.getConfiguration().getInt(PTG_CORE_POOL_SIZE_KEY, PTG_CORE_POOL_SIZE);
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
   * of threads to allow in the pool. key="scheduler.chunksched.ptg.workerMaxPoolSize"; default
   * value=200;
   */
  public int getPtGMaxPoolSize() {

    return cr.getConfiguration().getInt(PTG_MAX_POOL_SIZE_KEY, PTG_MAX_POOL_SIZE);
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
   * key="scheduler.chunksched.ptg.queueSize"; default value=2000;
   */
  public int getPtGQueueSize() {

    return cr.getConfiguration().getInt(PTG_QUEUE_SIZE_KEY, PTG_QUEUE_SIZE);
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
   * if they are idle. key="scheduler.chunksched.bol.workerCorePoolSize"; default value=50;
   */
  public int getBoLCorePoolSize() {

    return cr.getConfiguration().getInt(BOL_CORE_POOL_SIZE_KEY, BOL_CORE_POOL_SIZE);
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
   * pool. key="scheduler.chunksched.bol.workerMaxPoolSize"; default value=200;
   */
  public int getBoLMaxPoolSize() {

    return cr.getConfiguration().getInt(BOL_MAX_POOL_SIZE_KEY, BOL_MAX_POOL_SIZE);
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
   * the Runnable tasks submitted by the execute method. key="scheduler.chunksched.bol.queueSize";
   * default value=2000;
   */
  public int getBoLQueueSize() {

    return cr.getConfiguration().getInt(BOL_QUEUE_SIZE_KEY, BOL_QUEUE_SIZE);
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
   * are idle. key="scheduler.crusher.workerCorePoolSize"; default value=10;
   */
  public int getCorePoolSize() {

    return cr.getConfiguration().getInt(CORE_POOL_SIZE_KEY, CORE_POOL_SIZE);
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler Max Pool Size for the
   * Crisher. If no value is found in the configuration medium, then the default value is returned
   * instead. Scheduler component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize. When a new task is
   * submitted in method execute, and fewer than corePoolSize threads are running, a new thread is
   * created to handle the request, even if other worker threads are idle. If there are more than
   * corePoolSize but less than maximumPoolSize threads running, a new thread will be created only
   * if the queue is full. By setting corePoolSize and maximumPoolSize the same, you create a
   * fixed-size thread pool. maxPoolSize - the maximum number of threads to allow in the pool.
   * key="scheduler.crusher.workerMaxPoolSize"; default value=50;
   */
  public int getMaxPoolSize() {

    return cr.getConfiguration().getInt(MAX_POOL_SIZE_KEY, MAX_POOL_SIZE);
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
   * submitted by the execute method. key="scheduler.crusher.queueSize"; default value=2000;
   */
  public int getQueueSize() {

    return cr.getConfiguration().getInt(QUEUE_SIZE_KEY, QUEUE_SIZE);
  }

  /**
   * getNamespaceConfigFilename
   * 
   * @return String
   */
  public String getNamespaceConfigFilename() {

    return "namespace.xml";
  }

  /**
   * Retrieve the namespace schema file name from the first line (attribute) of namespace.xml.
   * 
   * @return String
   */
  public String getNamespaceSchemaFilename() {

    return "namespace-1.5.0.xsd";
  }

  /**
   * Method used by NaiveGridFTP internal client in srmCopy to establish the time out in
   * milliseconds for a reply from the server. If no value is found in the configuration medium,
   * then the default one is used instead. key="NaiveGridFTP.TimeOut"; default value="15000"
   */
  public int getGridFTPTimeOut() {

    return cr.getConfiguration().getInt(GRIDFTP_TIME_OUT_KEY, GRIDFTP_TIME_OUT);
  }

  /**
   * Method used by PtPChunk to find out if missing local directories should be created
   * automatically or not. SRM 2.2 specification forbids automatic creation. If no value is found in
   * the configuration medium, then the default one is used instead.
   * key="automatic.directory.creation"; default value=false
   */
  public boolean getAutomaticDirectoryCreation() {

    return cr.getConfiguration()
      .getBoolean(AUTOMATIC_DIRECTORY_CREATION_KEY, AUTOMATIC_DIRECTORY_CREATION);
  }

  /**
   * Method used by TOverwriteModeConverter to establish the default OverwriteMode to use. If no
   * value is found in the configuration medium, then the default one is used instead.
   * key="default.overwrite"; default value="N"
   */
  public String getDefaultOverwriteMode() {

    return cr.getConfiguration().getString(DEFAULT_OVERWRITE_MODE_KEY, DEFAULT_OVERWRITE_MODE);
  }

  /**
   * Method used by FileStorageTypeConverter to establish the default TFileStorageType to use. If no
   * value is found in the configuration medium, then the default one is used instead.
   * key="default.storagetype"; default value="V"
   */
  public String getDefaultFileStorageType() {

    return cr.getConfiguration()
      .getString(DEFAULT_FILE_STORAGE_TYPE_KEY, DEFAULT_FILE_STORAGE_TYPE);
  }

  /**
   * Method used by RequestSummaryDAO to establish the batch size for removing expired requests. If
   * no value is found in the configuration medium, then the default one is used instead.
   * key="purge.size"; default value=800
   */
  public int getPurgeBatchSize() {

    return cr.getConfiguration().getInt(PURGE_BATCH_SIZE_KEY, PURGE_BATCH_SIZE);
  }

  /**
   * Method used by RequestSummaryDAO to establish the time that must be elapsed for considering a
   * request expired. The time measure specified in the configuration medium is in _days_. The value
   * returned by this method, is expressed in _seconds_ If no value is found in the configuration
   * medium, then the default one is used instead. key="expired.request.time"; default value=7 (days
   * - which correspond to 7 * 24 * 60 * 60 seconds)
   */
  public long getExpiredRequestTime() {

    return cr.getConfiguration().getInt(EXPIRED_REQUEST_TIME_KEY, EXPIRED_REQUEST_TIME);
  }

  /**
   * Method used by RequestSummaryCatalog to establish the initial delay before starting the purging
   * thread, in _seconds_. If no value is found in the configuration medium, then the default one is
   * used instead. key="purge.delay"; default value=10
   */
  public int getRequestPurgerDelay() {

    return cr.getConfiguration().getInt(REQUEST_PURGER_DELAY_KEY, REQUEST_PURGER_DELAY);
  }

  /**
   * Method used by RequestSummaryCatalog to establish the time interval in _seconds_ between
   * successive purging checks. If no value is found in the configuration medium, then the default
   * one is used instead. key="purge.interval"; default value=600 (1o minutes)
   */
  public int getRequestPurgerPeriod() {

    return cr.getConfiguration().getInt(REQUEST_PURGER_PERIOD_KEY, REQUEST_PURGER_PERIOD);
  }

  /**
   * Method used by RequestSummaryCatalog to establish if the purging of expired requests should be
   * enabled or not. If no value is found in the configuration medium, then the default one is used
   * instead. key="purging"; default value=true
   */
  public boolean getExpiredRequestPurging() {

    return cr.getConfiguration().getBoolean(EXPIRED_REQUEST_PURGING_KEY, EXPIRED_REQUEST_PURGING);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL If no value is found in the configuration medium, then the default one is used instead.
   * key="extraslashes.file"; default value="" (that is 'file:///) value = "/" ==> file:////
   */
  public String getExtraSlashesForFileTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_FILE_TURL_KEY, EXTRA_SLASHES_FOR_FILE_TURL);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL If no value is found in the configuration medium, then the default one is used instead.
   * key="extraslashes.rfio"; default value="" (that is 'rfio://<hostname>:port<PhysicalFN>')) value
   * = "/" ==> 'rfio://<hostname>:port/<PhysicalFN>'
   */
  public String getExtraSlashesForRFIOTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_RFIO_TURL_KEY, EXTRA_SLASHES_FOR_RFIO_TURL);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL If no value is found in the configuration medium, then the default one is used instead.
   * key="extraslashes.gsiftp"; default value="" (that is 'gsiftp://<hostname>:port<PhysicalFN>'))
   * value = "/" ==> 'gsiftp://<hostname>:port/<PhysicalFN>'
   */
  public String getExtraSlashesForGsiFTPTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY, EXTRA_SLASHES_FOR_GSIFTP_TURL);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL If no value is found in the configuration medium, then the default one is used instead.
   * key="extraslashes.root"; default value="/" (that is 'root://<hostname>:port<PhysicalFN>'))
   * value = "" ==> 'root://<hostname>:port<PhysicalFN>'
   */
  public String getExtraSlashesForROOTTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_ROOT_TURL_KEY, EXTRA_SLASHES_FOR_ROOT_TURL);
  }

  /**
   * Method used by Ping Executor to retrieve the Properties File Name where the properties
   * <key,value> are stored. If no value is found in the configuration medium, then the default one
   * is used instead. key="ping-properties.filename"; default value="" (that is
   * 'gsiftp://<hostname>:port<PhysicalFN>')) value = "/" ==>
   * 'gsiftp://<hostname>:port/<PhysicalFN>'
   */
  public String getPingValuesPropertiesFilename() {

    return cr.getConfiguration()
      .getString(PING_VALUES_PROPERTIES_FILENAME_KEY, PING_VALUES_PROPERTIES_FILENAME);
  }

  /**
   * If no value is found in the configuration medium, then the default one is used instead.
   * key="health.electrocardiogram.period"; default value=60 (1 min)
   */
  public int getHearthbeatPeriod() {

    return cr.getConfiguration().getInt(HEARTHBEAT_PERIOD_KEY, HEARTHBEAT_PERIOD);
  }

  /**
   * getHearthbeatPerformanceGlanceTimeInterval
   * 
   * @return int If no value is found in the configuration medium, then the default one is used
   *         instead. key="health.performance.glance.timeInterval"; default value=15 (15 sec)
   */
  public int getHearthbeatPerformanceGlanceTimeInterval() {

    return cr.getConfiguration()
      .getInt(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY, PERFORMANCE_GLANCE_TIME_INTERVAL);
  }

  /**
   * getHearthbeatPerformanceGlancePeriod
   * 
   * @return int If no value is found in the configuration medium, then the default one is used
   *         instead. key="health.performance.logbook.timeInterval"; default value=15 (15 sec)
   */
  public int getHearthbeatPerformanceLogbookTimeInterval() {

    return cr.getConfiguration()
      .getInt(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY, PERFORMANCE_LOGBOOK_TIME_INTERVAL);
  }

  /**
   * isHearthbeatPerformanceMeasuringEnabled
   * 
   * @return boolean If no value is found in the configuration medium, then the default one is used
   *         instead. key="health.performance.mesauring.enabled"; default value=false
   */
  public boolean isHearthbeatPerformanceMeasuringEnabled() {

    return cr.getConfiguration().getBoolean(PERFORMANCE_MEASURING_KEY, PERFORMANCE_MEASURING);
  }

  /**
   * isHearthbeatBookkeepingEnabled
   * 
   * @return boolean Method used by Namespace Configuration Reloading Strategy (Peeper). If "peeper"
   *         found namespace.xml config file changed it checks if it can perform an automatic
   *         reload. If no value is found in the configuration medium, then the default one is used
   *         instead. key="health.bookkeeping.enabled"; default value=false
   */
  public boolean isHearthbeatBookkeepingEnabled() {

    return cr.getConfiguration().getBoolean(BOOK_KEEPING_ENABLED_KEY, BOOK_KEEPING_ENABLED);
  }

  /**
   * Enable write permission on new created directory for LocalAuthorizationSource usage.
   * 
   * @return false by default, otherwise what is specified in the properties
   */
  public boolean getEnableWritePermOnDirectory() {

    return cr.getConfiguration()
      .getBoolean(ENABLE_WRITE_PERM_ON_DIRECTORY_KEY, ENABLE_WRITE_PERM_ON_DIRECTORY);
  }

  public int getMaxLoop() {

    return cr.getConfiguration().getInt(MAX_LOOP_KEY, MAX_LOOP);
  }

  /**
   * Method used to retrieve the ClassName for the User Mapper Class If no value is found in the
   * configuration medium, then the default one is used instead, that is
   * "it.grid.storm.griduser.LcmapsJNAMapper" key="griduser.mapper.classname";
   */
  public String getGridUserMapperClassname() {

    return cr.getConfiguration()
      .getString(GRID_USER_MAPPER_CLASSNAME_KEY, GRID_USER_MAPPER_CLASSNAME);
  }

  /**
   * Method used to retrieve the default path where the AuthzDB file are stored If no value is found
   * in the configuration medium, then the default one is used instead, that is the "configuration
   * directory" key="authzdb.path";
   */
  public String getAuthzDBPath() {

    return cr.getConfiguration().getString(AUTHZ_DB_PATH_KEY, cr.configurationDirectory());
  }

  /**
   * Method used to retrieve the default refresh rate of the AuthzDB files If no value is found in
   * the configuration medium, then the default one is used instead, that is the "5 sec"
   * key="authzdb.refreshrate";
   */
  public int getRefreshRateAuthzDBfilesInSeconds() {

    return cr.getConfiguration()
      .getInt(REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS_KEY, REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS);
  }

  /**
   * Method used to retrieve the PORT where RESTful services listen (like the Recall Table service)
   * If no value is found in the configuration medium, then the default one is used instead, that is
   * the "9998" key="tape.recalltable.service.port";
   */
  public int getRestServicesPort() {

    return cr.getConfiguration().getInt(REST_SERVICES_PORT_KEY, REST_SERVICES_PORT);
  }

  public int getRestServicesMaxThreads() {

    return cr.getConfiguration().getInt(REST_SERVICES_MAX_THREAD_KEY, REST_SERVICES_MAX_THREAD);
  }

  public int getRestServicesMaxQueueSize() {

    return cr.getConfiguration()
      .getInt(REST_SERVICES_MAX_QUEUE_SIZE_KEY, REST_SERVICES_MAX_QUEUE_SIZE);
  }

  /**
   * Method used to retrieve the key string used to pass RETRY-VALUE parameter to Recall Table
   * service key="tape.recalltable.service.param.retry-value";
   */
  public String getRetryValueKey() {

    return "retry-value";
  }

  /**
   * Method used to retrieve the key string used to pass RETRY-VALUE parameter to Recall Table
   * service key="tape.recalltable.service.param.status";
   */
  public String getStatusKey() {

    return "status";
  }

  /**
   * Method used to retrieve the key string used to pass RETRY-VALUE parameter to Recall Table
   * service key="tape.recalltable.service.param.takeover";
   */
  public String getTaskoverKey() {

    return "first";
  }

  public String getStoRMPropertiesVersion() {

    return cr.getConfiguration().getString(STORM_PROPERTIES_VERSION_KEY, "No version specified");
  }

  /**
   * @return
   */
  public boolean getSynchronousQuotaCheckEnabled() {

    return cr.getConfiguration()
      .getBoolean(SYNCHRONOUS_QUOTA_CHECK_ENABLED_KEY, SYNCHRONOUS_QUOTA_CHECK_ENABLED);
  }

  /**
   * 
   * @return the refresh period in seconds
   */
  public int getGPFSQuotaRefreshPeriod() {

    return cr.getConfiguration().getInt(GPFS_QUOTA_REFRESH_PERIOD_KEY, GPFS_QUOTA_REFRESH_PERIOD);
  }

  /**
   * @return
   */
  public Long getServerPoolStatusCheckTimeout() {

    return cr.getConfiguration()
      .getLong(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY, SERVER_POOL_STATUS_CHECK_TIMEOUT);
  }

  public boolean getSanityCheckEnabled() {

    return cr.getConfiguration().getBoolean(SANITY_CHECK_ENABLED_KEY, SANITY_CHECK_ENABLED);
  }

  public Boolean getXmlRpcTokenEnabled() {

    return cr.getConfiguration().getBoolean(XMLRPC_SECURITY_ENABLED_KEY, XMLRPC_SECURITY_ENABLED);
  }

  public String getXmlRpcToken() {

    return cr.getConfiguration().getString(XMLRPC_SECURITY_TOKEN_KEY);
  }

  public Boolean getPTGSkipACLSetup() {

    return cr.getConfiguration().getBoolean(PTG_SKIP_ACL_SETUP_KEY, PTG_SKIP_ACL_SETUP);
  }

  public Boolean getPTPSkipACLSetup() {

    return cr.getConfiguration().getBoolean(PTP_SKIP_ACL_SETUP_KEY, PTP_SKIP_ACL_SETUP);
  }

  @Override
  public String toString() {

    StringBuilder configurationStringBuilder = new StringBuilder();
    try {
      // This class methods
      Method[] methods = StormConfiguration.instance.getClass().getDeclaredMethods();

      // This class fields
      Field[] fields = StormConfiguration.instance.getClass().getDeclaredFields();
      HashMap<String, String> methodKeyMap = new HashMap<>();
      for (Field field : fields) {
        String fieldName = field.getName();
        if (fieldName.endsWith("KEY") && field.getType().equals(String.class)) {
          // from a field like GROUP_TAPE_WRITE_BUFFER_KEY =
          // "tape.buffer.group.write"
          // puts in the map the pair
          // <getgrouptapewritebuffer,tape.buffer.group.write>
          String mapKey = "get"
              + fieldName.substring(0, fieldName.lastIndexOf('_')).replace("_", "").toLowerCase();
          if (methodKeyMap.containsKey(mapKey)) {
            String value = methodKeyMap.get(mapKey);
            methodKeyMap.put(mapKey,
                value + " , " + (String) field.get(StormConfiguration.instance));
          } else {
            methodKeyMap.put(mapKey, (String) field.get(StormConfiguration.instance));
          }
        }
      }

      Object field = null;
      Object[] dummyArray = new Object[0];
      for (Method method : methods) {
        /*
         * with method.getModifiers() == 1 we check that the method is public (otherwise he can
         * request real parameters)
         */
        if (method.getName().substring(0, 3).equals("get")
            && (!method.getName().equals("getInstance")) && method.getModifiers() == 1) {
          field = method.invoke(StormConfiguration.instance, dummyArray);
          if (field.getClass().isArray()) {
            field = ArrayUtils.toString(field);
          }
          String value = methodKeyMap.get(method.getName().toLowerCase());
          if (value == null) {
            configurationStringBuilder.insert(0,
                "!! Unable to find method " + method.getName() + " in methode key map!");
          } else {
            configurationStringBuilder.append("Property " + value + " : ");
          }
          if (field.getClass().equals(String.class)) {
            field = '\'' + ((String) field) + '\'';
          }
          configurationStringBuilder.append(method.getName() + "() == " + field.toString() + "\n");
        }
      }
      return configurationStringBuilder.toString();
    } catch (Exception e) {
      if (e.getClass().isAssignableFrom(java.lang.reflect.InvocationTargetException.class)) {
        configurationStringBuilder.insert(0,
            "!!! Cannot do toString! Got an Exception: " + e.getCause() + "\n");
      } else {
        configurationStringBuilder.insert(0,
            "!!! Cannot do toString! Got an Exception: " + e + "\n");
      }
      return configurationStringBuilder.toString();
    }
  }

  public String getHTTPTURLPrefix() {
    return cr.getConfiguration().getString(HTTP_TURL_PREFIX, "/fileTransfer");
  }

  public long getInProgressPutRequestExpirationTime() {
    return cr.getConfiguration()
      .getLong(EXPIRED_INPROGRESS_PTP_TIME_KEY, EXPIRED_INPROGRESS_PTP_TIME);
  }

  public long getInProgressBolRequestExpirationTime() {
    return cr.getConfiguration()
      .getLong(EXPIRED_INPROGRESS_BOL_TIME_KEY, EXPIRED_INPROGRESS_BOL_TIME);
  }

  public int getNetworkAddressCacheTtl() {
    return cr.getConfiguration().getInt(NETWORKADDRESS_CACHE_TTL, 0);
  }

  public int getNetworkAddressCacheNegativeTtl() {
    return cr.getConfiguration().getInt(NETWORKADDRESS_CACHE_NEGATIVE_TTL, 0);
  }

  public boolean getDiskUsageServiceEnabled() {

    return cr.getConfiguration()
      .getBoolean(DISKUSAGE_SERVICE_ENABLED_KEY, DISKUSAGE_SERVICE_ENABLED);
  }

  public int getDiskUsageServiceInitialDelay() {

    return cr.getConfiguration().getInt(DISKUSAGE_SERVICE_INITIAL_DELAY_KEY, DEFAULT_INITIAL_DELAY);
  }

  public int getDiskUsageServiceTasksInterval() {

    // default: 604800 s => 1 week
    return cr.getConfiguration()
      .getInt(DISKUSAGE_SERVICE_TASKS_INTERVAL_KEY, DEFAULT_TASKS_INTERVAL);
  }

  public boolean getDiskUsageServiceTasksParallel() {

    return cr.getConfiguration()
      .getBoolean(DISKUSAGE_SERVICE_TASKS_PARALLEL_KEY, DEFAULT_TASKS_PARALLEL);
  }

  public boolean getPreferIPv6Addresses() {

    return cr.getConfiguration()
      .getBoolean(JAVA_NET_PREFERIPV6ADDRESSES_KEY, JAVA_NET_PREFERIPV6ADDRESSES);
  }
}
