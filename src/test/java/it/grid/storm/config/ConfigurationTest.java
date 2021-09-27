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
import static it.grid.storm.config.model.v2.StormProperties.VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;

import com.google.common.collect.Lists;

import it.grid.storm.config.converter.StormPropertiesConversionException;
import it.grid.storm.config.model.v2.OverwriteMode;
import it.grid.storm.config.model.v2.StorageType;
import it.grid.storm.namespace.model.Authority;

public class ConfigurationTest {

  @Test
  public void testLoadConfiguration() throws IOException {

    Configuration.init("src/test/resources/storm.properties");
    Configuration config = Configuration.getInstance();

    assertEquals(VERSION, config.getVersion());
    assertFalse(config.getManagedSrmEndpoints().isEmpty());
    assertEquals(2, config.getManagedSrmEndpoints().size());
    assertEquals("storm.example", config.getManagedSrmEndpoints().get(0).getServiceHostname());
    assertEquals(8444, config.getManagedSrmEndpoints().get(0).getServicePort());
    assertEquals("alias.example", config.getManagedSrmEndpoints().get(1).getServiceHostname());
    assertEquals(8445, config.getManagedSrmEndpoints().get(1).getServicePort());
    assertEquals("storm.example", config.getDbHostname());
    assertEquals("test", config.getDbUsername());
    assertEquals("secret", config.getDbPassword());
    assertEquals(3308, config.getDbPort());
    assertEquals("test", config.getDbProperties());
    assertEquals(200, config.getDbPoolSize());
    assertEquals(1200, config.getDbPoolMaxWaitMillis());
    assertEquals(50, config.getDbPoolMinIdle());
    assertEquals(false, config.isDbPoolTestOnBorrow());
    assertEquals(false, config.isDbPoolTestWhileIdle());
    assertEquals(9999, config.getRestServicesPort());
    assertEquals(150, config.getRestServicesMaxThreads());
    assertEquals(1500, config.getRestServicesMaxQueueSize());
    assertEquals(9090, config.getXmlRpcServerPort());
    assertEquals(512, config.getXmlrpcMaxThreads());
    assertEquals(2000, config.getXmlrpcMaxQueueSize());
    assertEquals(true, config.isSecurityEnabled());
    assertEquals("ilovejava", config.getSecurityToken());
    assertEquals(true, config.isDiskUsageServiceEnabled());
    assertEquals(true, config.isDiskUsageServiceTasksParallel());
    assertEquals(120, config.getDiskUsageServiceInitialDelay());
    assertEquals(200000, config.getDiskUsageServiceTasksInterval());
    assertEquals(20, config.getInProgressAgentInitialDelay());
    assertEquals(400, config.getInProgressAgentInterval());
    assertEquals(333000, config.getInProgressPtpExpirationTime());
    assertEquals(20, config.getExpiredSpacesAgentInitialDelay());
    assertEquals(400, config.getExpiredSpacesAgentInterval());
    assertEquals(true, config.isCompletedRequestsAgentEnabled());
    assertEquals(20, config.getCompletedRequestsAgentDelay());
    assertEquals(400, config.getCompletedRequestsAgentPeriod());
    assertEquals(1800, config.getCompletedRequestsAgentPurgeSize());
    assertEquals(22200, config.getCompletedRequestsAgentPurgeAge());
    assertEquals(10, config.getCorePoolSize());
    assertEquals(50, config.getMaxPoolSize());
    assertEquals(2000, config.getQueueSize());
    assertEquals(50, config.getPtPCorePoolSize());
    assertEquals(200, config.getPtPMaxPoolSize());
    assertEquals(1000, config.getPtPQueueSize());
    assertEquals(50, config.getPtGCorePoolSize());
    assertEquals(200, config.getPtGMaxPoolSize());
    assertEquals(2000, config.getPtGQueueSize());
    assertEquals(50, config.getBoLCorePoolSize());
    assertEquals(200, config.getBoLMaxPoolSize());
    assertEquals(2000, config.getBoLQueueSize());
    assertEquals(10, config.getRequestsPickerAgentInitialDelay());
    assertEquals(20, config.getRequestsPickerAgentInterval());
    assertEquals(1000, config.getRequestsPickerAgentMaxFetchedSize());
    assertEquals(false, config.isSanityCheckEnabled());
    assertEquals("/file", config.getExtraSlashesForFileTURL());
    assertEquals("/rfio", config.getExtraSlashesForRFIOTURL());
    assertEquals("/root", config.getExtraSlashesForRootTURL());
    assertEquals("/gsiftp", config.getExtraSlashesForGsiFTPTURL());
    assertEquals(true, config.isLsDefaultAllLevelRecursive());
    assertEquals(2, config.getLsDefaultNumOfLevels());
    assertEquals(1, config.getLsDefaultOffset());
    assertEquals(3000, config.getLsMaxNumberOfEntry());
    assertEquals(300000, config.getPinLifetimeDefault());
    assertEquals(18000000, config.getPinLifetimeMaximum());
    assertEquals(true, config.isSkipPtgACLSetup());
    assertEquals(100000, config.getFileDefaultSize());
    assertEquals(300000, config.getFileLifetimeDefault());
    assertEquals("N", config.getDefaultOverwriteMode().name());
    assertEquals("P", config.getDefaultFileStorageType().name());
    assertEquals(true, config.isAutomaticDirectoryCreationEnabled());
    assertEquals(true, config.isDirectoryWritePermOnCreationEnabled());
    assertEquals(true, config.isHearthbeatBookkeepingEnabled());
    assertEquals(true, config.isHearthbeatPerformanceMeasuringEnabled());
    assertEquals(30, config.getHearthbeatPeriod());
    assertEquals(10, config.getHearthbeatPerformanceLogbookTimeInterval());
    assertEquals(10, config.getHearthbeatPerformanceGlanceTimeInterval());
    assertEquals(900, config.getGPFSQuotaRefreshPeriod());
    assertEquals("/", config.getHTTPTURLPrefix());
    assertEquals(20000, config.getServerPoolStatusCheckTimeout());
    assertEquals(10, config.getMaxLoop());
    assertEquals("ping-values.properties", config.getPingValuesPropertiesFilename());
  }

  @Test
  public void testLoadEmptyConfiguration() throws IOException {

    Configuration.init("src/test/resources/empty.properties");
    Configuration config = Configuration.getInstance();
    String hostname = InetAddress.getLocalHost().getHostName();

    assertEquals(VERSION, config.getVersion());
    assertFalse(config.getManagedSrmEndpoints().isEmpty());
    assertEquals(1, config.getManagedSrmEndpoints().size());
    assertEquals(hostname, config.getManagedSrmEndpoints().get(0).getServiceHostname());
    assertEquals(8444, config.getManagedSrmEndpoints().get(0).getServicePort());
    assertEquals(hostname, config.getDbHostname());
    assertEquals(DB_USERNAME, config.getDbUsername());
    assertEquals(DB_PASSWORD, config.getDbPassword());
    assertEquals(DB_PORT, config.getDbPort());
    assertEquals(DB_PROPERTIES, config.getDbProperties());
    assertEquals(DB_POOL_SIZE, config.getDbPoolSize());
    assertEquals(DB_POOL_MAX_WAIT_MILLIS, config.getDbPoolMaxWaitMillis());
    assertEquals(DB_POOL_MIN_IDLE, config.getDbPoolMinIdle());
    assertEquals(DB_POOL_TEST_ON_BORROW, config.isDbPoolTestOnBorrow());
    assertEquals(DB_POOL_TEST_WHILE_IDLE, config.isDbPoolTestWhileIdle());
    assertEquals(REST_SERVICES_PORT, config.getRestServicesPort());
    assertEquals(REST_SERVICES_MAX_THREADS, config.getRestServicesMaxThreads());
    assertEquals(REST_SERVICES_MAX_QUEUE_SIZE, config.getRestServicesMaxQueueSize());
    assertEquals(XMLRPC_SERVER_PORT, config.getXmlRpcServerPort());
    assertEquals(XMLRPC_MAX_THREADS, config.getXmlrpcMaxThreads());
    assertEquals(XMLRPC_MAX_QUEUE_SIZE, config.getXmlrpcMaxQueueSize());
    assertEquals(SECURITY_ENABLED, config.isSecurityEnabled());
    assertEquals(SECURITY_TOKEN, config.getSecurityToken());
    assertEquals(DISKUSAGE_SERVICE_ENABLED, config.isDiskUsageServiceEnabled());
    assertEquals(DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED,
        config.isDiskUsageServiceTasksParallel());
    assertEquals(DISKUSAGE_SERVICE_INITIAL_DELAY, config.getDiskUsageServiceInitialDelay());
    assertEquals(DISKUSAGE_SERVICE_TASKS_INTERVAL, config.getDiskUsageServiceTasksInterval());
    assertEquals(INPROGRESS_REQUESTS_AGENT_DELAY, config.getInProgressAgentInitialDelay());
    assertEquals(INPROGRESS_REQUESTS_AGENT_INTERVAL, config.getInProgressAgentInterval());
    assertEquals(INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME,
        config.getInProgressPtpExpirationTime());
    assertEquals(EXPIRED_SPACES_AGENT_DELAY, config.getExpiredSpacesAgentInitialDelay());
    assertEquals(EXPIRED_SPACES_AGENT_INTERVAL, config.getExpiredSpacesAgentInterval());
    assertEquals(COMPLETED_REQUESTS_AGENT_ENABLED, config.isCompletedRequestsAgentEnabled());
    assertEquals(COMPLETED_REQUESTS_AGENT_DELAY, config.getCompletedRequestsAgentDelay());
    assertEquals(COMPLETED_REQUESTS_AGENT_INTERVAL, config.getCompletedRequestsAgentPeriod());
    assertEquals(COMPLETED_REQUESTS_AGENT_PURGE_SIZE, config.getCompletedRequestsAgentPurgeSize());
    assertEquals(COMPLETED_REQUESTS_AGENT_PURGE_AGE, config.getCompletedRequestsAgentPurgeAge());
    assertEquals(REQUESTS_SCHEDULER_CORE_POOL_SIZE, config.getCorePoolSize());
    assertEquals(REQUESTS_SCHEDULER_MAX_POOL_SIZE, config.getMaxPoolSize());
    assertEquals(REQUESTS_SCHEDULER_QUEUE_SIZE, config.getQueueSize());
    assertEquals(PTP_SCHEDULER_CORE_POOL_SIZE, config.getPtPCorePoolSize());
    assertEquals(PTP_SCHEDULER_MAX_POOL_SIZE, config.getPtPMaxPoolSize());
    assertEquals(PTP_SCHEDULER_QUEUE_SIZE, config.getPtPQueueSize());
    assertEquals(PTG_SCHEDULER_CORE_POOL_SIZE, config.getPtGCorePoolSize());
    assertEquals(PTG_SCHEDULER_MAX_POOL_SIZE, config.getPtGMaxPoolSize());
    assertEquals(PTG_SCHEDULER_QUEUE_SIZE, config.getPtGQueueSize());
    assertEquals(BOL_SCHEDULER_CORE_POOL_SIZE, config.getBoLCorePoolSize());
    assertEquals(BOL_SCHEDULER_MAX_POOL_SIZE, config.getBoLMaxPoolSize());
    assertEquals(BOL_SCHEDULER_QUEUE_SIZE, config.getBoLQueueSize());
    assertEquals(REQUESTS_PICKER_AGENT_DELAY, config.getRequestsPickerAgentInitialDelay());
    assertEquals(REQUESTS_PICKER_AGENT_INTERVAL, config.getRequestsPickerAgentInterval());
    assertEquals(REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE,
        config.getRequestsPickerAgentMaxFetchedSize());
    assertEquals(SANITY_CHECK_ENABLED, config.isSanityCheckEnabled());
    assertEquals(EXTRA_SLASHES_FOR_FILE_TURL, config.getExtraSlashesForFileTURL());
    assertEquals(EXTRA_SLASHES_FOR_RFIO_TURL, config.getExtraSlashesForRFIOTURL());
    assertEquals(EXTRA_SLASHES_FOR_ROOT_TURL, config.getExtraSlashesForRootTURL());
    assertEquals(EXTRA_SLASHES_FOR_GSIFTP_TURL, config.getExtraSlashesForGsiFTPTURL());
    assertEquals(LS_DEFAULT_ALL_LEVEL_RECURSIVE, config.isLsDefaultAllLevelRecursive());
    assertEquals(LS_DEFAULT_NUM_OF_LEVELS, config.getLsDefaultNumOfLevels());
    assertEquals(LS_DEFAULT_OFFSET, config.getLsDefaultOffset());
    assertEquals(LS_MAX_NUMBER_OF_ENTRY, config.getLsMaxNumberOfEntry());
    assertEquals(PIN_LIFETIME_DEFAULT, config.getPinLifetimeDefault());
    assertEquals(PIN_LIFETIME_MAXIMUM, config.getPinLifetimeMaximum());
    assertEquals(PTG_SKIP_ACL_SETUP, config.isSkipPtgACLSetup());
    assertEquals(FILE_DEFAULT_SIZE, config.getFileDefaultSize());
    assertEquals(FILE_LIFETIME_DEFAULT, config.getFileLifetimeDefault());
    assertEquals(DEFAULT_OVERWRITE_MODE, config.getDefaultOverwriteMode().name());
    assertEquals(DEFAULT_FILE_STORAGE_TYPE, config.getDefaultFileStorageType().name());
    assertEquals(AUTOMATIC_DIRECTORY_CREATION, config.isAutomaticDirectoryCreationEnabled());
    assertEquals(ENABLE_WRITE_PERM_ON_DIRECTORY, config.isDirectoryWritePermOnCreationEnabled());
    assertEquals(BOOK_KEEPING_ENABLED, config.isHearthbeatBookkeepingEnabled());
    assertEquals(PERFORMANCE_MEASURING, config.isHearthbeatPerformanceMeasuringEnabled());
    assertEquals(HEARTHBEAT_PERIOD, config.getHearthbeatPeriod());
    assertEquals(PERFORMANCE_LOGBOOK_TIME_INTERVAL,
        config.getHearthbeatPerformanceLogbookTimeInterval());
    assertEquals(PERFORMANCE_GLANCE_TIME_INTERVAL,
        config.getHearthbeatPerformanceGlanceTimeInterval());
    assertEquals(GPFS_QUOTA_REFRESH_PERIOD, config.getGPFSQuotaRefreshPeriod());
    assertEquals(HTTP_TURL_PREFIX, config.getHTTPTURLPrefix());
    assertEquals(SERVER_POOL_STATUS_CHECK_TIMEOUT, config.getServerPoolStatusCheckTimeout());
    assertEquals(MAX_LOOP, config.getMaxLoop());
    assertEquals(PING_VALUES_PROPERTIES_FILENAME, config.getPingValuesPropertiesFilename());
  }

  @Test
  public void testLoadedConfigurationFromOldProperties()
      throws IOException, StormPropertiesConversionException {

    Configuration.init("src/test/resources/v1.properties");
    Configuration config = Configuration.getInstance();

    // SRM service
    assertEquals("fe.example.org", config.getSrmServiceHostname());
    assertEquals(8444, config.getSrmServicePort());
    assertEquals(config.getManagedSrmEndpoints(),
        Lists.newArrayList(new Authority("fe.example.org", 8444),
            new Authority("fe-01.example.org", 8444), new Authority("fe-02.example.org", 8444)));
    // database
    assertEquals("storm", config.getDbUsername());
    assertEquals("my-secret-password", config.getDbPassword());
    assertEquals("prop=1", config.getDbProperties());
    // not converted properties
    assertEquals(DB_PORT, config.getDbPort());
    assertEquals(DB_POOL_SIZE, config.getDbPoolSize());
    assertEquals(DB_POOL_MIN_IDLE, config.getDbPoolMinIdle());
    assertEquals(DB_POOL_MAX_WAIT_MILLIS, config.getDbPoolMaxWaitMillis());
    assertEquals(DB_POOL_TEST_ON_BORROW, config.isDbPoolTestOnBorrow());
    assertEquals(DB_POOL_TEST_WHILE_IDLE, config.isDbPoolTestWhileIdle());
    // REST
    assertEquals(9999, config.getRestServicesPort());
    assertEquals(512, config.getRestServicesMaxThreads());
    assertEquals(2000, config.getRestServicesMaxQueueSize());
    // sanity check
    assertEquals(true, config.isSanityCheckEnabled());
    // xmlrpc
    assertEquals(8081, config.getXmlRpcServerPort());
    assertEquals(512, config.getXmlrpcMaxThreads());
    assertEquals(2000, config.getXmlrpcMaxQueueSize());
    assertEquals(true, config.isSecurityEnabled());
    assertEquals("ilovejava", config.getSecurityToken());
    // disk usage
    assertEquals(true, config.isDiskUsageServiceEnabled());
    assertEquals(60, config.getDiskUsageServiceInitialDelay());
    assertEquals(360, config.getDiskUsageServiceTasksInterval());
    assertEquals(true, config.isDiskUsageServiceTasksParallel());
    //
    assertEquals(10, config.getExpiredSpacesAgentInitialDelay());
    assertEquals(300, config.getExpiredSpacesAgentInterval());
    //
    assertEquals(2000000, config.getFileDefaultSize());
    assertEquals(300000, config.getFileLifetimeDefault());
    assertEquals(310000, config.getPinLifetimeDefault());
    assertEquals(1900000, config.getPinLifetimeMaximum());

    assertEquals(15, config.getRequestsPickerAgentInitialDelay());
    assertEquals(25, config.getRequestsPickerAgentInterval());
    assertEquals(150, config.getRequestsPickerAgentMaxFetchedSize());
    // LS
    assertEquals(3000, config.getLsMaxNumberOfEntry());
    assertEquals(true, config.isLsDefaultAllLevelRecursive());
    assertEquals(3, config.getLsDefaultNumOfLevels());
    assertEquals(2, config.getLsDefaultOffset());
    //
    assertEquals(60, config.getPtPCorePoolSize());
    assertEquals(300, config.getPtPMaxPoolSize());
    assertEquals(2000, config.getPtPQueueSize());

    assertEquals(70, config.getPtGCorePoolSize());
    assertEquals(400, config.getPtGMaxPoolSize());
    assertEquals(3000, config.getPtGQueueSize());

    assertEquals(40, config.getBoLCorePoolSize());
    assertEquals(100, config.getBoLMaxPoolSize());
    assertEquals(1000, config.getBoLQueueSize());

    assertEquals(20, config.getCorePoolSize());
    assertEquals(60, config.getMaxPoolSize());
    assertEquals(3000, config.getQueueSize());

    assertEquals(true, config.isAutomaticDirectoryCreationEnabled());
    assertEquals(true, config.isDirectoryWritePermOnCreationEnabled());

    assertEquals(OverwriteMode.N, config.getDefaultOverwriteMode());
    assertEquals(StorageType.P, config.getDefaultFileStorageType());

    assertEquals(false, config.isCompletedRequestsAgentEnabled());
    assertEquals(100, config.getCompletedRequestsAgentDelay());
    assertEquals(600, config.getCompletedRequestsAgentPeriod());
    assertEquals(1000, config.getCompletedRequestsAgentPurgeSize());
    assertEquals(7200, config.getCompletedRequestsAgentPurgeAge());

    assertEquals(60, config.getInProgressAgentInitialDelay());
    assertEquals(600, config.getInProgressAgentInterval());
    assertEquals(7000, config.getInProgressPtpExpirationTime());

    assertEquals("/file", config.getExtraSlashesForFileTURL());
    assertEquals("/rfio", config.getExtraSlashesForRFIOTURL());
    assertEquals("/gsiftp", config.getExtraSlashesForGsiFTPTURL());
    assertEquals("/root", config.getExtraSlashesForRootTURL());

    assertEquals("ping-values.properties", config.getPingValuesPropertiesFilename());

    assertEquals(30, config.getHearthbeatPeriod());
    assertEquals(10, config.getHearthbeatPerformanceGlanceTimeInterval());
    assertEquals(10, config.getHearthbeatPerformanceLogbookTimeInterval());
    assertEquals(true, config.isHearthbeatBookkeepingEnabled());
    assertEquals(true, config.isHearthbeatPerformanceMeasuringEnabled());

    assertEquals(10, config.getMaxLoop());

    assertEquals(900, config.getGPFSQuotaRefreshPeriod());

    assertEquals(20000, config.getServerPoolStatusCheckTimeout());

    assertEquals(false, config.isSkipPtgACLSetup());

    assertEquals("/", config.getHTTPTURLPrefix());

    // check new file created
    File exported = new File("src/test/resources/v1.properties.new");
    assertEquals(exported.exists(), true);
    // clear file
    exported.delete();

  }

}
