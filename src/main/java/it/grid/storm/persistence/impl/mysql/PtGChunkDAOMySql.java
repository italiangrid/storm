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

import static it.grid.storm.srm.types.TRequestType.PREPARE_TO_GET;
import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;
import static it.grid.storm.srm.types.TStatusCode.SRM_FAILURE;
import static it.grid.storm.srm.types.TStatusCode.SRM_FILE_PINNED;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.grid.storm.ea.StormEA;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.StoRI;
import it.grid.storm.namespace.naming.SURL;
import it.grid.storm.persistence.converter.RequestTypeConverter;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.PtGChunkDAO;
import it.grid.storm.persistence.model.PtGChunkDataTO;
import it.grid.storm.persistence.model.ReducedPtGChunkDataTO;
import it.grid.storm.persistence.pool.StormDbConnectionPool;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

/**
 * DAO class for PtGChunkCatalog. This DAO is specifically designed to connect to a MySQL DB. The
 * raw data found in those tables is pre-treated in order to turn it into the Object Model of StoRM.
 * See Method comments for further info.
 * 
 * BEWARE! DAO Adjusts for extra fields in the DB that are not present in the object model.
 * 
 * @author EGRID ICTP
 * @version 3.0
 * @date June 2005
 */
public class PtGChunkDAOMySql extends AbstractDAO implements PtGChunkDAO {

  private static final Logger log = LoggerFactory.getLogger(PtGChunkDAOMySql.class);

  private static final String SELECT_REQUEST_WHERE_TOKEN =
      "SELECT * FROM request_queue WHERE r_token=?";

  private static final String INSERT_REQUEST =
      "INSERT INTO request_queue (config_RequestTypeID,client_dn,pinLifetime,status,errstring,r_token,nbreqfiles,timeStamp) "
          + "VALUES (?,?,?,?,?,?,?,?)";

  private static final String INSERT_REQUEST_TRASNFER_PROTOCOL =
      "INSERT INTO request_TransferProtocols (request_queueID,config_ProtocolsID) "
          + "VALUES (?,?)";

  private static final String INSERT_REQUEST_DIR_OPTION =
      "INSERT INTO request_DirOption (isSourceADirectory,allLevelRecursive,numOfLevels) "
          + "VALUES (?,?,?)";

  private static final String INSERT_REQUEST_GET =
      "INSERT INTO request_Get (request_DirOptionID,request_queueID,sourceSURL,normalized_sourceSURL_StFN,sourceSURL_uniqueID) "
          + "VALUES (?,?,?,?,?)";

  private static final String INSERT_STATUS_GET =
      "INSERT INTO status_Get (request_GetID,statusCode,explanation) VALUES (?,?,?)";

  private static final String UPDATE_REQUEST_GET_STATUS_WHERE_ID =
      "UPDATE request_queue rq JOIN (status_Get sg, request_Get rg) ON (rq.ID=rg.request_queueID AND sg.request_GetID=rg.ID) "
          + "SET sg.fileSize=?, sg.transferURL=?, sg.statusCode=?, sg.explanation=?, rq.pinLifetime=?, rg.normalized_sourceSURL_StFN=?, rg.sourceSURL_uniqueID=? "
          + "WHERE rg.ID=?";

  private static final String UPDATE_REQUEST_GET_WHERE_ID =
      "UPDATE request_Get rg SET rg.normalized_sourceSURL_StFN=?, rg.sourceSURL_uniqueID=? "
          + "WHERE rg.ID=?";

  private static final String SELECT_STATUS_GET_WHERE_GET_ID =
      "SELECT statusCode, transferURL FROM status_Get WHERE request_GetID=?";

  private static final String SELECT_REQUEST_GET_PROTOCOLS_WHERE_TOKEN =
      "SELECT tp.config_ProtocolsID "
          + "FROM request_TransferProtocols tp JOIN request_queue rq ON tp.request_queueID=rq.ID "
          + "WHERE rq.r_token=?";

  private static final String SELECT_REQUEST_GET_WHERE_TOKEN_AND_STATUS =
      "SELECT sg.statusCode, rq.pinLifetime, rg.ID, rq.timeStamp, rq.client_dn, rq.proxy, rg.sourceSURL, "
          + "rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID, d.isSourceADirectory, "
          + "d.allLevelRecursive, d.numOfLevels "
          + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
          + "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
          + "LEFT JOIN request_DirOption d ON rg.request_DirOptionID=d.ID "
          + "WHERE rq.r_token=? AND sg.statusCode<>?";

  private static final String SELECT_REQUEST_GET_WHERE_TOKEN =
      "SELECT sg.statusCode, rg.ID, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID "
          + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
          + "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) " + "WHERE rq.r_token=?";

  private static final String UPDATE_STATUS_GET_WHERE_REQUEST_GET_ID_IS =
      "UPDATE status_Get SET statusCode=?, explanation=? WHERE request_GetID=?";

  private static final String COUNT_REQUEST_ON_SURL_WITH_STATUS =
      "SELECT COUNT(rg.ID) FROM status_Get sg JOIN request_Get rg "
          + "ON (sg.request_GetID=rg.ID) WHERE rg.sourceSURL_uniqueID=? AND sg.statusCode=?";

  private static final String SELECT_EXPIRED_REQUESTS =
      "SELECT rg.sourceSURL , rg.sourceSURL_uniqueID "
          + "FROM request_Get rg JOIN (status_Get sg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
          + "WHERE sg.statusCode=?"
          + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime";

  private static final String UPDATE_STATUS_OF_EXPIRED_REQUESTS =
      "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
          + "SET sg.statusCode=? "
          + "WHERE sg.statusCode=? AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) >= rq.pinLifetime ";

  private static final String SELECT_PTG_PINNED_SURLS =
      "SELECT rg.sourceSURL , rg.sourceSURL_uniqueID FROM "
          + "request_Get rg JOIN (status_Get sg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
          + "WHERE sg.statusCode=?"
          + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) < rq.pinLifetime ";

  private static final String SELECT_BOL_PINNED_SURLS =
      "SELECT rb.sourceSURL , rb.sourceSURL_uniqueID FROM "
          + "request_BoL rb JOIN (status_BoL sb, request_queue rq) ON sb.request_BoLID=rb.ID AND rb.request_queueID=rq.ID "
          + "WHERE sb.statusCode=?"
          + " AND UNIX_TIMESTAMP(NOW())-UNIX_TIMESTAMP(rq.timeStamp) < rq.pinLifetime ";

  private static PtGChunkDAOMySql instance;

  public static synchronized PtGChunkDAO getInstance() {
    if (instance == null) {
      instance = new PtGChunkDAOMySql();
    }
    return instance;
  }

  private final RequestTypeConverter requestTypeConverter;
  private final StatusCodeConverter statusCodeConverter;

  private PtGChunkDAOMySql() {

    super(StormDbConnectionPool.getInstance());
    requestTypeConverter = RequestTypeConverter.getInstance();
    statusCodeConverter = StatusCodeConverter.getInstance();
  }

  /**
   * Method used to add a new record to the DB: the supplied PtGChunkDataTO gets its primaryKey
   * changed to the one assigned by the DB.
   * 
   * The supplied PtGChunkData is used to fill in only the DB table where file specific info gets
   * recorded: it does _not_ add a new request! So if spurious data is supplied, it will just stay
   * there because of a lack of a parent request!
   */
  public synchronized void addChild(PtGChunkDataTO to) {

    Connection con = null;
    PreparedStatement id = null;
    ResultSet rsid = null;

    try {

      // WARNING!!!! We are forced to run a query to get the ID of the request,
      // which should NOT be so
      // because the corresponding request object should have been changed with
      // the extra field! However, it is not possible
      // at the moment to perform such chage because of strict deadline and the
      // change could wreak havoc
      // the code. So we are forced to make this query!!!

      con = getManagedConnection();
      id = con.prepareStatement(SELECT_REQUEST_WHERE_TOKEN);
      id.setString(1, to.requestToken());
      log.debug("PTG CHUNK DAO: addChild; {}", id);
      rsid = id.executeQuery();

      if (rsid.next()) {

        int requestId = rsid.getInt("ID");
        int id_s = fillPtGTables(con, to, requestId);
        con.commit();
        to.setPrimaryKey(id_s);

      } else {
        log.error("Unable to find queued request for token {}", to.requestToken());
        con.rollback();
      }

    } catch (SQLException e) {
      log.error("PTG CHUNK DAO: unable to complete addChild! " + "PtGChunkDataTO: {}; error: {}",
          to, e.getMessage(), e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rsid);
      closeStatement(id);
      closeConnection(con);
    }
  }

  /**
   * Method used to add a new record to the DB: the supplied PtGChunkDataTO gets its primaryKey
   * changed to the one assigned by the DB. The client_dn must also be supplied as a String.
   * 
   * The supplied PtGChunkData is used to fill in all the DB tables where file specific info gets
   * recorded: it _adds_ a new request!
   */
  public synchronized void addNew(PtGChunkDataTO to, String clientDn) {

    Connection con = null;
    ResultSet rsNew = null;
    PreparedStatement addNew = null;
    PreparedStatement addProtocols = null;

    try {

      con = getManagedConnection();

      addNew = con.prepareStatement(INSERT_REQUEST, RETURN_GENERATED_KEYS);
      addNew.setString(1, requestTypeConverter.toDB(PREPARE_TO_GET));
      addNew.setString(2, clientDn);
      addNew.setInt(3, to.lifeTime());
      addNew.setInt(4, statusCodeConverter.toDB(SRM_REQUEST_INPROGRESS));
      addNew.setString(5, "New PtG Request resulting from srmCopy invocation.");
      addNew.setString(6, to.requestToken());
      addNew.setInt(7, 1); // number of requested files set to 1!
      addNew.setTimestamp(8, new Timestamp(new Date().getTime()));
      log.trace("PTG CHUNK DAO: addNew; {}", addNew);
      addNew.execute();

      rsNew = addNew.getGeneratedKeys();

      if (!rsNew.next()) {
        log.error("Unable to insert new request");
        con.rollback();
        return;
      }
      int idNew = rsNew.getInt(1);

      // add protocols...
      addProtocols = con.prepareStatement(INSERT_REQUEST_TRASNFER_PROTOCOL);
      for (Iterator<String> i = to.protocolList().iterator(); i.hasNext();) {
        addProtocols.setInt(1, idNew);
        addProtocols.setString(2, i.next());
        log.trace("PTG CHUNK DAO: addNew; {}", addProtocols);
        addProtocols.execute();
      }

      // addChild...
      int id = fillPtGTables(con, to, idNew);

      // end transaction!
      con.commit();

      // update primary key reading the generated key
      to.setPrimaryKey(id);

    } catch (SQLException e) {
      log.error("PTG CHUNK DAO: Rolling back! Unable to complete addNew! "
          + "PtGChunkDataTO: {}; error: {}", to, e.getMessage(), e);
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeResultSet(rsNew);
      closeStatement(addNew);
      closeStatement(addProtocols);
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
  private synchronized int fillPtGTables(Connection con, PtGChunkDataTO to, int requestQueueID)
      throws SQLException {

    ResultSet rsDo = null;
    ResultSet rsG = null;
    ResultSet rsS = null;
    PreparedStatement addDirOption = null;
    PreparedStatement addGet = null;
    PreparedStatement addChild = null;

    try {

      // first fill in TDirOption
      addDirOption = con.prepareStatement(INSERT_REQUEST_DIR_OPTION, RETURN_GENERATED_KEYS);
      addDirOption.setBoolean(1, to.dirOption());
      addDirOption.setBoolean(2, to.allLevelRecursive());
      addDirOption.setInt(3, to.numLevel());
      log.trace("PTG CHUNK DAO: addNew; {}", addDirOption);
      addDirOption.execute();

      rsDo = addDirOption.getGeneratedKeys();

      if (!rsDo.next()) {
        throw new SQLException("Unable to get dir_option id");
      }
      int idDo = rsDo.getInt(1);

      // second fill in request_Get... sourceSURL and TDirOption!
      addGet = con.prepareStatement(INSERT_REQUEST_GET, RETURN_GENERATED_KEYS);
      addGet.setInt(1, idDo);
      addGet.setInt(2, requestQueueID);
      addGet.setString(3, to.fromSURL());
      addGet.setString(4, to.normalizedStFN());
      addGet.setInt(5, to.surlUniqueID());
      log.trace("PTG CHUNK DAO: addNew; {}", addGet);
      addGet.execute();

      rsG = addGet.getGeneratedKeys();
      if (!rsG.next()) {
        throw new SQLException("Unable to get request_get id");
      }
      int idG = rsG.getInt(1);

      // third fill in status_Get...
      addChild = con.prepareStatement(INSERT_STATUS_GET, RETURN_GENERATED_KEYS);
      addChild.setInt(1, idG);
      addChild.setInt(2, to.status());
      addChild.setString(3, to.errString());
      log.trace("PTG CHUNK DAO: addNew; {}", addChild);
      addChild.execute();

      return idG;

    } finally {
      closeResultSet(rsDo);
      closeResultSet(rsG);
      closeResultSet(rsS);
      closeStatement(addDirOption);
      closeStatement(addGet);
      closeStatement(addChild);
    }
  }

  /**
   * Method used to save the changes made to a retrieved PtGChunkDataTO, back into the MySQL DB.
   * 
   * Only the fileSize, transferURL, statusCode and explanation, of status_Get table are written to
   * the DB. Likewise for the request pinLifetime.
   * 
   * In case of any error, an error message gets logged but no exception is thrown.
   */
  public synchronized void update(PtGChunkDataTO to) {

    Connection con = null;
    PreparedStatement updateFileReq = null;

    try {

      con = getConnection();
      updateFileReq = con.prepareStatement(UPDATE_REQUEST_GET_STATUS_WHERE_ID);
      updateFileReq.setLong(1, to.fileSize());
      updateFileReq.setString(2, to.turl());
      updateFileReq.setInt(3, to.status());
      updateFileReq.setString(4, to.errString());
      updateFileReq.setInt(5, to.lifeTime());
      updateFileReq.setString(6, to.normalizedStFN());
      updateFileReq.setInt(7, to.surlUniqueID());
      updateFileReq.setLong(8, to.primaryKey());
      // execute update
      log.trace("PTG CHUNK DAO: update method; {}", updateFileReq);
      updateFileReq.executeUpdate();

    } catch (SQLException e) {
      log.error("PtG CHUNK DAO: Unable to complete update! {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(updateFileReq);
      closeConnection(con);
    }
  }

  /**
   * Updates the request_Get represented by the received ReducedPtGChunkDataTO by setting its
   * normalized_sourceSURL_StFN and sourceSURL_uniqueID
   * 
   * @param chunkTO
   */
  public synchronized void updateIncomplete(ReducedPtGChunkDataTO chunkTO) {

    Connection con = null;
    PreparedStatement update = null;

    try {
      con = getConnection();
      update = con.prepareStatement(UPDATE_REQUEST_GET_WHERE_ID);
      update.setString(1, chunkTO.normalizedStFN());
      update.setInt(2, chunkTO.surlUniqueID());
      update.setLong(3, chunkTO.primaryKey());
      log.trace("PtG CHUNK DAO - update incomplete: {}", update);
      update.executeUpdate();
    } catch (SQLException e) {
      log.error("PtG CHUNK DAO: Unable to complete update incomplete! {}", e.getMessage(), e);
    } finally {
      closeStatement(update);
      closeConnection(con);
    }
  }

  /**
   * TODO WARNING! THIS IS A WORK IN PROGRESS!!!
   * 
   * Method used to refresh the PtGChunkDataTO information from the MySQL DB.
   * 
   * In this first version, only the statusCode and the TURL are reloaded from the DB. TODO The next
   * version must contains all the information related to the Chunk!
   * 
   * In case of any error, an error messagge gets logged but no exception is thrown.
   */

  public synchronized PtGChunkDataTO refresh(long primaryKey) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;
    PtGChunkDataTO chunkDataTO = null;

    try {

      con = getConnection();
      find = con.prepareStatement(SELECT_STATUS_GET_WHERE_GET_ID);
      find.setLong(1, primaryKey);
      log.trace("PTG CHUNK DAO: refresh status method; {}", find);
      rs = find.executeQuery();

      while (rs.next()) {
        chunkDataTO = new PtGChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
        chunkDataTO.setTurl(rs.getString("sg.transferURL"));
      }
      return chunkDataTO;

    } catch (SQLException e) {

      log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
      return null;

    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
  }

  /**
   * Method that queries the MySQL DB to find all entries matching the supplied TRequestToken. The
   * Collection contains the corresponding PtGChunkDataTO objects.
   * 
   * An initial simple query establishes the list of protocols associated with the request. A second
   * complex query establishes all chunks associated with the request, by properly joining
   * request_queue, request_Get, status_Get and request_DirOption. The considered fields are:
   * 
   * (1) From status_Get: the ID field which becomes the TOs primary key, and statusCode.
   * 
   * (2) From request_Get: sourceSURL
   * 
   * (3) From request_queue: pinLifetime
   * 
   * (4) From request_DirOption: isSourceADirectory, alLevelRecursive, numOfLevels
   * 
   * In case of any error, a log gets written and an empty collection is returned. No exception is
   * thrown.
   * 
   * NOTE! Chunks in SRM_ABORTED status are NOT returned!
   */
  public synchronized Collection<PtGChunkDataTO> find(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement findProtocols = null;
    PreparedStatement findRequest = null;
    ResultSet rsProtocols = null;
    ResultSet rsRequest = null;
    Collection<PtGChunkDataTO> results = Lists.newArrayList();

    try {

      con = getManagedConnection();
      findProtocols = con.prepareStatement(SELECT_REQUEST_GET_PROTOCOLS_WHERE_TOKEN);

      List<String> protocols = Lists.newArrayList();
      findProtocols.setString(1, requestToken.getValue());
      log.trace("PTG CHUNK DAO: find method; {}", findProtocols);
      rsProtocols = findProtocols.executeQuery();
      while (rsProtocols.next()) {
        protocols.add(rsProtocols.getString("tp.config_ProtocolsID"));
      }

      findRequest = con.prepareStatement(SELECT_REQUEST_GET_WHERE_TOKEN_AND_STATUS);
      findRequest.setString(1, requestToken.getValue());
      findRequest.setInt(2, statusCodeConverter.toDB(SRM_ABORTED));
      log.trace("PTG CHUNK DAO: find method; {}", findRequest);
      rsRequest = findRequest.executeQuery();

      PtGChunkDataTO chunkDataTO;
      while (rsRequest.next()) {
        chunkDataTO = new PtGChunkDataTO();
        chunkDataTO.setStatus(rsRequest.getInt("sg.statusCode"));
        chunkDataTO.setRequestToken(requestToken.getValue());
        chunkDataTO.setPrimaryKey(rsRequest.getLong("rg.ID"));
        chunkDataTO.setFromSURL(rsRequest.getString("rg.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rsRequest.getString("rg.normalized_sourceSURL_StFN"));
        int uniqueID = rsRequest.getInt("rg.sourceSURL_uniqueID");
        if (!rsRequest.wasNull()) {
          chunkDataTO.setSurlUniqueID(Integer.valueOf(uniqueID));
        }
        chunkDataTO.setClientDN(rsRequest.getString("rq.client_dn"));

        /**
         * This code is only for the 1.3.18. This is a workaround to get FQANs using the proxy field
         * on request_queue. The FE use the proxy field of request_queue to insert a single FQAN
         * string containing all FQAN separeted by the "#" char. The proxy is a BLOB, hence it has
         * to be properly conveted in string.
         */
        java.sql.Blob blob = rsRequest.getBlob("rq.proxy");
        if (!rsRequest.wasNull() && blob != null) {
          byte[] bdata = blob.getBytes(1, (int) blob.length());
          chunkDataTO.setVomsAttributes(new String(bdata));
        }
        chunkDataTO.setTimeStamp(rsRequest.getTimestamp("rq.timeStamp"));
        chunkDataTO.setLifeTime(rsRequest.getInt("rq.pinLifetime"));
        chunkDataTO.setDirOption(rsRequest.getBoolean("d.isSourceADirectory"));
        chunkDataTO.setAllLevelRecursive(rsRequest.getBoolean("d.allLevelRecursive"));
        chunkDataTO.setNumLevel(rsRequest.getInt("d.numOfLevels"));
        chunkDataTO.setProtocolList(protocols);
        results.add(chunkDataTO);
      }
      con.commit();
    } catch (SQLException e) {
      log.error("PTG CHUNK DAO: ", e.getMessage(), e);
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
   * Method that returns a Collection of ReducedPtGChunkDataTO associated to the given TRequestToken
   * expressed as String.
   */
  public synchronized Collection<ReducedPtGChunkDataTO> findReduced(TRequestToken requestToken) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;
    Collection<ReducedPtGChunkDataTO> results = Lists.newArrayList();

    try {

      con = getConnection();
      find = con.prepareStatement(SELECT_REQUEST_GET_WHERE_TOKEN);
      find.setString(1, requestToken.getValue());
      log.trace("PtG CHUNK DAO! findReduced with request token; {}", find);
      rs = find.executeQuery();

      while (rs.next()) {
        ReducedPtGChunkDataTO reducedChunkDataTO = new ReducedPtGChunkDataTO();
        reducedChunkDataTO.setStatus(rs.getInt("sg.statusCode"));
        reducedChunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
        reducedChunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
        reducedChunkDataTO.setNormalizedStFN(rs.getString("rg.normalized_sourceSURL_StFN"));
        int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          reducedChunkDataTO.setSurlUniqueID(uniqueID);
        }
        results.add(reducedChunkDataTO);
      }

    } catch (SQLException e) {
      log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return results;
  }

  public synchronized Collection<ReducedPtGChunkDataTO> findReduced(TRequestToken requestToken,
      int[] surlsUniqueIDs, String[] surlsArray) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;
    Collection<ReducedPtGChunkDataTO> results = Lists.newArrayList();

    try {

      String str =
          "SELECT sg.statusCode, rg.ID, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID "
              + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
              + "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
              + "WHERE rq.r_token=? AND ( rg.sourceSURL_uniqueID IN "
              + makeSURLUniqueIDWhere(surlsUniqueIDs) + " AND rg.sourceSURL IN "
              + makeSurlString(surlsArray) + " ) ";

      con = getConnection();
      find = con.prepareStatement(str);
      find.setString(1, requestToken.getValue());
      log.trace("PtG CHUNK DAO! findReduced with griduser+surlarray; {}", find);
      rs = find.executeQuery();

      while (rs.next()) {
        ReducedPtGChunkDataTO chunkDataTO = new ReducedPtGChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
        chunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
        chunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rs.getString("rg.normalized_sourceSURL_StFN"));
        int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          chunkDataTO.setSurlUniqueID(uniqueID);
        }
        results.add(chunkDataTO);
      }
    } catch (SQLException e) {
      log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return results;
  }

  /**
   * Method that returns a Collection of ReducedPtGChunkDataTO associated to the given griduser, and
   * whose SURLs are contained in the supplied array of Strings.
   */
  public synchronized Collection<ReducedPtGChunkDataTO> findReduced(String griduser,
      int[] surlUniqueIDs, String[] surls) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;
    Collection<ReducedPtGChunkDataTO> results = Lists.newArrayList();

    try {
      /*
       * NOTE: we search also on the fromSurl because otherwise we lost all request_get that have
       * not the uniqueID set because are not yet been used by anybody
       */
      con = getConnection();
      // get reduced chunks
      String str =
          "SELECT sg.statusCode, rg.ID, rg.sourceSURL, rg.normalized_sourceSURL_StFN, rg.sourceSURL_uniqueID "
              + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
              + "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
              + "WHERE rq.client_dn=? AND ( rg.sourceSURL_uniqueID IN "
              + makeSURLUniqueIDWhere(surlUniqueIDs) + " AND rg.sourceSURL IN "
              + makeSurlString(surls) + " ) ";
      find = con.prepareStatement(str);
      find.setString(1, griduser);
      log.trace("PtG CHUNK DAO! findReduced with griduser+surlarray; {}", find);
      rs = find.executeQuery();

      while (rs.next()) {
        ReducedPtGChunkDataTO chunkDataTO = new ReducedPtGChunkDataTO();
        chunkDataTO.setStatus(rs.getInt("sg.statusCode"));
        chunkDataTO.setPrimaryKey(rs.getLong("rg.ID"));
        chunkDataTO.setFromSURL(rs.getString("rg.sourceSURL"));
        chunkDataTO.setNormalizedStFN(rs.getString("rg.normalized_sourceSURL_StFN"));
        int uniqueID = rs.getInt("rg.sourceSURL_uniqueID");
        if (!rs.wasNull()) {
          chunkDataTO.setSurlUniqueID(uniqueID);
        }
        results.add(chunkDataTO);
      }
    } catch (SQLException e) {
      log.error("PTG CHUNK DAO: {}", e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return results;
  }

  /**
   * Method used in extraordinary situations to signal that data retrieved from the DB was malformed
   * and could not be translated into the StoRM object model.
   * 
   * This method attempts to change the status of the request to SRM_FAILURE and record it in the
   * DB.
   * 
   * This operation could potentially fail because the source of the malformed problems could be a
   * problematic DB; indeed, initially only log messagges where recorded.
   * 
   * Yet it soon became clear that the source of malformed data were the clients and/or FE recording
   * info in the DB. In these circumstances the client would see its request as being in the
   * SRM_IN_PROGRESS state for ever. Hence the pressing need to inform it of the encountered
   * problems.
   */
  public synchronized void fail(PtGChunkDataTO auxTO) {

    Connection con = null;
    PreparedStatement update = null;

    try {

      con = getConnection();
      update = con.prepareStatement(UPDATE_STATUS_GET_WHERE_REQUEST_GET_ID_IS);
      update.setInt(1, statusCodeConverter.toDB(SRM_FAILURE));
      update.setString(2, "Request is malformed!");
      update.setLong(3, auxTO.primaryKey());
      log.trace("PTG CHUNK DAO: signalMalformed; {}", update);
      update.executeUpdate();

    } catch (SQLException e) {
      log.error("PtGChunkDAO! Unable to signal in DB that the request was "
          + "malformed! Request: {}; Exception: {}", auxTO.toString(), e.toString());
      e.printStackTrace();
    } finally {
      closeStatement(update);
      closeConnection(con);
    }
  }

  /**
   * Method that returns the number of Get requests on the given SURL, that are in SRM_FILE_PINNED
   * state.
   * 
   * This method is intended to be used by PtGChunkCatalog in the isSRM_FILE_PINNED method
   * invocation.
   * 
   * In case of any error, 0 is returned.
   */
  // request_Get table
  public synchronized int numberInSRM_FILE_PINNED(int surlUniqueID) {

    return count(surlUniqueID, SRM_FILE_PINNED);
  }

  public synchronized int count(int surlUniqueID, TStatusCode status) {

    Connection con = null;
    PreparedStatement find = null;
    ResultSet rs = null;
    int count = 0;

    try {
      con = getConnection();
      find = con.prepareStatement(COUNT_REQUEST_ON_SURL_WITH_STATUS);
      find.setInt(1, surlUniqueID);
      find.setInt(2, statusCodeConverter.toDB(status));
      log.trace("PtG CHUNK DAO - numberInSRM_FILE_PINNED method: {}", find);
      rs = find.executeQuery();

      if (rs.next()) {
        count = rs.getInt(1);
      }
    } catch (SQLException e) {
      log.error("PtG CHUNK DAO! Unable to determine numberInSRM_FILE_PINNED! " + "Returning 0! {}",
          e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeResultSet(rs);
      closeStatement(find);
      closeConnection(con);
    }
    return count;
  }

  /**
   * Method that updates all expired requests in SRM_FILE_PINNED state, into SRM_RELEASED.
   * 
   * This is needed when the client forgets to invoke srmReleaseFiles().
   * 
   * @return
   */
  public synchronized Collection<TSURL> transitExpiredSRM_FILE_PINNED() {

    Map<String, Integer> expiredSurlMap = Maps.newHashMap();
    Set<Integer> pinnedSurlSet = Sets.newHashSet();

    Connection con = null;
    PreparedStatement findExpired = null;
    PreparedStatement updateExpired = null;
    PreparedStatement findPtgPinnedSurls = null;
    PreparedStatement findBolPinnedSurls = null;
    ResultSet expired = null;
    ResultSet ptgPinnedSurls = null;
    ResultSet bolPinnedSurls = null;

    /* Find all expired SURLs */
    try {
      // start transaction
      con = getManagedConnection();

      findExpired = con.prepareStatement(SELECT_EXPIRED_REQUESTS);
      findExpired.setInt(1, statusCodeConverter.toDB(SRM_FILE_PINNED));

      expired = findExpired.executeQuery();

      while (expired.next()) {
        String sourceSURL = expired.getString("rg.sourceSURL");
        Integer uniqueID = Integer.valueOf(expired.getInt("rg.sourceSURL_uniqueID"));
        /* If the uniqueID is not set compute it */
        if (expired.wasNull()) {
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
        con.commit();
        log.trace(
            "PtGChunkDAO! No chunk of PtG request was transited from SRM_FILE_PINNED to SRM_RELEASED.");
        return Lists.newArrayList();
      }

      updateExpired = con.prepareStatement(UPDATE_STATUS_OF_EXPIRED_REQUESTS);
      updateExpired.setInt(1, statusCodeConverter.toDB(SRM_RELEASED));
      updateExpired.setInt(2, statusCodeConverter.toDB(SRM_FILE_PINNED));
      log.trace("PtG CHUNK DAO - transitExpiredSRM_FILE_PINNED method: {}", updateExpired);
      int count = updateExpired.executeUpdate();

      if (count == 0) {
        log.trace("PtGChunkDAO! No chunk of PtG request was "
            + "transited from SRM_FILE_PINNED to SRM_RELEASED.");
      } else {
        log.info("PtGChunkDAO! {} chunks of PtG requests were transited from"
            + " SRM_FILE_PINNED to SRM_RELEASED.", count);
      }

      /*
       * in order to enhance performance here we can check if there is any file system with tape
       * (T1D0, T1D1), if there is not any we can skip the following
       */

      /* Find all not expired SURLs from PtG and BoL */

      findPtgPinnedSurls = con.prepareStatement(SELECT_PTG_PINNED_SURLS);
      findPtgPinnedSurls.setInt(1, statusCodeConverter.toDB(SRM_FILE_PINNED));

      ptgPinnedSurls = findPtgPinnedSurls.executeQuery();

      while (ptgPinnedSurls.next()) {
        String sourceSURL = ptgPinnedSurls.getString("rg.sourceSURL");
        Integer uniqueID = Integer.valueOf(ptgPinnedSurls.getInt("rg.sourceSURL_uniqueID"));
        /* If the uniqueID is not setted compute it */
        if (ptgPinnedSurls.wasNull()) {
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

      // SURLs pinned by BoLs
      findBolPinnedSurls = con.prepareStatement(SELECT_BOL_PINNED_SURLS);
      findBolPinnedSurls.setInt(1, statusCodeConverter.toDB(SRM_SUCCESS));
      bolPinnedSurls = findBolPinnedSurls.executeQuery();

      while (bolPinnedSurls.next()) {
        String sourceSURL = bolPinnedSurls.getString("rb.sourceSURL");
        Integer uniqueID = Integer.valueOf(bolPinnedSurls.getInt("rb.sourceSURL_uniqueID"));
        /* If the uniqueID is not setted compute it */
        if (bolPinnedSurls.wasNull()) {
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

      con.commit();
    } catch (SQLException e) {
      log.error("PtGChunkDAO! SQLException. {}", e.getMessage(), e);
      e.printStackTrace();
      try {
        con.rollback();
      } catch (SQLException e1) {
        e1.printStackTrace();
      }
    } finally {
      closeStatement(findExpired);
      closeStatement(updateExpired);
      closeStatement(findPtgPinnedSurls);
      closeStatement(findBolPinnedSurls);
      closeResultSet(expired);
      closeResultSet(ptgPinnedSurls);
      closeResultSet(bolPinnedSurls);
      closeConnection(con);
    }

    Collection<TSURL> expiredSurlList = Lists.newArrayList();
    /* Remove the Extended Attribute pinned if there is not a valid SURL on it */
    TSURL surl;
    for (Entry<String, Integer> surlEntry : expiredSurlMap.entrySet()) {
      if (!pinnedSurlSet.contains(surlEntry.getValue())) {
        try {
          surl = TSURL.makeFromStringValidate(surlEntry.getKey());
        } catch (InvalidTSURLAttributesException e) {
          log.error("Invalid SURL, cannot release the pin " + "(Extended Attribute): {}",
              surlEntry.getKey());
          continue;
        }
        expiredSurlList.add(surl);
        StoRI stori;
        try {
          stori = NamespaceDirector.getNamespace().resolveStoRIbySURL(surl);
        } catch (Throwable e) {
          log.error("Invalid SURL {} cannot release the pin. {}: {}", surlEntry.getKey(),
              e.getClass().getCanonicalName(), e.getMessage(), e);
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
   * Method that updates all chunks in SRM_FILE_PINNED state, into SRM_RELEASED. An array of long
   * representing the primary key of each chunk is required: only they get the status changed
   * provided their current status is SRM_FILE_PINNED.
   * 
   * This method is used during srmReleaseFiles
   * 
   * In case of any error nothing happens and no exception is thrown, but proper messagges get
   * logged.
   */
  public synchronized void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids) {

    String str = "UPDATE status_Get sg SET sg.statusCode=? "
        + "WHERE sg.statusCode=? AND sg.request_GetID IN " + makeWhereString(ids);

    Connection con = null;
    PreparedStatement stmt = null;
    try {

      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(SRM_RELEASED));
      stmt.setInt(2, statusCodeConverter.toDB(SRM_FILE_PINNED));
      log.trace("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: {}", stmt);
      int count = stmt.executeUpdate();
      if (count == 0) {
        log.trace("PtG CHUNK DAO! No chunk of PtG request was "
            + "transited from SRM_FILE_PINNED to SRM_RELEASED.");
      } else {
        log.info("PtG CHUNK DAO! {} chunks of PtG requests were transited "
            + "from SRM_FILE_PINNED to SRM_RELEASED.", count);
      }
    } catch (SQLException e) {
      log.error(
          "PtG CHUNK DAO! Unable to transit chunks" + " from SRM_FILE_PINNED to SRM_RELEASED! {}",
          e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * @param ids
   * @param token
   */
  public synchronized void transitSRM_FILE_PINNEDtoSRM_RELEASED(long[] ids, TRequestToken token) {

    if (token == null) {
      transitSRM_FILE_PINNEDtoSRM_RELEASED(ids);
      return;
    }

    /*
     * If a request token has been specified, only the related Get requests have to be released.
     * This is done adding the r.r_token="..." clause in the where subquery.
     */
    String str = "UPDATE "
        + "status_Get sg JOIN (request_Get rg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
        + "SET sg.statusCode=? " + "WHERE sg.statusCode=? AND rq.r_token='" + token.getValue()
        + "' AND rg.ID IN " + makeWhereString(ids);

    Connection con = null;
    PreparedStatement stmt = null;
    try {
      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(SRM_RELEASED));
      stmt.setInt(2, statusCodeConverter.toDB(SRM_FILE_PINNED));
      log.trace("PtG CHUNK DAO - transitSRM_FILE_PINNEDtoSRM_RELEASED: {}", stmt);
      int count = stmt.executeUpdate();
      if (count == 0) {
        log.trace("PtG CHUNK DAO! No chunk of PtG request was"
            + " transited from SRM_FILE_PINNED to SRM_RELEASED.");
      } else {
        log.info("PtG CHUNK DAO! {} chunks of PtG requests were transited from "
            + "SRM_FILE_PINNED to SRM_RELEASED.", count);
      }
    } catch (SQLException e) {
      log.error(
          "PtG CHUNK DAO! Unable to transit chunks from " + "SRM_FILE_PINNED to SRM_RELEASED! {}",
          e.getMessage(), e);
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  public synchronized void updateStatus(TRequestToken requestToken, int[] surlUniqueIDs,
      String[] surls, TStatusCode statusCode, String explanation) {

    String str = "UPDATE "
        + "status_Get sg JOIN (request_Get rg, request_queue rq) ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
        + "SET sg.statusCode=? , sg.explanation=? " + "WHERE rq.r_token='" + requestToken.toString()
        + "' AND ( rg.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlUniqueIDs)
        + " AND rg.sourceSURL IN " + makeSurlString(surls) + " ) ";

    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(statusCode));
      stmt.setString(2, (explanation != null ? explanation : ""));
      log.trace("PtG CHUNK DAO - updateStatus: {}", stmt);
      int count = stmt.executeUpdate();
      if (count == 0) {
        log.trace("PtG CHUNK DAO! No chunk of PtG request was updated to {}.", statusCode);
      } else {
        log.info("PtG CHUNK DAO! {} chunks of PtG requests were updated to {}.", count, statusCode);
      }
    } catch (SQLException e) {
      log.error("PtG CHUNK DAO! Unable to updated to {}! {}", statusCode, e.getMessage(), e);
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
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

  private synchronized void doUpdateStatusOnMatchingStatus(TRequestToken requestToken,
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

    String str = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
        + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID " + "SET sg.statusCode=? ";
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

    Connection con = null;
    PreparedStatement stmt = null;

    try {
      con = getConnection();
      stmt = con.prepareStatement(str);
      stmt.setInt(1, statusCodeConverter.toDB(newStatusCode));
      stmt.setInt(2, statusCodeConverter.toDB(expectedStatusCode));
      log.trace("PtG CHUNK DAO - updateStatusOnMatchingStatus: {}", stmt);
      int count = stmt.executeUpdate();
      if (count == 0) {
        log.trace("PtG CHUNK DAO! No chunk of PtG request was updated " + "from {} to {}.",
            expectedStatusCode, newStatusCode);
      } else {
        log.debug("PtG CHUNK DAO! {} chunks of PtG requests were updated " + "from {} to {}.",
            count, expectedStatusCode, newStatusCode);
      }
    } catch (SQLException e) {
      log.error("PtG CHUNK DAO! Unable to updated from {} to {}! {}", expectedStatusCode,
          newStatusCode, e.getMessage(), e);
      e.printStackTrace();
    } finally {
      closeStatement(stmt);
      closeConnection(con);
    }
  }

  /**
   * Method that returns a String containing all IDs.
   */
  private String makeWhereString(long[] rowids) {

    StringBuilder sb = new StringBuilder("(");
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
   * Method that returns a String containing all SURL's IDs.
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

  private String buildExpainationSet(String explanation) {

    return " sg.explanation='" + explanation + "' ";
  }

  private String buildTokenWhereClause(TRequestToken requestToken) {

    return " rq.r_token='" + requestToken.toString() + "' ";
  }

  private String buildSurlsWhereClause(int[] surlsUniqueIDs, String[] surls) {

    return " ( rg.sourceSURL_uniqueID IN " + makeSURLUniqueIDWhere(surlsUniqueIDs)
        + " AND rg.sourceSURL IN " + makeSurlString(surls) + " ) ";
  }

}
