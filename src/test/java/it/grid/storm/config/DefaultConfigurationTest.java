package it.grid.storm.config;

import static it.grid.storm.config.ConfigurationDefaults.AUTOMATIC_DIRECTORY_CREATION;
import static it.grid.storm.config.ConfigurationDefaults.BOL_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_SCHEDULER_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOOK_KEEPING_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_PURGE_AGE;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_PURGE_SIZE;
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
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_SPACES_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_SPACES_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_FILE_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_GSIFTP_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_RFIO_TURL;
import static it.grid.storm.config.ConfigurationDefaults.EXTRA_SLASHES_FOR_ROOT_TURL;
import static it.grid.storm.config.ConfigurationDefaults.FILE_DEFAULT_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.FILE_LIFETIME_DEFAULT;
import static it.grid.storm.config.ConfigurationDefaults.GPFS_QUOTA_REFRESH_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HEARTHBEAT_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HTTP_TURL_PREFIX;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_ALL_LEVEL_RECURSIVE;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_NUM_OF_LEVELS;
import static it.grid.storm.config.ConfigurationDefaults.LS_DEFAULT_OFFSET;
import static it.grid.storm.config.ConfigurationDefaults.LS_MAX_NUMBER_OF_ENTRY;
import static it.grid.storm.config.ConfigurationDefaults.MAX_LOOP;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_GLANCE_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_LOGBOOK_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_MEASURING;
import static it.grid.storm.config.ConfigurationDefaults.PING_VALUES_PROPERTIES_FILENAME;
import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_DEFAULT;
import static it.grid.storm.config.ConfigurationDefaults.PIN_LIFETIME_MAXIMUM;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SCHEDULER_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.PTP_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTP_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTP_SCHEDULER_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_SCHEDULER_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_PORT;
import static it.grid.storm.config.ConfigurationDefaults.SANITY_CHECK_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SECURITY_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.SECURITY_TOKEN;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SERVER_PORT;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import it.grid.storm.config.model.OverwriteMode;
import it.grid.storm.config.model.StorageType;

public class DefaultConfigurationTest {

  @Test
  public void testLoadedConfigurationFromOldProperties() throws UnknownHostException {

    Configuration.init("src/test/resources/empty.properties");
    Configuration config = Configuration.getInstance();

    String hostname = InetAddress.getLocalHost().getHostName();

    // SRM service
    assertEquals(config.getSrmServiceHostname(), hostname);
    assertEquals(config.getSrmServicePort(), 8444);
    assertEquals(config.getManagedSrmEndpoints().size(), 1);
    assertEquals(config.getManagedSrmEndpoints().get(0).getServiceHostname(), hostname);
    assertEquals(config.getManagedSrmEndpoints().get(0).getServicePort(), 8444);
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
    assertEquals(config.getXmlRpcSecurityEnabled(), SECURITY_ENABLED);
    assertEquals(config.getXmlRpcToken(), SECURITY_TOKEN);
    // disk usage
    assertEquals(config.getDiskUsageServiceEnabled(), DISKUSAGE_SERVICE_ENABLED);
    assertEquals(config.getDiskUsageServiceInitialDelay(), DISKUSAGE_SERVICE_INITIAL_DELAY);
    assertEquals(config.getDiskUsageServiceTasksInterval(), DISKUSAGE_SERVICE_TASKS_INTERVAL);
    assertEquals(config.getDiskUsageServiceTasksParallel(),
        DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED);
    //
    assertEquals(config.getCleaningInitialDelay(), EXPIRED_SPACES_AGENT_DELAY);
    assertEquals(config.getCleaningTimeInterval(), EXPIRED_SPACES_AGENT_INTERVAL);
    //
    assertEquals(config.getFileDefaultSize(), FILE_DEFAULT_SIZE);
    assertEquals(config.getFileLifetimeDefault(), FILE_LIFETIME_DEFAULT);
    assertEquals(config.getPinLifetimeDefault(), PIN_LIFETIME_DEFAULT);
    assertEquals(config.getPinLifetimeMaximum(), PIN_LIFETIME_MAXIMUM);

    assertEquals(config.getPickingInitialDelay(), REQUESTS_PICKER_AGENT_DELAY);
    assertEquals(config.getPickingTimeInterval(), REQUESTS_PICKER_AGENT_INTERVAL);
    assertEquals(config.getPickingMaxBatchSize(), REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE);
    // LS
    assertEquals(config.getLsMaxNumberOfEntry(), LS_MAX_NUMBER_OF_ENTRY);
    assertEquals(config.getLsAllLevelRecursive(), LS_DEFAULT_ALL_LEVEL_RECURSIVE);
    assertEquals(config.getLsNumOfLevels(), LS_DEFAULT_NUM_OF_LEVELS);
    assertEquals(config.getLsOffset(), LS_DEFAULT_OFFSET);
    //
    assertEquals(config.getPtPCorePoolSize(), PTP_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(config.getPtPMaxPoolSize(), PTP_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(config.getPtPQueueSize(), PTP_SCHEDULER_QUEUE_SIZE);

    assertEquals(config.getPtGCorePoolSize(), PTG_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(config.getPtGMaxPoolSize(), PTG_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(config.getPtGQueueSize(), PTG_SCHEDULER_QUEUE_SIZE);

    assertEquals(config.getBoLCorePoolSize(), BOL_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(config.getBoLMaxPoolSize(), BOL_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(config.getBoLQueueSize(), BOL_SCHEDULER_QUEUE_SIZE);

    assertEquals(config.getCorePoolSize(), REQUESTS_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(config.getMaxPoolSize(), REQUESTS_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(config.getQueueSize(), REQUESTS_SCHEDULER_QUEUE_SIZE);

    assertEquals(config.getAutomaticDirectoryCreation(), AUTOMATIC_DIRECTORY_CREATION);
    assertEquals(config.getEnableWritePermOnDirectory(), ENABLE_WRITE_PERM_ON_DIRECTORY);

    assertEquals(config.getDefaultOverwriteMode(), OverwriteMode.valueOf(DEFAULT_OVERWRITE_MODE));
    assertEquals(config.getDefaultFileStorageType(), StorageType.valueOf(DEFAULT_FILE_STORAGE_TYPE));

    assertEquals(config.getExpiredRequestPurging(), COMPLETED_REQUESTS_AGENT_ENABLED);
    assertEquals(config.getRequestPurgerDelay(), COMPLETED_REQUESTS_AGENT_DELAY);
    assertEquals(config.getRequestPurgerPeriod(), COMPLETED_REQUESTS_AGENT_INTERVAL);
    assertEquals(config.getPurgeBatchSize(), COMPLETED_REQUESTS_AGENT_PURGE_SIZE);
    assertEquals(config.getExpiredRequestTime(), COMPLETED_REQUESTS_AGENT_PURGE_AGE);

    assertEquals(config.getTransitInitialDelay(), INPROGRESS_REQUESTS_AGENT_DELAY);
    assertEquals(config.getTransitTimeInterval(), INPROGRESS_REQUESTS_AGENT_INTERVAL);
    assertEquals(config.getInProgressPtpExpirationTime(), INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME);

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

    // check new file created
    File exported = new File("src/test/resources/empty.properties.new");
    assertEquals(exported.exists(), true);
    // clear file
    exported.delete();

  }
}
