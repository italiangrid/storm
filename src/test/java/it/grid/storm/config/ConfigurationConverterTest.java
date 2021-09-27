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
    assertEquals(DB_PORT, properties.db.port);
    assertEquals(DB_POOL_SIZE, properties.db.pool.size);
    assertEquals(DB_POOL_MIN_IDLE, properties.db.pool.minIdle);
    assertEquals(DB_POOL_MAX_WAIT_MILLIS, properties.db.pool.maxWaitMillis);
    assertEquals(DB_POOL_TEST_ON_BORROW, properties.db.pool.testOnBorrow);
    assertEquals(DB_POOL_TEST_WHILE_IDLE, properties.db.pool.testWhileIdle);

    // SRM service
    assertEquals("fe.example.org", properties.srmEndpoints.get(0).host);
    assertEquals(8444, properties.srmEndpoints.get(0).port);
    assertEquals("fe-01.example.org", properties.srmEndpoints.get(1).host);
    assertEquals(8444, properties.srmEndpoints.get(1).port);
    assertEquals("fe-02.example.org", properties.srmEndpoints.get(2).host);
    assertEquals(8444, properties.srmEndpoints.get(2).port);
    assertEquals("be.example.org", properties.db.hostname);
    assertEquals("storm", properties.db.username);
    assertEquals("my-secret-password", properties.db.password);
    assertEquals("prop=1", properties.db.properties);
    assertEquals(9999, properties.rest.port);
    assertEquals(512, properties.rest.maxThreads);
    assertEquals(2000, properties.rest.maxQueueSize);
    assertEquals(8081, properties.xmlrpc.port);
    assertEquals(512, properties.xmlrpc.maxThreads);
    assertEquals(2000, properties.xmlrpc.maxQueueSize);
    assertEquals(true, properties.security.enabled);
    assertEquals("ilovejava", properties.security.token);
    assertEquals(true, properties.du.enabled);
    assertEquals(true, properties.du.parallelTasksEnabled);
    assertEquals(60, properties.du.initialDelay);
    assertEquals(360, properties.du.tasksInterval);
    assertEquals(true, properties.sanityChecksEnabled);
    assertEquals(true, properties.directories.enableAutomaticCreation);
    assertEquals(true, properties.directories.enableWritepermOnCreation);
    assertEquals(310000, properties.pinlifetime.defaultValue);
    assertEquals(1900000, properties.pinlifetime.maximum);
    assertEquals("/file", properties.extraslashes.file);
    assertEquals("/rfio", properties.extraslashes.rfio);
    assertEquals("/root", properties.extraslashes.root);
    assertEquals("/gsiftp", properties.extraslashes.gsiftp);
    assertEquals(2000000, properties.files.defaultSize);
    assertEquals(300000, properties.files.defaultLifetime);
    assertEquals("N", properties.files.defaultOverwrite);
    assertEquals("P", properties.files.defaultStoragetype);
    assertEquals(20, properties.requestsScheduler.corePoolSize);
    assertEquals(60, properties.requestsScheduler.maxPoolSize);
    assertEquals(3000, properties.requestsScheduler.queueSize);
    assertEquals(60, properties.ptpScheduler.corePoolSize);
    assertEquals(300, properties.ptpScheduler.maxPoolSize);
    assertEquals(2000, properties.ptpScheduler.queueSize);
    assertEquals(70, properties.ptgScheduler.corePoolSize);
    assertEquals(400, properties.ptgScheduler.maxPoolSize);
    assertEquals(3000, properties.ptgScheduler.queueSize);
    assertEquals(40, properties.bolScheduler.corePoolSize);
    assertEquals(100, properties.bolScheduler.maxPoolSize);
    assertEquals(1000, properties.bolScheduler.queueSize);
    assertEquals(15, properties.requestsPickerAgent.delay);
    assertEquals(25, properties.requestsPickerAgent.interval);
    assertEquals(150, properties.requestsPickerAgent.maxFetchedSize);
    assertEquals(true, properties.synchLs.defaultAllLevelRecursive);
    assertEquals(3, properties.synchLs.defaultNumLevels);
    assertEquals(2, properties.synchLs.defaultOffset);
    assertEquals(3000, properties.synchLs.maxEntries);
    assertEquals(false, properties.skipPtgAclSetup);

    assertEquals(60, properties.inprogressRequestsAgent.delay);
    assertEquals(600, properties.inprogressRequestsAgent.interval);
    assertEquals(7000, properties.inprogressRequestsAgent.ptpExpirationTime);

    assertEquals(10, properties.expiredSpacesAgent.delay);
    assertEquals(300, properties.expiredSpacesAgent.interval);

    assertEquals(false, properties.completedRequestsAgent.enabled);
    assertEquals(100, properties.completedRequestsAgent.delay);
    assertEquals(600, properties.completedRequestsAgent.interval);
    assertEquals(1000, properties.completedRequestsAgent.purgeSize);
    assertEquals(7200, properties.completedRequestsAgent.purgeAge);

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

    // delete temporary file
    target.delete();

  }
}
