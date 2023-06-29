/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs.surl;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.List;
import java.util.Map;

/**
 * This interface provides methods to check and update the status of SURLs
 * managed by StoRM.
 * 
 */
public interface SURLStatusManager {

  /**
   * Aborts all ongoing get requests for a given surl
   * 
   * @param surl
   *          the surl for which the requests must be aborted
   * @param explanation
   *          a string explaining why the request was aborted
   * 
   * @return <code>true</code> if some request was aborted, <code>false</code>
   *         otherwise
   */
  public boolean abortAllGetRequestsForSURL(GridUserInterface user, TSURL surl,
    String explanation);

  /**
   * Aborts all ongoing put requests for a given surl
   * 
   * @param surl
   *          the surl for which the requests must be aborted
   * @param explanation
   *          a string explaining why the request was aborted
   * 
   * @return <code>true</code> if some request was aborted, <code>false</code>
   *         otherwise
   */
  public boolean abortAllPutRequestsForSURL(GridUserInterface user, TSURL surl,
    String explanation);

  /**
   * Aborts a request identified by a given token
   *
   * @param user
   *          the authenticated user
   * @param token
   *          the request token
   * @param explanation
   *          a string explaining why the request was aborted
   * 
   * @return <code>true</code> if some request was aborted, <code>false</code>
   *         otherwise
   */
  public boolean abortRequest(GridUserInterface user, TRequestToken token,
    String explanation);

  /**
   * Aborts a request for a given SURL with the given explanation
   * 
   * @param user
   *          the authenticated user
   * @param token
   *          the request token
   * @param surl
   *          the surl for which the request must be aborted
   * @param explanation
   *          a string explaining why the request was aborted
   * 
   * @return <code>true</code> if some request was aborted, <code>false</code>
   *         otherwise
   */
  public boolean abortRequestForSURL(GridUserInterface user,
    TRequestToken token, TSURL surl, String explanation);

  /**
   * Sets the failed state for a SURL in the request with the given token.
   * 
   * @param user
   *          the authenticated user
   * @param token
   *          the request token
   * @param surl
   *          the surl that has the failure
   * @param code
   *          the error code
   * @param explanation
   *          a string explaining the reason behind the failure
   * 
   * @return <code>true</code> if some request was failed, <code>false</code>
   *         otherwise
   */
  public boolean failRequestForSURL(GridUserInterface user,
    TRequestToken token, TSURL surl, TStatusCode code, String explanation);

  /**
   * Checks that the input surls are pinned in requests coming from user user
   * and returns a map of the status where surls may be pinned or have an error
   * status (which happens when one of the surl passed as argument is not found
   * in the storm db)
   * 
   * @param user
   * @param surls
   * @return
   */
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, List<TSURL> surls);

  /**
   * Checks that the input surls are pinned in the request coming from a user
   * with a given token and returns a map of the status where surls may be
   * pinned or have an error status (which happens when one of the surl passed
   * as argument is not linked to the passed token)
   * 
   * @param user
   * @param surls
   * @return
   */
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, TRequestToken token, List<TSURL> surls);

  /**
   * Return a map of the surls status associated to the request token
   * 
   * 
   * @param token
   * @return
   */
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user,
    TRequestToken token);

  /**
   * Return a map of the surls status associated to the request token
   * 
   * @param token
   * @param surls
   * 
   * @return
   */
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user,
    TRequestToken token,
    List<TSURL> surls);

  /**
   * 
   * Checks if there is an ongoing prepare to put operation on a given SURL
   * registered in the StoRM database. The search for the ongoing PtP will
   * exclude the request whose token is equal to the request token passed as
   * argument.
   * 
   * @param requestTokenToExclude
   *          the token of the request that will not be considered in the search
   *          for ongoing prepare to put operations on the given surl
   * @param surl
   *          the surl to be checked
   * 
   * @return <code>true</code> if an ongoing PtP has been found for the surl,
   *         <code>false</code> otherwise
   */
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl);

  /**
   * Checks if there is an ongoing prepare to put operation on a given SURL
   * registered in the StoRM database.
   * 
   * @param surl
   *          the surl to be checked
   * @return <code>true</code> if an ongoing PtP has been found for the surl,
   *         <code>false</code> otherwise
   * 
   */
  public boolean isSURLBusy(TSURL surl);

  /**
   * Checks if there is an ongoing prepare to get operation for a given SURL
   * registered in the StoRM database.
   * 
   * @param surl
   *          the surl to be checked
   * @return <code>true</code> if an ongoing PtG has been found for the surl,
   *         <code>false</code> otherwise
   */
  public boolean isSURLPinned(TSURL surl);

  /**
   * Marks the list of surls passed as argument as ready for read operations for
   * the given request token
   * 
   * @param token
   *          the request token
   * @param surls
   *          the surl to be marked as ready for read operations
   * @return the number of surls updated on db
   */
  public int markSURLsReadyForRead(TRequestToken token, List<TSURL> surls);

  /**
   * Releases a list of surls in all ongoing PtG requests registered in the
   * StoRM database and assigned to a specific user.
   * 
   * @param user
   *          the user
   * @param surls
   *          the surls to be released
   */
  public void releaseSURLs(GridUserInterface user, List<TSURL> surls);

  /**
   * Releases a list of surls for the PtG request identified by the given token
   * StoRM database.
   * 
   * @param token
   *          the request token
   * @param surls
   *          the surls to be released
   */
  public void releaseSURLs(TRequestToken token, List<TSURL> surls);

}
