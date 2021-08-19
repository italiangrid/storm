package it.grid.storm.config;

import static it.grid.storm.Main.DEFAULT_REFRESH_RATE;
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
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SERVER_PORT;
import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.model.Authority;

public class DefaultConfigurationTest {

  static {
    Configuration.init("empty.properties", DEFAULT_REFRESH_RATE);
  }

  @Test
  public void testEmptyConfiguration() throws UnknownHostException {

    Configuration config = Configuration.getInstance();
    // local FQDN hostname
    String hostname = InetAddress.getLocalHost().getHostName();
    // SRM service
    assertEquals(config.getSrmServiceHostname(), hostname);
    assertEquals(config.getSrmServicePort(), SERVICE_SRM_PUBLIC_PORT);
    assertEquals(config.getManagedSrmEndpoints(),
        Lists.newArrayList(new Authority(hostname, SERVICE_SRM_PUBLIC_PORT)));
    // database
    assertEquals(config.getDbUsername(), DB_USERNAME);
    assertEquals(config.getDbPassword(), DB_PASSWORD);
    assertEquals(config.getDbProperties(), DB_PROPERTIES);
    assertEquals(config.getDbPort(), DB_PORT);
    assertEquals(config.getDbPoolSize(), DB_POOL_SIZE);
    assertEquals(config.getDbPoolMinIdle(), DB_POOL_MIN_IDLE);
    assertEquals(config.getDbPoolMaxWaitMillis(), DB_POOL_MAX_WAIT_MILLIS);
    assertEquals(config.isDbPoolTestOnBorrow(), DB_POOL_TEST_ON_BORROW);
    assertEquals(config.isDbPoolTestWhileIdle(), DB_POOL_TEST_WHILE_IDLE);
    // REST
    assertEquals(config.getRestServicesPort(), REST_SERVICES_PORT);
    assertEquals(config.getRestServicesMaxThreads(), REST_SERVICES_MAX_THREADS);
    assertEquals(config.getRestServicesMaxQueueSize(), REST_SERVICES_MAX_QUEUE_SIZE);
    // sanity check
    assertEquals(config.getSanityCheckEnabled(), SANITY_CHECK_ENABLED);
    // xmlrpc
    assertEquals(config.getXmlRpcServerPort(), XMLRPC_SERVER_PORT);
    assertEquals(config.getXmlrpcMaxThreads(), XMLRPC_MAX_THREADS);
    assertEquals(config.getXmlrpcMaxQueueSize(), XMLRPC_MAX_QUEUE_SIZE);
    assertEquals(config.getXmlRpcSecurityEnabled(), XMLRPC_SECURITY_ENABLED);
    assertEquals(config.getXmlRpcToken(), null);
    // disk usage
    assertEquals(config.getDiskUsageServiceEnabled(), DISKUSAGE_SERVICE_ENABLED);
    assertEquals(config.getDiskUsageServiceInitialDelay(), DISKUSAGE_SERVICE_INITIAL_DELAY);
    assertEquals(config.getDiskUsageServiceTasksInterval(), DISKUSAGE_SERVICE_TASKS_INTERVAL);
    assertEquals(config.getDiskUsageServiceTasksParallel(),
        DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED);
    //
    assertEquals(config.getCleaningInitialDelay(), CLEANING_INITIAL_DELAY);
    assertEquals(config.getCleaningTimeInterval(), CLEANING_TIME_INTERVAL);
    //
    assertEquals(config.getFileDefaultSize(), FILE_DEFAULT_SIZE);
    assertEquals(config.getFileLifetimeDefault(), FILE_LIFETIME_DEFAULT);
    assertEquals(config.getPinLifetimeDefault(), PIN_LIFETIME_DEFAULT);
    assertEquals(config.getPinLifetimeMaximum(), PIN_LIFETIME_MAXIMUM);
    assertEquals(config.getTransitInitialDelay(), TRANSIT_INITIAL_DELAY);
    assertEquals(config.getTransitTimeInterval(), TRANSIT_TIME_INTERVAL);
    assertEquals(config.getPickingInitialDelay(), PICKING_INITIAL_DELAY);
    assertEquals(config.getPickingTimeInterval(), PICKING_TIME_INTERVAL);
    assertEquals(config.getPickingMaxBatchSize(), PICKING_MAX_BATCH_SIZE);
    // LS
    assertEquals(config.getLsMaxNumberOfEntry(), LS_MAX_NUMBER_OF_ENTRY);
    assertEquals(config.getLsAllLevelRecursive(), LS_DEFAULT_ALL_LEVEL_RECURSIVE);
    assertEquals(config.getLsNumOfLevels(), LS_DEFAULT_NUM_OF_LEVELS);
    assertEquals(config.getLsOffset(), LS_OFFSET);
    //
    assertEquals(config.getPtPCorePoolSize(), PTP_CORE_POOL_SIZE);
    assertEquals(config.getPtPMaxPoolSize(), PTP_MAX_POOL_SIZE);
    assertEquals(config.getPtPQueueSize(), PTP_QUEUE_SIZE);

    assertEquals(config.getPtGCorePoolSize(), PTG_CORE_POOL_SIZE);
    assertEquals(config.getPtGMaxPoolSize(), PTG_MAX_POOL_SIZE);
    assertEquals(config.getPtGQueueSize(), PTG_QUEUE_SIZE);

    assertEquals(config.getBoLCorePoolSize(), BOL_CORE_POOL_SIZE);
    assertEquals(config.getBoLMaxPoolSize(), BOL_MAX_POOL_SIZE);
    assertEquals(config.getBoLQueueSize(), BOL_QUEUE_SIZE);

    assertEquals(config.getCorePoolSize(), CORE_POOL_SIZE);
    assertEquals(config.getMaxPoolSize(), MAX_POOL_SIZE);
    assertEquals(config.getQueueSize(), QUEUE_SIZE);

    assertEquals(config.getAutomaticDirectoryCreation(), AUTOMATIC_DIRECTORY_CREATION);
    assertEquals(config.getEnableWritePermOnDirectory(), ENABLE_WRITE_PERM_ON_DIRECTORY);

    assertEquals(config.getDefaultOverwriteMode(), DEFAULT_OVERWRITE_MODE);
    assertEquals(config.getDefaultFileStorageType(), DEFAULT_FILE_STORAGE_TYPE);

    assertEquals(config.getPurgeBatchSize(), PURGE_BATCH_SIZE);

    assertEquals(config.getExpiredRequestPurging(), EXPIRED_REQUEST_PURGING);
    assertEquals(config.getExpiredRequestTime(), EXPIRED_REQUEST_TIME);
    assertEquals(config.getRequestPurgerDelay(), REQUEST_PURGER_DELAY);
    assertEquals(config.getRequestPurgerPeriod(), REQUEST_PURGER_PERIOD);
    assertEquals(config.getInProgressPtpExpirationTime(), EXPIRED_INPROGRESS_PTP_TIME);

    assertEquals(config.getExtraSlashesForFileTURL(), EXTRA_SLASHES_FOR_FILE_TURL);
    assertEquals(config.getExtraSlashesForRFIOTURL(), EXTRA_SLASHES_FOR_RFIO_TURL);
    assertEquals(config.getExtraSlashesForGsiFTPTURL(), EXTRA_SLASHES_FOR_GSIFTP_TURL);
    assertEquals(config.getExtraSlashesForROOTTURL(), EXTRA_SLASHES_FOR_ROOT_TURL);

    assertEquals(config.getPingValuesPropertiesFilename(), PING_VALUES_PROPERTIES_FILENAME);

    assertEquals(config.getHearthbeatPeriod(), HEARTHBEAT_PERIOD);
    assertEquals(config.getPerformanceGlanceTimeInterval(), PERFORMANCE_GLANCE_TIME_INTERVAL);
    assertEquals(config.getPerformanceLogbookTimeInterval(), PERFORMANCE_LOGBOOK_TIME_INTERVAL);
    assertEquals(config.getPerformanceMeasuring(), PERFORMANCE_MEASURING);
    assertEquals(config.getBookKeepingEnabled(), BOOK_KEEPING_ENABLED);

    assertEquals(config.getMaxLoop(), MAX_LOOP);

    assertEquals(config.getGPFSQuotaRefreshPeriod(), GPFS_QUOTA_REFRESH_PERIOD);

    assertEquals(config.getServerPoolStatusCheckTimeout(), SERVER_POOL_STATUS_CHECK_TIMEOUT);

    assertEquals(config.getPTGSkipACLSetup(), PTG_SKIP_ACL_SETUP);

    assertEquals(config.getHTTPTURLPrefix(), HTTP_TURL_PREFIX);

  }
}
