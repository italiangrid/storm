package it.grid.storm.config.model.v2;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SrmEndpoint {

  private String host;
  private int port;

  @JsonCreator
  public SrmEndpoint(@JsonProperty(value = "host", required = true) String host,
      @JsonProperty(value = "port", required = false, defaultValue = "8444") int port) {
    this.host = host;
    this.port = port;
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
    SrmEndpoint other = (SrmEndpoint) obj;
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
