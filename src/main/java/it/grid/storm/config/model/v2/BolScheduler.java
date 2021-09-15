package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.BOL_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.BOL_SCHEDULER_QUEUE_SIZE;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BolScheduler {

  public int corePoolSize;
  public int maxPoolSize;
  public int queueSize;

  public BolScheduler() {
    corePoolSize = BOL_SCHEDULER_CORE_POOL_SIZE;
    maxPoolSize = BOL_SCHEDULER_MAX_POOL_SIZE;
    queueSize = BOL_SCHEDULER_QUEUE_SIZE;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("BolScheduler [corePoolSize=");
    builder.append(corePoolSize);
    builder.append(", maxPoolSize=");
    builder.append(maxPoolSize);
    builder.append(", queueSize=");
    builder.append(queueSize);
    builder.append("]");
    return builder.toString();
  }

}
