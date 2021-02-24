package it.grid.storm.persistence.pool;

import it.grid.storm.config.Configuration;

public class StormDbConnectionPool extends DBConnectionPool {

  private static final String DATABASE_NAME = "storm_db";

  private static StormDbConnectionPool instance;

  public static synchronized StormDbConnectionPool getInstance() {
    if (instance == null) {
      instance = new StormDbConnectionPool();
    }
    return instance;
  }

  private static Configuration c = Configuration.getInstance();
  private static DatabaseStrategy dbs = new MySQLDatabaseStrategy(DATABASE_NAME, c.getDBHostname(),
      c.getDBUsername(), c.getDBPassword());

  private StormDbConnectionPool() {

    super(dbs, c.getDbPoolSize(), c.getDbPoolMinIdle(), c.getDbPoolMaxWaitMillis(),
        c.isDbPoolTestOnBorrow(), c.isDbPoolTestWhileIdle());
  }
}
