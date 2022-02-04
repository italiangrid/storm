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

package it.grid.storm.persistence.impl.mysql;

import static it.grid.storm.srm.types.TRequestType.EMPTY;
import static it.grid.storm.srm.types.TRequestType.PREPARE_TO_GET;
import static it.grid.storm.srm.types.TRequestType.PREPARE_TO_PUT;
import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_QUEUED;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.config.Configuration;
import it.grid.storm.persistence.converter.RequestTypeConverter;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.RequestSummaryDAO;
import it.grid.storm.persistence.model.RequestSummaryDataTO;
import it.grid.storm.persistence.pool.StormDbConnectionPool;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TStatusCode;

/**
 * DAO class for RequestSummaryCatalog. This DAO is specifically designed to connect to a MySQL DB.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date May 2005
 */
public class RequestSummaryDAOMySql extends AbstractDAO implements RequestSummaryDAO {

  private static final Logger log = LoggerFactory.getLogger(RequestSummaryDAOMySql.class);

  private static final String SELECT_REQUEST_WHERE_STATUS_WITH_LIMIT =
      "SELECT ID, config_RequestTypeID, r_token, timeStamp, client_dn, proxy "
          + "FROM request_queue WHERE status=? LIMIT ?";

  private static final String UPDATE_REQUEST_STATUS_WHERE_ID_IS =
      "UPDATE request_queue SET status=?, errstring=? WHERE ID=?";

  private static final String UPDATE_REQUEST_STATUS_WHERE_TOKEN_IS =
      "UPDATE request_queue SET status=?, errstring=? WHERE r_token=?";

  private static final String UPDATE_REQUEST_STATUS_WHERE_TOKEN_AND_STATUS_ARE =
      "UPDATE request_queue SET status=?, errstring=? WHERE r_token=? AND status=?";

  private static final String UPDATE_REQUEST_STATUS_AND_PINLIFETIME_WHERE_TOKEN_IS =
      "UPDATE request_queue "
          + "SET status=?, errstring=?, pinLifetime=pinLifetime+(UNIX_TIMESTAMP()-UNIX_TIMESTAMP(timeStamp)) "
          + "WHERE r_token=?";

  private static final String SELECT_REQUEST_WHERE_TOKEN_IS =
      "SELECT ID, config_RequestTypeID from request_queue WHERE r_token=?";

  private static final String SELECT_FULL_REQUEST_WHERE_TOKEN_IS =
      "SELECT * from request_queue WHERE r_token=?";

  private static final String SELECT_REQUEST_WHERE_TOKEN_AND_STATUS =
      "SELECT ID, config_RequestTypeID FROM request_queue WHERE r_token=? AND status=?";

  private static final String UPDATE_REQUEST_GET_STATUS_WHERE_ID_IS = "UPDATE status_Get s "
      + "JOIN (request_queue r, request_Get t) ON (s.request_GetID=t.ID AND t.request_queueID=r.ID) "
      + "SET s.statusCode=?, s.explanation=? WHERE r.ID=?";

  private static final String UPDATE_REQUEST_PUT_STATUS_WHERE_ID_IS = "UPDATE status_Put s "
      + "JOIN (request_queue r, request_Put t) ON (s.request_PutID=t.ID AND t.request_queueID=r.ID) "
      + "SET s.statusCode=?, s.explanation=? WHERE r.ID=?";

  private static final String UPDATE_REQUEST_BOL_STATUS_WHERE_ID_IS = "UPDATE status_BoL s "
      + "JOIN (request_queue r, request_BoL t) ON (s.request_BoLID=t.ID AND t.request_queueID=r.ID) "
      + "SET s.statusCode=?, s.explanation=? WHERE r.ID=?";

  private static final String UPDATE_REQUEST_GET_STATUS_WHERE_ID_IS_AND_SURL_IN =
      "UPDATE status_Get s "
          + "JOIN (request_queue r, request_Get t) ON (s.request_GetID=t.ID AND t.request_queueID=r.ID) "
          + "SET s.statusCode=?, s.explanation=? WHERE r.ID=? AND sourceSURL IN ";

  private static final String UPDATE_REQUEST_PUT_STATUS_WHERE_ID_IS_AND_SURL_IN =
      "UPDATE status_Put s "
          + "JOIN (request_queue r, request_Put t) ON (s.request_PutID=t.ID AND t.request_queueID=r.ID) "
          + "SET s.statusCode=?, s.explanation=? WHERE r.ID=? AND targetSURL IN ";

  private static final String UPDATE_REQUEST_BOL_STATUS_WHERE_ID_IS_AND_SURL_IN =
      "UPDATE status_BoL s "
          + "JOIN (request_queue r, request_BoL t) ON (s.request_BoLID=t.ID AND t.request_queueID=r.ID) "
          + "SET s.statusCode=?, s.explanation=? WHERE r.ID=? AND sourceSURL IN ";

  private static final String SELECT_PURGEABLE_REQUESTS_WITH_LIMIT =
      "SELECT ID, r_token FROM request_queue "
          + "WHERE UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(timeStamp) > ? AND status <> ?  AND status <> ? LIMIT ?";

  private static final String COUNT_PURGEABLE_REQUESTS = "SELECT count(*) FROM request_queue "
      + "WHERE UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(timeStamp) > ? AND status <> ? AND status <> ? ";

  private static final String DELETE_ORPHANS_DIR_OPTION =
      "DELETE request_DirOption FROM request_DirOption "
          + " LEFT JOIN request_Get ON request_DirOption.ID = request_Get.request_DirOptionID"
          + " LEFT JOIN request_BoL ON request_DirOption.ID = request_BoL.request_DirOptionID "
          + " LEFT JOIN request_Copy ON request_DirOption.ID = request_Copy.request_DirOptionID"
          + " WHERE request_Copy.request_DirOptionID IS NULL AND"
          + " request_Get.request_DirOptionID IS NULL AND"
          + " request_BoL.request_DirOptionID IS NULL;";

  private static RequestSummaryDAO instance;

  private final StatusCodeConverter statusCodeConverter;
  private final RequestTypeConverter requestTypeConverter;
  private final int MAX_FETCHED_REQUESTS = Configuration.getInstance().getRequestsPickerAgentMaxFetchedSize();

  public static synchronized RequestSummaryDAO getInstance() {
    if (instance == null) {
      instance = new RequestSummaryDAOMySql();
    }
    return instance;
  }

  private RequestSummaryDAOMySql() {
    super(StormDbConnectionPool.getInstance());
    statusCodeConverter = StatusCodeConverter.getInstance();
    requestTypeConverter = RequestTypeConverter.getInstance();
  }

  /**
   * Method that retrieves requests in the SRM_REQUEST_QUEUED status: retrieved requests are limited
   * to the number specified by the Configuration method getPicker2MaxBatchSize. All retrieved
   * requests get their global status transited to SRM_REQUEST_INPROGRESS. A Collection of
   * RequestSummaryDataTO is returned: if none are found, an empty collection is returned.
   */
  public synchronized Collection<RequestSummaryDataTO> fetchNewRequests(int limit) {

    Connection con = null;
    PreparedStatement fetch = null;
    PreparedStatement update = null;
    ResultSet fetched = null;
    Collection<RequestSummaryDataTO> results = Lists.newArrayList();
    int howMuch = limit > MAX_FETCHED_REQUESTS ? MAX_FETCHED_REQUESTS : limit;

    try {
      con = getManagedConnection();

      // get id, request type, request token and client_DN of newly added
      // requests, which must be in SRM_REQUEST_QUEUED state
      fetch = con.prepareStatement(SELECT_REQUEST_WHERE_STATUS_WITH_LIMIT);
      fetch.setInt(1, statusCodeConverter.toDB(SRM_REQUEST_QUEUED));
      fetch.setInt(2, howMuch);
      fetched = fetch.executeQuery();

      Collection<Long> rowids = Lists.newArrayList();

      while (fetched.next()) {
        long id = fetched.getLong("ID");
        rowids.add(Long.valueOf(id));
        RequestSummaryDataTO aux = new RequestSummaryDataTO();
        aux.setPrimaryKey(id);
        aux.setRequestType(fetched.getString("config_RequestTypeID"));
        aux.setRequestToken(fetched.getString("r_token"));
        aux.setClientDN(fetched.getString("client_dn"));
        aux.setTimestamp(fetched.getTimestamp("timeStamp"));

        /**
         * This code is only for the 1.3.18. This is a workaround to get FQANs using the proxy field
         * on request_queue. The FE use the proxy field of request_queue to insert a single FQAN
         * string containing all FQAN separated by the "#" char. The proxy is a BLOB, hence it has
         * to be properly converted in string.
         */
        java.sql.Blob blob = fetched.getBlob("proxy");
        if (blob != null) {
          byte[] bdata = blob.getBytes(1, (int) blob.length());
          aux.setVomsAttributes(new String(bdata));
        }

        results.add(aux);
      }

      // transit state from SRM_REQUEST_QUEUED to SRM_REQUEST_INPROGRESS
      if (!results.isEmpty()) {
        String updateQuery =
            "UPDATE request_queue SET status=?, errstring=? WHERE ID IN " + makeWhereString(rowids);
        update = con.prepareStatement(updateQuery);
        update.setInt(1, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
        update.setString(2, "Request handled!");
        log.trace("REQUEST SUMMARY DAO - findNew: executing {}", update);
        update.executeUpdate();
      }

      // commit and finish transaction
      con.commit();

    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - findNew: Unable to complete picking. "
          + "Error: {}. Rolling back!", e.getMessage(), e);
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }

    } finally {
      closeResultSet(fetched);
      closeStatement(fetch);
      closeStatement(update);
      closeConnection(con);
    }

    return results;
  }

  /**
   * Method used to signal in the DB that a request failed: the status of the request identified by
   * the primary key index is transited to SRM_FAILURE, with the supplied explanation String. The
   * supplied index is the primary key of the global request. In case of any error, nothing gets
   * done and no exception is thrown, but proper error messages get logged.
   */
  public void failRequest(long requestId, String explanation) {

    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = getConnection();
      ps = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_ID_IS);
      ps.setInt(1, statusCodeConverter.toDB(SRM_FAILURE));
      ps.setString(2, explanation);
      ps.setLong(3, requestId);
      log.trace("REQUEST SUMMARY DAO! failRequest executing: {}", ps);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO! Unable to transit request identified by "
          + "ID {} to SRM_FAILURE! Error: {}", requestId, e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * Method used to signal in the DB that a PtGRequest failed. The global status transits to
   * SRM_FAILURE, as well as that of each chunk associated to the request. The supplied explanation
   * string is used both for the global status as well as for each individual chunk. The supplied
   * index is the primary key of the global request. In case of any error, nothing gets done and no
   * exception is thrown, but proper error messages get logged.
   */
  public void failPtGRequest(long requestId, String explanation) {

    Connection con = null;
    PreparedStatement updateReq = null;
    PreparedStatement updateChunk = null;

    int failCode = statusCodeConverter.toDB(SRM_FAILURE);
    try {
      // start transaction
      con = getManagedConnection();

      // update global status
      updateReq = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_ID_IS);
      updateReq.setInt(1, failCode);
      updateReq.setString(2, explanation);
      updateReq.setLong(3, requestId);
      log.trace("REQUEST SUMMARY DAO! failPtGRequest executing: {}", updateReq);
      updateReq.executeUpdate();

      // update each chunk status
      updateChunk = con.prepareStatement(UPDATE_REQUEST_GET_STATUS_WHERE_ID_IS);
      updateChunk.setInt(1, failCode);
      updateChunk.setString(2, explanation);
      updateChunk.setLong(3, requestId);
      log.trace("REQUEST SUMMARY DAO! failPtGRequest executing: {}", updateChunk);
      updateChunk.executeUpdate();

      // commit and finish transaction
      con.commit();
    } catch (SQLException e) {
      log.error(
          "REQUEST SUMMARY DAO! Unable to transit PtG request identified "
              + "by ID {} to SRM_FAILURE! Error: {}\nRolling back...",
          requestId, e.getMessage(), e);
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeStatement(updateReq);
      closeStatement(updateChunk);
      closeConnection(con);
    }
  }

  /**
   * Method used to signal in the DB that a PtPRequest failed. The global status transits to
   * SRM_FAILURE, as well as that of each chunk associated to the request. The supplied explanation
   * string is used both for the global status as well as for each individual chunk. The supplied
   * index is the primary key of the global request. In case of any error, nothing gets done and no
   * exception is thrown, but proper error messagges get logged.
   */
  public void failPtPRequest(long requestId, String explanation) {

    Connection con = null;
    PreparedStatement updateReq = null;
    PreparedStatement updateChunk = null;
    int failCode = statusCodeConverter.toDB(SRM_FAILURE);
    try {
      // start transaction
      con = getManagedConnection();

      // update global status
      updateReq = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_ID_IS);
      updateReq.setInt(1, failCode);
      updateReq.setString(2, explanation);
      updateReq.setLong(3, requestId);
      log.trace("REQUEST SUMMARY DAO! failPtPRequest executing: {}", updateReq);
      updateReq.executeUpdate();

      // update each chunk status
      updateChunk = con.prepareStatement(UPDATE_REQUEST_PUT_STATUS_WHERE_ID_IS);
      updateChunk.setInt(1, failCode);
      updateChunk.setString(2, explanation);
      updateChunk.setLong(3, requestId);
      log.trace("REQUEST SUMMARY DAO! failPtPRequest executing: {}", updateChunk);
      updateChunk.executeUpdate();

      // commit and finish transaction
      con.commit();

    } catch (SQLException e) {
      log.error(
          "REQUEST SUMMARY DAO! Unable to transit PtP request identified "
              + "by ID {} to SRM_FAILURE! Error: {}\nRolling back...",
          requestId, e.getMessage(), e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeStatement(updateReq);
      closeStatement(updateChunk);
      closeConnection(con);
    }
  }

  /**
   * Method used to update the global status of the request identified by the RequestToken rt. It
   * gets updated the supplied status, with the supplied explanation String. If the supplied request
   * token does not exist, nothing happens.
   */
  public void updateGlobalStatus(TRequestToken requestToken, TStatusCode status,
      String explanation) {

    Connection con = null;
    PreparedStatement update = null;
    try {
      con = getConnection();
      update = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_TOKEN_IS);
      update.setInt(1, statusCodeConverter.toDB(status));
      update.setString(2, explanation);
      update.setString(3, requestToken.getValue());
      log.trace("REQUEST SUMMARY DAO - updateGlobalStatus: executing {}", update);
      update.executeUpdate();
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(update);
      closeConnection(con);
    }
  }

  public void updateGlobalStatusOnMatchingGlobalStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation) {

    Connection con = null;
    PreparedStatement update = null;
    try {
      con = getConnection();
      update = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_TOKEN_AND_STATUS_ARE);
      update.setInt(1, statusCodeConverter.toDB(newStatusCode));
      update.setString(2, explanation);
      update.setString(3, requestToken.getValue());
      update.setInt(4, statusCodeConverter.toDB(expectedStatusCode));
      log.trace("REQUEST SUMMARY DAO - updateGlobalStatusOnMatchingGlobalStatus: executing {}",
          update);
      update.executeUpdate();
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO: {}", e.getMessage(), e);
    } finally {
      closeStatement(update);
      closeConnection(con);
    }
  }

  /**
   * Method used to update the global status of the request identified by the RequestToken rt. It
   * gets updated the supplied status, with the supplied explanation String and pin and file
   * lifetimes are updated in order to start the countdown from now. If the supplied request token
   * does not exist, nothing happens.
   */
  public void updateGlobalStatusPinFileLifetime(TRequestToken requestToken, TStatusCode status,
      String explanation) {

    Connection con = null;
    PreparedStatement update = null;

    try {
      con = getConnection();
      update = con.prepareStatement(UPDATE_REQUEST_STATUS_AND_PINLIFETIME_WHERE_TOKEN_IS);
      update.setInt(1, statusCodeConverter.toDB(status));
      update.setString(2, explanation);
      update.setString(3, requestToken.getValue());
      log.trace("REQUEST SUMMARY DAO - updateGlobalStatus: executing {}", update);
      update.executeUpdate();

    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(update);
      closeConnection(con);
    }
  }

  /**
   * Method used to transit the status of a request that is in SRM_REQUEST_QUEUED state, to
   * SRM_ABORTED. All files associated with the request will also get their status changed to
   * SRM_ABORTED. If the supplied token is null, or not found, or not in the SRM_REQUEST_QUEUED
   * state, then nothing happens.
   */
  public void abortRequest(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement update = null;
    PreparedStatement query = null;
    ResultSet rs = null;

    try {
      con = getManagedConnection();

      query = con.prepareStatement(SELECT_REQUEST_WHERE_TOKEN_AND_STATUS);
      query.setString(1, requestToken.getValue());
      query.setInt(2, statusCodeConverter.toDB(SRM_REQUEST_QUEUED));
      log.trace("REQUEST SUMMARY DAO - abortRequest - {}", query);
      rs = query.executeQuery();

      if (rs.next()) {
        long id = rs.getLong("ID");
        String type = rs.getString("config_RequestTypeID");
        update = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_ID_IS);
        update.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
        update.setString(2, "User aborted request!");
        update.setLong(3, id);
        log.trace("REQUEST SUMMARY DAO - abortRequest - {}", update);
        update.executeUpdate();

        // update single chunk file statuses
        TRequestType rtyp = requestTypeConverter.toSTORM(type);
        if (EMPTY.equals(rtyp)) {
          log.error("REQUEST SUMMARY DAO - Unable to complete abortRequest: "
              + "could not update file statuses because the request type could "
              + "not be translated from the DB!");
          con.rollback();
        } else {
          if (PREPARE_TO_GET.equals(rtyp)) {
            update = con.prepareStatement(UPDATE_REQUEST_GET_STATUS_WHERE_ID_IS);
          } else if (PREPARE_TO_PUT.equals(rtyp)) {
            update = con.prepareStatement(UPDATE_REQUEST_PUT_STATUS_WHERE_ID_IS);
          } else {
            update = con.prepareStatement(UPDATE_REQUEST_BOL_STATUS_WHERE_ID_IS);
          }
          update.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
          update.setString(2, "User aborted request!");
          update.setLong(3, id);
          log.trace("REQUEST SUMMARY DAO - abortRequest - {}", update);
          update.executeUpdate();
          con.commit();
        }
      } else {
        con.rollback();
      }
    } catch (SQLException e) {

      log.error("REQUEST SUMMARY DAO - abortRequest: {}", e.getMessage(), e);
      e.printStackTrace();

    } finally {
      closeResultSet(rs);
      closeStatement(update);
      closeStatement(query);
      closeConnection(con);
    }
  }

  /**
   * Method used to transit the status of a request that is in SRM_REQUEST_INPROGRESS state, to
   * SRM_ABORTED. All files associated with the request will also get their status changed to
   * SRM_ABORTED. If the supplied token is null, or not found, or not in the SRM_REQUEST_INPROGRESS
   * state, then nothing happens.
   */
  public void abortInProgressRequest(TRequestToken rt) {

    Connection con = null;
    PreparedStatement updateReq = null;
    PreparedStatement updateChunk = null;
    PreparedStatement query = null;
    ResultSet rs = null;

    try {
      con = getManagedConnection();

      query = con.prepareStatement(SELECT_REQUEST_WHERE_TOKEN_AND_STATUS);
      query.setString(1, rt.getValue());
      query.setInt(2, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      log.trace("REQUEST SUMMARY DAO - abortInProgressRequest - {}", query);
      rs = query.executeQuery();

      if (rs.next()) {
        // token found...
        // get ID
        long id = rs.getLong("ID");
        String type = rs.getString("config_RequestTypeID");
        // update global request status
        updateReq = con.prepareStatement(UPDATE_REQUEST_STATUS_WHERE_ID_IS);
        updateReq.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
        updateReq.setString(2, "User aborted request!");
        updateReq.setLong(3, id);
        log.trace("REQUEST SUMMARY DAO - abortInProgressRequest - {}", updateReq);
        updateReq.executeUpdate();

        // update single chunk file statuses
        TRequestType rtyp = requestTypeConverter.toSTORM(type);
        if (EMPTY.equals(rtyp)) {
          log.error("REQUEST SUMMARY DAO - Unable to complete abortRequest: "
              + "could not update file statuses because the request type could "
              + "not be translated from the DB!");
          con.rollback();
        } else {
          if (PREPARE_TO_GET.equals(rtyp)) {
            updateChunk = con.prepareStatement(UPDATE_REQUEST_GET_STATUS_WHERE_ID_IS);
          } else if (PREPARE_TO_PUT.equals(rtyp)) {
            updateChunk = con.prepareStatement(UPDATE_REQUEST_PUT_STATUS_WHERE_ID_IS);
          } else {
            updateChunk = con.prepareStatement(UPDATE_REQUEST_BOL_STATUS_WHERE_ID_IS);
          }
        }
        updateChunk.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
        updateChunk.setString(2, "User aborted request!");
        updateChunk.setLong(3, id);
        log.trace("REQUEST SUMMARY DAO - abortInProgressRequest - {}", updateChunk);
        updateChunk.executeUpdate();
      } else {
        con.rollback();
      }
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - abortInProgressRequest: {}", e.getMessage(), e);
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rs);
      closeStatement(query);
      closeStatement(updateReq);
      closeStatement(updateChunk);
      closeConnection(con);
    }
  }

  /**
   * Method used to transit the status of chunks of a request that is in SRM_REQUEST_INPROGRESS
   * state, to SRM_ABORTED. If the supplied token is null, or not found, or not in the
   * SRM_REQUEST_INPROGRESS state, then nothing happens.
   */
  public void abortChunksOfInProgressRequest(TRequestToken requestToken, Collection<String> surls) {

    Connection con = null;
    PreparedStatement update = null;
    PreparedStatement query = null;
    ResultSet rs = null;

    try {
      con = getManagedConnection();
      query = con.prepareStatement(SELECT_REQUEST_WHERE_TOKEN_AND_STATUS);
      query.setString(1, requestToken.getValue());
      query.setInt(2, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      log.trace("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest - {}", query);
      rs = query.executeQuery();

      if (rs.next()) {
        long id = rs.getLong("ID");
        String type = rs.getString("config_RequestTypeID");
        // update single chunk file statuses
        TRequestType rtyp = requestTypeConverter.toSTORM(type);
        if (EMPTY.equals(rtyp)) {
          log.error("REQUEST SUMMARY DAO - Unable to complete abortRequest: "
              + "could not update file statuses because the request type could "
              + "not be translated from the DB!");
          con.rollback();
        } else {
          String updateQuery;
          if (PREPARE_TO_GET.equals(rtyp)) {
            updateQuery = UPDATE_REQUEST_GET_STATUS_WHERE_ID_IS_AND_SURL_IN + makeInString(surls);
          } else if (PREPARE_TO_PUT.equals(rtyp)) {
            updateQuery = UPDATE_REQUEST_PUT_STATUS_WHERE_ID_IS_AND_SURL_IN + makeInString(surls);
          } else {
            updateQuery = UPDATE_REQUEST_BOL_STATUS_WHERE_ID_IS_AND_SURL_IN + makeInString(surls);
          }
          update = con.prepareStatement(updateQuery);
        }
        update.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
        update.setString(2, "User aborted request!");
        update.setLong(3, id);
        log.trace("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest - {}", update);
        update.executeUpdate();
        con.commit();
      }
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - abortChunksOfInProgressRequest: {}", e.getMessage(), e);
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rs);
      closeStatement(query);
      closeStatement(update);
      closeConnection(con);
    }
  }

  /**
   * Private method that returns a String of all SURLS in the collection of String.
   */
  private String makeInString(Collection<String> c) {

    StringBuilder sb = new StringBuilder("(");
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
   * Method that returns the config_RequestTypeID field present in request_queue table, for the
   * request with the specified request token rt. In case of any error, the empty String "" is
   * returned.
   */
  public TRequestType getRequestType(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement query = null;
    ResultSet rs = null;
    TRequestType result = EMPTY;

    try {
      con = getConnection();
      query = con.prepareStatement(SELECT_REQUEST_WHERE_TOKEN_IS);
      query.setString(1, requestToken.getValue());
      log.trace("REQUEST SUMMARY DAO - typeOf - {}", query);
      rs = query.executeQuery();
      if (rs.next()) {
        result = requestTypeConverter.toSTORM(rs.getString("config_RequestTypeID"));
      }
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - typeOf - {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(query);
      closeConnection(con);
    }
    return result;
  }

  /**
   * Method that returns the config_RequestTypeID field present in request_queue table, for the
   * request with the specified request token rt. In case of any error, the empty String "" is
   * returned.
   */
  public RequestSummaryDataTO find(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement query = null;
    ResultSet rs = null;
    RequestSummaryDataTO to = null;

    try {
      con = getConnection();
      query = con.prepareStatement(SELECT_FULL_REQUEST_WHERE_TOKEN_IS);
      query.setString(1, requestToken.getValue());
      rs = query.executeQuery();

      if (rs.first()) {
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
          log.warn("More than a row matches token {}", requestToken);
        }
      }
    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - find - {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(query);
      closeConnection(con);
    }
    return to;
  }

  /**
   * Method that purges expired requests: it only removes up to a fixed value of expired requests at
   * a time. The value is configured and obtained from the configuration property getPurgeBatchSize.
   * A List of Strings with the request tokens removed is returned. In order to completely remove
   * all expired requests, simply keep invoking this method until an empty List is returned. This
   * batch processing is needed because there could be millions of expired requests which are likely
   * to result in out-of-memory problems. Notice that in case of errors only error messages get
   * logged. An empty List is also returned.
   */
  public Collection<String> purgeExpiredRequests(long expiredRequestTime, int purgeSize) {

    Connection con = null;
    PreparedStatement fetch = null;
    PreparedStatement deleteReq = null;
    PreparedStatement deleteOrphans = null;
    ResultSet rs = null;
    Collection<String> requestTokens = Lists.newArrayList();

    try {
      // start transaction
      con = getManagedConnection();

      fetch = con.prepareStatement(SELECT_PURGEABLE_REQUESTS_WITH_LIMIT);
      fetch.setLong(1, expiredRequestTime);
      fetch.setInt(2, statusCodeConverter.toDB(SRM_REQUEST_QUEUED));
      fetch.setInt(3, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      fetch.setInt(4, purgeSize);
      log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - {}", fetch);
      rs = fetch.executeQuery();

      Collection<Long> ids = Lists.newArrayList();

      while (rs.next()) {
        requestTokens.add(rs.getString("r_token"));
        ids.add(Long.valueOf(rs.getLong("ID")));
      }

      if (!ids.isEmpty()) {
        // REMOVE BATCH OF EXPIRED REQUESTS!

        String deleteQuery = "DELETE FROM request_queue WHERE ID in " + makeWhereString(ids);
        deleteReq = con.prepareStatement(deleteQuery);
        log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - {}", deleteReq);

        int deleted = deleteReq.executeUpdate();
        if (deleted > 0) {
          log.info("REQUEST SUMMARY DAO - purgeExpiredRequests - Deleted {} expired requests.",
              deleted);
        } else {
          log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - No deleted expired requests.");
        }

        deleteOrphans = con.prepareStatement(DELETE_ORPHANS_DIR_OPTION);
        log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - {}", deleteOrphans);
        deleted = deleteOrphans.executeUpdate();

        if (deleted > 0) {
          log.info("REQUEST SUMMARY DAO - purgeExpiredRequests - Deleted {} "
              + "DirOption related to expired requests.", deleted);
        } else {
          log.trace("REQUEST SUMMARY DAO - purgeExpiredRequests - No Deleted "
              + "DirOption related to expired requests.");
        }
      }
      // commit and finish transaction
      con.commit();

    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - purgeExpiredRequests - Rolling back because of error: {}",
          e.getMessage(), e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rs);
      closeStatement(fetch);
      closeStatement(deleteReq);
      closeStatement(deleteOrphans);
      closeConnection(con);
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

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      // start transaction
      con = getConnection();

      ps = con.prepareStatement(COUNT_PURGEABLE_REQUESTS);
      ps.setLong(1, Configuration.getInstance().getCompletedRequestsAgentPurgeAge());
      ps.setInt(2, statusCodeConverter.toDB(SRM_REQUEST_QUEUED));
      ps.setInt(3, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));

      log.trace("REQUEST SUMMARY DAO - Number of expired requests: {}", ps);
      rs = ps.executeQuery();

      // Get the number of rows from the result set
      if (rs.next()) {
        rowCount = rs.getInt(1);
      }
      log.debug("Nr of expired requests is: {}", rowCount);

    } catch (SQLException e) {
      log.error("REQUEST SUMMARY DAO - purgeExpiredRequests - Rolling back because of error: {}",
          e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(con);
    }

    return rowCount;

  }

  /**
   * Private method that returns a String of all IDs retrieved by the last SELECT.
   */
  private String makeWhereString(Collection<Long> rowids) {

    StringBuilder sb = new StringBuilder("(");
    for (Iterator<Long> i = rowids.iterator(); i.hasNext();) {
      sb.append(i.next());
      if (i.hasNext()) {
        sb.append(",");
      }
    }
    sb.append(")");
    return sb.toString();
  }

}
