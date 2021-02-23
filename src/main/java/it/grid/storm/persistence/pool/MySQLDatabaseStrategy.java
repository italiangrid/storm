package it.grid.storm.persistence.pool;

import static java.lang.String.format;

public class MySQLDatabaseStrategy implements DatabaseStrategy {

  private static final String DBMS_VENDOR = "mysql";
  private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";
  private static final String PREFIX = "jdbc:mysql://";

  private static final SQLFormat SQL_FORMATTER = new MySqlFormat();

  private final String dbName;
  private final String hostname;
  private final String username;
  private final String password;

  public MySQLDatabaseStrategy(String dbName, String hostname, String username, String password) {

    this.dbName = dbName;
    this.hostname = hostname;
    this.username = username;
    this.password = password;
  }

  @Override
  public String getDbmsVendor() {
    return DBMS_VENDOR;
  }

  @Override
  public String getDriverName() {
    return DRIVER_NAME;
  }

  @Override
  public String getJdbcPrefix() {
    return PREFIX;
  }

  @Override
  public String getDbName() {
    return dbName;
  }

  @Override
  public String getDbHostname() {
    return hostname;
  }

  @Override
  public String getDbUsername() {
    return username;
  }

  @Override
  public String getDbPassword() {
    return password;
  }

  @Override
  public String getConnectionString() {
    return format("%s%s/%s", PREFIX, hostname, dbName);
  }

  @Override
  public SQLFormat getFormatter() {
    return SQL_FORMATTER;
  }

}
