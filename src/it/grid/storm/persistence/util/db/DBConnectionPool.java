package it.grid.storm.persistence.util.db;

import org.apache.commons.dbcp.datasources.*;
import org.apache.commons.dbcp.cpdsadapter.*;
import java.sql.Connection;
import org.apache.log4j.Logger;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import it.grid.storm.persistence.DataSourceConnectionFactory;
import it.grid.storm.persistence.exceptions.PersistenceException;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import java.sql.DriverManager;
import it.grid.storm.persistence.model.StorageSpaceTO;

import it.grid.storm.common.types.VO;
import it.grid.storm.griduser.*;

public class DBConnectionPool implements DataSourceConnectionFactory{

  private static final Logger log = Logger.getLogger("persistence");
  private DataBaseStrategy db;
  private static SharedPoolDataSource sharedDatasource;
  private static DBConnectionPool instance = new DBConnectionPool();
  private static long handle = -1;


  /***********************************************************
   *  CLASS Constructors
   */


  /**
   * Private constructor.  Singleton pattern.
   */
  private DBConnectionPool()
  {
    super();
  }

  public static DBConnectionPool getPoolInstance() {
    if (handle==-1) return null; //not initializated.
    else return instance;
  }

  /**
   *
   * @param db DataBase
   * @param maxActive int
   * @param maxWait int
   */
  public static void initPool(DataBaseStrategy db, int maxActive, int maxWait) throws PersistenceException {
    instance.init(db, maxActive, maxWait);
  }


  /***********************************************************
   *  PUBLIC METHODs
   */

  /**
   *
   * @return Connection
   * @throws PersistenceException
   */
  public Connection borrowConnection() throws PersistenceException {
    Connection result = null;
    if (handle==-1)
    {
       throw new PersistenceException("Connection Pool is not initializated!");
    }
    try{
      result = sharedDatasource.getConnection();
    }
    catch (SQLException sqle)
    {
      log.error("SQLException: "+sqle.getMessage()+"/n"+"SQLState: "+sqle.getSQLState()+"/n"+"VendorError: "+sqle.getErrorCode(), sqle);
      throw new PersistenceException("Problem retrieving connection from pool",sqle);
    }
    return result;
  }

  /**
   *
   * @param con Connection
   * @throws PersistenceException
   */
  public void giveBackConnection(Connection con) throws PersistenceException {
    if (con != null) {
      try {
        shutdown(con);
      }
      catch (SQLException sqle) {
        log.error("SQLException: " + sqle.getMessage() + "/n" + "SQLState: " + sqle.getSQLState() + "/n" +
                  "VendorError: " + sqle.getErrorCode(), sqle);
        throw new PersistenceException("Closing existing connection problem", sqle);
      }
    }
    else {
      throw new PersistenceException("Closing NON-Existing connection" );
    }
  }

  /********************************************
   * VALIDATION Methods
   */

  /**
   *
   * @return String
   * @throws PersistenceException
   */
  public String getPoolInfo() throws PersistenceException {
    String result = "";
    if (handle == -1) {
      throw new PersistenceException("Connection Pool is not initializated!");
    }
    if (sharedDatasource.getValidationQuery() != null)
      result += "Validation query = " + sharedDatasource.getValidationQuery() + "\n";
    if (sharedDatasource.getDescription() != null)
      result += "Description = " + sharedDatasource.getDescription() + "\n";
    result += "Nr Connection Active = " + sharedDatasource.getNumActive() + "\n";
    result += "Nr Connection Idle = " + sharedDatasource.getNumIdle() + "\n";
    result += "Nr Max Active Connection = " + sharedDatasource.getMaxActive() + "\n";

    return result;
  }

  /***********************************************************
  *  PRIVATE METHODs
  */


 /**
  *
  * @param db DataBase
  * @param maxActive int
  * @param maxWait int
  */
 private void init(DataBaseStrategy db, int maxActive, int maxWait) {

   instance.db = db;
   DriverAdapterCPDS connectionPoolDatasource = new DriverAdapterCPDS();
   try {
     connectionPoolDatasource.setDriver(db.getDriverName());
   }
   catch (Exception ex) {
     log.error("Exception while getting driver", ex);
   }

   connectionPoolDatasource.setUrl(db.getConnectionString());
   connectionPoolDatasource.setUser(db.getDbUsr());
   connectionPoolDatasource.setPassword(db.getDbPwd());

   sharedDatasource = new SharedPoolDataSource();
   sharedDatasource.setConnectionPoolDataSource(connectionPoolDatasource);

   sharedDatasource.setMaxActive(maxActive);
   sharedDatasource.setMaxWait(maxWait);

   handle = System.currentTimeMillis();
 }


 /**
  *
  * @throws SQLException
  */
 private void shutdown(Connection conn) throws SQLException
 {

   conn.close(); // if there are no other open connection
   conn = null;

   // db writes out to files and shuts down
   // this happens anyway at garbage collection
   // when program ends
  }



  /***********************************************************
   *  TEST CLASS
   */

  public static void printInfo( DBConnectionPool pool)
  {
    System.out.println("INFO ");
    try {
      System.out.println(pool.getPoolInfo());
    }
    catch (PersistenceException ex2) {
      ex2.printStackTrace();
    }

  }

  public static void main(String[] args) {
    DataBaseStrategy db = DataBaseStrategy.MYSQL;
    db.setDbUrl("testbed006");
    db.setDbName("storm_be_ISAM");
    db.setDbUsr("storm");
    db.setDbPwd("storm");
    System.out.println("Connection string ="+ db.getConnectionString());

    try {
      DBConnectionPool.initPool(db, 10, 50);
    }
    catch (PersistenceException ex1) {
      ex1.printStackTrace();
    }

    DBConnectionPool pool = DBConnectionPool.getPoolInstance();

    printInfo(pool);

    Connection conn = null;
    try {
      conn = pool.borrowConnection();
    }
    catch (PersistenceException ex) {
      ex.printStackTrace();
    }

    printInfo(pool);

    Statement myStatement = null;
    ResultSet myResult = null;

    if (conn != null) {
      try {
        myStatement = conn.createStatement();
       /**
        myResult = myStatement.executeQuery("SELECT count(*) FROM storage_file s;");
        // Get the resulting data back, and loop through it
        // to simulate the data retrieval.
        int numcols = myResult.getMetaData().getColumnCount();
        while (myResult.next()) {
          for (int i = 1; i <= numcols; i++) {
            System.out.println("res("+i+")= "+myResult.getString(i));
          }
        }
        **/
       StorageSpaceTO ssTO = new StorageSpaceTO();
       ssTO.setAlias("Test");
       ssTO.setCreated(new java.util.Date(System.currentTimeMillis()));
       ssTO.setGuaranteedSize(10000);
       ssTO.setLifetime(1000);
       VO vo = VO.make("testVO");
       VomsGridUser gu = null;
       gu = VomsGridUser.make("testUser");
       ssTO.setOwner(gu);
       ssTO.setSpaceFile("test_spaceFile");
       ssTO.setSpaceToken("test_spaceToken");
       ssTO.setSpaceType("volatile");


      }
      catch (SQLException e) {
        e.printStackTrace();
      }
      finally {
        // We want to be agressive about ensuring that our
        // connection is properly cleaned up and returned to
        // our pool.
        try {
          myResult.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        try {
          myStatement.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        try {
          pool.giveBackConnection(conn);
          //conn.close();
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      printInfo(pool);
    }

  }

/**
  public class DBCPDemo {

    public static void main(String args[]) throws Exception {

      // create a generic pool
      GenericObjectPool pool = new GenericObjectPool(null);

      // use the connection factory which will wraped by
      // the PoolableConnectionFactory
      DriverManagerConnectionFactory cf =
          new DriverManagerConnectionFactory(
              "jdbc:jtds:sqlserver://myserver:1433/tandem", "user", "pass");

      PoolableConnectionFactory pcf =
          new PoolableConnectionFactory(
              cf, pool, null, "SELECT * FROM mysql.db", false, true);

      // register our pool and give it a name
      new PoolingDriver().registerPool("myPool", pool);

      // get a connection and test it
      Connection conn =
          DriverManager.getConnection("jdbc:apache:commons:dbcp:myPool");

      // now we can use this pool the way we want.
      System.err.println("Are we connected? " + !conn.isClosed());

      System.err.println("Idle Connections: " + pool.getNumIdle() + ", out of " + pool.getNumActive());

    }
  }
**/

}
