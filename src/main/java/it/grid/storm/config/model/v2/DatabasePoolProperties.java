package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MAX_WAIT_MILLIS;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_MIN_IDLE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_ON_BORROW;
import static it.grid.storm.config.ConfigurationDefaults.DB_POOL_TEST_WHILE_IDLE;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DatabasePoolProperties [size=");
    builder.append(size);
    builder.append(", minIdle=");
    builder.append(minIdle);
    builder.append(", maxWaitMillis=");
    builder.append(maxWaitMillis);
    builder.append(", testOnBorrow=");
    builder.append(testOnBorrow);
    builder.append(", testWhileIdle=");
    builder.append(testWhileIdle);
    builder.append("]");
    return builder.toString();
  }

}
