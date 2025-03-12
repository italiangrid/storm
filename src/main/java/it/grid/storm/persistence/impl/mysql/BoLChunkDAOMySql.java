/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.impl.mysql;

import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_RELEASED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.BoLChunkDAO;
import it.grid.storm.persistence.model.BoLChunkDataTO;
import it.grid.storm.persistence.model.ReducedBoLChunkDataTO;
import it.grid.storm.persistence.pool.impl.StormDbConnectionPool;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TStatusCode;

/**
 * DAO class for BoLChunkCatalog. This DAO is specifically designed to connect to a MySQL DB. The
 * raw data found in those tables is pre-treated in order to turn it into the Object Model of StoRM.
 * See Method comments for further info. BEWARE! DAO Adjusts for extra fields in the DB that are not
 * present in the object model.
 * 
 * @author CNAF
 * @version 1.0
 * @date Aug 2009
 */
public class BoLChunkDAOMySql extends AbstractDAO implements BoLChunkDAO {

  private static final Logger log = LoggerFactory.getLogger(BoLChunkDAOMySql.class);

  private static final String SELECT_FROM_REQUEST_QUEUE_WITH_TOKEN =
      "SELECT rq.ID FROM request_queue rq WHERE rq.r_token=?";

  private static final String SELECT_FULL_BOL_REQUEST_WITH_TOKEN_AND_STATUS =
      "SELECT sb.statusCode, rq.timeStamp, rq.pinLifetime, rq.deferredStartTime, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "
          + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
          + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
          + "LEFT JOIN request_DirOption d ON rb.request_DirOptionID=d.ID "
          + "WHERE rq.r_token=? AND sb.statusCode<>?";

  private static final String INSERT_INTO_REQUEST_DIR_OPTION =
      "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";

  private static final String INSERT_INTO_REQUEST_BOL =
      "INSERT INTO request_BoL (request_DirOptionID,request_queueID,sourceSURL,normalized_sourceSURL_StFN,sourceSURL_uniqueID) "
          + "VALUES (?,?,?,?,?)";

  private static final String UPDATE_REQUEST_BOL_WHERE_ID =
      "UPDATE request_BoL SET normalized_sourceSURL_StFN=?, sourceSURL_uniqueID=? WHERE ID=?";

  private static final String INSERT_INTO_STATUS_BOL =
      "INSERT INTO status_BoL (request_BoLID,statusCode,explanation) VALUES (?,?,?)";

  private static final String UPDATE_REQUEST_QUEUE_WHERE_ID =
      "UPDATE request_queue rq JOIN (status_BoL sb, request_BoL rb) ON (rq.ID=rb.request_queueID AND sb.request_BoLID=rb.ID) "
          + "SET sb.fileSize=?, sb.statusCode=?, sb.explanation=?, rq.pinLifetime=?, rb.normalized_sourceSURL_StFN=?, rb.sourceSURL_uniqueID=? "
          + "WHERE rb.ID=?";

  private static final String SELECT_REQUEST_PROTOCOLS_WHERE_TOKEN = "SELECT tp.config_ProtocolsID "
      + "FROM request_TransferProtocols tp JOIN request_queue rq ON tp.request_queueID=rq.ID "
      + "WHERE rq.r_token=?";

  private static final String UPDATE_STATUS_WHERE_ID =
      "UPDATE status_BoL SET statusCode=?, explanation=? WHERE request_BoLID=?";

  private static final String UPDATE_STATUS_FOR_EXPIRED_PIN_REQUESTS_WITH_STATUS =
      "UPDATE status_BoL sb "
          + "JOIN (request_BoL rb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
          + "SET sb.statusCode=? "
          + "WHERE sb.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

  private static final String ABORT_EXPIRED_BOL_REQUESTS_INPROGRESS = "UPDATE status_BoL sb "
      + "JOIN (request_BoL rb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
      + "SET sb.statusCode=? "
      + "WHERE sb.statusCode=? AND rq.timeStamp <= DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL ? SECOND)";

  private static BoLChunkDAOMySql instance;

  public static synchronized BoLChunkDAO getInstance() {
    if (instance == null) {
      instance = new BoLChunkDAOMySql();
    }
    return instance;
  }

  private final StatusCodeConverter statusCodeConverter;

  private BoLChunkDAOMySql() {
    super(StormDbConnectionPool.getInstance());
    statusCodeConverter = StatusCodeConverter.getInstance();
  }

  /**
   * Method used to add a new record to the DB: the supplied BoLChunkDataTO gets its primaryKey
   * changed to the one assigned by the DB. The supplied BoLChunkData is used to fill in only the DB
   * table where file specific info gets recorded: it does _not_ add a new request! So if spurious
   * data is supplied, it will just stay there because of a lack of a parent request!
   */
  public synchronized void addChild(BoLChunkDataTO to) {

    Connection con = null;
    ResultSet res = null;
    PreparedStatement ps = null;

    try {
      con = getManagedConnection();

      /*
       * WARNING!!!! We are forced to run a query to get the ID of the request, which should NOT be
       * so because the corresponding request object should have been changed with the extra field!
       * However, it is not possible at the moment to perform such change because of strict deadline
       * and the change could wreak havoc the code. So we are forced to make this query!!!
       */

      ps = con.prepareStatement(SELECT_FROM_REQUEST_QUEUE_WITH_TOKEN);
      ps.setString(1, to.getRequestToken());
      log.debug("BoL CHUNK DAO: addChild; {}", ps);
      res = ps.executeQuery();

      /* ID of request in request_process! */
      int requestId = extractID(res);
      int id = fillBoLTables(con, to, requestId);

      // end transaction!
      con.commit();

      // update primary key reading the generated key
      to.setPrimaryKey(id);
    } catch (Exception e) {
      log.error("Got exception {}: {}", e.getClass(), e.getMessage());
      try {
        con.rollback();
      } catch (SQLException e1) {
        log.error("Got exception during rollback {}: {}", e1.getClass(), e1.getMessage());
      }
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
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
  private synchronized int fillBoLTables(Connection con, BoLChunkDataTO to, int requestQueueID)
      throws SQLException, Exception {

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

    try {
      // first fill in TDirOption
      addDirOption = con.prepareStatement(INSERT_INTO_REQUEST_DIR_OPTION, RETURN_GENERATED_KEYS);
      addDirOption.setBoolean(1, to.getDirOption());
      addDirOption.setBoolean(2, to.getAllLevelRecursive());
      addDirOption.setInt(3, to.getNumLevel());
      log.trace("BoL CHUNK DAO: addNew; {}", addDirOption);
      addDirOption.execute();

      rs_do = addDirOption.getGeneratedKeys();
      int id_do = extractID(rs_do);

      // second fill in request_BoL... sourceSURL and TDirOption!
      addBoL = con.prepareStatement(INSERT_INTO_REQUEST_BOL, RETURN_GENERATED_KEYS);
      addBoL.setInt(1, id_do);
      addBoL.setInt(2, requestQueueID);
      addBoL.setString(3, to.getFromSURL());
      addBoL.setString(4, to.normalizedStFN());
      addBoL.setInt(5, to.sulrUniqueID());
      log.trace("BoL CHUNK DAO: addNew; {}", addBoL);
      addBoL.execute();

      rs_b = addBoL.getGeneratedKeys();
      int id_g = extractID(rs_b);

      // third fill in status_BoL...
      addChild = con.prepareStatement(INSERT_INTO_STATUS_BOL, RETURN_GENERATED_KEYS);
      addChild.setInt(1, id_g);
      addChild.setInt(2, to.getStatus());
      addChild.setString(3, to.getErrString());
      log.trace("BoL CHUNK DAO: addNew; " + addChild);
      addChild.execute();

      return id_g;
    } finally {
      closeResultSet(rs_do);
      closeResultSet(rs_b);
      closeResultSet(rs_s);
      closeStatement(addDirOption);
      closeStatement(addBoL);
      closeStatement(addChild);
    }
  }

  /**
   * Method used to save the changes made to a retrieved BoLChunkDataTO, back into the MySQL DB.
   * Only the fileSize, statusCode and explanation, of status_BoL table are written to the DB.
   * Likewise for the request pinLifetime. In case of any error, an error message gets logged but no
   * exception is thrown.
   */
  public synchronized void update(BoLChunkDataTO to) {

    Connection con = null;
    PreparedStatement updateFileReq = null;
    try {
      con = getConnection();
      // ready updateFileReq...
      updateFileReq = con.prepareStatement(UPDATE_REQUEST_QUEUE_WHERE_ID);
      updateFileReq.setLong(1, to.getFileSize());
      updateFileReq.setInt(2, to.getStatus());
      updateFileReq.setString(3, to.getErrString());
      updateFileReq.setInt(4, to.getLifeTime());
      updateFileReq.setString(5, to.normalizedStFN());
      updateFileReq.setInt(6, to.sulrUniqueID());
      updateFileReq.setLong(7, to.getPrimaryKey());
      // execute update
      log.trace("BoL CHUNK DAO: update method; {}", updateFileReq);
      updateFileReq.executeUpdate();
    } catch (SQLException e) {
      log.error("BoL CHUNK DAO: Unable to complete update! {}", e.getMessage(), e);
    } finally {
      closeStatement(updateFileReq);
      closeConnection(con);
    }
  }

  /**
   * Updates the request_Bol represented by the received ReducedBoLChunkDataTO by setting its
   * normalized_sourceSURL_StFN and sourceSURL_uniqueID
   * 
   * @param chunkTO
   */
  public synchronized void updateIncomplete(ReducedBoLChunkDataTO chunkTO) {

    Connection con = null;
    PreparedStatement ps = null;
    try {
      con = getConnection();
      ps = con.prepareStatement(UPDATE_REQUEST_BOL_WHERE_ID);
      ps.setString(1, chunkTO.normalizedStFN());
      ps.setInt(2, chunkTO.surlUniqueID());
      ps.setLong(3, chunkTO.primaryKey());
      log.trace("BoL CHUNK DAO - update incomplete: {}", ps);
      ps.executeUpdate();
    } catch (SQLException e) {
      log.error("BoL CHUNK DAO: Unable to complete update incomplete! {}", e.getMessage(), e);
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * Method that queries the MySQL DB to find all entries matching the supplied TRequestToken. The
   * Collection contains the corresponding BoLChunkDataTO objects. An initial simple query
   * establishes the list of protocols associated with the request. A second complex query
   * establishes all chunks associated with the request, by properly joining request_queue,
   * request_BoL, status_BoL and request_DirOption. The considered fields are: (1) From status_BoL:
   * the ID field which becomes the TOs primary key, and statusCode. (2) From request_BoL:
   * sourceSURL (3) From request_queue: pinLifetime (4) From request_DirOption: isSourceADirectory,
   * alLevelRecursive, numOfLevels In case of any error, a log gets written and an empty collection
   * is returned. No exception is thrown. NOTE! Chunks in SRM_ABORTED status are NOT returned!
   */
  public synchronized Collection<BoLChunkDataTO> find(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement pps = null;
    PreparedStatement rps = null;
    ResultSet prs = null;
    ResultSet rrs = null;

    try {

      con = getConnection();
      pps = con.prepareStatement(SELECT_REQUEST_PROTOCOLS_WHERE_TOKEN);

      List<String> protocols = Lists.newArrayList();
      pps.setString(1, requestToken.getValue());
      log.trace("BoL CHUNK DAO: find method; {}", pps);
      prs = pps.executeQuery();

      while (prs.next()) {
        protocols.add(prs.getString("tp.config_ProtocolsID"));
      }

      rps = con.prepareStatement(SELECT_FULL_BOL_REQUEST_WITH_TOKEN_AND_STATUS);
      List<BoLChunkDataTO> results = Lists.newArrayList();
      rps.setString(1, requestToken.getValue());
      rps.setInt(2, statusCodeConverter.toDB(SRM_ABORTED));
      log.trace("BoL CHUNK DAO: find method; {}", rps);
      rrs = rps.executeQuery();

      while (rrs.next()) {

        BoLChunkDataTO chunkDataTO = new BoLChunkDataTO();
        chunkDataTO.setStatus(rrs.getInt("sb.statusCode"));
        chunkDataTO.setLifeTime(rrs.getInt("rq.pinLifetime"));
        chunkDataTO.setDeferredStartTime(rrs.getInt("rq.deferredStartTime"));
        chunkDataTO.setRequestToken(requestToken.getValue());
        chunkDataTO.setTimeStamp(rrs.getTimestamp("rq.timeStamp"));
        chunkDataTO.setPrimaryKey(rrs.getLong("rb.ID"));
        chunkDataTO.setFromSURL(rrs.getString("rb.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rrs.getString("rb.normalized_sourceSURL_StFN"));

        int uniqueID = rrs.getInt("rb.sourceSURL_uniqueID");
        if (!rrs.wasNull()) {
          chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
        }

        chunkDataTO.setDirOption(rrs.getBoolean("d.isSourceADirectory"));
        chunkDataTO.setAllLevelRecursive(rrs.getBoolean("d.allLevelRecursive"));
        chunkDataTO.setNumLevel(rrs.getInt("d.numOfLevels"));
        chunkDataTO.setProtocolList(protocols);
        results.add(chunkDataTO);
      }
      return results;

    } catch (SQLException e) {

      log.error("BOL CHUNK DAO: {}", e.getMessage(), e);
      return Lists.newArrayList();

    } finally {
      closeResultSet(prs);
      closeResultSet(rrs);
      closeStatement(pps);
      closeStatement(rps);
      closeConnection(con);
    }
  }

  public synchronized int updateStatus(BoLChunkDataTO to, TStatusCode status, String explanation) {

    Connection con = null;
    PreparedStatement ps = null;
    int result = 0;

    try {
      con = getConnection();
      ps = con.prepareStatement(UPDATE_STATUS_WHERE_ID);
      ps.setInt(1, statusCodeConverter.toDB(status));
      ps.setString(2, explanation);
      ps.setLong(3, to.getPrimaryKey());
      log.trace("BoL CHUNK DAO: update status {}", ps);
      result = ps.executeUpdate();

    } catch (SQLException e) {
      log.error("Got exception {}: {}", e.getClass(), e.getMessage());
    } finally {
      closeStatement(ps);
      closeConnection(con);
    }
    return result;
  }

  /**
   * Method that updates to SRM_RELEASED all the requests in SRM_SUCCESS status which have the
   * requested pin lifetime expired. This is necessary when the client forgets to invoke
   * srmReleaseFiles().
   * 
   * @return List of updated SURLs.
   */
  public synchronized int releaseExpiredAndSuccessfulRequests() {

    Connection con = null;
    PreparedStatement ps = null;

    int count = 0;

    try {

      // start transaction
      con = getConnection();

      /* Update status of all successful expired requests to SRM_RELEASED */
      ps = con.prepareStatement(UPDATE_STATUS_FOR_EXPIRED_PIN_REQUESTS_WITH_STATUS);
      ps.setInt(1, statusCodeConverter.toDB(SRM_RELEASED));
      ps.setInt(2, statusCodeConverter.toDB(SRM_SUCCESS));
      log.trace("BoL CHUNK DAO - transitExpiredSRM_SUCCESS method: {}", ps);

      count = ps.executeUpdate();

      if (count == 0) {
        log.trace(
            "BoLChunkDAO! No chunk of BoL request was transited from SRM_SUCCESS to SRM_RELEASED.");
      } else {
        log.info(
            "BoLChunkDAO! {} chunks of BoL requests were transited from SRM_SUCCESS to SRM_RELEASED.",
            count);
      }

    } catch (SQLException e) {

      log.error("BoLChunkDAO! SQLException.", e.getMessage(), e);

    } finally {

      closeStatement(ps);
      closeConnection(con);
    }
    return count;
  }

  public int abortInProgressRequestsSince(long expirationTimeInSeconds) {

    Connection con = null;
    PreparedStatement ps = null;

    int count = 0;

    try {

      // start transaction
      con = getConnection();

      ps = con.prepareStatement(ABORT_EXPIRED_BOL_REQUESTS_INPROGRESS);
      ps.setInt(1, statusCodeConverter.toDB(SRM_ABORTED));
      ps.setInt(2, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      ps.setLong(3, expirationTimeInSeconds);
      log.trace("BoL CHUNK DAO - transitExpiredSRM_SUCCESS method: {}", ps);

      count = ps.executeUpdate();

      if (count == 0) {
        log.trace(
            "BoLChunkDAO! No chunk of BoL request was transited from SRM_SUCCESS to SRM_RELEASED.");
      } else {
        log.info(
            "BoLChunkDAO! {} chunks of BoL requests were transited from SRM_SUCCESS to SRM_RELEASED.",
            count);
      }

    } catch (SQLException e) {

      log.error("BoLChunkDAO! SQLException.", e.getMessage(), e);

    } finally {

      closeStatement(ps);
      closeConnection(con);
    }
    return count;
  }


  public synchronized void updateStatusOnMatchingStatus(TRequestToken requestToken,
      TStatusCode expectedStatusCode, TStatusCode newStatusCode, String explanation) {

    if (requestToken == null || requestToken.getValue().trim().isEmpty() || explanation == null) {
      throw new IllegalArgumentException("Unable to perform the updateStatusOnMatchingStatus, "
          + "invalid arguments: requestToken=" + requestToken + " explanation=" + explanation);
    }
    doUpdateStatusOnMatchingStatus(requestToken, null, null, expectedStatusCode, newStatusCode,
        explanation, true, false, true);
  }

  private synchronized int doUpdateStatusOnMatchingStatus(TRequestToken requestToken,
      int[] surlUniqueIDs, String[] surls, TStatusCode expectedStatusCode,
      TStatusCode newStatusCode, String explanation, boolean withRequestToken, boolean withSurls,
      boolean withExplanation) throws IllegalArgumentException {

    if ((withRequestToken && requestToken == null) || (withExplanation && explanation == null)
        || (withSurls && (surlUniqueIDs == null || surls == null))) {
      throw new IllegalArgumentException("Unable to perform the doUpdateStatusOnMatchingStatus, "
          + "invalid arguments: withRequestToken=" + withRequestToken + " requestToken="
          + requestToken + " withSurls=" + withSurls + " surlUniqueIDs=" + surlUniqueIDs + " surls="
          + surls + " withExplaination=" + withExplanation + " explanation=" + explanation);
    }
    String str = "UPDATE status_BoL sb JOIN (request_BoL rb, request_queue rq) "
        + "ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID " + "SET sb.statusCode=? ";
    if (withExplanation) {
      str += " , " + buildExplanationSet(explanation);
    }
    str += " WHERE sb.statusCode=? ";
    if (withRequestToken) {
      str += " AND " + buildTokenWhereClause(requestToken);
    }
    if (withSurls) {
      str += " AND " + buildSurlsWhereClause(surlUniqueIDs, surls);
    }

    Connection con = null;
    PreparedStatement stmt = null;
    int count = 0;

    try {
      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(newStatusCode));
      stmt.setInt(2, statusCodeConverter.toDB(expectedStatusCode));
      log.trace("BOL CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt);
      count = stmt.executeUpdate();

    } catch (SQLException e) {

      log.error("BOL CHUNK DAO! Unable to updated from {} to {}!", expectedStatusCode,
          newStatusCode, e);

    } finally {

      closeStatement(stmt);
      closeConnection(con);

    }
    return count;
  }

  /**
   * Private method that returns the generated ID: it throws an exception in case of any problem!
   */
  private int extractID(ResultSet rs) throws Exception {

    if (rs == null) {
      throw new Exception("BoL CHUNK DAO! Null ResultSet!");
    }
    if (rs.next()) {
      return rs.getInt(1);
    }
    String msg =
        "BoL CHUNK DAO! It was not possible to establish the assigned autoincrement primary key!";
    log.error(msg);
    throw new Exception(msg);
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
   * Method that returns a String containing all SURLs.
   */
  private String makeSurlString(String[] surls) {

    StringBuilder sb = new StringBuilder("(");
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

  private String buildExplanationSet(String explanation) {

    return " sb.explanation='" + explanation + "' ";
  }

  private String buildTokenWhereClause(TRequestToken requestToken) {

    return " rq.r_token='" + requestToken.toString() + "' ";
  }

  private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls) {

    return " ( rb.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
        + " AND rb.sourceSURL IN " + makeSurlString(surls) + " ) ";
  }

}
