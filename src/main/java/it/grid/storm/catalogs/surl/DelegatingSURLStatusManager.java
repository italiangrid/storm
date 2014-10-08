package it.grid.storm.catalogs.surl;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.List;
import java.util.Map;

public class DelegatingSURLStatusManager implements SURLStatusManager {

  final SURLStatusManager delegate;

  public DelegatingSURLStatusManager(SURLStatusManager delegate) {

    this.delegate = delegate;
  }

  @Override
  public void abortAllGetRequestsForSURL(TSURL surl, String explanation) {

    delegate.abortAllGetRequestsForSURL(surl, explanation);
  }

  @Override
  public void abortAllPutRequestsForSURL(TSURL surl, String explanation) {

    delegate.abortAllPutRequestsForSURL(surl, explanation);
  }

  @Override
  public void abortRequest(TRequestToken token, String explanation) {

    delegate.abortRequest(token, explanation);
  }

  @Override
  public void abortRequestForSURL(TRequestToken token, TSURL surl,
    String explanation) {

    delegate.abortRequestForSURL(token, surl, explanation);
  }

  @Override
  public void failRequestForSURL(TRequestToken token, TSURL surl,
    TStatusCode code, String explanation) {

    delegate.failRequestForSURL(token, surl, code, explanation);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token) {

    return delegate.getSURLStatuses(token);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token,
    List<TSURL> surls) {

    return delegate.getSURLStatuses(token, surls);
  }

  @Override
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl) {

    return delegate.isSURLBusy(requestTokenToExclude, surl);
  }

  @Override
  public boolean isSURLBusy(TSURL surl) {

    return delegate.isSURLBusy(surl);
  }

  @Override
  public boolean isSURLPinned(TSURL surl) {

    return delegate.isSURLPinned(surl);
  }

  @Override
  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    delegate.markSURLsReadyForRead(token, surls);
  }

  @Override
  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {

    delegate.releaseSURLs(token, surls);
  }

  @Override
  public void releaseSURLs(GridUserInterface user, List<TSURL> surls) {

    delegate.releaseSURLs(user, surls);
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, List<TSURL> surls) {

    return delegate.getPinnedSURLsForUser(user, surls);

  }
}
