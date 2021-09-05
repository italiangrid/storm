package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.DEFAULT_SRM_PORT;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Endpoint {

  public String host;
  public int port;

  public Endpoint() {
    host = "localhost";
    port = DEFAULT_SRM_PORT;;
  }

  public static Endpoint build(String host, int port) {
    Endpoint e = new Endpoint();
    e.host = host;
    e.port = port;
    return e;
  }

  public static Endpoint DEFAULT() throws UnknownHostException {
    return build(InetAddress.getLocalHost().getHostName(), DEFAULT_SRM_PORT);
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


}
