/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.persistence.pool;

import static java.lang.String.valueOf;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.config.Configuration;

public class DBConnectionPool {

  private static final Logger log = LoggerFactory.getLogger(DBConnectionPool.class);

  private static final DataBaseStrategy MYSQL =
      new DataBaseStrategy("mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://", new MySqlFormat());

  private BasicDataSource bds;

  public DBConnectionPool(Configuration c) {
    this(c.getDbPoolSize(), c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(),
        c.isDbPoolTestOnBorrow(), c.isDbPoolTestWhileIdle());
  }

  public DBConnectionPool(int maxTotal, int minIdle, int maxConnLifetimeMillis,
      boolean isTestOnBorrow, boolean isTestWhileIdle) {

    bds = new BasicDataSource();
    // Set database driver name
    bds.setDriverClassName(MYSQL.getDriverName());
    // Set database URL
    bds.setUrl(MYSQL.getDbUrl());
    // Set database user
    bds.setUsername(MYSQL.getDbUsr());
    // Set database password
    bds.setPassword(MYSQL.getDbPwd());
    // Set the connection pool size
    bds.setMaxTotal(maxTotal);
    bds.setInitialSize(minIdle);
    bds.setMinIdle(minIdle);
    bds.setMaxConnLifetimeMillis(maxConnLifetimeMillis);
    bds.setTestOnBorrow(isTestOnBorrow);
    bds.setTestWhileIdle(isTestWhileIdle);
    log.info(
        "Connection pool for '{}' init with [max-total: {}, min-idle: {}, max-conn-lifetime-millis: {}, test-on-borrow: {}, test-while-idle: {}]",
        MYSQL.getDbUrl(), maxTotal, minIdle, maxConnLifetimeMillis, isTestOnBorrow,
        isTestWhileIdle);
  }

  public Connection getConnection() throws SQLException {

    return bds.getConnection();
  }

  public DataBaseStrategy getDatabaseStrategy() {

    return MYSQL;
  }

  public Map<String, String> getMetrics() {

    Map<String, String> metrics = Maps.newHashMap();
    metrics.put("max-total", valueOf(bds.getMaxTotal()));
    metrics.put("min-idle", valueOf(bds.getMinIdle()));
    metrics.put("test-on-borrow", valueOf(bds.getTestOnBorrow()));
    metrics.put("test-while-idle", valueOf(bds.getTestWhileIdle()));
    metrics.put("num-active", valueOf(bds.getNumActive()));
    metrics.put("num-idle", valueOf(bds.getNumIdle()));
    metrics.put("max-conn-lifetime-millis", valueOf(bds.getMaxConnLifetimeMillis()));
    metrics.put("max-idle", valueOf(bds.getMaxIdle()));
    return metrics;
  }

}
