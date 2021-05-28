package it.grid.storm.persistence.pool;

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

    super(DefaultDatabaseConnector.getStormBeIsamDatabaseConnector(), c.getDbPoolSize(),
        c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(),
        c.isDbPoolTestWhileIdle());
  }

}
