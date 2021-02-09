package it.grid.storm.persistence.pool;

import it.grid.storm.config.Configuration;

public class StormBeIsamConnectionPool {

  private static DBConnectionPool instance;

  public static synchronized DBConnectionPool getInstance() {
    if (instance == null) {
      instance = new DBConnectionPool(Configuration.getInstance());
    }
    return instance;
  }

}
