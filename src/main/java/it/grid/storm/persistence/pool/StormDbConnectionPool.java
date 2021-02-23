package it.grid.storm.persistence.pool;

import it.grid.storm.config.Configuration;

public class StormDbConnectionPool {

  private static final String DATABASE_NAME = "storm_db";

  private static DBConnectionPool instance;

  public static synchronized DBConnectionPool getInstance() {
    if (instance == null) {
      Configuration c = Configuration.getInstance();
      MySQLDatabaseStrategy dbs = new MySQLDatabaseStrategy(DATABASE_NAME, c.getDBHostname(),
          c.getDBUserName(), c.getDBPassword());
      instance = new DBConnectionPool(dbs, c.getDbPoolSize(), c.getDbPoolMinIdle(),
          c.getDbPoolMaxWaitMillis(), c.isDbPoolTestOnBorrow(), c.isDbPoolTestWhileIdle());
    }
    return instance;
  }

}
