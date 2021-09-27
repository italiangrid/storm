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
import static it.grid.storm.config.model.v2.StormProperties.VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import it.grid.storm.config.model.v2.StormProperties;

public class StormPropertiesTest {

  @Test
  public void testLoadingConfigurationFromFullPropertiesV2()
      throws JsonParseException, JsonMappingException, IOException {

    JavaPropsMapper mapper = new JavaPropsMapper();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("storm.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    System.out.println(properties);
    assertEquals(StormProperties.VERSION, properties.version);
    assertFalse(properties.srmEndpoints.isEmpty());
    assertEquals(2, properties.srmEndpoints.size());
    assertEquals("storm.example", properties.srmEndpoints.get(0).host);
    assertEquals(8444, properties.srmEndpoints.get(0).port);
    assertEquals("alias.example", properties.srmEndpoints.get(1).host);
    assertEquals(8445, properties.srmEndpoints.get(1).port);
    assertEquals("storm.example", properties.db.hostname);
    assertEquals("test", properties.db.username);
    assertEquals("secret", properties.db.password);
    assertEquals(3308, properties.db.port);
    assertEquals("test", properties.db.properties);
    assertEquals(200, properties.db.pool.size);
    assertEquals(1200, properties.db.pool.maxWaitMillis);
    assertEquals(false, properties.db.pool.testOnBorrow);
    assertEquals(false, properties.db.pool.testWhileIdle);
    assertEquals(9999, properties.rest.port);
    assertEquals(150, properties.rest.maxThreads);
    assertEquals(1500, properties.rest.maxQueueSize);
    assertEquals(9090, properties.xmlrpc.port);
    assertEquals(512, properties.xmlrpc.maxThreads);
    assertEquals(2000, properties.xmlrpc.maxQueueSize);
    assertEquals(true, properties.security.enabled);
    assertEquals("ilovejava", properties.security.token);
    assertEquals(true, properties.du.enabled);
    assertEquals(true, properties.du.parallelTasksEnabled);
    assertEquals(120, properties.du.initialDelay);
    assertEquals(200000, properties.du.tasksInterval);
    assertEquals(20, properties.inprogressRequestsAgent.delay);
    assertEquals(400, properties.inprogressRequestsAgent.interval);
    assertEquals(333000, properties.inprogressRequestsAgent.ptpExpirationTime);
    assertEquals(20, properties.expiredSpacesAgent.delay);
    assertEquals(400, properties.expiredSpacesAgent.interval);
    assertEquals(true, properties.completedRequestsAgent.enabled);
    assertEquals(20, properties.completedRequestsAgent.delay);
    assertEquals(400, properties.completedRequestsAgent.interval);
    assertEquals(1800, properties.completedRequestsAgent.purgeSize);
    assertEquals(22200, properties.completedRequestsAgent.purgeAge);
    assertEquals(10, properties.requestsScheduler.corePoolSize);
    assertEquals(50, properties.requestsScheduler.maxPoolSize);
    assertEquals(2000, properties.requestsScheduler.queueSize);
    assertEquals(50, properties.ptpScheduler.corePoolSize);
    assertEquals(200, properties.ptpScheduler.maxPoolSize);
    assertEquals(1000, properties.ptpScheduler.queueSize);
    assertEquals(50, properties.ptgScheduler.corePoolSize);
    assertEquals(200, properties.ptgScheduler.maxPoolSize);
    assertEquals(2000, properties.ptgScheduler.queueSize);
    assertEquals(50, properties.bolScheduler.corePoolSize);
    assertEquals(200, properties.bolScheduler.maxPoolSize);
    assertEquals(2000, properties.bolScheduler.queueSize);
    assertEquals(10, properties.requestsPickerAgent.delay);
    assertEquals(20, properties.requestsPickerAgent.interval);
    assertEquals(1000, properties.requestsPickerAgent.maxFetchedSize);
    assertEquals(false, properties.sanityChecksEnabled);
    assertEquals("/file", properties.extraslashes.file);
    assertEquals("/rfio", properties.extraslashes.rfio);
    assertEquals("/root", properties.extraslashes.root);
    assertEquals("/gsiftp", properties.extraslashes.gsiftp);
    assertEquals(true, properties.synchLs.defaultAllLevelRecursive);
    assertEquals(2, properties.synchLs.defaultNumLevels);
    assertEquals(1, properties.synchLs.defaultOffset);
    assertEquals(3000, properties.synchLs.maxEntries);
    assertEquals(300000, properties.pinlifetime.defaultValue);
    assertEquals(18000000, properties.pinlifetime.maximum);
    assertEquals(true, properties.skipPtgAclSetup);
    assertEquals(100000, properties.files.defaultSize);
    assertEquals(300000, properties.files.defaultLifetime);
    assertEquals("N", properties.files.defaultOverwrite);
    assertEquals("P", properties.files.defaultStoragetype);
    assertEquals(true, properties.directories.enableAutomaticCreation);
    assertEquals(true, properties.directories.enableWritepermOnCreation);
    assertEquals(true, properties.hearthbeat.bookkeepingEnabled);
    assertEquals(true, properties.hearthbeat.performanceMeasuringEnabled);
    assertEquals(30, properties.hearthbeat.period);
    assertEquals(10, properties.hearthbeat.performanceLogbookTimeInterval);
    assertEquals(10, properties.hearthbeat.performanceGlanceTimeInterval);
    assertEquals(900, properties.infoQuotaRefreshPeriod);
    assertEquals("/", properties.httpTurlPrefix);
    assertEquals(20000, properties.serverPoolStatusCheckTimeout);
    assertEquals(10, properties.abortMaxloop);
    assertEquals("ping-values.properties", properties.pingPropertiesFilename);

  }

  @Test
  public void testDefaultConfigurationStartingFromEmptyFile()
      throws JsonParseException, JsonMappingException, IOException {

    String hostname = InetAddress.getLocalHost().getHostName();
    JavaPropsMapper mapper = new JavaPropsMapper();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("empty.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    assertEquals(VERSION, properties.version);
    assertFalse(properties.srmEndpoints.isEmpty());
    assertEquals(1, properties.srmEndpoints.size());
    assertEquals(hostname, properties.srmEndpoints.get(0).host);
    assertEquals(8444, properties.srmEndpoints.get(0).port);
    assertEquals(hostname, properties.db.hostname);
    assertEquals(DB_USERNAME, properties.db.username);
    assertEquals(DB_PASSWORD, properties.db.password);
    assertEquals(DB_PORT, properties.db.port);
    assertEquals(DB_PROPERTIES, properties.db.properties);
    assertEquals(DB_POOL_SIZE, properties.db.pool.size);
    assertEquals(DB_POOL_MAX_WAIT_MILLIS, properties.db.pool.maxWaitMillis);
    assertEquals(DB_POOL_MIN_IDLE, properties.db.pool.minIdle);
    assertEquals(DB_POOL_TEST_ON_BORROW, properties.db.pool.testOnBorrow);
    assertEquals(DB_POOL_TEST_WHILE_IDLE, properties.db.pool.testWhileIdle);
    assertEquals(REST_SERVICES_PORT, properties.rest.port);
    assertEquals(REST_SERVICES_MAX_THREADS, properties.rest.maxThreads);
    assertEquals(REST_SERVICES_MAX_QUEUE_SIZE, properties.rest.maxQueueSize);
    assertEquals(XMLRPC_SERVER_PORT, properties.xmlrpc.port);
    assertEquals(XMLRPC_MAX_THREADS, properties.xmlrpc.maxThreads);
    assertEquals(XMLRPC_MAX_QUEUE_SIZE, properties.xmlrpc.maxQueueSize);
    assertEquals(SECURITY_ENABLED, properties.security.enabled);
    assertEquals(SECURITY_TOKEN, properties.security.token);
    assertEquals(DISKUSAGE_SERVICE_ENABLED, properties.du.enabled);
    assertEquals(DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED, properties.du.parallelTasksEnabled);
    assertEquals(DISKUSAGE_SERVICE_INITIAL_DELAY, properties.du.initialDelay);
    assertEquals(DISKUSAGE_SERVICE_TASKS_INTERVAL, properties.du.tasksInterval);
    assertEquals(INPROGRESS_REQUESTS_AGENT_DELAY, properties.inprogressRequestsAgent.delay);
    assertEquals(INPROGRESS_REQUESTS_AGENT_INTERVAL, properties.inprogressRequestsAgent.interval);
    assertEquals(INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME,
        properties.inprogressRequestsAgent.ptpExpirationTime);
    assertEquals(EXPIRED_SPACES_AGENT_DELAY, properties.expiredSpacesAgent.delay);
    assertEquals(EXPIRED_SPACES_AGENT_INTERVAL, properties.expiredSpacesAgent.interval);
    assertEquals(COMPLETED_REQUESTS_AGENT_ENABLED, properties.completedRequestsAgent.enabled);
    assertEquals(COMPLETED_REQUESTS_AGENT_DELAY, properties.completedRequestsAgent.delay);
    assertEquals(COMPLETED_REQUESTS_AGENT_INTERVAL, properties.completedRequestsAgent.interval);
    assertEquals(COMPLETED_REQUESTS_AGENT_PURGE_SIZE, properties.completedRequestsAgent.purgeSize);
    assertEquals(COMPLETED_REQUESTS_AGENT_PURGE_AGE, properties.completedRequestsAgent.purgeAge);
    assertEquals(REQUESTS_SCHEDULER_CORE_POOL_SIZE, properties.requestsScheduler.corePoolSize);
    assertEquals(REQUESTS_SCHEDULER_MAX_POOL_SIZE, properties.requestsScheduler.maxPoolSize);
    assertEquals(REQUESTS_SCHEDULER_QUEUE_SIZE, properties.requestsScheduler.queueSize);
    assertEquals(PTP_SCHEDULER_CORE_POOL_SIZE, properties.ptpScheduler.corePoolSize);
    assertEquals(PTP_SCHEDULER_MAX_POOL_SIZE, properties.ptpScheduler.maxPoolSize);
    assertEquals(PTP_SCHEDULER_QUEUE_SIZE, properties.ptpScheduler.queueSize);
    assertEquals(PTG_SCHEDULER_CORE_POOL_SIZE, properties.ptgScheduler.corePoolSize);
    assertEquals(PTG_SCHEDULER_MAX_POOL_SIZE, properties.ptgScheduler.maxPoolSize);
    assertEquals(PTG_SCHEDULER_QUEUE_SIZE, properties.ptgScheduler.queueSize);
    assertEquals(BOL_SCHEDULER_CORE_POOL_SIZE, properties.bolScheduler.corePoolSize);
    assertEquals(BOL_SCHEDULER_MAX_POOL_SIZE, properties.bolScheduler.maxPoolSize);
    assertEquals(BOL_SCHEDULER_QUEUE_SIZE, properties.bolScheduler.queueSize);
    assertEquals(REQUESTS_PICKER_AGENT_DELAY, properties.requestsPickerAgent.delay);
    assertEquals(REQUESTS_PICKER_AGENT_INTERVAL, properties.requestsPickerAgent.interval);
    assertEquals(REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE, properties.requestsPickerAgent.maxFetchedSize);
    assertEquals(SANITY_CHECK_ENABLED, properties.sanityChecksEnabled);
    assertEquals(EXTRA_SLASHES_FOR_FILE_TURL, properties.extraslashes.file);
    assertEquals(EXTRA_SLASHES_FOR_RFIO_TURL, properties.extraslashes.rfio);
    assertEquals(EXTRA_SLASHES_FOR_ROOT_TURL, properties.extraslashes.root);
    assertEquals(EXTRA_SLASHES_FOR_GSIFTP_TURL, properties.extraslashes.gsiftp);
    assertEquals(LS_DEFAULT_ALL_LEVEL_RECURSIVE, properties.synchLs.defaultAllLevelRecursive);
    assertEquals(LS_DEFAULT_NUM_OF_LEVELS, properties.synchLs.defaultNumLevels);
    assertEquals(LS_DEFAULT_OFFSET, properties.synchLs.defaultOffset);
    assertEquals(LS_MAX_NUMBER_OF_ENTRY, properties.synchLs.maxEntries);
    assertEquals(PIN_LIFETIME_DEFAULT, properties.pinlifetime.defaultValue);
    assertEquals(PIN_LIFETIME_MAXIMUM, properties.pinlifetime.maximum);
    assertEquals(PTG_SKIP_ACL_SETUP, properties.skipPtgAclSetup);
    assertEquals(FILE_DEFAULT_SIZE, properties.files.defaultSize);
    assertEquals(FILE_LIFETIME_DEFAULT, properties.files.defaultLifetime);
    assertEquals(DEFAULT_OVERWRITE_MODE, properties.files.defaultOverwrite);
    assertEquals(DEFAULT_FILE_STORAGE_TYPE, properties.files.defaultStoragetype);
    assertEquals(AUTOMATIC_DIRECTORY_CREATION, properties.directories.enableAutomaticCreation);
    assertEquals(ENABLE_WRITE_PERM_ON_DIRECTORY, properties.directories.enableWritepermOnCreation);
    assertEquals(BOOK_KEEPING_ENABLED, properties.hearthbeat.bookkeepingEnabled);
    assertEquals(PERFORMANCE_MEASURING, properties.hearthbeat.performanceMeasuringEnabled);
    assertEquals(HEARTHBEAT_PERIOD, properties.hearthbeat.period);
    assertEquals(PERFORMANCE_LOGBOOK_TIME_INTERVAL, properties.hearthbeat.performanceLogbookTimeInterval);
    assertEquals(PERFORMANCE_GLANCE_TIME_INTERVAL, properties.hearthbeat.performanceGlanceTimeInterval);
    assertEquals(GPFS_QUOTA_REFRESH_PERIOD, properties.infoQuotaRefreshPeriod);
    assertEquals(HTTP_TURL_PREFIX, properties.httpTurlPrefix);
    assertEquals(SERVER_POOL_STATUS_CHECK_TIMEOUT, properties.serverPoolStatusCheckTimeout);
    assertEquals(MAX_LOOP, properties.abortMaxloop);
    assertEquals(PING_VALUES_PROPERTIES_FILENAME, properties.pingPropertiesFilename);
  }

  @Test(expected = JsonMappingException.class)
  public void testNewConfigurationVersionOverOldFile()
      throws JsonParseException, JsonMappingException, IOException {

    JavaPropsMapper mapper = new JavaPropsMapper();
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("v1.properties").getFile());
    mapper.readValue(file, StormProperties.class);
  }

  @Test
  public void testSrmEndpointsWithoutHostname()
      throws JsonParseException, JsonMappingException, IOException {

    final String TEST_FILE = "/tmp/test-srm-endpoints.properties";
    PrintWriter writer = new PrintWriter(TEST_FILE, "UTF-8");
    writer.println("srm_endpoints[0].port: 8444");
    writer.close();
    JavaPropsMapper mapper = new JavaPropsMapper();
    File file = new File(TEST_FILE);
    try {
      mapper.readValue(file, StormProperties.class);
      fail("Expected JsonMappingException");
    } catch (JsonMappingException e) {
      assertTrue(
          e.getMessage().indexOf("Missing required creator property 'host' (index 0)") != -1);
    } finally {
      file.delete();
    }
  }
}
