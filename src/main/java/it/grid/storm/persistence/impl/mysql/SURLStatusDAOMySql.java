package it.grid.storm.persistence.impl.mysql;

import static it.grid.storm.srm.types.TStatusCode.SRM_SPACE_AVAILABLE;
import static it.grid.storm.srm.types.TStatusCode.SRM_SUCCESS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.converter.StatusCodeConverter;
import it.grid.storm.persistence.dao.AbstractDAO;
import it.grid.storm.persistence.dao.SURLStatusDAO;
import it.grid.storm.persistence.pool.StormDbConnectionPool;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

public class SURLStatusDAOMySql extends AbstractDAO implements SURLStatusDAO {

  public static final Logger LOGGER = LoggerFactory.getLogger(SURLStatusDAOMySql.class);

  private static SURLStatusDAO instance;

  public static synchronized SURLStatusDAO getInstance() {
    if (instance == null) {
      instance = new SURLStatusDAOMySql();
    }
    return instance;
  }

  private final StatusCodeConverter converter;
  private final RequestSummaryCatalog requestSummaryCatalog;
  private final PtPChunkCatalog ptpChunkCatalog;

  private SURLStatusDAOMySql() {
    super(StormDbConnectionPool.getInstance());
    converter = StatusCodeConverter.getInstance();
    requestSummaryCatalog = RequestSummaryCatalog.getInstance();
    ptpChunkCatalog = PtPChunkCatalog.getInstance();
  }

  public boolean abortActivePtGsForSURL(GridUserInterface user, TSURL surl, String explanation) {

    surlSanityChecks(surl);

    Connection con = null;
    PreparedStatement stat = null;
    int updateCount = 0;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
          + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
          + "SET sg.statusCode=20, rq.status=20, sg.explanation=? "
          + "WHERE rg.sourceSURL = ? and rg.sourceSURL_uniqueID = ? "
          + "AND (sg.statusCode=22 OR sg.statusCode=17) ";

      if (user != null) {
        query += "AND rq.client_dn = ?";
      }
      stat = con.prepareStatement(query);

      stat.setString(1, explanation);
      stat.setString(2, surl.getSURLString());
      stat.setInt(3, surl.uniqueId());

      if (user != null) {
        stat.setString(4, user.getDn());
      }

      updateCount = stat.executeUpdate();
      LOGGER.debug("abortActivePtGsForSURL: surl={}, numOfAbortedRequests={}", surl, updateCount);

    } catch (SQLException e) {

      String msg = String.format("abortActivePtGsForSURL: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeConnection(con);
    }

    return (updateCount != 0);
  }

  public boolean abortActivePtPsForSURL(GridUserInterface user, TSURL surl, String explanation) {

    surlSanityChecks(surl);

    Connection con = null;
    PreparedStatement stat = null;
    int updateCount = 0;

    try {
      con = getConnection();

      String query = "UPDATE status_Put sp JOIN (request_Put rp, request_queue rq) "
          + "ON sp.request_PutID=rp.ID AND rp.request_queueID=rq.ID "
          + "SET sp.statusCode=20, rq.status=20, sp.explanation=? "
          + "WHERE rp.targetSURL = ? and rp.targetSURL_uniqueID = ? "
          + "AND (sp.statusCode=24 OR sp.statusCode=17)";

      if (user != null) {
        query += "AND rq.client_dn = ?";
      }

      stat = con.prepareStatement(query);
      stat.setString(1, explanation);
      stat.setString(2, surl.getSURLString());
      stat.setInt(3, surl.uniqueId());

      if (user != null) {
        stat.setString(4, user.getDn());
      }

      updateCount = stat.executeUpdate();

      LOGGER.debug("abortActivePtPsForSURL: surl={}, numOfAbortedRequests={}", surl, updateCount);

    } catch (SQLException e) {

      String msg = String.format("abortActivePtPsForSURL: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeConnection(con);
    }
    return (updateCount != 0);
  }

  private Map<TSURL, TReturnStatus> buildStatusMap(ResultSet rs) throws SQLException {

    if (rs == null) {
      throw new IllegalArgumentException("rs cannot be null");
    }

    Map<TSURL, TReturnStatus> statusMap = new HashMap<TSURL, TReturnStatus>();
    while (rs.next()) {
      TSURL surl = surlFromString(rs.getString(1));
      TStatusCode sc = converter.toSTORM(rs.getInt(2));

      statusMap.put(surl, new TReturnStatus(sc));
    }

    return statusMap;

  }

  private Map<TSURL, TReturnStatus> filterSURLStatuses(Map<TSURL, TReturnStatus> statuses,
      List<TSURL> surls) {

    if (surls == null) {
      return statuses;
    }

    List<TSURL> surlsCopy = new ArrayList<TSURL>(surls);

    // Keep only the SURLs really requested.
    statuses.keySet().retainAll(surls);

    // The surls that are not in the statuses key set
    // are those not linked to the actual token
    // (this is an error in the request)
    surlsCopy.removeAll(statuses.keySet());

    // Add a failure state for the surls that were
    // requested but are not linked to the token
    for (TSURL s : surlsCopy) {
      statuses.put(s,
          new TReturnStatus(TStatusCode.SRM_FAILURE, "SURL not linked to passed request token."));
    }

    return statuses;
  }

  private Map<TSURL, TReturnStatus> getBoLSURLStatuses(TRequestToken token) {

    tokenSanityChecks(token);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;
    Map<TSURL, TReturnStatus> result = null;

    try {
      con = getConnection();

      String query = "SELECT rb.sourceSURL, sb.statusCode "
          + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
          + "ON (rb.request_queueID = rq.ID AND sb.request_BoLID = rb.ID)"
          + "WHERE ( rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      rs = stat.executeQuery();

      result = buildStatusMap(rs);

    } catch (SQLException e) {

      String msg = String.format("getBoLSURLStatuses: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);
      result = Maps.newHashMap();

    } finally {

      closeResultSet(rs);
      closeStatement(stat);
      closeConnection(con);
    }

    return result;
  }

  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(GridUserInterface user,
      List<TSURL> surls) {

    if (user == null) {
      throw new NullPointerException("getPinnedSURLsForUser: null user!");
    }

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;
    Map<TSURL, TReturnStatus> result = Maps.newHashMap();

    try {

      con = getConnection();

      String query = "SELECT rg.sourceSURL, rg.sourceSURL_uniqueID, sg.statusCode "
          + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
          + "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
          + "WHERE ( sg.statusCode = 22  and rq.client_dn = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, user.getDn());

      rs = stat.executeQuery();
      Map<TSURL, TReturnStatus> statusMap = Maps.newHashMap();

      while (rs.next()) {

        TSURL surl = surlFromString(rs.getString(1));
        surl.setUniqueID(rs.getInt(2));
        statusMap.put(surl, new TReturnStatus(converter.toSTORM(rs.getInt(3))));

      }

      result = filterSURLStatuses(statusMap, surls);

    } catch (SQLException e) {

      String msg = String.format("getPinnedSURLsForUser: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }

    return result;
  }

  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(GridUserInterface user,
      TRequestToken token, List<TSURL> surls) {

    userSanityChecks(user);
    tokenSanityChecks(token);
    surlSanityChecks(surls);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    Map<TSURL, TReturnStatus> result = Maps.newHashMap();

    try {
      con = getConnection();

      String query = "SELECT rg.sourceSURL, rg.sourceSURL_uniqueID, sg.statusCode "
          + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
          + "ON (rg.request_queueID=rq.ID AND sg.request_GetID=rg.ID) "
          + "WHERE ( sg.statusCode = 22  and rq.client_dn = ? and rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, user.getDn());
      stat.setString(2, token.getValue());

      rs = stat.executeQuery();
      Map<TSURL, TReturnStatus> statusMap = Maps.newHashMap();

      while (rs.next()) {

        TSURL surl = surlFromString(rs.getString(1));
        surl.setUniqueID(rs.getInt(2));
        statusMap.put(surl, new TReturnStatus(converter.toSTORM(rs.getInt(3))));

      }

      result = filterSURLStatuses(statusMap, surls);

    } catch (SQLException e) {

      String msg = String.format("getPinnedSURLsForUser: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
    return result;
  }

  private Map<TSURL, TReturnStatus> getPtGSURLStatuses(TRequestToken token) {

    tokenSanityChecks(token);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    Map<TSURL, TReturnStatus> result = Maps.newHashMap();

    try {
      con = getConnection();

      String query = "SELECT rg.sourceSURL, sg.statusCode "
          + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
          + "ON (rg.request_queueID = rq.ID AND sg.request_GetID=rg.ID) "
          + "WHERE ( rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      rs = stat.executeQuery();

      result = buildStatusMap(rs);

    } catch (SQLException e) {

      String msg = String.format("getPtGSURLStatuses: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
    return result;
  }

  private Map<TSURL, TReturnStatus> getPtPSURLStatuses(TRequestToken token) {

    tokenSanityChecks(token);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;
    Map<TSURL, TReturnStatus> result = Maps.newHashMap();

    try {
      con = getConnection();

      String query = "SELECT rp.targetSURL, sp.statusCode "
          + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
          + "ON (rp.request_queueID = rq.ID AND sp.request_PutID = rp.ID)"
          + "WHERE ( rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      rs = stat.executeQuery();

      result = buildStatusMap(rs);

    } catch (SQLException e) {

      String msg = String.format("getPtPSURLStatuses: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
    return result;
  }

  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token) {

    TRequestType rt = requestSummaryCatalog.typeOf(token);

    if (rt.isEmpty())
      throw new UnknownTokenException(token.getValue());

    if (token.hasExpirationDate() && token.isExpired())
      throw new ExpiredTokenException(token.getValue());

    switch (rt) {
      case PREPARE_TO_GET:
        return getPtGSURLStatuses(token);

      case PREPARE_TO_PUT:
        return getPtPSURLStatuses(token);

      case BRING_ON_LINE:
        return getBoLSURLStatuses(token);

      default:
        String msg = String.format("Invalid request type for token %s: %s", token, rt.toString());
        throw new IllegalArgumentException(msg);
    }
  }

  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token, List<TSURL> surls) {

    TRequestType rt = requestSummaryCatalog.typeOf(token);

    switch (rt) {
      case PREPARE_TO_GET:
        return filterSURLStatuses(getPtGSURLStatuses(token), surls);

      case PREPARE_TO_PUT:
        return filterSURLStatuses(getPtPSURLStatuses(token), surls);

      case BRING_ON_LINE:
        return filterSURLStatuses(getBoLSURLStatuses(token), surls);

      default:
        String msg = String.format("Invalid request type for token %s: %s", token, rt.toString());
        throw new IllegalArgumentException(msg);
    }
  }

  public int markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    tokenSanityChecks(token);
    surlSanityChecks(surls);

    // I am not re-implementing the whole catalog functions
    return ptpChunkCatalog.updateFromPreviousStatus(token, surls, SRM_SPACE_AVAILABLE, SRM_SUCCESS);

  }

  private String quoteSURLUniqueIDs(List<TSURL> surls) {

    StringBuilder sb = new StringBuilder();

    for (TSURL s : surls) {
      if (sb.length() > 0) {
        sb.append(",");
      }

      sb.append(s.uniqueId());
    }

    return sb.toString();

  }

  private String quoteSURLList(List<TSURL> surls) {

    StringBuilder sb = new StringBuilder();
    for (TSURL s : surls) {
      if (sb.length() > 0) {
        sb.append(",");
      }

      sb.append('\'');
      sb.append(s);
      sb.append('\'');
    }
    return sb.toString();
  }

  public void releaseSURL(TSURL surl) {

    surlSanityChecks(surl);

    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
          + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID SET sg.statusCode=21"
          + "WHERE (sg.statusCode=22 OR sg.statusCode=0) "
          + "AND rg.sourceSURL = ? and rg.sourceSURL_uniqueID = ?";

      stat = con.prepareStatement(query);
      stat.setString(1, surl.getSURLString());
      stat.setInt(2, surl.uniqueId());

      stat.executeUpdate();

    } catch (SQLException e) {

      String msg = String.format("releaseSURL: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeConnection(con);
    }
  }

  public void releaseSURLs(GridUserInterface user, List<TSURL> surls) {

    userSanityChecks(user);
    surlSanityChecks(surls);

    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
          + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID SET sg.statusCode=21 "
          + "WHERE (sg.statusCode=22 OR sg.statusCode=0) AND rg.sourceSURL_uniqueID IN ("
          + quoteSURLUniqueIDs(surls) + ") AND rg.sourceSURL IN (" + quoteSURLList(surls)
          + ") AND rq.client_dn = ?";

      stat = con.prepareStatement(query);
      stat.setString(1, user.getDn());
      int releasedSURLsCount = stat.executeUpdate();

      LOGGER.debug("releaseSURLs: released {} surls", releasedSURLsCount);

    } catch (SQLException e) {

      String msg = String.format("releaseSURLs: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeConnection(con);
    }
  }

  public void releaseSURLs(List<TSURL> surls) {

    surlSanityChecks(surls);

    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
          + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID SET sg.statusCode=21 "
          + "WHERE (sg.statusCode=22 OR sg.statusCode=0) AND rg.sourceSURL_uniqueID IN ("
          + quoteSURLUniqueIDs(surls) + ") AND rg.sourceSURL IN (" + quoteSURLList(surls)
          + ")";

      stat = con.prepareStatement(query);
      stat.executeUpdate();

    } catch (SQLException e) {

      String msg = String.format("releaseSURLs: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeConnection(con);
    }
  }

  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {

    surlSanityChecks(surls);
    tokenSanityChecks(token);

    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg JOIN (request_Get rg, request_queue rq) "
          + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID SET sg.statusCode=21 "
          + "WHERE (sg.statusCode=22 OR sg.statusCode=0) AND rg.sourceSURL_uniqueID IN ("
          + quoteSURLUniqueIDs(surls) + ") AND rg.sourceSURL IN (" + quoteSURLList(surls)
          + ") AND rq.r_token = ?";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      stat.executeUpdate();

    } catch (SQLException e) {

      String msg = String.format("releaseSURLs: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeConnection(con);
    }
  }

  private TSURL surlFromString(String s) {

    try {

      return TSURL.makeFromStringWellFormed(s);

    } catch (InvalidTSURLAttributesException e) {
      throw new IllegalArgumentException("Error creating surl from string: " + s, e);
    }
  }

  public boolean surlHasOngoingPtGs(TSURL surl) {

    surlSanityChecks(surl);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;
    boolean result = false;

    try {
      con = getConnection();

      // We basically check whether there are active requests
      // that have the SURL in SRM_FILE_PINNED status
      String query = "SELECT rq.ID, rg.ID, sg.statusCode "
          + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
          + "ON (rg.request_queueID = rq.ID AND sg.request_GetID = rg.ID) "
          + "WHERE ( rg.sourceSURL = ? and rg.sourceSURL_uniqueID = ? "
          + "and sg.statusCode = 22 )";

      stat = con.prepareStatement(query);
      stat.setString(1, surl.getSURLString());
      stat.setInt(2, surl.uniqueId());

      rs = stat.executeQuery();
      result = rs.next();

    } catch (SQLException e) {

      String msg = String.format("surlHasOngoingPtGs: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
    return result;
  }

  public boolean surlHasOngoingPtPs(TSURL surl, TRequestToken ptpRequestToken) {

    surlSanityChecks(surl);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;
    boolean result = false;

    try {

      con = getConnection();
      // We basically check whether there are active requests
      // that have the SURL in SRM_SPACE_AVAILABLE status
      String query = "SELECT rq.ID, rp.ID, sp.statusCode "
          + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
          + "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
          + "WHERE ( rp.targetSURL = ? and rp.targetSURL_uniqueID = ? " + "and sp.statusCode=24 )";

      if (ptpRequestToken != null) {
        query += " AND rq.r_token != ?";
      }

      stat = con.prepareStatement(query);
      stat.setString(1, surl.getSURLString());
      stat.setInt(2, surl.uniqueId());

      if (ptpRequestToken != null) {
        stat.setString(3, ptpRequestToken.getValue());
      }

      rs = stat.executeQuery();
      result = rs.next();

    } catch (SQLException e) {

      String msg = String.format("surlHasOngoingPtPs: SQL error: %s", e.getMessage());
      LOGGER.error(msg, e);

    } finally {

      closeStatement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
    return result;
  }

  private void surlSanityChecks(List<TSURL> surls) {

    if (surls == null)
      throw new IllegalArgumentException("surls must be non-null.");

    for (TSURL s : surls) {
      surlSanityChecks(s);
    }
  }

  private void surlSanityChecks(TSURL surl) {

    if (surl == null || surl.getSURLString() == null)
      throw new IllegalArgumentException("surl must be non-null.");

    if (surl.getSURLString().isEmpty())
      throw new IllegalArgumentException("surl must be non-empty.");

  }

  private void tokenSanityChecks(TRequestToken token) {

    if (token == null || token.getValue() == null)
      throw new IllegalArgumentException("token must be non-null.");

    if (token.getValue().isEmpty())
      throw new IllegalArgumentException("token must be non-empty.");

  }

  private void userSanityChecks(GridUserInterface user) {

    if (user == null)
      throw new IllegalArgumentException("user must be non-null.");
  }

}
