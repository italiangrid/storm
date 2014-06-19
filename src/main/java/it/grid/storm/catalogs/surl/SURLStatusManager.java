package it.grid.storm.catalogs.surl;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

import java.util.List;
import java.util.Map;


/**
 * This interface provides methods to check and update the status of SURLs
 * managed by StoRM.
 * 
 */
public interface SURLStatusManager {

  /**
   * Checks if there is an ongoing prepare to put operation on a given
   * SURL registered in the StoRM database.
   * 
   * @param surl the surl to be checked
   * @return <code>true</code> if an ongoing PtP has been found for the surl, 
   * <code>false</code> otherwise
   * 
   */
  public boolean isSURLBusy(TSURL surl);
  
  /**
   * 
   * Checks if there is an ongoing prepare to put operation on a given
   * SURL registered in the StoRM database. The search for the ongoing
   * PtP will exclude the request whose token is equal to the request
   * token passed as argument.  
   * @param requestTokenToExclude the token of the request that will
   * not be considered in the search for ongoing prepare to put operations on 
   * the given surl 
   * @param surl the surl to be checked
   * 
   * @return <code>true</code> if an ongoing PtP has been found for the surl, 
   * <code>false</code> otherwise
   */
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl);
  
  
  /**
   * Checks if there is an ongoing prepare to get operation for a given 
   * SURL registered in the StoRM database. 
   * 
   * @param surl the surl to be checked
   * @return <code>true</code> if an ongoing PtG has been found for the surl, 
   * <code>false</code> otherwise
   */
  public boolean isSURLPinned(TSURL surl);
  
  /**
   * Release surl in all ongoing PtG requests registered in the StoRM database.
   * 
   * @param surl the surl to be released
   */
  public void releaseSURL(TSURL surl);
  
  /**
   * Releases a list of surls in all ongoing PtG requests registered in the
   * StoRM database.
   * 
   * @param surls the surls to be released
   */
  public void releaseSURLs(List<TSURL> surls);
  
  /**
   * Releases a list of surls for the PtG request identified by the given token 
   * StoRM database.
   * 
   * @param token the request token
   * @param surls the surls to be released
   */
  public void releaseSURLs(TRequestToken token, List<TSURL> surls);
  
  /**
   * Marks the list of surls passed as argument as ready for read operations
   * for the given request token
   * @param token the request token
   * @param surls the surl to be marked as ready for read operations
   */
  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls);
  
  
  /**
   * Return a map of the surls status associated to the request token
   * 
   * 
   * @param token
   * @return
   */
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token);
  
  /**
   * Return a map of the surls status associated to the request token
   * @param token
   * @param surls
   * 
   * @return
   */
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token, 
    List<TSURL> surls);
  
  /**
   * Aborts a request identified by a given token
   * @param token the request token
   * @param explanation a string explaining why the request was aborted
   */
  public void abortRequest(TRequestToken token, String explanation);
  
  /**
   * Aborts a request for a given SURL with the given explanation
   * 
   * @param token the request token
   * @param surl the surl for which the request must be aborted
   * @param explanation a string explaining why the request was aborted
   */
  public void abortRequestForSURL(TRequestToken token, TSURL surl, 
    String explanation);
  
  /**
   * Aborts all ongoing put requests for a given surl
   * @param surl the surl for which the requests must be aborted
   * @param explanation a string explaining why the request was aborted
   */
  public void abortAllPutRequestsForSURL(TSURL surl, String explanation);
  
  /**
   * Aborts all ongoing get requests for a given surl
   * 
   * @param surl the surl for which the requests must be aborted
   * @param explanation a string explaining why the request was aborted
   */
  public void abortAllGetRequestsForSURL(TSURL surl, String explanation);
  
}
