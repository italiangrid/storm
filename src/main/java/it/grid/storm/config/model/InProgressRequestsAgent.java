package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InProgressRequestsAgent {

  public int delay;
  public int interval;
  public long ptpExpirationTime;

  public InProgressRequestsAgent() {
    delay = INPROGRESS_REQUESTS_AGENT_DELAY;
    interval = INPROGRESS_REQUESTS_AGENT_INTERVAL;
    ptpExpirationTime = INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME;
  }

  public void log(Logger log, String prefix) {
    log.info("{}.delay: {}", prefix, delay);
    log.info("{}.interval: {}", prefix, interval);
    log.info("{}.ptp_expiration_time: {}", prefix, ptpExpirationTime);
  }
}
