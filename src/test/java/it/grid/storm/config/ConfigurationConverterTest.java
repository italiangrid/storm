package it.grid.storm.config;

import static it.grid.storm.config.ConfigurationDefaults.BOOK_KEEPING_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MAX_WAIT_MILLIS;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MIN_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_ON_BORROW;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_WHILE_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_PORT;
import static it.grid.storm.config.ConfigurationDefaults.DB_PROPERTIES;
import static it.grid.storm.config.ConfigurationDefaults.DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.GPFS_QUOTA_REFRESH_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HEARTHBEAT_PERIOD;
import static it.grid.storm.config.ConfigurationDefaults.HTTP_TURL_PREFIX;
import static it.grid.storm.config.ConfigurationDefaults.MAX_LOOP;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_GLANCE_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_LOGBOOK_TIME_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.PERFORMANCE_MEASURING;
import static it.grid.storm.config.ConfigurationDefaults.PING_VALUES_PROPERTIES_FILENAME;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SKIP_ACL_SETUP;
import static it.grid.storm.config.ConfigurationDefaults.SERVER_POOL_STATUS_CHECK_TIMEOUT;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.google.common.collect.Lists;

import it.grid.storm.config.model.v2.OverwriteMode;
import it.grid.storm.config.model.v2.StorageType;
import it.grid.storm.namespace.model.Authority;

public class ConfigurationConverterTest {
  
  @Test
  public void testLoadedConfigurationFromOldProperties() throws IOException {

    Configuration.init("src/test/resources/v1.properties");
    Configuration config = Configuration.getInstance();

    // SRM service
    assertEquals("fe.example.org", config.getSrmServiceHostname());
    assertEquals(config.getSrmServicePort(), 8444);
    assertEquals(config.getManagedSrmEndpoints(),
        Lists.newArrayList(new Authority("fe.example.org", 8444),
            new Authority("fe-01.example.org", 8444),
            new Authority("fe-02.example.org", 8444)));
    // database
    assertEquals(config.getDbUsername(), "storm");
    assertEquals(config.getDbPassword(), "my-secret-password");
    assertEquals(config.getDbProperties(), DB_PROPERTIES);
    assertEquals(config.getDbPort(), DB_PORT);
    assertEquals(config.getDbPoolSize(), DB_POOL_SIZE);
    assertEquals(config.getDbPoolMinIdle(), DB_POOL_MIN_IDLE);
    assertEquals(config.getDbPoolMaxWaitMillis(), DB_POOL_MAX_WAIT_MILLIS);
    assertEquals(config.isDbPoolTestOnBorrow(), DB_POOL_TEST_ON_BORROW);
    assertEquals(config.isDbPoolTestWhileIdle(), DB_POOL_TEST_WHILE_IDLE);
    // REST
    assertEquals(config.getRestServicesPort(), 9998);
    assertEquals(config.getRestServicesMaxThreads(), 256);
    assertEquals(config.getRestServicesMaxQueueSize(), 1000);
    // sanity check
    assertEquals(config.getSanityCheckEnabled(), true);
    // xmlrpc
    assertEquals(config.getXmlRpcServerPort(), 8080);
    assertEquals(config.getXmlrpcMaxThreads(), 100);
    assertEquals(config.getXmlrpcMaxQueueSize(), 500);
    assertEquals(config.isSecurityEnabled(), true);
    assertEquals(config.getSecurityToken(), "abracadabra");
    // disk usage
    assertEquals(config.isDiskUsageServiceEnabled(), true);
    assertEquals(config.getDiskUsageServiceInitialDelay(), 60);
    assertEquals(config.getDiskUsageServiceTasksInterval(), 360);
    assertEquals(config.isDiskUsageServiceTasksParallel(),
        DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED);
    //
    assertEquals(config.getCleaningInitialDelay(), 10);
    assertEquals(config.getCleaningTimeInterval(), 300);
    //
    assertEquals(config.getFileDefaultSize(), 2000000);
    assertEquals(config.getFileLifetimeDefault(), 300000);
    assertEquals(config.getPinLifetimeDefault(), 310000);
    assertEquals(config.getPinLifetimeMaximum(), 1900000);

    assertEquals(config.getPickingInitialDelay(), 15);
    assertEquals(config.getPickingTimeInterval(), 25);
    assertEquals(config.getPickingMaxBatchSize(), 150);
    // LS
    assertEquals(config.getLsMaxNumberOfEntry(), 3000);
    assertEquals(config.getLsAllLevelRecursive(), true);
    assertEquals(config.getLsNumOfLevels(), 3);
    assertEquals(config.getLsOffset(), 2);
    //
    assertEquals(config.getPtPCorePoolSize(), 60);
    assertEquals(config.getPtPMaxPoolSize(), 300);
    assertEquals(config.getPtPQueueSize(), 2000);

    assertEquals(config.getPtGCorePoolSize(), 70);
    assertEquals(config.getPtGMaxPoolSize(), 400);
    assertEquals(config.getPtGQueueSize(), 3000);

    assertEquals(config.getBoLCorePoolSize(), 40);
    assertEquals(config.getBoLMaxPoolSize(), 100);
    assertEquals(config.getBoLQueueSize(), 1000);

    assertEquals(config.getCorePoolSize(), 20);
    assertEquals(config.getMaxPoolSize(), 60);
    assertEquals(config.getQueueSize(), 3000);

    assertEquals(config.getAutomaticDirectoryCreation(), true);
    assertEquals(config.getEnableWritePermOnDirectory(), true);

    assertEquals(config.getDefaultOverwriteMode(), OverwriteMode.N);
    assertEquals(config.getDefaultFileStorageType(), StorageType.P);

    assertEquals(config.getExpiredRequestPurging(), false);
    assertEquals(config.getRequestPurgerDelay(), 100);
    assertEquals(config.getRequestPurgerPeriod(), 600);
    assertEquals(config.getPurgeBatchSize(), 1000);
    assertEquals(config.getExpiredRequestTime(), 7200);
    
    assertEquals(config.getTransitInitialDelay(), 60);
    assertEquals(config.getTransitTimeInterval(), 600);
    assertEquals(config.getInProgressPtpExpirationTime(), 7000);

    assertEquals(config.getExtraSlashesForFileTURL(), "/file");
    assertEquals(config.getExtraSlashesForRFIOTURL(), "/rfio");
    assertEquals(config.getExtraSlashesForGsiFTPTURL(), "/gsiftp");
    assertEquals(config.getExtraSlashesForROOTTURL(), "/root");

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
    File exported = new File("src/test/resources/v1.properties.new");
    assertEquals(exported.exists(), true);
    // clear file
    exported.delete();

  }
}
