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

import it.grid.storm.https.impl.DefaultHTTPPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton holding all configuration values that any other object in the StoRM
 * backend reads from configuration files, databases, etc. Implements a
 * 'get<something>' method for each value that should be looked up this way. In
 * fact, this is a "read-only" class. If no value is specified in the
 * configuration medium, a default one is used instead; some properties may hold
 * several comma separated values without any white spaces in-between; the name
 * of the property in the configuration medium, default values, as well as the
 * option of holding multiple values, is specified in each method comment.
 */

public class Configuration {

  private static final Logger log = LoggerFactory
    .getLogger(Configuration.class);

  private ConfigReader cr = new ConfigReader(); // set an empty ConfigReader
  // as default
  static Configuration instance = new Configuration(); // only
  // instance of this configuration class

  private static final String MANAGED_SURLS_KEY = "storm.service.SURL.endpoint";
  private static final String MANAGED_SURL_DEFAULT_PORTS_KEY = "storm.service.SURL.default-ports";
  private static final String SERVICE_HOSTNAME_KEY = "storm.service.FE-public.hostname";
  private static final String SERVICE_PORT_KEY = "storm.service.port";
  private static final String LIST_OF_MACHINE_NAMES_KEY = "storm.service.FE-list.hostnames";
  private static final String LIST_OF_MACHINE_IPS_KEY = "storm.service.FE-list.IPs";
  private static final String DB_DRIVER_KEY = "storm.service.request-db.dbms-vendor";
  private static final String DB_URL_1KEY = "storm.service.request-db.protocol";
  private static final String DB_URL_2KEY = "storm.service.request-db.host";
  private static final String DB_URL_3KEY = "storm.service.request-db.db-name";
  private static final String DB_USER_NAME_KEY = "storm.service.request-db.username";
  private static final String DB_PASSWORD_KEY = "storm.service.request-db.passwd";
  private static final String DB_RECONNECT_PERIOD_KEY = "asynch.db.ReconnectPeriod";
  private static final String DB_RECONNECT_DELAY_KEY = "asynch.db.DelayPeriod";
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
  private static final String SRMCLIENT_PUT_TOTAL_RETRY_TIME_KEY = "asynch.srmclient.retrytime";
  private static final String SRMCLIENT_PUT_TIME_OUT_KEY = "asynch.srmclient.timeout";
  private static final String SRMCLIENT_PUT_SLEEP_TIME_KEY = "asynch.srmclient.sleeptime";
  private static final String SRMCLIENT_PUT_DONE_SLEEP_TIME_KEY = "asynch.srmclient.putdone.sleeptime";
  private static final String SRMCLIENT_PUT_DONE_TIME_OUT_KEY = "asynch.srmclient.putdone.timeout";
  private static final String MAX_XMLRPC_THREAD_KEY = "synchcall.xmlrpc.maxthread";
  private static final String LIST_OF_DEFAULT_SPACE_TOKEN_KEY = "storm.service.defaultSpaceTokens";
  private static final String GRIDFTP_TRANSFER_CLIENT_KEY = "asynch.gridftpclient";
  private static final String SRMCLIENT_KEY = "asynch.srmclient";
  private static final String COMMAND_SERVER_BINDING_PORT_KEY = "storm.commandserver.port";
  private static final String SERIAL_SCHEDULER_KEY = "scheduler.serial";
  private static final String BE_PERSISTENCE_DB_VENDOR_KEY = "persistence.internal-db.dbms-vendor";
  private static final String BE_PERSISTENCE_DBMS_URL_1KEY = "persistence.internal-db.host";
  private static final String BE_PERSISTENCE_DBMS_URL_2KEY = "" + DB_URL_2KEY;
  private static final String BE_PERSISTENCE_DB_NAME_KEY = "persistence.internal-db.db-name";
  private static final String BE_PERSISTENCEDB_USER_NAME_1KEY = "persistence.internal-db.username";
  private static final String BE_PERSISTENCEDB_USER_NAME_2KEY = ""
    + DB_USER_NAME_KEY;
  private static final String BE_PERSISTENCE_DB_PASSWORD_1KEY = "persistence.internal-db.passwd";
  private static final String BE_PERSISTENCE_DB_PASSWORD_2KEY = ""
    + DB_PASSWORD_KEY;
  private static final String BE_PERSISTENCE_POOL_DB_KEY = "persistence.internal-db.connection-pool";
  private static final String BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY = "persistence.internal-db.connection-pool.maxActive";
  private static final String BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY = "persistence.internal-db.connection-pool.maxWait";
  private static final String XMLRPC_SERVER_PORT_KEY = "synchcall.xmlrpc.unsecureServerPort";
  private static final String LS_MAX_NUMBER_OF_ENTRY_KEY = "synchcall.directoryManager.maxLsEntry";
  private static final String LS_ALL_LEVEL_RECURSIVE_KEY = "synchcall.directoryManager.default.AllLevelRecursive";
  private static final String LS_NUM_OF_LEVELS_KEY = "synchcall.directoryManager.default.Levels";
  private static final String LS_OFFSET_KEY = "synchcall.directoryManager.default.Offset";
  private static final String PTP_CORE_POOL_SIZE_KEY = "scheduler.chunksched.ptp.workerCorePoolSize";
  private static final String PTP_MAX_POOL_SIZE_KEY = "scheduler.chunksched.ptp.workerMaxPoolSize";
  private static final String PTP_QUEUE_SIZE_KEY = "scheduler.chunksched.ptp.queueSize";
  private static final String PTG_CORE_POOL_SIZE_KEY = "scheduler.chunksched.ptg.workerCorePoolSize";
  private static final String PTG_MAX_POOL_SIZE_KEY = "scheduler.chunksched.ptg.workerMaxPoolSize";
  private static final String PTG_QUEUE_SIZE_KEY = "scheduler.chunksched.ptg.queueSize";
  private static final String COPY_CORE_POOL_SIZE_KEY = "scheduler.chunksched.copy.workerCorePoolSize";
  private static final String COPY_MAX_POOL_SIZE_KEY = "scheduler.chunksched.copy.workerMaxPoolSize";
  private static final String COPY_QUEUE_SIZE_KEY = "scheduler.chunksched.copy.queueSize";
  private static final String BOL_CORE_POOL_SIZE_KEY = "scheduler.chunksched.bol.workerCorePoolSize";
  private static final String BOL_MAX_POOL_SIZE_KEY = "scheduler.chunksched.bol.workerMaxPoolSize";
  private static final String BOL_QUEUE_SIZE_KEY = "scheduler.chunksched.bol.queueSize";
  private static final String CORE_POOL_SIZE_KEY = "scheduler.crusher.workerCorePoolSize";
  private static final String MAX_POOL_SIZE_KEY = "scheduler.crusher.workerMaxPoolSize";
  private static final String QUEUE_SIZE_KEY = "scheduler.crusher.queueSize";
  private static final String NAMESPACE_CONFIG_FILENAME_KEY = "namespace.filename";
  private static final String NAMESPACE_SCHEMA_FILENAME_KEY = "namespace.schema.filename";
  private static final String NAMESPACE_CONFIG_REFRESH_RATE_IN_SECONDS_KEY = "namespace.refreshrate";
  private static final String NAMESPACE_AUTOMATIC_RELOADING_KEY = "namespace.automatic-config-reload";
  private static final String GRIDFTP_TIME_OUT_KEY = "asynch.srmcopy.gridftp.timeout";
  private static final String SRM22CLIENT_PIN_LIFE_TIME_KEY = "SRM22Client.PinLifeTime";
  private static final String PROXY_HOME_KEY = "proxy.home";
  private static final String AUTOMATIC_DIRECTORY_CREATION_KEY = "directory.automatic-creation";
  private static final String DEFAULT_OVERWRITE_MODE_KEY = "default.overwrite";
  private static final String DEFAULT_FILE_STORAGE_TYPE_KEY = "default.storagetype";
  private static final String PURGE_BATCH_SIZE_KEY = "purge.size";
  private static final String EXPIRED_REQUEST_TIME_KEY = "expired.request.time";
  private static final String REQUEST_PURGER_DELAY_KEY = "purge.delay";
  private static final String REQUEST_PURGER_PERIOD_KEY = "purge.interval";
  private static final String EXPIRED_REQUEST_PURGING_KEY = "purging";
  private static final String EXTRA_SLASHES_FOR_FILE_TURL_KEY = "extraslashes.file";
  private static final String EXTRA_SLASHES_FOR_RFIO_TURL_KEY = "extraslashes.rfio";
  private static final String EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY = "extraslashes.gsiftp";
  private static final String EXTRA_SLASHES_FOR_ROOT_TURL_KEY = "extraslashes.root";
  private static final String PING_VALUES_PROPERTIES_FILENAME_KEY = "ping-properties.filename";
  private static final String HEARTHBEAT_PERIOD_KEY = "health.electrocardiogram.period";
  private static final String PERFORMANCE_GLANCE_TIME_INTERVAL_KEY = "health.performance.glance.timeInterval";
  private static final String PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY = "health.performance.logbook.timeInterval";
  private static final String PERFORMANCE_MEASURING_KEY = "health.performance.mesauring.enabled";
  private static final String BOOK_KEEPING_ENABLED_KEY = "health.bookkeeping.enabled";
  private static final String ENABLE_WRITE_PERM_ON_DIRECTORY_KEY = "directory.writeperm";
  private static final String MAX_LOOP_KEY = "abort.maxloop";
  private static final String GRID_USER_MAPPER_CLASSNAME_KEY = "griduser.mapper.classname";
  private static final String AUTHZ_DB_PATH_KEY = "authzdb.path";
  private static final String REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS_KEY = "authzdb.refreshrate";
  private static final String RECALL_TABLE_TESTING_MODE_KEY = "tape.recalltable.service.test-mode";
  private static final String REST_SERVICES_PORT_KEY = "storm.rest.services.port";
  private static final String RETRY_VALUE_KEY_KEY = "tape.recalltable.service.param.retry-value";
  private static final String STATUS_KEY_KEY = "tape.recalltable.service.param.status";
  private static final String TASKOVER_KEY_KEY = "tape.recalltable.service.param.takeover";
  private static final String GRIDHTTPS_ENABLED_KEY = "gridhttps.enabled";
  private static final String STORM_PROPERTIES_VERSION_KEY = "storm.properties.version";
  private static final String TAPE_SUPPORT_ENABLED_KEY = "tape.support.enabled";
  private static final String SYNCHRONOUS_QUOTA_CHECK_ENABLED_KEY = "info.quota-check.enabled";
  private static final String GPFS_QUOTA_REFRESH_PERIOD_KEY = "info.quota.refresh.period";
  private static final String FAST_BOOTSTRAP_ENABLED_KEY = "bootstrap.fast.enabled";
  private static final String SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY = "server-pool.status-check.timeout";
  private static final String SANITY_CHECK_ENABLED_KEY = "sanity-check.enabled";
  private static final String XMLRPC_SECURITY_ENABLED_KEY = "synchcall.xmlrpc.security.enabled";
  private static final String XMLRPC_SECURITY_TOKEN_KEY = "synchcall.xmlrpc.security.token";
  private static final String PTG_SKIP_ACL_SETUP = "ptg.skip-acl-setup";
  private static final String HTTP_TURL_PREFIX = "http.turl_prefix";

  private Configuration() {

  }

  /**
   * Returns the sole instance of the Configuration class.
   */
  public static Configuration getInstance() {

    return Configuration.instance;
  }

  /**
   * Method used to set the config reader: if a null is supplied then a default
   * empty ConfigReader is used instead.
   */
  public void setConfigReader(ConfigReader cr) {

    if (cr != null) {
      this.cr = cr;
    }
  }

  /**
   * Method that returns the directory holding the configuration file. The
   * methods that make use of it are uncertain... must be found soon!!! Beware
   * that the configuration directory is implicit in the complete pathname to
   * the configuration file supplied in the command line when starting StoRM BE.
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

    String storm_home = System.getProperty("user.dir");
    String configDir = storm_home + File.separator + "etc";
    return configDir;
  }

  /**
   * MANDATORY CONFIGURATION PARAMETER! Define the SURL endpoints.
   * 
   * @return String[]
   */
  public String[] getManagedSURLs() {

    String[] defaultValue = { "UNDEFINED_SERVICE_ENDPOINT" };
    if (!cr.getConfiguration().containsKey(MANAGED_SURLS_KEY)) {
      return defaultValue;
    } else {
      // load from external source
      return cr.getConfiguration().getStringArray(MANAGED_SURLS_KEY);
    }
  }

  /**
   * @return
   */
  public Integer[] getManagedSurlDefaultPorts() {

    Integer[] portsArray;
    if (!cr.getConfiguration().containsKey(MANAGED_SURL_DEFAULT_PORTS_KEY)) {
      portsArray = new Integer[] { 8444 };
    } else {
      // load from external source
      String[] portString = cr.getConfiguration().getStringArray(
        MANAGED_SURL_DEFAULT_PORTS_KEY);
      ArrayList<Integer> ports = new ArrayList<Integer>();
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

    String defaultValue = "UNDEFINED_STORM_HOSTNAME";
    if (!cr.getConfiguration().containsKey(SERVICE_HOSTNAME_KEY)) {
      return defaultValue;
    } else {
      // load from external source
      return cr.getConfiguration().getString(SERVICE_HOSTNAME_KEY);
    }
  }

  /**
   * Method used by SFN to establish the FE binding port. If no value is found
   * in the configuration medium, then the default one is used instead.
   * key="storm.service.port"; default value="8444"
   */
  public int getServicePort() {

    int defaultValue = 8444;
    if (!cr.getConfiguration().containsKey(SERVICE_PORT_KEY)) {
      return defaultValue;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(SERVICE_PORT_KEY);
    }
  }

  /**
   * Method used to get a List of Strings of the names of the machine hosting
   * the FE for _this_ StoRM instance! Used in srmCopy to understand if the
   * fromSURL/toSURL refer to the server itself or to some other foreign server!
   * The List contains Strings in _lower_case_!!! If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="storm.machinenames"; default value={"testbed006.cnaf.infn.it"};
   */
  public List<String> getListOfMachineNames() {

    if (cr.getConfiguration().containsKey(LIST_OF_MACHINE_NAMES_KEY)) {
      String[] names = cr.getConfiguration().getStringArray(
        LIST_OF_MACHINE_NAMES_KEY);

      for (int i = 0; i < names.length; i++) {
        names[i] = names[i].trim().toLowerCase();
      }
      return Arrays.asList(names);
    } else {
      return Arrays.asList(new String[] { "localhost" });
    }
  }

  /**
   * Method used to get a List of Strings of the IPs of the machine hosting the
   * FE for _this_ StoRM instance! Used in the xmlrcp server configuration, to
   * allow request coming from the specified IP. (Into the xmlrpc server the
   * filter is done by IP, not hostname.) This paramter is mandatory when a
   * distribuited FE-BE installation of StoRM is used togheter with a dynamic
   * DNS on the FE hostname. In that case the properties storm.machinenames is
   * not enough meaningfull. If no value is found in the configuration medium,
   * then the default value is returned instead. key="storm.machineIPs"; default
   * value={"127.0.0.1"};
   */
  public List<String> getListOfMachineIPs() {

    if (cr.getConfiguration().containsKey(LIST_OF_MACHINE_IPS_KEY)) {

      String[] names = cr.getConfiguration().getString(LIST_OF_MACHINE_IPS_KEY)
        .split(";"); // split
      for (int i = 0; i < names.length; i++) {
        names[i] = names[i].trim().toLowerCase(); // for each bit remove
      }
      return Arrays.asList(names);

    } else {
      return Arrays.asList(new String[] { "127.0.0.1" });
    }
  }

  /**
   * Method used by all DAO Objects to get the DataBase Driver. If no value is
   * found in the configuration medium, then the default value is returned
   * instead. key="asynch.picker.db.driver"; default
   * value="com.mysql.jdbc.Driver";
   */
  public String getDBDriver() {

    if (!cr.getConfiguration().containsKey(DB_DRIVER_KEY)) {
      // return default
      return "com.mysql.jdbc.Driver";
    } else {
      String vendor = cr.getConfiguration().getString(DB_DRIVER_KEY);
      String driver = "";
      if (vendor.toLowerCase().equals("mysql")) {
        driver = "com.mysql.jdbc.Driver";
      } else {
        log.error("CONFIG ERROR 'RDBMS Vendor ('{}') unknown.'", vendor);
      }
      return driver;
    }
  }

  /**
   * Method used by all DAO Objects to get DB URL. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key1="asynch.picker.db.protocol"; default value="jdbc:mysql://";
   * key2="asynch.picker.db.host"; default value="localhost";
   * key3="asynch.picker.db.name"; default value="storm_db"; The returned value
   * is made up of the above default values and whatever is read from the
   * configuration medium, combined in the following way: protocol + host + "/"
   * + name
   */
  public String getDBURL() {

    String prefix = "";
    String host = "";
    String name = "";
    // get prefix...
    if (!cr.getConfiguration().containsKey(DB_URL_1KEY)) {
      // use default
      prefix = "jdbc:mysql://";
    } else {
      // load from external source
      prefix = cr.getConfiguration().getString(DB_URL_1KEY);
    }
    // get host...
    if (!cr.getConfiguration().containsKey(DB_URL_2KEY)) {
      // use default
      host = "localhost";
    } else {
      // load from external source
      host = cr.getConfiguration().getString(DB_URL_2KEY);
    }
    // get db name...
    if (!cr.getConfiguration().containsKey(DB_URL_3KEY)) {
      // use default
      name = "storm_db";
    } else {
      // load from external source
      name = cr.getConfiguration().getString(DB_URL_3KEY);
    }
    // return value...
    return prefix + host + "/" + name;
  }

  /**
   * Method used by all DAO Objects to get the DB username. If no value is found
   * in the configuration medium, then the default value is returned instead.
   * Default value = "storm"; key searched in medium =
   * "asynch.picker.db.username".
   */
  public String getDBUserName() {

    if (!cr.getConfiguration().containsKey(DB_USER_NAME_KEY)) {
      // return default
      return "storm";
    } else {
      // load from external source
      return cr.getConfiguration().getString(DB_USER_NAME_KEY);
    }
  }

  /**
   * Method used by all DAO Objects to get the DB password. If no value is found
   * in the configuration medium, then the default value is returned instead.
   * Deafult value = "storm"; key searched in medium =
   * "asynch.picker.db.passwd".
   */
  public String getDBPassword() {

    if (!cr.getConfiguration().containsKey(DB_PASSWORD_KEY)) {
      // return default
      return "storm";
    } else {
      // load from external source
      return cr.getConfiguration().getString(DB_PASSWORD_KEY);
    }
  }

  /*
   * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ ## ## END
   * definition of MANDATORY PROPERTIESs ##
   * ############################################################
   */

  /**
   * Method used by all DAOs to establish the reconnection period in _seconds_:
   * after such period the DB connection will be closed and re-opened. Beware
   * that after such time expires, the connection is _not_ automatically closed
   * and reopened; rather, it acts as a flag that is considered by the main code
   * and when the most appropriate time comes, the connection is closed and
   * reopened. This is because of MySQL bug that does not allow a connection to
   * remain open for an arbitrary amount of time! Else an Unexpected EOF
   * Exception gets thrown by the JDBC driver! If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="asynch.db.ReconnectPeriod"; default value=18000; Keep in mind that
   * 18000 seconds = 5 hours.
   */
  public long getDBReconnectPeriod() {

    if (!cr.getConfiguration().containsKey(DB_RECONNECT_PERIOD_KEY)) {
      // return default
      return 18000; // 18000 sec = 5 hours
    } else {
      // load from external source
      return cr.getConfiguration().getLong(DB_RECONNECT_PERIOD_KEY);
    }
  }

  /**
   * Method used by all DAOs to establish the reconnection delay in _seconds_:
   * when StoRM is first launched it will wait for this amount of time before
   * starting the timer. This is because of MySQL bug that does not allow a
   * connection to remain open for an arbitrary amount of time! Else an
   * Unexpected EOF Exception gets thrown by the JDBC driver! If no value is
   * found in the configuration medium, then the default value is returned
   * instead. key="asynch.db.ReconnectDelay"; default value=30;
   */
  public long getDBReconnectDelay() {

    if (!cr.getConfiguration().containsKey(DB_RECONNECT_DELAY_KEY)) {
      // return default
      return 30;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(DB_RECONNECT_DELAY_KEY);
    }
  }

  /**
   * Method used by PinnedFilesCatalog to get the initial delay in _seconds_
   * before starting the cleaning thread. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="pinnedfiles.cleaning.delay"; default value=10;
   */
  public long getCleaningInitialDelay() {

    if (!cr.getConfiguration().containsKey(CLEANING_INITIAL_DELAY_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(CLEANING_INITIAL_DELAY_KEY);
    }
  }

  /**
   * Method used by PinnedFilesCatalog to get the cleaning time interval, in
   * _seconds_. If no value is found in the configuration medium, then the
   * default value is returned instead. key="pinnedfiles.cleaning.interval";
   * default value=300; Keep in mind that 300 seconds = 5 minutes.
   */
  public long getCleaningTimeInterval() {

    if (!cr.getConfiguration().containsKey(CLEANING_TIME_INTERVAL_KEY)) {
      // return default
      return 300;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(CLEANING_TIME_INTERVAL_KEY);
    }
  }

  /**
   * Get the default file size
   * 
   * @return
   */
  public long getFileDefaultSize() {

    if (!cr.getConfiguration().containsKey(FILE_DEFAULT_SIZE_KEY)) {
      // return default
      return (1000000); // 1 MB
    } else {
      // load from external source
      return cr.getConfiguration().getLong(FILE_DEFAULT_SIZE_KEY);
    }
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the default fileLifetime to use
   * when a volatile entry is being added/updated, but the user specified a non
   * positive value. Measured in _seconds_. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="fileLifetime.default"; default value=3600;
   */
  public long getFileLifetimeDefault() {

    if (!cr.getConfiguration().containsKey(FILE_LIFETIME_DEFAULT_KEY)) {
      // return default
      return 3600;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(FILE_LIFETIME_DEFAULT_KEY);
    }
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the minimum pinLifetime
   * allowed, when a jit is being added/updated, but the user specified a lower
   * one. This method is also used by the PinLifetimeConverter to translate a
   * NULL/0/negative value to a default one. Measured in _seconds_. If no value
   * is found in the configuration medium, then the default value is returned
   * instead. key="pinLifetime.minimum"; default value=30;
   */
  public long getPinLifetimeDefault() {

    if (!cr.getConfiguration().containsKey(PIN_LIFETIME_DEFAULT_KEY)) {
      // return default
      return 259200;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(PIN_LIFETIME_DEFAULT_KEY);
    }
  }

  /**
   * Method used by VolatileAndJiTCatalog to get the maximum pinLifetime
   * allowed, when a jit is being added/updated, but the user specified a higher
   * one. Measured in _seconds_. If no value is found in the configuration
   * medium, then the default value is returned instead.
   * key="pinLifetime.maximum"; default value=144000 (40 hours);
   */
  public long getPinLifetimeMaximum() {

    if (!cr.getConfiguration().containsKey(PIN_LIFETIME_MAXIMUM_KEY)) {
      // return default
      return 1814400;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(PIN_LIFETIME_MAXIMUM_KEY);
    }
  }

  /**
   * Method used by PtPChunkCatalog to get the initial delay in _seconds_ before
   * starting the transiting thread. If no value is found in the configuration
   * medium, then the default value is returned instead. key="transit.delay";
   * default value=10;
   */
  public long getTransitInitialDelay() {

    if (!cr.getConfiguration().containsKey(TRANSIT_INITIAL_DELAY_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(TRANSIT_INITIAL_DELAY_KEY);
    }
  }

  /**
   * Method used by PtPChunkCatalog to get the transiting time interval, in
   * _seconds_. If no value is found in the configuration medium, then the
   * default value is returned instead. key="transit.interval"; default
   * value=300; Keep in mind that 300 seconds = 5 minutes.
   */
  public long getTransitTimeInterval() {

    if (!cr.getConfiguration().containsKey(TRANSIT_TIME_INTERVAL_KEY)) {
      // return default
      return 300;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(TRANSIT_TIME_INTERVAL_KEY);
    }
  }

  /**
   * Method used by AdvancedPicker to get the initial delay before starting to
   * pick data from the DB, in _seconds_. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="asynch.PickingInitialDelay"; default value=5;
   */
  public long getPickingInitialDelay() {

    if (!cr.getConfiguration().containsKey(PICKING_INITIAL_DELAY_KEY)) {
      // return default
      return 1;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(PICKING_INITIAL_DELAY_KEY);
    }
  }

  /**
   * Method used by AdvancedPicker to get the time interval of successive
   * pickings, in _seconds_. If no value is found in the configuration medium,
   * then the default value is returned instead.
   * key="asynch.PickingTimeInterval"; default value=15;
   */
  public long getPickingTimeInterval() {

    if (!cr.getConfiguration().containsKey(PICKING_TIME_INTERVAL_KEY)) {
      // return default
      return 2;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(PICKING_TIME_INTERVAL_KEY);
    }
  }

  /**
   * Method used by RequestSummaryDAO to establish the maximum number of
   * requests to retrieve with each polling. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="asynch.PickingMaxBatchSize"; default value=30;
   */
  public int getPickingMaxBatchSize() {

    if (!cr.getConfiguration().containsKey(PICKING_MAX_BATCH_SIZE_KEY)) {
      // return default
      return 100;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PICKING_MAX_BATCH_SIZE_KEY);
    }
  }

  /**
   * Method used by CopyChunk when making a remote srmPrepareToPut (Push Mode).
   * It needs it to estalish the totalRetryTime in seconds to supply to the
   * internal SRMClient. The parameter is passed to the prepareToPut
   * functionality. If no value is found in the configuration medium, then the
   * default value is returned instead. key="asynch.srmclient.retrytime";
   * default value=60;
   */
  public long getSRMClientPutTotalRetryTime() {

    if (!cr.getConfiguration().containsKey(SRMCLIENT_PUT_TOTAL_RETRY_TIME_KEY)) {
      // return default
      return 60;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(SRMCLIENT_PUT_TOTAL_RETRY_TIME_KEY);
    }
  }

  /**
   * Method used by CopyChunk when making a remote srmPrepareToPut (Push Mode).
   * The CopyChunk will periodically invoke the statusOfPutRequest functionality
   * of the internal SRMClient, for at most the time out interval in seconds
   * returned by this method. If no value is found in the configuration medium,
   * then the default value is returned instead. key="asynch.srmclient.timeout";
   * default value=180;
   */
  public long getSRMClientPutTimeOut() {

    if (!cr.getConfiguration().containsKey(SRMCLIENT_PUT_TIME_OUT_KEY)) {
      // return default
      return 180;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(SRMCLIENT_PUT_TIME_OUT_KEY);
    }
  }

  /**
   * Method used by CopyChunk when making a remote srmPrepareToPut (Push Mode).
   * The CopyChunk will wait the amount of time in seconds returned by this
   * method, before invoking again the statusOfPutRequest functionality of the
   * internal SRMClient. That is, it tells the polling interval. If no value is
   * found in the configuration medium, then the default value is returned
   * instead. key="asynch.srmclient.sleeptime"; default value=5;
   */
  public long getSRMClientPutSleepTime() {

    if (!cr.getConfiguration().containsKey(SRMCLIENT_PUT_SLEEP_TIME_KEY)) {
      // return default
      return 5;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(SRMCLIENT_PUT_SLEEP_TIME_KEY);
    }
  }

  /**
   * Method used by CopyChunk when making the FileTransfer and finally invoking
   * a remote srmPutDone. The CopyChunk will wait the amount of time in seconds
   * returned by this method, before invoking again the srmPutDone functionality
   * of the internal SRMClient. That is, it tells the time interval between
   * successive invocations: they are necessary when the returned status is
   * SRM_INTERNAL_ERROR which denotes a transient error situation. If no value
   * is found in the configuration medium, then the default value is returned
   * instead. key="asynch.srmclient.putdone.sleeptime"; default value=2;
   */
  public long getSRMClientPutDoneSleepTime() {

    if (!cr.getConfiguration().containsKey(SRMCLIENT_PUT_DONE_SLEEP_TIME_KEY)) {
      // return default
      return 1;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(SRMCLIENT_PUT_DONE_SLEEP_TIME_KEY);
    }
  }

  /**
   * Method used by CopyChunk when making the FileTransfer and finally invoking
   * a remote srmPutDone. The CopyChunk may have to periodically invoke
   * srmPutDone functionality of the internal SRMClient if the web service
   * returns SRM_INTERNAL_ERROR; in that case invocations will be attempted for
   * at most the time out interval in seconds returned by this method. If no
   * value is found in the configuration medium, then the default value is
   * returned instead. key="asynch.srmclient.putdone.timeout"; default value=60;
   */
  public long getSRMClientPutDoneTimeOut() {

    if (!cr.getConfiguration().containsKey(SRMCLIENT_PUT_DONE_TIME_OUT_KEY)) {
      // return default
      return 60;
    } else {
      // load from external source
      return cr.getConfiguration().getLong(SRMCLIENT_PUT_DONE_TIME_OUT_KEY);
    }
  }

  /**
   * Get max number of xmlrpc threads into for the XMLRPC server.
   */
  public int getMaxXMLRPCThread() {

    if (!cr.getConfiguration().containsKey(MAX_XMLRPC_THREAD_KEY)) {
      // return default
      return 100;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(MAX_XMLRPC_THREAD_KEY);
    }
  }

  /**
   * Get Default Space Tokens
   * 
   * @return
   */
  public List<String> getListOfDefaultSpaceToken() {

    if (cr.getConfiguration().containsKey(LIST_OF_DEFAULT_SPACE_TOKEN_KEY)) {

      String[] namesArray = cr.getConfiguration().getStringArray(
        LIST_OF_DEFAULT_SPACE_TOKEN_KEY);
      if (namesArray == null) {
        return new ArrayList<String>();
      }
      return Arrays.asList(namesArray);

    } else {
      return new ArrayList<String>();
    }
  }

  /**
   * Method used by Factory invoked in CopyChunk subclasses, to instantiate a
   * GridFTPTransferClient. The String returned specifies the name of the class
   * to instantiate; for now, there are two classes: NaiveGridFTPTransferClient
   * and StubGridFTPTransferClient. If no value is found in the configuration
   * medium, then the default value is returned instead.
   * key="asynch.gridftpclient"; default
   * value="it.grid.storm.asynch.NaiveGridFTPTransferClient";
   */
  public String getGridFTPTransferClient() {

    if (!cr.getConfiguration().containsKey(GRIDFTP_TRANSFER_CLIENT_KEY)) {
      // return default
      return "it.grid.storm.asynch.NaiveGridFTPTransferClient";
    } else {
      // load from external source
      return cr.getConfiguration().getString(GRIDFTP_TRANSFER_CLIENT_KEY);
    }
  }

  /**
   * Method used by Factory invoked in CopyChunk subclasses, to instantiate an
   * SRMClient. The String returned specifies the name of the class to
   * instantiate; for now, there are two classes: NaiveSRMClient and
   * StubSRMClient. If no value is found in the configuration medium, then the
   * default value is returned instead. key="asynch.srmclient"; default
   * value="it.grid.storm.asynch.SRM22Client";
   */
  public String getSRMClient() {

    if (!cr.getConfiguration().containsKey(SRMCLIENT_KEY)) {
      // return default
      return "it.grid.storm.asynch.SRM22Client";
    } else {
      // load from external source
      return cr.getConfiguration().getString(SRMCLIENT_KEY);
    }
  }

  /**
   * Method used by StoRMCommandServer to establish the listening port to which
   * it should bind. If no value is found in the configuration medium, then the
   * default value is returned instead. key="storm.commandserver.port"; default
   * value=4444;
   */
  public int getCommandServerBindingPort() {

    if (!cr.getConfiguration().containsKey(COMMAND_SERVER_BINDING_PORT_KEY)) {
      // return default
      return 4444;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(COMMAND_SERVER_BINDING_PORT_KEY);
    }
  }

  /**
   * Method used by Dispatcher and Feeder objects to check if a serial scheduler
   * must be used, or not. If no value is found in the configuration medium,
   * then the default value is returned instead. key="scheduler.serial"; default
   * value=false;
   */
  public boolean getSerialScheduler() {

    if (!cr.getConfiguration().containsKey(SERIAL_SCHEDULER_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(SERIAL_SCHEDULER_KEY);
    }
  }

  /**
   * Method used in Persistence Component It returns the DB vendor name. If no
   * value is found in the configuration medium, then the default value is
   * returned instead. key="persistence.db.vendor"; default value="mysql";
   */
  public String getBEPersistenceDBVendor() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_DB_VENDOR_KEY)) {
      // return default
      return "mysql";
    } else {
      // load from external source
      return cr.getConfiguration().getString(BE_PERSISTENCE_DB_VENDOR_KEY);
    }
  }

  /**
   * Method used in Persistence Component: it returns the host where the DB
   * resides. If no value is found in the configuration medium, then the default
   * value is returned instead. key="persistence.db.host"; default
   * value="localhost";
   */
  public String getBEPersistenceDBMSUrl() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_DBMS_URL_1KEY)) {
      // Try with the alternative key
      if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_DBMS_URL_2KEY)) {
        // return default
        return "localhost";
      } else {
        return cr.getConfiguration().getString(BE_PERSISTENCE_DBMS_URL_2KEY);
      }
    } else {
      // load from external source
      return cr.getConfiguration().getString(BE_PERSISTENCE_DBMS_URL_1KEY);
    }
  }

  /**
   * Method used in Persistence Component it returns the name of the DB to use.
   * If no value is found in the configuration medium, then the default value is
   * returned instead. key="persistence.db.name"; default value="storm_be_ISAM";
   */
  public String getBEPersistenceDBName() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_DB_NAME_KEY)) {
      // return default
      return "storm_be_ISAM";
    } else {
      // load from external source
      return cr.getConfiguration().getString(BE_PERSISTENCE_DB_NAME_KEY);
    }
  }

  /**
   * Method used in Persistence Component it returns the name of the DB user
   * that must be used. If no value is found in the configuration medium, then
   * the default value is returned instead. key="persistence.db.username";
   * default value="storm";
   */
  public String getBEPersistenceDBUserName() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCEDB_USER_NAME_1KEY)) {
      // Try with the alternative key
      if (!cr.getConfiguration().containsKey(BE_PERSISTENCEDB_USER_NAME_2KEY)) {
        // return default
        return "storm";
      } else {
        return cr.getConfiguration().getString(BE_PERSISTENCEDB_USER_NAME_2KEY);
      }
    } else {
      // load from external source
      return cr.getConfiguration().getString(BE_PERSISTENCEDB_USER_NAME_1KEY);
    }
  }

  /**
   * Method used in Persistence Component it returns the password for the DB
   * user that must be used. If no value is found in the configuration medium,
   * then the default value is returned instead. key="persistence.db.passwd";
   * default value="storm";
   */
  public String getBEPersistenceDBPassword() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_DB_PASSWORD_1KEY)) {
      // Try with the alternative key
      if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_DB_PASSWORD_2KEY)) {
        // return default
        return "storm";
      } else {
        return cr.getConfiguration().getString(BE_PERSISTENCE_DB_PASSWORD_2KEY);
      }
    } else {
      // load from external source
      return cr.getConfiguration().getString(BE_PERSISTENCE_DB_PASSWORD_1KEY);
    }
  }

  /**
   * Method used in Persistence Component it returns a boolean indicating
   * whether to use connection pooling or not. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="persistence.db.pool"; default value=false;
   */
  public boolean getBEPersistencePoolDB() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_POOL_DB_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(BE_PERSISTENCE_POOL_DB_KEY);
    }
  }

  /**
   * Method used in Persistence Component it returns an int indicating the
   * maximum number of active connections in the connection pool. It is the
   * maximum number of active connections that can be allocated from this pool
   * at the same time... 0 (zero) for no limit. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="persistence.db.pool.maxActive"; default value=10;
   */
  public int getBEPersistencePoolDBMaxActive() {

    if (!cr.getConfiguration().containsKey(
      BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration()
        .getInt(BE_PERSISTENCE_POOL_DB_MAX_ACTIVE_KEY);
    }
  }

  /**
   * Method used in Persistence Component it returns an int indicating the
   * maximum waiting time in _milliseconds_ for the connection in the pool. It
   * represents the time that the pool will wait (when there are no available
   * connections) for a connection to be returned before throwing an
   * exception... a value of -1 to wait indefinitely. If no value is found in
   * the configuration medium, then the default value is returned instead.
   * key="persistence.db.pool.maxWait"; default value=50;
   */
  public int getBEPersistencePoolDBMaxWait() {

    if (!cr.getConfiguration().containsKey(BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY)) {
      // return default
      return 50;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(BE_PERSISTENCE_POOL_DB_MAX_WAIT_KEY);
    }
  }

  /**
   * Method used by the Synch Component to set the binding port for the
   * _unsecure_ xmlrpc server in the BE. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="synchcall.xmlrpc.unsecureServerPort"; default value=8080;
   */
  public int getXmlRpcServerPort() {

    if (!cr.getConfiguration().containsKey(XMLRPC_SERVER_PORT_KEY)) {
      // return default
      return 8080;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(XMLRPC_SERVER_PORT_KEY);
    }
  }

  /**
   * Method used by the Synch Component to set the maximum number of entries to
   * return for the srmLs functionality. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="synchcall.directoryManager.maxLsEntry"; default value=500;
   * 
   * @return int
   */
  public int getLSMaxNumberOfEntry() {

    if (!cr.getConfiguration().containsKey(LS_MAX_NUMBER_OF_ENTRY_KEY)) {
      // return default
      return 500;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(LS_MAX_NUMBER_OF_ENTRY_KEY);
    }
  }

  /**
   * Default value for the parameter "allLevelRecursive" of the LS request.
   * 
   * @return boolean
   */
  public boolean getLSallLevelRecursive() {

    if (!cr.getConfiguration().containsKey(LS_ALL_LEVEL_RECURSIVE_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(LS_ALL_LEVEL_RECURSIVE_KEY);
    }
  }

  /**
   * Default value for the parameter "numOfLevels" of the LS request.
   * 
   * @return int
   */
  public int getLSnumOfLevels() {

    if (!cr.getConfiguration().containsKey(LS_NUM_OF_LEVELS_KEY)) {
      // return default
      return 1;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(LS_NUM_OF_LEVELS_KEY);
    }
  }

  /**
   * Default value for the parameter "offset" of the LS request.
   * 
   * @return int
   */
  public int getLSoffset() {

    if (!cr.getConfiguration().containsKey(LS_OFFSET_KEY)) {
      // return default
      return 0;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(LS_OFFSET_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Core Poolsize for the srmPrepareToPut management. Scheduler component uses
   * a thread pool. Scheduler pool will automatically adjust the pool size
   * according to the bounds set by corePoolSize and maximumPoolSize. When a new
   * task is submitted in method execute, and fewer than corePoolSize threads
   * are running, a new thread is created to handle the request, even if other
   * worker threads are idle. If there are more than corePoolSize but less than
   * maximumPoolSize threads running, a new thread will be created only if the
   * queue is full. By setting corePoolSize and maximumPoolSize the same, you
   * create a fixed-size thread pool. corePoolSize - the number of threads to
   * keep in the pool, even if they are idle. If no value is found in the
   * configuration medium, then the default value is returned instead.
   * key="scheduler.chunksched.ptp.workerCorePoolSize"; default value=10;
   */
  public int getPtPCorePoolSize() {

    if (!cr.getConfiguration().containsKey(PTP_CORE_POOL_SIZE_KEY)) {
      // return default
      return 50;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PTP_CORE_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Max Pool Size for the srmPrepareToPut management. Scheduler component uses
   * a thread pool. Scheduler pool will automatically adjust the pool size
   * according to the bounds set by corePoolSize and maximumPoolSize. When a new
   * task is submitted in method execute, and fewer than corePoolSize threads
   * are running, a new thread is created to handle the request, even if other
   * worker threads are idle. If there are more than corePoolSize but less than
   * maximumPoolSize threads running, a new thread will be created only if the
   * queue is full. By setting corePoolSize and maximumPoolSize the same, you
   * create a fixed-size thread pool. maxPoolSize - the maximum number of
   * threads to allow in the pool. If no value is found in the configuration
   * medium, then the default value is returned instead.
   * key="scheduler.chunksched.ptp.workerMaxPoolSize"; default value=100;
   */
  public int getPtPMaxPoolSize() {

    if (!cr.getConfiguration().containsKey(PTP_MAX_POOL_SIZE_KEY)) {
      // return default
      return 200;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PTP_MAX_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the
   * srmPrepareToPut management. If no value is found in the configuration
   * medium, then the default value is returned instead. Scheduler hold a
   * blocking priority queue used to transfer and hols submitted tasks. The use
   * of this queue interacts with pool sizing: - If fewer than corePoolSize
   * threads are running, the Scheduler always prefers adding a new thread
   * rather than queuing. - If corePoolSize or more threads are running, the
   * Scheduler always prefers queuing a request rather than adding a new thread.
   * - If a request cannot be queued, a new thread is created unless this would
   * exceed maxPoolSize, in which case, the task will be rejected. QueueSize -
   * The initial capacity for this priority queue used for holding tasks before
   * they are executed. The queue will hold only the Runnable tasks submitted by
   * the execute method. key="scheduler.chunksched.ptp.queueSize"; default
   * value=100;
   */
  public int getPtPQueueSize() {

    if (!cr.getConfiguration().containsKey(PTP_QUEUE_SIZE_KEY)) {
      // return default
      return 1000;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PTP_QUEUE_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Core Pool Size for the srmPrepareToGet management. If no value is found in
   * the configuration medium, then the default value is returned instead.
   * Scheduler component uses a thread pool. Scheduler pool will automatically
   * adjust the pool size according to the bounds set by corePoolSize and
   * maximumPoolSize. When a new task is submitted in method execute, and fewer
   * than corePoolSize threads are running, a new thread is created to handle
   * the request, even if other worker threads are idle. If there are more than
   * corePoolSize but less than maximumPoolSize threads running, a new thread
   * will be created only if the queue is full. By setting corePoolSize and
   * maximumPoolSize the same, you create a fixed-size thread pool. corePoolSize
   * - the number of threads to keep in the pool, even if they are idle.
   * key="scheduler.chunksched.ptg.workerCorePoolSize"; default value=10;
   */
  public int getPtGCorePoolSize() {

    if (!cr.getConfiguration().containsKey(PTG_CORE_POOL_SIZE_KEY)) {
      // return default
      return 50;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PTG_CORE_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Max Pool Size for the srmPrepareToGet management. If no value is found in
   * the configuration medium, then the default value is returned instead.
   * Scheduler component uses a thread pool. Scheduler pool will automatically
   * adjust the pool size according to the bounds set by corePoolSize and
   * maximumPoolSize. When a new task is submitted in method execute, and fewer
   * than corePoolSize threads are running, a new thread is created to handle
   * the request, even if other worker threads are idle. If there are more than
   * corePoolSize but less than maximumPoolSize threads running, a new thread
   * will be created only if the queue is full. By setting corePoolSize and
   * maximumPoolSize the same, you create a fixed-size thread pool. maxPoolSize
   * - the maximum number of threads to allow in the pool.
   * key="scheduler.chunksched.ptg.workerMaxPoolSize"; default value=100;
   */
  public int getPtGMaxPoolSize() {

    if (!cr.getConfiguration().containsKey(PTG_MAX_POOL_SIZE_KEY)) {
      // return default
      return 200;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PTG_MAX_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the
   * srmPrepareToGet management. If no value is found in the configuration
   * medium, then the default value is returned instead. Scheduler hold a
   * blocking priority queue used to transfer and hols submitted tasks. The use
   * of this queue interacts with pool sizing: - If fewer than corePoolSize
   * threads are running, the Scheduler always prefers adding a new thread
   * rather than queuing. - If corePoolSize or more threads are running, the
   * Scheduler always prefers queuing a request rather than adding a new thread.
   * - If a request cannot be queued, a new thread is created unless this would
   * exceed maxPoolSize, in which case, the task will be rejected. QueueSize -
   * The initial capacity for this priority queue used for holding tasks before
   * they are executed. The queue will hold only the Runnable tasks submitted by
   * the execute method. key="scheduler.chunksched.ptg.queueSize"; default
   * value=100;
   */
  public int getPtGQueueSize() {

    if (!cr.getConfiguration().containsKey(PTG_QUEUE_SIZE_KEY)) {
      // return default
      return 2000;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PTG_QUEUE_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Core Pool Size for the srmCopy management. If no value is found in the
   * configuration medium, then the default value is returned instead. Scheduler
   * component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize.
   * When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if
   * other worker threads are idle. If there are more than corePoolSize but less
   * than maximumPoolSize threads running, a new thread will be created only if
   * the queue is full. By setting corePoolSize and maximumPoolSize the same,
   * you create a fixed-size thread pool. corePoolSize - the number of threads
   * to keep in the pool, even if they are idle.
   * key="scheduler.chunksched.copy.workerCorePoolSize"; default value=10;
   */
  public int getCopyCorePoolSize() {

    if (!cr.getConfiguration().containsKey(COPY_CORE_POOL_SIZE_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(COPY_CORE_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Max Pool Size for the srmCopy management. If no value is found in the
   * configuration medium, then the default value is returned instead. Scheduler
   * component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize.
   * When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if
   * other worker threads are idle. If there are more than corePoolSize but less
   * than maximumPoolSize threads running, a new thread will be created only if
   * the queue is full. By setting corePoolSize and maximumPoolSize the same,
   * you create a fixed-size thread pool. maxPoolSize - the maximum number of
   * threads to allow in the pool.
   * key="scheduler.chunksched.copy.workerMaxPoolSize"; default value=100;
   */
  public int getCopyMaxPoolSize() {

    if (!cr.getConfiguration().containsKey(COPY_MAX_POOL_SIZE_KEY)) {
      // return default
      return 50;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(COPY_MAX_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the
   * srmCopy management. If no value is found in the configuration medium, then
   * the default value is returned instead. Scheduler hold a blocking priority
   * queue used to transfer and hols submitted tasks. The use of this queue
   * interacts with pool sizing: - If fewer than corePoolSize threads are
   * running, the Scheduler always prefers adding a new thread rather than
   * queuing. - If corePoolSize or more threads are running, the Scheduler
   * always prefers queuing a request rather than adding a new thread. - If a
   * request cannot be queued, a new thread is created unless this would exceed
   * maxPoolSize, in which case, the task will be rejected. QueueSize - The
   * initial capacity for this priority queue used for holding tasks before they
   * are executed. The queue will hold only the Runnable tasks submitted by the
   * execute method. key="scheduler.chunksched.copy.queueSize"; default
   * value=100;
   */
  public int getCopyQueueSize() {

    if (!cr.getConfiguration().containsKey(COPY_QUEUE_SIZE_KEY)) {
      // return default
      return 500;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(COPY_QUEUE_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Core Pool Size for the srmBoL management. If no value is found in the
   * configuration medium, then the default value is returned instead. Scheduler
   * component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize.
   * When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if
   * other worker threads are idle. If there are more than corePoolSize but less
   * than maximumPoolSize threads running, a new thread will be created only if
   * the queue is full. By setting corePoolSize and maximumPoolSize the same,
   * you create a fixed-size thread pool. corePoolSize - the number of threads
   * to keep in the pool, even if they are idle.
   * key="scheduler.chunksched.bol.workerCorePoolSize"; default value=10;
   */
  public int getBoLCorePoolSize() {

    if (!cr.getConfiguration().containsKey(BOL_CORE_POOL_SIZE_KEY)) {
      // return default
      return 50;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(BOL_CORE_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Max Pool Size for the srmBoL management. If no value is found in the
   * configuration medium, then the default value is returned instead. Scheduler
   * component uses a thread pool. Scheduler pool will automatically adjust the
   * pool size according to the bounds set by corePoolSize and maximumPoolSize.
   * When a new task is submitted in method execute, and fewer than corePoolSize
   * threads are running, a new thread is created to handle the request, even if
   * other worker threads are idle. If there are more than corePoolSize but less
   * than maximumPoolSize threads running, a new thread will be created only if
   * the queue is full. By setting corePoolSize and maximumPoolSize the same,
   * you create a fixed-size thread pool. maxPoolSize - the maximum number of
   * threads to allow in the pool.
   * key="scheduler.chunksched.bol.workerMaxPoolSize"; default value=100;
   */
  public int getBoLMaxPoolSize() {

    if (!cr.getConfiguration().containsKey(BOL_MAX_POOL_SIZE_KEY)) {
      // return default
      return 200;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(BOL_MAX_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the srmBoL
   * management. If no value is found in the configuration medium, then the
   * default value is returned instead. Scheduler hold a blocking priority queue
   * used to transfer and hols submitted tasks. The use of this queue interacts
   * with pool sizing: - If fewer than corePoolSize threads are running, the
   * Scheduler always prefers adding a new thread rather than queuing. - If
   * corePoolSize or more threads are running, the Scheduler always prefers
   * queuing a request rather than adding a new thread. - If a request cannot be
   * queued, a new thread is created unless this would exceed maxPoolSize, in
   * which case, the task will be rejected. QueueSize - The initial capacity for
   * this priority queue used for holding tasks before they are executed. The
   * queue will hold only the Runnable tasks submitted by the execute method.
   * key="scheduler.chunksched.bol.queueSize"; default value=100;
   */
  public int getBoLQueueSize() {

    if (!cr.getConfiguration().containsKey(BOL_QUEUE_SIZE_KEY)) {
      // return default
      return 2000;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(BOL_QUEUE_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Core Pool Size for the Crusher. If no value is found in the configuration
   * medium, then the default value is returned instead. Scheduler component
   * uses a thread pool. Scheduler pool will automatically adjust the pool size
   * according to the bounds set by corePoolSize and maximumPoolSize. When a new
   * task is submitted in method execute, and fewer than corePoolSize threads
   * are running, a new thread is created to handle the request, even if other
   * worker threads are idle. If there are more than corePoolSize but less than
   * maximumPoolSize threads running, a new thread will be created only if the
   * queue is full. By setting corePoolSize and maximumPoolSize the same, you
   * create a fixed-size thread pool. corePoolSize - the number of threads to
   * keep in the pool, even if they are idle.
   * key="scheduler.crusher.workerCorePoolSize"; default value=10;
   */
  public int getCorePoolSize() {

    if (!cr.getConfiguration().containsKey(CORE_POOL_SIZE_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(CORE_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the QuotaJobResultsHandler
   * Max Pool Size for the Crisher. If no value is found in the configuration
   * medium, then the default value is returned instead. Scheduler component
   * uses a thread pool. Scheduler pool will automatically adjust the pool size
   * according to the bounds set by corePoolSize and maximumPoolSize. When a new
   * task is submitted in method execute, and fewer than corePoolSize threads
   * are running, a new thread is created to handle the request, even if other
   * worker threads are idle. If there are more than corePoolSize but less than
   * maximumPoolSize threads running, a new thread will be created only if the
   * queue is full. By setting corePoolSize and maximumPoolSize the same, you
   * create a fixed-size thread pool. maxPoolSize - the maximum number of
   * threads to allow in the pool. key="scheduler.crusher.workerMaxPoolSize";
   * default value=100;
   */
  public int getMaxPoolSize() {

    if (!cr.getConfiguration().containsKey(MAX_POOL_SIZE_KEY)) {
      // return default
      return 50;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(MAX_POOL_SIZE_KEY);
    }
  }

  /**
   * Method used by the Scheduler Component to get the Queue Size for the
   * Crusher. If no value is found in the configuration medium, then the default
   * value is returned instead. Scheduler hold a blocking priority queue used to
   * transfer and hols submitted tasks. The use of this queue interacts with
   * pool sizing: - If fewer than corePoolSize threads are running, the
   * Scheduler always prefers adding a new thread rather than queuing. - If
   * corePoolSize or more threads are running, the Scheduler always prefers
   * queuing a request rather than adding a new thread. - If a request cannot be
   * queued, a new thread is created unless this would exceed maxPoolSize, in
   * which case, the task will be rejected. QueueSize - The initial capacity for
   * this priority queue used for holding tasks before they are executed. The
   * queue will hold only the Runnable tasks submitted by the execute method.
   * key="scheduler.crusher.queueSize"; default value=100;
   */
  public int getQueueSize() {

    if (!cr.getConfiguration().containsKey(QUEUE_SIZE_KEY)) {
      // return default
      return 2000;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(QUEUE_SIZE_KEY);
    }
  }

  /**
   * getNamespaceConfigFilename
   * 
   * @return String
   */
  public String getNamespaceConfigFilename() {

    if (!cr.getConfiguration().containsKey(NAMESPACE_CONFIG_FILENAME_KEY)) {
      // return default
      return "namespace.xml";
    } else {
      // load from external source
      return cr.getConfiguration().getString(NAMESPACE_CONFIG_FILENAME_KEY);
    }
  }

  /**
   * Retrieve the namespace schema file name from the first line (attribute) of
   * namespace.xml.
   * 
   * @return String
   */
  public String getNamespaceSchemaFilename() {

    if (!cr.getConfiguration().containsKey(NAMESPACE_SCHEMA_FILENAME_KEY)) {
      return "Schema UNKNOWN!";
    } else {
      // load from external source
      return cr.getConfiguration().getString(NAMESPACE_SCHEMA_FILENAME_KEY);
    }
  }

  public int getNamespaceConfigRefreshRateInSeconds() {

    if (!cr.getConfiguration().containsKey(
      NAMESPACE_CONFIG_REFRESH_RATE_IN_SECONDS_KEY)) {
      // return default
      return 3;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(
        NAMESPACE_CONFIG_REFRESH_RATE_IN_SECONDS_KEY);
    }

  }

  /**
   * getNamespaceAutomaticReloading
   * 
   * @return boolean Method used by Namespace Configuration Reloading Strategy
   *         (Peeper). If "peeper" found namespace.xml config file changed it
   *         checks if it can perform an automatic reload. If no value is found
   *         in the configuration medium, then the default one is used instead.
   *         key="namespace.automatic-config-reload"; default value=false
   */
  public boolean getNamespaceAutomaticReloading() {

    if (!cr.getConfiguration().containsKey(NAMESPACE_AUTOMATIC_RELOADING_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration()
        .getBoolean(NAMESPACE_AUTOMATIC_RELOADING_KEY);
    }
  }

  /**
   * Method used by NaiveGridFTP internal client in srmCopy to establish the
   * time out in milliseconds for a reply from the server. If no value is found
   * in the configuration medium, then the default one is used instead.
   * key="NaiveGridFTP.TimeOut"; default value="15000"
   */
  public int getGridFTPTimeOut() {

    if (!cr.getConfiguration().containsKey(GRIDFTP_TIME_OUT_KEY)) {
      // return default
      return 15000;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(GRIDFTP_TIME_OUT_KEY);
    }
  }

  /**
   * Method used by SRM22Client in srmCopy to establish the PinLifeTime in
   * seconds when issuing srmPtP. If no value is found in the configuration
   * medium, then the default one is used instead.
   * key="SRM22Client.PinLifeTime"; default value="300"
   */
  public int getSRM22ClientPinLifeTime() {

    if (!cr.getConfiguration().containsKey(SRM22CLIENT_PIN_LIFE_TIME_KEY)) {
      // return default
      return 259200;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(SRM22CLIENT_PIN_LIFE_TIME_KEY);
    }
  }

  /**
   * Method used by RequestCredentialsDAO to establish the directory that holds
   * the proxy file. If no value is found in the configuration medium, then the
   * default one is used instead. key="proxy.home"; default
   * value="/opt/storm/var/proxies"
   */
  public String getProxyHome() {

    if (!cr.getConfiguration().containsKey(PROXY_HOME_KEY)) {
      // return default
      return "/opt/storm/var/proxies";
    } else {
      // load from external source
      return cr.getConfiguration().getString(PROXY_HOME_KEY);
    }
  }

  /**
   * Method used by PtPChunk to find out if missing local directories should be
   * created automatically or not. SRM 2.2 specification forbids automatic
   * creation. If no value is found in the configuration medium, then the
   * default one is used instead. key="automatic.directory.creation"; default
   * value=false
   */
  public boolean getAutomaticDirectoryCreation() {

    if (!cr.getConfiguration().containsKey(AUTOMATIC_DIRECTORY_CREATION_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(AUTOMATIC_DIRECTORY_CREATION_KEY);
    }
  }

  /**
   * Method used by TOverwriteModeConverter to establish the default
   * OverwriteMode to use. If no value is found in the configuration medium,
   * then the default one is used instead. key="default.overwrite"; default
   * value="A"
   */
  public String getDefaultOverwriteMode() {

    if (!cr.getConfiguration().containsKey(DEFAULT_OVERWRITE_MODE_KEY)) {
      // return default
      return "N";
    } else {
      // load from external source
      return cr.getConfiguration().getString(DEFAULT_OVERWRITE_MODE_KEY);
    }
  }

  /**
   * Method used by FileStorageTypeConverter to establish the default
   * TFileStorageType to use. If no value is found in the configuration medium,
   * then the default one is used instead. key="default.storagetype"; default
   * value="V"
   */
  public String getDefaultFileStorageType() {

    if (!cr.getConfiguration().containsKey(DEFAULT_FILE_STORAGE_TYPE_KEY)) {
      // return default as specified in SRMv2.2 specification
      return "V";
    } else {
      // load from external source
      return cr.getConfiguration().getString(DEFAULT_FILE_STORAGE_TYPE_KEY);
    }
  }

  /**
   * Method used by RequestSummaryDAO to establish the batch size for removing
   * expired requests. If no value is found in the configuration medium, then
   * the default one is used instead. key="purge.size"; default value=800
   */
  public int getPurgeBatchSize() {

    if (!cr.getConfiguration().containsKey(PURGE_BATCH_SIZE_KEY)) {
      // return default
      return 800;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PURGE_BATCH_SIZE_KEY);
    }
  }

  /**
   * Method used by RequestSummaryDAO to establish the time that must be elapsed
   * for considering a request expired. The time measure specified in the
   * configuration medium is in _days_. The value returned by this method, is
   * expressed in _seconds_ If no value is found in the configuration medium,
   * then the default one is used instead. key="expired.request.time"; default
   * value=7 (days - which correspond to 7 * 24 * 60 * 60 seconds)
   */
  public long getExpiredRequestTime() {

    if (!cr.getConfiguration().containsKey(EXPIRED_REQUEST_TIME_KEY)) {
      // 7 * 24 * 60 * 60
      return 604800;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(EXPIRED_REQUEST_TIME_KEY);
    }
  }

  /**
   * Method used by RequestSummaryCatalog to establish the initial delay before
   * starting the purging thread, in _seconds_. If no value is found in the
   * configuration medium, then the default one is used instead.
   * key="purge.delay"; default value=10
   */
  public int getRequestPurgerDelay() {

    if (!cr.getConfiguration().containsKey(REQUEST_PURGER_DELAY_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(REQUEST_PURGER_DELAY_KEY);
    }
  }

  /**
   * Method used by RequestSummaryCatalog to establish the time interval in
   * _seconds_ between successive purging checks. If no value is found in the
   * configuration medium, then the default one is used instead.
   * key="purge.interval"; default value=600 (1o minutes)
   */
  public int getRequestPurgerPeriod() {

    if (!cr.getConfiguration().containsKey(REQUEST_PURGER_PERIOD_KEY)) {
      // return default
      return 600;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(REQUEST_PURGER_PERIOD_KEY);
    }
  }

  /**
   * Method used by RequestSummaryCatalog to establish if the purging of expired
   * requests should be enabled or not. If no value is found in the
   * configuration medium, then the default one is used instead. key="purging";
   * default value=true
   */
  public boolean getExpiredRequestPurging() {

    if (!cr.getConfiguration().containsKey(EXPIRED_REQUEST_PURGING_KEY)) {
      // return default
      return true;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(EXPIRED_REQUEST_PURGING_KEY);
    }
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the
   * "authority" part of a TURL If no value is found in the configuration
   * medium, then the default one is used instead. key="extraslashes.file";
   * default value="" (that is 'file:///) value = "/" ==> file:////
   */
  public String getExtraSlashesForFileTURL() {

    if (!cr.getConfiguration().containsKey(EXTRA_SLASHES_FOR_FILE_TURL_KEY)) {
      // return default
      return "";
    } else {
      // load from external source
      return cr.getConfiguration().getString(EXTRA_SLASHES_FOR_FILE_TURL_KEY);
    }
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the
   * "authority" part of a TURL If no value is found in the configuration
   * medium, then the default one is used instead. key="extraslashes.rfio";
   * default value="" (that is 'rfio://<hostname>:port<PhysicalFN>')) value =
   * "/" ==> 'rfio://<hostname>:port/<PhysicalFN>'
   */
  public String getExtraSlashesForRFIOTURL() {

    if (!cr.getConfiguration().containsKey(EXTRA_SLASHES_FOR_RFIO_TURL_KEY)) {
      // return default
      return "";
    } else {
      // load from external source
      return cr.getConfiguration().getString(EXTRA_SLASHES_FOR_RFIO_TURL_KEY);
    }
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the
   * "authority" part of a TURL If no value is found in the configuration
   * medium, then the default one is used instead. key="extraslashes.gsiftp";
   * default value="" (that is 'gsiftp://<hostname>:port<PhysicalFN>')) value =
   * "/" ==> 'gsiftp://<hostname>:port/<PhysicalFN>'
   */
  public String getExtraSlashesForGsiFTPTURL() {

    if (!cr.getConfiguration().containsKey(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY)) {
      // return default
      return "";
    } else {
      // load from external source
      return cr.getConfiguration().getString(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY);
    }
  }

  /**
   * Method used by TURLBuilder to adding (in case) extra slashes after the
   * "authority" part of a TURL If no value is found in the configuration
   * medium, then the default one is used instead. key="extraslashes.root";
   * default value="/" (that is 'root://<hostname>:port<PhysicalFN>')) value =
   * "" ==> 'root://<hostname>:port<PhysicalFN>'
   */
  public String getExtraSlashesForROOTTURL() {

    if (!cr.getConfiguration().containsKey(EXTRA_SLASHES_FOR_ROOT_TURL_KEY)) {
      // return default
      return "/";
    } else {
      // load from external source
      return cr.getConfiguration().getString(EXTRA_SLASHES_FOR_ROOT_TURL_KEY);
    }
  }

  /**
   * Method used by Ping Executor to retrieve the Properties File Name where the
   * properties <key,value> are stored. If no value is found in the
   * configuration medium, then the default one is used instead.
   * key="ping-properties.filename"; default value="" (that is
   * 'gsiftp://<hostname>:port<PhysicalFN>')) value = "/" ==>
   * 'gsiftp://<hostname>:port/<PhysicalFN>'
   */
  public String getPingValuesPropertiesFilename() {

    if (!cr.getConfiguration().containsKey(PING_VALUES_PROPERTIES_FILENAME_KEY)) {
      // return default
      return "ping-values.properties";
    } else {
      // load from external source
      return cr.getConfiguration().getString(
        PING_VALUES_PROPERTIES_FILENAME_KEY);
    }
  }

  /**
   * If no value is found in the configuration medium, then the default one is
   * used instead. key="health.electrocardiogram.period"; default value=60 (1
   * min)
   */
  public int getHearthbeatPeriod() {

    if (!cr.getConfiguration().containsKey(HEARTHBEAT_PERIOD_KEY)) {
      // return default
      return 60;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(HEARTHBEAT_PERIOD_KEY);
    }
  }

  /**
   * getPerformanceGlancePeriod
   * 
   * @return int If no value is found in the configuration medium, then the
   *         default one is used instead.
   *         key="health.performance.glance.timeInterval"; default value=15 (15
   *         sec)
   */
  public int getPerformanceGlanceTimeInterval() {

    if (!cr.getConfiguration()
      .containsKey(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY)) {
      // return default
      return 15;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY);
    }
  }

  /**
   * getPerformanceGlancePeriod
   * 
   * @return int If no value is found in the configuration medium, then the
   *         default one is used instead.
   *         key="health.performance.logbook.timeInterval"; default value=20 (20
   *         sec)
   */
  public int getPerformanceLogbookTimeInterval() {

    if (!cr.getConfiguration().containsKey(
      PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY)) {
      // return default
      return 15;
    } else {
      // load from external source
      return cr.getConfiguration()
        .getInt(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY);
    }
  }

  /**
   * getPerformanceMeasuring
   * 
   * @return boolean If no value is found in the configuration medium, then the
   *         default one is used instead.
   *         key="health.performance.mesauring.enabled"; default value=false
   */
  public boolean getPerformanceMeasuring() {

    if (!cr.getConfiguration().containsKey(PERFORMANCE_MEASURING_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(PERFORMANCE_MEASURING_KEY);
    }
  }

  /**
   * getBookKeppeingEnabled
   * 
   * @return boolean Method used by Namespace Configuration Reloading Strategy
   *         (Peeper). If "peeper" found namespace.xml config file changed it
   *         checks if it can perform an automatic reload. If no value is found
   *         in the configuration medium, then the default one is used instead.
   *         key="health.bookkeeping.enabled"; default value=false
   */
  public boolean getBookKeepingEnabled() {

    if (!cr.getConfiguration().containsKey(BOOK_KEEPING_ENABLED_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(BOOK_KEEPING_ENABLED_KEY);
    }
  }

  /**
   * Enable write permission on new created directory for
   * LocalAuthorizationSource usage.
   * 
   * @return false by default, otherwise what is specified in the properties
   */
  public boolean getEnableWritePermOnDirectory() {

    if (!cr.getConfiguration().containsKey(ENABLE_WRITE_PERM_ON_DIRECTORY_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(
        ENABLE_WRITE_PERM_ON_DIRECTORY_KEY, false);
    }
  }

  public int getMaxLoop() {

    if (!cr.getConfiguration().containsKey(MAX_LOOP_KEY)) {
      // return default
      return 10;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(MAX_LOOP_KEY);
    }
  }

  /**
   * Method used to retrieve the ClassName for the User Mapper Class If no value
   * is found in the configuration medium, then the default one is used instead,
   * that is "it.grid.storm.griduser.LcmapsJNAMapper"
   * key="griduser.mapper.classname";
   */
  public String getGridUserMapperClassname() {

    if (!cr.getConfiguration().containsKey(GRID_USER_MAPPER_CLASSNAME_KEY)) {
      // return default
      return "it.grid.storm.griduser.StormLcmapsJNAMapper";
    } else {
      // load from external source
      return cr.getConfiguration().getString(GRID_USER_MAPPER_CLASSNAME_KEY);
    }
  }

  /**
   * Method used to retrieve the default path where the AuthzDB file are stored
   * If no value is found in the configuration medium, then the default one is
   * used instead, that is the "configuration directory" key="authzdb.path";
   */
  public String getAuthzDBPath() {

    if (!cr.getConfiguration().containsKey(AUTHZ_DB_PATH_KEY)) {
      // return default
      return cr.configurationDirectory();
    } else {
      // load from external source
      return cr.getConfiguration().getString(AUTHZ_DB_PATH_KEY);
    }
  }

  /**
   * Method used to retrieve the default refresh rate of the AuthzDB files If no
   * value is found in the configuration medium, then the default one is used
   * instead, that is the "5 sec" key="authzdb.refreshrate";
   */
  public int getRefreshRateAuthzDBfilesInSeconds() {

    if (!cr.getConfiguration().containsKey(
      REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS_KEY)) {
      // return default
      return 5;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(
        REFRESH_RATE_AUTHZDB_FILES_IN_SECONDS_KEY);
    }
  }

  public boolean getRecallTableTestingMode() {

    if (cr.getConfiguration().containsKey(RECALL_TABLE_TESTING_MODE_KEY)) {
      return cr.getConfiguration().getBoolean(RECALL_TABLE_TESTING_MODE_KEY);
    }
    return false;
  }

  /**
   * Method used to retrieve the PORT where RESTful services listen (like the
   * Recall Table service) If no value is found in the configuration medium,
   * then the default one is used instead, that is the "9998"
   * key="tape.recalltable.service.port";
   */
  public int getRestServicesPort() {

    if (!cr.getConfiguration().containsKey(REST_SERVICES_PORT_KEY)) {
      // return default
      return 9998;
    } else {
      // load from external source
      return cr.getConfiguration().getInt(REST_SERVICES_PORT_KEY);
    }
  }

  /**
   * Method used to retrieve the key string used to pass RETRY-VALUE parameter
   * to Recall Table service key="tape.recalltable.service.param.retry-value";
   */
  public String getRetryValueKey() {

    if (!cr.getConfiguration().containsKey(RETRY_VALUE_KEY_KEY)) {
      // return default
      return "retry-value";
    } else {
      // load from external source
      return cr.getConfiguration().getString(RETRY_VALUE_KEY_KEY);
    }
  }

  /**
   * Method used to retrieve the key string used to pass RETRY-VALUE parameter
   * to Recall Table service key="tape.recalltable.service.param.status";
   */
  public String getStatusKey() {

    if (!cr.getConfiguration().containsKey(STATUS_KEY_KEY)) {
      // return default
      return "status";
    } else {
      // load from external source
      return cr.getConfiguration().getString(STATUS_KEY_KEY);
    }
  }

  /**
   * Method used to retrieve the key string used to pass RETRY-VALUE parameter
   * to Recall Table service key="tape.recalltable.service.param.takeover";
   */
  public String getTaskoverKey() {

    if (!cr.getConfiguration().containsKey(TASKOVER_KEY_KEY)) {
      // return default
      return "first";
    } else {
      // load from external source
      return cr.getConfiguration().getString(TASKOVER_KEY_KEY);
    }
  }

  /**
   * This is the FLAG to support or not the http(s) protocol. Default is false.
   * 
   * @return
   */
  public boolean getGridhttpsEnabled() {

    if (!cr.getConfiguration().containsKey(GRIDHTTPS_ENABLED_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(GRIDHTTPS_ENABLED_KEY);
    }
  }

  public String getStoRMPropertiesVersion() {

    if (!cr.getConfiguration().containsKey(STORM_PROPERTIES_VERSION_KEY)) {
      // return default
      return "No version specified";
    } else {
      // load from external source
      return cr.getConfiguration().getString(STORM_PROPERTIES_VERSION_KEY);
    }
  }

  /**
   * Flag to support or not the TAPE integration. Default value is false.
   * 
   * @return
   */
  public boolean getTapeSupportEnabled() {

    if (!cr.getConfiguration().containsKey(TAPE_SUPPORT_ENABLED_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(TAPE_SUPPORT_ENABLED_KEY);
    }
  }



  /**
   * @return
   */
  public String getGRIDHTTPSPluginClassName() {

    return DefaultHTTPPlugin.class.getName();

  }

  /**
   * @return
   */
  public boolean getSynchronousQuotaCheckEnabled() {

    if (!cr.getConfiguration().containsKey(SYNCHRONOUS_QUOTA_CHECK_ENABLED_KEY)) {
      // return default
      return false;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(
        SYNCHRONOUS_QUOTA_CHECK_ENABLED_KEY);
    }
  }

  /**
   * 
   * @return the refresh period in seconds
   */
  public int getGPFSQuotaRefreshPeriod() {

    // GPFS_QUOTA_REFRESH_PERIOD_KEY
    if (!cr.getConfiguration().containsKey(GPFS_QUOTA_REFRESH_PERIOD_KEY)) {
      // return default
      return 900; // 15 minutes
    } else {
      // load from external source
      return cr.getConfiguration().getInt(GPFS_QUOTA_REFRESH_PERIOD_KEY);
    }
  }

  /**
   * @return
   */
  public boolean getFastBootstrapEnabled() {

    if (!cr.getConfiguration().containsKey(FAST_BOOTSTRAP_ENABLED_KEY)) {
      // return default
      return true;
    } else {
      // load from external source
      return cr.getConfiguration().getBoolean(FAST_BOOTSTRAP_ENABLED_KEY);
    }
  }

  /**
   * @return
   */
  public Long getServerPoolStatusCheckTimeout() {

    if (!cr.getConfiguration()
      .containsKey(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY)) {
      // return default
      return new Long(20000); // 20 seconds
    } else {
      // load from external source
      return new Long(cr.getConfiguration().getLong(
        SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY));
    }
  }

  public boolean getSanityCheckEnabled() {

    if (!cr.getConfiguration().containsKey(SANITY_CHECK_ENABLED_KEY)) {
      // return default
      return true;
    } else {
      // load from external source
      return new Boolean(cr.getConfiguration().getBoolean(
        SANITY_CHECK_ENABLED_KEY));
    }
  }

  public Boolean getXmlRpcTokenEnabled() {

    return cr.getConfiguration().getBoolean(XMLRPC_SECURITY_ENABLED_KEY, false);
  }

  public String getXmlRpcToken() {

    return cr.getConfiguration().getString(XMLRPC_SECURITY_TOKEN_KEY);
  }

  public Boolean getPTGSkipACLSetup() {

    return cr.getConfiguration().getBoolean(PTG_SKIP_ACL_SETUP, false);
  }

  @Override
  public String toString() {

    StringBuilder configurationStringBuilder = new StringBuilder();
    try {
      // This class methods
      Method methods[] = Configuration.instance.getClass().getDeclaredMethods();

      // This class fields
      Field[] fields = Configuration.instance.getClass().getDeclaredFields();
      HashMap<String, String> methodKeyMap = new HashMap<String, String>();
      for (Field field : fields) {
        String fieldName = field.getName();
        if (fieldName.endsWith("KEY") && field.getType().equals(String.class)) {
          // from a field like GROUP_TAPE_WRITE_BUFFER_KEY =
          // "tape.buffer.group.write"
          // puts in the map the pair
          // <getgrouptapewritebuffer,tape.buffer.group.write>
          String mapKey = "get"
            + fieldName.substring(0, fieldName.lastIndexOf("_"))
              .replaceAll("_", "").toLowerCase();
          if (methodKeyMap.containsKey(mapKey)) {
            String value = methodKeyMap.get(mapKey);
            methodKeyMap.put(mapKey,
              value + " , " + (String) field.get(Configuration.instance));
          } else {
            methodKeyMap
              .put(mapKey, (String) field.get(Configuration.instance));
          }
        }
      }

      Object field = null;
      Object[] dummyArray = new Object[0];
      for (Method method : methods) {
        /*
         * with method.getModifiers() == 1 we check that the method is public
         * (otherwise he can request real parameters)
         */
        if (method.getName().substring(0, 3).equals("get")
          && (!method.getName().equals("getInstance"))
          && method.getModifiers() == 1) {
          field = method.invoke(Configuration.instance, dummyArray);
          if (field.getClass().isArray()) {
            field = ArrayUtils.toString(field);
          }
          String value = methodKeyMap.get(method.getName().toLowerCase());
          if (value == null) {
            configurationStringBuilder.insert(0, "!! Unable to find method "
              + method.getName() + " in methode key map!");
          } else {
            configurationStringBuilder.append("Property " + value + " : ");
          }
          if (field.getClass().equals(String.class)) {
            field = '\'' + ((String) field) + '\'';
          }
          configurationStringBuilder.append(method.getName() + "() == "
            + field.toString() + "\n");
        }
      }
      return configurationStringBuilder.toString();
    } catch (Exception e) {
      if (e.getClass().isAssignableFrom(
        java.lang.reflect.InvocationTargetException.class)) {
        configurationStringBuilder.insert(0,
          "!!! Cannot do toString! Got an Exception: " + e.getCause() + "\n");
      } else {
        configurationStringBuilder.insert(0,
          "!!! Cannot do toString! Got an Exception: " + e + "\n");
      }
      return configurationStringBuilder.toString();
    }
  }
  
  public String getHTTPTURLPrefix(){
    return cr.getConfiguration().getString(HTTP_TURL_PREFIX,"/fileTransfer");
  }

}
