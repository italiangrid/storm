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

  private String username;
  private String password;
  private String hostname;
  private int port;
  private String properties;

  private DatabasePoolProperties pool;

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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getProperties() {
    return properties;
  }

  public void setProperties(String properties) {
    this.properties = properties;
  }

  public DatabasePoolProperties getPool() {
    return pool;
  }

  public void setPool(DatabasePoolProperties pool) {
    this.pool = pool;
  }

}
