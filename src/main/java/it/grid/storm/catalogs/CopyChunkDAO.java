/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
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
 * @author EGRID - ICTP Trieste
 * @version 2.0
 * @date September 2005
 */
public class CopyChunkDAO {

	private static final Logger log = LoggerFactory.getLogger(CopyChunkDAO.class);

	/* String with the name of the class for the DB driver */
	private final String driver = Configuration.getInstance().getDBDriver();
	/* String referring to the URL of the DB */
	private final String url = Configuration.getInstance().getStormDbURL();
	/* String with the password for the DB */
	private final String password = Configuration.getInstance().getDBPassword();
	/* String with the name for the DB */
	private final String name = Configuration.getInstance().getDBUserName();

	/* Connection to DB - WARNING!!! It is kept open all the time! */
	private Connection con = null;
	/* boolean that tells whether reconnection is needed because of MySQL bug! */
	private boolean reconnect = false;

	/* Singleton instance */
	private final static CopyChunkDAO dao = new CopyChunkDAO();

	/* timer thread that will run a task to alert when reconnecting is necessary! */
	private Timer clock = null;
	/*
	 * timer task that will update the boolean signaling that a reconnection is
	 * needed!
	 */
	private TimerTask clockTask = null;
	/* milliseconds that must pass before reconnecting to DB */
	private final long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;
	/* initial delay in milliseconds before starting timer */
	private final long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;

	private CopyChunkDAO() {

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
	 * Method that returns the only instance of the CopyChunkDAO.
	 */
	public static CopyChunkDAO getInstance() {

		return dao;
	}

	/**
	 * Method used to save the changes made to a retrieved CopyChunkDataTO, back
	 * into the MySQL DB.
	 * 
	 * Only statusCode and explanation, of status_Copy table get written to the
	 * DB. Likewise for fileLifetime of request_queue table.
	 * 
	 * In case of any error, an error messagge gets logged but no exception is
	 * thrown.
	 */
	public synchronized void update(CopyChunkDataTO to) {

		if (!checkConnection()) {
			log.error("COPY CHUNK DAO: update - unable to get a valid connection!");
			return;
		}
		PreparedStatement updateFileReq = null;
		try {
			// ready updateFileReq...
			updateFileReq = con
				.prepareStatement("UPDATE request_queue rq JOIN (status_Copy sc, request_Copy rc) "
					+ "ON (rq.ID=rc.request_queueID AND sc.request_CopyID=rc.ID) "
					+ "SET sc.statusCode=?, sc.explanation=?, rq.fileLifetime=?, rq.config_FileStorageTypeID=?, rq.config_OverwriteID=?, "
					+ "rc.normalized_sourceSURL_StFN=?, rc.sourceSURL_uniqueID=?, rc.normalized_targetSURL_StFN=?, rc.targetSURL_uniqueID=? "
					+ "WHERE rc.ID=?");
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
		} catch (SQLException e) {
			log.error("COPY CHUNK DAO: Unable to complete update! {}", 
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
	public synchronized void updateIncomplete(ReducedCopyChunkDataTO chunkTO) {

		if (!checkConnection()) {
			log
				.error("COPY CHUNK DAO: updateIncomplete - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE request_Copy SET normalized_sourceSURL_StFN=?, sourceSURL_uniqueID=?, normalized_targetSURL_StFN=?, targetSURL_uniqueID=? "
			+ "WHERE ID=?";
		PreparedStatement stmt = null;
		try {
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

			log.trace("COPY CHUNK DAO - update incomplete: {}", stmt.toString());
			stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
		} catch (SQLException e) {
			log.error("COPY CHUNK DAO: Unable to complete update incomplete! {}", 
				e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * Method that queries the MySQL DB to find all entries matching the supplied
	 * TRequestToken. The Collection contains the corresponding CopyChunkDataTO
	 * objects.
	 * 
	 * A complex query establishes all chunks associated with the request token,
	 * by properly joining request_queue, request_Copy and status_Copy. The
	 * considered fields are:
	 * 
	 * (1) From status_Copy: the ID field which becomes the TOs primary key, and
	 * statusCode.
	 * 
	 * (2) From request_Copy: targetSURL and sourceSURL.
	 * 
	 * (3) From request_queue: fileLifetime, config_FileStorageTypeID, s_token,
	 * config_OverwriteID.
	 * 
	 * In case of any error, a log gets written and an empty collection is
	 * returned. No exception is returned.
	 * 
	 * NOTE! Chunks in SRM_ABORTED status are NOT returned!
	 */
	public synchronized Collection<CopyChunkDataTO> find(
		TRequestToken requestToken) {

		if (!checkConnection()) {
			log.error("COPY CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<CopyChunkDataTO>();
		}
		String strToken = requestToken.toString();
		String str = null;
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			/* get chunks of the request */
			str = "SELECT rq.s_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.fileLifetime, rc.ID, rc.sourceSURL, rc.targetSURL, rc.normalized_sourceSURL_StFN, rc.sourceSURL_uniqueID, rc.normalized_targetSURL_StFN, rc.targetSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "
				+ "FROM request_queue rq JOIN (request_Copy rc, status_Copy sc) "
				+ "ON (rc.request_queueID=rq.ID AND sc.request_CopyID=rc.ID) "
				+ "LEFT JOIN request_DirOption d ON rc.request_DirOptionID=d.ID "
				+ "WHERE rq.r_token=? AND sc.statusCode<>?";

			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			ArrayList<CopyChunkDataTO> list = new ArrayList<CopyChunkDataTO>();
			find.setString(1, strToken);
			logWarnings(find.getWarnings());

			find.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
			logWarnings(find.getWarnings());

			log.debug("COPY CHUNK DAO: find method; " + find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			CopyChunkDataTO chunkDataTO;
			while (rs.next()) {
				chunkDataTO = new CopyChunkDataTO();
				chunkDataTO.setRequestToken(strToken);
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
				chunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setLifeTime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setPrimaryKey(rs.getLong("rc.ID"));
				chunkDataTO.setFromSURL(rs.getString("rc.sourceSURL"));
				chunkDataTO.setNormalizedSourceStFN(rs
					.getString("rc.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSourceSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setToSURL(rs.getString("rc.targetSURL"));
				chunkDataTO.setNormalizedTargetStFN(rs
					.getString("rc.normalized_sourceSURL_StFN"));
				uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setTargetSurlUniqueID(new Integer(uniqueID));
				}

				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("COPY CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<CopyChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}

	}

	public synchronized Collection<CopyChunkDataTO> find(
		TRequestToken requestToken, int[] surlUniqueIDs, String[] surls) {

		if (!checkConnection()) {
			log.error("COPY CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<CopyChunkDataTO>();
		}
		String strToken = requestToken.toString();
		String str = null;
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			/* get chunks of the request */
			str = "SELECT rq.s_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.fileLifetime, rc.ID, rc.sourceSURL, rc.targetSURL, rc.normalized_sourceSURL_StFN, rc.sourceSURL_uniqueID, rc.normalized_targetSURL_StFN, rc.targetSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "
				+ "FROM request_queue rq JOIN (request_Copy rc, status_Copy sc) "
				+ "ON (rc.request_queueID=rq.ID AND sc.request_CopyID=rc.ID) "
				+ "LEFT JOIN request_DirOption d ON rc.request_DirOptionID=d.ID "
				+ "WHERE rq.r_token=? AND ( rc.sourceSURL_uniqueID IN "
				+ makeSURLUniqueIDWhere(surlUniqueIDs)
				+ " AND rc.sourceSURL IN "
				+ makeSurlString(surls) + " ) ";

			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			ArrayList<CopyChunkDataTO> list = new ArrayList<CopyChunkDataTO>();
			find.setString(1, strToken);
			logWarnings(find.getWarnings());

			log.debug("COPY CHUNK DAO: find method; {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			CopyChunkDataTO chunkDataTO;
			while (rs.next()) {
				chunkDataTO = new CopyChunkDataTO();
				chunkDataTO.setRequestToken(strToken);
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
				chunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setLifeTime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setPrimaryKey(rs.getLong("rc.ID"));
				chunkDataTO.setFromSURL(rs.getString("rc.sourceSURL"));
				chunkDataTO.setNormalizedSourceStFN(rs
					.getString("rc.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSourceSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setToSURL(rs.getString("rc.targetSURL"));
				chunkDataTO.setNormalizedTargetStFN(rs
					.getString("rc.normalized_sourceSURL_StFN"));
				uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setTargetSurlUniqueID(new Integer(uniqueID));
				}

				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("COPY CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<CopyChunkDataTO>();
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
	 * its request as being in the SRM_IN_PROGRESS state for ever. Hence the
	 * pressing need to inform it of the encountered problems.
	 */
	public synchronized void signalMalformedCopyChunk(CopyChunkDataTO auxTO) {

		if (!checkConnection()) {
			log
				.error("COPY CHUNK DAO: signalMalformedCopyChunk - unable to get a valid connection!");
			return;
		}
		String signalSQL = "UPDATE status_Copy SET statusCode="
			+ StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE)
			+ ", explanation=? WHERE request_CopyID=" + auxTO.primaryKey();

		PreparedStatement signal = null;
		try {
			/* update storm_put_filereq */
			signal = con.prepareStatement(signalSQL);
			logWarnings(con.getWarnings());

			/* Prepared statement spares DB-specific String notation! */
			signal.setString(1, "Request is malformed!");
			logWarnings(signal.getWarnings());

			signal.executeUpdate();
			logWarnings(signal.getWarnings());
		} catch (SQLException e) {
			log.error("CopyChunkDAO! Unable to signal in DB that the request was "
				+ "malformed! Request: {}; Error: {}", auxTO.toString(), 
				e.getMessage(), e);
		} finally {
			close(signal);
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
				log.error("COPY CHUNK DAO! Unable to close Statement {} - Error: {}", 
					stmt.toString(), e.getMessage(), e);
			}
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
				log.error("COPY CHUNK DAO! Unable to close ResultSet! Error: {}",
					e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary private method that logs all SQL warnings.
	 */
	private void logWarnings(SQLWarning w) {

		if (w != null) {
			log.debug("COPY CHUNK DAO: {}", w.toString());
			while ((w = w.getNextWarning()) != null) {
				log.debug("COPY CHUNK DAO: {}", w.toString());
			}
		}
	}

	/**
	 * Auxiliary method that sets up the conenction to the DB.
	 */
	private boolean setUpConnection() {

		boolean response = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			logWarnings(con.getWarnings());
			response = con.isValid(0);
		} catch (SQLException | ClassNotFoundException e) {
			log.error("COPY CHUNK DAO! Exception in setUpConnection! {}", e.getMessage(), e);
		}
		return response;
	}

	/**
	 * Auxiliary method that checks if time for resetting the connection has come,
	 * and eventually takes it down and up back again.
	 */
	private synchronized boolean checkConnection() {

		boolean response = true;
		if (reconnect) {
			log.debug("COPY CHUNK DAO! Reconnecting to DB! ");
			takeDownConnection();
			response = setUpConnection();
			if (response) {
				reconnect = false;
			}
		}
		return response;
	}

	/**
	 * Auxiliary method that takes down a conenctin to the DB.
	 */
	private void takeDownConnection() {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("COPY CHUNK DAO! Exception in takeDownConnection method: {}", 
					e.getMessage(), e);
			}
		}
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
				.error("COPY CHUNK DAO: updateStatusOnMatchingStatus - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE request_queue rq JOIN (status_Copy sc, request_Copy rc) "
			+ "ON (rq.ID=rc.request_queueID AND sc.request_CopyID=rc.ID) "
			+ "SET sc.statusCode=? ";
		if (withExplanation) {
			str += " , " + buildExpainationSet(explanation);
		}
		str += " WHERE sc.statusCode=? ";
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

			log.trace("COPY CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("COPY CHUNK DAO! No chunk of COPY request was updated "
					+ "from {} to {}.", expectedStatusCode, newStatusCode);
			} else {
				log.debug("COPY CHUNK DAO! {} chunks of COPY requests were updated "
					+ "from {} to {}.", count, expectedStatusCode, newStatusCode);
			}
		} catch (SQLException e) {
			log.error("COPY CHUNK DAO! Unable to updated from {} to {}! {}", 
				expectedStatusCode, newStatusCode, e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * Method that returns a String containing all Surl's IDs.
	 */
	private String makeSURLUniqueIDWhere(int[] surlUniqueIDs) {

		StringBuilder sb = new StringBuilder("(");
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

		StringBuilder sb = new StringBuilder("(");
		int n = surls.length;
		for (int i = 0; i < n; i++) {
			sb.append("'");
			sb.append(surls[i]);
			sb.append("'");
			if (i < (n - 1)) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	public synchronized Collection<CopyChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray, String dn) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0 || dn == null) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray + " dn=" + dn);
		}
		return find(surlsUniqueIDs, surlsArray, dn, true);
	}

	public synchronized Collection<CopyChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray);
		}
		return find(surlsUniqueIDs, surlsArray, null, false);
	}

	private synchronized Collection<CopyChunkDataTO> find(int[] surlsUniqueIDs,
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
			log.error("COPY CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<CopyChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			String str = "SELECT rq.r_token, rq.s_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, "
				+ "rq.fileLifetime, rc.ID, rc.sourceSURL, rc.targetSURL, rc.normalized_sourceSURL_StFN, "
				+ "rc.sourceSURL_uniqueID, rc.normalized_targetSURL_StFN, rc.targetSURL_uniqueID, d.isSourceADirectory, "
				+ "d.allLevelRecursive, d.numOfLevels "
				+ "FROM request_queue rq JOIN (request_Copy rc, status_Copy sc) "
				+ "ON (rc.request_queueID=rq.ID AND sc.request_CopyID=rc.ID) "
				+ "LEFT JOIN request_DirOption d ON rc.request_DirOptionID=d.ID "
				+ "WHERE ( rc.sourceSURL_uniqueID IN "
				+ makeSURLUniqueIDWhere(surlsUniqueIDs)
				+ " AND rc.sourceSURL IN "
				+ makeSurlString(surlsArray) + " )";
			if (withDn) {
				str += " AND rq.client_dn=\'" + dn + "\'";
			}
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			List<CopyChunkDataTO> list = new ArrayList<CopyChunkDataTO>();

			log.trace("COPY CHUNK DAO - find method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			CopyChunkDataTO chunkDataTO = null;
			while (rs.next()) {
				chunkDataTO = new CopyChunkDataTO();
				chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
				chunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setLifeTime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setPrimaryKey(rs.getLong("rc.ID"));
				chunkDataTO.setFromSURL(rs.getString("rc.sourceSURL"));
				chunkDataTO.setNormalizedSourceStFN(rs
					.getString("rc.normalized_sourceSURL_StFN"));
				int uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSourceSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setToSURL(rs.getString("rc.targetSURL"));
				chunkDataTO.setNormalizedTargetStFN(rs
					.getString("rc.normalized_sourceSURL_StFN"));
				uniqueID = rs.getInt("rc.sourceSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setTargetSurlUniqueID(new Integer(uniqueID));
				}
				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("COPY CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<CopyChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	private String buildExpainationSet(String explanation) {

		return " sc.explanation='" + explanation + "' ";
	}

	private String buildTokenWhereClause(TRequestToken requestToken) {

		return " rq.r_token='" + requestToken.toString() + "' ";
	}

	private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls) {

		return " ( rc.sourceSURL_uniqueID IN "
			+ makeSURLUniqueIDWhere(surlsUniqueIDs) + " AND rc.sourceSURL IN "
			+ makeSurlString(surls) + " ) ";
	}

}
