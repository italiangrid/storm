package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TStatusCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for PtPChunkCatalog. This DAO is specifically designed to connect
 * to a MySQL DB. The raw data found in those tables is pre-treated in order to
 * turn it into the Object Model of StoRM. See Method comments for further info.
 *
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the
 * object model.
 *
 * @author  EGRID - ICTP Trieste
 * @version 2.0
 * @date    September 2005
 */
public class CopyChunkDAO {

    private static final Logger log = LoggerFactory.getLogger(CopyChunkDAO.class);
    private final String driver=Configuration.getInstance().getDBDriver(); //String with the name of the class for the DB driver
    private final String url=Configuration.getInstance().getDBURL(); //String referring to the URL of the DB
    private final String password=Configuration.getInstance().getDBPassword(); //String with the password for the DB
    private final String name=Configuration.getInstance().getDBUserName();     //String with the name for the DB
    private Connection con=null; //Connection to DB - WARNING!!! It is kept open all the time!

    private final static CopyChunkDAO dao = new CopyChunkDAO(); //DAO!

    private Timer clock = null; //timer thread that will run a task to alert when reconnecting is necessary!
    private TimerTask clockTask = null; //timer task that will update the boolean signalling that a reconnection is neede!
    private long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;//milliseconds that must pass before reconnecting to DB
    private long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;//initial delay in millseconds before starting timer
    private boolean reconnect = false; //boolean that tells whether reconnection is needed because of MySQL bug!

    private CopyChunkDAO() {
        setUpConnection();
        clock = new Timer();
        clockTask = new TimerTask() {
            @Override
            public void run() {
                reconnect = true;
            }
        }; //clock task
        clock.scheduleAtFixedRate(clockTask,delay,period);
    }

    /**
     * Method that returns the only instance of the CopyChunkDAO.
     */
    public static CopyChunkDAO getInstance() {
        return dao;
    }

    /**
     * Auxiliary method that sets up the conenction to the DB.
     */
    private void setUpConnection() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url,name,password);
            if (con==null) {
                log.error("COPY CHUNK DAO! DriverManager returned a _null_ connection!");
            } else {
                logWarnings(con.getWarnings());
            }
        } catch (ClassNotFoundException e) {
            log.error("COPY CHUNK DAO! Exception in setUpConnection! "+e);
        } catch (SQLException e) {
            log.error("COPY CHUNK DAO! Exception in setUpConnection! "+e);
        }
    }

    /**
     * Auxiliary method that takes down a conenctin to the DB.
     */
    private void takeDownConnection() {
        try {
            con.close();
        } catch (SQLException e) {
            log.error("COPY CHUNK DAO! Exception in takeDownConnection method: "+e);
        }
    }

    /**
     * Auxiliary method that checks if time for resetting the connection has
     * come, and eventually takes it down and up back again.
     */
    private void checkConnection() {
        if (reconnect) {
            log.debug("COPY CHUNK DAO! Reconnecting to DB! ");
            takeDownConnection();
            setUpConnection();
            reconnect = false;
        }
    }




    /**
     * Method that queries the MySQL DB to find all entries matching the supplied
     * TRequestToken. The Collection contains the corresponding CopyChunkDataTO
     * objects.
     *
     * A complex query establishes all chunks associated with the request token,
     * by properly joining request_queue, request_Copy and status_Copy.
     * The considered fields are:
     *
     * (1) From status_Copy: the ID field which becomes the TOs primary key, and
     * statusCode.
     *
     * (2) From request_Copy: targetSURL and sourceSURL.
     *
     * (3) From request_queue: fileLifetime, config_FileStorageTypeID, s_token,
     *                         config_OverwriteID.
     *
     * In case of any error, a log gets written and an empty collection is
     * returned. No exception is returned.
     *
     * NOTE! Chunks in SRM_ABORTED status are NOT returned!
     */
    public Collection find(TRequestToken requestToken) {
        checkConnection();
        String strToken = requestToken.toString();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            //get chunks of the request
            str = "SELECT s.ID, r.s_token, r.config_FileStorageTypeID, r.config_OverwriteID, r.fileLifetime, c.sourceSURL, c.targetSURL, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "+
            "FROM request_queue r JOIN (request_Copy c, status_Copy s) "+
            "ON (c.request_queueID=r.ID AND s.request_CopyID=c.ID) "+
            "LEFT JOIN request_DirOption d ON c.request_DirOptionID=d.ID "+
            "WHERE r.r_token=? AND s.statusCode<>?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            List list = new ArrayList();
            find.setString(1,strToken);
            logWarnings(find.getWarnings());
            find.setInt(2,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED) );
            logWarnings(find.getWarnings());
            log.debug("COPY CHUNK DAO: find method; "+find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            CopyChunkDataTO aux = null;
            while (rs.next()) {
                aux = new CopyChunkDataTO();
                aux.setPrimaryKey(rs.getLong("s.ID"));
                aux.setRequestToken(strToken);
                aux.setSpaceToken(rs.getString("r.s_token"));
                aux.setFileStorageType(rs.getString("r.config_FileStorageTypeID"));
                aux.setOverwriteOption(rs.getString("r.config_OverwriteID"));
                aux.setLifeTime(rs.getInt("r.fileLifetime"));
                aux.setFromSURL(rs.getString("c.sourceSURL"));
                aux.setToSURL(rs.getString("c.targetSURL"));
                list.add(aux);
            }
            close(rs);
            close(find);
            return list;
        } catch (SQLException e) {
            log.error("COPY CHUNK DAO: "+e);
            close(rs);
            close(find);
            return new ArrayList(); //return empty Collection!
        }
    }



    /**
     * Method used to save the changes made to a retrieved CopyChunkDataTO,
     * back into the MySQL DB.
     *
     * Only statusCode and explanation, of status_Copy table get written to the
     * DB. Likewise for fileLifetime of request_queue table.
     *
     * In case of any error, an error messagge gets logged but no exception is
     * thrown.
     */
    public void update(CopyChunkDataTO to) {
        checkConnection();
        PreparedStatement updateFileReq = null;
        PreparedStatement updateReq = null;
        try {
            //ready updateFileReq...
            updateFileReq = con.prepareStatement("UPDATE request_queue r JOIN (status_Copy s, request_Copy c) ON (r.ID=c.request_queueID AND s.request_CopyID=c.ID) SET s.statusCode=?, s.explanation=?, r.fileLifetime=?, r.config_FileStorageTypeID=?, r.config_OverwriteID=? WHERE s.ID=?");
            logWarnings(con.getWarnings());
            updateFileReq.setInt(1,to.status());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setString(2,to.errString());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setInt(3,to.lifeTime());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setString(4,to.fileStorageType());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setString(5,to.overwriteOption());
            logWarnings(updateFileReq.getWarnings());
            updateFileReq.setLong(6,to.primaryKey());
            logWarnings(updateFileReq.getWarnings());
            //run updateFileReq
            updateFileReq.executeUpdate();
            logWarnings(updateFileReq.getWarnings());
        } catch (SQLException e) {
            log.error("COPY CHUNK DAO: Unable to complete update! "+e);
        } finally {
            close(updateFileReq);
        }
    }

    /**
     * Method used in extraordinary situations to signal that data retrieved from
     * the DB was malformed and could not be translated into the StoRM object model.
     *
     * This method attempts to change the status of the request to SRM_FAILURE and
     * record it in the DB.
     *
     * This operation could potentially fail because the source of the malformed
     * problems could be a problematic DB; indeed, initially only log messagges
     * where recorded.
     *
     * Yet it soon became clear that the source of malformed data were the clients
     * and/or FE recording info in the DB. In these circumstances the client would
     * its request as being in the SRM_IN_PROGRESS state for ever. Hence the pressing
     * need to inform it of the encountered problems.
     */
    public void signalMalformedCopyChunk(CopyChunkDataTO auxTO) {
        checkConnection();
        String signalSQL = "UPDATE status_Copy s "+
        "SET s.statusCode="+ StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE) +", s.explanation=? "+
        "WHERE s.ID="+auxTO.primaryKey();
        PreparedStatement signal = null;
        try {
            //update storm_put_filereq;
            signal = con.prepareStatement(signalSQL);
            logWarnings(con.getWarnings());
            signal.setString(1,"Request is malformed!"); //Prepared statement spares DB-specific String notation!
            logWarnings(signal.getWarnings());
            signal.executeUpdate();
            logWarnings(signal.getWarnings());
        } catch (SQLException e) {
            log.error("PtPChunkDAO! Unable to signal in DB that the request was malformed! Request: "+auxTO.toString()+"; Exception: "+e.toString());
        } finally {
            close(signal);
        }
    }



    /**
     * Auxiliary method used to roll back a failed transaction
     */
    private void rollback(Connection con) {
        if (con!=null) {
            try {
                con.rollback();
                log.error("COPY CHUNK DAO: roll back successful!");
            } catch (SQLException e2) {
                log.error("COPY CHUNK DAO: roll back failed! "+e2);
            }
        }
    }

    /**
     * Auxiliary method used to close a Statement
     */
    private void close(Statement stmt) {
        if (stmt!=null) {
            try {
                stmt.close();
            } catch (Exception e) {
                log.error("COPY CHUNK DAO! Unable to close Statement "+stmt.toString()+" - Exception: "+e);
            }
        }
    }

    /**
     * Auxiliary method used to close a ResultSet
     */
    private void close(ResultSet rset) {
        if (rset!=null) {
            try {
                rset.close();
            } catch (Exception e) {
                log.error("COPY CHUNK DAO! Unable to close ResultSet! Exception: "+e);
            }
        }
    }

    /**
     * Auxiliary private method that logs all SQL warnings.
     */
    private void logWarnings(SQLWarning w) {
        if (w!=null) {
            log.debug("COPY CHUNK DAO: "+w.toString());
            while ((w=w.getNextWarning())!=null) {
                log.debug("COPY CHUNK DAO: "+w.toString());
            }
        }
    }
}
