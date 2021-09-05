package it.grid.storm.config.model;

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
import static it.grid.storm.config.model.StormProperties.UNRECOGNIZED_VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

public class StormPropertiesTest {

  private static final Logger log = LoggerFactory.getLogger(StormPropertiesTest.class);

  @Test
  public void testConfigurationLoadFromPropertiesV2() throws JsonParseException, JsonMappingException, IOException {

    JavaPropsMapper mapper = new JavaPropsMapper();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("storm.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    properties.log(log);
    assertEquals(properties.version, "2");
    assertFalse(properties.srmEndpoints.isEmpty());
    assertEquals(properties.srmEndpoints.size(), 2);
    assertEquals(properties.srmEndpoints.get(0).host, "storm.example");
    assertEquals(properties.srmEndpoints.get(0).port, 8444);
    assertEquals(properties.srmEndpoints.get(1).host, "alias.example");
    assertEquals(properties.srmEndpoints.get(1).port, 8445);
    assertEquals(properties.db.hostname, "storm.example");
    assertEquals(properties.db.username, "test");
    assertEquals(properties.db.password, "secret");
    assertEquals(properties.db.port, 3308);
    assertEquals(properties.db.properties, "test");
    assertEquals(properties.db.pool.size, 200);
    assertEquals(properties.db.pool.maxWaitMillis, 1200);
    assertEquals(properties.db.pool.testOnBorrow, false);
    assertEquals(properties.db.pool.testWhileIdle, false);
    assertEquals(properties.rest.port, 9999);
    assertEquals(properties.rest.maxThreads, 150);
    assertEquals(properties.rest.maxQueueSize, 1500);
    assertEquals(properties.xmlrpc.port, 9090);
    assertEquals(properties.xmlrpc.maxThreads, 512);
    assertEquals(properties.xmlrpc.maxQueueSize, 2000);
    assertEquals(properties.security.enabled, true);
    assertEquals(properties.security.token, "ilovejava");
    assertEquals(properties.du.enabled, true);
    assertEquals(properties.du.parallelTasksEnabled, true);
    assertEquals(properties.du.initialDelay, 120);
    assertEquals(properties.du.tasksInterval, 200000);
    assertEquals(properties.inprogressRequestsAgent.delay, 20);
    assertEquals(properties.inprogressRequestsAgent.interval, 400);
    assertEquals(properties.inprogressRequestsAgent.ptpExpirationTime, 333000);
    assertEquals(properties.expiredSpacesAgent.delay, 20);
    assertEquals(properties.expiredSpacesAgent.interval, 400);
    assertEquals(properties.completedRequestsAgent.enabled, true);
    assertEquals(properties.completedRequestsAgent.delay, 20);
    assertEquals(properties.completedRequestsAgent.interval, 400);
    assertEquals(properties.completedRequestsAgent.purgeSize, 1800);
    assertEquals(properties.completedRequestsAgent.purgeAge, 22200);
    assertEquals(properties.completedRequestsAgent.isDynamic, true);
    assertEquals(properties.requestsScheduler.corePoolSize, 10);
    assertEquals(properties.requestsScheduler.maxPoolSize, 50);
    assertEquals(properties.requestsScheduler.queueSize, 2000);
    assertEquals(properties.ptpScheduler.corePoolSize, 50);
    assertEquals(properties.ptpScheduler.maxPoolSize, 200);
    assertEquals(properties.ptpScheduler.queueSize, 1000);
    assertEquals(properties.ptgScheduler.corePoolSize, 50);
    assertEquals(properties.ptgScheduler.maxPoolSize, 200);
    assertEquals(properties.ptgScheduler.queueSize, 2000);
    assertEquals(properties.bolScheduler.corePoolSize, 50);
    assertEquals(properties.bolScheduler.maxPoolSize, 200);
    assertEquals(properties.bolScheduler.queueSize, 2000);
    assertEquals(properties.requestsPickerAgent.delay, 10);
    assertEquals(properties.requestsPickerAgent.interval, 20);
    assertEquals(properties.requestsPickerAgent.maxFetchedSize, 1000);
    assertEquals(properties.sanityChecksEnabled, false);
    assertEquals(properties.extraslashes.file, "/file");
    assertEquals(properties.extraslashes.rfio, "/rfio");
    assertEquals(properties.extraslashes.root, "/root");
    assertEquals(properties.extraslashes.gsiftp, "/gsiftp");
    assertEquals(properties.synchLs.defaultAllLevelRecursive, true);
    assertEquals(properties.synchLs.defaultNumLevels, 2);
    assertEquals(properties.synchLs.defaultOffset, 1);
    assertEquals(properties.synchLs.maxEntries, 3000);
    assertEquals(properties.pinlifetime.defaultValue, 300000);
    assertEquals(properties.pinlifetime.maximum, 18000000);
    assertEquals(properties.skipPtgAclSetup, true);
    assertEquals(properties.files.defaultSize, 100000);
    assertEquals(properties.files.defaultLifetime, 300000);
    assertEquals(properties.files.defaultOverwrite, "N");
    assertEquals(properties.files.defaultStoragetype, "P");
    assertEquals(properties.directories.enableAutomaticCreation, true);
    assertEquals(properties.directories.enableWritepermOnCreation, true);
    assertEquals(properties.hearthbeat.bookkeepingEnabled, true);
    assertEquals(properties.hearthbeat.performanceMeasuringEnabled, true);
    assertEquals(properties.hearthbeat.period, 30);
    assertEquals(properties.hearthbeat.performanceLogbookTimeInterval, 10);
    assertEquals(properties.hearthbeat.performanceGlanceTimeInterval, 10);
    assertEquals(properties.infoQuotaRefreshPeriod, 900);
    assertEquals(properties.httpTurlPrefix, "/");
    assertEquals(properties.serverPoolStatusCheckTimeout, 20000);
    assertEquals(properties.abortMaxloop, 10);
    assertEquals(properties.pingPropertiesFilename, "ping-values.properties");

  }

  @Test
  public void testEmptyConfiguration()
      throws JsonParseException, JsonMappingException, IOException {

    String hostname = InetAddress.getLocalHost().getHostName();
    JavaPropsMapper mapper = new JavaPropsMapper();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("empty.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    assertEquals(properties.version, UNRECOGNIZED_VERSION);
    assertFalse(properties.srmEndpoints.isEmpty());
    assertEquals(properties.srmEndpoints.size(), 1);
    assertEquals(properties.srmEndpoints.get(0).host, hostname);
    assertEquals(properties.srmEndpoints.get(0).port, 8444);
    assertEquals(properties.db.hostname, hostname);
    assertEquals(properties.db.username, DB_USERNAME);
    assertEquals(properties.db.password, DB_PASSWORD);
    assertEquals(properties.db.port, DB_PORT);
    assertEquals(properties.db.properties, DB_PROPERTIES);
    assertEquals(properties.db.pool.size, DB_POOL_SIZE);
    assertEquals(properties.db.pool.maxWaitMillis, DB_POOL_MAX_WAIT_MILLIS);
    assertEquals(properties.db.pool.minIdle, DB_POOL_MIN_IDLE);
    assertEquals(properties.db.pool.testOnBorrow, DB_POOL_TEST_ON_BORROW);
    assertEquals(properties.db.pool.testWhileIdle, DB_POOL_TEST_WHILE_IDLE);
    assertEquals(properties.rest.port, REST_SERVICES_PORT);
    assertEquals(properties.rest.maxThreads, REST_SERVICES_MAX_THREADS);
    assertEquals(properties.rest.maxQueueSize, REST_SERVICES_MAX_QUEUE_SIZE);
    assertEquals(properties.xmlrpc.port, XMLRPC_SERVER_PORT);
    assertEquals(properties.xmlrpc.maxThreads, XMLRPC_MAX_THREADS);
    assertEquals(properties.xmlrpc.maxQueueSize, XMLRPC_MAX_QUEUE_SIZE);
    assertEquals(properties.security.enabled, SECURITY_ENABLED);
    assertEquals(properties.security.token, SECURITY_TOKEN);
    assertEquals(properties.du.enabled, DISKUSAGE_SERVICE_ENABLED);
    assertEquals(properties.du.parallelTasksEnabled, DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED);
    assertEquals(properties.du.initialDelay, DISKUSAGE_SERVICE_INITIAL_DELAY);
    assertEquals(properties.du.tasksInterval, DISKUSAGE_SERVICE_TASKS_INTERVAL);
    assertEquals(properties.inprogressRequestsAgent.delay, INPROGRESS_REQUESTS_AGENT_DELAY);
    assertEquals(properties.inprogressRequestsAgent.interval, INPROGRESS_REQUESTS_AGENT_INTERVAL);
    assertEquals(properties.inprogressRequestsAgent.ptpExpirationTime,
        INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME);
    assertEquals(properties.expiredSpacesAgent.delay, EXPIRED_SPACES_AGENT_DELAY);
    assertEquals(properties.expiredSpacesAgent.interval, EXPIRED_SPACES_AGENT_INTERVAL);
    assertEquals(properties.completedRequestsAgent.enabled, COMPLETED_REQUESTS_AGENT_ENABLED);
    assertEquals(properties.completedRequestsAgent.delay, COMPLETED_REQUESTS_AGENT_DELAY);
    assertEquals(properties.completedRequestsAgent.interval, COMPLETED_REQUESTS_AGENT_INTERVAL);
    assertEquals(properties.completedRequestsAgent.purgeSize, COMPLETED_REQUESTS_AGENT_PURGE_SIZE);
    assertEquals(properties.completedRequestsAgent.purgeAge, COMPLETED_REQUESTS_AGENT_PURGE_AGE);
    assertEquals(properties.completedRequestsAgent.isDynamic, COMPLETED_REQUESTS_AGENT_ENABLED);
    assertEquals(properties.requestsScheduler.corePoolSize, REQUESTS_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(properties.requestsScheduler.maxPoolSize, REQUESTS_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(properties.requestsScheduler.queueSize, REQUESTS_SCHEDULER_QUEUE_SIZE);
    assertEquals(properties.ptpScheduler.corePoolSize, PTP_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(properties.ptpScheduler.maxPoolSize, PTP_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(properties.ptpScheduler.queueSize, PTP_SCHEDULER_QUEUE_SIZE);
    assertEquals(properties.ptgScheduler.corePoolSize, PTG_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(properties.ptgScheduler.maxPoolSize, PTG_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(properties.ptgScheduler.queueSize, PTG_SCHEDULER_QUEUE_SIZE);
    assertEquals(properties.bolScheduler.corePoolSize, BOL_SCHEDULER_CORE_POOL_SIZE);
    assertEquals(properties.bolScheduler.maxPoolSize, BOL_SCHEDULER_MAX_POOL_SIZE);
    assertEquals(properties.bolScheduler.queueSize, BOL_SCHEDULER_QUEUE_SIZE);
    assertEquals(properties.requestsPickerAgent.delay, REQUESTS_PICKER_AGENT_DELAY);
    assertEquals(properties.requestsPickerAgent.interval, REQUESTS_PICKER_AGENT_INTERVAL);
    assertEquals(properties.requestsPickerAgent.maxFetchedSize,
        REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE);
    assertEquals(properties.sanityChecksEnabled, SANITY_CHECK_ENABLED);
    assertEquals(properties.extraslashes.file, EXTRA_SLASHES_FOR_FILE_TURL);
    assertEquals(properties.extraslashes.rfio, EXTRA_SLASHES_FOR_RFIO_TURL);
    assertEquals(properties.extraslashes.root, EXTRA_SLASHES_FOR_ROOT_TURL);
    assertEquals(properties.extraslashes.gsiftp, EXTRA_SLASHES_FOR_GSIFTP_TURL);
    assertEquals(properties.synchLs.defaultAllLevelRecursive, LS_DEFAULT_ALL_LEVEL_RECURSIVE);
    assertEquals(properties.synchLs.defaultNumLevels, LS_DEFAULT_NUM_OF_LEVELS);
    assertEquals(properties.synchLs.defaultOffset, LS_DEFAULT_OFFSET);
    assertEquals(properties.synchLs.maxEntries, LS_MAX_NUMBER_OF_ENTRY);
    assertEquals(properties.pinlifetime.defaultValue, PIN_LIFETIME_DEFAULT);
    assertEquals(properties.pinlifetime.maximum, PIN_LIFETIME_MAXIMUM);
    assertEquals(properties.skipPtgAclSetup, PTG_SKIP_ACL_SETUP);
    assertEquals(properties.files.defaultSize, FILE_DEFAULT_SIZE);
    assertEquals(properties.files.defaultLifetime, FILE_LIFETIME_DEFAULT);
    assertEquals(properties.files.defaultOverwrite, DEFAULT_OVERWRITE_MODE);
    assertEquals(properties.files.defaultStoragetype, DEFAULT_FILE_STORAGE_TYPE);
    assertEquals(properties.directories.enableAutomaticCreation, AUTOMATIC_DIRECTORY_CREATION);
    assertEquals(properties.directories.enableWritepermOnCreation, ENABLE_WRITE_PERM_ON_DIRECTORY);
    assertEquals(properties.hearthbeat.bookkeepingEnabled, BOOK_KEEPING_ENABLED);
    assertEquals(properties.hearthbeat.performanceMeasuringEnabled, PERFORMANCE_MEASURING);
    assertEquals(properties.hearthbeat.period, HEARTHBEAT_PERIOD);
    assertEquals(properties.hearthbeat.performanceLogbookTimeInterval,
        PERFORMANCE_LOGBOOK_TIME_INTERVAL);
    assertEquals(properties.hearthbeat.performanceGlanceTimeInterval,
        PERFORMANCE_GLANCE_TIME_INTERVAL);
    assertEquals(properties.infoQuotaRefreshPeriod, GPFS_QUOTA_REFRESH_PERIOD);
    assertEquals(properties.httpTurlPrefix, HTTP_TURL_PREFIX);
    assertEquals(properties.serverPoolStatusCheckTimeout, SERVER_POOL_STATUS_CHECK_TIMEOUT);
    assertEquals(properties.abortMaxloop, MAX_LOOP);
    assertEquals(properties.pingPropertiesFilename, PING_VALUES_PROPERTIES_FILENAME);
  }
  
  @Test
  public void testNewConfigurationVersionOverOldFile() throws JsonParseException, JsonMappingException, IOException {

    JavaPropsMapper mapper = new JavaPropsMapper();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("v1.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    assertEquals(properties.version, UNRECOGNIZED_VERSION);
  }
}
