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

import static it.grid.storm.config.ConfigurationDefaults.AUTOMATIC_DIRECTORY_CREATION;
import static it.grid.storm.config.ConfigurationDefaults.BOL_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOOK_KEEPING_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.CLEANING_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.CLEANING_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_PASSWORD;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MAX_WAIT_MILLIS;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MIN_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_ON_BORROW;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_WHILE_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_PORT;
import static it.grid.storm.config.ConfigurationDefaults.DB_PROPERTIES;
import static it.grid.storm.config.ConfigurationDefaults.DB_USERNAME;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_FILE_STORAGE_TYPE;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_OVERWRITE_MODE;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_TASKS_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.ENABLE_WRITE_PERM_ON_DIRECTORY;
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
import static it.grid.storm.config.ConfigurationDefaults.HEARTHBEAT_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HTTP_TURL_PREFIX;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_ALL_LEVEL_RECURSIVE;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_NUM_OF_LEVELS;
import static it.grid.storm.config.ConfigurationDefaults.LS_MAX_NUMBER_OF_ENTRY;
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
import static it.grid.storm.config.ConfigurationDefaults.PURGE_BATCH_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUEST_PURGER_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.REQUEST_PURGER_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_PORT;
import static it.grid.storm.config.ConfigurationDefaults.SANITY_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;
import static it.grid.storm.config.ConfigurationDefaults.SERVICE_SRM_PUBLIC_PORT;
import static it.grid.storm.config.ConfigurationDefaults.TRANSIT_INITIAL_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.TRANSIT_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SECURITY_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SECURITY_TOKEN;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SERVER_PORT;
import static java.lang.System.getProperty;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.model.Authority;

/**
 * Singleton holding all configuration values that any other object in the StoRM backend reads from
 * configuration files, databases, etc. Implements a 'get<something>' method for each value that
 * should be looked up this way. In fact, this is a "read-only" class. If no value is specified in
 * the configuration medium, a default one is used instead; some properties may hold several comma
 * separated values without any white spaces in-between; the name of the property in the
 * configuration medium, default values, as well as the option of holding multiple values, is
 * specified in each method comment.
 */

public class Configuration {

  private Logger log = NamespaceDirector.getLogger();

  private final ConfigReader cr;

  private static Configuration instance;

  /* Configuration file properties */

  /* SRM public info */
  private static final String SERVICE_SRM_PUBLIC_HOST_KEY = "srm_public_host";
  private static final String SERVICE_SRM_PUBLIC_PORT_KEY = "srm_public_port";

  /* SRM end-points */
  private static final String MANAGED_SRM_ENDPOINTS_KEY = "srm_endpoints";

  /* Database */
  private static final String DB_USERNAME_KEY = "db_username";
  private static final String DB_PASSWORD_KEY = "db_password";
  private static final String DB_HOSTNAME_KEY = "db_hostname";
  private static final String DB_PORT_KEY = "db_port";
  private static final String DB_PROPERTIES_KEY = "db_properties";
  /* Database connection pool */
  private static final String DB_POOL_SIZE_KEY = "db_pool_size";
  private static final String DB_POOL_MIN_IDLE_KEY = "db_pool_min_idle";
  private static final String DB_POOL_MAX_WAIT_MILLIS_KEY = "db_pool_max_wait_millis";
  private static final String DB_POOL_TEST_ON_BORROW_KEY = "db_pool_test_on_borrow";
  private static final String DB_POOL_TEST_WHILE_IDLE_KEY = "db_pool_test_while_idle";
  /* REST service */
  private static final String REST_SERVICES_PORT_KEY = "rest_services_port";
  private static final String REST_SERVICES_MAX_THREADS_KEY = "rest_services_max_threads";
  private static final String REST_SERVICES_MAX_QUEUE_SIZE_KEY = "rest_services_max_queue_size";
  /* Sanity check enabled */
  private static final String SANITY_CHECK_ENABLED_KEY = "sanity_check_enabled";
  /* Disk usage service */
  public static final String DISKUSAGE_SERVICE_ENABLED_KEY = "du_service_enabled";
  private static final String DISKUSAGE_SERVICE_INITIAL_DELAY_KEY = "du_service_initial_delay";
  private static final String DISKUSAGE_SERVICE_TASKS_INTERVAL_KEY = "du_service_tasks_interval";
  private static final String DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED_KEY =
      "du_service_parallel_tasks_enabled";
  /* XMLRPC */
  private static final String XMLRPC_MAX_THREADS_KEY = "xmlrpc_max_threads";
  private static final String XMLRPC_MAX_QUEUE_SIZE_KEY = "xmlrpc_max_queue_size";
  private static final String XMLRPC_SERVER_PORT_KEY = "xmlrpc_unsecure_server_port";
  private static final String XMLRPC_SECURITY_ENABLED_KEY = "xmlrpc_security_enabled";
  private static final String XMLRPC_SECURITY_TOKEN_KEY = "xmlrpc_security_token";

  private static final String AUTOMATIC_DIRECTORY_CREATION_KEY = "directory_automatic_creation";
  private static final String ENABLE_WRITE_PERM_ON_DIRECTORY_KEY = "directory_writeperm";

  private static final String PIN_LIFETIME_DEFAULT_KEY = "pinlifetime_default";
  private static final String PIN_LIFETIME_MAXIMUM_KEY = "pinlifetime_maximum";

  private static final String EXTRA_SLASHES_FOR_FILE_TURL_KEY = "extraslashes_file";
  private static final String EXTRA_SLASHES_FOR_RFIO_TURL_KEY = "extraslashes_rfio";
  private static final String EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY = "extraslashes_gsiftp";
  private static final String EXTRA_SLASHES_FOR_ROOT_TURL_KEY = "extraslashes_root";

  private static final String CLEANING_INITIAL_DELAY_KEY = "gc_pinnedfiles_cleaning_delay";
  private static final String CLEANING_TIME_INTERVAL_KEY = "gc_pinnedfiles_cleaning_interval";

  private static final String FILE_DEFAULT_SIZE_KEY = "filesize_default";
  private static final String FILE_LIFETIME_DEFAULT_KEY = "filelifetime_default";

  private static final String TRANSIT_INITIAL_DELAY_KEY = "transit_delay";
  private static final String TRANSIT_TIME_INTERVAL_KEY = "transit_interval";

  private static final String PICKING_INITIAL_DELAY_KEY = "asynch_picking_initial_delay";
  private static final String PICKING_TIME_INTERVAL_KEY = "asynch_picking_time_interval";
  private static final String PICKING_MAX_BATCH_SIZE_KEY = "asynch_picking_max_batch_size";

  private static final String LS_MAX_NUMBER_OF_ENTRY_KEY = "synch_ls_max_entries";
  private static final String LS_DEFAULT_ALL_LEVEL_RECURSIVE_KEY = "synch_ls_default_all_level_recursive";
  private static final String LS_DEFAULT_NUM_OF_LEVELS_KEY = "synch_ls_default_num_levels";
  private static final String LS_OFFSET_KEY = "synch_ls_default_offset";

  private static final String PTG_SKIP_ACL_SETUP_KEY = "ptg_skip_acl_setup";
  private static final String HTTP_TURL_PREFIX_KEY = "http_turl_prefix";

  private static final String PTP_CORE_POOL_SIZE_KEY =
      "scheduler_chunksched_ptp_worker_core_pool_size";
  private static final String PTP_MAX_POOL_SIZE_KEY =
      "scheduler_chunksched_ptp_worker_max_pool_size";
  private static final String PTP_QUEUE_SIZE_KEY = "scheduler_chunksched_ptp_queueSize";
  private static final String PTG_CORE_POOL_SIZE_KEY =
      "scheduler_chunksched_ptg_worker_core_pool_size";
  private static final String PTG_MAX_POOL_SIZE_KEY =
      "scheduler_chunksched_ptg_worker_max_pool_size";
  private static final String PTG_QUEUE_SIZE_KEY = "scheduler_chunksched_ptg_queueSize";
  private static final String BOL_CORE_POOL_SIZE_KEY =
      "scheduler_chunksched_bol_worker_core_pool_size";
  private static final String BOL_MAX_POOL_SIZE_KEY =
      "scheduler_chunksched_bol_worker_max_pool_size";
  private static final String BOL_QUEUE_SIZE_KEY = "scheduler_chunksched_bol_queueSize";
  private static final String CORE_POOL_SIZE_KEY = "scheduler_crusher_worker_core_pool_size";
  private static final String MAX_POOL_SIZE_KEY = "scheduler_crusher_worker_max_pool_size";
  private static final String QUEUE_SIZE_KEY = "scheduler_crusher_queue_size";

  private static final String DEFAULT_OVERWRITE_MODE_KEY = "default_overwrite";
  private static final String DEFAULT_FILE_STORAGE_TYPE_KEY = "default_storagetype";

  private static final String EXPIRED_REQUEST_PURGING_KEY = "purging";
  private static final String REQUEST_PURGER_PERIOD_KEY = "purge_interval";
  private static final String PURGE_BATCH_SIZE_KEY = "purge_size";
  private static final String EXPIRED_REQUEST_TIME_KEY = "expired_request_time";
  private static final String EXPIRED_INPROGRESS_PTP_TIME_KEY = "expired_inprogress_time";
  private static final String REQUEST_PURGER_DELAY_KEY = "purge_delay";

  private static final String PING_VALUES_PROPERTIES_FILENAME_KEY = "ping_properties_filename";

  private static final String HEARTHBEAT_PERIOD_KEY = "health_electrocardiogram_period";
  private static final String PERFORMANCE_GLANCE_TIME_INTERVAL_KEY =
      "health_performance_glance_time_interval";
  private static final String PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY =
      "health_performance_logbook_time_interval";
  private static final String PERFORMANCE_MEASURING_KEY = "health_performance_measuring_enabled";
  private static final String BOOK_KEEPING_ENABLED_KEY = "health_bookkeeping_enabled";

  private static final String MAX_LOOP_KEY = "abort_maxloop";

  private static final String GPFS_QUOTA_REFRESH_PERIOD_KEY = "info_quota_refresh_period";

  private static final String SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY =
      "server_pool_status_check_timeout";

  public static void init(String filePath, int refreshRate) {
    try {
      instance = new Configuration(filePath, refreshRate);
    } catch (ConfigurationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private Configuration(String filePath, int refreshRate) throws ConfigurationException {

    cr = new ConfigReader(filePath, refreshRate);
  }

  /**
   * Returns the sole instance of the Configuration class.
   */
  public static Configuration getInstance() {

    return Configuration.instance;
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
   * @throws UnknownHostException
   */
  public List<Authority> getManagedSrmEndpoints() {

    String defaultValue = String.format("%s:%d", getSrmServiceHostname(), getSrmServicePort());
    return cr.getConfiguration()
      .getList(MANAGED_SRM_ENDPOINTS_KEY, Lists.newArrayList(defaultValue))
      .stream()
      .map(o -> Authority.fromString(String.valueOf(o)))
      .collect(Collectors.toList());
  }

  /**
   * The published host of SRM service. It's used also to initialize a SURL starting from the SFN.
   */
  public String getSrmServiceHostname() {

    try {
      return cr.getConfiguration()
        .getString(SERVICE_SRM_PUBLIC_HOST_KEY, InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      e.printStackTrace();
      log.error("Unable to get local FQDN hostname: please set {} into your storm.properties file",
          SERVICE_SRM_PUBLIC_HOST_KEY);
      System.exit(1);
      return null;
    }
  }

  /**
   * The published port of SRM service. It's used also to initialize a SURL starting from the SFN.
   */
  public int getSrmServicePort() {

    return cr.getConfiguration().getInt(SERVICE_SRM_PUBLIC_PORT_KEY, SERVICE_SRM_PUBLIC_PORT);
  }

  /**
   * Get database host
   */
  public String getDbHostname() {

    try {
      return cr.getConfiguration()
        .getString(DB_HOSTNAME_KEY, InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      e.printStackTrace();
      log.error("Unable to get local FQDN hostname: please set {} into your storm.properties file",
          DB_HOSTNAME_KEY);
      System.exit(1);
      return null;
    }
  }

  /**
   * Get database URL's sub-name
   */
  public int getDbPort() {

    return cr.getConfiguration().getInt(DB_PORT_KEY, DB_PORT);
  }

  /**
   * Get database username.
   */
  public String getDbUsername() {

    return cr.getConfiguration().getString(DB_USERNAME_KEY, DB_USERNAME);
  }

  /**
   * Get database password.
   */
  public String getDbPassword() {

    return cr.getConfiguration().getString(DB_PASSWORD_KEY, DB_PASSWORD);
  }

  /**
   * Get database connection properties
   */
  public String getDbProperties() {

    return cr.getConfiguration().getString(DB_PROPERTIES_KEY, DB_PROPERTIES);
  }

  /**
   * Sets the maximum total number of idle and borrows connections that can be active at the same
   * time. Use a negative value for no limit.
   */
  public int getDbPoolSize() {

    return cr.getConfiguration().getInt(DB_POOL_SIZE_KEY, DB_POOL_SIZE);
  }

  /**
   * Sets the minimum number of idle connections in the pool.
   */
  public int getDbPoolMinIdle() {

    return cr.getConfiguration().getInt(DB_POOL_MIN_IDLE_KEY, DB_POOL_MIN_IDLE);
  }

  /**
   * Sets the MaxWaitMillis property. Use -1 to make the pool wait indefinitely.
   */
  public int getDbPoolMaxWaitMillis() {

    return cr.getConfiguration().getInt(DB_POOL_MAX_WAIT_MILLIS_KEY, DB_POOL_MAX_WAIT_MILLIS);
  }

  /**
   * This property determines whether or not the pool will validate objects before they are borrowed
   * from the pool.
   */
  public boolean isDbPoolTestOnBorrow() {

    return cr.getConfiguration().getBoolean(DB_POOL_TEST_ON_BORROW_KEY, DB_POOL_TEST_ON_BORROW);
  }

  /**
   * This property determines whether or not the idle object evictor will validate connections.
   */
  public boolean isDbPoolTestWhileIdle() {

    return cr.getConfiguration().getBoolean(DB_POOL_TEST_WHILE_IDLE_KEY, DB_POOL_TEST_WHILE_IDLE);
  }

  /**
   * Method used to retrieve the PORT where RESTful services listen (like the Recall Table service)
   */
  public int getRestServicesPort() {

    return cr.getConfiguration().getInt(REST_SERVICES_PORT_KEY, REST_SERVICES_PORT);
  }

  public int getRestServicesMaxThreads() {

    int value =
        cr.getConfiguration().getInt(REST_SERVICES_MAX_THREADS_KEY, REST_SERVICES_MAX_THREADS);
    return value < 0 ? REST_SERVICES_MAX_THREADS : value;
  }

  public int getRestServicesMaxQueueSize() {

    int value = cr.getConfiguration()
      .getInt(REST_SERVICES_MAX_QUEUE_SIZE_KEY, REST_SERVICES_MAX_QUEUE_SIZE);
    return value < 0 ? REST_SERVICES_MAX_QUEUE_SIZE : value;
  }

  public boolean getSanityCheckEnabled() {

    return cr.getConfiguration().getBoolean(SANITY_CHECK_ENABLED_KEY, SANITY_CHECK_ENABLED);
  }

  /**
   * Get max number of XMLRPC threads into for the XMLRPC server.
   */
  public int getXmlrpcMaxThreads() {

    int value = cr.getConfiguration().getInt(XMLRPC_MAX_THREADS_KEY, XMLRPC_MAX_THREADS);
    return value < 0 ? XMLRPC_MAX_THREADS : value;
  }

  public int getXmlrpcMaxQueueSize() {

    int value = cr.getConfiguration().getInt(XMLRPC_MAX_QUEUE_SIZE_KEY, XMLRPC_MAX_QUEUE_SIZE);
    return value < 0 ? XMLRPC_MAX_QUEUE_SIZE : value;
  }

  public int getXmlRpcServerPort() {

    return cr.getConfiguration().getInt(XMLRPC_SERVER_PORT_KEY, XMLRPC_SERVER_PORT);
  }

  public Boolean getXmlRpcSecurityEnabled() {

    return cr.getConfiguration().getBoolean(XMLRPC_SECURITY_ENABLED_KEY, XMLRPC_SECURITY_ENABLED);
  }

  public String getXmlRpcToken() {

    return cr.getConfiguration().getString(XMLRPC_SECURITY_TOKEN_KEY, XMLRPC_SECURITY_TOKEN);
  }

  public boolean getDiskUsageServiceEnabled() {

    return cr.getConfiguration()
      .getBoolean(DISKUSAGE_SERVICE_ENABLED_KEY, DISKUSAGE_SERVICE_ENABLED);
  }

  public int getDiskUsageServiceInitialDelay() {

    return cr.getConfiguration()
      .getInt(DISKUSAGE_SERVICE_INITIAL_DELAY_KEY, DISKUSAGE_SERVICE_INITIAL_DELAY);
  }

  public int getDiskUsageServiceTasksInterval() {

    // default: 604800 s => 1 week
    return cr.getConfiguration()
      .getInt(DISKUSAGE_SERVICE_TASKS_INTERVAL_KEY, DISKUSAGE_SERVICE_TASKS_INTERVAL);
  }

  public boolean getDiskUsageServiceTasksParallel() {

    return cr.getConfiguration()
      .getBoolean(DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED_KEY,
          DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED);
  }

  public String getNamespaceConfigFilename() {

    return "namespace.xml";
  }

  /**
   * Used by PinnedFilesCatalog to get the initial delay in _seconds_ before starting the cleaning
   * thread.
   */
  public long getCleaningInitialDelay() {

    return cr.getConfiguration().getLong(CLEANING_INITIAL_DELAY_KEY, CLEANING_INITIAL_DELAY);
  }

  /**
   * Used by PinnedFilesCatalog to get the cleaning time interval, in _seconds_.
   */
  public long getCleaningTimeInterval() {

    return cr.getConfiguration().getLong(CLEANING_TIME_INTERVAL_KEY, CLEANING_TIME_INTERVAL);
  }

  public long getFileDefaultSize() {

    return cr.getConfiguration().getLong(FILE_DEFAULT_SIZE_KEY, FILE_DEFAULT_SIZE);
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the default fileLifetime to use when a volatile
   * entry is being added/updated, but the user specified a non positive value. Measured in
   * _seconds_.
   */
  public long getFileLifetimeDefault() {

    return cr.getConfiguration().getLong(FILE_LIFETIME_DEFAULT_KEY, FILE_LIFETIME_DEFAULT);
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the minimum pinLifetime allowed, when a jit is
   * being added/updated, but the user specified a lower one. This method is also used by the
   * PinLifetimeConverter to translate a NULL/0/negative value to a default one. Measured in
   * _seconds_.
   */
  public long getPinLifetimeDefault() {

    return cr.getConfiguration().getLong(PIN_LIFETIME_DEFAULT_KEY, PIN_LIFETIME_DEFAULT);
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the maximum pinLifetime allowed, when a jit is
   * being added/updated, but the user specified a higher one. Measured in _seconds_.
   */
  public long getPinLifetimeMaximum() {

    return cr.getConfiguration().getLong(PIN_LIFETIME_MAXIMUM_KEY, PIN_LIFETIME_MAXIMUM);
  }

  /**
   * Method used by PtPChunkCatalog to get the initial delay in _seconds_ before starting the
   * transiting thread.
   */
  public long getTransitInitialDelay() {

    return cr.getConfiguration().getLong(TRANSIT_INITIAL_DELAY_KEY, TRANSIT_INITIAL_DELAY);
  }

  /**
   * Method used by PtPChunkCatalog to get the transiting time interval, in _seconds_.
   */
  public long getTransitTimeInterval() {

    return cr.getConfiguration().getLong(TRANSIT_TIME_INTERVAL_KEY, TRANSIT_TIME_INTERVAL);
  }

  /**
   * Method used by AdvancedPicker to get the initial delay before starting to pick data from the
   * DB, in _seconds_.
   */
  public long getPickingInitialDelay() {

    return cr.getConfiguration().getLong(PICKING_INITIAL_DELAY_KEY, PICKING_INITIAL_DELAY);
  }

  /**
   * Method used by AdvancedPicker to get the time interval of successive pickings, in _seconds_.
   */
  public long getPickingTimeInterval() {

    return cr.getConfiguration().getLong(PICKING_TIME_INTERVAL_KEY, PICKING_TIME_INTERVAL);
  }

  /**
   * Method used by RequestSummaryDAO to establish the maximum number of requests to retrieve with
   * each polling.
   */
  public int getPickingMaxBatchSize() {

    return cr.getConfiguration().getInt(PICKING_MAX_BATCH_SIZE_KEY, PICKING_MAX_BATCH_SIZE);
  }

  /**
   * Method used by the Synch Component to set the maximum number of entries to return for the srmLs
   * functionality.
   */
  public int getLsMaxNumberOfEntry() {

    return cr.getConfiguration().getInt(LS_MAX_NUMBER_OF_ENTRY_KEY, LS_MAX_NUMBER_OF_ENTRY);
  }

  /**
   * Default value for the parameter "allLevelRecursive" of the LS request.
   */
  public boolean getLsAllLevelRecursive() {

    return cr.getConfiguration().getBoolean(LS_DEFAULT_ALL_LEVEL_RECURSIVE_KEY, LS_DEFAULT_ALL_LEVEL_RECURSIVE);
  }

  /**
   * Default value for the parameter "numOfLevels" of the LS request.
   */
  public int getLsNumOfLevels() {

    return cr.getConfiguration().getInt(LS_DEFAULT_NUM_OF_LEVELS_KEY, LS_DEFAULT_NUM_OF_LEVELS);
  }

  /**
   * Default value for the parameter "offset" of the LS request.
   */
  public int getLsOffset() {

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
   * threads to keep in the pool, even if they are idle.
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
   * of threads to allow in the pool.
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
   * of threads to allow in the pool.
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
   * if they are idle.
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
   * pool.
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
   * the Runnable tasks submitted by the execute method.
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
   * are idle.
   */
  public int getCorePoolSize() {

    return cr.getConfiguration().getInt(CORE_POOL_SIZE_KEY, CORE_POOL_SIZE);
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
   * submitted by the execute method.
   */
  public int getQueueSize() {

    return cr.getConfiguration().getInt(QUEUE_SIZE_KEY, QUEUE_SIZE);
  }

  /**
   * Method used by PtPChunk to find out if missing local directories should be created
   * automatically or not. SRM 2.2 specification forbids automatic creation.
   */
  public boolean getAutomaticDirectoryCreation() {

    return cr.getConfiguration()
      .getBoolean(AUTOMATIC_DIRECTORY_CREATION_KEY, AUTOMATIC_DIRECTORY_CREATION);
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

  /**
   * Method used by TOverwriteModeConverter to establish the default OverwriteMode to use.
   */
  public String getDefaultOverwriteMode() {

    return cr.getConfiguration().getString(DEFAULT_OVERWRITE_MODE_KEY, DEFAULT_OVERWRITE_MODE);
  }

  /**
   * Method used by FileStorageTypeConverter to establish the default TFileStorageType to use.
   */
  public String getDefaultFileStorageType() {

    return cr.getConfiguration()
      .getString(DEFAULT_FILE_STORAGE_TYPE_KEY, DEFAULT_FILE_STORAGE_TYPE);
  }

  /**
   * Method used by RequestSummaryDAO to establish the batch size for removing expired requests.
   */
  public int getPurgeBatchSize() {

    return cr.getConfiguration().getInt(PURGE_BATCH_SIZE_KEY, PURGE_BATCH_SIZE);
  }

  /**
   * Method used by RequestSummaryDAO to establish the time that must be elapsed for considering a
   * request expired. The time measure specified in the configuration medium is in _days_. The value
   * returned by this method, is expressed in _seconds_.
   */
  public long getExpiredRequestTime() {

    return cr.getConfiguration().getLong(EXPIRED_REQUEST_TIME_KEY, EXPIRED_REQUEST_TIME);
  }

  /**
   * Method used by RequestSummaryCatalog to establish the initial delay before starting the purging
   * thread, in _seconds_.
   */
  public int getRequestPurgerDelay() {

    return cr.getConfiguration().getInt(REQUEST_PURGER_DELAY_KEY, REQUEST_PURGER_DELAY);
  }

  /**
   * Method used by RequestSummaryCatalog to establish the time interval in _seconds_ between
   * successive purging checks.
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

  public long getInProgressPtpExpirationTime() {

    return cr.getConfiguration()
      .getLong(EXPIRED_INPROGRESS_PTP_TIME_KEY, EXPIRED_INPROGRESS_PTP_TIME);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForFileTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_FILE_TURL_KEY, EXTRA_SLASHES_FOR_FILE_TURL);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForRFIOTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_RFIO_TURL_KEY, EXTRA_SLASHES_FOR_RFIO_TURL);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForGsiFTPTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY, EXTRA_SLASHES_FOR_GSIFTP_TURL);
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the "authority" part of a
   * TURL.
   */
  public String getExtraSlashesForROOTTURL() {

    return cr.getConfiguration()
      .getString(EXTRA_SLASHES_FOR_ROOT_TURL_KEY, EXTRA_SLASHES_FOR_ROOT_TURL);
  }

  /**
   * Method used by Ping Executor to retrieve the Properties File Name where the properties
   * <key,value> are stored.
   */
  public String getPingValuesPropertiesFilename() {

    return cr.getConfiguration()
      .getString(PING_VALUES_PROPERTIES_FILENAME_KEY, PING_VALUES_PROPERTIES_FILENAME);
  }


  public int getHearthbeatPeriod() {

    return cr.getConfiguration().getInt(HEARTHBEAT_PERIOD_KEY, HEARTHBEAT_PERIOD);
  }

  public int getPerformanceGlanceTimeInterval() {

    return cr.getConfiguration()
      .getInt(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY, PERFORMANCE_GLANCE_TIME_INTERVAL);
  }

  public int getPerformanceLogbookTimeInterval() {

    return cr.getConfiguration()
      .getInt(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY, PERFORMANCE_LOGBOOK_TIME_INTERVAL);
  }

  public boolean getPerformanceMeasuring() {

    return cr.getConfiguration().getBoolean(PERFORMANCE_MEASURING_KEY, PERFORMANCE_MEASURING);
  }

  public boolean getBookKeepingEnabled() {

    return cr.getConfiguration().getBoolean(BOOK_KEEPING_ENABLED_KEY, BOOK_KEEPING_ENABLED);
  }

  public int getMaxLoop() {

    return cr.getConfiguration().getInt(MAX_LOOP_KEY, MAX_LOOP);
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

    return cr.getConfiguration().getInt(GPFS_QUOTA_REFRESH_PERIOD_KEY, GPFS_QUOTA_REFRESH_PERIOD);
  }

  public long getServerPoolStatusCheckTimeout() {

    return cr.getConfiguration()
      .getLong(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY, SERVER_POOL_STATUS_CHECK_TIMEOUT);
  }

  public boolean getPTGSkipACLSetup() {

    return cr.getConfiguration().getBoolean(PTG_SKIP_ACL_SETUP_KEY, PTG_SKIP_ACL_SETUP);
  }

  public String getHTTPTURLPrefix() {

    return cr.getConfiguration().getString(HTTP_TURL_PREFIX_KEY, HTTP_TURL_PREFIX);
  }

  public int getNetworkAddressCacheTtl() {
    return 0;
  }

  public int getNetworkAddressCacheNegativeTtl() {
    return 0;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Configuration={");
    Iterator<String> keys = cr.getConfiguration().getKeys();
    while (keys.hasNext()) {
      String name = keys.next();
      sb.append(name + "=" + cr.getConfiguration().getString(name));
    }
    sb.append("}");
    return sb.toString();
  }

}
