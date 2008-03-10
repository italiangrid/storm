package it.grid.storm.persistence;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.util.db.DataBaseStrategy;
import org.apache.log4j.Logger;
import it.grid.storm.persistence.exceptions.PersistenceException;
import it.grid.storm.persistence.util.db.DBConnectionPool;
import it.grid.storm.persistence.util.db.DBConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PersistenceDirector {

  private static final Log log = LogFactory.getLog("persistence");
  private static Configuration config = Configuration.getInstance();
  private static String dbVendor;
  private static DataBaseStrategy dbMan;
  private static DAOFactory daoFactory;
  private static DataSourceConnectionFactory connFactory;

  static {
    log.debug("Inizializating Persistence Director...");
    dbMan = initializeDataBase();
    daoFactory = initializeFactory();
    connFactory = connectToDateSource();
  }

  private static DataBaseStrategy initializeDataBase()
  {
    dbVendor = config.getBE_PersistenceDBVendor();
    log.debug("DBMS Vendor =  "+dbVendor);
    log.debug("DBMS URL    =  "+config.getBE_PersistenceDBMSUrl());
    return DataBaseStrategy.getInstance(dbVendor);
  }

  /**
   * Use an ad hoc String mapping scheme, and introduce an if-else
   * branch for each alternative.
   */
  private static DAOFactory initializeFactory() {
    if (dbVendor.equalsIgnoreCase("MySql")) {
      return MySqlDAOFactory.getInstance();
    }
    else if (dbVendor.equalsIgnoreCase("memory")) {
      return new MemoryDAOFactory();
    }
    else if (dbVendor.equalsIgnoreCase("mock")) {
      return new MockDAOFactory();
    }
    else {
      log.error("Persistence Data Source was setted in a BAD way..");
      throw new IllegalArgumentException("Unknown datastore identifier.");
    }
  }

  private static DataSourceConnectionFactory connectToDateSource()
  {
    DataSourceConnectionFactory result = null;
    //Collect Logging information
    StringBuffer sf = new StringBuffer();
    sf.append("Connecting to Data Source..."+"\n");
    sf.append("  Connection String = "+dbMan.getConnectionString()+"\n");

    boolean poolMode = config.getBE_PersistencePoolDB();
    int maxActive = config.getBE_PersistencePoolDB_MaxActive();
    int maxWait = config.getBE_PersistencePoolDB_MaxWait();
    sf.append("  Pool Mode = "+poolMode+"\n");
    if (poolMode)
    {
      sf.append("    pool Max Active = "+ maxActive +"\n");
      sf.append("    pool Max Wait =  "+ maxWait +"\n");
    }
    log.debug(sf);

    if (poolMode)
    {
      log.debug("Setup Connection Pool");
      try {
        DBConnectionPool.initPool(dbMan, maxActive, maxWait);
        result = DBConnectionPool.getPoolInstance();
      }
      catch (PersistenceException ex1) {
        log.error("Connection In Pool Mode to Data Source FAIL ", ex1);
      }
    }
    else
    {
      log.debug("Setup Shared Connection");
      try {
        result = new DBConnection(dbMan);
      }
      catch (PersistenceException ex) {
        log.error("Connection to Data Source FAIL", ex);
      }
    }
    return result;
  }



  public static DAOFactory getDAOFactory()
  {
    return daoFactory;
  }

  public static DataBaseStrategy getDataBase()
  {
    return dbMan;
  }

  public static DataSourceConnectionFactory getConnectionFactory()
  {
    return connFactory;
  }

  public static Log getLogger() {
      return log;
    }

}
