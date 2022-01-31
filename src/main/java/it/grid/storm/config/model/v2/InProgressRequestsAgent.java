package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_DELAY;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_INTERVAL;
import static it.grid.storm.config.ConfigurationDefaults.INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InProgressRequestsAgent {

  private int delay;
  private int interval;
  private long ptpExpirationTime;

  public InProgressRequestsAgent() {
    delay = INPROGRESS_REQUESTS_AGENT_DELAY;
    interval = INPROGRESS_REQUESTS_AGENT_INTERVAL;
    ptpExpirationTime = INPROGRESS_REQUESTS_AGENT_PTP_EXPIRATION_TIME;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("InProgressRequestsAgent [delay=");
    builder.append(delay);
    builder.append(", interval=");
    builder.append(interval);
    builder.append(", ptpExpirationTime=");
    builder.append(ptpExpirationTime);
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

  public long getPtpExpirationTime() {
    return ptpExpirationTime;
  }

  public void setPtpExpirationTime(long ptpExpirationTime) {
    this.ptpExpirationTime = ptpExpirationTime;
  }

}
