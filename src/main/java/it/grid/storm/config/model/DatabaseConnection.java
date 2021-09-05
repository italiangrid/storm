package it.grid.storm.config.model;

import static it.grid.storm.config.ConfigurationDefaults.DB_PASSWORD;
import static it.grid.storm.config.ConfigurationDefaults.DB_PORT;
import static it.grid.storm.config.ConfigurationDefaults.DB_PROPERTIES;
import static it.grid.storm.config.ConfigurationDefaults.DB_USERNAME;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;

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

  public void log(Logger log, String prefix) {
    log.info("{}.hostname: {}", prefix, hostname);
    log.info("{}.username: {}", prefix, username);
    log.info("{}.password: {}", prefix, password);
    log.info("{}.port: {}", prefix, port);
    log.info("{}.properties: {}", prefix, properties);
    pool.log(log, prefix + ".pool");
  }

}
