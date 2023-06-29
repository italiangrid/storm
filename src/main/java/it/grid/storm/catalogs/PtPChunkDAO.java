/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import static it.grid.storm.catalogs.ChunkDAOUtils.buildInClauseForArray;
import static it.grid.storm.catalogs.ChunkDAOUtils.printWarnings;
import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_LIFETIME_EXPIRED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_SPACE_AVAILABLE;
import static it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
	private final String url = Configuration.getInstance().getStormDbURL();
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

	private StatusCodeConverter statusCodeConverter = StatusCodeConverter.getInstance();

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
			printWarnings(con.getWarnings());

			updatePut.setString(1, to.transferURL());
			printWarnings(updatePut.getWarnings());

			updatePut.setInt(2, to.status());
			printWarnings(updatePut.getWarnings());

			updatePut.setString(3, to.errString());
			printWarnings(updatePut.getWarnings());

			updatePut.setInt(4, to.pinLifetime());
			printWarnings(updatePut.getWarnings());

			updatePut.setInt(5, to.fileLifetime());
			printWarnings(updatePut.getWarnings());

			updatePut.setString(6, to.fileStorageType());
			printWarnings(updatePut.getWarnings());

			updatePut.setString(7, to.overwriteOption());
			printWarnings(updatePut.getWarnings());

			updatePut.setString(8, to.normalizedStFN());
			printWarnings(updatePut.getWarnings());

			updatePut.setInt(9, to.surlUniqueID());
			printWarnings(updatePut.getWarnings());

			updatePut.setLong(10, to.primaryKey());
			printWarnings(updatePut.getWarnings());
			// run updateStatusPut...
			log.trace("PtP CHUNK DAO - update method: {}", updatePut);
			updatePut.executeUpdate();
			printWarnings(updatePut.getWarnings());
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
			printWarnings(con.getWarnings());

			stmt.setString(1, chunkTO.normalizedStFN());
			printWarnings(stmt.getWarnings());

			stmt.setInt(2, chunkTO.surlUniqueID());
			printWarnings(stmt.getWarnings());

			stmt.setLong(3, chunkTO.primaryKey());
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - update incomplete: {}", stmt);
			stmt.executeUpdate();
			printWarnings(stmt.getWarnings());
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
	public synchronized PtPChunkDataTO refresh(long id) {

		if (!checkConnection()) {
			log.error("PtP CHUNK DAO: refresh - unable to get a valid connection!");
			return null;
		}
		String prot = "SELECT tp.config_ProtocolsID FROM request_TransferProtocols tp "
			+ "WHERE tp.request_queueID IN "
			+ "(SELECT rp.request_queueID FROM request_Put rp WHERE rp.ID=?)";

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
			printWarnings(con.getWarnings());

			List<String> protocols = Lists.newArrayList();
			stmt.setLong(1, id);
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - refresh method: {}", stmt);
			rs = stmt.executeQuery();
			printWarnings(stmt.getWarnings());
			while (rs.next()) {
				protocols.add(rs.getString("tp.config_ProtocolsID"));
			}
			close(rs);
			close(stmt);

			// get chunk of the request
			stmt = con.prepareStatement(refresh);
			printWarnings(con.getWarnings());

			stmt.setLong(1, id);
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - refresh method: {}", stmt);
			rs = stmt.executeQuery();
			printWarnings(stmt.getWarnings());

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
					chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
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
				 * separated by the "#" char. The proxy is a BLOB, hence it has to be
				 * properly converted in string.
				 */
				java.sql.Blob blob = rs.getBlob("rq.proxy");
				if (!rs.wasNull() && blob != null) {
					byte[] bdata = blob.getBytes(1, (int) blob.length());
					chunkDataTO.setVomsAttributes(new String(bdata));
				}
				if (rs.next()) {
					log.warn("ATTENTION in PtP CHUNK DAO! Possible DB corruption! "
						+ "refresh method invoked for specific chunk with id {}, but found "
						+ "more than one such chunks!", id);
				}
			} else {
				log.warn("ATTENTION in PtP CHUNK DAO! Possible DB corruption! "
					+ "refresh method invoked for specific chunk with id {}, but chunk "
					+ "NOT found in persistence!", id);
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
	 * SRM_ABORTED status are NOT returned! This is important because this method
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
			printWarnings(con.getWarnings());

			List<String> protocols = Lists.newArrayList();
			find.setString(1, strToken);
			printWarnings(find.getWarnings());

			log.trace("PtP CHUNK DAO - find method: {}", find);
			rs = find.executeQuery();
			printWarnings(find.getWarnings());

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
			printWarnings(con.getWarnings());

			List<PtPChunkDataTO> list = Lists.newArrayList();
			find.setString(1, strToken);
			printWarnings(find.getWarnings());

			find.setInt(2,
				statusCodeConverter.toDB(SRM_ABORTED));
			printWarnings(find.getWarnings());

			log.trace("PtP CHUNK DAO - find method: {}", find);
			rs = find.executeQuery();
			printWarnings(find.getWarnings());
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
				 * separated by the "#" char. The proxy is a BLOB, hence it has to be
				 * properly converted in string.
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
					chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
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
			return Lists.newArrayList();
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
			return Lists.newArrayList();
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
			printWarnings(con.getWarnings());

			List<ReducedPtPChunkDataTO> list = Lists.newArrayList();
			find.setString(1, reqtoken);
			printWarnings(find.getWarnings());
			if (addInClause) {
				Iterator<TSURL> iterator = surls.iterator();
				int start = 2;
				while (iterator.hasNext()) {
					TSURL surl = iterator.next();
					find.setInt(start++, surl.uniqueId());
				}
			}
			printWarnings(find.getWarnings());
			log.trace("PtP CHUNK DAO! findReduced with request token; {}", find);
			rs = find.executeQuery();
			printWarnings(find.getWarnings());

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
			return Lists.newArrayList();
		} finally {
			close(rs);
			close(find);
		}
	}

	/**
	 * Method that returns a Collection of ReducedPtPChunkDataTO corresponding to
	 * the IDs supplied in the given List of Long. If the List is null or empty,
	 * an empty collection is returned and error messages get logged.
	 */
	public synchronized Collection<ReducedPtPChunkDataTO> findReduced(
		List<Long> ids) {

		if (ids != null && !ids.isEmpty()) {
			if (!checkConnection()) {
				log
					.error("PtP CHUNK DAO: findReduced - unable to get a valid connection!");
				return Lists.newArrayList();
			}
			PreparedStatement find = null;
			ResultSet rs = null;
			try {
				// get reduced chunks
				String str = "SELECT rq.fileLifetime, rq.config_FileStorageTypeID, rp.ID, rp.targetSURL, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, sp.statusCode "
					+ "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
					+ "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
					+ "WHERE rp.ID IN (" + StringUtils.join(ids.toArray(), ',') + ")";
				find = con.prepareStatement(str);
				printWarnings(con.getWarnings());

				List<ReducedPtPChunkDataTO> list = Lists.newArrayList();
				log.trace("PtP CHUNK DAO! fetchReduced; {}", find);
				rs = find.executeQuery();
				printWarnings(find.getWarnings());

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
				return Lists.newArrayList();
			} finally {
				close(rs);
				close(find);
			}
		} else {
			log.warn("ATTENTION in PtP CHUNK DAO! fetchReduced "
				+ "invoked with null or empty list of IDs!");
			return Lists.newArrayList();
		}
	}

	/**
	 * Method used in extraordinary situations to signal that data retrieved from
	 * the DB was malformed and could not be translated into the StoRM object
	 * model. This method attempts to change the status of the chunk to
	 * SRM_FAILURE and record it in the DB, in the status_Put table. This
	 * operation could potentially fail because the source of the malformed
	 * problems could be a problematic DB; indeed, initially only log messages
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
			+ statusCodeConverter.toDB(SRM_FAILURE)
			+ ", sp.explanation=? " + "WHERE sp.request_PutID=" + auxTO.primaryKey();
		PreparedStatement signal = null;
		try {
			signal = con.prepareStatement(signalSQL);
			printWarnings(con.getWarnings());
			/* NB: Prepared statement spares DB-specific String notation! */
			signal.setString(1, "This chunk of the request is malformed!");
			printWarnings(signal.getWarnings());

			log.trace("PtP CHUNK DAO - signalMalformedPtPChunk method: {}", signal);
			signal.executeUpdate();
			printWarnings(signal.getWarnings());
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
	 * PtPChunkCatalog in the isSRM_SPACE_AVAILABLE method invocation. In case of
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
			printWarnings(con.getWarnings());

			/* Prepared statement spares DB-specific String notation! */
			stmt.setInt(1, surlUniqueID);
			printWarnings(stmt.getWarnings());

			stmt.setInt(2,statusCodeConverter.toDB(SRM_SPACE_AVAILABLE));
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - numberInSRM_SPACE_AVAILABLE method: {}", stmt);
			rs = stmt.executeQuery();
			printWarnings(stmt.getWarnings());

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
	 * Method that retrieves all expired requests in SRM_SPACE_AVAILABLE state.
	 * 
	 * @return a Map containing the ID of the request as key and the relative
	 * SURL as value
	 */
	public synchronized Map<Long,String> getExpiredSRM_SPACE_AVAILABLE() {

		Map<Long,String> ids = Maps.newHashMap();

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: getExpiredSRM_SPACE_AVAILABLE - unable to get a valid connection!");
			return ids;
		}

		String idsstr = "SELECT rp.ID, rp.targetSURL FROM "
			+ "status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
			+ "WHERE sp.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = con.prepareStatement(idsstr);
			printWarnings(con.getWarnings());

			stmt.setInt(1, statusCodeConverter.toDB(SRM_SPACE_AVAILABLE));
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - getExpiredSRM_SPACE_AVAILABLE: {}", stmt);

			rs = stmt.executeQuery();
			printWarnings(stmt.getWarnings());

			while (rs.next()) {
			    ids.put(rs.getLong("rp.ID"), rs.getString("rp.targetSURL"));
			}
		} catch (SQLException e) {
			log.error("PtPChunkDAO! Unable to select expired "
				+ "SRM_SPACE_AVAILABLE chunks of PtP requests. {}", e.getMessage(), e);

		} finally {
			close(rs);
			close(stmt);
		}
		return ids;
	}

    /**
     * Method that retrieves all ptp requests in SRM_REQUEST_INPROGRESS state which can be
     * considered as expired.
     *
     * @return a Map containing the ID of the request as key and the involved array of SURLs as
     *         value
     */
    public synchronized List<Long> getExpiredSRM_REQUEST_INPROGRESS(long expirationTime) {

        List<Long> ids = Lists.newArrayList();

        if (!checkConnection()) {
            log.error(
                    "PtP CHUNK DAO: getExpiredSRM_REQUEST_INPROGRESS - unable to get a valid connection!");
            return ids;
        }

        String query = "SELECT rq.ID FROM request_queue rq, request_Put rp, status_Put sp "
                + "WHERE rq.ID = rp.request_queueID and rp.ID = sp.request_PutID "
                + "AND rq.status=? AND rq.timeStamp <= DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL ? SECOND)";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.prepareStatement(query);
            printWarnings(con.getWarnings());

            stmt.setLong(1, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
            printWarnings(stmt.getWarnings());

            stmt.setLong(2, expirationTime);
            printWarnings(stmt.getWarnings());

            log.trace("PtP CHUNK DAO - getExpiredSRM_REQUEST_INPROGRESS: {}", stmt);

            rs = stmt.executeQuery();
            printWarnings(stmt.getWarnings());

            while (rs.next()) {
                ids.add(rs.getLong("rq.ID"));
            }
        } catch (SQLException e) {
            log.error(
                    "PtPChunkDAO! Unable to select expired "
                            + "SRM_REQUEST_INPROGRESS chunks of PtP requests. {}",
                    e.getMessage(), e);

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
	 * nothing happens and no exception is thrown, but proper messages get
	 * logged.
	 */
	public synchronized void transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS(List<Long> ids) {

		if (!checkConnection()) {
			log
				.error("PtP CHUNK DAO: transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS - unable to get a valid connection!");
			return;
		}

		String str = "UPDATE "
			+ "status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
			+ "SET sp.statusCode=? " + "WHERE sp.statusCode=? AND rp.ID IN ("
			+ StringUtils.join(ids.toArray(), ',') + ")";

		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			printWarnings(con.getWarnings());

			stmt.setInt(1,
				statusCodeConverter.toDB(SRM_SUCCESS));
			printWarnings(stmt.getWarnings());

			stmt.setInt(2, statusCodeConverter.toDB(SRM_SPACE_AVAILABLE));
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - "
				+ "transitSRM_SPACE_AVAILABLEtoSRM_SUCCESS: {}", stmt);

			int count = stmt.executeUpdate();
			printWarnings(stmt.getWarnings());

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
     * Method that updates chunks in SRM_SPACE_AVAILABLE state, into
     * SRM_FILE_LIFETIME_EXPIRED. An array of Long representing the primary key
     * of each chunk is required. This is needed when the client forgets to invoke
     * srmPutDone(). In case of any error or exception, the returned int value 
     * will be zero or less than the input List size.
     * 
     * @param the list of the request id to update
     * 
     * @return The number of the updated records into the db
     */
    public synchronized int transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(Collection<Long> ids) {

        Preconditions.checkNotNull(ids, "Invalid list of id");
        
        if (!checkConnection()) {
            log.error("Unable to get a valid connection to the database!");
            return 0;
        }

        String querySQL = "UPDATE status_Put sp "
            + "JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
            + "SET sp.statusCode=?, sp.explanation=? "
            + "WHERE sp.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";
        
        
        if (!ids.isEmpty()) {
            querySQL += "AND rp.ID IN (" + StringUtils.join(ids.toArray(), ',') + ")";
        }

        PreparedStatement stmt = null;
        int count = 0;
        try {
            stmt = con.prepareStatement(querySQL);
            printWarnings(con.getWarnings());

            stmt.setInt(1, statusCodeConverter.toDB(SRM_FILE_LIFETIME_EXPIRED));
            printWarnings(stmt.getWarnings());
            
            stmt.setString(2, "Expired pinLifetime");
            printWarnings(stmt.getWarnings());

            stmt.setInt(3, statusCodeConverter.toDB(SRM_SPACE_AVAILABLE));
            printWarnings(stmt.getWarnings());

            log.trace(
                "PtP CHUNK DAO - transit SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED: {}",
                stmt);

            count = stmt.executeUpdate();
            printWarnings(stmt.getWarnings());

        } catch (SQLException e) {
            log.error(
                "PtPChunkDAO! Unable to transit chunks from "
                    + "SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED! {}",
                e.getMessage(), e);
        } finally {
            close(stmt);
        }
        log.trace("PtPChunkDAO! {} chunks of PtP requests were transited "
          + "from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED.", count);
        return count;
    }

    /**
     * Method that updates enqueued requests selected by id into SRM_FAILURE.
     * An array of Long representing the id of each request is required.
     *
     * @param the list of the request id to update
     *
     * @return The number of the updated records. Zero or less than the input list size in case of errors.
     */
    public synchronized int transitExpiredSRM_REQUEST_INPROGRESStoSRM_FAILURE(Collection<Long> ids) {

        Preconditions.checkNotNull(ids, "Invalid list of id");

        if (ids.isEmpty()) {
            return 0;
        }

        if (!checkConnection()) {
            log.error("Unable to get a valid connection to the database!");
            return 0;
        }

        String querySQL = "UPDATE request_queue rq, request_Put rp, status_Put sp "
            + "SET rq.status=?, sp.statusCode=?, sp.explanation=? "
            + "WHERE rq.ID = rp.request_queueID and rp.ID = sp.request_PutID "
            + "AND rq.status=? AND rq.ID IN (" + buildInClauseForArray(ids.size()) + ")";

        PreparedStatement stmt = null;
        int count = 0;
        try {
            stmt = con.prepareStatement(querySQL);
            printWarnings(con.getWarnings());

            stmt.setInt(1, statusCodeConverter.toDB(SRM_FAILURE));
            printWarnings(stmt.getWarnings());

            stmt.setInt(2, statusCodeConverter.toDB(SRM_FAILURE));
            printWarnings(stmt.getWarnings());

            stmt.setString(3, "Request expired");
            printWarnings(stmt.getWarnings());

            stmt.setInt(4, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
            printWarnings(stmt.getWarnings());

            int i = 5;
            for (Long id: ids) {
              stmt.setLong(i, id);
              printWarnings(stmt.getWarnings());
              i++;
            }

            log.trace(
                "PtP CHUNK DAO - transit SRM_REQUEST_INPROGRESS to SRM_FAILURE: {}",
                stmt);

            count = stmt.executeUpdate();
            printWarnings(stmt.getWarnings());

        } catch (SQLException e) {
            log.error(
                "PtPChunkDAO! Unable to transit chunks from "
                    + "SRM_REQUEST_INPROGRESS to SRM_FAILURE! {}",
                e.getMessage(), e);
        } finally {
            close(stmt);
        }
        log.trace("PtPChunkDAO! {} chunks of PtP requests were transited "
          + "from SRM_REQUEST_INPROGRESS to SRM_FAILURE.", count);
        return count;

    }

	/**
	 * Method that transit chunks in SRM_SPACE_AVAILABLE to SRM_ABORTED, for the
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
			printWarnings(con.getWarnings());

			stmt.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
			printWarnings(stmt.getWarnings());

			stmt.setString(2, explanation);
			printWarnings(stmt.getWarnings());

			stmt.setInt(3, statusCodeConverter.toDB(SRM_SPACE_AVAILABLE));
			printWarnings(stmt.getWarnings());

			stmt.setInt(4, surlUniqueID);
			printWarnings(stmt.getWarnings());

			stmt.setString(5, surl);
			printWarnings(stmt.getWarnings());

			log.trace("PtP CHUNK DAO - "
				+ "transitSRM_SPACE_AVAILABLEtoSRM_ABORTED: {}", stmt);
			int count = stmt.executeUpdate();
			printWarnings(stmt.getWarnings());

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
	 * Auxiliary method that sets up the connection to the DB.
	 */
	private boolean setUpConnection() {

		boolean response = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			printWarnings(con.getWarnings());
			response = con.isValid(0);
		} catch (ClassNotFoundException | SQLException e) {
			log.error("PTP CHUNK DAO! Exception in setUpConnection! {}", e.getMessage(), e);
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

	public synchronized int updateStatus(int[] surlsUniqueIDs, String[] surls,
		TStatusCode statusCode, String explanation) {

		if (explanation == null) {
			throw new IllegalArgumentException("Unable to perform the updateStatus, "
				+ "invalid arguments: explanation=" + explanation);
		}
		return doUpdateStatus(null, surlsUniqueIDs, surls, statusCode, explanation, false,
			true);
	}

	public synchronized int updateStatus(TRequestToken requestToken,
		int[] surlsUniqueIDs, String[] surls, TStatusCode statusCode,
		String explanation) {

		if (requestToken == null || requestToken.getValue().trim().isEmpty()
			|| explanation == null) {
			throw new IllegalArgumentException("Unable to perform the updateStatus, "
				+ "invalid arguments: requestToken=" + requestToken + " explanation="
				+ explanation);
		}
		return doUpdateStatus(requestToken, surlsUniqueIDs, surls, statusCode,
			explanation, true, true);
	}

	private int doUpdateStatus(TRequestToken requestToken, int[] surlsUniqueIDs,
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
			return 0;
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
		int count = 0;
		try {
			stmt = con.prepareStatement(str);
			printWarnings(con.getWarnings());
			stmt.setInt(1, statusCodeConverter.toDB(statusCode));
			printWarnings(stmt.getWarnings());

			log.trace("PTP CHUNK DAO - updateStatus: {}", stmt);
			count = stmt.executeUpdate();
			printWarnings(stmt.getWarnings());
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
		return count;
	}

	public synchronized int updateStatusOnMatchingStatus(
		TRequestToken requestToken, TStatusCode expectedStatusCode,
		TStatusCode newStatusCode, String explanation) {

		if (requestToken == null || requestToken.getValue().trim().isEmpty()
			|| explanation == null) {
			throw new IllegalArgumentException(
				"Unable to perform the updateStatusOnMatchingStatus, "
					+ "invalid arguments: requestToken=" + requestToken + " explanation="
					+ explanation);
		}
		return doUpdateStatusOnMatchingStatus(requestToken, null, null,
			expectedStatusCode, newStatusCode, explanation, true, false, true);
	}

	public synchronized int updateStatusOnMatchingStatus(int[] surlsUniqueIDs,
		String[] surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode,
		String explanation) {

		if (surlsUniqueIDs == null || surls == null || explanation == null
			|| surlsUniqueIDs.length == 0 || surls.length == 0
			|| surlsUniqueIDs.length != surls.length) {
			throw new IllegalArgumentException(
				"Unable to perform the updateStatusOnMatchingStatus, "
					+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs + " surls="
					+ surls + " explanation=" + explanation);
		}
		return doUpdateStatusOnMatchingStatus(null, surlsUniqueIDs, surls,
			expectedStatusCode, newStatusCode, explanation, false, true, true);
	}

	public synchronized int updateStatusOnMatchingStatus(
		TRequestToken requestToken, int[] surlsUniqueIDs, String[] surls,
		TStatusCode expectedStatusCode, TStatusCode newStatusCode) {

		if (requestToken == null || requestToken.getValue().trim().isEmpty()
			|| surlsUniqueIDs == null || surls == null || surlsUniqueIDs.length == 0
			|| surls.length == 0 || surlsUniqueIDs.length != surls.length) {
			throw new IllegalArgumentException(
				"Unable to perform the updateStatusOnMatchingStatus, "
					+ "invalid arguments: requestToken=" + requestToken
					+ "surlsUniqueIDs=" + surlsUniqueIDs + " surls=" + surls);
		}
		return doUpdateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls,
			expectedStatusCode, newStatusCode, null, true, true, false);
	}

	private int doUpdateStatusOnMatchingStatus(TRequestToken requestToken,
		int[] surlsUniqueIDs, String[] surls, TStatusCode expectedStatusCode,
		TStatusCode newStatusCode, String explanation, boolean withRequestToken,
		boolean withSurls, boolean withExplanation) {

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
			return 0;
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

		int count = 0;
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(str);
			printWarnings(con.getWarnings());
			stmt.setInt(1, statusCodeConverter.toDB(newStatusCode));
			printWarnings(stmt.getWarnings());

			stmt.setInt(2, statusCodeConverter.toDB(expectedStatusCode));
			printWarnings(stmt.getWarnings());

			log.trace("PTP CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt);
			count = stmt.executeUpdate();
			printWarnings(stmt.getWarnings());
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
		return count;
	}

	public Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray, String dn) {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0 || dn == null) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray + " dn=" + dn);
		}
		return find(surlsUniqueIDs, surlsArray, dn, true);
	}

	public Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray) {

		if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0
			|| surlsArray == null || surlsArray.length == 0) {
			throw new IllegalArgumentException("Unable to perform the find, "
				+ "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
				+ " surlsArray=" + surlsArray);
		}
		return find(surlsUniqueIDs, surlsArray, null, false);
	}
	
	
	private List<PtPChunkDataTO> chunkTOfromResultSet(ResultSet rs) 
	  throws SQLException{

	List<PtPChunkDataTO> results = Lists.newArrayList();
	  while (rs.next()) {
	    
	    PtPChunkDataTO chunkDataTO = new PtPChunkDataTO();
      
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
       * separated by the "#" char. The proxy is a BLOB, hence it has to be
       * properly converted in string.
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
        chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
      }

      chunkDataTO.setExpectedFileSize(rs.getLong("rp.expectedFileSize"));       
      chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
      chunkDataTO.setStatus(rs.getInt("sp.statusCode"));
      results.add(chunkDataTO);
    }
	  
	  return results;
	}
	
	
	
	public synchronized List<PtPChunkDataTO> findActivePtPsOnSURLs(List<String> surls){
	
	  if (surls == null || surls.isEmpty()){
	    throw new IllegalArgumentException("cannot find active active "
        + "PtPs for an empty or null list of SURLs!");
	  }
	  
	  ResultSet rs = null;
    PreparedStatement stat = null;
   
    try {
        String query = "SELECT rq.ID, rq.r_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, "
        + "rq.s_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, "
        + "sp.statusCode "
        + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
        + "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
        + "WHERE ( rp.targetSURL in "+ makeSurlString((String[])surls.toArray()) +" )"
        + "AND sp.statusCode = 24";
      
        stat = con.prepareStatement(query);
        printWarnings(con.getWarnings());
        
        rs = stat.executeQuery();
        List<PtPChunkDataTO> results = chunkTOfromResultSet(rs);

        return results;
    
    } catch (SQLException e) {

      log.error("findActivePtPsOnSURLs(): SQL Error: {}", e.getMessage(),e);
      return Collections.emptyList();
      
    } finally {
      close(rs);
      close(stat);
    }
	}


  public synchronized List<PtPChunkDataTO> findActivePtPsOnSURL(String surl) {
    return findActivePtPsOnSURL(surl, null);
  }

  public synchronized List<PtPChunkDataTO> findActivePtPsOnSURL(String surl,
    String currentRequestToken) {

    if (surl == null || surl.isEmpty()) {
      throw new IllegalArgumentException("cannot find active active "
        + "PtPs for an empty or null SURL!");
    }
    
    ResultSet rs = null;
    PreparedStatement stat = null;
    
    try {

      String query = "SELECT rq.ID, rq.r_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, "
        + "rq.s_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, "
        + "sp.statusCode "
        + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
        + "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
        + "WHERE ( rp.targetSURL = ? and sp.statusCode=24 )";
      
      if (currentRequestToken != null){
        query += "AND rq.r_token != ?";
      }

      stat = con.prepareStatement(query);
      printWarnings(con.getWarnings());

      stat.setString(1, surl);
      
      if (currentRequestToken != null){
        stat.setString(2, currentRequestToken);
      }
      
      rs = stat.executeQuery();
      List<PtPChunkDataTO> results = chunkTOfromResultSet(rs);

      return results;
      
    } catch (SQLException e) {

      log.error("findActivePtPsOnSURL(): SQL Error: {}", e.getMessage(),e);
      return Collections.emptyList();
      
    } finally {
      close(rs);
      close(stat);
    }

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
			return Lists.newArrayList();
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
			printWarnings(con.getWarnings());

			List<PtPChunkDataTO> list = Lists.newArrayList();

			log.trace("PtP CHUNK DAO - find method: {}", find);
			rs = find.executeQuery();
			printWarnings(find.getWarnings());
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
				 * separated by the "#" char. The proxy is a BLOB, hence it has to be
				 * properly converted in string.
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
					chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
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
			return Lists.newArrayList();
		} finally {
			close(rs);
			close(find);
		}
	}

	public synchronized List<String> findProtocols(long requestQueueId) {

		if (!checkConnection()) {
			log.error("PtP CHUNK DAO: find - unable to get a valid connection!");
			return Lists.newArrayList();
		}
		String str = null;
		PreparedStatement find = null;
		ResultSet rs = null;
		try {
			str = "SELECT tp.config_ProtocolsID "
				+ "FROM request_TransferProtocols tp " + "WHERE tp.request_queueID=?";

			find = con.prepareStatement(str);
			printWarnings(con.getWarnings());

			List<String> protocols = Lists.newArrayList();
			find.setLong(1, requestQueueId);
			printWarnings(find.getWarnings());

			log.trace("PtP CHUNK DAO - findProtocols method: {}", find);
			rs = find.executeQuery();
			printWarnings(find.getWarnings());

			while (rs.next()) {
				protocols.add(rs.getString("tp.config_ProtocolsID"));
			}

			return protocols;
		} catch (SQLException e) {
			log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
			/* return empty Collection! */
			return Lists.newArrayList();
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
			
			SURL requestedSURL;
			
			try {
				requestedSURL = SURL.makeSURLfromString(surls[i]);
			} catch (NamespaceException e) {
				log.error(e.getMessage(), e);
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