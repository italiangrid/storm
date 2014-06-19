package it.grid.storm.catalogs.surl;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;

import java.util.List;
import java.util.Map;


public class SURLStatusManagerImpl implements SURLStatusManager {

  @Override
  public void abortAllGetRequestsForSURL(TSURL surl, String explanation) {

    // TODO Auto-generated method stub
    
  }

  @Override
  public void abortAllPutRequestsForSURL(TSURL surl, String explanation) {

    // TODO Auto-generated method stub
    
  }


  @Override
  public void abortRequest(TRequestToken token, String explanation) {

    // TODO Auto-generated method stub
    
  }

  @Override
  public void abortRequestForSURL(TRequestToken token, TSURL surl,
    String explanation) {

    // TODO Auto-generated method stub
    
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token,
    List<TSURL> surls) {
    
    return null;
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    return dao.getSURLStatuses(token);
  }

  @Override
  public boolean isSURLBusy(TSURL surl) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    return dao.surlHasOngoingPtPs(surl, null);
  }

  @Override
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    return dao.surlHasOngoingPtPs(surl, requestTokenToExclude);
  }

  @Override
  public boolean isSURLPinned(TSURL surl) {
    final SURLStatusDAO dao = new SURLStatusDAO();
    return dao.surlHasOngoingPtGs(surl);
  }

  

  @Override
  public void releaseSURL(TSURL surl) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    dao.releaseSURL(surl);
    
  }

  @Override
  public void releaseSURLs(List<TSURL> surls) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    dao.releaseSURLs(surls);
    
  }

  @Override
  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {
    
    
  }

  @Override
  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    
  }
  
  
}
