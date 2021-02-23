package it.grid.storm.persistence.pool;

import it.grid.storm.config.Configuration;

public class StormDbConnectionPool {

  private static DBConnectionPool instance;

  public static synchronized DBConnectionPool getInstance() {
    if (instance == null) {
      Configuration c = Configuration.getInstance();
      instance = new DBConnectionPool(c.getStormDbURL(), c.getDbPoolSize(), c.getDbPoolMinIdle(),
          c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(), c.isDbPoolTestWhileIdle());
    }
    return instance;
  }

}
