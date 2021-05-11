package it.grid.storm.persistence.util.db;

import java.util.Map;

import com.google.common.collect.Maps;

import it.grid.storm.config.Configuration;

public class Databases {

  private static final Map<String, DataBaseStrategy> DATABASES = Maps.newHashMap();

  private static final String MYSQL_VENDOR = "mysql";
  private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
  private static final String MYSQL_PREFIX = "jdbc:mysql://";
  private static final SQLFormat MYSQL_FORMATTER = new MySqlFormat();

  static {
    Configuration config = Configuration.getInstance();
    DataBaseStrategy dbs = new DataBaseStrategy(MYSQL_VENDOR, MYSQL_DRIVER, MYSQL_PREFIX, MYSQL_FORMATTER);
    dbs.setDbUsr(config.getBEPersistenceDBUserName());
    dbs.setDbPwd(config.getBEPersistenceDBPassword());
    dbs.setProperties(config.getBEPersistenceDbProperties());
    dbs.setDbName(config.getBEPersistenceDBName());
    dbs.setDbUrl(config.getBEPersistenceDBMSUrl());
    DATABASES.put(MYSQL_VENDOR, dbs);
  }

  public static DataBaseStrategy getDataBaseStrategy(String vendor) {

    return DATABASES.get(vendor);
}
}
