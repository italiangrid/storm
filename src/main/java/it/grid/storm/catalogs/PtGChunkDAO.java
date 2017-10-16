/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.catalogs;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.naming.SURL;
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
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for PtGChunkCatalog. This DAO is specifically designed to connect
 * to a MySQL DB. The raw data found in those tables is pre-treated in order to
 * turn it into the Object Model of StoRM. See Method comments for further info.
 * 
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the
 * object model.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date June 2005
 */
public class PtGChunkDAO {

	private static final Logger log = LoggerFactory.getLogger(PtGChunkDAO.class);

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
	/** boolean that tells whether reconnection is needed because of MySQL bug! */
	private boolean reconnect = false;

	/** Singleton instance */
	private final static PtGChunkDAO dao = new PtGChunkDAO();

	/** timer thread that will run a task to alert when reconnecting is necessary! */
	private Timer clock = null;
	/**
	 * timer task that will update the boolean signaling that a reconnection is
	 * needed!
	 */
	private TimerTask clockTask = null;
	/** milliseconds that must pass before reconnecting to DB */
	private final long period = Configuration.getInstance()
		.getDBReconnectPeriod() * 1000;
	/** initial delay in milliseconds before starting timer */
	private final long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;

	private PtGChunkDAO() {

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
	 * Method that returns the only instance of the PtGChunkDAO.
	 */
	public static PtGChunkDAO getInstance() {

		return dao;
	}

	/**
	 * Method used to add a new record to the DB: the supplied PtGChunkDataTO gets
	 * its primaryKey changed to the one assigned by the DB.
	 * 
	 * The supplied PtGChunkData is used to fill in only the DB table where file
	 * specific info gets recorded: it does _not_ add a new request! So if
	 * spurious data is supplied, it will just stay there because of a lack of a
	 * parent request!
	 */
	public synchronized void addChild(PtGChunkDataTO to) {

		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: addChild - unable to get a valid connection!");
			return;
		}
		String str = null;
		PreparedStatement id = null; // statement to find out the ID associated to
																	// the request token
		ResultSet rsid = null; // result set containing the ID of the request.
		try {

			// WARNING!!!! We are forced to run a query to get the ID of the request,
			// which should NOT be so
			// because the corresponding request object should have been changed with
			// the extra field! However, it is not possible
			// at the moment to perform such chage because of strict deadline and the
			// change could wreak havoc
			// the code. So we are forced to make this query!!!

			// begin transaction
			con.setAutoCommit(false);
			logWarnings(con.getWarnings());

			// find ID of request corresponding to given RequestToken
			str = "SELECT rq.ID FROM request_queue rq WHERE rq.r_token=?";

			id = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			id.setString(1, to.requestToken());
			logWarnings(id.getWarnings());

			log.debug("PTG CHUNK DAO: addChild; {}", id.toString());
			rsid = id.executeQuery();
			logWarnings(id.getWarnings());

			/* ID of request in request_process! */
			int request_id = extractID(rsid);
			int id_s = fillPtGTables(to, request_id);

			/* end transaction! */
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());

			// update primary key reading the generated key
			to.setPrimaryKey(id_s);
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: unable to complete addChild! "
				+ "PtGChunkDataTO: {}; error: {}", to, e.getMessage(), e);
			rollback(con);
		} catch (Exception e) {
			log.error("PTG CHUNK DAO: unable to complete addChild! "
				+ "PtGChunkDataTO: {}; error: {}", to, e.getMessage(), e);
			rollback(con);
		} finally {
			close(rsid);
			close(id);
		}
	}

	/**
	 * Method used to add a new record to the DB: the supplied PtGChunkDataTO gets
	 * its primaryKey changed to the one assigned by the DB. The client_dn must
	 * also be supplied as a String.
	 * 
	 * The supplied PtGChunkData is used to fill in all the DB tables where file
	 * specific info gets recorded: it _adds_ a new request!
	 */
	public synchronized void addNew(PtGChunkDataTO to, String client_dn) {

		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: addNew - unable to get a valid connection!");
			return;
		}
		String str = null;
		/* Result set containing the ID of the inserted new request */
		ResultSet rs_new = null;
		/* Insert new request into process_request */
		PreparedStatement addNew = null;
		/* Insert protocols for request. */
		PreparedStatement addProtocols = null;
		try {
			// begin transaction
			con.setAutoCommit(false);
			logWarnings(con.getWarnings());

			// add to request_queue...
			str = "INSERT INTO request_queue (config_RequestTypeID,client_dn,pinLifetime,status,errstring,r_token,nbreqfiles,timeStamp) VALUES (?,?,?,?,?,?,?,?)";
			addNew = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
			logWarnings(con.getWarnings());
			/* Request type set to prepare to get! */
			addNew.setString(1,
				RequestTypeConverter.getInstance().toDB(TRequestType.PREPARE_TO_GET));
			logWarnings(addNew.getWarnings());

			addNew.setString(2, client_dn);
			logWarnings(addNew.getWarnings());

			addNew.setInt(3, to.lifeTime());
			logWarnings(addNew.getWarnings());

			addNew.setInt(
				4,
				StatusCodeConverter.getInstance().toDB(
					TStatusCode.SRM_REQUEST_INPROGRESS));
			logWarnings(addNew.getWarnings());

			addNew.setString(5, "New PtG Request resulting from srmCopy invocation.");
			logWarnings(addNew.getWarnings());

			addNew.setString(6, to.requestToken());
			logWarnings(addNew.getWarnings());

			addNew.setInt(7, 1); // number of requested files set to 1!
			logWarnings(addNew.getWarnings());

			addNew.setTimestamp(8, new Timestamp(new Date().getTime()));
			logWarnings(addNew.getWarnings());

			log.trace("PTG CHUNK DAO: addNew; {}", addNew.toString());
			addNew.execute();
			logWarnings(addNew.getWarnings());

			rs_new = addNew.getGeneratedKeys();
			int id_new = extractID(rs_new);

			// add protocols...
			str = "INSERT INTO request_TransferProtocols (request_queueID,config_ProtocolsID) VALUES (?,?)";
			addProtocols = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			for (Iterator<String> i = to.protocolList().iterator(); i.hasNext();) {
				addProtocols.setInt(1, id_new);
				logWarnings(addProtocols.getWarnings());

				addProtocols.setString(2, i.next());
				logWarnings(addProtocols.getWarnings());

				log.trace("PTG CHUNK DAO: addNew; {}", addProtocols.toString());
				addProtocols.execute();
				logWarnings(addProtocols.getWarnings());
			}

			// addChild...
			int id_s = fillPtGTables(to, id_new);

			// end transaction!
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());

			// update primary key reading the generated key
			to.setPrimaryKey(id_s);
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: Rolling back! Unable to complete addNew! "
				+ "PtGChunkDataTO: {}; error: {}", to, e.getMessage(), e);
			rollback(con);
		} catch (Exception e) {
			log.error("PTG CHUNK DAO: unable to complete addNew! "
				+ "PtGChunkDataTO: {}; error: {}", to, e.getMessage(), e);
			rollback(con);
		} finally {
			close(rs_new);
			close(addNew);
			close(addProtocols);
		}
	}

	/**
	 * To be used inside a transaction
	 * 
	 * @param to
	 * @param requestQueueID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	private synchronized int fillPtGTables(PtGChunkDataTO to, int requestQueueID)
		throws SQLException, Exception {

		String str = null;
		/* Result set containing the ID of the inserted */
		ResultSet rs_do = null;
		/* Result set containing the ID of the inserted */
		ResultSet rs_g = null;
		/* Result set containing the ID of the inserted */
		ResultSet rs_s = null;
		/* insert TDirOption for request */
		PreparedStatement addDirOption = null;
		/* insert request_Get for request */
		PreparedStatement addGet = null;
		PreparedStatement addChild = null;

		try {
			// first fill in TDirOption
			str = "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";
			addDirOption = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
			logWarnings(con.getWarnings());
			addDirOption.setBoolean(1, to.dirOption());
			logWarnings(addDirOption.getWarnings());

			addDirOption.setBoolean(2, to.allLevelRecursive());
			logWarnings(addDirOption.getWarnings());

			addDirOption.setInt(3, to.numLevel());
			logWarnings(addDirOption.getWarnings());

			log.trace("PTG CHUNK DAO: addNew; {}", addDirOption.toString());
			addDirOption.execute();
			logWarnings(addDirOption.getWarnings());

			rs_do = addDirOption.getGeneratedKeys();
			int id_do = extractID(rs_do);

			// second fill in request_Get... sourceSURL and TDirOption!
			str = "INSERT INTO request_Get (request_DirOptionID,request_queueID,sourceSURL,normalized_sourceSURL_StFN,sourceSURL_uniqueID) VALUES (?,?,?,?,?)";
			addGet = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
			logWarnings(con.getWarnings());
			addGet.setInt(1, id_do);
			logWarnings(addGet.getWarnings());

			addGet.setInt(2, requestQueueID);
			logWarnings(addGet.getWarnings());

			addGet.setString(3, to.fromSURL());
			logWarnings(addGet.getWarnings());

			addGet.setString(4, to.normalizedStFN());
			logWarnings(addGet.getWarnings());

			addGet.setInt(5, to.surlUniqueID());
			logWarnings(addGet.getWarnings());

			log.trace("PTG CHUNK DAO: addNew; {}", addGet.toString());
			addGet.execute();
			logWarnings(addGet.getWarnings());

			rs_g = addGet.getGeneratedKeys();
			int id_g = extractID(rs_g);

			// third fill in status_Get...
			str = "INSERT INTO status_Get (request_GetID,statusCode,explanation) VALUES (?,?,?)";
			addChild = con.prepareStatement(str, Statement.RETURN_GENERATED_KEYS);
			logWarnings(con.getWarnings());
			addChild.setInt(1, id_g);
			logWarnings(addChild.getWarnings());

			addChild.setInt(2, to.status());
			logWarnings(addChild.getWarnings());

			addChild.setString(3, to.errString());
			logWarnings(addChild.getWarnings());

			log.trace("PTG CHUNK DAO: addNew; {}", addChild.toString());
			addChild.execute();
			logWarnings(addChild.getWarnings());

			return id_g;
		} finally {
			close(rs_do);
			close(rs_g);
			close(rs_s);
			close(addDirOption);
			close(addGet);
			close(addChild);
		}
	}

	/**
	 * Method used to save the changes made to a retrieved PtGChunkDataTO, back
	 * into the MySQL DB.
	 * 
	 * Only the fileSize, transferURL, statusCode and explanation, of status_Get
	 * table are written to the DB. Likewise for the request pinLifetime.
	 * 
	 * In case of any error, an error message gets logged but no exception is
	 * thrown.
	 */
	public synchronized void update(PtGChunkDataTO to) {

		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: update - unable to get a valid connection!");
			return;
		}
		PreparedStatement updateFileReq = null;
		try {
			// ready updateFileReq...
			updateFileReq = con
				.prepareStatement("UPDATE request_queue rq JOIN (status_Get sg, request_Get rg) ON (rq.ID=rg.request_queueID AND sg.request_GetID=rg.ID) "
					+ "SET sg.fileSize=?, sg.transferURL=?, sg.statusCode=?, sg.explanation=?, rq.pinLifetime=?, rg.normalized_sourceSURL_StFN=?, rg.sourceSURL_uniqueID=? "
					+ "WHERE rg.ID=?");
			logWarnings(con.getWarnings());

			updateFileReq.setLong(1, to.fileSize());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setString(2, to.turl());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setInt(3, to.status());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setString(4, to.errString());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setInt(5, to.lifeTime());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setString(6, to.normalizedStFN());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setInt(7, to.surlUniqueID());
			logWarnings(updateFileReq.getWarnings());

			updateFileReq.setLong(8, to.primaryKey());
			logWarnings(updateFileReq.getWarnings());
			// execute update
			log.trace("PTG CHUNK DAO: update method; {}", updateFileReq.toString());
			updateFileReq.executeUpdate();
			logWarnings(updateFileReq.getWarnings());
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO: Unable to complete update! {}", 
				e.getMessage(), e);
		} finally {
			close(updateFileReq);
		}
	}

	/**
	 * Updates the request_Get represented by the received ReducedPtGChunkDataTO
	 * by setting its normalized_sourceSURL_StFN and sourceSURL_uniqueID
	 * 
	 * @param chunkTO
	 */
	public synchronized void updateIncomplete(ReducedPtGChunkDataTO chunkTO) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: updateIncomplete - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE request_Get rg SET rg.normalized_sourceSURL_StFN=?, rg.sourceSURL_uniqueID=? "
			+ "WHERE rg.ID=?";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			stmt.setString(1, chunkTO.normalizedStFN());
			logWarnings(stmt.getWarnings());

			stmt.setInt(2, chunkTO.surlUniqueID());
			logWarnings(stmt.getWarnings());

			stmt.setLong(3, chunkTO.primaryKey());
			logWarnings(stmt.getWarnings());

			log.trace("PtG CHUNK DAO - update incomplete: {}", stmt.toString());
			stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO: Unable to complete update incomplete! {}", 
				e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * TODO WARNING! THIS IS A WORK IN PROGRESS!!!
	 * 
	 * Method used to refresh the PtGChunkDataTO information from the MySQL DB.
	 * 
	 * In this first version, only the statusCode and the TURL are reloaded from
	 * the DB. TODO The next version must contains all the information related to
	 * the Chunk!
	 * 
	 * In case of any error, an error messagge gets logged but no exception is
	 * thrown.
	 */

	public synchronized PtGChunkDataTO refresh(long primary_key) {

		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: refresh - unable to get a valid connection!");
			return null;
		}
		String queryString = null;
		PreparedStatement find = null;
		ResultSet rs = null;

		try {
			// get chunks of the request
			queryString = "SELECT  sg.statusCode, sg.transferURL "
				+ "FROM status_Get sg " + "WHERE sg.request_GetID=?";
			find = con.prepareStatement(queryString);
			logWarnings(con.getWarnings());
			find.setLong(1, primary_key);
			logWarnings(find.getWarnings());
			log.trace("PTG CHUNK DAO: refresh status method; {}", find.toString());

			rs = find.executeQuery();

			logWarnings(find.getWarnings());
			PtGChunkDataTO chunkDataTO = null;
			// The result shoul be un
			while (rs.next()) {
				chunkDataTO = new PtGChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
				chunkDataTO.setTurl(rs.getString("sg.transferURL"));
			}
			return chunkDataTO;
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
			/* Return null TransferObject! */
			return null;
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that queries the MySQL DB to find all entries matching the supplied
	 * TRequestToken. The Collection contains the corresponding PtGChunkDataTO
	 * objects.
	 * 
	 * An initial simple query establishes the list of protocols associated with
	 * the request. A second complex query establishes all chunks associated with
	 * the request, by properly joining request_queue, request_Get, status_Get and
	 * request_DirOption. The considered fields are:
	 * 
	 * (1) From status_Get: the ID field which becomes the TOs primary key, and
	 * statusCode.
	 * 
	 * (2) From request_Get: sourceSURL
	 * 
	 * (3) From request_queue: pinLifetime
	 * 
	 * (4) From request_DirOption: isSourceADirectory, alLevelRecursive,
	 * numOfLevels
	 * 
	 * In case of any error, a log gets written and an empty collection is
	 * returned. No exception is thrown.
	 * 
	 * NOTE! Chunks in SRM_ABORTED status are NOT returned!
	 */
	public synchronized Collection<PtGChunkDataTO> find(TRequestToken requestToken) {

		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<PtGChunkDataTO>();
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

			List<String> protocols = new ArrayList<String>();
			find.setString(1, strToken);
			logWarnings(find.getWarnings());

			log.trace("PTG CHUNK DAO: find method; {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			while (rs.next()) {
				protocols.add(rs.getString("tp.config_ProtocolsID"));
			}
			close(rs);
			close(find);

			// get chunks of the request
			str = "SELECT sg.statusCode, rq.pinLifetime, rg.ID, rq.timeStamp, rq.client_dn, rq.proxy, rg.sourceSURL, "
				+ "rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID, d.isSourceADirectory, "
				+ "d.allLevelRecursive, d.numOfLevels "
				+ "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
				+ "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
				+ "LEFT JOIN request_DirOption d ON rg.request_DirOptionID=d.ID "
				+ "WHERE rq.r_token=? AND sg.statusCode<>?";
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			ArrayList<PtGChunkDataTO> list = new ArrayList<PtGChunkDataTO>();
			find.setString(1, strToken);
			logWarnings(find.getWarnings());

			find.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
			logWarnings(find.getWarnings());

			log.trace("PTG CHUNK DAO: find method; " + find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			PtGChunkDataTO chunkDataTO;
			while (rs.next()) {
				chunkDataTO = new PtGChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
				chunkDataTO.setRequestToken(strToken);
				chunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
				chunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
				chunkDataTO.setNormalizedStFN(rs
					.getString("rg.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setClientDN(rs.getString("rq.client_dn"));

				/**
				 * This code is only for the 1.3.18. This is a workaround to get FQANs
				 * using the proxy field on request_queue. The FE use the proxy field of
				 * request_queue to insert a single FQAN string containing all FQAN
				 * separeted by the "#" char. The proxy is a BLOB, hence it has to be
				 * properly conveted in string.
				 */
				java.sql.Blob blob = rs.getBlob("rq.proxy");
				if (!rs.wasNull() && blob != null) {
					byte[] bdata = blob.getBytes(1, (int) blob.length());
					chunkDataTO.setVomsAttributes(new String(bdata));
				}
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setLifeTime(rs.getInt("rq.pinLifetime"));
				chunkDataTO.setDirOption(rs.getBoolean("d.isSourceADirectory"));
				chunkDataTO.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
				chunkDataTO.setNumLevel(rs.getInt("d.numOfLevels"));
				chunkDataTO.setProtocolList(protocols);
				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: ", e.getMessage(), e);
			/* Return empty Collection! */
			return new ArrayList<PtGChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that returns a Collection of ReducedPtGChunkDataTO associated to the
	 * given TRequestToken expressed as String.
	 */
	public synchronized Collection<ReducedPtGChunkDataTO> findReduced(
		String reqtoken) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: findReduced - unable to get a valid connection!");
			return new ArrayList<ReducedPtGChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			// get reduced chunks
			String str = "SELECT sg.statusCode, rg.ID, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID "
				+ "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
				+ "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
				+ "WHERE rq.r_token=?";
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			ArrayList<ReducedPtGChunkDataTO> list = new ArrayList<ReducedPtGChunkDataTO>();
			find.setString(1, reqtoken);
			logWarnings(find.getWarnings());

			log.trace("PtG CHUNK DAO! findReduced with request token; {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			ReducedPtGChunkDataTO reducedChunkDataTO = null;
			while (rs.next()) {
				reducedChunkDataTO = new ReducedPtGChunkDataTO();
				reducedChunkDataTO.setStatus(rs.getInt("sg.statusCode"));
				reducedChunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
				reducedChunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
				reducedChunkDataTO.setNormalizedStFN(rs
					.getString("rg.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					reducedChunkDataTO.setSurlUniqueID(uniqueID);
				}

				list.add(reducedChunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
			/* Return empty Collection! */
			return new ArrayList<ReducedPtGChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	public synchronized Collection<ReducedPtGChunkDataTO> findReduced(
		TRequestToken requestToken, int[] surlsUniqueIDs, String[] surlsArray) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: findReduced - unable to get a valid connection!");
			return new ArrayList<ReducedPtGChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;

		try {

			String str = "SELECT sg.statusCode, rg.ID, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID "
				+ "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
				+ "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
				+ "WHERE rq.r_token=? AND ( rg.sourceSURL_uniqueID IN "
				+ makeSURLUniqueIDWhere(surlsUniqueIDs)
				+ " AND rg.sourceSURL IN "
				+ makeSurlString(surlsArray) + " ) ";

			find = con.prepareStatement(str);

			logWarnings(con.getWarnings());

			ArrayList<ReducedPtGChunkDataTO> list = new ArrayList<ReducedPtGChunkDataTO>();
			find.setString(1, requestToken.getValue());
			logWarnings(find.getWarnings());

			log.trace("PtG CHUNK DAO! findReduced with griduser+surlarray; {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			ReducedPtGChunkDataTO chunkDataTO = null;
			while (rs.next()) {
				chunkDataTO = new ReducedPtGChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
				chunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
				chunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
				chunkDataTO.setNormalizedStFN(rs
					.getString("rg.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(uniqueID);
				}

				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
			/* Return empty Collection! */
			return new ArrayList<ReducedPtGChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that returns a Collection of ReducedPtGChunkDataTO associated to the
	 * given griduser, and whose SURLs are contained in the supplied array of
	 * Strings.
	 */
	public synchronized Collection<ReducedPtGChunkDataTO> findReduced(
		String griduser, int[] surlUniqueIDs, String[] surls) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: findReduced - unable to get a valid connection!");
			return new ArrayList<ReducedPtGChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			/*
			 * NOTE: we search also on the fromSurl because otherwise we lost all
			 * request_get that have not the uniqueID set because are not yet been
			 * used by anybody
			 */
			// get reduced chunks
			String str = "SELECT sg.statusCode, rg.ID, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID "
				+ "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
				+ "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
				+ "WHERE rq.client_dn=? AND ( rg.sourceSURL_uniqueID IN "
				+ makeSURLUniqueIDWhere(surlUniqueIDs)
				+ " AND rg.sourceSURL IN "
				+ makeSurlString(surls) + " ) ";
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			ArrayList<ReducedPtGChunkDataTO> list = new ArrayList<ReducedPtGChunkDataTO>();
			find.setString(1, griduser);
			logWarnings(find.getWarnings());

			log.trace("PtG CHUNK DAO! findReduced with griduser+surlarray; {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			ReducedPtGChunkDataTO chunkDataTO = null;
			while (rs.next()) {
				chunkDataTO = new ReducedPtGChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
				chunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
				chunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
				chunkDataTO.setNormalizedStFN(rs
					.getString("rg.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(uniqueID);
				}

				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
			/* Return empty Collection! */
			return new ArrayList<ReducedPtGChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method used in extraordinary situations to signal that data retrieved from
	 * the DB was malformed and could not be translated into the StoRM object
	 * model.
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
	 * see its request as being in the SRM_IN_PROGRESS state for ever. Hence the
	 * pressing need to inform it of the encountered problems.
	 */
	public synchronized void signalMalformedPtGChunk(PtGChunkDataTO auxTO) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: signalMalformedPtGChunk - unable to get a valid connection!");
			return;
		}
		String signalSQL = "UPDATE status_Get SET statusCode="
			+ StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE)
			+ ", explanation=? WHERE request_GetID=" + auxTO.primaryKey();
		PreparedStatement signal = null;
		try {
			signal = con.prepareStatement(signalSQL);
			logWarnings(con.getWarnings());
			/* Prepared statement spares DB-specific String notation! */
			signal.setString(1, "Request is malformed!");
			logWarnings(signal.getWarnings());

			log.trace("PTG CHUNK DAO: signalMalformed; {}", signal.toString());
			signal.executeUpdate();
			logWarnings(signal.getWarnings());
		} catch (SQLException e) {
			log.error("PtGChunkDAO! Unable to signal in DB that the request was "
				+ "malformed! Request: {}; Exception: {}", auxTO.toString(), e.toString());
		} finally {
			close(signal);
		}
	}

	/**
	 * Method that returns the number of Get requests on the given SURL, that are
	 * in SRM_FILE_PINNED state.
	 * 
	 * This method is intended to be used by PtGChunkCatalog in the
	 * isSRM_FILE_PINNED method invocation.
	 * 
	 * In case of any error, 0 is returned.
	 */
	// request_Get table
	public synchronized int numberInSRM_FILE_PINNED(int surlUniqueID) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: numberInSRM_FILE_PINNED - unable to get a valid connection!");
			return 0;
		}
		String str = "SELECT COUNT(rg.ID) "
			+ "FROM status_Get sg JOIN request_Get rg "
			+ "ON (sg.request_GetID=rg.ID) "
			+ "WHERE rg.sourceSURL_uniqueID=? AND sg.statusCode=?";
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			/* Prepared statement spares DB-specific String notation! */
			find.setInt(1, surlUniqueID);
			logWarnings(find.getWarnings());

			find.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
			logWarnings(find.getWarnings());

			log.trace("PtG CHUNK DAO - numberInSRM_FILE_PINNED method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			int numberFilePinned = 0;
			if (rs.next()) {
				numberFilePinned = rs.getInt(1);
			}
			return numberFilePinned;
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO! Unable to determine numberInSRM_FILE_PINNED! "
				+ "Returning 0! {}", e.getMessage(), e);
			return 0;
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that updates all expired requests in SRM_FILE_PINNED state, into
	 * SRM_RELEASED.
	 * 
	 * This is needed when the client forgets to invoke srmReleaseFiles().
	 * 
	 * @return
	 */
	public synchronized List<TSURL> transitExpiredSRM_FILE_PINNED() {

		// tring to the surl unique ID
		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: transitExpiredSRM_FILE_PINNED - unable to get a valid connection!");
			return new ArrayList<TSURL>();
		}
		HashMap<String, Integer> expiredSurlMap = new HashMap<String, Integer>();
		String str = null;
		// Statement statement = null;
		PreparedStatement preparedStatement = null;

		/* Find all expired surls */
		try {
			// start transaction
			con.setAutoCommit(false);

			str = "SELECT rg.sourceSURL , rg.sourceSURL_uniqueID "
				+ "FROM request_Get rg JOIN (status_Get sg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
				+ "WHERE sg.statusCode=?"
				+ " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

			preparedStatement = con.prepareStatement(str);
			preparedStatement.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));

			ResultSet res = preparedStatement.executeQuery();
			logWarnings(preparedStatement.getWarnings());

			while (res.next()) {
				String sourceSURL = res.getString("rg.sourceSURL");
				Integer uniqueID = new Integer(res.getInt("rg.sourceSURL_uniqueID"));
				/* If the uniqueID is not setted compute it */
				if (res.wasNull()) {
					try {
						TSURL tsurl = TSURL.makeFromStringWellFormed(sourceSURL);
						uniqueID = tsurl.uniqueId();
					} catch (InvalidTSURLAttributesException e) {
						log.warn("PtGChunkDAO! unable to build the TSURL from {}: "
							+ "InvalidTSURLAttributesException {}", sourceSURL, e.getMessage(), e);
					}
				}
				expiredSurlMap.put(sourceSURL, uniqueID);
			}

			if (expiredSurlMap.isEmpty()) {
				commit(con);
				log
					.trace("PtGChunkDAO! No chunk of PtG request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
				return new ArrayList<TSURL>();
			}
		} catch (SQLException e) {
			log.error("PtGChunkDAO! SQLException. {}", e.getMessage(), e);
			rollback(con);
			return new ArrayList<TSURL>();
		} finally {
			close(preparedStatement);
		}

		/* Update status of all expired surls to SRM_RELEASED */

		preparedStatement = null;
		try {

			str = "UPDATE "
				+ "status_Get sg JOIN (request_Get rg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
				+ "SET sg.statusCode=? "
				+ "WHERE sg.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

			preparedStatement = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			preparedStatement.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
			logWarnings(preparedStatement.getWarnings());

			preparedStatement.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
			logWarnings(preparedStatement.getWarnings());

			log.trace("PtG CHUNK DAO - transitExpiredSRM_FILE_PINNED method: {}", 
				preparedStatement.toString());

			int count = preparedStatement.executeUpdate();
			logWarnings(preparedStatement.getWarnings());

			if (count == 0) {
				log.trace("PtGChunkDAO! No chunk of PtG request was "
					+ "transited from SRM_FILE_PINNED to SRM_RELEASED.");
			} else {
				log.info("PtGChunkDAO! {} chunks of PtG requests were transited from"
					+ " SRM_FILE_PINNED to SRM_RELEASED.", count);
			}
		} catch (SQLException e) {
			log.error("PtGChunkDAO! Unable to transit expired SRM_FILE_PINNED chunks "
				+ "of PtG requests, to SRM_RELEASED! {}", e.getMessage(), e);
			rollback(con);
			return new ArrayList<TSURL>();
		} finally {
			close(preparedStatement);
		}

		/*
		 * in order to enhance performance here we can check if there is any file
		 * system with tape (T1D0, T1D1), if there is not any we can skip the
		 * following
		 */

		/* Find all not expired surls from PtG and BoL */

		HashSet<Integer> pinnedSurlSet = new HashSet<Integer>();
		try {

			// SURLs pinned by PtGs
			str = "SELECT rg.sourceSURL , rg.sourceSURL_uniqueID FROM "
				+ "request_Get rg JOIN (status_Get sg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
				+ "WHERE sg.statusCode=?"
				+ " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) < rq.pinLifetime ";

			preparedStatement = con.prepareStatement(str);
			preparedStatement.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));

			ResultSet res = preparedStatement.executeQuery();
			logWarnings(preparedStatement.getWarnings());

			while (res.next()) {
				String sourceSURL = res.getString("rg.sourceSURL");
				Integer uniqueID = new Integer(res.getInt("rg.sourceSURL_uniqueID"));
				/* If the uniqueID is not setted compute it */
				if (res.wasNull()) {
					try {
						TSURL tsurl = TSURL.makeFromStringWellFormed(sourceSURL);
						uniqueID = tsurl.uniqueId();
					} catch (InvalidTSURLAttributesException e) {
						log.warn("PtGChunkDAO! unable to build the TSURL from {}. "
							+ "InvalidTSURLAttributesException: {}", sourceSURL, e.getMessage());
					}
				}
				pinnedSurlSet.add(uniqueID);
			}

			close(preparedStatement);

			// SURLs pinned by BoLs
			str = "SELECT rb.sourceSURL , rb.sourceSURL_uniqueID FROM "
				+ "request_BoL rb JOIN (status_BoL sb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
				+ "WHERE sb.statusCode=?"
				+ " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) < rq.pinLifetime ";

			preparedStatement = con.prepareStatement(str);
			preparedStatement.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));

			res = preparedStatement.executeQuery();
			logWarnings(preparedStatement.getWarnings());

			while (res.next()) {
				String sourceSURL = res.getString("rb.sourceSURL");
				Integer uniqueID = new Integer(res.getInt("rb.sourceSURL_uniqueID"));
				/* If the uniqueID is not setted compute it */
				if (res.wasNull()) {
					try {
						TSURL tsurl = TSURL.makeFromStringWellFormed(sourceSURL);
						uniqueID = tsurl.uniqueId();
					} catch (InvalidTSURLAttributesException e) {
						log.warn("PtGChunkDAO! unable to build the TSURL from {}. "
							+ "InvalidTSURLAttributesException: {}", sourceSURL, e.getMessage(), e);
					}
				}
				pinnedSurlSet.add(uniqueID);
			}
			commit(con);
		} catch (SQLException e) {
			log.error("PtGChunkDAO! SQLException. {}", e.getMessage(), e);
			rollback(con);
		} finally {
			close(preparedStatement);
		}

		ArrayList<TSURL> expiredSurlList = new ArrayList<TSURL>();
		/* Remove the Extended Attribute pinned if there is not a valid surl on it */
		TSURL surl;
		for (Entry<String, Integer> surlEntry : expiredSurlMap.entrySet()) {
			if (!pinnedSurlSet.contains(surlEntry.getValue())) {
				try {
					surl = TSURL.makeFromStringValidate(surlEntry.getKey());
				} catch (InvalidTSURLAttributesException e) {
					log.error("Invalid SURL, cannot release the pin "
						+ "(Extended Attribute): {}", surlEntry.getKey());
					continue;
				}
				expiredSurlList.add(surl);
				StoRI stori;
				try {
					stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
				} catch (Throwable e) {
					log.error("Invalid SURL {} cannot release the pin. {}: {}", 
						surlEntry.getKey(), e.getClass().getCanonicalName(), e.getMessage(), e);
					continue;
				}

				if (stori.getVirtualFileSystem().getStorageClassType().isTapeEnabled()) {
					StormEA.removePinned(stori.getAbsolutePath());
				}
			}
		}
		return expiredSurlList;
	}

	/**
	 * Method that updates all chunks in SRM_FILE_PINNED state, into SRM_RELEASED.
	 * An array of long representing the primary key of each chunk is required:
	 * only they get the status changed provided their current status is
	 * SRM_FILE_PINNED.
	 * 
	 * This method is used during srmReleaseFiles
	 * 
	 * In case of any error nothing happens and no exception is thrown, but proper
	 * messagges get logged.
	 */
	public synchronized void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: transitSRM_FILE_PINNEDtoSRM_RELEASED - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE status_Get sg SET sg.statusCode=? "
			+ "WHERE sg.statusCode=? AND sg.request_GetID IN " + makeWhereString(ids);
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
			logWarnings(stmt.getWarnings());

			stmt.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
			logWarnings(stmt.getWarnings());

			log.trace("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: {}",
				stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("PtG CHUNK DAO! No chunk of PtG request was "
					+ "transited from SRM_FILE_PINNED to SRM_RELEASED.");
			} else {
				log.info("PtG CHUNK DAO! {} chunks of PtG requests were transited "
					+ "from SRM_FILE_PINNED to SRM_RELEASED.", count);
			}
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO! Unable to transit chunks"
				+ " from SRM_FILE_PINNED to SRM_RELEASED! {}", e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * @param ids
	 * @param token
	 */
	public synchronized void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids,
		TRequestToken token) {

		if (token == null) {
			transitSRM_FILE_PINNEDtoSRM_RELEASED(ids);
			return;
		}
		
		/*
		 * If a request token has been specified, only the related Get requests
		 * have to be released. This is done adding the r.r_token="..." clause in
		 * the where subquery.
		 */
		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: transitSRM_FILE_PINNEDtoSRM_RELEASED - "
				+ "unable to get a valid connection!");
			return;
		}
		
		String str = "UPDATE "
			+ "status_Get sg JOIN (request_Get rg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
			+ "SET sg.statusCode=? " + "WHERE sg.statusCode=? AND rq.r_token='"
			+ token.toString() + "' AND rg.ID IN " + makeWhereString(ids);
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_RELEASED));
			logWarnings(stmt.getWarnings());

			stmt.setInt(2,StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FILE_PINNED));
			logWarnings(stmt.getWarnings());

			log.trace("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("PtG CHUNK DAO! No chunk of PtG request was"
					+ " transited from SRM_FILE_PINNED to SRM_RELEASED.");
			} else {
				log.info("PtG CHUNK DAO! {} chunks of PtG requests were transited from "
					+ "SRM_FILE_PINNED to SRM_RELEASED.", count);
			}
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO! Unable to transit chunks from "
				+ "SRM_FILE_PINNED to SRM_RELEASED! {}", e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	public synchronized void updateStatus(TRequestToken requestToken,
		int[] surlUniqueIDs, String[] surls, TStatusCode statusCode,
		String explanation) {

		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: updateStatus - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE "
			+ "status_Get sg JOIN (request_Get rg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
			+ "SET sg.statusCode=? , sg.explanation=? " + "WHERE rq.r_token='"
			+ requestToken.toString() + "' AND ( rg.sourceSURL_uniqueID IN "
			+ makeSURLUniqueIDWhere(surlUniqueIDs) + " AND rg.sourceSURL IN "
			+ makeSurlString(surls) + " ) ";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1, StatusCodeConverter.getInstance().toDB(statusCode));
			logWarnings(stmt.getWarnings());

			stmt.setString(2, (explanation != null ? explanation : ""));
			logWarnings(stmt.getWarnings());

			log.trace("PtG CHUNK DAO - updateStatus: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("PtG CHUNK DAO! No chunk of PtG request was updated to {}.", 
					statusCode);
			} else {
				log.info("PtG CHUNK DAO! {} chunks of PtG requests were updated to {}.", 
					count, statusCode);
			}
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO! Unable to updated to {}! {}", statusCode, 
				e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	public synchronized void updateStatusOnMatchingStatus(int[] surlsUniqueIDs,
		String[] surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode,
		String explanation) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surls == null || explanation == null
			|| surlsUniqueIDs.length == 0 || surls.length == 0
			|| surlsUniqueIDs.length != surls.length) {

			throw new IllegalArgumentException(
				"Unable to perform the updateStatusOnMatchingStatus, "
					+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs + " surls="
					+ surls + " explanation=" + explanation);
		}

		doUpdateStatusOnMatchingStatus(null, surlsUniqueIDs, surls,
			expectedStatusCode, newStatusCode, explanation, false, true, true);
	}

	public synchronized void updateStatusOnMatchingStatus(
		TRequestToken requestToken, TStatusCode expectedStatusCode,
		TStatusCode newStatusCode, String explanation) {

		if (requestToken == null || requestToken.getValue().trim().isEmpty()
			|| explanation == null) {
			throw new IllegalArgumentException(
				"Unable to perform the updateStatusOnMatchingStatus, "
					+ "invalid arguments: requestToken=" + requestToken + " explanation="
					+ explanation);
		}
		doUpdateStatusOnMatchingStatus(requestToken, null, null,
			expectedStatusCode, newStatusCode, explanation, true, false, true);
	}

	public synchronized void updateStatusOnMatchingStatus(
		TRequestToken requestToken, int[] surlsUniqueIDs, String[] surls,
		TStatusCode expectedStatusCode, TStatusCode newStatusCode)
		throws IllegalArgumentException {

		if (requestToken == null || requestToken.getValue().trim().isEmpty()
			|| surlsUniqueIDs == null || surls == null || surlsUniqueIDs.length == 0
			|| surls.length == 0 || surlsUniqueIDs.length != surls.length) {
			throw new IllegalArgumentException(
				"Unable to perform the updateStatusOnMatchingStatus, "
					+ "invalid arguments: requestToken=" + requestToken
					+ "surlsUniqueIDs=" + surlsUniqueIDs + " surls=" + surls);
		}
		doUpdateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls,
			expectedStatusCode, newStatusCode, null, true, true, false);
	}

	public synchronized void doUpdateStatusOnMatchingStatus(
		TRequestToken requestToken, int[] surlUniqueIDs, String[] surls,
		TStatusCode expectedStatusCode, TStatusCode newStatusCode,
		String explanation, boolean withRequestToken, boolean withSurls,
		boolean withExplanation) throws IllegalArgumentException {

		if ((withRequestToken && requestToken == null)
			|| (withExplanation && explanation == null)
			|| (withSurls && (surlUniqueIDs == null || surls == null))) {

			throw new IllegalArgumentException(
				"Unable to perform the doUpdateStatusOnMatchingStatus, "
					+ "invalid arguments: withRequestToken=" + withRequestToken
					+ " requestToken=" + requestToken + " withSurls=" + withSurls
					+ " surlUniqueIDs=" + surlUniqueIDs + " surls=" + surls
					+ " withExplaination=" + withExplanation + " explanation="
					+ explanation);
		}
		if (!checkConnection()) {
			log
				.error("PTG CHUNK DAO: updateStatusOnMatchingStatus - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
			+ "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
			+ "SET sg.statusCode=? ";
		if (withExplanation) {
			str += " , " + buildExpainationSet(explanation);
		}
		str += " WHERE sg.statusCode=? ";
		if (withRequestToken) {
			str += " AND " + buildTokenWhereClause(requestToken);
		}
		if (withSurls) {
			str += " AND " + buildSurlsWhereClause(surlUniqueIDs, surls);
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1, StatusCodeConverter.getInstance().toDB(newStatusCode));
			logWarnings(stmt.getWarnings());

			stmt
				.setInt(2, StatusCodeConverter.getInstance().toDB(expectedStatusCode));
			logWarnings(stmt.getWarnings());

			log.trace("PtG CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("PtG CHUNK DAO! No chunk of PtG request was updated "
					+ "from {} to {}.", expectedStatusCode, newStatusCode);
			} else {
				log.debug("PtG CHUNK DAO! {} chunks of PtG requests were updated "
					+ "from {} to {}.", count, expectedStatusCode, newStatusCode);
			}
		} catch (SQLException e) {
			log.error("PtG CHUNK DAO! Unable to updated from {} to {}! {}", 
				expectedStatusCode, newStatusCode, e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * Auxiliary method used to close a ResultSet
	 */
	private void close(ResultSet rset) {

		if (rset != null) {
			try {
				rset.close();
			} catch (Exception e) {
				log.error("PTG CHUNK DAO! Unable to close ResultSet! Error: {}", 
					e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary method used to close a Statement
	 */
	private void close(Statement stmt) {

		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error("PTG CHUNK DAO! Unable to close Statement {} - Error: {}", 
					stmt.toString(), e.getMessage(), e);
			}
		}
	}

	private void commit(Connection con) {

		if (con != null) {
			try {
				con.commit();
				con.setAutoCommit(true);
			} catch (SQLException e) {
				log.error("PtG, SQL Exception: {}", e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary method used to roll back a failed transaction
	 */
	private void rollback(Connection con) {

		if (con != null) {
			try {
				con.rollback();
				con.setAutoCommit(true);
				log.error("PTG CHUNK DAO: roll back successful!");
			} catch (SQLException e2) {
				log.error("PTG CHUNK DAO: roll back failed! {}", e2.getMessage(), e2);
			}
		}
	}

	/**
	 * Private method that returns the generated ID: it throws an exception in
	 * case of any problem!
	 */
	private int extractID(ResultSet rs) throws Exception {

		if (rs == null) {
			throw new Exception("PTG CHUNK DAO! Null ResultSet!");
		}
		if (rs.next()) {
			return rs.getInt(1);
		} else {
			log.error("PTG CHUNK DAO! It was not possible to establish "
				+ "the assigned autoincrement primary key!");
			throw new Exception("PTG CHUNK DAO! It was not possible to"
				+ " establish the assigned autoincrement primary key!");
		}
	}

	/**
	 * Auxiliary private method that logs all SQL warnings.
	 */
	private void logWarnings(SQLWarning w) {

		if (w != null) {
			log.debug("PTG CHUNK DAO: {}", w.toString());
			while ((w = w.getNextWarning()) != null) {
				log.debug("PTG CHUNK DAO: {}", w.toString());
			}
		}
	}

	/**
	 * Method that returns a String containing all IDs.
	 */
	private String makeWhereString(long[] rowids) {

		StringBuffer sb = new StringBuffer("(");
		int n = rowids.length;
		for (int i = 0; i < n; i++) {
			sb.append(rowids[i]);
			if (i < (n - 1)) {
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
		for (int i = 0; i < surlUniqueIDs.length; i++) {
			if (i > 0) {
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

		for (int i = 0; i < n; i++) {

			SURL requestedSURL;

			try {
				requestedSURL = SURL.makeSURLfromString(surls[i]);
			} catch (NamespaceException e) {
				log.error(e.getMessage());
				log.debug("Skip '{}' during query creation", surls[i]);
				continue;
			}

			sb.append("'");
			sb.append(requestedSURL.getNormalFormAsString());
			sb.append("','");
			sb.append(requestedSURL.getQueryFormAsString());
			sb.append("'");

			if (i < (n - 1)) {
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
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			logWarnings(con.getWarnings());
			response = con.isValid(0);
		} catch (ClassNotFoundException | SQLException e) {
			log.error("PTG CHUNK DAO! Exception in setUpConnection! {}", e.getMessage(), e);
		}
		return response;
	}

	/**
	 * Auxiliary method that checks if time for resetting the connection has come,
	 * and eventually takes it down and up back again.
	 */
	private boolean checkConnection() {

		boolean response = true;
		if (reconnect) {
			log.debug("PTG CHUNK DAO! Reconnecting to DB! ");
			takeDownConnection();
			response = setUpConnection();
			if (response) {
				reconnect = false;
			}
		}
		return response;
	}

	/**
	 * Auxiliary method that tales down a connection to the DB.
	 */
	private void takeDownConnection() {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("PTG CHUNK DAO! Exception in takeDownConnection method: {}", 
					e.getMessage(), e);
			}
		}
	}

	public Collection<PtGChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray, String dn) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0 || dn == null) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray + " dn=" + dn);
		}
		return find(surlsUniqueIDs, surlsArray, dn, true);
	}

	public Collection<PtGChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray);
		}
		return find(surlsUniqueIDs, surlsArray, null, false);
	}

	private synchronized Collection<PtGChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray, String dn, boolean withDn)
		throws IllegalArgumentException {

		if ((withDn && dn == null) || surlsUniqueIDs == null
			|| surlsUniqueIDs.length == 0 || surlsArray == null
			|| surlsArray.length == 0) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray + " withDn=" + withDn + " dn=" + dn);
		}
		if (!checkConnection()) {
			log.error("PTG CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<PtGChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;

		try {

			String str = "SELECT rq.ID, rq.r_token, sg.statusCode, rq.pinLifetime, rg.ID, rq.timeStamp, "
				+ "rq.client_dn, rq.proxy, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID, "
				+ "d.isSourceADirectory, d.allLevelRecursive,  d.numOfLevels "
				+ "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
				+ "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
				+ "LEFT JOIN request_DirOption d ON rg.request_DirOptionID=d.ID "
				+ "WHERE ( rg.sourceSURL_uniqueID IN "
				+ makeSURLUniqueIDWhere(surlsUniqueIDs)
				+ " AND rg.sourceSURL IN "
				+ makeSurlString(surlsArray) + " )";

			if (withDn) {

				str += " AND rq.client_dn=\'" + dn + "\'";
			}

			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			List<PtGChunkDataTO> list = new ArrayList<PtGChunkDataTO>();

			log.trace("PTG CHUNK DAO - find method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			PtGChunkDataTO chunkDataTO = null;
			while (rs.next()) {

				chunkDataTO = new PtGChunkDataTO();
				chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
				chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
				chunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
				chunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));

				chunkDataTO.setNormalizedStFN(rs
					.getString("rg.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setLifeTime(rs.getInt("rq.pinLifetime"));
				chunkDataTO.setClientDN(rs.getString("rq.client_dn"));

				/**
				 * This code is only for the 1.3.18. This is a workaround to get FQANs
				 * using the proxy field on request_queue. The FE use the proxy field of
				 * request_queue to insert a single FQAN string containing all FQAN
				 * separeted by the "#" char. The proxy is a BLOB, hence it has to be
				 * properly conveted in string.
				 */
				java.sql.Blob blob = rs.getBlob("rq.proxy");
				if (!rs.wasNull() && blob != null) {
					byte[] bdata = blob.getBytes(1, (int) blob.length());
					chunkDataTO.setVomsAttributes(new String(bdata));
				}
				chunkDataTO.setDirOption(rs.getBoolean("d.isSourceADirectory"));
				chunkDataTO.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
				chunkDataTO.setNumLevel(rs.getInt("d.numOfLevels"));
				
				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<PtGChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	private String buildExpainationSet(String explanation) {

		return " sg.explanation='" + explanation + "' ";
	}

	private String buildTokenWhereClause(TRequestToken requestToken) {

		return " rq.r_token='" + requestToken.toString() + "' ";
	}

	private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls) {

		return " ( rg.sourceSURL_uniqueID IN "
			+ makeSURLUniqueIDWhere(surlsUniqueIDs) + " AND rg.sourceSURL IN "
			+ makeSurlString(surls) + " ) ";
	}

}
