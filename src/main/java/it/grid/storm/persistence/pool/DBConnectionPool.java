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

public class DBConnectionPool {

  private static final Logger log = LoggerFactory.getLogger(DBConnectionPool.class);

  private BasicDataSource bds;

  public DBConnectionPool(DatabaseStrategy dbs, int maxTotal, int minIdle,
      int maxConnLifetimeMillis, boolean isTestOnBorrow, boolean isTestWhileIdle) {

    bds = new BasicDataSource();

    bds.setDriverClassName(dbs.getDriverName());
    bds.setUrl(dbs.getConnectionString());
    bds.setUsername(dbs.getDbUsername());
    bds.setPassword(dbs.getDbPassword());
    bds.setMaxTotal(maxTotal);
    bds.setInitialSize(minIdle);
    bds.setMinIdle(minIdle);
    bds.setMaxConnLifetimeMillis(maxConnLifetimeMillis);
    bds.setTestOnBorrow(isTestOnBorrow);
    bds.setTestWhileIdle(isTestWhileIdle);

    log.info("Connected to '{}'@'{}' with '{}' "
        + "[max-total: {}, min-idle: {}, max-conn-lifetime-millis: {}, test-on-borrow: {}, test-while-idle: {}]",
        dbs.getDbName(), dbs.getDbHostname(), maxTotal, minIdle, maxConnLifetimeMillis,
        isTestOnBorrow, isTestWhileIdle);
  }

  public Connection getConnection() throws SQLException {

    return bds.getConnection();
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
