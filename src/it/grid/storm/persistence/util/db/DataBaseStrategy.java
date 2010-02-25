package it.grid.storm.persistence.util.db;

import java.util.HashMap;
import java.util.Map;

public class DataBaseStrategy {

    private final String dbmsVendor;
    private final String driverName;
    private final String jdbcPrefix;
    private String dbName;
    private String dbPrefix;
    private String dbUrl;
    private String dbUsr;
    private String dbPwd;
    private SQLFormat formatter;
    // public static final DataBaseStrategy HSQL = new DataBaseStrategy("hsql", "org.hsqldb.jdbcDriver",
    // "jdbc:hsqldb://", null);
    public static final DataBaseStrategy MYSQL = new DataBaseStrategy("mysql",
                                                                      "com.mysql.jdbc.Driver",
                                                                      "jdbc:mysql://",
                                                                      new MySqlFormat());
    private static final Map<String, DataBaseStrategy> DATABASES = new HashMap<String, DataBaseStrategy>();

    static {
        DataBaseStrategy.DATABASES.put(DataBaseStrategy.MYSQL.toString(), DataBaseStrategy.MYSQL);
        // DATABASES.put(HSQL.toString(), HSQL);
    }

    /**
     * Prevent instantiation and subclassing with a private constructor.
     */
    private DataBaseStrategy(String dbmsVendor, String driverName, String prefix, SQLFormat formatter) {
        this.dbmsVendor = dbmsVendor;
        this.driverName = driverName;
        jdbcPrefix = prefix;
        this.formatter = formatter;
    }

    // ********************** Common Methods ********************** //

    public String getDbmsVendor() {
        return dbmsVendor;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getJdbcPrefix() {
        return jdbcPrefix;
    }

    public void setDbUsr(String usrDb) {
        dbUsr = usrDb;
    }

    public String getDbUsr() {
        return dbUsr;
    }

    public void setDbPwd(String pwd) {
        dbPwd = pwd;
    }

    public String getDbPwd() {
        return dbPwd;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbPrefix(String dbName) {
        dbPrefix = dbName;
    }

    public String getDbPrefix() {
        return dbPrefix;
    }

    public void setDbUrl(String url) {
        dbUrl = url;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getConnectionString() {
        String connStr;
        connStr = jdbcPrefix + dbUrl + "/" + dbName;
        return connStr;
    }

    public void setFormatter(SQLFormat formatter) {
        this.formatter = formatter;
    }

    public SQLFormat getFormatter() {
        return formatter;
    }

    @Override
    public String toString() {
        return dbmsVendor;
    }

    // ********************** Common CLASS Methods ********************** //

    public static DataBaseStrategy getInstance(String vendor) {
        return DataBaseStrategy.DATABASES.get(vendor);
    }

    public static String getDriverName(String vendor) {
        return (DataBaseStrategy.getInstance(vendor)).driverName;
    }

    public static String getJdbcPrefix(String vendor) {
        return (DataBaseStrategy.getInstance(vendor)).jdbcPrefix;
    }
}
