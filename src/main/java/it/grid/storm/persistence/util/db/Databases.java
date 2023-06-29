/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
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

  private static final String DB_NAME = "storm_be_ISAM";

  static {
    Configuration config = Configuration.getInstance();
    DataBaseStrategy dbs = new DataBaseStrategy(MYSQL_VENDOR, MYSQL_DRIVER, MYSQL_PREFIX, MYSQL_FORMATTER);
    dbs.setDbUsr(config.getDBUserName());
    dbs.setDbPwd(config.getDBPassword());
    dbs.setProperties(config.getDBProperties());
    dbs.setDbName(DB_NAME);
    dbs.setDbHost(config.getDBHostname());
    DATABASES.put(MYSQL_VENDOR, dbs);
  }

  public static DataBaseStrategy getDataBaseStrategy(String vendor) {

    return DATABASES.get(vendor);
}
}
