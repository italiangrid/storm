package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_PURGE_AGE;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_PURGE_SIZE;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CompletedRequestsAgent {

  public boolean enabled;
  public int delay;
  public int interval;
  public long purgeAge;
  public int purgeSize;

  public CompletedRequestsAgent() {
    enabled = COMPLETED_REQUESTS_AGENT_ENABLED;
    delay = COMPLETED_REQUESTS_AGENT_DELAY;
    interval = COMPLETED_REQUESTS_AGENT_INTERVAL;
    purgeAge = COMPLETED_REQUESTS_AGENT_PURGE_AGE;
    purgeSize = COMPLETED_REQUESTS_AGENT_PURGE_SIZE;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.enabled: {}", prefix, enabled);
    log.info("{}.delay: {}", prefix, delay);
    log.info("{}.interval: {}", prefix, interval);
    log.info("{}.purge_age: {}", prefix, purgeAge);
    log.info("{}.purge_size: {}", prefix, purgeSize);
  }
}
