package it.grid.storm.persistence.pool;

import it.grid.storm.config.Configuration;

public class StormBeIsamConnectionPool extends DBConnectionPool {

  private static final String DATABASE_NAME = "storm_be_ISAM";

  private static StormBeIsamConnectionPool instance;

  public static synchronized StormBeIsamConnectionPool getInstance() {
    if (instance == null) {
      instance = new StormBeIsamConnectionPool();
    }
    return instance;
  }

  private static Configuration c = Configuration.getInstance();
  private static DatabaseStrategy dbs = new MySQLDatabaseStrategy(DATABASE_NAME, c.getDBHostname(),
      c.getDBUsername(), c.getDBPassword());

  private StormBeIsamConnectionPool() {

    super(dbs, c.getDbPoolSize(), c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(),
        c.isDbPoolTestOnBorrow(), c.isDbPoolTestWhileIdle());
  }

}
