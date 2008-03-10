package it.grid.storm.namespace.admin.server;

import java.sql.*;
import java.util.logging.*;

import org.hsqldb.*;
import org.quickserver.net.server.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: INFN-CNAF</p>
 *
 * @author R.Zappi
 * @version 1.0
 */
public class NSAdminDBUtil {

    private static final Logger logger = Logger.getLogger("it.grid.storm.namespace.admin.client");
    static {
        System.setProperty("java.util.logging.config.file", "./etc/adminserver-log.properties");
        try {
            LogManager.getLogManager().readConfiguration();
        }
        catch (Exception e) {
            System.err.println("Could not read logging configuration: " + e.getMessage());
            System.err.println("Proceeding...");
        }
    }

    private static String dbURL;
    private static String dbUser;
    private static String dbPassword;
    private static Server server;
    private static boolean isNetwork;
    private static String serverProps;

    private static NSAdminDBUtil instance = null;

    private NSAdminDBUtil() {
        initValues();
        setUp();
    }

    private void initValues() {
        //dbURL = "jdbc:hsqldb:file:..\\db\\nsadmindb;sql.enforce_strict_size=true";
        //dbURL = "jdbc:hsqldb:mem:aname;sql.enforce_strict_size=true";
        dbURL = "jdbc:hsqldb:hsql://localhost/xdb;sql.enforce_strict_size=true";

        dbUser = "sa";
        dbPassword = "";
        isNetwork = false;
        server = null;
    }

    private void setUp() {
        if (isNetwork) {
            serverProps = "database.0=mem:test;sql.enforce_strict_size=true";
            server = new Server();
            server.putPropertiesFromString(serverProps);
            server.setLogWriter(null);
            server.setErrWriter(null);
            server.start();
        }
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        }
        catch (Exception e) {
            System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();
            return;
        }
    }

    private static NSAdminDBUtil getInstance() {
        if (instance == null) {
            instance = new NSAdminDBUtil();
        }
        return instance;
    }

    private static Connection newConnection() {
        instance = getInstance();
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        }
        catch (SQLException ex) {
            System.out.println("ERROR: failed to load HSQLDB JDBC driver.");
            ex.printStackTrace();
            return null;
        }
        return conn;
    }

    /***************************************************************************
     *   Utility to manage Authorization Data Base
     ***************************************************************************/


    public static void shutdownDB() {
        if (instance != null) {
            if (isNetwork) {
                server.stop();
                server = null;
            }
        }
    }

    public static Connection getConnection() throws NSAdminException {
        Connection result = newConnection();
        if (result == null) {
            throw new NSAdminException();
        }
        return result;
    }

    /**
     *
     * @param login_id String
     * @param passwd String
     * @param userName String
     * @param welcomeMsg String
     */
    public static void addNewUserIntoDB(String login_id, String passwd, String userName, String welcomeMsg) {
        String prepStat;
        PreparedStatement ps = null;
        Connection connection = null;

        prepStat = "INSERT INTO USERAUTHZ (LOGIN_ID,PASSWORD,USER_NAME,WELCOME_MSG,CREATE_DATE) " +
            "VALUES (?,?,?,?,?)";
        try {
            connection = getConnection();
            ps = connection.prepareStatement(prepStat);
            ps.setString(1, login_id);
            ps.setString(2, passwd);
            ps.setString(3, userName);
            ps.setString(4, welcomeMsg);
            ps.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            int res = ps.executeUpdate();
            System.out.println("Insert Res = " + res);
        }

        catch (NSAdminException nsex) {
            nsex.printStackTrace();
        }
        catch (SQLException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }
        finally {
            close(ps);
        }
    }

    public static int countUser() {
        Connection connection;
        Statement stat = null;
        int result = -1;
        ResultSet r = null;
        try {
            connection = getConnection();
            stat = connection.createStatement();
            r = stat.executeQuery("SELECT count(*) FROM USERAUTHZ;");

            //Create NSClientData
            if (r.next()) {
                result = r.getInt(1);
                System.out.println(result);
            }
            r.close();
        }
        catch (NSAdminException nsex) {
            nsex.printStackTrace();
        }
        catch (SQLException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }
        finally {
            close(r);
            close(stat);
        }
        return result;

    }

    public static boolean retrieveUser(ClientHandler clientHandler, String loginId, String passwd) {
        Connection connection;
        Statement stat;
        NSClientData clientData = (NSClientData) clientHandler.getClientData();
        boolean result = false;
        try {
            connection = getConnection();
            stat = connection.createStatement();
            ResultSet r = stat.executeQuery(
                "SELECT user_id, user_name, welcome_msg, create_date, last_connection_date, last_from_locality " +
                "FROM USERAUTHZ " +
                "WHERE login_id='" + loginId + "' AND PASSWORD='" + passwd + "'");

            //Create NSClientData
            if (r.next()) {
                System.out.println("User ID = " + r.getInt(1));
                clientData.setLoginID(loginId);
                clientData.setPassword(passwd);
                clientData.setUsername(r.getString(2));
                clientData.setWelcomeMsg(r.getString(3));
                clientData.setCreationDate( (r.getTimestamp(4)).getTime());
                java.sql.Timestamp timeStamp = r.getTimestamp(5);
                if (timeStamp != null) {
                    clientData.setLastConnectionTime(timeStamp.getTime());
                }
                clientData.setFromWhere(r.getString(6));

                System.out.println(clientData);
                result = true;
            }
            else {
                //User is not authorized to access!
                //Send msg to Client.
                /** @todo Handle this case */
                result = false;
            }
            connection.close();
        }
        catch (NSAdminException nsex) {
            nsex.printStackTrace();
        }
        catch (SQLException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }
        System.out.println(" --- returning validate : " + result);
        return result;
    }

    /**
     * Only for test
     *
     * @param loginId String
     * @param passwd String
     * @return boolean
     */
    public static boolean retrieveUser(String loginId, String passwd) {
        Connection connection;
        Statement stat;
        NSClientData clientData = new NSClientData();
        boolean result = false;
        try {
            connection = getConnection();
            stat = connection.createStatement();
            ResultSet r = stat.executeQuery(
                "SELECT user_id, user_name, welcome_msg, create_date, last_connection_date, last_from_locality " +
                "FROM USERAUTHZ " +
                "WHERE login_id='" + loginId + "' AND PASSWORD='" + passwd + "'");

            //Create NSClientData
            if (r.next()) {
                System.out.println("User ID = " + r.getInt(1));
                clientData.setLoginID(loginId);
                clientData.setPassword(passwd);
                clientData.setUsername(r.getString(2));
                clientData.setWelcomeMsg(r.getString(3));
                clientData.setCreationDate( (r.getTimestamp(4)).getTime());
                java.sql.Timestamp timeStamp = r.getTimestamp(5);
                if (timeStamp != null) {
                    clientData.setLastConnectionTime(timeStamp.getTime());
                }
                clientData.setFromWhere(r.getString(6));
                System.out.println(clientData);
                result = true;
            }
            else {
                //User is not authorized to access!
                //Send msg to Client.
                /** @todo Handle this case */
                result = false;
            }
            connection.close();
        }
        catch (NSAdminException nsex) {
            nsex.printStackTrace();
        }
        catch (SQLException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }
        return result;
    }

    public static boolean updateAuthorizedClientData(NSClientData clientData, long newAccessTime,
        String newFromLocality) {
        boolean result = false;
        Connection connection = null;
        PreparedStatement updateStat = null;

        String expUpdateClient = "UPDATE USERAUTHZ   SET LAST_CONNECTION_DATE = ? ";
        expUpdateClient += ", LAST_FROM_LOCALITY = ? ";
        expUpdateClient += " WHERE LOGIN_ID = ? ";
        System.out.println("Query : " + expUpdateClient);

        try {
            connection = getConnection();
            updateStat = connection.prepareStatement(expUpdateClient);

            updateStat.setTimestamp(1, new java.sql.Timestamp(newAccessTime));
            updateStat.setString(2, newFromLocality);
            updateStat.setString(3, clientData.getLoginID());

            int i = updateStat.executeUpdate(); // run the query
            if (i == -1) {
                System.out.println("db error : " + expUpdateClient);
            }

        }
        catch (SQLException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }
        catch (NSAdminException ex) {
            ex.printStackTrace();
            /** @todo Handle this exception */
        }
        finally {
            close(updateStat);
        }
        return result;
    }

    /**
     * Auxiliary method used to close a Statement
     */
    private static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (Exception e) {
                logger.warning("Unable to close Statement " + stmt.toString() + " - Exception: " + e);
            }
        }
    }

    /**
     * Auxiliary method used to close a ResultSet
     */
    private static void close(ResultSet rset) {
        if (rset != null) {
            try {
                rset.close();
            }
            catch (Exception e) {
                logger.warning("Unable to close ResultSet! Exception: " + e);
            }
        }
    }

    public static void createDB() {
        String ddl0 = "DROP TABLE USERAUTHZ IF EXISTS;";
        /**
          create table TESTTABLE (
              USER_ID                integer         not null          identity  [primary key   auto_increment],
              LOGIN_ID               varchar(10)     not null,
              PASSWORD               varchar(10)     default ''        not null,
              USER_NAME              varchar(16)     default 'n.a.'    not null,
              WELCOME_MSG            varchar(200),
              CREATE_DATE            timestamp       default current_timestamp   not null,
              LAST_CONNECTION_DATE   timestamp
              LAST_FROM_LOCALITY     varchar(200)
           );
         **/

        String ddl1 =
            "CREATE TABLE USERAUTHZ(USER_ID INTEGER NOT NULL IDENTITY,LOGIN_ID VARCHAR(10) NOT NULL," +
            "PASSWORD VARCHAR(10) DEFAULT '' NOT NULL,USER_NAME VARCHAR(16) DEFAULT 'n.a.' NOT NULL," +
            "WELCOME_MSG VARCHAR(200),CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
            "LAST_CONNECTION_DATE TIMESTAMP, LAST_FROM_LOCALITY VARCHAR(200),CONSTRAINT IXUQ_LOGIN_ID0 UNIQUE(LOGIN_ID))";

        try {
            Statement stmnt;
            Connection connection = getConnection();
            stmnt = connection.createStatement();
            stmnt.executeUpdate(ddl0);
            stmnt.executeUpdate(ddl1);
            stmnt.close();
        }
        catch (NSAdminException nsex) {
            nsex.printStackTrace();
        }
        catch (SQLException ex) {
            /** @todo Handle this exception */
            ex.printStackTrace();
        }

    }

}
