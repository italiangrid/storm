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

import static it.grid.storm.srm.types.TRequestType.BRING_ON_LINE;
import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_RELEASED;
import static it.grid.storm.srm.types.TStatusCode.SRM_REQUEST_INPROGRESS;
import static it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.persistence.converter.RequestTypeConverter;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.BoLChunkDAO;
import it.grid.storm.persistence.model.BoLChunkDataTO;
import it.grid.storm.persistence.model.ReducedBoLChunkDataTO;
import it.grid.storm.persistence.pool.StormDbConnectionPool;
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

  private static final String SELECT_FULL_BOL_REQUEST_WITH_TOKEN =
      "SELECT sb.statusCode, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID "
          + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
          + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) " + "WHERE rq.r_token=?";

  private static final String INSERT_INTO_REQUEST_QUEUE =
      "INSERT INTO request_queue (config_RequestTypeID,client_dn,pinLifetime,status,errstring,r_token,nbreqfiles,timeStamp,deferredStartTime) "
          + "VALUES (?,?,?,?,?,?,?,?,?)";

  private static final String INSERT_INTO_REQUEST_TRANSFER_PROTOCOLS =
      "INSERT INTO request_TransferProtocols (request_queueID,config_ProtocolsID) VALUES (?,?)";

  private static final String INSERT_INTO_REQUEST_DIR_OPTION =
      "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) VALUES (?,?,?)";

  private static final String INSERT_INTO_REQUEST_BOL =
      "INSERT INTO request_BoL (request_DirOptionID,request_queueID,sourceSURL,normalized_sourceSURL_StFN,sourceSURL_uniqueID) "
          + "VALUES (?,?,?,?,?)";

  private static final String UPDATE_REQUEST_BOL_WHERE_ID =
      "UPDATE request_BoL SET normalized_sourceSURL_StFN=?, sourceSURL_uniqueID=? " + "WHERE ID=?";

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

  private static BoLChunkDAOMySql instance;

  public static synchronized BoLChunkDAO getInstance() {
    if (instance == null) {
      instance = new BoLChunkDAOMySql();
    }
    return instance;
  }

  private final StatusCodeConverter statusCodeConverter;
  private final RequestTypeConverter requestTypeConverter;

  private BoLChunkDAOMySql() {
    super(StormDbConnectionPool.getInstance());
    statusCodeConverter = StatusCodeConverter.getInstance();
    requestTypeConverter = RequestTypeConverter.getInstance();
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
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(res);
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * Method used to add a new record to the DB: the supplied BoLChunkDataTO gets its primaryKey
   * changed to the one assigned by the DB. The client_dn must also be supplied as a String. The
   * supplied BoLChunkData is used to fill in all the DB tables where file specific info gets
   * recorded: it _adds_ a new request!
   */
  public synchronized void addNew(BoLChunkDataTO to, String client_dn) {

    final String DESCRIPTION = "New BoL Request resulting from srmCopy invocation.";

    /* Result set containing the ID of the inserted new request */
    ResultSet rs = null;
    PreparedStatement addReqQ = null;
    PreparedStatement addReqTP = null;
    Connection con = null;

    try {
      // begin transaction

      con = getManagedConnection();

      // add to request_queue...
      addReqQ = con.prepareStatement(INSERT_INTO_REQUEST_QUEUE, RETURN_GENERATED_KEYS);
      /* request type set to bring online */
      addReqQ.setString(1, requestTypeConverter.toDB(BRING_ON_LINE));
      addReqQ.setString(2, client_dn);
      addReqQ.setInt(3, to.getLifeTime());
      addReqQ.setInt(4, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      addReqQ.setString(5, DESCRIPTION);
      addReqQ.setString(6, to.getRequestToken());
      addReqQ.setInt(7, 1); // number of requested files set to 1!
      addReqQ.setTimestamp(8, new Timestamp(new Date().getTime()));
      addReqQ.setInt(9, to.getDeferredStartTime());
      log.trace("BoL CHUNK DAO: addNew; {}", addReqQ);
      addReqQ.execute();

      rs = addReqQ.getGeneratedKeys();
      int id_new = extractID(rs);

      addReqTP = con.prepareStatement(INSERT_INTO_REQUEST_TRANSFER_PROTOCOLS);
      for (Iterator<String> i = to.getProtocolList().iterator(); i.hasNext();) {
        addReqTP.setInt(1, id_new);
        addReqTP.setString(2, i.next());
        log.trace("BoL CHUNK DAO: addNew; {}", addReqTP);
        addReqTP.execute();
      }

      // addChild...
      int id_s = fillBoLTables(con, to, id_new);

      // end transaction!
      con.commit();

      // update primary key reading the generated key
      to.setPrimaryKey(id_s);
    } catch (Exception e) {
      log.error("BoL CHUNK DAO: unable to complete addNew! BoLChunkDataTO: {}; "
          + "exception received: {}", to, e.getMessage(), e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rs);
      closeStatement(addReqQ);
      closeStatement(addReqTP);
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
      e.printStackTrace();
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
          chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
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
      e.printStackTrace();
      return Lists.newArrayList();

    } finally {
      closeResultSet(prs);
      closeResultSet(rrs);
      closeStatement(pps);
      closeStatement(rps);
      closeConnection(con);
    }
  }

  /**
   * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given TRequestToken
   * expressed as String.
   */
  public synchronized Collection<ReducedBoLChunkDataTO> findReduced(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    List<ReducedBoLChunkDataTO> results = Lists.newArrayList();

    try {

      con = getConnection();

      ps = con.prepareStatement(SELECT_FULL_BOL_REQUEST_WITH_TOKEN);
      ps.setString(1, requestToken.getValue());
      log.trace("BoL CHUNK DAO! findReduced with request token; {}", ps);
      rs = ps.executeQuery();

      ReducedBoLChunkDataTO chunkDataTO = null;
      while (rs.next()) {
        chunkDataTO = new ReducedBoLChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
        chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
        chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
        int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          chunkDataTO.setSurlUniqueID(uniqueID);
        }
        results.add(chunkDataTO);
      }
      return results;

    } catch (SQLException e) {

      log.error("BOL CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
      return results;

    } finally {
      closeResultSet(rs);
      closeStatement(ps);
      closeConnection(con);
    }
  }

  /**
   * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given griduser, and
   * whose SURLs are contained in the supplied array of Strings.
   */
  public synchronized Collection<ReducedBoLChunkDataTO> findReduced(TRequestToken requestToken,
      int[] surlUniqueIDs, String[] surls) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;
    Collection<ReducedBoLChunkDataTO> results = Lists.newArrayList();

    try {

      con = getConnection();

      /*
       * NOTE: we search also on the fromSurl because otherwise we lost all request_Bol that have
       * not the uniqueID set because are not yet been used by anybody
       */
      // get reduced chunks
      String str =
          "SELECT sb.statusCode, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID "
              + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
              + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
              + "WHERE rq.r_token=? AND ( rb.sourceSURL_uniqueID IN "
              + makeSURLUniqueIDWhere(surlUniqueIDs) + " AND rb.sourceSURL IN "
              + makeSurlString(surls) + " ) ";
      find = con.prepareStatement(str);
      find.setString(1, requestToken.getValue());

      log.trace("BoL CHUNK DAO! findReduced with griduser+surlarray; {}", find);
      rs = find.executeQuery();

      ReducedBoLChunkDataTO chunkDataTO = null;
      while (rs.next()) {
        chunkDataTO = new ReducedBoLChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
        chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
        chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
        int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          chunkDataTO.setSurlUniqueID(uniqueID);
        }
        results.add(chunkDataTO);
      }
    } catch (SQLException e) {
      log.error("BoL CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return results;
  }

  /**
   * Method that returns a Collection of ReducedBoLChunkDataTO associated to the given griduser, and
   * whose SURLs are contained in the supplied array of Strings.
   */
  public synchronized Collection<ReducedBoLChunkDataTO> findReduced(String griduser,
      int[] surlUniqueIDs, String[] surls) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;

    Collection<ReducedBoLChunkDataTO> results = Lists.newArrayList();

    try {

      con = getConnection();

      /*
       * NOTE: we search also on the fromSurl because otherwise we lost all request_Bol that have
       * not the uniqueID set because are not yet been used by anybody
       */
      // get reduced chunks
      String str =
          "SELECT sb.statusCode, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, rb.sourceSURL_uniqueID "
              + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
              + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
              + "WHERE rq.client_dn=? AND ( rb.sourceSURL_uniqueID IN "
              + makeSURLUniqueIDWhere(surlUniqueIDs) + " AND rb.sourceSURL IN "
              + makeSurlString(surls) + " ) ";
      find = con.prepareStatement(str);
      find.setString(1, griduser);
      log.trace("BoL CHUNK DAO! findReduced with griduser+surlarray; {}", find);
      rs = find.executeQuery();

      while (rs.next()) {
        ReducedBoLChunkDataTO chunkDataTO = new ReducedBoLChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
        chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
        chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));
        int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          chunkDataTO.setSurlUniqueID(uniqueID);
        }
        results.add(chunkDataTO);
      }
    } catch (SQLException e) {
      log.error("BoL CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return results;
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
      e.printStackTrace();
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
      e.printStackTrace();

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
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }

    return count;
  }

  public Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray, String dn)
      throws IllegalArgumentException {

    if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0 || surlsArray == null
        || surlsArray.length == 0 || dn == null) {
      throw new IllegalArgumentException(
          "Unable to perform the find, " + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs
              + " surlsArray=" + surlsArray + " dn=" + dn);
    }
    return find(surlsUniqueIDs, surlsArray, dn, true);
  }

  public Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray)
      throws IllegalArgumentException {

    if (surlsUniqueIDs == null || surlsUniqueIDs.length == 0 || surlsArray == null
        || surlsArray.length == 0) {
      throw new IllegalArgumentException("Unable to perform the find, "
          + "invalid arguments: surlsUniqueIDs=" + surlsUniqueIDs + " surlsArray=" + surlsArray);
    }
    return find(surlsUniqueIDs, surlsArray, null, false);
  }

  private synchronized Collection<BoLChunkDataTO> find(int[] surlsUniqueIDs, String[] surlsArray,
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
    Collection<BoLChunkDataTO> results = Lists.newArrayList();

    try {

      con = getConnection();

      // get chunks of the request
      String str = "SELECT rq.ID, rq.r_token, sb.statusCode, rq.timeStamp, rq.pinLifetime, "
          + "rq.deferredStartTime, rb.ID, rb.sourceSURL, rb.normalized_sourceSURL_StFN, "
          + "rb.sourceSURL_uniqueID, d.isSourceADirectory, d.allLevelRecursive, d.numOfLevels "
          + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
          + "ON (rb.request_queueID=rq.ID AND sb.request_BoLID=rb.ID) "
          + "LEFT JOIN request_DirOption d ON rb.request_DirOptionID=d.ID "
          + "WHERE ( rb.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
          + " AND rb.sourceSURL IN " + makeSurlString(surlsArray) + " )";

      if (withDn) {
        str += " AND rq.client_dn=\'" + dn + "\'";
      }
      find = con.prepareStatement(str);

      log.trace("BOL CHUNK DAO - find method: {}", find);
      rs = find.executeQuery();

      while (rs.next()) {

        BoLChunkDataTO chunkDataTO = new BoLChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sb.statusCode"));
        chunkDataTO.setLifeTime(rs.getInt("rq.pinLifetime"));
        chunkDataTO.setDeferredStartTime(rs.getInt("rq.deferredStartTime"));
        chunkDataTO.setRequestToken(rs.getString("rq.r_token"));
        chunkDataTO.setTimeStamp(rs.getTimestamp("rq.timeStamp"));
        chunkDataTO.setPrimaryKey(rs.getLong("rb.ID"));
        chunkDataTO.setFromSURL(rs.getString("rb.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rs.getString("rb.normalized_sourceSURL_StFN"));

        int uniqueID = rs.getInt("rb.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          chunkDataTO.setSurlUniqueID(new Integer(uniqueID));
        }

        chunkDataTO.setDirOption(rs.getBoolean("d.isSourceADirectory"));
        chunkDataTO.setAllLevelRecursive(rs.getBoolean("d.allLevelRecursive"));
        chunkDataTO.setNumLevel(rs.getInt("d.numOfLevels"));

        results.add(chunkDataTO);
      }

    } catch (SQLException e) {

      log.error("BOL CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();

    } finally {

      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);

    }

    return results;
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
