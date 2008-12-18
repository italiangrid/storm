package it.grid.storm.persistence.util.db;


import java.sql.*;
import org.apache.log4j.Logger;
import it.grid.storm.persistence.DataSourceConnectionFactory;
import it.grid.storm.persistence.exceptions.PersistenceException;

//DBConnection - manages one connection to the DataBase
//--->could be moved to be inner class of DBConnectionPool
//--->could keep track of things about itself, such as when last used and by what

public class DBConnection implements DataSourceConnectionFactory

{
  private static final Logger log = Logger.getLogger("persistence");
  private Connection connection = null;
  private DataBaseStrategy db;

  /***********************************************************
   *  CLASS Constructors
   */

  /**
   *
   * @param db DataBase
   */
  public DBConnection(DataBaseStrategy db) throws PersistenceException
  {
    //register the driver

    this.db = db;

    try
    {
      Class.forName(db.getDriverName()).newInstance();
    }
    catch (Exception ex)
    {
      log.error("Exception while getting driver", ex);
      throw new PersistenceException("Driver loading problem");

    }
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
    try
    {
      result = getConnection();
    }
    catch (SQLException sqle)
    {
      log.error("SQLException: "+sqle.getMessage()+"/n"+"SQLState: "+sqle.getSQLState()+"/n"+"VendorError: "+sqle.getErrorCode(), sqle);
      throw new PersistenceException("Creating new connection problem", sqle);
    }
    return result;
  }

  /**
   *
   * @param con Connection
   * @throws PersistenceException
   */
  public void giveBackConnection(Connection con) throws PersistenceException {
    if (connection != null) {
      try {
        shutdown();
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



  /***********************************************************
   *  PRIVATE METHODs
   */

  /**
   *
   * @return Connection
   */
  private Connection getConnection() throws SQLException
  {
    if (connection==null)
    {
      String url = db.getConnectionString();
      connection = DriverManager.getConnection(url, db.getDbUsr(), db.getDbPwd());
   }
    return connection;
  }

  /**
   *
   * @throws SQLException
   */
  private void shutdown() throws SQLException
  {

    connection.close(); // if there are no other open connection
    connection = null;

    // db writes out to files and shuts down
    // this happens anyway at garbage collection
    // when program ends
  }


}
