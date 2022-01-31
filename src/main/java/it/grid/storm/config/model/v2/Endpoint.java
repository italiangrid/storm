package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_SRM_PORT;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Endpoint {

  private String host;
  private int port;

  @JsonCreator
  public Endpoint(@JsonProperty(value = "host", required = true) String host) {
    this.host = host;
    port = DEFAULT_SRM_PORT;
  }

  @Override
  public int hashCode() {
    return Objects.hash(host, port);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Endpoint other = (Endpoint) obj;
    return Objects.equals(host, other.host) && port == other.port;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Endpoint [host=");
    builder.append(host);
    builder.append(", port=");
    builder.append(port);
    builder.append("]");
    return builder.toString();
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

}
