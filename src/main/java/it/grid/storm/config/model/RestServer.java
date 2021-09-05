package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.REST_SERVICES_PORT;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RestServer {

  public int port;
  public int maxThreads;
  public int maxQueueSize;

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
  
  public void log(Logger log, String prefix) {
    log.info("{}.port: {}", prefix, port);
    log.info("{}.max_threads: {}", prefix, maxThreads);
    log.info("{}.max_queue_size: {}", prefix, maxQueueSize);
  }
}
