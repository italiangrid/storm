package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MAX_WAIT_MILLIS;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MIN_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_ON_BORROW;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_WHILE_IDLE;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DatabasePoolProperties {

  public int size;
  public int minIdle;
  public int maxWaitMillis;
  public boolean testOnBorrow;
  public boolean testWhileIdle;

  public DatabasePoolProperties() {
    size = DB_POOL_SIZE;
    minIdle = DB_POOL_MIN_IDLE;
    maxWaitMillis = DB_POOL_MAX_WAIT_MILLIS;
    testOnBorrow = DB_POOL_TEST_ON_BORROW;
    testWhileIdle = DB_POOL_TEST_WHILE_IDLE;
  }
  
  public void log(Logger log, String prefix) {
    log.info("{}.size: {}", prefix, size);
    log.info("{}.min_idle: {}", prefix, minIdle);
    log.info("{}.max_wait_millis: {}", prefix, maxWaitMillis);
    log.info("{}.test_on_borrow: {}", prefix, testOnBorrow);
    log.info("{}.test_while_idle: {}", prefix, testWhileIdle);
  }
}
