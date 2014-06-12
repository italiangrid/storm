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
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DAO class for PtPChunkCatalog. This DAO is specifically designed to connect
 * to a MySQL DB. The raw data found in those tables is pre-treated in order to
 * turn it into the Object Model of StoRM. See Method comments for further info.
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the
 * object model.
 * 
 * @author EGRID ICTP
 * @version 2.0
 * @date June 2005
 */
public class PtPChunkDAO {

	private static final Logger log = LoggerFactory.getLogger(PtPChunkDAO.class);

	/* String with the name of the class for the DB driver */
	private final String driver = Configuration.getInstance().getDBDriver();
	/* String referring to the URL of the DB */
	private final String url = Configuration.getInstance().getDBURL();
	/* String with the password for the DB */
	private final String password = Configuration.getInstance().getDBPassword();
	/* String with the name for the DB */
	private final String name = Configuration.getInstance().getDBUserName();
	/* Connection to DB - WARNING!!! It is kept open all the time! */
	private Connection con = null;

	private static final PtPChunkDAO dao = new PtPChunkDAO();

	/* timer thread that will run a task to alert when reconnecting is necessary! */
	private Timer clock = null;
	/*
	 * timer task that will update the boolean signaling that a reconnection is
	 * needed
	 */
	private TimerTask clockTask = null;
	/* milliseconds that must pass before reconnecting to DB */
	private final long period = Configuration.getInstance().getDBReconnectPeriod() * 1000;
	/* initial delay in milliseconds before starting timer */
	private final long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;

	/* boolean that tells whether reconnection is needed because of MySQL bug! */
	private boolean reconnect = false;

	private PtPChunkDAO() {

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
	 * Method that returns the only instance of the PtPChunkDAO.
	 */
	public static PtPChunkDAO getInstance() {

		return dao;
	}

	/**
	 * Method used to save the changes made to a retrieved PtPChunkDataTO, back
	 * into the MySQL DB. Only the transferURL, statusCode and explanation, of
	 * status_Put table get written to the DB. Likewise for the pinLifetime and
	 * fileLifetime of request_queue. In case of any error, an error messagge gets
	 * logged but no exception is thrown.
	 */
	public synchronized void update(PtPChunkDataTO to) {

		if (!checkConnection()) {
			log.error("PtP CHUNK DAO: update - unable to get a valid connection!");
			return;
		}
		PreparedStatement updatePut = null;
		try {
			// prepare statement...
			updatePut = con
				.prepareStatement("UPDATE "
					+ "request_queue rq JOIN (status_Put sp, request_Put rp) ON "
					+ "(rq.ID=rp.request_queueID AND sp.request_PutID=rp.ID) "
					+ "SET sp.transferURL=?, sp.statusCode=?, sp.explanation=?, rq.pinLifetime=?, rq.fileLifetime=?, rq.config_FileStorageTypeID=?, rq.config_OverwriteID=?, "
					+ "rp.normalized_targetSURL_StFN=?, rp.targetSURL_uniqueID=? "
					+ "WHERE rp.ID=?");
			logWarnings(con.getWarnings());

			updatePut.setString(1, to.transferURL());
			logWarnings(updatePut.getWarnings());

			updatePut.setInt(2, to.status());
			logWarnings(updatePut.getWarnings());

			updatePut.setString(3, to.errString());
			logWarnings(updatePut.getWarnings());

			updatePut.setInt(4, to.pinLifetime());
			logWarnings(updatePut.getWarnings());

			updatePut.setInt(5, to.fileLifetime());
			logWarnings(updatePut.getWarnings());

			updatePut.setString(6, to.fileStorageType());
			logWarnings(updatePut.getWarnings());

			updatePut.setString(7, to.overwriteOption());
			logWarnings(updatePut.getWarnings());

			updatePut.setString(8, to.normalizedStFN());
			logWarnings(updatePut.getWarnings());

			updatePut.setInt(9, to.surlUniqueID());
			logWarnings(updatePut.getWarnings());

			updatePut.setLong(10, to.primaryKey());
			logWarnings(updatePut.getWarnings());
			// run updateStatusPut...
			log.trace("PtP CHUNK DAO - update method: {}", updatePut.toString());
			updatePut.executeUpdate();
			logWarnings(updatePut.getWarnings());
		} catch (SQLException e) {
			log.error("PtP CHUNK DAO: Unable to complete update! {}", e.getMessage(), e);
		} finally {
			close(updatePut);
		}
	}

	/**
	 * Updates the request_Put represented by the received ReducedPtPChunkDataTO
	 * by setting its normalized_targetSURL_StFN and targetSURL_uniqueID
	 * 
	 * @param chunkTO
	 */
	public synchronized void updateIncomplete(ReducedPtPChunkDataTO chunkTO) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: updateIncomplete - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE request_Put SET normalized_targetSURL_StFN=?, targetSURL_uniqueID=? "
			+ "WHERE ID=?";
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

			log.trace("PtP CHUNK DAO - update incomplete: {}", stmt.toString());
			stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
		} catch (SQLException e) {
			log.error("PtP CHUNK DAO: Unable to complete update incomplete! {}", 
				e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * Method used to refresh the PtPChunkDataTO information from the MySQL DB.
	 * This method is intended to be used during the srmAbortRequest/File
	 * operation. In case of any error, an error message gets logged but no
	 * exception is thrown; a null PtPChunkDataTO is returned.
	 */
	public synchronized PtPChunkDataTO refresh(long primary_key) {

		if (!checkConnection()) {
			log.error("PtP CHUNK DAO: refresh - unable to get a valid connection!");
			return null;
		}
		String prot = "SELECT tp.config_ProtocolsID FROM request_TransferProtocols tp "
			+ "WHERE tp.request_queueID IN "
			+ "(SELECT rp.request_queueID FROM request_Put rp " + "WHERE rp.ID=?)";

		String refresh = "SELECT rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, rq.s_token, rq.r_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, sp.statusCode, sp.transferURL "
			+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
			+ "ON (rq.ID=rp.request_queueID AND sp.request_PutID=rp.ID) "
			+ "WHERE rp.ID=?";

		PreparedStatement stmt = null;
		ResultSet rs = null;
		PtPChunkDataTO chunkDataTO = null;

		try {
			// get protocols for the request
			stmt = con.prepareStatement(prot);
			logWarnings(con.getWarnings());

			List<String> protocols = new ArrayList<String>();
			stmt.setLong(1, primary_key);
			logWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - refresh method: {}", stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());
			while (rs.next()) {
				protocols.add(rs.getString("tp.config_ProtocolsID"));
			}
			close(rs);
			close(stmt);

			// get chunk of the request
			stmt = con.prepareStatement(refresh);
			logWarnings(con.getWarnings());

			stmt.setLong(1, primary_key);
			logWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - refresh method: {}", stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());

			if (rs.next()) {
				chunkDataTO = new PtPChunkDataTO();
				chunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setPinLifetime(rs.getInt("rq.pinLifetime"));
				chunkDataTO.setFileLifetime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
				chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
				chunkDataTO.setPrimaryKey(rs.getLong("rp.ID"));
				chunkDataTO.setToSURL(rs.getString("rp.targetSURL"));
				chunkDataTO.setNormalizedStFN(rs
					.getString("rp.normalized_targetSURL_StFN"));
				int uniqueID = rs.getInt("rp.targetSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setExpectedFileSize(rs.getLong("rp.expectedFileSize"));
				chunkDataTO.setProtocolList(protocols);
				chunkDataTO.setStatus(rs.getInt("sp.statusCode"));
				chunkDataTO.setTransferURL(rs.getString("sp.transferURL"));
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
				if (rs.next()) {
					log.warn("ATTENTION in PtP CHUNK DAO! Possible DB corruption! "
						+ "refresh method invoked for specific chunk with id {}, but found "
						+ "more than one such chunks!", primary_key);
				}
			} else {
				log.warn("ATTENTION in PtP CHUNK DAO! Possible DB corruption! "
					+ "refresh method invoked for specific chunk with id {}, but chunk "
					+ "NOT found in persistence!", primary_key);
			}
		} catch (SQLException e) {
			log.error("PtP CHUNK DAO! Unable to refresh chunk! {}", e.getMessage(), e);
			chunkDataTO = null;
		} finally {
			close(rs);
			close(stmt);
		}
		return chunkDataTO;
	}

	/**
	 * Method that queries the MySQL DB to find all entries matching the supplied
	 * TRequestToken. The Collection contains the corresponding PtPChunkDataTO
	 * objects. An initial simple query establishes the list of protocols
	 * associated with the request. A second complex query establishes all chunks
	 * associated with the request, by properly joining request_queue, request_Put
	 * and status_Put. The considered fields are: (1) From status_Put: the ID
	 * field which becomes the TOs primary key, and statusCode. (2) From
	 * request_Put: targetSURL and expectedFileSize. (3) From request_queue:
	 * pinLifetime, fileLifetime, config_FileStorageTypeID, s_token,
	 * config_OverwriteID. In case of any error, a log gets written and an empty
	 * collection is returned. No exception is returned. NOTE! Chunks in
	 * SRM_ABORTED status are NOT returned! This is imporant because this method
	 * is intended to be used by the Feeders to fetch all chunks in the request,
	 * and aborted chunks should not be picked up for processing!
	 */
	public synchronized Collection<PtPChunkDataTO> find(TRequestToken requestToken) {

		if (!checkConnection()) {
			log.error("PtP CHUNK DAO: find - unable to get a valid connection!");
			return null;
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

			log.trace("PtP CHUNK DAO - find method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			while (rs.next()) {
				protocols.add(rs.getString("tp.config_ProtocolsID"));
			}
			close(rs);
			close(find);

			// get chunks of the request
			str = "SELECT rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, rq.s_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, sp.statusCode "
				+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
				+ "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
				+ "WHERE rq.r_token=? AND sp.statusCode<>?";

			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			List<PtPChunkDataTO> list = new ArrayList<PtPChunkDataTO>();
			find.setString(1, strToken);
			logWarnings(find.getWarnings());

			find.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
			logWarnings(find.getWarnings());

			log.trace("PtP CHUNK DAO - find method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			PtPChunkDataTO chunkDataTO = null;
			while (rs.next()) {
				chunkDataTO = new PtPChunkDataTO();
				chunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setPinLifetime(rs.getInt("rq.pinLifetime"));
				chunkDataTO.setFileLifetime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
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
				chunkDataTO.setPrimaryKey(rs.getLong("rp.ID"));
				chunkDataTO.setToSURL(rs.getString("rp.targetSURL"));

				chunkDataTO.setNormalizedStFN(rs
					.getString("rp.normalized_targetSURL_StFN"));
				int uniqueID = rs.getInt("rp.targetSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setExpectedFileSize(rs.getLong("rp.expectedFileSize"));
				chunkDataTO.setProtocolList(protocols);
				chunkDataTO.setRequestToken(strToken);
				chunkDataTO.setStatus(rs.getInt("sp.statusCode"));
				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<PtPChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that returns a Collection of ReducedPtPChunkDataTO associated to the
	 * given TRequestToken expressed as String.
	 */
	public synchronized Collection<ReducedPtPChunkDataTO> findReduced(
		String reqtoken, Collection<TSURL> surls) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: findReduced - unable to get a valid connection!");
			return new ArrayList<ReducedPtPChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;
		boolean addInClause = surls != null && !surls.isEmpty();
		try {
			// get reduced chunks
			String str = "SELECT rq.fileLifetime, rq.config_FileStorageTypeID, rp.ID, rp.targetSURL, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, sp.statusCode "
				+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
				+ "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
				+ "WHERE rq.r_token=?";
			if (addInClause) {
				str += " AND rp.targetSURL_uniqueID IN (";
				for (int i=0; i<surls.size(); i++) {
					str += i==0 ? "?" : ", ?";
				}
				str += ")";
			}
			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			List<ReducedPtPChunkDataTO> list = new ArrayList<ReducedPtPChunkDataTO>();
			find.setString(1, reqtoken);
			logWarnings(find.getWarnings());
			if (addInClause) {
				Iterator<TSURL> iterator = surls.iterator();
				int start = 2;
				while (iterator.hasNext()) {
					TSURL surl = iterator.next();
					find.setInt(start++, surl.uniqueId());
				}
			}
			logWarnings(find.getWarnings());
			log.trace("PtP CHUNK DAO! findReduced with request token; {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			ReducedPtPChunkDataTO reducedChunkDataTO = null;
			while (rs.next()) {
				reducedChunkDataTO = new ReducedPtPChunkDataTO();
				reducedChunkDataTO.setFileLifetime(rs.getInt("rq.fileLifetime"));
				reducedChunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				reducedChunkDataTO.setPrimaryKey(rs.getLong("rp.ID"));
				reducedChunkDataTO.setToSURL(rs.getString("rp.targetSURL"));
				reducedChunkDataTO.setNormalizedStFN(rs
					.getString("rp.normalized_targetSURL_StFN"));
				int uniqueID = rs.getInt("rp.targetSURL_uniqueID");
				if (!rs.wasNull()) {
					reducedChunkDataTO.setSurlUniqueID(uniqueID);
				}

				reducedChunkDataTO.setStatus(rs.getInt("sp.statusCode"));
				list.add(reducedChunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<ReducedPtPChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that returns a Collection of ReducedPtPChunkDataTO corresponding to
	 * the IDs supplied in the given List of Long. If the List is null or empty,
	 * an empty collection is returned and error messagges get logged.
	 */
	public synchronized Collection<ReducedPtPChunkDataTO> findReduced(
		List<Long> ids) {

		if (ids != null && !ids.isEmpty()) {
			if (!checkConnection()) {
				log
					.error("PtP CHUNK DAO: findReduced - unable to get a valid connection!");
				return new ArrayList<ReducedPtPChunkDataTO>();
			}
			PreparedStatement find = null;
			ResultSet rs = null;
			try {
				// get reduced chunks
				String str = "SELECT rq.fileLifetime, rq.config_FileStorageTypeID, rp.ID, rp.targetSURL, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, sp.statusCode "
					+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
					+ "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
					+ "WHERE rp.ID IN " + makeWhereString(ids);
				find = con.prepareStatement(str);
				logWarnings(con.getWarnings());

				List<ReducedPtPChunkDataTO> list = new ArrayList<ReducedPtPChunkDataTO>();
				log.trace("PtP CHUNK DAO! fetchReduced; {}", find.toString());
				rs = find.executeQuery();
				logWarnings(find.getWarnings());

				ReducedPtPChunkDataTO reducedChunkDataTO = null;
				while (rs.next()) {
					reducedChunkDataTO = new ReducedPtPChunkDataTO();
					reducedChunkDataTO.setFileLifetime(rs.getInt("rq.fileLifetime"));
					reducedChunkDataTO.setFileStorageType(rs
						.getString("rq.config_FileStorageTypeID"));
					reducedChunkDataTO.setPrimaryKey(rs.getLong("rp.ID"));
					reducedChunkDataTO.setToSURL(rs.getString("rp.targetSURL"));
					reducedChunkDataTO.setNormalizedStFN(rs
						.getString("rp.normalized_targetSURL_StFN"));
					int uniqueID = rs.getInt("rp.targetSURL_uniqueID");
					if (!rs.wasNull()) {
						reducedChunkDataTO.setSurlUniqueID(uniqueID);
					}

					reducedChunkDataTO.setStatus(rs.getInt("sp.statusCode"));
					list.add(reducedChunkDataTO);
				}
				return list;
			} catch (SQLException e) {
				log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
				/* return empty Collection */
				return new ArrayList<ReducedPtPChunkDataTO>();
			} finally {
				close(rs);
				close(find);
			}
		} else {
			log.warn("ATTENTION in PtP CHUNK DAO! fetchReduced "
				+ "invoked with null or empty list of IDs!");
			return new ArrayList<ReducedPtPChunkDataTO>();
		}
	}

	/**
	 * Method used in extraordinary situations to signal that data retrieved from
	 * the DB was malformed and could not be translated into the StoRM object
	 * model. This method attempts to change the status of the chunk to
	 * SRM_FAILURE and record it in the DB, in the status_Put table. This
	 * operation could potentially fail because the source of the malformed
	 * problems could be a problematic DB; indeed, initially only log messagges
	 * were recorded. Yet it soon became clear that the source of malformed data
	 * were actually the clients themselves and/or FE recording in the DB. In
	 * these circumstances the client would find its request as being in the
	 * SRM_IN_PROGRESS state for ever. Hence the pressing need to inform it of the
	 * encountered problems.
	 */
	public synchronized void signalMalformedPtPChunk(PtPChunkDataTO auxTO) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: signalMalformedPtPChunk - unable to get a valid connection!");
			return;
		}
		String signalSQL = "UPDATE status_Put sp SET sp.statusCode="
			+ StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE)
			+ ", sp.explanation=? " + "WHERE sp.request_PutID=" + auxTO.primaryKey();
		PreparedStatement signal = null;
		try {
			signal = con.prepareStatement(signalSQL);
			logWarnings(con.getWarnings());
			/* NB: Prepared statement spares DB-specific String notation! */
			signal.setString(1, "This chunk of the request is malformed!");
			logWarnings(signal.getWarnings());

			log.trace("PtP CHUNK DAO - signalMalformedPtPChunk method: {}", 
				signal.toString());
			signal.executeUpdate();
			logWarnings(signal.getWarnings());
		} catch (SQLException e) {
			log.error("PtPChunkDAO! Unable to signal in DB that a chunk of "
				+ "the request was malformed! Request: {}; Error: {}", auxTO.toString(), 
				e.getMessage(), e);
		} finally {
			close(signal);
		}
	}

	/**
	 * Method that returns the number of Put requests on the given SURL, that are
	 * in SRM_SPACE_AVAILABLE state. This method is intended to be used by
	 * PtPChunkCatalog in the isSRM_SPACE_AVAILABLE method ivocation. In case of
	 * any error, 0 is returned.
	 */
	public synchronized int numberInSRM_SPACE_AVAILABLE(int surlUniqueID) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: numberInSRM_SPACE_AVAILABLE - unable to get a valid connection!");
			return 0;
		}
		
		String str = "SELECT COUNT(rp.ID) FROM status_Put sp JOIN request_Put rp "
			+ "ON (sp.request_PutID=rp.ID) "
			+ "WHERE rp.targetSURL_uniqueID=? AND sp.statusCode=?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			/* Prepared statement spares DB-specific String notation! */
			stmt.setInt(1, surlUniqueID);
			logWarnings(stmt.getWarnings());

			stmt.setInt(2,StatusCodeConverter.getInstance().toDB(
				TStatusCode.SRM_SPACE_AVAILABLE));
			logWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - numberInSRM_SPACE_AVAILABLE method: {}", 
				stmt.toString());
			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());

			int numberSpaceAvailable = 0;
			if (rs.next()) {
				numberSpaceAvailable = rs.getInt(1);
			}
			return numberSpaceAvailable;
		} catch (SQLException e) {
			log.error("PtPChunkDAO! Unable to determine "
				+ "numberInSRM_SPACE_AVAILABLE! Returning 0! {}", e.getMessage(), e);
			return 0;
		} finally {
			close(rs);
			close(stmt);
		}
	}

	/**
	 * Method that updates all expired requests in SRM_SPACE_AVAILABLE state, into
	 * SRM_FILE_LIFTIME_EXPIRED. It returns a List containing the ID of the
	 * requests that were transited. This is needed when the client forgets to
	 * invoke srmPutDone().
	 */
	public synchronized List<Long> getExpiredSRM_SPACE_AVAILABLE() {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: getExpiredSRM_SPACE_AVAILABLE - unable to get a valid connection!");
			return new ArrayList<Long>();
		}

		String idsstr = "SELECT rp.ID FROM "
			+ "status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
			+ "WHERE sp.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

		ArrayList<Long> ids = new ArrayList<Long>();
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.prepareStatement(idsstr);
			logWarnings(con.getWarnings());

			stmt
				.setInt(
					1,
					StatusCodeConverter.getInstance().toDB(
						TStatusCode.SRM_SPACE_AVAILABLE));
			logWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - transitExpiredSRM_SPACE_AVAILABLE: {}", stmt.toString());

			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());

			while (rs.next()) {
				ids.add(new Long(rs.getLong("rp.ID")));
			}
		} catch (SQLException e) {
			log.error("PtPChunkDAO! Unable to select expired "
				+ "SRM_SPACE_AVAILABLE chunks of PtP requests. {}", e.getMessage(), e);
			/* make an empty list! */
			ids = new ArrayList<Long>();

		} finally {
			close(rs);
			close(stmt);
		}
		return ids;
	}

	/**
	 * Method that updates chunks in SRM_SPACE_AVAILABLE state, into SRM_SUCCESS.
	 * An array of long representing the primary key of each chunk is required.
	 * This is needed when the client invokes srmPutDone() In case of any error
	 * nothing happens and no exception is thrown, but proper messagges get
	 * logged.
	 */
	public synchronized void transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(
		List<Long> ids) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS - unable to get a valid connection!");
			return;
		}

		String str = "UPDATE "
			+ "status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
			+ "SET sp.statusCode=? " + "WHERE sp.statusCode=? AND rp.ID IN "
			+ makeWhereString(ids);

		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			stmt.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_SUCCESS));
			logWarnings(stmt.getWarnings());

			stmt.setInt(2, StatusCodeConverter.getInstance().toDB(
						TStatusCode.SRM_SPACE_AVAILABLE));
			logWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - "
				+ "transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS: {}", stmt.toString());

			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());

			if (count == 0) {
				log.trace("PtPChunkDAO! No chunk of PtP request was "
					+ "transited from SRM_SPACE_AVAILABLE to SRM_SUCCESS.");
			} else {
				log.info("PtPChunkDAO! {} chunks of PtP requests were transited "
					+ "from SRM_SPACE_AVAILABLE to SRM_SUCCESS.", count);
			}
		} catch (SQLException e) {
			log.error("PtPChunkDAO! Unable to transit chunks from "
				+ "SRM_SPACE_AVAILABLE to SRM_SUCCESS! {}", e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	/**
	 * Method that transits chunks in SRM_SPACE_AVAILABLE to SRM_ABORTED, for the
	 * given SURL: the overall request status of the requests containing that
	 * chunk, is not changed! The TURL is set to null. Beware, that the chunks may
	 * be part of requests that have finished, or that still have not finished
	 * because other chunks are still being processed.
	 */
	public synchronized void transitSRM_SPACE_AVAILABLEtoSRM_ABORTED(
		int surlUniqueID, String surl, String explanation) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: transitSRM_SPACE_AVAILABLEtoSRM_ABORTED - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE "
			+ "status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
			+ "SET sp.statusCode=?, sp.explanation=?, sp.transferURL=NULL "
			+ "WHERE sp.statusCode=? AND (rp.targetSURL_uniqueID=? OR rp.targetSURL=?)";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
			logWarnings(stmt.getWarnings());

			stmt.setString(2, explanation);
			logWarnings(stmt.getWarnings());

			stmt.setInt(3, StatusCodeConverter.getInstance().toDB(
						TStatusCode.SRM_SPACE_AVAILABLE));
			logWarnings(stmt.getWarnings());

			stmt.setInt(4, surlUniqueID);
			logWarnings(stmt.getWarnings());

			stmt.setString(5, surl);
			logWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - "
				+ "transitSRM_SPACE_AVAILABLEtoSRM_ABORTED: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());

			if (count > 0) {
				log.info("PtP CHUNK DAO! {} chunks were transited from "
					+ "SRM_SPACE_AVAILABLE to SRM_ABORTED.", count);
			} else {
				log.trace("PtP CHUNK DAO! No chunks "
					+ "were transited from SRM_SPACE_AVAILABLE to SRM_ABORTED.");
			}
		} catch (SQLException e) {
			log.error("PtP CHUNK DAO! Unable to "
				+ "transitSRM_SPACE_AVAILABLEtoSRM_ABORTED! {}", e.getMessage(), e);
		} finally {
			close(stmt);
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
				log.error("PTP CHUNK DAO! Unable to close Statement {} - Error: {}", 
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
				log.error("PTP CHUNK DAO! Unable to close ResultSet! Error: {}", 
					e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary private method that logs all SQL warnings.
	 */
	private void logWarnings(SQLWarning w) {

		if (w != null) {
			log.debug("PTP CHUNK DAO: {}", w.toString());
			while ((w = w.getNextWarning()) != null) {
				log.debug("PTP CHUNK DAO: {}", w.toString());
			}
		}
	}

	/**
	 * Private method that returns a String of all IDs retrieved by the last
	 * SELECT.
	 */
	private String makeWhereString(List<Long> rowids) {

		StringBuffer sb = new StringBuffer("(");
		for (Iterator<Long> i = rowids.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Auxiliary method that sets up the conenction to the DB.
	 */
	private boolean setUpConnection() {

		boolean response = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			if (con == null) {
				log.error("PTP CHUNK DAO! DriverManager returned a null connection!");
			} else {
				logWarnings(con.getWarnings());
				response = con.isValid(0);
			}
		} catch (ClassNotFoundException e) {
			log.error("PTP CHUNK DAO! Exception in setUpConenction! {}", 
				e.getMessage(), e);
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO! Exception in setUpConnection! {}", 
				e.getMessage(), e);
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
			log.debug("PTP CHUNK DAO! Reconnecting to DB! ");
			takeDownConnection();
			response = setUpConnection();
			if (response) {
				reconnect = false;
			}
		}
		return response;
	}

	/**
	 * Auxiliary method that takes down a connection to the DB.
	 */
	private void takeDownConnection() {

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				log.error("PTP CHUNK DAO! Exception in takeDownConnection method - "
					+ "could not close connection! {}", e.getMessage(), e);
			}
		}
	}

	public synchronized void updateStatus(int[] surlsUniqueIDs, String[] surls,
		TStatusCode statusCode, String explanation) throws IllegalArgumentException {

		if (explanation == null) {
			throw new IllegalArgumentException("Unable to perform the updateStatus, "
				+ "invalid arguments: explanation=" + explanation);
		}
		doUpdateStatus(null, surlsUniqueIDs, surls, statusCode, explanation, false,
			true);
	}

	public synchronized void updateStatus(TRequestToken requestToken,
		int[] surlsUniqueIDs, String[] surls, TStatusCode statusCode,
		String explanation) throws IllegalArgumentException {

		if (requestToken == null || requestToken.getValue().trim().isEmpty()
			|| explanation == null) {
			throw new IllegalArgumentException("Unable to perform the updateStatus, "
				+ "invalid arguments: requestToken=" + requestToken + " explanation="
				+ explanation);
		}
		doUpdateStatus(requestToken, surlsUniqueIDs, surls, statusCode,
			explanation, true, true);
	}

	private void doUpdateStatus(TRequestToken requestToken, int[] surlsUniqueIDs,
		String[] surls, TStatusCode statusCode, String explanation,
		boolean withRequestToken, boolean withExplaination)
		throws IllegalArgumentException {

		if ((withRequestToken && requestToken == null)
			|| (withExplaination && explanation == null)) {
			throw new IllegalArgumentException("Unable to perform the updateStatus, "
				+ "invalid arguments: withRequestToken=" + withRequestToken
				+ " requestToken=" + requestToken + " withExplaination="
				+ withExplaination + " explaination=" + explanation);
		}
		if (!checkConnection()) {
			log
				.error("PTP CHUNK DAO: updateStatus - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND "
			+ "rp.request_queueID=rq.ID " + "SET sp.statusCode=? ";
		if (withExplaination) {
			str += " , " + buildExpainationSet(explanation);
		}
		str += " WHERE ";
		if (withRequestToken) {
			str += buildTokenWhereClause(requestToken) + " AND ";
		}
		str += " ( rp.targetSURL_uniqueID IN "
			+ makeSURLUniqueIDWhere(surlsUniqueIDs) + " AND rp.targetSURL IN "
			+ makeSurlString(surls) + " ) ";
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			logWarnings(con.getWarnings());
			stmt.setInt(1, StatusCodeConverter.getInstance().toDB(statusCode));
			logWarnings(stmt.getWarnings());

			log.trace("PTP CHUNK DAO - updateStatus: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("PTP CHUNK DAO! No chunk of PTP request was updated to {}.", 
					statusCode);
			} else {
				log.info("PTP CHUNK DAO! {} chunks of PTP requests were updated "
					+ "to {}.", count, statusCode);
			}
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO! Unable to updated from to {}! {}", statusCode, 
				e.getMessage(), e);
		} finally {
			close(stmt);
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

	private void doUpdateStatusOnMatchingStatus(TRequestToken requestToken,
		int[] surlsUniqueIDs, String[] surls, TStatusCode expectedStatusCode,
		TStatusCode newStatusCode, String explanation, boolean withRequestToken,
		boolean withSurls, boolean withExplanation) throws IllegalArgumentException {

		if ((withRequestToken && requestToken == null)
			|| (withExplanation && explanation == null)
			|| (withSurls && (surlsUniqueIDs == null || surls == null))) {
			throw new IllegalArgumentException(
				"Unable to perform the doUpdateStatusOnMatchingStatus, "
					+ "invalid arguments: withRequestToken=" + withRequestToken
					+ " requestToken=" + requestToken + " withSurls=" + withSurls
					+ " surlsUniqueIDs=" + surlsUniqueIDs + " surls=" + surls
					+ " withExplaination=" + withExplanation + " explanation="
					+ explanation);
		}
		if (!checkConnection()) {
			log
				.error("PTP CHUNK DAO: updateStatusOnMatchingStatus - unable to get a valid connection!");
			return;
		}
		String str = "UPDATE "
			+ "status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
			+ "SET sp.statusCode=? ";
		if (withExplanation) {
			str += " , " + buildExpainationSet(explanation);
		}
		str += " WHERE sp.statusCode=? ";
		if (withRequestToken) {
			str += " AND " + buildTokenWhereClause(requestToken);
		}
		if (withSurls) {
			str += " AND " + buildSurlsWhereClause(surlsUniqueIDs, surls);
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

			log.trace("PTP CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt.toString());
			int count = stmt.executeUpdate();
			logWarnings(stmt.getWarnings());
			if (count == 0) {
				log.trace("PTP CHUNK DAO! No chunk of PTP request was updated "
					+ "from {} to {}.", expectedStatusCode, newStatusCode);
			} else {
				log.debug("PTP CHUNK DAO! {} chunks of PTP requests were updated "
					+ "from {} to {}.", count, expectedStatusCode, newStatusCode);
			}
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO! Unable to updated from {} to {}! Error: {}", 
				expectedStatusCode, newStatusCode, e.getMessage(), e);
		} finally {
			close(stmt);
		}
	}

	public Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray, String dn) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0 || dn == null) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray + " dn=" + dn);
		}
		return find(surlsUniqueIDs, surlsArray, dn, true);
	}

	public Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs,
		String[] surlsArray) throws IllegalArgumentException {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray);
		}
		return find(surlsUniqueIDs, surlsArray, null, false);
	}

	private synchronized Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs,
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
			log.error("PtP CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<PtPChunkDataTO>();
		}
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			// get chunks of the request
			String str = "SELECT rq.ID, rq.r_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, "
				+ "rq.s_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, "
				+ "sp.statusCode "
				+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
				+ "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
				+ "WHERE ( rp.targetSURL_uniqueID IN "
				+ makeSURLUniqueIDWhere(surlsUniqueIDs)
				+ " AND rp.targetSURL IN "
				+ makeSurlString(surlsArray) + " )";

			if (withDn) {
				str += " AND rq.client_dn=\'" + dn + "\'";
			}

			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			List<PtPChunkDataTO> list = new ArrayList<PtPChunkDataTO>();

			log.trace("PtP CHUNK DAO - find method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());
			PtPChunkDataTO chunkDataTO = null;
			while (rs.next()) {
				chunkDataTO = new PtPChunkDataTO();
				chunkDataTO.setFileStorageType(rs
					.getString("rq.config_FileStorageTypeID"));
				chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
				chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
				chunkDataTO.setPinLifetime(rs.getInt("rq.pinLifetime"));
				chunkDataTO.setFileLifetime(rs.getInt("rq.fileLifetime"));
				chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
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
				chunkDataTO.setPrimaryKey(rs.getLong("rp.ID"));
				chunkDataTO.setToSURL(rs.getString("rp.targetSURL"));

				chunkDataTO.setNormalizedStFN(rs
					.getString("rp.normalized_targetSURL_StFN"));
				int uniqueID = rs.getInt("rp.targetSURL_uniqueID");
				if (!rs.wasNull()) {
					chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
				}

				chunkDataTO.setExpectedFileSize(rs.getLong("rp.expectedFileSize"));				
				chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
				chunkDataTO.setStatus(rs.getInt("sp.statusCode"));
				list.add(chunkDataTO);
			}
			return list;
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<PtPChunkDataTO>();
		} finally {
			close(rs);
			close(find);
		}
	}

	public synchronized List<String> findProtocols(long requestQueueId) {

		if (!checkConnection()) {
			log.error("PtP CHUNK DAO: find - unable to get a valid connection!");
			return new ArrayList<String>();
		}
		String str = null;
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			str = "SELECT tp.config_ProtocolsID "
				+ "FROM request_TransferProtocols tp " + "WHERE tp.request_queueID=?";

			find = con.prepareStatement(str);
			logWarnings(con.getWarnings());

			List<String> protocols = new ArrayList<String>();
			find.setLong(1, requestQueueId);
			logWarnings(find.getWarnings());

			log.trace("PtP CHUNK DAO - findProtocols method: {}", find.toString());
			rs = find.executeQuery();
			logWarnings(find.getWarnings());

			while (rs.next()) {
				protocols.add(rs.getString("tp.config_ProtocolsID"));
			}

			return protocols;
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return new ArrayList<String>();
		} finally {
			close(rs);
			close(find);
		}
	}

	private String buildExpainationSet(String explanation) {

		return " sp.explanation='" + explanation + "' ";
	}

	private String buildTokenWhereClause(TRequestToken requestToken) {

		return " rq.r_token='" + requestToken.toString() + "' ";
	}

	private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls) {

		return " ( rp.targetSURL_uniqueID IN "
			+ makeSURLUniqueIDWhere(surlsUniqueIDs) + " AND rp.targetSURL IN "
			+ makeSurlString(surls) + " ) ";
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

}
