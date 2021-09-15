package it.grid.storm.config.converter;

import static it.grid.storm.config.model.v1.StormProperties.AUTOMATIC_DIRECTORY_CREATION_KEY;
import static it.grid.storm.config.model.v1.StormProperties.BOL_CORE_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.BOL_MAX_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.BOL_QUEUE_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.BOOK_KEEPING_ENABLED_KEY;
import static it.grid.storm.config.model.v1.StormProperties.CLEANING_INITIAL_DELAY_KEY;
import static it.grid.storm.config.model.v1.StormProperties.CLEANING_TIME_INTERVAL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.CORE_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DB_PASSWORD_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DB_URL_HOSTNAME_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DB_URL_PROPERTIES_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DB_USER_NAME_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DEFAULT_FILE_STORAGE_TYPE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DEFAULT_OVERWRITE_MODE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.DISKUSAGE_SERVICE_ENABLED;
import static it.grid.storm.config.model.v1.StormProperties.DISKUSAGE_SERVICE_INITIAL_DELAY;
import static it.grid.storm.config.model.v1.StormProperties.DISKUSAGE_SERVICE_TASKS_INTERVAL;
import static it.grid.storm.config.model.v1.StormProperties.DISKUSAGE_SERVICE_TASKS_PARALLEL;
import static it.grid.storm.config.model.v1.StormProperties.ENABLE_WRITE_PERM_ON_DIRECTORY_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXPIRED_INPROGRESS_PTP_TIME_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXPIRED_REQUEST_PURGING_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXPIRED_REQUEST_TIME_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXTRA_SLASHES_FOR_FILE_TURL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXTRA_SLASHES_FOR_RFIO_TURL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.EXTRA_SLASHES_FOR_ROOT_TURL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.FILE_DEFAULT_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.FILE_LIFETIME_DEFAULT_KEY;
import static it.grid.storm.config.model.v1.StormProperties.GPFS_QUOTA_REFRESH_PERIOD_KEY;
import static it.grid.storm.config.model.v1.StormProperties.HEARTHBEAT_PERIOD_KEY;
import static it.grid.storm.config.model.v1.StormProperties.HTTP_TURL_PREFIX;
import static it.grid.storm.config.model.v1.StormProperties.LS_ALL_LEVEL_RECURSIVE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.LS_MAX_NUMBER_OF_ENTRY_KEY;
import static it.grid.storm.config.model.v1.StormProperties.LS_NUM_OF_LEVELS_KEY;
import static it.grid.storm.config.model.v1.StormProperties.LS_OFFSET_KEY;
import static it.grid.storm.config.model.v1.StormProperties.MANAGED_SURLS_KEY;
import static it.grid.storm.config.model.v1.StormProperties.MAX_LOOP_KEY;
import static it.grid.storm.config.model.v1.StormProperties.MAX_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PERFORMANCE_GLANCE_TIME_INTERVAL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PERFORMANCE_MEASURING_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PICKING_INITIAL_DELAY_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PICKING_MAX_BATCH_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PICKING_TIME_INTERVAL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PING_VALUES_PROPERTIES_FILENAME_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PIN_LIFETIME_DEFAULT_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PIN_LIFETIME_MAXIMUM_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PTG_CORE_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PTG_MAX_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PTG_QUEUE_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.model.v1.StormProperties.PTP_CORE_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PTP_MAX_POOL_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PTP_QUEUE_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.PURGE_BATCH_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.QUEUE_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.REQUEST_PURGER_DELAY_KEY;
import static it.grid.storm.config.model.v1.StormProperties.REQUEST_PURGER_PERIOD_KEY;
import static it.grid.storm.config.model.v1.StormProperties.REST_SERVICES_MAX_QUEUE_SIZE;
import static it.grid.storm.config.model.v1.StormProperties.REST_SERVICES_MAX_THREAD;
import static it.grid.storm.config.model.v1.StormProperties.REST_SERVICES_PORT_KEY;
import static it.grid.storm.config.model.v1.StormProperties.SANITY_CHECK_ENABLED_KEY;
import static it.grid.storm.config.model.v1.StormProperties.SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY;
import static it.grid.storm.config.model.v1.StormProperties.SERVICE_HOSTNAME_KEY;
import static it.grid.storm.config.model.v1.StormProperties.SERVICE_PORT_KEY;
import static it.grid.storm.config.model.v1.StormProperties.TRANSIT_INITIAL_DELAY_KEY;
import static it.grid.storm.config.model.v1.StormProperties.TRANSIT_TIME_INTERVAL_KEY;
import static it.grid.storm.config.model.v1.StormProperties.XMLRPC_MAX_QUEUE_SIZE_KEY;
import static it.grid.storm.config.model.v1.StormProperties.XMLRPC_MAX_THREAD_KEY;
import static it.grid.storm.config.model.v1.StormProperties.XMLRPC_SECURITY_ENABLED_KEY;
import static it.grid.storm.config.model.v1.StormProperties.XMLRPC_SECURITY_TOKEN_KEY;
import static it.grid.storm.config.model.v1.StormProperties.XMLRPC_SERVER_PORT_KEY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.properties.SortedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.config.model.v2.StormProperties;

public class StormPropertiesConverter {

  private static final Logger log = LoggerFactory.getLogger(StormPropertiesConverter.class);


  private static final List<String> MANDATORY_KEYS =
      Lists.newArrayList(SERVICE_HOSTNAME_KEY, SERVICE_PORT_KEY, MANAGED_SURLS_KEY);

  public static void convert(File source, File target) throws IOException {

    Properties old = new Properties();
    old.load(new FileInputStream(source));

    for (String mandatoryKey : MANDATORY_KEYS) {
      if (!old.containsKey(mandatoryKey)) {
        throw new RuntimeException("Missing mandatory properties for conversion");
      }
    }

    SortedProperties properties = new SortedProperties();

    // version
    properties.setProperty("version", StormProperties.VERSION);

    // srmEndpoints
    String publicHost = old.getProperty(SERVICE_HOSTNAME_KEY).trim();
    String publicPort = old.getProperty(SERVICE_PORT_KEY).trim();
    properties.setProperty("srm_endpoints[0].host", publicHost);
    properties.setProperty("srm_endpoints[0].port", publicPort);
    int i = 1;
    for (String surl : old.getProperty(MANAGED_SURLS_KEY).trim().split(",")) {
      Pattern pattern = Pattern.compile("srm://(.*?)/.*");
      Matcher matcher = pattern.matcher(surl);
      if (matcher.find()) {
        String host = matcher.group(1).split(":")[0];
        String port = matcher.group(1).split(":")[1];
        if (!publicHost.equals(host)) {
          properties.setProperty("srm_endpoints[" + i + "].host", host);
          properties.setProperty("srm_endpoints[" + i + "].port", port);
          i += 1;
        }
      }
    }

    // db
    if (old.containsKey(DB_URL_HOSTNAME_KEY)) {
      properties.setProperty("db.hostname", old.getProperty(DB_URL_HOSTNAME_KEY).trim());
    }
    if (old.containsKey(DB_USER_NAME_KEY)) {
      properties.setProperty("db.username", old.getProperty(DB_USER_NAME_KEY).trim());
    }
    if (old.containsKey(DB_PASSWORD_KEY)) {
      properties.setProperty("db.password", old.getProperty(DB_PASSWORD_KEY).trim());
    }
    if (old.containsKey(DB_URL_PROPERTIES_KEY)) {
      properties.setProperty("db.properties", old.getProperty(DB_URL_PROPERTIES_KEY).trim());
    }

    // xmlrpc
    if (old.containsKey(XMLRPC_SERVER_PORT_KEY)) {
      properties.setProperty("xmlrpc.port", old.getProperty(XMLRPC_SERVER_PORT_KEY).trim());
    }
    if (old.containsKey(XMLRPC_MAX_THREAD_KEY)) {
      properties.setProperty("xmlrpc.max_threads", old.getProperty(XMLRPC_MAX_THREAD_KEY).trim());
    }
    if (old.containsKey(XMLRPC_MAX_QUEUE_SIZE_KEY)) {
      properties.setProperty("xmlrpc.max_queue_size",
          old.getProperty(XMLRPC_MAX_QUEUE_SIZE_KEY).trim());
    }

    // rest
    if (old.containsKey(REST_SERVICES_PORT_KEY)) {
      properties.setProperty("rest.port", old.getProperty(REST_SERVICES_PORT_KEY).trim());
    }
    if (old.containsKey(REST_SERVICES_MAX_THREAD)) {
      properties.setProperty("rest.max_threads", old.getProperty(REST_SERVICES_MAX_THREAD).trim());
    }
    if (old.containsKey(REST_SERVICES_MAX_QUEUE_SIZE)) {
      properties.setProperty("rest.max_queue_size",
          old.getProperty(REST_SERVICES_MAX_QUEUE_SIZE).trim());
    }

    // security
    if (old.containsKey(XMLRPC_SECURITY_ENABLED_KEY)) {
      properties.setProperty("security.enabled", old.getProperty(XMLRPC_SECURITY_ENABLED_KEY));
    }
    if (old.containsKey(XMLRPC_SECURITY_TOKEN_KEY)) {
      properties.setProperty("security.token", old.getProperty(XMLRPC_SECURITY_TOKEN_KEY).trim());
    }

    // du
    if (old.containsKey(DISKUSAGE_SERVICE_ENABLED)) {
      properties.setProperty("du.enabled", old.getProperty(DISKUSAGE_SERVICE_ENABLED));
    }
    if (old.containsKey(DISKUSAGE_SERVICE_INITIAL_DELAY)) {
      properties.setProperty("du.initial_delay", old.getProperty(DISKUSAGE_SERVICE_INITIAL_DELAY));
    }
    if (old.containsKey(DISKUSAGE_SERVICE_TASKS_PARALLEL)) {
      properties.setProperty("du.parallel_tasks_enabled",
          old.getProperty(DISKUSAGE_SERVICE_TASKS_PARALLEL));
    }
    if (old.containsKey(DISKUSAGE_SERVICE_TASKS_INTERVAL)) {
      properties.setProperty("du.tasks_interval",
          old.getProperty(DISKUSAGE_SERVICE_TASKS_INTERVAL));
    }

    // sanity check
    if (old.containsKey(SANITY_CHECK_ENABLED_KEY)) {
      properties.setProperty("sanity_checks_enabled", old.getProperty(SANITY_CHECK_ENABLED_KEY));
    }

    // ls
    if (old.containsKey(LS_MAX_NUMBER_OF_ENTRY_KEY)) {
      properties.setProperty("synch_ls.max_entries", old.getProperty(LS_MAX_NUMBER_OF_ENTRY_KEY));
    }
    if (old.containsKey(LS_ALL_LEVEL_RECURSIVE_KEY)) {
      properties.setProperty("synch_ls.default_all_level_recursive",
          old.getProperty(LS_ALL_LEVEL_RECURSIVE_KEY));
    }
    if (old.containsKey(LS_NUM_OF_LEVELS_KEY)) {
      properties.setProperty("synch_ls.default_num_levels", old.getProperty(LS_NUM_OF_LEVELS_KEY));
    }
    if (old.containsKey(LS_OFFSET_KEY)) {
      properties.setProperty("synch_ls.default_offset", old.getProperty(LS_OFFSET_KEY));
    }

    // directories
    if (old.containsKey(AUTOMATIC_DIRECTORY_CREATION_KEY)) {
      properties.setProperty("directories.enable_automatic_creation",
          old.getProperty(AUTOMATIC_DIRECTORY_CREATION_KEY));
    }
    if (old.containsKey(ENABLE_WRITE_PERM_ON_DIRECTORY_KEY)) {
      properties.setProperty("directories.enable_writeperm_on_creation",
          old.getProperty(ENABLE_WRITE_PERM_ON_DIRECTORY_KEY));
    }

    // files
    if (old.containsKey(FILE_DEFAULT_SIZE_KEY)) {
      properties.setProperty("files.default_size", old.getProperty(FILE_DEFAULT_SIZE_KEY));
    }
    if (old.containsKey(FILE_LIFETIME_DEFAULT_KEY)) {
      properties.setProperty("files.default_lifetime", old.getProperty(FILE_LIFETIME_DEFAULT_KEY));
    }
    if (old.containsKey(DEFAULT_OVERWRITE_MODE_KEY)) {
      properties.setProperty("files.default_overwrite",
          old.getProperty(DEFAULT_OVERWRITE_MODE_KEY).trim());
    }
    if (old.containsKey(DEFAULT_FILE_STORAGE_TYPE_KEY)) {
      properties.setProperty("files.default_storagetype",
          old.getProperty(DEFAULT_FILE_STORAGE_TYPE_KEY).trim());
    }

    // extraslashes
    if (old.containsKey(EXTRA_SLASHES_FOR_FILE_TURL_KEY)) {
      properties.setProperty("extraslashes.file",
          old.getProperty(EXTRA_SLASHES_FOR_FILE_TURL_KEY).trim());
    }
    if (old.containsKey(EXTRA_SLASHES_FOR_RFIO_TURL_KEY)) {
      properties.setProperty("extraslashes.rfio",
          old.getProperty(EXTRA_SLASHES_FOR_RFIO_TURL_KEY).trim());
    }
    if (old.containsKey(EXTRA_SLASHES_FOR_ROOT_TURL_KEY)) {
      properties.setProperty("extraslashes.root",
          old.getProperty(EXTRA_SLASHES_FOR_ROOT_TURL_KEY).trim());
    }
    if (old.containsKey(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY)) {
      properties.setProperty("extraslashes.gsiftp",
          old.getProperty(EXTRA_SLASHES_FOR_GSIFTP_TURL_KEY).trim());
    }

    // skip ptg acl
    if (old.containsKey(PTG_SKIP_ACL_SETUP)) {
      properties.setProperty("skip_ptg_acl_setup", old.getProperty(PTG_SKIP_ACL_SETUP));
    }

    // hearthbeat
    if (old.containsKey(BOOK_KEEPING_ENABLED_KEY)) {
      properties.setProperty("hearthbeat.bookkeeping_enabled",
          old.getProperty(BOOK_KEEPING_ENABLED_KEY));
    }
    if (old.containsKey(PERFORMANCE_MEASURING_KEY)) {
      properties.setProperty("hearthbeat.performance_measuring_enabled",
          old.getProperty(PERFORMANCE_MEASURING_KEY));
    }
    if (old.containsKey(HEARTHBEAT_PERIOD_KEY)) {
      properties.setProperty("hearthbeat.period", old.getProperty(HEARTHBEAT_PERIOD_KEY));
    }
    if (old.containsKey(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY)) {
      properties.setProperty("hearthbeat.performance_logbook_time_interval",
          old.getProperty(PERFORMANCE_LOGBOOK_TIME_INTERVAL_KEY));
    }
    if (old.containsKey(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY)) {
      properties.setProperty("hearthbeat.performance_glance_time_interval",
          old.getProperty(PERFORMANCE_GLANCE_TIME_INTERVAL_KEY));
    }

    // requests picker
    if (old.containsKey(PICKING_INITIAL_DELAY_KEY)) {
      properties.setProperty("requests_picker_agent.delay",
          old.getProperty(PICKING_INITIAL_DELAY_KEY));
    }
    if (old.containsKey(PICKING_TIME_INTERVAL_KEY)) {
      properties.setProperty("requests_picker_agent.interval",
          old.getProperty(PICKING_TIME_INTERVAL_KEY));
    }
    if (old.containsKey(PICKING_MAX_BATCH_SIZE_KEY)) {
      properties.setProperty("requests_picker_agent.max_fetched_size",
          old.getProperty(PICKING_MAX_BATCH_SIZE_KEY));
    }

    // requests scheduler
    if (old.containsKey(CORE_POOL_SIZE_KEY)) {
      properties.setProperty("requests_scheduler.core_pool_size",
          old.getProperty(CORE_POOL_SIZE_KEY));
    }
    if (old.containsKey(MAX_POOL_SIZE_KEY)) {
      properties.setProperty("requests_scheduler.max_pool_size",
          old.getProperty(MAX_POOL_SIZE_KEY));
    }
    if (old.containsKey(QUEUE_SIZE_KEY)) {
      properties.setProperty("requests_scheduler.queue_size", old.getProperty(QUEUE_SIZE_KEY));
    }

    // ptp requests scheduler
    if (old.containsKey(PTP_CORE_POOL_SIZE_KEY)) {
      properties.setProperty("ptp_scheduler.core_pool_size",
          old.getProperty(PTP_CORE_POOL_SIZE_KEY));
    }
    if (old.containsKey(PTP_MAX_POOL_SIZE_KEY)) {
      properties.setProperty("ptp_scheduler.max_pool_size", old.getProperty(PTP_MAX_POOL_SIZE_KEY));
    }
    if (old.containsKey(PTP_QUEUE_SIZE_KEY)) {
      properties.setProperty("ptp_scheduler.queue_size", old.getProperty(PTP_QUEUE_SIZE_KEY));
    }

    // ptg requests scheduler
    if (old.containsKey(PTG_CORE_POOL_SIZE_KEY)) {
      properties.setProperty("ptg_scheduler.core_pool_size",
          old.getProperty(PTG_CORE_POOL_SIZE_KEY));
    }
    if (old.containsKey(PTG_MAX_POOL_SIZE_KEY)) {
      properties.setProperty("ptg_scheduler.max_pool_size", old.getProperty(PTG_MAX_POOL_SIZE_KEY));
    }
    if (old.containsKey(PTG_QUEUE_SIZE_KEY)) {
      properties.setProperty("ptg_scheduler.queue_size", old.getProperty(PTG_QUEUE_SIZE_KEY));
    }

    // bol requests scheduler
    if (old.containsKey(BOL_CORE_POOL_SIZE_KEY)) {
      properties.setProperty("bol_scheduler.core_pool_size",
          old.getProperty(BOL_CORE_POOL_SIZE_KEY));
    }
    if (old.containsKey(BOL_MAX_POOL_SIZE_KEY)) {
      properties.setProperty("bol_scheduler.max_pool_size", old.getProperty(BOL_MAX_POOL_SIZE_KEY));
    }
    if (old.containsKey(BOL_QUEUE_SIZE_KEY)) {
      properties.setProperty("bol_scheduler.queue_size", old.getProperty(BOL_QUEUE_SIZE_KEY));
    }

    // pin lifetime
    if (old.containsKey(PIN_LIFETIME_DEFAULT_KEY)) {
      properties.setProperty("pinlifetime.default", old.getProperty(PIN_LIFETIME_DEFAULT_KEY));
    }
    if (old.containsKey(PIN_LIFETIME_MAXIMUM_KEY)) {
      properties.setProperty("pinlifetime.maximum", old.getProperty(PIN_LIFETIME_MAXIMUM_KEY));
    }

    // storage spaces agent
    if (old.containsKey(CLEANING_INITIAL_DELAY_KEY)) {
      properties.setProperty("expired_spaces_agent.delay",
          old.getProperty(CLEANING_INITIAL_DELAY_KEY));
    }
    if (old.containsKey(CLEANING_TIME_INTERVAL_KEY)) {
      properties.setProperty("expired_spaces_agent.interval",
          old.getProperty(CLEANING_TIME_INTERVAL_KEY));
    }

    // in progress requests agent
    if (old.containsKey(TRANSIT_INITIAL_DELAY_KEY)) {
      properties.setProperty("inprogress_requests_agent.delay",
          old.getProperty(TRANSIT_INITIAL_DELAY_KEY));
    }
    if (old.containsKey(TRANSIT_TIME_INTERVAL_KEY)) {
      properties.setProperty("inprogress_requests_agent.interval",
          old.getProperty(TRANSIT_TIME_INTERVAL_KEY));
    }
    if (old.containsKey(EXPIRED_INPROGRESS_PTP_TIME_KEY)) {
      properties.setProperty("inprogress_requests_agent.ptp_expiration_time",
          old.getProperty(EXPIRED_INPROGRESS_PTP_TIME_KEY));
    }

    // completed requests agent
    if (old.containsKey(EXPIRED_REQUEST_PURGING_KEY)) {
      properties.setProperty("completed_requests_agent.enabled",
          old.getProperty(EXPIRED_REQUEST_PURGING_KEY));
    }
    if (old.containsKey(EXPIRED_REQUEST_TIME_KEY)) {
      properties.setProperty("completed_requests_agent.purge_age",
          old.getProperty(EXPIRED_REQUEST_TIME_KEY));
    }
    if (old.containsKey(PURGE_BATCH_SIZE_KEY)) {
      properties.setProperty("completed_requests_agent.purge_size",
          old.getProperty(PURGE_BATCH_SIZE_KEY));
    }
    if (old.containsKey(REQUEST_PURGER_DELAY_KEY)) {
      properties.setProperty("completed_requests_agent.delay",
          old.getProperty(REQUEST_PURGER_DELAY_KEY));
    }
    if (old.containsKey(REQUEST_PURGER_PERIOD_KEY)) {
      properties.setProperty("completed_requests_agent.interval",
          old.getProperty(REQUEST_PURGER_PERIOD_KEY));
    }

    // others
    if (old.containsKey(HTTP_TURL_PREFIX)) {
      properties.setProperty("http_turl_prefix", old.getProperty(HTTP_TURL_PREFIX));
    }
    if (old.containsKey(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY)) {
      properties.setProperty("server_pool_status_check_timeout",
          old.getProperty(SERVER_POOL_STATUS_CHECK_TIMEOUT_KEY));
    }
    if (old.containsKey(MAX_LOOP_KEY)) {
      properties.setProperty("abort_maxloop", old.getProperty(MAX_LOOP_KEY));
    }
    if (old.containsKey(GPFS_QUOTA_REFRESH_PERIOD_KEY)) {
      properties.setProperty("info_quota_refresh_period",
          old.getProperty(GPFS_QUOTA_REFRESH_PERIOD_KEY));
    }
    if (old.containsKey(PING_VALUES_PROPERTIES_FILENAME_KEY)) {
      properties.setProperty("ping_properties_filename",
          old.getProperty(PING_VALUES_PROPERTIES_FILENAME_KEY).trim());
    }

    log.debug("This is your generated configuration:");
    Enumeration<Object> keys = properties.keys();
    FileWriter fw = new FileWriter(target);
    fw.write(String.format("# Configuration generated from '%s' %n", source.toString()));
    fw.write(String.format("# %s %n", new Date().toString()));
    while (keys.hasMoreElements()) {
      String key = String.valueOf(keys.nextElement());
      String value = String.valueOf(properties.get(key));
      //log.debug("{}: {}", key, value);
      fw.write(String.format("%s: %s%n", key, value));
    }
    fw.close();
  }

}
