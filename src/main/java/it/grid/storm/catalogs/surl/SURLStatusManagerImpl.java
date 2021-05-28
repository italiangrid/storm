package it.grid.storm.catalogs.surl;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.persistence.dao.SURLStatusDAO;
import it.grid.storm.persistence.impl.mysql.SURLStatusDAOMySql;
import it.grid.storm.persistence.model.RequestSummaryData;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import static it.grid.storm.srm.types.TStatusCode.SRM_ABORTED;

import java.util.List;
import java.util.Map;

public class SURLStatusManagerImpl implements SURLStatusManager {

  @Override
  public boolean abortAllGetRequestsForSURL(GridUserInterface user, TSURL surl,
    String explanation) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.abortActivePtGsForSURL(user, surl, explanation);

  }

  @Override
  public boolean abortAllPutRequestsForSURL(GridUserInterface user, TSURL surl,
    String explanation) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.abortActivePtPsForSURL(user, surl, explanation);

  }

  @Override
  public boolean abortRequest(GridUserInterface user, TRequestToken token,
    String explanation) {

    RequestSummaryData request = lookupAndCheckRequest(user, token);

    switch (request.requestType()) {
    case PREPARE_TO_GET:

      PtGChunkCatalog.getInstance().updateFromPreviousStatus(token,
        TStatusCode.SRM_REQUEST_QUEUED, TStatusCode.SRM_ABORTED, explanation);
      break;

    case PREPARE_TO_PUT:
      PtPChunkCatalog.getInstance().updateFromPreviousStatus(token,
        TStatusCode.SRM_REQUEST_QUEUED, TStatusCode.SRM_ABORTED, explanation);
      break;

    case BRING_ON_LINE:
      BoLChunkCatalog.getInstance().updateFromPreviousStatus(token,
        TStatusCode.SRM_REQUEST_QUEUED, TStatusCode.SRM_ABORTED, explanation);
      break;

    case EMPTY:
      break;

    default:
      throw new IllegalArgumentException(
        "Abort not supported for request type: " + request.requestType());

    }

    return true;
  }

  @Override
  public boolean abortRequestForSURL(GridUserInterface user,
    TRequestToken token, TSURL surl, String explanation) {

    RequestSummaryData request = lookupAndCheckRequest(user, token);

    switch (request.requestType()) {

      case PREPARE_TO_GET:
        PtGChunkCatalog.getInstance().updateStatus(token, surl, SRM_ABORTED, explanation);
        break;

      case PREPARE_TO_PUT:
        PtPChunkCatalog.getInstance().updateStatus(token, surl, SRM_ABORTED, explanation);
        break;

      default:
        throw new IllegalArgumentException(
            "Unsupported abort for request type: " + request.requestType());
    }

    return true;
  }

  private void authzCheck(GridUserInterface user, RequestSummaryData request) {

    if (!request.gridUser().getDn().equals(user.getDn())) {
      String errorMsg = String.format("User %s is not authorized to abort "
        + "request %s", user.getDn(), request.requestToken());
      throw new AuthzException(errorMsg);
    }
  }

  @Override
  public boolean failRequestForSURL(GridUserInterface user,
    TRequestToken token, TSURL surl, TStatusCode code, String explanation) {

    RequestSummaryData request = lookupAndCheckRequest(user, token);

    switch (request.requestType()) {

    case PREPARE_TO_PUT:
      PtPChunkCatalog.getInstance().updateStatus(token, surl,
        TStatusCode.SRM_AUTHORIZATION_FAILURE, explanation);
      break;

    default:
      throw new IllegalArgumentException("Unsupported request type: "
        + request.requestType());

    }

    return true;
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, List<TSURL> surls) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.getPinnedSURLsForUser(user, surls);
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, TRequestToken token, List<TSURL> surls) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.getPinnedSURLsForUser(user, token, surls);

  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user, 
    TRequestToken token) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.getSURLStatuses(token);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user,
    TRequestToken token,
    List<TSURL> surls) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.getSURLStatuses(token, surls);
  }

  @Override
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.surlHasOngoingPtPs(surl, requestTokenToExclude);
  }

  @Override
  public boolean isSURLBusy(TSURL surl) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.surlHasOngoingPtPs(surl, null);
  }

  @Override
  public boolean isSURLPinned(TSURL surl) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.surlHasOngoingPtGs(surl);
  }

  private RequestSummaryData lookupAndCheckRequest(GridUserInterface user,
    TRequestToken token) {

    RequestSummaryData request = lookupRequest(token);
    authzCheck(user, request);
    return request;
  }

  private RequestSummaryData lookupRequest(TRequestToken token) {

    RequestSummaryData request = RequestSummaryCatalog.getInstance()
      .find(token);

    if (request == null) {
      throw new IllegalArgumentException("No request found matching token "
        + token);
    }

    return request;
  }

  @Override
  public int markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    return dao.markSURLsReadyForRead(token, surls);

  }

  @Override
  public void releaseSURLs(GridUserInterface user, List<TSURL> surls) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    dao.releaseSURLs(user, surls);

  }

  @Override
  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {

    final SURLStatusDAO dao = SURLStatusDAOMySql.getInstance();
    dao.releaseSURLs(token, surls);
  }

}
