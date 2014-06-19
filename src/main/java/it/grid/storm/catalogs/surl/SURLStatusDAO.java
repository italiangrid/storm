package it.grid.storm.catalogs.surl;

import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.catalogs.StatusCodeConverter;
import it.grid.storm.catalogs.StoRMDataSource;
import it.grid.storm.srm.types.InvalidTReturnStatusAttributeException;
import it.grid.storm.srm.types.InvalidTSURLAttributesException;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.ExpiredTokenException;
import it.grid.storm.synchcall.surl.UnknownTokenException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SURLStatusDAO implements SURLStatusManager{

  public static final Logger log = LoggerFactory.getLogger(SURLStatusDAO.class);

  private Connection getConnection() throws SQLException {

    if (StoRMDataSource.getInstance() == null) {
      throw new IllegalStateException("SToRM Data source not initialized!");
    }
    return StoRMDataSource.getInstance().getConnection();
  }

  private void surlSanityChecks(List<TSURL> surls) {

    if (surls == null)
      throw new IllegalArgumentException("surls must be non-null.");

    for (TSURL s : surls) {
      surlSanityChecks(s);
    }
  }

  private void tokenSanityChecks(TRequestToken token) {

    if (token == null || token.getValue() == null)
      throw new IllegalArgumentException("token must be non-null.");

    if (token.getValue().isEmpty())
      throw new IllegalArgumentException("token must be non-empty.");

  }

  private void surlSanityChecks(TSURL surl) {

    if (surl == null || surl.getSURLString() == null)
      throw new IllegalArgumentException("surl must be non-null.");

    if (surl.getSURLString().isEmpty())
      throw new IllegalArgumentException("surl must be non-empty.");

  }

  private void closeConnection(Connection conn) {

    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        log.error("Error closing connection: {}.", e.getMessage(), e);
      }
    }
  }

  private void closeStatetement(Statement stat) {

    if (stat != null) {
      try {
        stat.close();
      } catch (SQLException e) {
        log.error("Error closing statement: {}.", e.getMessage(), e);
      }
    }
  }

  private void closeResultSet(ResultSet rs) {

    if (rs != null) {

      try {
        rs.close();
      } catch (SQLException e) {
        log.error("Error closing result set: {}", e.getMessage(), e);
      }
    }
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

  public void releaseSURLs(List<TSURL> surls) {

    surlSanityChecks(surls);

    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg "
        + "JOIN (request_Get rg, request_queue rq) "
        + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
        + "SET sg.statusCode=21"
        + "WHERE (sg.statusCode=22 OR sg.statusCode=0) "
        + "AND rg.sourceSURL IN (" + quoteSURLList(surls) + ")";

      stat = con.prepareStatement(query);
      stat.executeUpdate();

    } catch (SQLException e) {
      String msg = String.format("%s: SQL error: %s", "releaseSURLs",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);

    } finally {
      closeStatetement(stat);
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

      String query = "UPDATE status_Get sg "
        + "JOIN (request_Get rg, request_queue rq) "
        + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
        + "SET sg.statusCode=21"
        + "WHERE (sg.statusCode=22 OR sg.statusCode=0) "
        + "AND rg.sourceSURL IN (" + quoteSURLList(surls) + ")"
        + "AND rq.r_token = ?";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      stat.executeUpdate();

    } catch (SQLException e) {
      String msg = String.format("%s: SQL error: %s", "releaseSURLs",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);

    } finally {
      closeStatetement(stat);
      closeConnection(con);
    }
  }
  public void releaseSURL(TSURL surl) {

    surlSanityChecks(surl);

    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "UPDATE status_Get sg "
        + "JOIN (request_Get rg, request_queue rq) "
        + "ON sg.request_GetID=rg.ID AND rg.request_queueID=rq.ID "
        + "SET sg.statusCode=21"
        + "WHERE (sg.statusCode=22 OR sg.statusCode=0) "
        + "AND rg.sourceSURL = ?";

      stat = con.prepareStatement(query);
      stat.setString(1, surl.getSURLString());

      stat.executeUpdate();
    } catch (SQLException e) {
      String msg = String.format("%s: SQL error: %s", "releaseSURL",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);

    } finally {
      closeStatetement(stat);
      closeConnection(con);
    }
  }

  public boolean surlHasOngoingPtGs(TSURL surl) {

    surlSanityChecks(surl);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      // We basically check whether there are active requests
      // that have the SURL in SRM_FILE_PINNED status
      String query = "SELECT rq.ID, rg.ID, sg.statusCode"
        + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
        + "ON (rg.request_queueID = rq.ID AND sg.ID = rg.ID)"
        + "WHERE ( rg.sourceSURL = ? and sg.statusCode = 22)";

      stat = con.prepareStatement(query);
      stat.setString(1, surl.getSURLString());

      rs = stat.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      String msg = String.format("%s: SQL error: %s", "surlHasOngoingPtGs",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    } finally {
      closeStatetement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
  }

  private TSURL surlFromString(String s){
    try{
      
     return TSURL.makeFromStringWellFormed(s); 
    
    }catch(InvalidTSURLAttributesException e){
      throw new IllegalArgumentException("Error creating surl from string: "+s,
        e);
    }
  }
  
  private TReturnStatus returnStatusFromStatusCode(TStatusCode sc){
    
    return returnStatusFromStatusCode(sc, null);
  }
  
  private TReturnStatus returnStatusFromStatusCode(TStatusCode sc, 
    String explanation){
    try {
      return new TReturnStatus(sc, explanation);
    } catch (InvalidTReturnStatusAttributeException e) {
      throw new IllegalArgumentException(e);
    }
    
  }
  private Map<TSURL, TReturnStatus> buildStatusMap(ResultSet rs)
    throws SQLException {

    if (rs == null) {
      throw new IllegalArgumentException("rs cannot be null");
    }

    Map<TSURL, TReturnStatus> statusMap = new HashMap<TSURL, TReturnStatus>();
    StatusCodeConverter converter = StatusCodeConverter.getInstance();
    while (rs.next()) {
      TSURL surl = surlFromString(rs.getString(1));
      TStatusCode sc = converter.toSTORM(rs.getInt(2));
          
      statusMap.put(surl, returnStatusFromStatusCode(sc));
    }
    
    return statusMap;

  }
  
  private Map<TSURL, TReturnStatus> getPtGSURLStatuses(TRequestToken token) {

    tokenSanityChecks(token);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "SELECT rg.sourceSURL, sg.statusCode"
        + "FROM request_queue rq JOIN (request_Get rg, status_Get sg) "
        + "ON (rg.request_queueID = rq.ID AND sg.ID = rg.ID)"
        + "WHERE ( rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      return buildStatusMap(rs);

    } catch (SQLException e) {
      String msg = String.format("%s: SQL error: %s", "getPtGSURLStatuses",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    } finally {
      closeStatetement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
  }

  private Map<TSURL, TReturnStatus> getPtPSURLStatuses(TRequestToken token) {

    tokenSanityChecks(token);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "SELECT rp.targetSURL, sp.statusCode"
        + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
        + "ON (rp.request_queueID = rq.ID AND sp.ID = rp.ID)"
        + "WHERE ( rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      return buildStatusMap(rs);

    } catch (SQLException e) {
    
      String msg = String.format("%s: SQL error: %s", "getPtPSURLStatuses",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);
   
    } finally {
      closeStatetement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }
   
  }

  private Map<TSURL, TReturnStatus> getBoLSURLStatuses(TRequestToken token) {
    tokenSanityChecks(token);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    try {
      con = getConnection();

      String query = "SELECT rb.sourceSURL, sb.statusCode"
        + "FROM request_queue rq JOIN (request_BoL rb, status_BoL sb) "
        + "ON (rb.request_queueID = rq.ID AND sb.ID = rb.ID)"
        + "WHERE ( rq.r_token = ? )";

      stat = con.prepareStatement(query);
      stat.setString(1, token.getValue());
      return buildStatusMap(rs);

    } catch (SQLException e) {
    
      String msg = String.format("%s: SQL error: %s", "getPtPSURLStatuses",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);
   
    } finally {
      closeStatetement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }

  }

  private Map<TSURL, TReturnStatus> filterSURLStatuses(
    Map<TSURL, TReturnStatus> statuses,
    List<TSURL> surls) {
    
    List<TSURL> surlsCopy = new ArrayList<TSURL>(surls);
    
    // Keep only the SURLs really requested. 
    statuses.keySet().retainAll(surls);
    
    // The surls that are not in the statuses key set
    // are those not linked to the actual token
    // (this is an error in the request)
    surlsCopy.removeAll(statuses.keySet());
    
    // Add a failure state for the surls that were
    // requested but are not linked to the token
    for (TSURL s: surlsCopy){
      TReturnStatus rs = returnStatusFromStatusCode(TStatusCode.SRM_FAILURE,
        "SURL not linked to passed request token.");
      statuses.put(s, rs);
    }
    
    return statuses;
  }
  
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token, 
    List<TSURL> surls) {

    TRequestType rt = RequestSummaryCatalog.getInstance().typeOf(token);
    
    switch (rt) {
    case PREPARE_TO_GET:
      return filterSURLStatuses(getPtGSURLStatuses(token), surls);

    case PREPARE_TO_PUT:
      return filterSURLStatuses(getPtPSURLStatuses(token), surls);

    case BRING_ON_LINE:
      return filterSURLStatuses(getBoLSURLStatuses(token), surls);

    default:
      String msg = String.format("Invalid request type for token %s: %s",
        token, rt.toString());
      throw new IllegalArgumentException(msg);
    }
  }
  
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token) {

    TRequestType rt = RequestSummaryCatalog.getInstance().typeOf(token);

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
      String msg = String.format("Invalid request type for token %s: %s",
        token, rt.toString());
      throw new IllegalArgumentException(msg);
    }
  }

  public boolean surlHasOngoingPtPs(TSURL surl, TRequestToken ptpRequestToken) {

    surlSanityChecks(surl);

    ResultSet rs = null;
    PreparedStatement stat = null;
    Connection con = null;

    try {

      con = getConnection();
      // We basically check whether there are active requests
      // that have the SURL in SRM_SPACE_AVAILABLE status
      String query = "SELECT rq.ID, rp.ID, sp.statusCode "
        + "FROM request_queue rq JOIN (request_Put rp, status_Put sp) "
        + "ON (rp.request_queueID=rq.ID AND sp.request_PutID=rp.ID) "
        + "WHERE ( rp.targetSURL = ? and sp.statusCode=24 )";

      if (ptpRequestToken != null) {
        query += " AND rq.r_token != ?";
      }

      stat = con.prepareStatement(query);
      stat.setString(1, surl.getSURLString());

      if (ptpRequestToken != null) {
        stat.setString(2, ptpRequestToken.getValue());
      }

      rs = stat.executeQuery();
      return rs.next();
    } catch (SQLException e) {
      String msg = String.format("%s: SQL error: %s", "surlHasOngoingPtPs",
        e.getMessage());
      log.error(msg, e);
      throw new RuntimeException(msg, e);
    } finally {
      closeStatetement(stat);
      closeResultSet(rs);
      closeConnection(con);
    }

  }

  @Override
  public boolean isSURLBusy(TSURL surl) {
    return false;
  }

  @Override
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean isSURLPinned(TSURL surl) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    
    
  }


  @Override
  public void abortRequest(TRequestToken token, String explanation) {
  	
    
    
  }

  private void updateStatus(TRequestType rt, 
  	TRequestToken token, TSURL surl, TStatusCode code, String explanation){
  	
  	switch(rt){
  	
  		case PREPARE_TO_GET:
  			PtGChunkCatalog.getInstance()
  				.updateStatus(token, surl, code, explanation);
  			break;
  			
  		case PREPARE_TO_PUT:
  			PtPChunkCatalog.getInstance()
  				.updateStatus(token, surl, code, explanation);
  			break;
  			
  		default:
  			throw new IllegalArgumentException("Unsupported request type for "
  				+ "updateStatus: "+rt.getValue());
  	}
  }
  
  private void abortPTGRequest(TRequestToken token, 
  	TSURL surl, String explanation) {

    
  }
  
  private void abortPTPRequest(TRequestToken token, 
  	TSURL surl, String explanation) {

    
  }
  
  private void abortBoLRequest(TRequestToken token, 
  	TSURL surl, String explanation) {

    
  }

  @Override
  public void abortRequestForSURL(TRequestToken token, TSURL surl,
    String explanation) {

    
    
  }

  @Override
  public void abortAllPutRequestsForSURL(TSURL surl, String explanation) {

  	
    
  }

  @Override
  public void abortAllGetRequestsForSURL(TSURL surl, String explanation) {

    
  }

	@Override
	public void failRequestForSURL(TRequestToken token, TSURL surl,
		TStatusCode code, String explanation) {

		
		
	}
}
