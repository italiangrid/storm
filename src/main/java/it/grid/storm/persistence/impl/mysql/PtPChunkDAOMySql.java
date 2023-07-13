/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.impl.mysql;

import static it.grid.storm.catalogs.ChunkDAOUtils.buildInClauseForArray;
import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_LIFETIME_EXPIRED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_SPACE_AVAILABLE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.PtPChunkDAO;
import it.grid.storm.persistence.model.PtPChunkDataTO;
import it.grid.storm.persistence.model.ReducedPtPChunkDataTO;
import it.grid.storm.persistence.pool.impl.StormDbConnectionPool;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TStatusCode;

/**
 * DAO class for PtPChunkCatalog. This DAO is specifically designed to connect to a MySQL DB. The
 * raw data found in those tables is pre-treated in order to turn it into the Object Model of StoRM.
 * See Method comments for further info. BEWARE! DAO Adjusts for extra fields in the DB that are not
 * present in the object model.
 * 
 * @author EGRID ICTP
 * @version 2.0
 * @date June 2005
 */
public class PtPChunkDAOMySql extends AbstractDAO implements PtPChunkDAO {

  private static final Logger log = LoggerFactory.getLogger(PtPChunkDAOMySql.class);

  private static final String UPDATE_REQUEST_PUT_WHERE_ID_IS = "UPDATE "
      + "request_queue rq JOIN (status_Put sp, request_Put rp) ON "
      + "(rq.ID=rp.request_queueID AND sp.request_PutID=rp.ID) "
      + "SET sp.transferURL=?, sp.statusCode=?, sp.explanation=?, rq.pinLifetime=?, rq.fileLifetime=?, "
      + "rq.config_FileStorageTypeID=?, rq.config_OverwriteID=?, "
      + "rp.normalized_targetSURL_StFN=?, rp.targetSURL_uniqueID=? " + "WHERE rp.ID=?";

  private static final String UPDATE_REDUCED_REQUEST_PUT_WHERE_ID_IS =
      "UPDATE request_Put SET normalized_targetSURL_StFN=?, targetSURL_uniqueID=? " + "WHERE ID=?";

  private static final String SELECT_REQUEST_PROTOCOLS_WHERE_TOKEN_IS =
      "SELECT tp.config_ProtocolsID "
          + "FROM request_TransferProtocols tp JOIN request_queue rq ON tp.request_queueID=rq.ID "
          + "WHERE rq.r_token=?";

  private static final String SELECT_FULL_REQUEST_PUT_WHERE_TOKEN_AND_STATUS =
      "SELECT rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, rq.s_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, sp.statusCode "
          + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
          + "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
          + "WHERE rq.r_token=? AND sp.statusCode<>?";

  private static final String UPDATE_STATUS_PUT_WHERE_ID_IS =
      "UPDATE status_Put sp SET sp.statusCode=?, sp.explanation=? WHERE sp.request_PutID=?";

  private static final String SELECT_EXPIRED_REQUEST_PUT_WHERE_STATUS_IS =
      "SELECT rp.ID, rp.targetSURL "
          + "FROM status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
          + "WHERE sp.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

  private static PtPChunkDAO instance;

  public static synchronized PtPChunkDAO getInstance() {
    if (instance == null) {
      instance = new PtPChunkDAOMySql();
    }
    return instance;
  }

  private StatusCodeConverter statusCodeConverter;

  private PtPChunkDAOMySql() {

    super(StormDbConnectionPool.getInstance());
    statusCodeConverter = StatusCodeConverter.getInstance();
  }

  /**
   * Method used to save the changes made to a retrieved PtPChunkDataTO, back into the MySQL DB.
   * Only the transferURL, statusCode and explanation, of status_Put table get written to the DB.
   * Likewise for the pinLifetime and fileLifetime of request_queue. In case of any error, an error
   * message gets logged but no exception is thrown.
   */
  public synchronized void update(PtPChunkDataTO to) {

    Connection con = null;
    PreparedStatement updatePut = null;
    try {
      con = getConnection();
      updatePut = con.prepareStatement(UPDATE_REQUEST_PUT_WHERE_ID_IS);

      updatePut.setString(1, to.transferURL());
      updatePut.setInt(2, to.status());
      updatePut.setString(3, to.errString());
      updatePut.setInt(4, to.pinLifetime());
      updatePut.setInt(5, to.fileLifetime());
      updatePut.setString(6, to.fileStorageType());
      updatePut.setString(7, to.overwriteOption());
      updatePut.setString(8, to.normalizedStFN());
      updatePut.setInt(9, to.surlUniqueID());
      updatePut.setLong(10, to.primaryKey());
      // run updateStatusPut...
      log.trace("PtP CHUNK DAO - update method: {}", updatePut);
      updatePut.executeUpdate();
    } catch (SQLException e) {
      log.error("PtP CHUNK DAO: Unable to complete update! {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(updatePut);
      closeConnection(con);
    }
  }

  /**
   * Updates the request_Put represented by the received ReducedPtPChunkDataTO by setting its
   * normalized_targetSURL_StFN and targetSURL_uniqueID
   * 
   * @param chunkTO
   */
  public synchronized void updateIncomplete(ReducedPtPChunkDataTO chunkTO) {

    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = getConnection();
      stmt = con.prepareStatement(UPDATE_REDUCED_REQUEST_PUT_WHERE_ID_IS);
      stmt.setString(1, chunkTO.normalizedStFN());
      stmt.setInt(2, chunkTO.surlUniqueID());
      stmt.setLong(3, chunkTO.primaryKey());
      log.trace("PtP CHUNK DAO - update incomplete: {}", stmt);
      stmt.executeUpdate();
    } catch (SQLException e) {
      log.error("PtP CHUNK DAO: Unable to complete update incomplete! {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method that queries the MySQL DB to find all entries matching the supplied TRequestToken. The
   * Collection contains the corresponding PtPChunkDataTO objects. An initial simple query
   * establishes the list of protocols associated with the request. A second complex query
   * establishes all chunks associated with the request, by properly joining request_queue,
   * request_Put and status_Put. The considered fields are: (1) From status_Put: the ID field which
   * becomes the TOs primary key, and statusCode. (2) From request_Put: targetSURL and
   * expectedFileSize. (3) From request_queue: pinLifetime, fileLifetime, config_FileStorageTypeID,
   * s_token, config_OverwriteID. In case of any error, a log gets written and an empty collection
   * is returned. No exception is returned. NOTE! Chunks in SRM_ABORTED status are NOT returned!
   * This is important because this method is intended to be used by the Feeders to fetch all chunks
   * in the request, and aborted chunks should not be picked up for processing!
   */
  public synchronized Collection<PtPChunkDataTO> find(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement findProtocols = null;
    PreparedStatement findRequest = null;
    ResultSet rsProtocols = null;
    ResultSet rsRequest = null;

    Collection<PtPChunkDataTO> results = Lists.newArrayList();

    try {

      con = getManagedConnection();
      findProtocols = con.prepareStatement(SELECT_REQUEST_PROTOCOLS_WHERE_TOKEN_IS);

      findProtocols.setString(1, requestToken.getValue());

      log.trace("PtP CHUNK DAO - find method: {}", findProtocols);
      rsProtocols = findProtocols.executeQuery();

      List<String> protocols = Lists.newArrayList();
      while (rsProtocols.next()) {
        protocols.add(rsProtocols.getString("tp.config_ProtocolsID"));
      }

      // get chunks of the request
      findRequest = con.prepareStatement(SELECT_FULL_REQUEST_PUT_WHERE_TOKEN_AND_STATUS);
      findRequest.setString(1, requestToken.getValue());
      findRequest.setInt(2, statusCodeConverter.toDB(SRM_ABORTED));
      log.trace("PtP CHUNK DAO - find method: {}", findRequest);
      rsRequest = findRequest.executeQuery();

      while (rsRequest.next()) {
        PtPChunkDataTO chunkDataTO = new PtPChunkDataTO();
        chunkDataTO.setFileStorageType(rsRequest.getString("rq.config_FileStorageTypeID"));
        chunkDataTO.setOverwriteOption(rsRequest.getString("rq.config_OverwriteID"));
        chunkDataTO.setTimeStamp(rsRequest.getTimestamp("rq.timeStamp"));
        chunkDataTO.setPinLifetime(rsRequest.getInt("rq.pinLifetime"));
        chunkDataTO.setFileLifetime(rsRequest.getInt("rq.fileLifetime"));
        chunkDataTO.setSpaceToken(rsRequest.getString("rq.s_token"));
        chunkDataTO.setClientDN(rsRequest.getString("rq.client_dn"));

        /**
         * This code is only for the 1.3.18. This is a workaround to get FQANs using the proxy field
         * on request_queue. The FE use the proxy field of request_queue to insert a single FQAN
         * string containing all FQAN separated by the "#" char. The proxy is a BLOB, hence it has
         * to be properly converted in string.
         */
        java.sql.Blob blob = rsRequest.getBlob("rq.proxy");
        if (!rsRequest.wasNull() && blob != null) {
          byte[] bdata = blob.getBytes(1, (int) blob.length());
          chunkDataTO.setVomsAttributes(new String(bdata));
        }
        chunkDataTO.setPrimaryKey(rsRequest.getLong("rp.ID"));
        chunkDataTO.setToSURL(rsRequest.getString("rp.targetSURL"));

        chunkDataTO.setNormalizedStFN(rsRequest.getString("rp.normalized_targetSURL_StFN"));
        int uniqueID = rsRequest.getInt("rp.targetSURL_uniqueID");
        if (!rsRequest.wasNull()) {
          chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
        }

        chunkDataTO.setExpectedFileSize(rsRequest.getLong("rp.expectedFileSize"));
        chunkDataTO.setProtocolList(protocols);
        chunkDataTO.setRequestToken(requestToken.getValue());
        chunkDataTO.setStatus(rsRequest.getInt("sp.statusCode"));
        results.add(chunkDataTO);
      }
      con.commit();
    } catch (SQLException e) {
      log.error("PTP CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rsProtocols);
      closeResultSet(rsRequest);
      closeStatement(findProtocols);
      closeStatement(findRequest);
      closeConnection(con);
    }
    return results;
  }

  /**
   * Method used in extraordinary situations to signal that data retrieved from the DB was malformed
   * and could not be translated into the StoRM object model. This method attempts to change the
   * status of the chunk to SRM_FAILURE and record it in the DB, in the status_Put table. This
   * operation could potentially fail because the source of the malformed problems could be a
   * problematic DB; indeed, initially only log messages were recorded. Yet it soon became clear
   * that the source of malformed data were actually the clients themselves and/or FE recording in
   * the DB. In these circumstances the client would find its request as being in the
   * SRM_IN_PROGRESS state for ever. Hence the pressing need to inform it of the encountered
   * problems.
   */
  public synchronized int fail(PtPChunkDataTO auxTO) {

    Connection con = null;
    PreparedStatement signal = null;
    int updated = 0;

    try {
      con = getConnection();
      signal = con.prepareStatement(UPDATE_STATUS_PUT_WHERE_ID_IS);
      signal.setInt(1, statusCodeConverter.toDB(SRM_FAILURE));
      signal.setString(2, "This chunk of the request is malformed!");
      signal.setLong(3, auxTO.primaryKey());
      log.trace("PtP CHUNK DAO - signalMalformedPtPChunk method: {}", signal);
      updated = signal.executeUpdate();
    } catch (SQLException e) {
      log.error(
          "PtPChunkDAO! Unable to signal in DB that a chunk of "
              + "the request was malformed! Request: {}; Error: {}",
          auxTO.toString(), e.getMessage(), e);
      e.printStackTrace();
      updated = 0;
    } finally {
      closeStatement(signal);
      closeConnection(con);
    }
    return updated;
  }

  /**
   * Method that retrieves all expired requests in SRM_SPACE_AVAILABLE state.
   * 
   * @return a Map containing the ID of the request as key and the relative SURL as value
   */
  public synchronized Map<Long, String> getExpiredSRM_SPACE_AVAILABLE() {

    return getExpired(SRM_SPACE_AVAILABLE);
  }

  public synchronized Map<Long, String> getExpired(TStatusCode status) {

    Map<Long, String> expiredRequests = Maps.newHashMap();

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;

    try {

      con = getConnection();
      find = con.prepareStatement(SELECT_EXPIRED_REQUEST_PUT_WHERE_STATUS_IS);
      find.setInt(1, statusCodeConverter.toDB(status));
      log.trace("PtP CHUNK DAO - getExpiredSRM_SPACE_AVAILABLE: {}", find);
      rs = find.executeQuery();
      while (rs.next()) {
        expiredRequests.put(rs.getLong("rp.ID"), rs.getString("rp.targetSURL"));
      }

    } catch (SQLException e) {

      log.error("PtPChunkDAO! Unable to select expired "
          + "SRM_SPACE_AVAILABLE chunks of PtP requests. {}", e.getMessage(), e);
      e.printStackTrace();

    } finally {

      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return expiredRequests;
  }

  /**
   * Method that updates chunks in SRM_SPACE_AVAILABLE state, into SRM_FILE_LIFETIME_EXPIRED. An
   * array of Long representing the primary key of each chunk is required. This is needed when the
   * client forgets to invoke srmPutDone(). In case of any error or exception, the returned int
   * value will be zero or less than the input List size.
   * 
   * @param the list of the request id to update
   * 
   * @return The number of the updated records into the db
   */
  public synchronized int transitExpiredSRM_SPACE_AVAILABLEtoSRM_FILE_LIFETIME_EXPIRED(
      Collection<Long> ids) {

    Preconditions.checkNotNull(ids, "Invalid list of id");

    String querySQL = "UPDATE status_Put sp "
        + "JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
        + "SET sp.statusCode=?, sp.explanation=? "
        + "WHERE sp.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

    if (!ids.isEmpty()) {
      querySQL += "AND rp.ID IN (" + StringUtils.join(ids.toArray(), ',') + ")";
    }

    Connection con = null;
    PreparedStatement stmt = null;
    int count = 0;

    try {
      con = getConnection();
      stmt = con.prepareStatement(querySQL);
      stmt.setInt(1, statusCodeConverter.toDB(SRM_FILE_LIFETIME_EXPIRED));
      stmt.setString(2, "Expired pinLifetime");
      stmt.setInt(3, statusCodeConverter.toDB(SRM_SPACE_AVAILABLE));

      log.trace("PtP CHUNK DAO - transit SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED: {}",
          stmt);

      count = stmt.executeUpdate();

    } catch (SQLException e) {
      log.error("PtPChunkDAO! Unable to transit chunks from "
          + "SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED! {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
    log.trace("PtPChunkDAO! {} chunks of PtP requests were transited "
        + "from SRM_SPACE_AVAILABLE to SRM_FILE_LIFETIME_EXPIRED.", count);
    return count;
  }

  public synchronized int transitLongTimeInProgressRequestsToStatus(long expirationTime, TStatusCode status, String explanation) {

    String sql = "UPDATE request_queue rq, request_Put rp, status_Put sp "
        + "SET rq.status=?, sp.statusCode=?, sp.explanation=? "
        + "WHERE rq.ID = rp.request_queueID and rp.ID = sp.request_PutID "
        + "AND rq.status=? AND rq.timeStamp <= DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL ? SECOND)";

    Connection con = null;
    PreparedStatement stmt = null;
    int count = 0;

    try {
      con = getConnection();
      stmt = con.prepareStatement(sql);
      stmt.setInt(1, statusCodeConverter.toDB(status));
      stmt.setInt(2, statusCodeConverter.toDB(status));
      stmt.setString(3, explanation);
      stmt.setInt(4, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      stmt.setLong(5, expirationTime);
      log.trace("PtP CHUNK DAO - transit SRM_REQUEST_INPROGRESS to {}: {}", status, stmt);
      count = stmt.executeUpdate();
 
    } catch (SQLException e) {
      log.error("PtPChunkDAO! Unable to transit chunks from "
          + "SRM_REQUEST_INPROGRESS to SRM_FAILURE! {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  return count;
  }

  public synchronized int updateStatus(Collection<Long> ids, TStatusCode fromStatus,
      TStatusCode toStatus, String explanation) {

    Preconditions.checkNotNull(ids, "Invalid list of id");

    if (ids.isEmpty()) {
      return 0;
    }

    String querySQL = "UPDATE request_queue rq, request_Put rp, status_Put sp "
        + "SET rq.status=?, sp.statusCode=?, sp.explanation=? "
        + "WHERE rq.ID = rp.request_queueID and rp.ID = sp.request_PutID "
        + "AND rq.status=? AND rq.ID IN (" + buildInClauseForArray(ids.size()) + ")";

    Connection con = null;
    PreparedStatement stmt = null;
    int count = 0;

    try {
      con = getConnection();
      stmt = con.prepareStatement(querySQL);
      stmt.setInt(1, statusCodeConverter.toDB(toStatus));
      stmt.setInt(2, statusCodeConverter.toDB(toStatus));
      stmt.setString(3, explanation);
      stmt.setInt(4, statusCodeConverter.toDB(fromStatus));
      int i = 5;
      for (Long id : ids) {
        stmt.setLong(i, id);
        i++;
      }
      log.trace("PtP CHUNK DAO - transit SRM_REQUEST_INPROGRESS to SRM_FAILURE: {}", stmt);
      count = stmt.executeUpdate();

    } catch (SQLException e) {
      log.error("PtPChunkDAO! Unable to transit chunks from "
          + "SRM_REQUEST_INPROGRESS to SRM_FAILURE! {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
    log.trace("PtPChunkDAO! {} chunks of PtP requests were transited "
        + "from SRM_REQUEST_INPROGRESS to SRM_FAILURE.", count);
    return count;
  }

  public synchronized int updateStatus(TRequestToken requestToken, int[] surlsUniqueIDs,
      String[] surls, TStatusCode statusCode, String explanation) {

    if (requestToken == null || requestToken.getValue().trim().isEmpty() || explanation == null) {
      throw new IllegalArgumentException("Unable to perform the updateStatus, "
          + "invalid arguments: requestToken=" + requestToken + " explanation=" + explanation);
    }
    return doUpdateStatus(requestToken, surlsUniqueIDs, surls, statusCode, explanation, true, true);
  }

  private int doUpdateStatus(TRequestToken requestToken, int[] surlsUniqueIDs, String[] surls,
      TStatusCode statusCode, String explanation, boolean withRequestToken,
      boolean withExplaination) throws IllegalArgumentException {

    if ((withRequestToken && requestToken == null) || (withExplaination && explanation == null)) {
      throw new IllegalArgumentException(
          "Unable to perform the updateStatus, " + "invalid arguments: withRequestToken="
              + withRequestToken + " requestToken=" + requestToken + " withExplaination="
              + withExplaination + " explaination=" + explanation);
    }

    String str =
        "UPDATE status_Put sp JOIN (request_Put rp, request_queue rq) ON sp.request_PutID=rp.ID AND "
            + "rp.request_queueID=rq.ID " + "SET sp.statusCode=? ";
    if (withExplaination) {
      str += " , " + buildExpainationSet(explanation);
    }
    str += " WHERE ";
    if (withRequestToken) {
      str += buildTokenWhereClause(requestToken) + " AND ";
    }
    str += " ( rp.targetSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
        + " AND rp.targetSURL IN " + makeSurlString(surls) + " ) ";

    Connection con = null;
    PreparedStatement stmt = null;
    int count = 0;

    try {
      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(statusCode));

      log.trace("PTP CHUNK DAO - updateStatus: {}", stmt);
      count = stmt.executeUpdate();
      if (count == 0) {
        log.trace("PTP CHUNK DAO! No chunk of PTP request was updated to {}.", statusCode);
      } else {
        log.info("PTP CHUNK DAO! {} chunks of PTP requests were updated " + "to {}.", count,
            statusCode);
      }
    } catch (SQLException e) {
      log.error("PTP CHUNK DAO! Unable to updated from to {}! {}", statusCode, e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
    return count;
  }

  public synchronized int updateStatusOnMatchingStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation) {

    if (requestToken == null || requestToken.getValue().trim().isEmpty() || explanation == null) {
      throw new IllegalArgumentException("Unable to perform the updateStatusOnMatchingStatus, "
          + "invalid arguments: requestToken=" + requestToken + " explanation=" + explanation);
    }
    return doUpdateStatusOnMatchingStatus(requestToken, null, null, expectedStatusCode,
        newStatusCode, explanation, true, false, true);
  }

  public synchronized int updateStatusOnMatchingStatus(TRequestToken requestToken,
      int[] surlsUniqueIDs, String[] surls, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode) {

    if (requestToken == null || requestToken.getValue().trim().isEmpty() || surlsUniqueIDs == null
        || surls == null || surlsUniqueIDs.length == 0 || surls.length == 0
        || surlsUniqueIDs.length != surls.length) {
      throw new IllegalArgumentException("Unable to perform the updateStatusOnMatchingStatus, "
          + "invalid arguments: requestToken=" + requestToken + "surlsUniqueIDs=" + surlsUniqueIDs
          + " surls=" + surls);
    }
    return doUpdateStatusOnMatchingStatus(requestToken, surlsUniqueIDs, surls, expectedStatusCode,
        newStatusCode, null, true, true, false);
  }

  private int doUpdateStatusOnMatchingStatus(TRequestToken requestToken, int[] surlsUniqueIDs,
      String[] surls, TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation,
      boolean withRequestToken, boolean withSurls, boolean withExplanation) {

    if ((withRequestToken && requestToken == null) || (withExplanation && explanation == null)
        || (withSurls && (surlsUniqueIDs == null || surls == null))) {
      throw new IllegalArgumentException("Unable to perform the doUpdateStatusOnMatchingStatus, "
          + "invalid arguments: withRequestToken=" + withRequestToken + " requestToken="
          + requestToken + " withSurls=" + withSurls + " surlsUniqueIDs=" + surlsUniqueIDs
          + " surls=" + surls + " withExplaination=" + withExplanation + " explanation="
          + explanation);
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

    Connection con = null;
    PreparedStatement stmt = null;
    int count = 0;

    try {
      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(newStatusCode));
      stmt.setInt(2, statusCodeConverter.toDB(expectedStatusCode));
      log.trace("PTP CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt);
      count = stmt.executeUpdate();
      if (count == 0) {
        log.trace("PTP CHUNK DAO! No chunk of PTP request was updated " + "from {} to {}.",
            expectedStatusCode, newStatusCode);
      } else {
        log.debug("PTP CHUNK DAO! {} chunks of PTP requests were updated " + "from {} to {}.",
            count, expectedStatusCode, newStatusCode);
      }
    } catch (SQLException e) {
      log.error("PTP CHUNK DAO! Unable to updated from {} to {}! Error: {}", expectedStatusCode,
          newStatusCode, e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
    return count;
  }

  public Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray, String dn) {

    if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0 || surlsArray == null
        || surlsArray.length == 0 || dn == null) {
      throw new IllegalArgumentException(
          "Unable to perform the find, " + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
              + " surlsArray=" + surlsArray + " dn=" + dn);
    }
    return find(surlsUniqueIDs, surlsArray, dn, true);
  }

  private synchronized Collection<PtPChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray,
      String dn, boolean withDn) throws IllegalArgumentException {

    if ((withDn && dn == null) || surlsUniqueIDs == null || surlsUniqueIDs.length == 0
        || surlsArray == null || surlsArray.length == 0) {
      throw new IllegalArgumentException(
          "Unable to perform the find, " + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
              + " surlsArray=" + surlsArray + " withDn=" + withDn + " dn=" + dn);
    }

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;

    try {
      // get chunks of the request
      String str =
          "SELECT rq.ID, rq.r_token, rq.config_FileStorageTypeID, rq.config_OverwriteID, rq.timeStamp, rq.pinLifetime, rq.fileLifetime, "
              + "rq.s_token, rq.client_dn, rq.proxy, rp.ID, rp.targetSURL, rp.expectedFileSize, rp.normalized_targetSURL_StFN, rp.targetSURL_uniqueID, "
              + "sp.statusCode " + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
              + "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
              + "WHERE ( rp.targetSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
              + " AND rp.targetSURL IN " + makeSurlString(surlsArray) + " )";

      if (withDn) {
        str += " AND rq.client_dn=\'" + dn + "\'";
      }

      con = getConnection();
      find = con.prepareStatement(str);

      List<PtPChunkDataTO> list = Lists.newArrayList();

      log.trace("PtP CHUNK DAO - find method: {}", find);
      rs = find.executeQuery();

      while (rs.next()) {

        PtPChunkDataTO chunkDataTO = new PtPChunkDataTO();
        chunkDataTO.setFileStorageType(rs.getString("rq.config_FileStorageTypeID"));
        chunkDataTO.setOverwriteOption(rs.getString("rq.config_OverwriteID"));
        chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
        chunkDataTO.setPinLifetime(rs.getInt("rq.pinLifetime"));
        chunkDataTO.setFileLifetime(rs.getInt("rq.fileLifetime"));
        chunkDataTO.setSpaceToken(rs.getString("rq.s_token"));
        chunkDataTO.setClientDN(rs.getString("rq.client_dn"));

        /**
         * This code is only for the 1.3.18. This is a workaround to get FQANs using the proxy field
         * on request_queue. The FE use the proxy field of request_queue to insert a single FQAN
         * string containing all FQAN separated by the "#" char. The proxy is a BLOB, hence it has
         * to be properly converted in string.
         */
        java.sql.Blob blob = rs.getBlob("rq.proxy");
        if (!rs.wasNull() && blob != null) {
          byte[] bdata = blob.getBytes(1, (int) blob.length());
          chunkDataTO.setVomsAttributes(new String(bdata));
        }
        chunkDataTO.setPrimaryKey(rs.getLong("rp.ID"));
        chunkDataTO.setToSURL(rs.getString("rp.targetSURL"));

        chunkDataTO.setNormalizedStFN(rs.getString("rp.normalized_targetSURL_StFN"));
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
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
  }

  private String buildExpainationSet(String explanation) {

    return " sp.explanation='" + explanation + "' ";
  }

  private String buildTokenWhereClause(TRequestToken requestToken) {

    return " rq.r_token='" + requestToken.toString() + "' ";
  }

  private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls) {

    return " ( rp.targetSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
        + " AND rp.targetSURL IN " + makeSurlString(surls) + " ) ";
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