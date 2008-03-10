package it.grid.storm.persistence.util.db;


import java.util.HashMap;
import java.util.Map;


public class DataBaseStrategy {

  private String dbmsVendor;
  private String driverName;
  private String jdbcPrefix;
  private String dbName;
  private String dbPrefix;
  private String dbUrl;
  private String dbUsr;
  private String dbPwd;
  private SQLFormat formatter;
  public static final DataBaseStrategy HSQL = new DataBaseStrategy("hsql", "org.hsqldb.jdbcDriver", "jdbc:hsqldb://", null);
  public static final DataBaseStrategy MYSQL = new DataBaseStrategy("mysql", "com.mysql.jdbc.Driver", "jdbc:mysql://", new MySqlFormat());
  private static final Map DATABASES = new HashMap();

  static
  {
    DATABASES.put(MYSQL.toString(), MYSQL);
    DATABASES.put(HSQL.toString(), HSQL);
  }

  /**
   * Prevent instantiation and subclassing with a private constructor.
   */
  private DataBaseStrategy(String dbmsVendor, String driverName, String prefix, SQLFormat formatter)
  {
    this.dbmsVendor = dbmsVendor;
    this.driverName = driverName;
    this.jdbcPrefix = prefix;
    this.formatter = formatter;
  }

  // ********************** Common Methods ********************** //

  public String getDbmsVendor()
  {
    return this.dbmsVendor;
  }

  public String getDriverName()
  {
    return this.driverName;
  }

  public String getJdbcPrefix()
  {
    return this.jdbcPrefix;
  }

  public void setDbUsr(String usrDb)
  {
    this.dbUsr = usrDb;
  }

  public String getDbUsr()
  {
    return this.dbUsr;
  }

  public void setDbPwd(String pwd)
  {
    this.dbPwd = pwd;
  }

  public String getDbPwd()
  {
    return this.dbPwd;
  }

  public void setDbName(String dbName)
  {
    this.dbName = dbName;
  }

  public String getDbName()
  {
    return this.dbName;
  }

  public void setDbPrefix(String dbName)
  {
    this.dbPrefix = dbName;
  }

  public String getDbPrefix()
  {
    return this.dbPrefix;
  }

  public void setDbUrl(String url)
  {
    this.dbUrl = url;
  }

  public String getDbUrl()
  {
    return this.dbUrl;
  }

  public String getConnectionString()
  {
    String connStr;
    connStr = jdbcPrefix + dbUrl+"/"+dbName;
    return connStr;
  }

  public void setFormatter(SQLFormat formatter)
  {
    this.formatter = formatter;
  }

  public SQLFormat getFormatter()
  {
    return this.formatter;
  }


  public String toString()
  {
    return this.dbmsVendor;
  }

  // ********************** Common CLASS Methods ********************** //

  public static DataBaseStrategy getInstance(String vendor)
  {
    return (DataBaseStrategy)DATABASES.get(vendor);
  }

  public static String getDriverName(String vendor)
  {
    return (getInstance(vendor)).driverName;
  }

  public static String getJdbcPrefix(String vendor)
  {
    return (getInstance(vendor)).jdbcPrefix;
  }
}
