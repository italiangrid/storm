package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.REQUESTS_PICKER_AGENT_MAX_FETCHED_SIZE;

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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RequestsPickerAgent [delay=");
    builder.append(delay);
    builder.append(", interval=");
    builder.append(interval);
    builder.append(", maxFetchedSize=");
    builder.append(maxFetchedSize);
    builder.append("]");
    return builder.toString();
  }
}
