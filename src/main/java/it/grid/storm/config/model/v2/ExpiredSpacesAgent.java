package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_SPACES_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.EXPIRED_SPACES_AGENT_INTERVAL;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExpiredSpacesAgent {

  private int delay;
  private int interval;

  public ExpiredSpacesAgent() {
    delay = EXPIRED_SPACES_AGENT_DELAY;
    interval = EXPIRED_SPACES_AGENT_INTERVAL;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ExpiredSpacesAgent [delay=");
    builder.append(delay);
    builder.append(", interval=");
    builder.append(interval);
    builder.append("]");
    return builder.toString();
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

}
