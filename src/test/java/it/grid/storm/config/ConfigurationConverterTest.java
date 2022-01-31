package it.grid.storm.config;

import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MAX_WAIT_MILLIS;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MIN_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_ON_BORROW;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_WHILE_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_PORT;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import it.grid.storm.config.converter.StormPropertiesConversionException;
import it.grid.storm.config.converter.StormPropertiesConverter;
import it.grid.storm.config.model.v2.StormProperties;

public class ConfigurationConverterTest {

  @Test
  public void testLoadedConfigurationFromOldProperties()
      throws IOException, StormPropertiesConversionException {

    ClassLoader classLoader = getClass().getClassLoader();
    File source = new File(classLoader.getResource("v1.properties").getFile());
    File target = new File("/tmp/converted.properties");

    // convert source configuration file and save it into target file:
    StormPropertiesConverter.convert(source, target);

    // load new configuration from file
    JavaPropsMapper mapper = new JavaPropsMapper();
    StormProperties properties = mapper.readValue(target, StormProperties.class);

    // not converted
    assertEquals(DB_PORT, properties.getDb().getPort());
    assertEquals(DB_POOL_SIZE, properties.getDb().getPool().getSize());
    assertEquals(DB_POOL_MIN_IDLE, properties.getDb().getPool().getMinIdle());
    assertEquals(DB_POOL_MAX_WAIT_MILLIS, properties.getDb().getPool().getMaxWaitMillis());
    assertEquals(DB_POOL_TEST_ON_BORROW, properties.getDb().getPool().isTestOnBorrow());
    assertEquals(DB_POOL_TEST_WHILE_IDLE, properties.getDb().getPool().isTestWhileIdle());

    // SRM service
    assertEquals("fe.example.org", properties.getSrmEndpoints().get(0).getHost());
    assertEquals(8444, properties.getSrmEndpoints().get(0).getPort());
    assertEquals("fe-01.example.org", properties.getSrmEndpoints().get(1).getHost());
    assertEquals(8444, properties.getSrmEndpoints().get(1).getPort());
    assertEquals("fe-02.example.org", properties.getSrmEndpoints().get(2).getHost());
    assertEquals(8444, properties.getSrmEndpoints().get(2).getPort());
    assertEquals("be.example.org", properties.getDb().getHostname());
    assertEquals("storm", properties.getDb().getUsername());
    assertEquals("my-secret-password", properties.getDb().getPassword());
    assertEquals("prop=1", properties.getDb().getProperties());
    assertEquals(9999, properties.getRest().getPort());
    assertEquals(512, properties.getRest().getMaxThreads());
    assertEquals(2000, properties.getRest().getMaxQueueSize());
    assertEquals(8081, properties.getXmlrpc().getPort());
    assertEquals(512, properties.getXmlrpc().getMaxThreads());
    assertEquals(2000, properties.getXmlrpc().getMaxQueueSize());
    assertEquals(true, properties.getSecurity().isEnabled());
    assertEquals("ilovejava", properties.getSecurity().getToken());
    assertEquals(true, properties.getDu().isEnabled());
    assertEquals(true, properties.getDu().isParallelTasksEnabled());
    assertEquals(60, properties.getDu().getInitialDelay());
    assertEquals(360, properties.getDu().getTasksInterval());
    assertEquals(true, properties.isSanityChecksEnabled());
    assertEquals(true, properties.getDirectories().isEnableAutomaticCreation());
    assertEquals(true, properties.getDirectories().isEnableWritepermOnCreation());
    assertEquals(310000, properties.getPinlifetime().getDefaultValue());
    assertEquals(1900000, properties.getPinlifetime().getMaximum());
    assertEquals("/file", properties.getExtraslashes().getFile());
    assertEquals("/rfio", properties.getExtraslashes().getRfio());
    assertEquals("/root", properties.getExtraslashes().getRoot());
    assertEquals("/gsiftp", properties.getExtraslashes().getGsiftp());
    assertEquals(2000000, properties.getFiles().getDefaultSize());
    assertEquals(300000, properties.getFiles().getDefaultLifetime());
    assertEquals("N", properties.getFiles().getDefaultOverwrite());
    assertEquals("P", properties.getFiles().getDefaultStoragetype());
    assertEquals(20, properties.getRequestsScheduler().getCorePoolSize());
    assertEquals(60, properties.getRequestsScheduler().getMaxPoolSize());
    assertEquals(3000, properties.getRequestsScheduler().getQueueSize());
    assertEquals(60, properties.getPtpScheduler().getCorePoolSize());
    assertEquals(300, properties.getPtpScheduler().getMaxPoolSize());
    assertEquals(2000, properties.getPtpScheduler().getQueueSize());
    assertEquals(70, properties.getPtgScheduler().getCorePoolSize());
    assertEquals(400, properties.getPtgScheduler().getMaxPoolSize());
    assertEquals(3000, properties.getPtgScheduler().getQueueSize());
    assertEquals(40, properties.getBolScheduler().getCorePoolSize());
    assertEquals(100, properties.getBolScheduler().getMaxPoolSize());
    assertEquals(1000, properties.getBolScheduler().getQueueSize());
    assertEquals(15, properties.getRequestsPickerAgent().getDelay());
    assertEquals(25, properties.getRequestsPickerAgent().getInterval());
    assertEquals(150, properties.getRequestsPickerAgent().getMaxFetchedSize());
    assertEquals(true, properties.getSynchLs().isDefaultAllLevelRecursive());
    assertEquals(3, properties.getSynchLs().getDefaultNumLevels());
    assertEquals(2, properties.getSynchLs().getDefaultOffset());
    assertEquals(3000, properties.getSynchLs().getMaxEntries());
    assertEquals(false, properties.isSkipPtgAclSetup());

    assertEquals(60, properties.getInprogressRequestsAgent().getDelay());
    assertEquals(600, properties.getInprogressRequestsAgent().getInterval());
    assertEquals(7000, properties.getInprogressRequestsAgent().getPtpExpirationTime());

    assertEquals(10, properties.getExpiredSpacesAgent().getDelay());
    assertEquals(300, properties.getExpiredSpacesAgent().getInterval());

    assertEquals(false, properties.getCompletedRequestsAgent().isEnabled());
    assertEquals(100, properties.getCompletedRequestsAgent().getDelay());
    assertEquals(600, properties.getCompletedRequestsAgent().getInterval());
    assertEquals(1000, properties.getCompletedRequestsAgent().getPurgeSize());
    assertEquals(7200, properties.getCompletedRequestsAgent().getPurgeAge());

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

    // delete temporary file
    target.delete();

  }
}
