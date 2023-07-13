/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.pool.impl;

import it.grid.storm.config.Configuration;

public class StormDbConnectionPool extends DefaultDatabaseConnectionPool {

  private static StormDbConnectionPool instance;

  public static synchronized StormDbConnectionPool getInstance() {
    if (instance == null) {
      instance = new StormDbConnectionPool();
    }
    return instance;
  }

  private final static Configuration c = Configuration.getInstance();

  private StormDbConnectionPool() {

    super(DefaultMySqlDatabaseConnector.getStormDbDatabaseConnector(), c.getDbPoolSize(),
        c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(),
        c.isDbPoolTestWhileIdle());
  }
}