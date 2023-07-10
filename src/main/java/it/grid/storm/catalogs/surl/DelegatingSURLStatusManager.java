/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
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
  public boolean abortAllGetRequestsForSURL(
      GridUserInterface user, TSURL surl, String explanation) {

    return delegate.abortAllGetRequestsForSURL(user, surl, explanation);
  }

  @Override
  public boolean abortAllPutRequestsForSURL(
      GridUserInterface user, TSURL surl, String explanation) {

    return delegate.abortAllPutRequestsForSURL(user, surl, explanation);
  }

  @Override
  public boolean abortRequest(GridUserInterface user, TRequestToken token, String explanation) {

    return delegate.abortRequest(user, token, explanation);
  }

  @Override
  public boolean abortRequestForSURL(
      GridUserInterface user, TRequestToken token, TSURL surl, String explanation) {

    return delegate.abortRequestForSURL(user, token, surl, explanation);
  }

  @Override
  public boolean failRequestForSURL(
      GridUserInterface user,
      TRequestToken token,
      TSURL surl,
      TStatusCode code,
      String explanation) {

    return delegate.failRequestForSURL(user, token, surl, code, explanation);
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
      GridUserInterface user, List<TSURL> surls) {

    return delegate.getPinnedSURLsForUser(user, surls);
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
      GridUserInterface user, TRequestToken token, List<TSURL> surls) {

    return delegate.getPinnedSURLsForUser(user, token, surls);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user, TRequestToken token) {

    return delegate.getSURLStatuses(user, token);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(
      GridUserInterface user, TRequestToken token, List<TSURL> surls) {

    return delegate.getSURLStatuses(user, token, surls);
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
  public int markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    return delegate.markSURLsReadyForRead(token, surls);
  }

  @Override
  public void releaseSURLs(GridUserInterface user, List<TSURL> surls) {

    delegate.releaseSURLs(user, surls);
  }

  @Override
  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {

    delegate.releaseSURLs(token, surls);
  }
}
