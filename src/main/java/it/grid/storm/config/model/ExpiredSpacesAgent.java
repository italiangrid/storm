package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_SPACES_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_SPACES_AGENT_INTERVAL;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExpiredSpacesAgent {

  public int delay;
  public int interval;

  public ExpiredSpacesAgent() {
    delay = EXPIRED_SPACES_AGENT_DELAY;
    interval = EXPIRED_SPACES_AGENT_INTERVAL;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.delay: {}", prefix, delay);
    log.info("{}.interval: {}", prefix, interval);
  }
}
