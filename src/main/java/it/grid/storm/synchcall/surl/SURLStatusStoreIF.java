package it.grid.storm.synchcall.surl;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SURLStatusStoreIF {

  public int abortAllRequestForSURL(TSURL surl);

  public int checkedUpdate(GridUserInterface user,
    List<TSURL> surls,
    TStatusCode requiredStatusCode,
    TStatusCode newStatusCode, 
    String explanation);

  public int checkedUpdate(TRequestToken requestToken, List<TSURL> surls,
    TStatusCode requiredStatusCode, TStatusCode newStatusCode,
    String explanation) throws IllegalArgumentException, UnknownTokenException,
    ExpiredTokenException, UnknownSurlException;

  public Map<TRequestToken, TReturnStatus> getSurlPerTokenStatuses(TSURL surl)
    throws IllegalArgumentException, UnknownSurlException;

  public Map<TSURL, TReturnStatus> getSurlStatuses(GridUserInterface user,
    TRequestToken token);

  public Map<TSURL, TReturnStatus> getSurlStatuses(GridUserInterface user,
    TRequestToken token, 
    List<TSURL> surls);
  
  public Collection<TReturnStatus> getSurlStatuses(GridUserInterface user,
    TSURL surl)
    throws UnknownSurlException, IllegalArgumentException;
  
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(GridUserInterface user,
    TRequestToken token, List<TSURL> surls);
  
  public boolean hasEntryForToken(TRequestToken requestToken);
  
  public void store(TRequestToken requestToken, GridUserInterface user,
    HashMap<TSURL, TReturnStatus> surlStatuses)
    throws IllegalArgumentException, TokenDuplicationException;
  
  public void store(TRequestToken requestToken,
    HashMap<TSURL, TReturnStatus> surlStatuses)
    throws IllegalArgumentException, TokenDuplicationException;
  
  public int update(TRequestToken requestToken, List<TSURL> surls,
    TStatusCode newStatusCode, String explanation)
    throws IllegalArgumentException, UnknownTokenException,
    ExpiredTokenException, UnknownSurlException;
  
  public int update(TRequestToken requestToken, 
    TStatusCode newStatusCode, String explanation) 
      throws UnknownSurlException;
  
  public int update(TRequestToken requestToken, TSURL surl,
    TStatusCode newStatusCode) throws IllegalArgumentException,
    UnknownTokenException, ExpiredTokenException, UnknownSurlException;
  
  public int update(TRequestToken requestToken, TSURL surl,
    TStatusCode newStatusCode, String explanation)
    throws IllegalArgumentException, UnknownTokenException,
    ExpiredTokenException, UnknownSurlException;

}