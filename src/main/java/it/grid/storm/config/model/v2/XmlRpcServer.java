package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_QUEUE_SIZE;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_MAX_THREADS;
import static it.grid.storm.config.ConfigurationDefaults.XMLRPC_SERVER_PORT;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class XmlRpcServer {

  public int port;
  public int maxThreads;
  public int maxQueueSize;

  public XmlRpcServer() {
    port = XMLRPC_SERVER_PORT;
    maxThreads = XMLRPC_MAX_THREADS;
    maxQueueSize = XMLRPC_MAX_QUEUE_SIZE;
  }

  public void setMaxThreads(int maxThreads) {
    this.maxThreads = maxThreads > 0 ? maxThreads : XMLRPC_MAX_THREADS;
  }

  public void setMaxQueueSize(int maxQueueSize) {
    this.maxQueueSize = maxQueueSize > 0 ? maxQueueSize : XMLRPC_MAX_QUEUE_SIZE;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("XmlRpcServer [port=");
    builder.append(port);
    builder.append(", maxThreads=");
    builder.append(maxThreads);
    builder.append(", maxQueueSize=");
    builder.append(maxQueueSize);
    builder.append("]");
    return builder.toString();
  }

}
