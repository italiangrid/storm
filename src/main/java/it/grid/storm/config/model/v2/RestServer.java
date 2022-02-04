package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_PORT;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RestServer {

  private int port;
  private int maxThreads;
  private int maxQueueSize;

  public RestServer() {
    port = REST_SERVICES_PORT;
    maxThreads = REST_SERVICES_MAX_THREADS;
    maxQueueSize = REST_SERVICES_MAX_QUEUE_SIZE;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads > 0 ? maxThreads : REST_SERVICES_MAX_THREADS;
  }

  public void setMaxQueueSize(int maxQueueSize) {
    this.maxQueueSize = maxQueueSize > 0 ? maxQueueSize : REST_SERVICES_MAX_QUEUE_SIZE;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getMaxThreads() {
    return maxThreads;
  }

  public int getMaxQueueSize() {
    return maxQueueSize;
  }


}
