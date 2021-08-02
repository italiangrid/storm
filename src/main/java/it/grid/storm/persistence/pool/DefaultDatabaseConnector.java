package it.grid.storm.persistence.pool;

import static java.lang.String.format;

import it.grid.storm.config.Configuration;

public class DefaultDatabaseConnector implements DatabaseConnector {

  private final String name;
  private final String driver;
  private final String url;
  private final String username;
  private final String password;

  private DefaultDatabaseConnector(String database) {

    this.name = database;

    Configuration config = Configuration.getInstance();

    this.username = config.getDbUsername();
    this.password = config.getDbPassword();
    this.driver = config.getDbDriver();

    String subprotocol = config.getDbUrlSubprotocol();
    String subname = config.getDbUrlSubname();
    String hostname = config.getDbUrlHostname();
    String port = config.getDbUrlPort();
    String properties = config.getDbUrlProperties();

    if (properties.isEmpty()) {
      this.url = format("jdbc:%s:%s//%s:%s/%s", subprotocol, subname, hostname, port, database);
    } else {
      this.url = format("jdbc:%s:%s//%s:%s/%s?%s", subprotocol, subname, hostname, port, database, properties);
    }
  }

  @Override
  public String getDriverName() {
    return driver;
  }

  @Override
  public String getDbURL() {
     return url;
  }

  @Override
  public String getDbUsername() {
    return username;
  }

  @Override
  public String getDbPassword() {
    return password;
  }

  public static DatabaseConnector getStormDbDatabaseConnector() {
    return new DefaultDatabaseConnector("storm_db");
  }

  public static DatabaseConnector getStormBeIsamDatabaseConnector() {
    return new DefaultDatabaseConnector("storm_be_ISAM");
  }

  @Override
  public String getDbName() {
    return name;
  }
}
