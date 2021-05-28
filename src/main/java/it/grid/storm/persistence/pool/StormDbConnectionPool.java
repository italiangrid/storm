package it.grid.storm.persistence.pool;

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

    super(DefaultDatabaseConnector.getStormDbDatabaseConnector(), c.getDbPoolSize(),
        c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(),
        c.isDbPoolTestWhileIdle());
  }
}
