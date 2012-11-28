/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for BoLChunkCatalog. This DAO is specifically designed to connect to a MySQL DB. The raw data found in
 * those tables is pre-treated in order to turn it into the Object Model of StoRM. See Method comments for further info.
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the object model.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLChunkDAO {

    private static final Logger log = LoggerFactory.getLogger(BoLChunkDAO.class);

    /** String with the name of the class for the DB driver */
    private final String driver = Configuration.getInstance().getDBDriver();
    /** String referring to the URL of the DB */
    private final String url = Configuration.getInstance().getDBURL();
    /** String with the password for the DB */
    private final String password = Configuration.getInstance().getDBPassword();
    /** String with the name for the DB */
    private final String name = Configuration.getInstance().getDBUserName();
    /** Connection to DB - WARNING!!! It is kept open all the time! */
    private Connection con = null;
    private final static BoLChunkDAO dao = new BoLChunkDAO();

    /** timer thread that will run a taask to alert when reconnecting is necessary! */
    private Timer clock = null;
    /** timer task that will update the boolean signalling that a reconnection is neede! */
    private TimerTask clockTask = null;
    /** milliseconds that must pass before reconnecting to DB */
    private long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;
    /** initial delay in millseconds before starting timer */
    private long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;
    /** boolean that tells whether reconnection is needed because of MySQL bug! */
    private boolean reconnect = false;

    private BoLChunkDAO() {

        setUpConnection();

        clock = new Timer();
        clockTask = new TimerTask() {
            @Override
            public void run() {
                reconnect = true;
            }
        }; // clock task
        clock.scheduleAtFixedRate(clockTask, delay, period);
    }

    /**
     * Method that returns the only instance of the BoLChunkDAO.
     */
    public static BoLChunkDAO getInstance() {
        return dao;
    }

    /**
     * Method used to add a new record to the DB: the supplied BoLChunkDataTO gets its primaryKey changed to the one
     * assigned by the DB. The supplied BoLChunkData is used to fill in only the DB table where file specific info gets
     * recorded: it does _not_ add a new request! So if spurious data is supplied, it will just stay there because of a
     * lack of a parent request!
     */
    public synchronized void addChild(BoLChunkDataTO to) {
       
    	if(!checkConnection())
    	{
            log.error("BoL CHUNK DAO: addChild - unable to get a valid connection!");
            return;
    	}
        String str = null;
        PreparedStatement id = null; // statement to find out the ID associated to the request token
        ResultSet rsid = null; // result set containing the ID of the request.
        // insertion
        try {

            // WARNING!!!! We are forced to run a query to get the ID of the request, which should
            // NOT be so
            // because the corresponding request object should have been changed with the extra
            // field! However, it is not possible
            // at the moment to perform such change because of strict deadline and the change could
            // wreak havoc
            // the code. So we are forced to make this query!!!

            // begin transaction
            con.setAutoCommit(false);
            logWarnings(con.getWarnings());

            // find ID of request corresponding to given RequestToken
            str = "SELECT rq.ID FROM request_queue rq WHERE rq.r_token=?";
            
            id = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            
            id.setString(1, to.getRequestToken());
            logWarnings(id.getWarnings());
            
            log.debug("BoL CHUNK DAO: addChild; " + id.toString());
            rsid = id.executeQuery();
            logWarnings(id.getWarnings());
            
            /* ID of request in request_process! */
            int request_id = extractID(rsid); 
            int id_s = fillBoLTables(to, request_id);
            
            // end transaction!
            con.commit();
            logWarnings(con.getWarnings());
            con.setAutoCommit(true);
            logWarnings(con.getWarnings());

            // update primary key reading the generated key
            to.setPrimaryKey(id_s);
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: unable to complete addChild! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } catch (Exception e) {
            log.error("BoL CHUNK DAO: unable to complete addChild! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } finally {
            close(rsid);
            close(id);
        }
    }

    /**
     * Method used to add a new record to the DB: the supplied BoLChunkDataTO gets its primaryKey changed to the one
     * assigned by the DB. The client_dn must also be supplied as a String. The supplied BoLChunkData is used to fill in
     * all the DB tables where file specific info gets recorded: it _adds_ a new request!
     */
    public synchronized void addNew(BoLChunkDataTO to, String client_dn) {
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: addNew - unable to get a valid connection!");
            return;
        }
        String str = null;
        /* Result set containing the ID of the inserted  new request */
        ResultSet rs_new = null; 
        /* Insert new request into process_request */
        PreparedStatement addNew = null; 
        /* Insert protocols for request. */
        PreparedStatement addProtocols = null; // insert protocols for request.
        try {
            // begin transaction
            con.setAutoCommit(false);
            logWarnings(con.getWarnings());

            // add to request_queue...
            str = "INSERT INTO request_queue (config_RequestTypeID,client_dn,pinLifetime,status,errstring,r_token,nbreqfiles,timeStamp,deferredStartTime) VALUES (?,?,?,?,?,?,?,?,?)";
            addNew = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            /* request type set to bring online */
            addNew.setString(1, RequestTypeConverter.getInstance().toDB(TRequestType.BRING_ON_LINE)); 
            logWarnings(addNew.getWarnings());
            
            addNew.setString(2, client_dn);
            logWarnings(addNew.getWarnings());
            
            addNew.setInt(3, to.getLifeTime());
            logWarnings(addNew.getWarnings());
            
            addNew.setInt(4, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_INPROGRESS));
            logWarnings(addNew.getWarnings());
            
            addNew.setString(5, "New BoL Request resulting from srmCopy invocation.");
            logWarnings(addNew.getWarnings());
            
            addNew.setString(6, to.getRequestToken());
            logWarnings(addNew.getWarnings());
            
            addNew.setInt(7, 1); // number of requested files set to 1!
            logWarnings(addNew.getWarnings());
            
            addNew.setTimestamp(8, new Timestamp(new Date().getTime()));
            logWarnings(addNew.getWarnings());
            
            addNew.setInt(9, to.getDeferredStartTime());
            logWarnings(addNew.getWarnings());
            
            log.trace("BoL CHUNK DAO: addNew; " + addNew.toString());
            addNew.execute();
            logWarnings(addNew.getWarnings());
            
            rs_new = addNew.getGeneratedKeys();
            int id_new = extractID(rs_new);

            // add protocols...
            str = "INSERT INTO request_TransferProtocols (request_queueID,config_ProtocolsID) VALUES (?,?)";
            addProtocols = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            for (Iterator<String> i = to.getProtocolList().iterator(); i.hasNext();) {
                addProtocols.setInt(1, id_new);
                logWarnings(addProtocols.getWarnings());
                
                addProtocols.setString(2, (String) i.next());
                logWarnings(addProtocols.getWarnings());
                
                log.trace("BoL CHUNK DAO: addNew; " + addProtocols.toString());
                addProtocols.execute();
                logWarnings(addProtocols.getWarnings());
            }

            // addChild...
            int id_s = fillBoLTables(to, id_new);

            // end transaction!
            con.commit();
            logWarnings(con.getWarnings());
            con.setAutoCommit(true);
            logWarnings(con.getWarnings());

            // update primary key reading the generated key
            to.setPrimaryKey(id_s);
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: Rolling back! Unable to complete addNew! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } catch (Exception e) {
            log.error("BoL CHUNK DAO: unable to complete addNew! BoLChunkDataTO: " + to
                    + "; exception received:" + e);
            rollback(con);
        } finally {
            close(rs_new);
            close(addNew);
            close(addProtocols);
        }
    }

    /**
	 * To be used inside a transaction
	 * @param to
	 * @param requestQueueID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	private synchronized int fillBoLTables(BoLChunkDataTO to, int requestQueueID)
			throws SQLException, Exception {

		String str = null;
		/* Result set containing the ID of the inserted */
		ResultSet rs_do = null;
		/* Result set containing the ID of the inserted */
		ResultSet rs_b = null; 
		/* Result set containing the ID of the inserted */
		ResultSet rs_s = null; 
		/* insert TDirOption for request */
		PreparedStatement addDirOption = null;
		/* insert request_Bol for request */
		PreparedStatement addBoL = null; 
		PreparedStatement addChild = null;
		
		try
		{
			 // first fill in TDirOption
            str = "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";
            addDirOption = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addDirOption.setBoolean(1, to.getDirOption());
            logWarnings(addDirOption.getWarnings());
            
            addDirOption.setBoolean(2, to.getAllLevelRecursive());
            logWarnings(addDirOption.getWarnings());
            
            addDirOption.setInt(3, to.getNumLevel());
            logWarnings(addDirOption.getWarnings());
            
            log.trace("BoL CHUNK DAO: addNew; " + addDirOption.toString());
            addDirOption.execute();
            logWarnings(addDirOption.getWarnings());
            
            rs_do = addDirOption.getGeneratedKeys();
            int id_do = extractID(rs_do);

            // second fill in request_BoL... sourceSURL and TDirOption!
            str = "INSERT INTO request_BoL (request_DirOptionID,request_queueID,sourceSURL,normalized_sourceSURL_StFN,sourceSURL_uniqueID) VALUES (?,?,?,?,?)";
            addBoL = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addBoL.setInt(1, id_do);
            logWarnings(addBoL.getWarnings());
            
            addBoL.setInt(2, requestQueueID);
            logWarnings(addBoL.getWarnings());
            
            addBoL.setString(3, to.getFromSURL());
            logWarnings(addBoL.getWarnings());
            
            //TODO MICHELE USER_SURL set new fields
            addBoL.setString(4, to.normalizedStFN());
			logWarnings(addBoL.getWarnings());
			
			addBoL.setInt(5, to.sulrUniqueID());
			logWarnings(addBoL.getWarnings());
            
            log.trace("BoL CHUNK DAO: addNew; " + addBoL.toString());
            addBoL.execute();
            logWarnings(addBoL.getWarnings());
            
            rs_b = addBoL.getGeneratedKeys();
            int id_g = extractID(rs_b);

            // third fill in status_BoL...
            str = "INSERT INTO status_BoL (request_BoLID,statusCode,explanation) VALUES (?,?,?)";
            addChild = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
            logWarnings(con.getWarnings());
            addChild.setInt(1, id_g);
            logWarnings(addChild.getWarnings());
            
            addChild.setInt(2, to.getStatus());
            logWarnings(addChild.getWarnings());
            
            addChild.setString(3, to.getErrString());
            logWarnings(addChild.getWarnings());
            
            log.trace("BoL CHUNK DAO: addNew; " + addChild.toString());
            addChild.execute();
            logWarnings(addChild.getWarnings());
            
//            rs_s = addChild.getGeneratedKeys();
//			return extractID(rs_s);
            return id_g;
		} finally
		{
			close(rs_do);
			close(rs_b);
			close(rs_s);
			close(addDirOption);
			close(addBoL);
			close(addChild);
		}
	}
    
    /**
     * Method used to save the changes made to a retrieved BoLChunkDataTO, back into the MySQL DB. Only the fileSize,
     * statusCode and explanation, of status_BoL table are written to the DB. Likewise for the request pinLifetime. In
     * case of any error, an error message gets logged but no exception is thrown.
     */
    public synchronized void update(BoLChunkDataTO to) {
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: update - unable to get a valid connection!");
            return;
        }
        PreparedStatement updateFileReq = null;
        try 
        {
        	// TODO MICHELE USER_SURL set new fields
            // ready updateFileReq...
            updateFileReq = con.prepareStatement("UPDATE request_queue rq JOIN (status_BoL sb, request_BoL rb) ON (rq.ID=rb.request_queueID AND sb.request_BoLID=rb.ID)" +
            		" SET sb.fileSize=?, sb.statusCode=?, sb.explanation=?, rq.pinLifetime=?, rb.normalized_sourceSURL_StFN=?, rb.sourceSURL_uniqueID=?" +
            		" WHERE rb.ID=?");
            logWarnings(con.getWarnings());
            updateFileReq.setLong(1, to.getFileSize());
            logWarnings(updateFileReq.getWarnings());
            
            updateFileReq.setInt(2, to.getStatus());
            logWarnings(updateFileReq.getWarnings());
            
            updateFileReq.setString(3, to.getErrString());
            logWarnings(updateFileReq.getWarnings());
            
            updateFileReq.setInt(4, to.getLifeTime());
            logWarnings(updateFileReq.getWarnings());
            
         // TODO MICHELE USER_SURL fill new fields
			updateFileReq.setString(5, to.normalizedStFN());
			logWarnings(updateFileReq.getWarnings());
			
			updateFileReq.setInt(6, to.sulrUniqueID());
			logWarnings(updateFileReq.getWarnings());

            updateFileReq.setLong(7, to.getPrimaryKey());
            logWarnings(updateFileReq.getWarnings());
            // execute update
            log.trace("BoL CHUNK DAO: update method; " + updateFileReq.toString());
            updateFileReq.executeUpdate();
            logWarnings(updateFileReq.getWarnings());
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: Unable to complete update! " + e);
        } finally {
            close(updateFileReq);
        }
    }
    
    
    /**
	 * Updates the request_Bol represented by the received ReducedBoLChunkDataTO by
	 * setting its normalized_sourceSURL_StFN and sourceSURL_uniqueID
	 * 
	 * @param chunkTO
	 */
	//TODO MICHELE USER_SURL new method
    public synchronized void updateIncomplete(ReducedBoLChunkDataTO chunkTO) {

	    if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: updateIncomplete - unable to get a valid connection!");
            return;
        }
		String str = "UPDATE request_BoL SET normalized_sourceSURL_StFN=?, sourceSURL_uniqueID=? "
						 + "WHERE ID=?";
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			
			stmt.setString(1, chunkTO.normalizedStFN());
			logWarnings(stmt.getWarnings());
			
			stmt.setInt(2, chunkTO.surlUniqueID());
			logWarnings(stmt.getWarnings());

			stmt.setLong(3, chunkTO.primaryKey());
			logWarnings(stmt.getWarnings());
			
			log.trace("BoL CHUNK DAO - update incomplete: " + stmt.toString());
			stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
		} catch(SQLException e)
		{
			log.error("BoL CHUNK DAO: Unable to complete update incomplete! " + e);
		} finally
		{
			close(stmt);
		}
	}
	
	/**
     * TODO WARNING! THIS IS A WORK IN PROGRESS!!! Method used to refresh the BoLChunkDataTO information from the MySQL
     * DB. In this first version, only the statusCode is reloaded from the DB. TODO The next version must contains all
     * the information related to the Chunk! In case of any error, an error message gets logged but no exception is
     * thrown.
     */
	public synchronized BoLChunkDataTO refresh(long primary_key) {

	    if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: refresh - unable to get a valid connection!");
            return null;
        }
		String str = null;
		PreparedStatement find = null;
		ResultSet rs = null;

		try
		{
			// get chunks of the request
			str = "SELECT  statusCode " + "FROM status_BoL " + "WHERE request_BoLID=?";
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			find.setLong(1, primary_key);

			logWarnings(find.getWarnings());
			log.trace("BoL CHUNK DAO: refresh status method; " + find.toString());

			rs = find.executeQuery();

			logWarnings(find.getWarnings());
			BoLChunkDataTO aux = null;
			// The result shoul be un
			// TODO REMOVE THIS WHILE
			while(rs.next())
			{
				aux = new BoLChunkDataTO();
				aux.setStatus(rs.getInt("statusCode"));
			}
			return aux;
		} catch(SQLException e)
		{
			log.error("BoL CHUNK DAO: " + e);
			/* Return null TransferObject! */
			return null;
		} finally
		{
			close(rs);
			close(find);
		}
	}
	
    /**
     * Method that queries the MySQL DB to find all entries matching the supplied TRequestToken. The Collection contains
     * the corresponding BoLChunkDataTO objects. An initial simple query establishes the list of protocols associated
     * with the request. A second complex query establishes all chunks associated with the request, by properly joining
     * request_queue, request_BoL, status_BoL and request_DirOption. The considered fields are: (1) From status_BoL: the
     * ID field which becomes the TOs primary key, and statusCode. (2) From request_BoL: sourceSURL (3) From
     * request_queue: pinLifetime (4) From request_DirOption: isSourceADirectory, alLevelRecursive, numOfLevels In case
     * of any error, a log gets written and an empty collection is returned. No exception is thrown. NOTE! Chunks in
     * SRM_ABORTED status are NOT returned!
     */
    public synchronized Collection<BoLChunkDataTO> find(TRequestToken requestToken) {
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: find - unable to get a valid connection!");
            return new ArrayList<BoLChunkDataTO>();
        }
        String strToken = requestToken.toString();
        String str = null;
        PreparedStatement find = null;
        ResultSet rs = null;
        try {
            str = "SELECT tp.config_ProtocolsID "
                    + "FROM request_TransferProtocols tp JOIN request_queue rq ON tp.request_queueID=rq.ID "
                    + "WHERE rq.r_token=?";

            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            
            ArrayList<String> protocols = new ArrayList<String>();
            find.setString(1, strToken);
            logWarnings(find.getWarnings());
            
            log.trace("BoL CHUNK DAO: find method; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            while (rs.next()) {
                protocols.add(rs.getString("tp.config_ProtocolsID"));
            }
            close(rs);
            close(find);

            //TODO MICHELE USER_SURL get new fields
            // get chunks of the request
            str = "SELECT sb.statusCode, rq.timeStamp, rq.pinLifetime, rq.deferredStartTime, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "
                    + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
                    + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
                    + "LEFT JOIN request_DirOption d ON rb.request_DirOptionID=d.ID "
                    + "WHERE rq.r_token=? AND sb.statusCode<>?";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            ArrayList<BoLChunkDataTO> list = new ArrayList<BoLChunkDataTO>();
            find.setString(1, strToken);
            logWarnings(find.getWarnings());
            
            find.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
            logWarnings(find.getWarnings());

            log.trace("BoL CHUNK DAO: find method; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            
            BoLChunkDataTO chunkDataTO = null;
			while(rs.next())
			{
				chunkDataTO = new BoLChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
				chunkDataTO.setLifeTime(rs.getInt("rq.pinLifetime"));
				chunkDataTO.setDeferredStartTime(rs.getInt("rq.deferredStartTime"));
				chunkDataTO.setRequestToken(strToken);
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
				chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
				// TODO MICHELE USER_SURL fill new fields

				chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
				if(!rs.wasNull())
				{
					chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
				}
				
				chunkDataTO.setDirOption(rs.getBoolean("d.isSourceADirectory"));
				chunkDataTO.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
				chunkDataTO.setNumLevel(rs.getInt("d.numOfLevels"));
				chunkDataTO.setProtocolList(protocols);
				list.add(chunkDataTO);
			}
			return list;
        } catch (SQLException e) {
            log.error("BOL CHUNK DAO: " + e);
            /* Return empty Collection! */
            return new ArrayList<BoLChunkDataTO>(); 
		} finally
		{
			close(rs);
			close(find);
		}
    }

    /**
     * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given TRequestToken expressed as
     * String.
     */
	public synchronized Collection<ReducedBoLChunkDataTO> findReduced(String reqtoken) {

	    if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: findReduced - unable to get a valid connection!");
            return new ArrayList<ReducedBoLChunkDataTO>();
        }
		PreparedStatement find = null;
		ResultSet rs = null;
		try
		{
			// TODO MICHELE USER_SURL get new fields
			// get reduced chunks
			String str =
						 "SELECT sb.statusCode, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID "
							 + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
							 + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
							 + "WHERE rq.r_token=?";
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			
			ArrayList<ReducedBoLChunkDataTO> list = new ArrayList<ReducedBoLChunkDataTO>();
			find.setString(1, reqtoken);
			logWarnings(find.getWarnings());
			
			log.trace("BoL CHUNK DAO! findReduced with request token; " + find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			
			ReducedBoLChunkDataTO chunkDataTO = null;
			while(rs.next())
			{
				chunkDataTO = new ReducedBoLChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
				chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
				chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
				// TODO MICHELE USER_SURL fill new fields
				chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
                if(!rs.wasNull())
        		{
                	chunkDataTO.setSurlUniqueID(uniqueID);	
        		}
				
				list.add(chunkDataTO);
			}
			return list;
		} catch(SQLException e)
		{
			log.error("BOL CHUNK DAO: " + e);
			/* Return empty Collection! */
			return new ArrayList<ReducedBoLChunkDataTO>();
		} finally
		{
			close(rs);
			close(find);
		}
	}
	
	/**
     * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given griduser, and whose SURLs are
     * contained in the supplied array of Strings.
     */
    public synchronized Collection<ReducedBoLChunkDataTO> findReduced(TRequestToken requestToken,
        int[] surlUniqueIDs,  String[] surls) {
        
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: findReduced - unable to get a valid connection!");
            return new ArrayList<ReducedBoLChunkDataTO>(); // return empty Collection!
        }
        PreparedStatement find = null;
        ResultSet rs = null;
        try 
        {
            // TODO MICHELE USER_SURL get new fields and select on the uniqueID and on the fromSurl 
            //TODO MICHELE USER_SURL when the uniqueID and normalized surl will be made on the FrontEnd remove the String[] surls parameter
            /* NOTE: we search also on the fromSurl because otherwise we lost all request_Bol that have not the uniqueID set because are not yet been used by anybody */
            // get reduced chunks
            String str = "SELECT sb.statusCode, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID "
                             + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
                             + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
                             + "WHERE rq.r_token=? AND ( rb.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlUniqueIDs) + " OR rb.sourceSURL IN " + makeSurlString(surls) + " ) ";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            
            ArrayList<ReducedBoLChunkDataTO> list = new ArrayList<ReducedBoLChunkDataTO>();
            find.setString(1, requestToken.getValue());
            logWarnings(find.getWarnings());
            
            log.trace("BoL CHUNK DAO! findReduced with griduser+surlarray; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            
            ReducedBoLChunkDataTO chunkDataTO = null;
            while (rs.next()) {
                chunkDataTO = new ReducedBoLChunkDataTO();
                chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
                chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
                chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
             // TODO MICHELE USER_SURL fill new fields
                chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
                int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
                if(!rs.wasNull())
                {
                    chunkDataTO.setSurlUniqueID(uniqueID);                  
                }

                list.add(chunkDataTO);
            }
            return list;
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: " + e);
            return new ArrayList<ReducedBoLChunkDataTO>(); // return empty Collection!
        } finally
        {
            close(rs);
            close(find);
        }
    }

    /**
     * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given griduser, and whose SURLs are
     * contained in the supplied array of Strings.
     */
    public synchronized Collection<ReducedBoLChunkDataTO> findReduced(String griduser,
		int[] surlUniqueIDs,  String[] surls) {
    	
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: findReduced - unable to get a valid connection!");
            return new ArrayList<ReducedBoLChunkDataTO>(); // return empty Collection!
        }
        PreparedStatement find = null;
        ResultSet rs = null;
        try 
        {
        	// TODO MICHELE USER_SURL get new fields and select on the uniqueID and on the fromSurl 
			//TODO MICHELE USER_SURL when the uniqueID and normalized surl will be made on the FrontEnd remove the String[] surls parameter
        	/* NOTE: we search also on the fromSurl because otherwise we lost all request_Bol that have not the uniqueID set because are not yet been used by anybody */
            // get reduced chunks
			String str = "SELECT sb.statusCode, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID "
							 + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
							 + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
							 + "WHERE rq.client_dn=? AND ( rb.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlUniqueIDs) + " OR rb.sourceSURL IN " + makeSurlString(surls) + " ) ";
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            
            ArrayList<ReducedBoLChunkDataTO> list = new ArrayList<ReducedBoLChunkDataTO>();
            find.setString(1, griduser);
            logWarnings(find.getWarnings());
            
            log.trace("BoL CHUNK DAO! findReduced with griduser+surlarray; " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            
            ReducedBoLChunkDataTO chunkDataTO = null;
            while (rs.next()) {
                chunkDataTO = new ReducedBoLChunkDataTO();
                chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
                chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
                chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
             // TODO MICHELE USER_SURL fill new fields
				chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
                if(!rs.wasNull())
        		{
                	chunkDataTO.setSurlUniqueID(uniqueID);                	
        		}

                list.add(chunkDataTO);
            }
            return list;
        } catch (SQLException e) {
            log.error("BoL CHUNK DAO: " + e);
            return new ArrayList<ReducedBoLChunkDataTO>(); // return empty Collection!
        } finally
		{
			close(rs);
			close(find);
		}
    }

    /**
     * Method that returns the number of BoL requests on the given SURL, that are in SRM_SUCCESS state. This method is
     * intended to be used by BoLChunkCatalog in the isSRM_SUCCESS method invocation. In case of any error, 0 is
     * returned.
     */
  //TODO MICHELE USER_SURL use the unique ID to perform the select on the request_Bol table
    public synchronized int numberInSRM_SUCCESS(int surlUniqueID) {
        
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: numberInSRM_SUCCESS - unable to get a valid connection!");
            return 0;
        }
        String str = "SELECT COUNT(rb.ID) " + "FROM status_BoL sb JOIN request_BoL rb "
        				 + "ON (sb.request_BoLID=rb.ID) " + "WHERE rb.sourceSURL_uniqueID=? AND sb.statusCode=?";
        PreparedStatement find = null;
		ResultSet rs = null;
		try
		{
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			/* Prepared statement spares DB-specific String notation! */
			find.setInt(1, surlUniqueID); 
			logWarnings(find.getWarnings());
			
			find.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
			logWarnings(find.getWarnings());
			
			log.trace("BoL CHUNK DAO - numberInSRM_SUCCESS method: " + find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			
			int numberFileSuccessful = 0;
			if(rs.next())
			{
				numberFileSuccessful = rs.getInt(1);
			}
			return numberFileSuccessful;
		} catch(SQLException e)
		{
			log.error("BoL CHUNK DAO! Unable to determine numberInSRM_SUCCESS! Returning 0! " + e);
			return 0;
		} finally
		{
			close(rs);
			close(find);
		}
    }

    /**
     * Method used in extraordinary situations to signal that data retrieved from the DB was malformed and could not be
     * translated into the StoRM object model. This method attempts to change the status of the request to SRM_FAILURE
     * and record it in the DB. This operation could potentially fail because the source of the malformed problems could
     * be a problematic DB; indeed, initially only log messages where recorded. Yet it soon became clear that the source
     * of malformed data were the clients and/or FE recording info in the DB. In these circumstances the client would
     * see its request as being in the SRM_IN_PROGRESS state for ever. Hence the pressing need to inform it of the
     * encountered problems.
     */
	public synchronized void signalMalformedBoLChunk(BoLChunkDataTO auxTO) {

	    if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: signalMalformedBoLChunk - unable to get a valid connection!");
            return;
        }
		String signalSQL =
						   "UPDATE status_BoL SET statusCode="
							   + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE)
							   + ", explanation=? WHERE request_BoLID=" + auxTO.getPrimaryKey();
		PreparedStatement signal = null;
		try
		{
			signal = con.prepareStatement(signalSQL);
			logWarnings(con.getWarnings());
			/* Prepared statement spares DB-specific String notation! */
			signal.setString(1, "Request is malformed!"); 
			logWarnings(signal.getWarnings());
			
			log.trace("BoL CHUNK DAO: signalMalformed; " + signal.toString());
			signal.executeUpdate();
			logWarnings(signal.getWarnings());
		} catch(SQLException e)
		{
			log.error("BoLChunkDAO! Unable to signal in DB that the"
				+ " request was malformed! Request: " + auxTO.toString() + "; Exception: "
				+ e.toString());
		} finally
		{
			close(signal);
		}
	}

    /**
     * Method that updates all expired requests in SRM_SUCCESS state, into SRM_RELEASED. This is needed when the client
     * forgets to invoke srmReleaseFiles().
     * @return 
     */
    //TODO MICHELE USER_SURL debug
    public synchronized List<TSURL> transitExpiredSRM_SUCCESS() {

        // TODO: put a limit on the queries.....
    	//TODO MICHELE USER_SURL moved the checks for surl requests from the surl tring to the surl unique ID
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: transitExpiredSRM_SUCCESS - unable to get a valid connection!");
            return new ArrayList<TSURL>();
        }
        HashMap<String, Integer> expiredSurlMap = new HashMap<String, Integer>();
        String str = null;
        Statement statement = null;

        /* Find all expired surls*/
        try {
            // start transaction
            con.setAutoCommit(false);

            statement = con.createStatement();

			str = "SELECT rb.sourceSURL , rb.sourceSURL_uniqueID FROM "
					  + "request_BoL rb JOIN (status_BoL sb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
					  + "WHERE sb.statusCode="
					  + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS)
					  + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

            ResultSet res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
            	String sourceSURL = res.getString("rb.sourceSURL");
            	Integer uniqueID = new Integer(res.getInt("rb.sourceSURL_uniqueID"));
            	/* If the uniqueID is not setted compute it*/
            	if(res.wasNull())
				{
					try
					{
						TSURL tsurl = TSURL.makeFromStringWellFormed(sourceSURL);
						uniqueID = tsurl.uniqueId();
					} catch(InvalidTSURLAttributesException e)
					{
						log.warn("BoLChunkDAO! unable to build the TSURL from " + sourceSURL + " : InvalidTSURLAttributesException " + e);
					}
				}
            	expiredSurlMap.put(sourceSURL, uniqueID);
            }

            if (expiredSurlMap.isEmpty()) {
                commit(con);
                log.trace("BoLChunkDAO! No chunk of BoL request was transited from SRM_SUCCESS to SRM_RELEASED.");
                return new ArrayList<TSURL>();
            }
        } catch (SQLException e) {
            log.error("BoLChunkDAO! SQLException." + e);
            rollback(con);
            return new ArrayList<TSURL>();
        } finally {
            close(statement);
        }
        
        /* Update status of all successful surls to SRM_RELEASED*/

        PreparedStatement preparedStatement = null;
        try {

            str = "UPDATE "
                    + "status_BoL sb JOIN (request_BoL rb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
                    + "SET sb.statusCode=? "
                    + "WHERE sb.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";
            
            preparedStatement = con.prepareStatement(str);
            logWarnings(con.getWarnings());

            preparedStatement.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
            logWarnings(preparedStatement.getWarnings());
            
            preparedStatement.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
            logWarnings(preparedStatement.getWarnings());

            log.trace("BoL CHUNK DAO - transitExpiredSRM_SUCCESS method: " + preparedStatement.toString());

            int count = preparedStatement.executeUpdate();
            logWarnings(preparedStatement.getWarnings());

			if(count == 0)
			{
				log.trace("BoLChunkDAO! No chunk of BoL request was"
					+ " transited from SRM_SUCCESS to SRM_RELEASED.");
			}
			else
			{
				log.info("BoLChunkDAO! " + count + " chunks of BoL requests were transited from"
					+ " SRM_SUCCESS to SRM_RELEASED.");
			}
        } catch (SQLException e) {
            log.error("BoLChunkDAO! Unable to transit expired SRM_SUCCESS chunks of BoL requests, to SRM_RELEASED! "
                    + e);
            rollback(con);
            return new ArrayList<TSURL>();
        } finally {
            close(preparedStatement);
        }

        /* in order to enhance performance here we can check if there is any file system with tape (T1D0, T1D1), if there is not any we can skip the following*/

        /* Find all not expired surls from PtG*/
        
        HashSet<Integer> pinnedSurlSet = new HashSet<Integer>();
        try {

            statement = con.createStatement();

           // SURLs pinned by BoLs
            str = "SELECT rb.sourceSURL , rb.sourceSURL_uniqueID FROM "
                    + "request_BoL rb JOIN (status_BoL sb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
                    + "WHERE sb.statusCode=" 
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) < rq.pinLifetime ";

            ResultSet res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
            	String sourceSURL = res.getString("rb.sourceSURL");
            	Integer uniqueID = new Integer(res.getInt("rb.sourceSURL_uniqueID"));
            	/* If the uniqueID is not setted compute it */
            	if(res.wasNull())
				{
					try
					{
						TSURL  tsurl = TSURL.makeFromStringWellFormed(sourceSURL);
						uniqueID = tsurl.uniqueId();
					} catch(InvalidTSURLAttributesException e)
					{
						log.warn("BoLChunkDAO! unable to build the TSURL from " + sourceSURL + " : InvalidTSURLAttributesException " + e);
					}
				}
            	pinnedSurlSet.add(uniqueID);
            }

            str = "SELECT rg.sourceSURL , rg.sourceSURL_uniqueID FROM "
                    + "request_Get rg JOIN (status_Get sg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
                    + "WHERE sg.statusCode="
                    + StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED)
                    + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) < rq.pinLifetime ";

            res = statement.executeQuery(str);
            logWarnings(statement.getWarnings());

            while (res.next()) {
            	String sourceSURL = res.getString("rg.sourceSURL");
            	Integer uniqueID = new Integer(res.getInt("rg.sourceSURL_uniqueID"));
            	/* If the uniqueID is not setted compute it */
            	if(res.wasNull())
				{
					try
					{
						TSURL  tsurl = TSURL.makeFromStringWellFormed(sourceSURL);
						uniqueID = tsurl.uniqueId();
					} catch(InvalidTSURLAttributesException e)
					{
						log.warn("BoLChunkDAO! unable to build the TSURL from " + sourceSURL + " : InvalidTSURLAttributesException " + e);
					}
				}
            	pinnedSurlSet.add(uniqueID);
            }

            commit(con);

        } catch (SQLException e) {
            log.error("BoLChunkDAO! SQLException." + e);
            rollback(con);
        } finally {
            close(statement);
        }

        /* Remove the Extended Attribute pinned if there is not a valid surl on it */
        ArrayList<TSURL> expiredSurlList = new ArrayList<TSURL>();
        TSURL surl;
        for(Entry<String, Integer> surlEntry : expiredSurlMap.entrySet())
		{
			if(!pinnedSurlSet.contains(surlEntry.getValue()))
			{
			    try
                {
                    surl = TSURL.makeFromStringValidate(surlEntry.getKey());
                }catch(InvalidTSURLAttributesException e)
                {
                    log.error("Invalid SURL, cannot release the pin (Extended Attribute): "
                        + surlEntry.getKey());
                    continue;
                }
                expiredSurlList.add(surl);
			    StoRI stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);

				if(stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled())
				{
					StormEA.removePinned(stori.getAbsolutePath());
				}
			}
		}
        return expiredSurlList;
    }

    /**
     * Method that transits chunks in SRM_SUCCESS to SRM_ABORTED, for the given SURL: the overall request status of the
     * requests containing that chunk, is not changed! The TURL is set to null. Beware, that the chunks may be part of
     * requests that have finished, or that still have not finished because other chunks are still being processed.
     */
	public synchronized void transitSRM_SUCCESStoSRM_ABORTED(int surlUniqueID, String surl,
			String explanation) {

	    if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: transitSRM_SUCCESStoSRM_ABORTED - unable to get a valid connection!");
            return;
        }
		//TODO MICHELE USER_SURL use the unique ID to perform the select on the request_Bol table (removed a bug)
        //TODO MICHELE USER_SURL when the uniqueID and normalized surl is provided by the FrontEnd remove the String surl parameter
		String str = "UPDATE "
			 + "status_BoL sb JOIN request_BoL rb ON sb.request_BoLID=rb.ID "
			 + "SET sb.statusCode=?, sb.explanation=?, sb.transferURL=NULL "
			 + "WHERE sb.statusCode=? AND (rb.sourceSURL_uniqueID=? OR rb.targetSURL=?)";
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
			logWarnings(stmt.getWarnings());
			
			stmt.setString(2, explanation);
			logWarnings(stmt.getWarnings());
			
			stmt.setInt(3, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
			logWarnings(stmt.getWarnings());
			
            stmt.setInt(4, surlUniqueID);
            logWarnings(stmt.getWarnings());
            
            stmt.setString(5, surl);
            logWarnings(stmt.getWarnings());
			
			log.trace("BoL CHUNK DAO - transitSRM_SUCCESStoSRM_ABORTED: " + stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if(count > 0)
			{
				log.info("BoL CHUNK DAO! " + count
					+ " chunks were transited from SRM_SUCCESS to SRM_ABORTED.");
			}
			else
			{
				log.trace("BoL CHUNK DAO! No chunks were transited "
					+ "from SRM_SUCCESS to SRM_ABORTED.");
			}
		} catch(SQLException e)
		{
			log.error("BoL CHUNK DAO! Unable to transitSRM_SUCCESStoSRM_ABORTED! " + e);
		} finally
		{
			close(stmt);
		}
	}

    /**
     * Method that updates all chunks in SRM_SUCCESS state, into SRM_RELEASED. An array of long representing the primary
     * key of each chunk is required: only they get the status changed provided their current status is SRM_SUCCESS.
     * This method is used during srmReleaseFiles In case of any error nothing happens and no exception is thrown, but
     * proper messages get logged.
     */
	public synchronized void transitSRM_SUCCESStoSRM_RELEASED(long[] ids) {

	    if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: transitSRM_SUCCESStoSRM_RELEASED - unable to get a valid connection!");
            return;
        }
		String str =
//					 "UPDATE "
//						 + "status_BoL s JOIN (request_BoL rg, request_queue r) ON s.request_BoLID=rg.ID AND rg.request_queueID=r.ID "
//						 + "SET s.statusCode=? " + "WHERE s.statusCode=? AND s.ID IN "
//						 + makeWhereString(ids);
        			"UPDATE "
        			 + "status_BoL "
        			 + "SET statusCode=? " + "WHERE statusCode=? AND request_BoLID IN "
        			 + makeWhereString(ids);
		PreparedStatement stmt = null;
		try
		{
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
			logWarnings(stmt.getWarnings());
			
			stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
			logWarnings(stmt.getWarnings());
			
			log.trace("BoL CHUNK DAO - transitSRM_SUCCESStoSRM_RELEASED: " + stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if(count == 0)
			{
				log.trace("BoL CHUNK DAO! No chunk of BoL request "
					+ "was transited from SRM_SUCCESS to SRM_RELEASED.");
			}
			else
			{
				log.info("BoL CHUNK DAO! " + count
					+ " chunks of BoL requests were transited from SRM_SUCCESS to SRM_RELEASED.");
			}
		} catch(SQLException e)
		{
			log.error("BoL CHUNK DAO! Unable to transit chunks from SRM_SUCCESS to SRM_RELEASED! "
				+ e);
		} finally
		{
			close(stmt);
		}
	}

	public synchronized void transitSRM_SUCCESStoSRM_RELEASED(long[] ids, TRequestToken token) {

		if(token == null)
		{
			transitSRM_SUCCESStoSRM_RELEASED(ids);
		}
		else
		{
			/*
			 * If a request token has been specified, only the related BoL
			 * requests have to be released. This is done adding the
			 * r.r_token="..." clause in the where subquery.
			 */
		    if(!checkConnection())
	        {
	            log.error("BoL CHUNK DAO: transitSRM_SUCCESStoSRM_RELEASED - unable to get a valid connection!");
	            return;
	        }
			String str = "UPDATE "
							 + "status_BoL sb JOIN (request_BoL rb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
							 + "SET sb.statusCode=? " + "WHERE sb.statusCode=? AND rq.r_token='"
							 + token.toString() + "' AND rb.ID IN " + makeWhereString(ids);
			PreparedStatement stmt = null;
			try
			{
				stmt = con.prepareStatement(str);
				logWarnings(con.getWarnings());
				stmt.setInt(1, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
				logWarnings(stmt.getWarnings());
				
				stmt.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
				logWarnings(stmt.getWarnings());
				
				log.trace("BoL CHUNK DAO - transitSRM_SUCCESStoSRM_RELEASED: " + stmt.toString());
				int count = stmt.executeUpdate();
				logWarnings(stmt.getWarnings());
				if(count == 0)
				{
					log.trace("BoL CHUNK DAO! No chunk of BoL request was "
						+ "transited from SRM_SUCCESS to SRM_RELEASED.");
				}
				else
				{
					log.info("BoL CHUNK DAO! " + count + " chunks of BoL requests were transited "
						+ "from SRM_SUCCESS to SRM_RELEASED.");
				}
			} catch(SQLException e)
			{
				log.error("BoL CHUNK DAO! Unable to transit chunks "
					+ "from SRM_SUCCESS to SRM_RELEASED! " + e);
			} finally
			{
				close(stmt);
			}
		}
	}

	/**
	 * Auxiliary method used to close a ResultSet
	 */
	private void close(ResultSet rset) {

		if(rset != null)
		{
			try
			{
				rset.close();
			} catch(Exception e)
			{
				log.error("BoL CHUNK DAO! Unable to close ResultSet! Exception: " + e);
			}
		}
	}

	/**
	 * Auxiliary method used to close a Statement
	 */
	private void close(Statement stmt) {

		if(stmt != null)
		{
			try
			{
				stmt.close();
			} catch(Exception e)
			{
				log.error("BoL CHUNK DAO! Unable to close Statement " + stmt.toString()
					+ " - Exception: " + e);
			}
		}
	}

	private void commit(Connection con) {

		if(con != null)
		{
			try
			{
				con.commit();
				con.setAutoCommit(true);
			} catch(SQLException e)
			{
				log.error("BoL, SQL EXception", e);
			}
		}
	}

	/**
	 * Auxiliary method used to roll back a failed transaction
	 */
	private void rollback(Connection con) {

		if(con != null)
		{
			try
			{
				con.rollback();
				con.setAutoCommit(true);
				log.error("BoL CHUNK DAO: roll back successful!");
			} catch(SQLException e2)
			{
				log.error("BoL CHUNK DAO: roll back failed! " + e2);
			}
		}
	}
    
    /**
     * Private method that returns the generated ID: it throws an exception in case of any problem!
     */
	private int extractID(ResultSet rs)
			throws Exception {

		if(rs == null)
		{
			throw new Exception("BoL CHUNK DAO! Null ResultSet!");
		}
		if(rs.next())
		{
			return rs.getInt(1);
		}
		else
		{
			log.error("BoL CHUNK DAO! It was not possible to establish "
				+ "the assigned autoincrement primary key!");
			throw new Exception(
				"BoL CHUNK DAO! It was not possible to establish the assigned autoincrement primary key!");
		}
	}

    /**
     * Auxiliary private method that logs all SQL warnings.
     */
	private void logWarnings(SQLWarning w) {

		if(w != null)
		{
			log.debug("BoL CHUNK DAO: " + w.toString());
			while((w = w.getNextWarning()) != null)
			{
				log.debug("BoL CHUNK DAO: " + w.toString());
			}
		}
	}

    /**
     * Method that returns a String containing all IDs.
     */
	private String makeWhereString(long[] rowids) {

		StringBuffer sb = new StringBuffer("(");
		int n = rowids.length;
		for(int i = 0; i < n; i++)
		{
			sb.append(rowids[i]);
			if(i < (n - 1))
			{
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
    
	/**
	 * Method that returns a String containing all Surl's IDs.
	 */
	private String makeSURLUniqueIDWhere(int[] surlUniqueIDs) {

		StringBuffer sb = new StringBuffer("(");
		for(int i = 0; i < surlUniqueIDs.length; i++)
		{
			if(i > 0)
			{
				sb.append(",");
			}
			sb.append(surlUniqueIDs[i]);
		}
		sb.append(")");
		return sb.toString();
	}
    
	/**
	 * Method that returns a String containing all Surls.
	 */
	private String makeSurlString(String[] surls) {

		StringBuffer sb = new StringBuffer("(");
		int n = surls.length;
		for(int i = 0; i < n; i++)
		{
			sb.append("'");
			sb.append(surls[i]);
			sb.append("'");
			if(i < (n - 1))
			{
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Auxiliary method that sets up the connection to the DB, as well as the
	 * prepared statement.
	 */
	private boolean setUpConnection() {
        boolean response = false;
		try
		{
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			if(con == null)
			{
				log.error("BoL CHUNK DAO! Exception in setUpConnection!"
					+ " DriverManager could not create connection!");
			}
			else
			{
				logWarnings(con.getWarnings());
				response = con.isValid(0);
			}
		} catch(ClassNotFoundException e)
		{
			log.error("BoL CHUNK DAO! Exception in setUpConnection! " + e);
		} catch(SQLException e)
		{
			log.error("BoL CHUNK DAO! Exception in setUpConenction! " + e);
		}
		return response;
	}

	/**
	 * Auxiliary method that checks if time for resetting the connection has
	 * come, and eventually takes it down and up back again.
	 */
	private boolean checkConnection() {
	    boolean response = true;
		if(reconnect)
		{
			log.debug("BoL CHUNK DAO! Reconnecting to DB! ");
			takeDownConnection();
		    response = setUpConnection();
		    if(response)
		    {
		        reconnect = false;    
		    }
        }
        return response;
	}

	/**
	 * Auxiliary method that tales down a connection to the DB.
	 */
	private void takeDownConnection() {
	    if(con != null)
	    {
    		try
    		{
    			con.close();
    		} catch(SQLException e)
    		{
    			log.error("BoL CHUNK DAO! Exception in takeDownConnection method: " + e);
    		}
	    }
	}
	
	public synchronized void updateStatusOnMatchingStatus(TRequestToken requestToken, TStatusCode expectedStatusCode,
            TStatusCode newStatusCode, String explanation)
    {
	    if (requestToken == null || requestToken.getValue().trim().isEmpty() || explanation == null)
        {
            throw new IllegalArgumentException("Unable to perform the updateStatusOnMatchingStatus, "
                    + "invalid arguments: requestToken=" + requestToken + " explanation=" + explanation);
        }
        doUpdateStatusOnMatchingStatus(requestToken, null, null, expectedStatusCode, newStatusCode,
                                       explanation, true, false, true);
    }
	
	public synchronized void updateStatusOnMatchingStatus(TRequestToken requestToken, int[] surlsUniqueIDs,
            String[] surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode) throws IllegalArgumentException
    {
        if (requestToken == null || requestToken.getValue().trim().isEmpty() || surlsUniqueIDs == null
                || surls == null || surlsUniqueIDs.length == 0 || surls.length == 0
                || surlsUniqueIDs.length != surls.length)
        {
            throw new IllegalArgumentException("Unable to perform the updateStatusOnMatchingStatus, "
                    + "invalid arguments: requestToken=" + requestToken + "surlsUniqueIDs=" + surlsUniqueIDs
                    + " surls=" + surls);
        }
        doUpdateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls, expectedStatusCode,
                                       newStatusCode, null, true, true, false);
    }

	public synchronized void doUpdateStatusOnMatchingStatus(TRequestToken requestToken, int[] surlUniqueIDs,
            String[] surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation,
            boolean withRequestToken, boolean withSurls, boolean withExplanation)
            throws IllegalArgumentException
    {
        if ((withRequestToken && requestToken == null) || (withExplanation && explanation == null)
                || (withSurls && (surlUniqueIDs == null || surls == null)))
        {
            throw new IllegalArgumentException("Unable to perform the doUpdateStatusOnMatchingStatus, "
                    + "invalid arguments: withRequestToken=" + withRequestToken + " requestToken="
                    + requestToken + " withSurls=" + withSurls + " surlUniqueIDs=" + surlUniqueIDs
                    + " surls=" + surls + " withExplaination=" + withExplanation + " explanation="
                    + explanation);
        }
        if(!checkConnection())
        {
            log.error("BOL CHUNK DAO: updateStatusOnMatchingStatus - unable to get a valid connection!");
            return;
        }
        String str = "UPDATE status_BoL sb JOIN (request_BoL rb, request_queue rq) " +
        		"ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
                + "SET sb.statusCode=? ";
        if (withExplanation)
        {
            str += " , " + buildExpainationSet(explanation);
        }       
        str += " WHERE sb.statusCode=? ";
        if (withRequestToken)
        {
            str += " AND " + buildTokenWhereClause(requestToken);
        }
        if(withSurls)
        {
            str += " AND " + buildSurlsWhereClause(surlUniqueIDs, surls);    
        }
        PreparedStatement stmt = null;
        try
        {
            stmt = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            stmt.setInt(1, StatusCodeConverter.getInstance().toDB(newStatusCode));
            logWarnings(stmt.getWarnings());
            
            stmt.setInt(2, StatusCodeConverter.getInstance().toDB(expectedStatusCode));
            logWarnings(stmt.getWarnings());
            
            log.trace("BOL CHUNK DAO - updateStatusOnMatchingStatus: "
                + stmt.toString());
            int count = stmt.executeUpdate();
            logWarnings(stmt.getWarnings());
            if(count == 0)
            {
                log.trace("BOL CHUNK DAO! No chunk of BOL request was"
                    + " updated from " + expectedStatusCode + " to " + newStatusCode + ".");
            }
            else
            {
                log.debug("BOL CHUNK DAO! " + count + " chunks of BOL requests were updated from "
                        + expectedStatusCode + " to " + newStatusCode + ".");
            }
        } catch(SQLException e)
        {
            log.error("BOL CHUNK DAO! Unable to updated from " + expectedStatusCode + " to " + newStatusCode
                    + " !" + e);
        } finally
        {
            close(stmt);
        }
    }
	
    public Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray, String dn)
            throws IllegalArgumentException
    {
        if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0 || surlsArray == null
                || surlsArray.length == 0 || dn == null)
        {
            throw new IllegalArgumentException("Unable to perform the find, "
                    + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs + " surlsArray=" + surlsArray
                    + " dn=" + dn);
        }
        return find(surlsUniqueIDs, surlsArray, dn, true);
    }

    public Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray)
            throws IllegalArgumentException
    {
        if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0 || surlsArray == null
                || surlsArray.length == 0)
        {
            throw new IllegalArgumentException("Unable to perform the find, "
                    + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs + " surlsArray=" + surlsArray);
        }
        return find(surlsUniqueIDs, surlsArray, null, false);
    }

    private synchronized Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray,
            String dn, boolean withDn) throws IllegalArgumentException
    {
        if ((withDn && dn == null) || surlsUniqueIDs == null || surlsUniqueIDs.length == 0
                || surlsArray == null || surlsArray.length == 0)
        {
            throw new IllegalArgumentException("Unable to perform the find, "
                    + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs + " surlsArray=" + surlsArray
                    + " withDn=" + withDn + " dn=" + dn);
        }
        if(!checkConnection())
        {
            log.error("BoL CHUNK DAO: find - unable to get a valid connection!");
            return new ArrayList<BoLChunkDataTO>();
        }
        PreparedStatement find = null;
        ResultSet rs = null;
        try
        {
            //TODO MICHELE USER_SURL get new fields
            // get chunks of the request
            String str = "SELECT rq.ID, rq.r_token, sb.statusCode, rq.timeStamp, rq.pinLifetime, "
                    + "rq.deferredStartTime, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, "
                    + "rb.sourceSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "
                    + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
                    + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
                    + "LEFT JOIN request_DirOption d ON rb.request_DirOptionID=d.ID "
                    + "WHERE ( rb.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
                    + " OR rb.sourceSURL IN " + makeSurlString(surlsArray) + " )";
            if(withDn)
            {
                str += " AND rq.client_dn=\'" + dn + "\'";
            }
            find = con.prepareStatement(str);
            logWarnings(con.getWarnings());
            
            List<BoLChunkDataTO> list = new ArrayList<BoLChunkDataTO>();
            
            log.trace("BOL CHUNK DAO - find method: " + find.toString());
            rs = find.executeQuery();
            logWarnings(find.getWarnings());
            BoLChunkDataTO chunkDataTO = null;
            while(rs.next())
            {
                
                chunkDataTO = new BoLChunkDataTO();
                chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
                chunkDataTO.setLifeTime(rs.getInt("rq.pinLifetime"));
                chunkDataTO.setDeferredStartTime(rs.getInt("rq.deferredStartTime"));
                chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
                chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
                chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
                chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
                // TODO MICHELE USER_SURL fill new fields

                chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
                int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
                if(!rs.wasNull())
                {
                    chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
                }
                
                chunkDataTO.setDirOption(rs.getBoolean("d.isSourceADirectory"));
                chunkDataTO.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
                chunkDataTO.setNumLevel(rs.getInt("d.numOfLevels"));
                chunkDataTO.setProtocolList(PtPChunkDAO.getInstance().findProtocols(rs.getLong("rq.ID")));
                list.add(chunkDataTO);
            }
            return list;
        } catch(SQLException e)
        {
            log.error("BOL CHUNK DAO: " + e);
            /* return empty Collection! */
            return new ArrayList<BoLChunkDataTO>();
        } finally
        {
            close(rs);
            close(find);
        }
    }
    
    private String buildExpainationSet(String explanation)
    {
        return " sb.explanation='" + explanation + "' "  ;
    }

    private String buildTokenWhereClause(TRequestToken requestToken)
    {
        return " rq.r_token='" + requestToken.toString() + "' ";
    }
    
    private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls)
    {
        return " ( rb.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
                + " OR rb.sourceSURL IN " + makeSurlString(surls) + " ) ";
    }

}
