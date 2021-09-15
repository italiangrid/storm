package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_ENABLED;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_PURGE_AGE;
import static it.grid.storm.config.ConfigurationDefaults.COMPLETED_REQUESTS_AGENT_PURGE_SIZE;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CompletedRequestsAgent [enabled=");
    builder.append(enabled);
    builder.append(", delay=");
    builder.append(delay);
    builder.append(", interval=");
    builder.append(interval);
    builder.append(", purgeAge=");
    builder.append(purgeAge);
    builder.append(", purgeSize=");
    builder.append(purgeSize);
    builder.append("]");
    return builder.toString();
  }

}
