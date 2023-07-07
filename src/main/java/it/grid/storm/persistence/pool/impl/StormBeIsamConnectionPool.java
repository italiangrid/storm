/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.pool.impl;

import it.grid.storm.config.Configuration;

public class StormBeIsamConnectionPool extends DefaultDatabaseConnectionPool {

  private static StormBeIsamConnectionPool instance;

  public static synchronized StormBeIsamConnectionPool getInstance() {
    if (instance == null) {
      instance = new StormBeIsamConnectionPool();
    }
    return instance;
  }

  private final static Configuration c = Configuration.getInstance();

  private StormBeIsamConnectionPool() {

    super(DefaultMySqlDatabaseConnector.getStormBeIsamDatabaseConnector(), c.getDbPoolSize(),
        c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(),
        c.isDbPoolTestWhileIdle());
  }

}
