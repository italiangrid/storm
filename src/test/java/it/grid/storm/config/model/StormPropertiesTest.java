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
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_QUALITY_LEVEL;
import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_SITENAME;
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

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import it.grid.storm.config.model.v2.QualityLevel;
import it.grid.storm.config.model.v2.StormProperties;

public class StormPropertiesTest {

  private JavaPropsMapper mapper;
  private ClassLoader classLoader;

  @Before
  public void init() {
 
    mapper = new JavaPropsMapper();
    mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    classLoader = getClass().getClassLoader();
  }

  @Test
  public void testLoadingConfigurationFromFullPropertiesV2()
      throws JsonParseException, JsonMappingException, IOException {

    File file = new File(classLoader.getResource("storm.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    System.out.println(properties);
    assertEquals(StormProperties.VERSION, properties.getVersion());
    assertFalse(properties.getSrmEndpoints().isEmpty());
    assertEquals(2, properties.getSrmEndpoints().size());
    assertEquals("storm-fe01.example", properties.getSrmEndpoints().get(0).getHost());
    assertEquals(8444, properties.getSrmEndpoints().get(0).getPort());
    assertEquals("storm-fe02.example", properties.getSrmEndpoints().get(1).getHost());
    assertEquals(8445, properties.getSrmEndpoints().get(1).getPort());
    assertEquals("storm-db.example", properties.getDb().getHostname());
    assertEquals("test", properties.getDb().getUsername());
    assertEquals("secret", properties.getDb().getPassword());
    assertEquals(3308, properties.getDb().getPort());
    assertEquals("test", properties.getDb().getProperties());
    assertEquals(200, properties.getDb().getPool().getSize());
    assertEquals(1200, properties.getDb().getPool().getMaxWaitMillis());
    assertEquals(false, properties.getDb().getPool().isTestOnBorrow());
    assertEquals(false, properties.getDb().getPool().isTestWhileIdle());
    assertEquals(9999, properties.getRest().getPort());
    assertEquals(150, properties.getRest().getMaxThreads());
    assertEquals(1500, properties.getRest().getMaxQueueSize());
    assertEquals(9090, properties.getXmlrpc().getPort());
    assertEquals(512, properties.getXmlrpc().getMaxThreads());
    assertEquals(2000, properties.getXmlrpc().getMaxQueueSize());
    assertEquals(true, properties.getSecurity().isEnabled());
    assertEquals("ilovejava", properties.getSecurity().getToken());
    assertEquals(true, properties.getDu().isEnabled());
    assertEquals(true, properties.getDu().isParallelTasksEnabled());
    assertEquals(120, properties.getDu().getInitialDelay());
    assertEquals(200000, properties.getDu().getTasksInterval());
    assertEquals(20, properties.getInprogressRequestsAgent().getDelay());
    assertEquals(400, properties.getInprogressRequestsAgent().getInterval());
    assertEquals(333000, properties.getInprogressRequestsAgent().getPtpExpirationTime());
    assertEquals(20, properties.getExpiredSpacesAgent().getDelay());
    assertEquals(400, properties.getExpiredSpacesAgent().getInterval());
    assertEquals(true, properties.getCompletedRequestsAgent().isEnabled());
    assertEquals(20, properties.getCompletedRequestsAgent().getDelay());
    assertEquals(400, properties.getCompletedRequestsAgent().getInterval());
    assertEquals(1800, properties.getCompletedRequestsAgent().getPurgeSize());
    assertEquals(22200, properties.getCompletedRequestsAgent().getPurgeAge());
    assertEquals(10, properties.getRequestsScheduler().getCorePoolSize());
    assertEquals(50, properties.getRequestsScheduler().getMaxPoolSize());
    assertEquals(2000, properties.getRequestsScheduler().getQueueSize());
    assertEquals(50, properties.getPtpScheduler().getCorePoolSize());
    assertEquals(200, properties.getPtpScheduler().getMaxPoolSize());
    assertEquals(1000, properties.getPtpScheduler().getQueueSize());
    assertEquals(50, properties.getPtgScheduler().getCorePoolSize());
    assertEquals(200, properties.getPtgScheduler().getMaxPoolSize());
    assertEquals(2000, properties.getPtgScheduler().getQueueSize());
    assertEquals(50, properties.getBolScheduler().getCorePoolSize());
    assertEquals(200, properties.getBolScheduler().getMaxPoolSize());
    assertEquals(2000, properties.getBolScheduler().getQueueSize());
    assertEquals(10, properties.getRequestsPickerAgent().getDelay());
    assertEquals(20, properties.getRequestsPickerAgent().getInterval());
    assertEquals(1000, properties.getRequestsPickerAgent().getMaxFetchedSize());
    assertEquals(false, properties.isSanityChecksEnabled());
    assertEquals("/file", properties.getExtraslashes().getFile());
    assertEquals("/rfio", properties.getExtraslashes().getRfio());
    assertEquals("/root", properties.getExtraslashes().getRoot());
    assertEquals("/gsiftp", properties.getExtraslashes().getGsiftp());
    assertEquals(true, properties.getSynchLs().isDefaultAllLevelRecursive());
    assertEquals(2, properties.getSynchLs().getDefaultNumLevels());
    assertEquals(1, properties.getSynchLs().getDefaultOffset());
    assertEquals(3000, properties.getSynchLs().getMaxEntries());
    assertEquals(300000, properties.getPinlifetime().getDefaultValue());
    assertEquals(18000000, properties.getPinlifetime().getMaximum());
    assertEquals(true, properties.isSkipPtgAclSetup());
    assertEquals(100000, properties.getFiles().getDefaultSize());
    assertEquals(300000, properties.getFiles().getDefaultLifetime());
    assertEquals("N", properties.getFiles().getDefaultOverwrite());
    assertEquals("P", properties.getFiles().getDefaultStoragetype());
    assertEquals(true, properties.getDirectories().isEnableAutomaticCreation());
    assertEquals(true, properties.getDirectories().isEnableWritepermOnCreation());
    assertEquals(true, properties.getHearthbeat().isBookkeepingEnabled());
    assertEquals(true, properties.getHearthbeat().isPerformanceMeasuringEnabled());
    assertEquals(30, properties.getHearthbeat().getPeriod());
    assertEquals(10, properties.getHearthbeat().getPerformanceLogbookTimeInterval());
    assertEquals(10, properties.getHearthbeat().getPerformanceGlanceTimeInterval());
    assertEquals(900, properties.getInfoQuotaRefreshPeriod());
    assertEquals("/", properties.getHttpTurlPrefix());
    assertEquals(20000, properties.getServerPoolStatusCheckTimeout());
    assertEquals(10, properties.getAbortMaxloop());
    assertEquals("ping-values.properties", properties.getPingPropertiesFilename());
    assertEquals("StoRM test", properties.getSite().getName());
    assertEquals(QualityLevel.PRODUCTION, properties.getSite().getQualityLevel());
  }

  @Test
  public void testDefaultConfigurationStartingFromEmptyFile()
      throws JsonParseException, JsonMappingException, IOException {

    String hostname = InetAddress.getLocalHost().getHostName();
    File file = new File(classLoader.getResource("empty.properties").getFile());
    StormProperties properties = mapper.readValue(file, StormProperties.class);
    assertEquals(VERSION, properties.getVersion());
    assertFalse(properties.getSrmEndpoints().isEmpty());
    assertEquals(1, properties.getSrmEndpoints().size());
    assertEquals(hostname, properties.getSrmEndpoints().get(0).getHost());
    assertEquals(8444, properties.getSrmEndpoints().get(0).getPort());
    assertEquals(hostname, properties.getDb().getHostname());
    assertEquals(DB_USERNAME, properties.getDb().getUsername());
    assertEquals(DB_PASSWORD, properties.getDb().getPassword());
    assertEquals(DB_PORT, properties.getDb().getPort());
    assertEquals(DB_PROPERTIES, properties.getDb().getProperties());
    assertEquals(DB_POOL_SIZE, properties.getDb().getPool().getSize());
    assertEquals(DB_POOL_MAX_WAIT_MILLIS, properties.getDb().getPool().getMaxWaitMillis());
    assertEquals(DB_POOL_MIN_IDLE, properties.getDb().getPool().getMinIdle());
    assertEquals(DB_POOL_TEST_ON_BORROW, properties.getDb().getPool().isTestOnBorrow());
    assertEquals(DB_POOL_TEST_WHILE_IDLE, properties.getDb().getPool().isTestWhileIdle());
    assertEquals(REST_SERVICES_PORT, properties.getRest().getPort());
    assertEquals(REST_SERVICES_MAX_THREADS, properties.getRest().getMaxThreads());
    assertEquals(REST_SERVICES_MAX_QUEUE_SIZE, properties.getRest().getMaxQueueSize());
    assertEquals(XMLRPC_SERVER_PORT, properties.getXmlrpc().getPort());
    assertEquals(XMLRPC_MAX_THREADS, properties.getXmlrpc().getMaxThreads());
    assertEquals(XMLRPC_MAX_QUEUE_SIZE, properties.getXmlrpc().getMaxQueueSize());
    assertEquals(SECURITY_ENABLED, properties.getSecurity().isEnabled());
    assertEquals(SECURITY_TOKEN, properties.getSecurity().getToken());
    assertEquals(DISKUSAGE_SERVICE_ENABLED, properties.getDu().isEnabled());
    assertEquals(DISKUSAGE_SERVICE_PARALLEL_TASKS_ENABLED,
        properties.getDu().isParallelTasksEnabled());
    assertEquals(DISKUSAGE_SERVICE_INITIAL_DELAY, properties.getDu().getInitialDelay());
    assertEquals(DISKUSAGE_SERVICE_TASKS_INTERVAL, properties.getDu().getTasksInterval());
    assertEquals(INPROGRESS_REQUESTS_AGENT_DELAY,
        properties.getInprogressRequestsAgent().getDelay());
    assertEquals(INPROGRESS_REQUESTS_AGENT_INTERVAL,
        properties.getInprogressRequestsAgent().getInterval());
    assertEquals(INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME,
        properties.getInprogressRequestsAgent().getPtpExpirationTime());
    assertEquals(EXPIRED_SPACES_AGENT_DELAY, properties.getExpiredSpacesAgent().getDelay());
    assertEquals(EXPIRED_SPACES_AGENT_INTERVAL, properties.getExpiredSpacesAgent().getInterval());
    assertEquals(COMPLETED_REQUESTS_AGENT_ENABLED,
        properties.getCompletedRequestsAgent().isEnabled());
    assertEquals(COMPLETED_REQUESTS_AGENT_DELAY, properties.getCompletedRequestsAgent().getDelay());
    assertEquals(COMPLETED_REQUESTS_AGENT_INTERVAL,
        properties.getCompletedRequestsAgent().getInterval());
    assertEquals(COMPLETED_REQUESTS_AGENT_PURGE_SIZE,
        properties.getCompletedRequestsAgent().getPurgeSize());
    assertEquals(COMPLETED_REQUESTS_AGENT_PURGE_AGE,
        properties.getCompletedRequestsAgent().getPurgeAge());
    assertEquals(REQUESTS_SCHEDULER_CORE_POOL_SIZE,
        properties.getRequestsScheduler().getCorePoolSize());
    assertEquals(REQUESTS_SCHEDULER_MAX_POOL_SIZE,
        properties.getRequestsScheduler().getMaxPoolSize());
    assertEquals(REQUESTS_SCHEDULER_QUEUE_SIZE, properties.getRequestsScheduler().getQueueSize());
    assertEquals(PTP_SCHEDULER_CORE_POOL_SIZE, properties.getPtpScheduler().getCorePoolSize());
    assertEquals(PTP_SCHEDULER_MAX_POOL_SIZE, properties.getPtpScheduler().getMaxPoolSize());
    assertEquals(PTP_SCHEDULER_QUEUE_SIZE, properties.getPtpScheduler().getQueueSize());
    assertEquals(PTG_SCHEDULER_CORE_POOL_SIZE, properties.getPtgScheduler().getCorePoolSize());
    assertEquals(PTG_SCHEDULER_MAX_POOL_SIZE, properties.getPtgScheduler().getMaxPoolSize());
    assertEquals(PTG_SCHEDULER_QUEUE_SIZE, properties.getPtgScheduler().getQueueSize());
    assertEquals(BOL_SCHEDULER_CORE_POOL_SIZE, properties.getBolScheduler().getCorePoolSize());
    assertEquals(BOL_SCHEDULER_MAX_POOL_SIZE, properties.getBolScheduler().getMaxPoolSize());
    assertEquals(BOL_SCHEDULER_QUEUE_SIZE, properties.getBolScheduler().getQueueSize());
    assertEquals(REQUESTS_PICKER_AGENT_DELAY, properties.getRequestsPickerAgent().getDelay());
    assertEquals(REQUESTS_PICKER_AGENT_INTERVAL, properties.getRequestsPickerAgent().getInterval());
    assertEquals(REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE,
        properties.getRequestsPickerAgent().getMaxFetchedSize());
    assertEquals(SANITY_CHECK_ENABLED, properties.isSanityChecksEnabled());
    assertEquals(EXTRA_SLASHES_FOR_FILE_TURL, properties.getExtraslashes().getFile());
    assertEquals(EXTRA_SLASHES_FOR_RFIO_TURL, properties.getExtraslashes().getRfio());
    assertEquals(EXTRA_SLASHES_FOR_ROOT_TURL, properties.getExtraslashes().getRoot());
    assertEquals(EXTRA_SLASHES_FOR_GSIFTP_TURL, properties.getExtraslashes().getGsiftp());
    assertEquals(LS_DEFAULT_ALL_LEVEL_RECURSIVE,
        properties.getSynchLs().isDefaultAllLevelRecursive());
    assertEquals(LS_DEFAULT_NUM_OF_LEVELS, properties.getSynchLs().getDefaultNumLevels());
    assertEquals(LS_DEFAULT_OFFSET, properties.getSynchLs().getDefaultOffset());
    assertEquals(LS_MAX_NUMBER_OF_ENTRY, properties.getSynchLs().getMaxEntries());
    assertEquals(PIN_LIFETIME_DEFAULT, properties.getPinlifetime().getDefaultValue());
    assertEquals(PIN_LIFETIME_MAXIMUM, properties.getPinlifetime().getMaximum());
    assertEquals(PTG_SKIP_ACL_SETUP, properties.isSkipPtgAclSetup());
    assertEquals(FILE_DEFAULT_SIZE, properties.getFiles().getDefaultSize());
    assertEquals(FILE_LIFETIME_DEFAULT, properties.getFiles().getDefaultLifetime());
    assertEquals(DEFAULT_OVERWRITE_MODE, properties.getFiles().getDefaultOverwrite());
    assertEquals(DEFAULT_FILE_STORAGE_TYPE, properties.getFiles().getDefaultStoragetype());
    assertEquals(AUTOMATIC_DIRECTORY_CREATION,
        properties.getDirectories().isEnableAutomaticCreation());
    assertEquals(ENABLE_WRITE_PERM_ON_DIRECTORY,
        properties.getDirectories().isEnableWritepermOnCreation());
    assertEquals(BOOK_KEEPING_ENABLED, properties.getHearthbeat().isBookkeepingEnabled());
    assertEquals(PERFORMANCE_MEASURING, properties.getHearthbeat().isPerformanceMeasuringEnabled());
    assertEquals(HEARTHBEAT_PERIOD, properties.getHearthbeat().getPeriod());
    assertEquals(PERFORMANCE_LOGBOOK_TIME_INTERVAL,
        properties.getHearthbeat().getPerformanceLogbookTimeInterval());
    assertEquals(PERFORMANCE_GLANCE_TIME_INTERVAL,
        properties.getHearthbeat().getPerformanceGlanceTimeInterval());
    assertEquals(GPFS_QUOTA_REFRESH_PERIOD, properties.getInfoQuotaRefreshPeriod());
    assertEquals(HTTP_TURL_PREFIX, properties.getHttpTurlPrefix());
    assertEquals(SERVER_POOL_STATUS_CHECK_TIMEOUT, properties.getServerPoolStatusCheckTimeout());
    assertEquals(MAX_LOOP, properties.getAbortMaxloop());
    assertEquals(PING_VALUES_PROPERTIES_FILENAME, properties.getPingPropertiesFilename());
    assertEquals(DEFAULT_SITENAME, properties.getSite().getName());
    assertEquals(DEFAULT_QUALITY_LEVEL, properties.getSite().getQualityLevel());
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
