package it.grid.storm.catalogs.surl;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.List;
import java.util.Map;

public class DelegatingSURLStatusManager {

  final SURLStatusManager delegate;

  public DelegatingSURLStatusManager(SURLStatusManager delegate) {

    this.delegate = delegate;
  }

  public boolean isSURLBusy(TSURL surl) {

    return delegate.isSURLBusy(surl);
  }

  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl) {

    return delegate.isSURLBusy(requestTokenToExclude, surl);
  }

  public boolean isSURLPinned(TSURL surl) {

    return delegate.isSURLPinned(surl);
  }

  public void releaseSURL(TSURL surl) {

    delegate.releaseSURL(surl);
  }

  public void releaseSURLs(List<TSURL> surls) {

    delegate.releaseSURLs(surls);
  }

  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {

    delegate.releaseSURLs(token, surls);
  }

  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    delegate.markSURLsReadyForRead(token, surls);
  }

  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token) {

    return delegate.getSURLStatuses(token);
  }

  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token,
    List<TSURL> surls) {

    return delegate.getSURLStatuses(token, surls);
  }

  public void abortRequest(TRequestToken token, String explanation) {

    delegate.abortRequest(token, explanation);
  }

  public void abortRequestForSURL(TRequestToken token, TSURL surl,
    String explanation) {

    delegate.abortRequestForSURL(token, surl, explanation);
  }

  public void abortAllPutRequestsForSURL(TSURL surl, String explanation) {

    delegate.abortAllPutRequestsForSURL(surl, explanation);
  }

  public void abortAllGetRequestsForSURL(TSURL surl, String explanation) {

    delegate.abortAllGetRequestsForSURL(surl, explanation);
  }

  public void failRequestForSURL(TRequestToken token, TSURL surl,
    TStatusCode code, String explanation) {

    delegate.failRequestForSURL(token, surl, code, explanation);
  }

}
