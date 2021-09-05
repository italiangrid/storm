package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.PTG_SCHEDULER_CORE_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SCHEDULER_MAX_POOL_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.PTG_SCHEDULER_QUEUE_SIZE;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PtgScheduler {

  public int corePoolSize;
  public int maxPoolSize;
  public int queueSize;
  
  public PtgScheduler() {
    corePoolSize = PTG_SCHEDULER_CORE_POOL_SIZE;
    maxPoolSize = PTG_SCHEDULER_MAX_POOL_SIZE;
    queueSize = PTG_SCHEDULER_QUEUE_SIZE;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.core_pool_size: {}", prefix, corePoolSize);
    log.info("{}.max_pool_size: {}", prefix, maxPoolSize);
    log.info("{}.queue_size: {}", prefix, queueSize);
  }
}
