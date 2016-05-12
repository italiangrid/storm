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
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
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
 * DAO class for RequestSummaryCatalog. This DAO is specifically designed to
 * connect to a MySQL DB.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date May 2005
 */
public class RequestSummaryDAO {

	private static final Logger log = LoggerFactory
		.getLogger(RequestSummaryDAO.class);

	/** String with the name of the class for the DB driver */
	private final String driver = Configuration.getInstance().getDBDriver();
	/** String referring to the URL of the DB */
	private final String url = Configuration.getInstance().getDBURL();
	/** String with the password for the DB */
	private final String password = Configuration.getInstance().getDBPassword();
	/** String with the name for the DB */
	private final String name = Configuration.getInstance().getDBUserName();
	/** maximum number of requests that will be retrieved */
	private int limit;
	/** Connection to DB - WARNING!!! It is kept open all the time! */
	private Connection con = null;

	/** milliseconds that must pass before reconnecting to DB */
	private final long period = Configuration.getInstance()
		.getDBReconnectPeriod() * 1000;
	/** initial delay in milliseconds before starting timer */
	private final long delay = Configuration.getInstance().getDBReconnectDelay() * 1000;
	/** timer thread that will run a task to alert when reconnecting is necessary! */
	private Timer clock = null;
	/**
	 * timer task that will update the boolean signaling that a reconnection is
	 * needed!
	 */
	private TimerTask clockTask = null;
	/** boolean that tells whether reconnection is needed because of MySQL bug! */
	private boolean reconnect = false;

	private static final RequestSummaryDAO dao = new RequestSummaryDAO();

	private RequestSummaryDAO() {

		int aux = Configuration.getInstance().getPickingMaxBatchSize();
		if (aux > 1) {
			limit = aux;
		} else {
			limit = 1;
		}
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
	 * Method that returns the only instance of the RequestSummaryDAO.
	 */
	public static RequestSummaryDAO getInstance() {

		return dao;
	}

	/**
	 * Method that retrieves requests in the SRM_REQUEST_QUEUED status: retrieved
	 * requests are limited to the number specified by the Configuration method
	 * getPicker2MaxBatchSize. All retrieved requests get their global status
	 * transited to SRM_REQUEST_INPROGRESS. A Collection of RequestSummaryDataTO
	 * is returned: if none are found, an empty collection is returned.
	 */
	public Collection<RequestSummaryDataTO> findNew(int freeSlot) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<RequestSummaryDataTO> list = new ArrayList<RequestSummaryDataTO>(); // ArrayList
																																							// containing
																																							// all
																																							// retrieved
		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - findNew: unable to get a valid connection!");
			return list;
		}
		// RequestSummaryDataTO
		try {
			// start transaction
			con.setAutoCommit(false);

			int howMuch = -1;
			if (freeSlot > limit) {
				howMuch = limit;
			} else {
				howMuch = freeSlot;
			}

			String query = "SELECT ID, config_RequestTypeID, r_token, timeStamp, "
				+ "client_dn, proxy FROM request_queue WHERE status=? LIMIT ?";

			// get id, request type, request token and client_DN of newly added
			// requests, which must be in SRM_REQUEST_QUEUED state
			stmt = con.prepareStatement(query);
			logWarnings(con.getWarnings());

			stmt.setInt(1,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED));
			stmt.setInt(2, howMuch);

			rs = stmt.executeQuery();
			logWarnings(stmt.getWarnings());

			List<Long> rowids = new ArrayList<Long>(); // arraylist with selected ids
			RequestSummaryDataTO aux = null; // RequestSummaryDataTO made from
			// retrieved row
			long auxid; // primary key of retrieved row
			while (rs.next()) {
				auxid = rs.getLong("ID");
				rowids.add(new Long(auxid));
				aux = new RequestSummaryDataTO();
				aux.setPrimaryKey(auxid);
				aux.setRequestType(rs.getString("config_RequestTypeID"));
				aux.setRequestToken(rs.getString("r_token"));
				aux.setClientDN(rs.getString("client_dn"));
				aux.setTimestamp(rs.getTimestamp("timeStamp"));

				/**
				 * This code is only for the 1.3.18. This is a workaround to get FQANs
				 * using the proxy field on request_queue. The FE use the proxy field of
				 * request_queue to insert a single FQAN string containing all FQAN
				 * separeted by the "#" char. The proxy is a BLOB, hence it has to be
				 * properly conveted in string.
				 */
				java.sql.Blob blob = rs.getBlob("proxy");
				if (blob != null) {
					byte[] bdata = blob.getBytes(1, (int) blob.length());
					aux.setVomsAttributes(new String(bdata));
				}

				list.add(aux);
			}
			close(rs);
			close(stmt);

			// transit state from SRM_REQUEST_QUEUED to SRM_REQUEST_INPROGRESS
			if (!list.isEmpty()) {
				logWarnings(con.getWarnings());
				String where = makeWhereString(rowids);
				String update = "UPDATE request_queue SET status="
					+ StatusCodeConverter.getInstance().toDB(
						TStatusCode.SRM_REQUEST_INPROGRESS) + ", errstring=?"
					+ " WHERE ID IN " + where;
				stmt = con.prepareStatement(update);
				logWarnings(stmt.getWarnings());
				stmt.setString(1, "Request handled!");
				logWarnings(stmt.getWarnings());
				log.trace("REQUEST SUMMARY DAO - findNew: executing {}", stmt.toString());
				stmt.executeUpdate();
				close(stmt);
			}

			// commit and finish transaction
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - findNew: Unable to complete picking. "
				+ "Error: {}. Rolling back!", e.getMessage(), e);
		} finally {
			close(rs);
			close(stmt);
		}
		// return collection of requests
		if (!list.isEmpty()) {
			log.debug("REQUEST SUMMARY DAO - findNew: returning {}", list);
		}
		return list;
	}

	/**
	 * Method used to signal in the DB that a request failed: the status of the
	 * request identified by the primary key index is transited to SRM_FAILURE,
	 * with the supplied explanation String. The supplied index is the primary key
	 * of the global request. In case of any error, nothing gets done and no
	 * exception is thrown, but proper error messagges get logged.
	 */
	public void failRequest(long index, String explanation) {

		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - failRequest: unable to get a valid connection!");
			return;
		}
		String signalSQL = "UPDATE request_queue r " + "SET r.status="
			+ StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_FAILURE)
			+ ", r.errstring=? " + "WHERE r.ID=?";
		PreparedStatement signal = null;
		try {
			signal = con.prepareStatement(signalSQL);
			logWarnings(con.getWarnings());
			signal.setString(1, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(signal.getWarnings());
			signal.setLong(2, index);
			logWarnings(signal.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failRequest executing: {}", signal);
			signal.executeUpdate();
			logWarnings(signal.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO! Unable to transit request identified by "
				+ "ID {} to SRM_FAILURE! Error: {}", index, e.getMessage(), e);
		} finally {
			close(signal);
		}
	}

	/**
	 * Method used to signal in the DB that a PtGRequest failed. The global status
	 * transits to SRM_FAILURE, as well as that of each chunk associated to the
	 * request. The supplied explanation string is used both for the global status
	 * as well as for each individual chunk. The supplied index is the primary key
	 * of the global request. In case of any error, nothing gets done and no
	 * exception is thrown, but proper error messagges get logged.
	 */
	public void failPtGRequest(long index, String explanation) {

		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - failPtGRequest: unable to get a valid connection!");
			return;
		}
		String requestSQL = "UPDATE request_queue r "
			+ "SET r.status=?, r.errstring=? " + "WHERE r.ID=?";
		String chunkSQL = "UPDATE "
			+ "status_Get s JOIN (request_queue r, request_Get g) ON s.request_GetID=g.ID AND g.request_queueID=r.ID "
			+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=?";
		PreparedStatement request = null;
		PreparedStatement chunk = null;
		int failCode = StatusCodeConverter.getInstance().toDB(
			TStatusCode.SRM_FAILURE);
		try {
			// start transaction
			con.setAutoCommit(false);

			// update global status
			request = con.prepareStatement(requestSQL);
			logWarnings(con.getWarnings());
			request.setInt(1, failCode);
			logWarnings(request.getWarnings());
			request.setString(2, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(request.getWarnings());
			request.setLong(3, index);
			logWarnings(request.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failPtGRequest executing: {}", request);
			request.executeUpdate();
			logWarnings(request.getWarnings());

			// update each chunk status
			chunk = con.prepareStatement(chunkSQL);
			logWarnings(con.getWarnings());
			chunk.setInt(1, failCode);
			logWarnings(chunk.getWarnings());
			chunk.setString(2, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(chunk.getWarnings());
			chunk.setLong(3, index);
			logWarnings(chunk.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failPtGRequest executing: {}", chunk);
			chunk.executeUpdate();
			logWarnings(chunk.getWarnings());

			// commit and finish transaction
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO! Unable to transit PtG request identified "
				+ "by ID {} to SRM_FAILURE! Error: {}\nRolling back...", index, 
				e.getMessage(), e);
			rollback(con);
		} finally {
			close(request);
			close(chunk);
		}
	}

	/**
	 * Method used to signal in the DB that a PtPRequest failed. The global status
	 * transits to SRM_FAILURE, as well as that of each chunk associated to the
	 * request. The supplied explanation string is used both for the global status
	 * as well as for each individual chunk. The supplied index is the primary key
	 * of the global request. In case of any error, nothing gets done and no
	 * exception is thrown, but proper error messagges get logged.
	 */
	public void failPtPRequest(long index, String explanation) {

		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - failPtPRequest: unable to get a valid connection!");
			return;
		}
		String requestSQL = "UPDATE request_queue r "
			+ "SET r.status=?, r.errstring=? " + "WHERE r.ID=?";
		String chunkSQL = "UPDATE "
			+ "status_Put s JOIN (request_queue r, request_Put p) ON s.request_PutID=p.ID AND p.request_queueID=r.ID "
			+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=?";
		PreparedStatement request = null;
		PreparedStatement chunk = null;
		int failCode = StatusCodeConverter.getInstance().toDB(
			TStatusCode.SRM_FAILURE);
		try {
			// start transaction
			con.setAutoCommit(false);

			// update global status
			request = con.prepareStatement(requestSQL);
			logWarnings(con.getWarnings());
			request.setInt(1, failCode);
			logWarnings(request.getWarnings());
			request.setString(2, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(request.getWarnings());
			request.setLong(3, index);
			logWarnings(request.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failPtPRequest executing: {}", request);
			request.executeUpdate();
			logWarnings(request.getWarnings());

			// update each chunk status
			chunk = con.prepareStatement(chunkSQL);
			logWarnings(con.getWarnings());
			chunk.setInt(1, failCode);
			logWarnings(chunk.getWarnings());
			chunk.setString(2, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(chunk.getWarnings());
			chunk.setLong(3, index);
			logWarnings(chunk.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failPtPRequest executing: {}", chunk);
			chunk.executeUpdate();
			logWarnings(chunk.getWarnings());

			// commit and finish transaction
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO! Unable to transit PtP request identified "
				+ "by ID {} to SRM_FAILURE! Error: {}\nRolling back...", index, 
				e.getMessage(), e);
			rollback(con);
		} finally {
			close(request);
			close(chunk);
		}
	}

	/**
	 * Method used to signal in the DB that a CopyRequest failed. The global
	 * status transits to SRM_FAILURE, as well as that of each chunk associated to
	 * the request. The supplied explanation string is used both for the global
	 * status as well as for each individual chunk. The supplied index is the
	 * primary key of the global request. In case of any error, nothing gets done
	 * and no exception is thrown, but proper error messagges get logged.
	 */
	public void failCopyRequest(long index, String explanation) {

		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - failCopyRequest: unable to get a valid connection!");
			return;
		}
		String requestSQL = "UPDATE request_queue r "
			+ "SET r.status=?, r.errstring=? " + "WHERE r.ID=?";
		String chunkSQL = "UPDATE "
			+ "status_Copy s JOIN (request_queue r, request_Copy c) ON s.request_CopyID=c.ID AND c.request_queueID=r.ID "
			+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=?";
		PreparedStatement request = null;
		PreparedStatement chunk = null;
		int failCode = StatusCodeConverter.getInstance().toDB(
			TStatusCode.SRM_FAILURE);
		try {
			// start transaction
			con.setAutoCommit(false);

			// update global status
			request = con.prepareStatement(requestSQL);
			logWarnings(con.getWarnings());
			request.setInt(1, failCode);
			logWarnings(request.getWarnings());
			request.setString(2, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(request.getWarnings());
			request.setLong(3, index);
			logWarnings(request.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failCopyRequest executing: {}", request);
			request.executeUpdate();
			logWarnings(request.getWarnings());

			// update each chunk status
			chunk = con.prepareStatement(chunkSQL);
			logWarnings(con.getWarnings());
			chunk.setInt(1, failCode);
			logWarnings(chunk.getWarnings());
			chunk.setString(2, explanation); // Prepared statement spares
			// DB-specific String notation!
			logWarnings(chunk.getWarnings());
			chunk.setLong(3, index);
			logWarnings(chunk.getWarnings());
			log.trace("REQUEST SUMMARY DAO! failCopyRequest executing: {}", chunk);
			chunk.executeUpdate();
			logWarnings(chunk.getWarnings());

			// commit and finish transaction
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO! Unable to transit Copy request identified "
				+ "by ID {} to SRM_FAILURE! Error: {}\nRolling back...", index, 
				e.getMessage(), e);
			rollback(con);
		} finally {
			close(request);
			close(chunk);
		}
	}

	/**
	 * Method used to update the global status of the request identified by the
	 * RequestToken rt. It gets updated the supplied status, with the supplied
	 * explanation String. If the supplied request token does not exist, nothing
	 * happens.
	 */
	public void updateGlobalStatus(String rt, int status, String explanation) {

		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - updateGlobalStatus: unable to get a valid connection!");
			return;
		}
		PreparedStatement update = null;
		try {
			update = con
				.prepareStatement("UPDATE request_queue SET status=?, errstring=? WHERE r_token=?");
			logWarnings(con.getWarnings());
			update.setInt(1, status);
			logWarnings(update.getWarnings());
			update.setString(2, explanation);
			logWarnings(update.getWarnings());
			update.setString(3, rt);
			logWarnings(update.getWarnings());
			log.trace("REQUEST SUMMARY DAO - updateGlobalStatus: executing {}", update);
			update.executeUpdate();
			logWarnings(update.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO: {}", e.getMessage(), e);
		} finally {
			close(update);
		}
	}

	public void updateGlobalStatusOnMatchingGlobalStatus(
		TRequestToken requestToken, TStatusCode expectedStatusCode,
		TStatusCode newStatusCode, String explanation) {

		if (!checkConnection()) {
			log
				.error("REQUEST SUMMARY DAO - updateGlobalStatusOnMatchingGlobalStatus: "
					+ "unable to get a valid connection!");
			return;
		}
		PreparedStatement update = null;
		try {
			update = con
				.prepareStatement("UPDATE request_queue SET status=?, errstring=? WHERE r_token=? AND status=?");
			logWarnings(con.getWarnings());
			update.setInt(1, StatusCodeConverter.getInstance().toDB(newStatusCode));
			logWarnings(update.getWarnings());
			update.setString(2, explanation);
			logWarnings(update.getWarnings());
			update.setString(3, requestToken.toString());
			logWarnings(update.getWarnings());
			update.setInt(4,
				StatusCodeConverter.getInstance().toDB(expectedStatusCode));
			logWarnings(update.getWarnings());
			log.trace("REQUEST SUMMARY DAO - updateGlobalStatusOnMatchingGlobalStatus: "
				+ "executing {}", update);
			update.executeUpdate();
			logWarnings(update.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO: {}", e.getMessage(), e);
		} finally {
			close(update);
		}
	}

	/**
	 * Method used to update the global status of the request identified by the
	 * RequestToken rt. It gets updated the supplied status, with the supplied
	 * explanation String and pin and file lifetimes are updated in order to start
	 * the countdown from now. If the supplied request token does not exist,
	 * nothing happens.
	 */
	public void updateGlobalStatusPinFileLifetime(String rt, int status,
		String explanation) {

		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - updateGlobalStatusPinFileLifetime: "
				+ "unable to get a valid connection!");
			return;
		}
		PreparedStatement update = null;

		String query = "UPDATE request_queue SET status=?, errstring=?, "
			+ "pinLifetime=pinLifetime+(UNIX_TIMESTAMP()-UNIX_TIMESTAMP(timeStamp)) "
			+ "WHERE r_token=?";

		try {
			update = con.prepareStatement(query);
			logWarnings(con.getWarnings());

			update.setInt(1, status);
			logWarnings(update.getWarnings());

			update.setString(2, explanation);
			logWarnings(update.getWarnings());

			update.setString(3, rt);
			logWarnings(update.getWarnings());

			log.trace("REQUEST SUMMARY DAO - updateGlobalStatus: executing {}", update);

			update.executeUpdate();
			logWarnings(update.getWarnings());

		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO: {}", e.getMessage(), e);
		} finally {
			close(update);
		}
	}

	/**
	 * Method used to transit the status of a request that is in
	 * SRM_REQUEST_QUEUED state, to SRM_ABORTED. All files associated with the
	 * request will also get their status changed to SRM_ABORTED. If the supplied
	 * token is null, or not found, or not in the SRM_REQUEST_QUEUED state, then
	 * nothing happens.
	 */
	public void abortRequest(String rt) {

		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - abortRequest: unable to get a valid connection!");
			return;
		}
		PreparedStatement update = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			query = con
				.prepareStatement("SELECT ID,config_RequestTypeID FROM request_queue WHERE r_token=? AND status=?");
			logWarnings(con.getWarnings());
			query.setString(1, rt);
			logWarnings(query.getWarnings());
			query.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED));
			logWarnings(query.getWarnings());
			log.trace("REQUEST SUMMARY DAO - abortRequest - {}", query);
			rs = query.executeQuery();
			logWarnings(query.getWarnings());
			if (rs.next()) {
				long id = rs.getLong("ID");
				String type = rs.getString("config_RequestTypeID");
				update = con
					.prepareStatement("UPDATE request_queue SET status=?, errstring=? WHERE ID=?");
				logWarnings(con.getWarnings());
				update.setInt(1,
					StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
				logWarnings(update.getWarnings());
				update.setString(2, "User aborted request!");
				logWarnings(update.getWarnings());
				update.setLong(3, id);
				logWarnings(update.getWarnings());
				log.trace("REQUEST SUMMARY DAO - abortRequest - {}", update);
				update.executeUpdate();
				logWarnings(update.getWarnings());
				close(update);
				// update single chunk file statuses
				TRequestType rtyp = RequestTypeConverter.getInstance().toSTORM(type);
				String status_table = null;
				String request_table = null;
				String join_column = null;
				if (rtyp != TRequestType.EMPTY) {
					if (rtyp == TRequestType.PREPARE_TO_GET) {
						status_table = "status_Get";
						request_table = "request_Get";
						join_column = "request_GetID";
					} else if (rtyp == TRequestType.PREPARE_TO_PUT) {
						request_table = "request_Put";
						status_table = "status_Put";
						join_column = "request_PutID";
					} else if (rtyp == TRequestType.COPY) {
						request_table = "request_Copy";
						status_table = "status_Copy";
						join_column = "request_CopyID";
					} else {
						request_table = "request_BoL";
						status_table = "status_BoL";
						join_column = "request_BoLID";
					}
					String auxstr = "UPDATE " + status_table
						+ " s JOIN (request_queue r, " + request_table + " t) ON (s."
						+ join_column + "=t.ID AND t.request_queueID=r.ID) "
						+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=?";
					update = con.prepareStatement(auxstr);
					logWarnings(con.getWarnings());
					update.setInt(1,
						StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
					logWarnings(update.getWarnings());
					update.setString(2, "User aborted request!");
					logWarnings(update.getWarnings());
					update.setLong(3, id);
					logWarnings(update.getWarnings());
					log.trace("REQUEST SUMMARY DAO - abortRequest - {}", update);
					update.executeUpdate();
					logWarnings(update.getWarnings());
				} else {
					log.error("REQUEST SUMMARY DAO - Unable to complete abortRequest: "
							+ "could not update file statuses because the request type could "
							+ "not be translated from the DB!");
				}
			}
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - abortRequest: {}", e.getMessage(), e);
		} finally {
			close(rs);
			close(query);
			close(update);
		}
	}

	/**
	 * Method used to transit the status of a request that is in
	 * SRM_REQUEST_INPROGRESS state, to SRM_ABORTED. All files associated with the
	 * request will also get their status changed to SRM_ABORTED. If the supplied
	 * token is null, or not found, or not in the SRM_REQUEST_INPROGRESS state,
	 * then nothing happens.
	 */
	public void abortInProgressRequest(String rt) {

		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - abortInProgressRequest: unable to get "
				+ "a valid connection!");
			return;
		}
		PreparedStatement update = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			query = con
				.prepareStatement("SELECT ID,config_RequestTypeID FROM request_queue WHERE r_token=? AND status=?");
			logWarnings(con.getWarnings());
			query.setString(1, rt);
			logWarnings(query.getWarnings());
			query.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_INPROGRESS));
			logWarnings(query.getWarnings());
			log.trace("REQUEST SUMMARY DAO - abortInProgressRequest - {}", query);
			rs = query.executeQuery();
			logWarnings(query.getWarnings());
			if (rs.next()) {
				// token found...
				// get ID
				long id = rs.getLong("ID");
				String type = rs.getString("config_RequestTypeID");
				// update global request status
				update = con
					.prepareStatement("UPDATE request_queue SET status=?, errstring=? WHERE ID=?");
				logWarnings(con.getWarnings());
				update.setInt(1,
					StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
				logWarnings(update.getWarnings());
				update.setString(2, "User aborted request!");
				logWarnings(update.getWarnings());
				update.setLong(3, id);
				logWarnings(update.getWarnings());
				log.trace("REQUEST SUMMARY DAO - abortInProgressRequest - {}", update);
				update.executeUpdate();
				logWarnings(update.getWarnings());
				close(update);
				// update single chunk file statuses
				TRequestType rtyp = RequestTypeConverter.getInstance().toSTORM(type);
				String status_table = null;
				String request_table = null;
				String join_column = null;
				if (rtyp != TRequestType.EMPTY) {
					if (rtyp == TRequestType.PREPARE_TO_GET) {
						request_table = "request_Get";
						status_table = "status_Get";
						join_column = "request_GetID";
					} else if (rtyp == TRequestType.PREPARE_TO_PUT) {
						request_table = "request_Put";
						status_table = "status_Put";
						join_column = "request_PutID";
					} else if (rtyp == TRequestType.COPY) {
						request_table = "request_Copy";
						status_table = "status_Copy";
						join_column = "request_CopyID";
					} else {
						request_table = "request_BoL";
						status_table = "status_BoL";
						join_column = "request_BoLID";
					}
					String auxstr = "UPDATE " + status_table
						+ " s JOIN (request_queue r, " + request_table + " t ON s."
						+ join_column + "=t.ID AND t.request_queueID=r.ID )"
						+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=?";
					update = con.prepareStatement(auxstr);
					logWarnings(con.getWarnings());
					update.setInt(1,
						StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
					logWarnings(update.getWarnings());
					update.setString(2, "User aborted request!");
					logWarnings(update.getWarnings());
					update.setLong(3, id);
					logWarnings(update.getWarnings());
					log.trace("REQUEST SUMMARY DAO - abortInProgressRequest - {}", update);
					update.executeUpdate();
					logWarnings(update.getWarnings());
				} else {
					log.error("REQUEST SUMMARY DAO - Unable to complete "
						+ "abortInProgressRequest: could not update file statuses because "
						+ "the request type could not be translated from the DB!");
				}
			}
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - abortInProgressRequest: {}", 
				e.getMessage(), e);
		} finally {
			close(rs);
			close(query);
			close(update);
		}
	}

	/**
	 * Method used to transit the status of chunks of a request that is in
	 * SRM_REQUEST_QUEUED state, to SRM_ABORTED. If the supplied token is null, or
	 * not found, or not in the SRM_REQUEST_QUEUED state, then nothing happens.
	 */
	public void abortChunksOfRequest(String rt, Collection<String> surls) {

		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - abortChunksOfRequest: unable to get a "
				+ "valid connection!");
			return;
		}
		PreparedStatement update = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			query = con
				.prepareStatement("SELECT ID,config_RequestTypeID FROM request_queue WHERE r_token=? AND status=?");
			logWarnings(con.getWarnings());
			query.setString(1, rt);
			logWarnings(query.getWarnings());
			query.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED));
			logWarnings(query.getWarnings());
			log.trace("REQUEST SUMMARY DAO - abortChunksOfRequest - {}", query);
			rs = query.executeQuery();
			logWarnings(query.getWarnings());
			if (rs.next()) {
				long id = rs.getLong("ID");
				String type = rs.getString("config_RequestTypeID");
				// update single chunk file statuses
				TRequestType rtyp = RequestTypeConverter.getInstance().toSTORM(type);
				String status_table = null;
				String request_table = null;
				String join_column = null;
				String surl_column = null;
				if (rtyp != TRequestType.EMPTY) {
					if (rtyp == TRequestType.PREPARE_TO_GET) {
						request_table = "request_Get";
						status_table = "status_Get";
						join_column = "request_GetID";
						surl_column = "sourceSURL";
					} else if (rtyp == TRequestType.PREPARE_TO_PUT) {
						request_table = "request_Put";
						status_table = "status_Put";
						join_column = "request_PutID";
						surl_column = "targetSURL";
					} else if (rtyp == TRequestType.COPY) {
						request_table = "request_Copy";
						status_table = "status_Copy";
						join_column = "request_CopyID";
						surl_column = "targetSURL";
					} else {
						request_table = "request_BoL";
						status_table = "status_BoL";
						join_column = "request_BoLID";
						surl_column = "sourceSURL";
					}
					String auxstr = "UPDATE " + status_table
						+ " s JOIN (request_queue r, " + request_table + " t ON s."
						+ join_column + "=t.ID AND t.request_queueID=r.ID "
						+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=? AND "
						+ surl_column + " IN " + makeInString(surls);
					update = con.prepareStatement(auxstr);
					logWarnings(con.getWarnings());
					update.setInt(1,
						StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
					logWarnings(update.getWarnings());
					update.setString(2, "User aborted request!");
					logWarnings(update.getWarnings());
					update.setLong(3, id);
					logWarnings(update.getWarnings());
					log.trace("REQUEST SUMMARY DAO - abortChunksOfRequest - {}", update);
					update.executeUpdate();
					logWarnings(update.getWarnings());
				} else {
					log.error("REQUEST SUMMARY DAO - Unable to complete "
						+ "abortChunksOfRequest: could not update file statuses because "
						+ "the request type could not be translated from the DB!");
				}
			}
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - abortChunksOfRequest: {}", 
				e.getMessage(), e);
		} finally {
			close(rs);
			close(query);
			close(update);
		}
	}

	/**
	 * Method used to transit the status of chunks of a request that is in
	 * SRM_REQUEST_INPROGRESS state, to SRM_ABORTED. If the supplied token is
	 * null, or not found, or not in the SRM_REQUEST_INPROGRESS state, then
	 * nothing happens.
	 */
	public void abortChunksOfInProgressRequest(String rt, Collection<String> surls) {

		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest: unable "
				+ "to get a valid connection!");
			return;
		}
		PreparedStatement update = null;
		PreparedStatement query = null;
		ResultSet rs = null;
		try {
			query = con
				.prepareStatement("SELECT ID,config_RequestTypeID FROM request_queue WHERE r_token=? AND status=?");
			logWarnings(con.getWarnings());
			query.setString(1, rt);
			logWarnings(query.getWarnings());
			query.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_INPROGRESS));
			logWarnings(query.getWarnings());
			log.trace("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest - {}", query);
			rs = query.executeQuery();
			logWarnings(query.getWarnings());
			if (rs.next()) {
				long id = rs.getLong("ID");
				String type = rs.getString("config_RequestTypeID");
				// update single chunk file statuses
				TRequestType rtyp = RequestTypeConverter.getInstance().toSTORM(type);
				String status_table = null;
				String request_table = null;
				String join_column = null;
				String surl_column = null;
				if (rtyp != TRequestType.EMPTY) {
					if (rtyp == TRequestType.PREPARE_TO_GET) {
						request_table = "request_Get";
						status_table = "status_Get";
						join_column = "request_GetID";
						surl_column = "sourceSURL";
					} else if (rtyp == TRequestType.PREPARE_TO_PUT) {
						request_table = "request_Put";
						status_table = "status_Put";
						join_column = "request_PutID";
						surl_column = "targetSURL";
					} else if (rtyp == TRequestType.COPY) {
						request_table = "request_Copy";
						status_table = "status_Copy";
						join_column = "request_CopyID";
						surl_column = "targetSURL";
					} else {
						request_table = "request_BoL";
						status_table = "status_BoL";
						join_column = "request_BoLID";
						surl_column = "sourceSURL";
					}
					String auxstr = "UPDATE " + status_table
						+ " s JOIN (request_queue r, " + request_table + " t ON s."
						+ join_column + "=t.ID AND t.request_queueID=r.ID "
						+ "SET s.statusCode=?, s.explanation=? " + "WHERE r.ID=? AND "
						+ surl_column + " IN " + makeInString(surls);
					update = con.prepareStatement(auxstr);
					logWarnings(con.getWarnings());
					update.setInt(1,
						StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_ABORTED));
					logWarnings(update.getWarnings());
					update.setString(2, "User aborted request!");
					logWarnings(update.getWarnings());
					update.setLong(3, id);
					logWarnings(update.getWarnings());
					log.trace("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest "
						+ "- {}", update);
					update.executeUpdate();
					logWarnings(update.getWarnings());
				} else {
					log.error("REQUEST SUMMARY DAO - Unable to complete "
						+ "abortChunksOfInProgressRequest: could not update file statuses "
						+ "because the request type could not be translated from the DB!");
				}
			}
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest: {}", 
				e.getMessage(), e);
		} finally {
			close(rs);
			close(query);
			close(update);
		}
	}

	/**
	 * Private method that returns a String of all SURLS in the collection of
	 * String.
	 */
	private String makeInString(Collection<String> c) {

		StringBuffer sb = new StringBuffer("(");
		for (Iterator<String> i = c.iterator(); i.hasNext();) {
			sb.append(i.next());
			if (i.hasNext()) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Method that returns the config_RequestTypeID field present in request_queue
	 * table, for the request with the specified request token rt. In case of any
	 * error, the empty String "" is returned.
	 */
	public String typeOf(String rt) {

		PreparedStatement query = null;
		ResultSet rs = null;
		String result = "";
		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - typeOf: unable to get a valid connection!");
			return result;
		}
		try {
			query = con
				.prepareStatement("SELECT config_RequestTypeID from request_queue WHERE r_token=?");
			logWarnings(con.getWarnings());
			query.setString(1, rt);
			logWarnings(query.getWarnings());
			log.trace("REQUEST SUMMARY DAO - typeOf - {}", query);
			rs = query.executeQuery();
			logWarnings(query.getWarnings());
			if (rs.next()) {
				result = rs.getString("config_RequestTypeID");
			}
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - typeOf - {}", e.getMessage(), e);
		} finally {
			close(rs);
			close(query);
		}
		return result;
	}

	/**
	 * Method that returns the config_RequestTypeID field present in request_queue
	 * table, for the request with the specified request token rt. In case of any
	 * error, the empty String "" is returned.
	 */
	public RequestSummaryDataTO find(String rt) {

		PreparedStatement query = null;
		ResultSet rs = null;
		RequestSummaryDataTO to = null;
		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - find: unable to get a valid connection!");
			return null;
		}
		try {
			query = con
				.prepareStatement("SELECT * from request_queue WHERE r_token=?");
			logWarnings(con.getWarnings());
			query.setString(1, rt);
			con.setAutoCommit(false);

			rs = query.executeQuery();
			logWarnings(query.getWarnings());
			if (!rs.first()) {
				log.debug("No requests found with token {}", rt);
				return null;
			}
			to = new RequestSummaryDataTO();
			to.setPrimaryKey(rs.getLong("ID"));
			to.setRequestType(rs.getString("config_RequestTypeID"));
			to.setClientDN(rs.getString("client_dn"));
			to.setUserToken(rs.getString("u_token"));
			to.setRetrytime(rs.getInt("retrytime"));
			to.setPinLifetime(rs.getInt("pinLifetime"));
			to.setSpaceToken(rs.getString("s_token"));
			to.setStatus(rs.getInt("status"));
			to.setErrstring(rs.getString("errstring"));
			to.setRequestToken(rs.getString("r_token"));
			to.setRemainingTotalTime(rs.getInt("remainingTotalTime"));
			to.setFileLifetime(rs.getInt("fileLifetime"));
			to.setNbreqfiles(rs.getInt("nbreqfiles"));
			to.setNumOfCompleted(rs.getInt("numOfCompleted"));
			to.setNumOfWaiting(rs.getInt("numOfWaiting"));
			to.setNumOfFailed(rs.getInt("numOfFailed"));
			to.setTimestamp(rs.getTimestamp("timeStamp"));
			
			
			java.sql.Blob blob = rs.getBlob("proxy");
			if (blob != null) {
				byte[] bdata = blob.getBytes(1, (int) blob.length());
				to.setVomsAttributes(new String(bdata));
			}
			to.setDeferredStartTime(rs.getInt("deferredStartTime"));
			to.setRemainingDeferredStartTime(rs.getInt("remainingDeferredStartTime"));

			if (rs.next()) {
				log.warn("More than a row matches token {}", rt);
			}
			close(rs);
			close(query);
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - find - {}", e.getMessage(), e);
		} finally {
			close(rs);
			close(query);
		}
		return to;
	}

	/**
	 * Method that purges expired requests: it only removes up to a fixed value of
	 * expired requests at a time. The value is configured and obtained from the
	 * configuration property getPurgeBatchSize. A List of Strings with the
	 * request tokens removed is returned. In order to completely remove all
	 * expired requests, simply keep invoking this method until an empty List is
	 * returned. This batch processing is needed because there could be millions
	 * of expired requests which are likely to result in out-of-memory problems.
	 * Notice that in case of errors only error messages get logged. An empty List
	 * is also returned.
	 */
	public List<String> purgeExpiredRequests(long expiredRequestTime, int purgeSize) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> requestTokens = new ArrayList<String>();
		List<Long> ids = new ArrayList<Long>();
		
		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - purgeExpiredRequests: unable to get a "
				+ "valid connection!");
			return requestTokens;
		}
		
		try {
			// start transaction
			con.setAutoCommit(false);
			String stmt = "SELECT ID, r_token FROM request_queue WHERE UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(timeStamp) > ? AND status <> ?  AND status <> ? LIMIT ?";
			ps = con.prepareStatement(stmt);
			ps.setLong(1, expiredRequestTime);
			ps.setInt(2, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED));
			ps.setInt(3, StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_INPROGRESS));
			ps.setInt(4, purgeSize);
			logWarnings(con.getWarnings());
			log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - {}", ps);

			rs = ps.executeQuery();
			logWarnings(ps.getWarnings());

			while (rs.next()) {
				requestTokens.add(rs.getString("r_token"));
				ids.add(new Long(rs.getLong("ID")));
			}

			close(rs);
			close(ps);

			if (!ids.isEmpty()) {
				// REMOVE BATCH OF EXPIRED REQUESTS!
				stmt = "DELETE FROM request_queue WHERE ID in " + makeWhereString(ids);

				ps = con.prepareStatement(stmt);
				logWarnings(con.getWarnings());
				log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - {}", stmt);

				int deleted = ps.executeUpdate();
				logWarnings(ps.getWarnings());
				if (deleted > 0) {
					log.info("REQUEST SUMMARY DAO - purgeExpiredRequests - Deleted {} "
						+ "expired requests.", deleted);
				} else {
					log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - No deleted "
						+ "expired requests.");
				}

				close(ps);

				stmt = "DELETE request_DirOption FROM request_DirOption "
					+ " LEFT JOIN request_Get ON request_DirOption.ID = request_Get.request_DirOptionID"
					+ " LEFT JOIN request_BoL ON request_DirOption.ID = request_BoL.request_DirOptionID "
					+ " LEFT JOIN request_Copy ON request_DirOption.ID = request_Copy.request_DirOptionID"
					+ " WHERE request_Copy.request_DirOptionID IS NULL AND"
					+ " request_Get.request_DirOptionID IS NULL AND"
					+ " request_BoL.request_DirOptionID IS NULL;";

				ps = con.prepareStatement(stmt);
				logWarnings(con.getWarnings());
				log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - {}", stmt);
				deleted = ps.executeUpdate();
				logWarnings(ps.getWarnings());

				if (deleted > 0) {
					log.info("REQUEST SUMMARY DAO - purgeExpiredRequests - Deleted {} "
						+ "DirOption related to expired requests.", deleted);
				} else {
					log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - No Deleted "
						+ "DirOption related to expired requests.");
				}
				close(ps);

			}
			// commit and finish transaction
			con.commit();
			logWarnings(con.getWarnings());
			con.setAutoCommit(true);
			logWarnings(con.getWarnings());
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - purgeExpiredRequests - Rolling back "
				+ "because of error: {}", e.getMessage(), e);
			rollback(con);
		} finally {
			close(rs);
			close(ps);
		}
		return requestTokens;
	}

	/**
	 * Retrieve the total number of expired requests.
	 * 
	 * @return
	 */
	public int getNumberExpired() {

		int rowCount = 0;

		if (!checkConnection()) {
			log.error("REQUEST SUMMARY DAO - getNumberExpired: unable to get a "
				+ "valid connection!");
			return 0;
		}

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// start transaction
			con.setAutoCommit(false);

			String stmt = "SELECT count(*) FROM request_queue WHERE UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(timeStamp) > ? AND status <> ? AND status <> ? ";
			ps = con.prepareStatement(stmt);
			ps.setLong(1, Configuration.getInstance().getExpiredRequestTime());
			ps.setInt(2,
				StatusCodeConverter.getInstance().toDB(TStatusCode.SRM_REQUEST_QUEUED));
			ps.setInt(3, StatusCodeConverter.getInstance()
				.toDB(TStatusCode.SRM_REQUEST_INPROGRESS));

			logWarnings(con.getWarnings());
			log.trace("REQUEST SUMMARY DAO - Number of expired requests: {}", ps);
			rs = ps.executeQuery();
			logWarnings(ps.getWarnings());

			// Get the number of rows from the result set
			rs.next();
			rowCount = rs.getInt(1);
			log.debug("Nr of expired requests is: {}", rowCount);

			close(rs);
			close(ps);
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO - purgeExpiredRequests - Rolling back "
				+ "because of error: {}", e.getMessage(), e);
			rollback(con);
		} finally {
			close(rs);
			close(ps);
		}

		return rowCount;

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
	 * Auxiliary method that sets up the connection to the DB, as well as the
	 * prepared statement.
	 */
	private boolean setUpConnection() {

		boolean response = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, name, password);
			if (con == null) {
				log.error("REQUEST SUMMARY DAO! DriverManager returned null connection!");
			} else {
				logWarnings(con.getWarnings());
				response = con.isValid(0);
			}
		} catch (ClassNotFoundException e) {
			log.error("REQUEST SUMMARY DAO! Exception in setUpConnection! {}", 
				e.getMessage(), e);
		} catch (SQLException e) {
			log.error("REQUEST SUMMARY DAO! Exception in setUpConnection! {}", 
				e.getMessage(), e);
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
				log.error("REQUEST SUMMARY DAO! Exception in takeDownConnection "
					+ "method: {}", e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary method that checks if time for resetting the connection has come,
	 * and eventually takes it down and up back again.
	 */
	private boolean checkConnection() {

		boolean response = true;
		if (reconnect) {
			takeDownConnection();
			response = setUpConnection();
			if (response) {
				reconnect = false;
			}
		}
		return response;
	}

	/**
	 * Auxiliary method used to close a Statement
	 */
	private void close(Statement stmt) {

		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error("REQUEST SUMMARY DAO! Unable to close Statement {} - "
					+ "Error: {}", stmt.toString(), e.getMessage(), e);
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
				log.error("REQUEST SUMMARY DAO! Unable to close ResultSet! Error: {}", 
					e.getMessage(), e);
			}
		}
	}

	/**
	 * Auxiliary method used to roll back a transaction
	 */
	private void rollback(Connection con) {

		if (con != null) {
			try {
				con.rollback();
				logWarnings(con.getWarnings());
				log.error("PICKER2: roll back successful!");
			} catch (SQLException e2) {
				log.error("PICKER2: roll back failed! {}", e2.getMessage(), e2);
			}
		}
	}

	/**
	 * Private auxiliary method used to log SQLWarnings.
	 */
	private void logWarnings(SQLWarning warning) {

		if (warning != null) {
			log.debug("REQUEST SUMMARY DAO: {}", warning.toString());
			while ((warning = warning.getNextWarning()) != null) {
				log.debug("REQUEST SUMMARY DAO: {}", warning.toString());
			}
		}
	}

}
