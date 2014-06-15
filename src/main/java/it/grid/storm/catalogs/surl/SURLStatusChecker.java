package it.grid.storm.catalogs.surl;


/**
 * This interface provides methods to check the status of SURLs
 * managed by StoRM.
 * 
 */
public interface SURLStatusChecker {

  /**
   * Checks if there is an ongoing prepare to put operation on a given
   * SURL registered in the StoRM database.
   * 
   * @param surl the surl to be checked
   * @return <code>true</code> if an ongoing PtP has been found for the surl, 
   * <code>false</code> otherwise
   * 
   */
  public boolean isSURLBusy(String surl);
  
  /**
   * 
   * Checks if there is an ongoing prepare to put operation on a given
   * SURL registered in the StoRM database. The search for the ongoing
   * PtP will exclude the request whose token is equal to the request
   * token passed as argument.  
   * 
   * @param surl the surl to be checked
   * @param requestTokenToExclude the token of the request that will
   * not be considered in the search for ongoing prepare to put operations on 
   * the given surl 
   * @return <code>true</code> if an ongoing PtP has been found for the surl, 
   * <code>false</code> otherwise
   */
  public boolean isSURLBusy(String surl, String requestTokenToExclude);
  
  
  /**
   * Checks if there is an ongoing prepare to get operation for a given 
   * SURL registered in the StoRM database. 
   * 
   * @param surl the surl to be checked
   * @return <code>true</code> if an ongoing PtG has been found for the surl, 
   * <code>false</code> otherwise
   */
  public boolean isSURLPinned(String surl);
  
}
