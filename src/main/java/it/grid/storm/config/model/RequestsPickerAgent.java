package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RequestsPickerAgent {

  public int delay;
  public int interval;
  public int maxFetchedSize;

  public RequestsPickerAgent() {
    delay = REQUESTS_PICKER_AGENT_DELAY;
    interval = REQUESTS_PICKER_AGENT_INTERVAL;
    maxFetchedSize = REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.delay: {}", prefix, delay);
    log.info("{}.interval: {}", prefix, interval);
    log.info("{}.max_fetched_size: {}", prefix, maxFetchedSize);
  }
}
