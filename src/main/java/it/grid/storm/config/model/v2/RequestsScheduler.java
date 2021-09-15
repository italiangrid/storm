package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_SCHEDULER_QUEUE_SIZE;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestsScheduler {

  public int corePoolSize;
  public int maxPoolSize;
  public int queueSize;

  public RequestsScheduler() {
    corePoolSize = REQUESTS_SCHEDULER_CORE_POOL_SIZE;
    maxPoolSize = REQUESTS_SCHEDULER_MAX_POOL_SIZE;
    queueSize = REQUESTS_SCHEDULER_QUEUE_SIZE;
  }

}
