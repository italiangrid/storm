package it.grid.storm.config.model.v2;

import static it.grid.storm.config.ConfigurationDefaults.DB_PASSWORD;
import static it.grid.storm.config.ConfigurationDefaults.DB_PORT;
import static it.grid.storm.config.ConfigurationDefaults.DB_PROPERTIES;
import static it.grid.storm.config.ConfigurationDefaults.DB_USERNAME;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DatabaseConnection {

  public String username;
  public String password;
  public String hostname;
  public int port;
  public String properties;

  public DatabasePoolProperties pool;

  public DatabaseConnection() throws UnknownHostException {
    username = DB_USERNAME;
    password = DB_PASSWORD;
    hostname = InetAddress.getLocalHost().getHostName();
    port = DB_PORT;
    properties = DB_PROPERTIES;
    pool = new DatabasePoolProperties();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DatabaseConnection [username=");
    builder.append(username);
    builder.append(", password=");
    builder.append(password);
    builder.append(", hostname=");
    builder.append(hostname);
    builder.append(", port=");
    builder.append(port);
    builder.append(", properties=");
    builder.append(properties);
    builder.append(", pool=");
    builder.append(pool);
    builder.append("]");
    return builder.toString();
  }
}
