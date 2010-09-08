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
    
    /* String with the name of the class for the DB driver*/
    private final String driver=Configuration.getInstance().getDBDriver();
    /* String referring to the URL of the DB */
    private final String url=Configuration.getInstance().getDBURL(); 
    /* String with the password for the DB */
    private final String password=Configuration.getInstance().getDBPassword(); 
    /* String with the name for the DB */
    private final String name=Configuration.getInstance().getDBUserName();
    
    /* Connection to DB - WARNING!!! It is kept open all the time! */
    private Connection con=null;
    /* boolean that tells whether reconnection is needed because of MySQL bug! */
    private boolean reconnect = false;
    
    /* Singleton instance */
    private final static CopyChunkDAO dao = new CopyChunkDAO();

    /* timer thread that will run a task to alert when reconnecting is necessary! */
    private Timer clock = null;
    /* timer task that will update the boolean signaling that a reconnection is needed! */
    private TimerTask clockTask = null; 
    /* milliseconds that must pass before reconnecting to DB */
    private long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;
    /* initial delay in milliseconds before starting timer */
    private long delay = Configuration.getInstance().getDBReconnectDelay() * 1000; 
    

	private CopyChunkDAO() {

		setUpConnection();
		clock = new Timer();
		clockTask = new TimerTask()
		{
			@Override
			public void run() {

				reconnect = true;
			}
		}; // clock task
		clock.scheduleAtFixedRate(clockTask, delay, period);
	}

    /**
     * Method that returns the only instance of the CopyChunkDAO.
     */
    public static CopyChunkDAO getInstance() {
        return dao;
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
		try
		{
			// TODO MICHELE USER_SURL set new fields
			// ready updateFileReq...
			updateFileReq = con.prepareStatement("UPDATE request_queue rq JOIN (status_Copy sc, request_Copy rc) " +
										"ON (rq.ID=rc.request_queueID AND sc.request_CopyID=rc.ID) " +
										"SET sc.statusCode=?, sc.explanation=?, rq.fileLifetime=?, rq.config_FileStorageTypeID=?, rq.config_OverwriteID=?, " +
										"rc.normalized_sourceSURL_StFN=?, rc.sourceSURL_uniqueID=?, rc.normalized_targetSURL_StFN=?, rc.targetSURL_uniqueID=? " +
										"WHERE rc.ID=?");
			logWarnings(con.getWarnings());
			
			updateFileReq.setInt(1, to.status());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setString(2, to.errString());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setInt(3, to.lifeTime());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setString(4, to.fileStorageType());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setString(5, to.overwriteOption());
			logWarnings(updateFileReq.getWarnings());
			
			// TODO MICHELE USER_SURL fill new fields
			updateFileReq.setString(6, to.normalizedSourceStFN());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setInt(7, to.sourceSurlUniqueID());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setString(8, to.normalizedTargetStFN());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setInt(9, to.targetSurlUniqueID());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setLong(10, to.primaryKey());
			logWarnings(updateFileReq.getWarnings());
			
			// run updateFileReq
			updateFileReq.executeUpdate();
			logWarnings(updateFileReq.getWarnings());
		} catch(SQLException e)
		{
			log.error("COPY CHUNK DAO: Unable to complete update! " + e);
		} finally
		{
			close(updateFileReq);
		}
	}
    
	/**
	 * Updates the request_Get represented by the received ReducedPtGChunkDataTO by
	 * setting its normalized_sourceSURL_StFN and sourceSURL_uniqueID
	 * 
	 * @param chunkTO
	 */
	//TODO MICHELE USER_SURL new method
	public void updateIncomplete(ReducedCopyChunkDataTO chunkTO) {

		checkConnection();
		String str = "UPDATE request_Copy SET normalized_sourceSURL_StFN=?, sourceSURL_uniqueID=?, normalized_targetSURL_StFN=?, targetSURL_uniqueID=? "
						 + "WHERE ID=?";
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			
			stmt.setString(1, chunkTO.normalizedSourceStFN());
			logWarnings(stmt.getWarnings());
			
			stmt.setInt(2, chunkTO.sourceSurlUniqueID());
			logWarnings(stmt.getWarnings());
			
			stmt.setString(3, chunkTO.normalizedTargetStFN());
			logWarnings(stmt.getWarnings());
			
			stmt.setInt(4, chunkTO.targetSurlUniqueID());
			logWarnings(stmt.getWarnings());

			stmt.setLong(5, chunkTO.primaryKey());
			logWarnings(stmt.getWarnings());
			
			log.trace("COPY CHUNK DAO - update incomplete: " + stmt.toString());
			stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
		} catch(SQLException e)
		{
			log.error("COPY CHUNK DAO: Unable to complete update incomplete! " + e);
		} finally
		{
			close(stmt);
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
	public Collection<CopyChunkDataTO> find(TRequestToken requestToken) {

		checkConnection();
		String strToken = requestToken.toString();
		String str = null;
		PreparedStatement find = null;
		ResultSet rs = null;
		try
		{
			//TODO MICHELE USER_SURL get new fields
			/* get chunks of the request */
			str =
				  "SELECT rq.s_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.fileLifetime, rc.ID, rc.sourceSURL, rc.targetSURL, rc.normalized_sourceSURL_StFN, rc.sourceSURL_uniqueID, rc.normalized_targetSURL_StFN, rc.targetSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels " 
					  + "FROM request_queue rq JOIN (request_Copy rc, status_Copy sc) "
					  + "ON (rc.request_queueID=rq.ID AND sc.request_CopyID=rc.ID) "
					  + "LEFT JOIN request_DirOption d ON rc.request_DirOptionID=d.ID "
					  + "WHERE rq.r_token=? AND sc.statusCode<>?";
			
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			
			ArrayList<CopyChunkDataTO> list = new ArrayList<CopyChunkDataTO>();
			find.setString(1, strToken);
			logWarnings(find.getWarnings());
			
			find.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
			logWarnings(find.getWarnings());
			
			log.debug("COPY CHUNK DAO: find method; " + find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			
			CopyChunkDataTO chunkDataTO;
			while(rs.next())
			{
				chunkDataTO = new CopyChunkDataTO();
				chunkDataTO.setRequestToken(strToken);
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
				chunkDataTO.setFileStorageType(rs.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setLifeTime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setPrimaryKey(rs.getLong("rc.ID"));
				chunkDataTO.setFromSURL(rs.getString("rc.sourceSURL"));
				// TODO MICHELE USER_SURL fill new fields
				chunkDataTO.setNormalizedSourceStFN(rs.getString("rc.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if(!rs.wasNull())
				{
					chunkDataTO.setSourceSurlUniqueID(new Integer(uniqueID));
				}
				
				chunkDataTO.setToSURL(rs.getString("rc.targetSURL"));
				// TODO MICHELE USER_SURL fill new fields
				chunkDataTO.setNormalizedTargetStFN(rs.getString("rc.normalized_sourceSURL_StFN"));
				uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if(!rs.wasNull())
				{
					chunkDataTO.setTargetSurlUniqueID(new Integer(uniqueID));
				}

				list.add(chunkDataTO);
			}
			return list;
		} catch(SQLException e)
		{
			log.error("COPY CHUNK DAO: " + e);
			/* return empty Collection! */
			return new ArrayList<CopyChunkDataTO>();
		} finally
		{
			close(rs);
			close(find);
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
		String signalSQL =
						   "UPDATE status_Copy SET statusCode="
							   + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE)
							   + ", explanation=? WHERE request_CopyID=" + auxTO.primaryKey();
		
		PreparedStatement signal = null;
		try
		{
			/* update storm_put_filereq */
			signal = con.prepareStatement(signalSQL);
			logWarnings(con.getWarnings());
			
			/* Prepared statement spares DB-specific String notation! */
			signal.setString(1, "Request is malformed!");
			logWarnings(signal.getWarnings());
			
			signal.executeUpdate();
			logWarnings(signal.getWarnings());
		} catch(SQLException e)
		{
			log.error("CopyChunkDAO! Unable to signal in DB that the"
				+ "request was malformed! Request: " + auxTO.toString() + "; Exception: "
				+ e.toString());
		} finally
		{
			close(signal);
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
    
    /**
     * Auxiliary method that sets up the conenction to the DB.
     */
	private void setUpConnection() {

		try
		{
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			if(con == null)
			{
				log.error("COPY CHUNK DAO! DriverManager returned a _null_ connection!");
			}
			else
			{
				logWarnings(con.getWarnings());
			}
		} catch(ClassNotFoundException e)
		{
			log.error("COPY CHUNK DAO! Exception in setUpConnection! " + e);
		} catch(SQLException e)
		{
			log.error("COPY CHUNK DAO! Exception in setUpConnection! " + e);
		}
	}

	 /**
     * Auxiliary method that checks if time for resetting the connection has
     * come, and eventually takes it down and up back again.
     */
	private void checkConnection() {

		if(reconnect)
		{
			log.debug("COPY CHUNK DAO! Reconnecting to DB! ");
			takeDownConnection();
			setUpConnection();
			reconnect = false;
		}
	}
	
    /**
     * Auxiliary method that takes down a conenctin to the DB.
     */
	private void takeDownConnection() {

		try
		{
			con.close();
		} catch(SQLException e)
		{
			log.error("COPY CHUNK DAO! Exception in takeDownConnection method: " + e);
		}
	}
}
