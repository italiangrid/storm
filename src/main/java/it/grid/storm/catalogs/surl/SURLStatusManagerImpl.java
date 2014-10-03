package it.grid.storm.catalogs.surl;

import it.grid.storm.catalogs.BoLChunkCatalog;
import it.grid.storm.catalogs.CopyChunkCatalog;
import it.grid.storm.catalogs.PtGChunkCatalog;
import it.grid.storm.catalogs.PtPChunkCatalog;
import it.grid.storm.catalogs.RequestSummaryCatalog;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TRequestType;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.List;
import java.util.Map;

public class SURLStatusManagerImpl implements SURLStatusManager {

  @Override
  public void abortAllGetRequestsForSURL(TSURL surl, String explanation) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    dao.abortActivePtGsForSURL(surl, explanation);

  }

  @Override
  public void abortAllPutRequestsForSURL(TSURL surl, String explanation) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    dao.abortActivePtPsForSURL(surl, explanation);

  }

  @Override
  public void abortRequest(TRequestToken token, String explanation) {

    TRequestType requestType = RequestSummaryCatalog.getInstance()
      .typeOf(token);

    switch (requestType) {
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

    case COPY:
      CopyChunkCatalog.getInstance().updateFromPreviousStatus(token,
        TStatusCode.SRM_REQUEST_QUEUED, TStatusCode.SRM_ABORTED, explanation);
      break;

    case EMPTY:
      break;

    default:
      throw new IllegalArgumentException(
        "Abort not supported for request type: " + requestType);

    }
  }

  @Override
  public void abortRequestForSURL(TRequestToken token, TSURL surl,
    String explanation) {

    TRequestType requestType = RequestSummaryCatalog.getInstance()
      .typeOf(token);
    switch (requestType) {

    case PREPARE_TO_GET:
      PtGChunkCatalog.getInstance().updateStatus(token, surl,
        TStatusCode.SRM_ABORTED, explanation);
      break;
      
    case PREPARE_TO_PUT:
      PtPChunkCatalog.getInstance().updateStatus(token, surl,
        TStatusCode.SRM_ABORTED, explanation);
      break;

    default:
      throw new IllegalArgumentException("Unsupported abort for request type: "
        + requestType);
    }
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(TRequestToken token,
    List<TSURL> surls) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    return dao.getSURLStatuses(token, surls);
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

    final SURLStatusDAO dao = new SURLStatusDAO();
    dao.releaseSURLs(token, surls);
  }

  @Override
  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    final SURLStatusDAO dao = new SURLStatusDAO();
    dao.markSURLsReadyForRead(token, surls);

  }

  @Override
  public void failRequestForSURL(TRequestToken token, TSURL surl,
    TStatusCode code, String explanation) {

    TRequestType requestType = RequestSummaryCatalog.getInstance()
      .typeOf(token);

    switch (requestType) {

    case PREPARE_TO_PUT:
      PtPChunkCatalog.getInstance().updateStatus(token, surl,
        TStatusCode.SRM_AUTHORIZATION_FAILURE, explanation);
      break;

    default:
      throw new IllegalArgumentException("Unsupported request type: "
        + requestType);

    }

  }

}
