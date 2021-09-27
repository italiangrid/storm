package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_SRM_PORT;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Endpoint {

  public String host;
  public int port;

  @JsonCreator
  public Endpoint(@JsonProperty(value = "host", required = true) String host) {
    this.host = host;
    port = DEFAULT_SRM_PORT;
  }

  public static Endpoint DEFAULT() throws UnknownHostException {
    Endpoint e = new Endpoint(InetAddress.getLocalHost().getHostName());
    e.port = DEFAULT_SRM_PORT;
    return e;
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

}
