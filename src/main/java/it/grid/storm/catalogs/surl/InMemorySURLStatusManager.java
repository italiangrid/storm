package it.grid.storm.catalogs.surl;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;
import it.grid.storm.synchcall.surl.SURLStatusStore;
import it.grid.storm.synchcall.surl.SURLStatusStoreIF;
import it.grid.storm.synchcall.surl.UnknownSurlException;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemorySURLStatusManager extends DelegatingSURLStatusManager {

  public static final Logger LOGGER = LoggerFactory
    .getLogger(InMemorySURLStatusManager.class);

  private static final EnumSet<TStatusCode> busyStatuses = EnumSet
    .of(TStatusCode.SRM_SPACE_AVAILABLE);

  private static final EnumSet<TStatusCode> pinnedStatuses = EnumSet
    .of(TStatusCode.SRM_FILE_PINNED);

  public InMemorySURLStatusManager(SURLStatusManager delegate) {

    super(delegate);

  }

  @Override
  public boolean abortAllGetRequestsForSURL(GridUserInterface user, TSURL surl,
    String explanation) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;
    boolean someRequestAborted = (store.abortAllRequestForSURL(surl) != 0);

    return (someRequestAborted || super.abortAllGetRequestsForSURL(user, surl,
      explanation));
  }

  @Override
  public boolean abortAllPutRequestsForSURL(GridUserInterface user, TSURL surl,
    String explanation) {

    final SURLStatusStoreIF store = SURLStatusStore.INSTANCE;
    boolean someRequestAborted = (store.abortAllRequestForSURL(surl) != 0);

    return (someRequestAborted || super.abortAllPutRequestsForSURL(user, surl,
      explanation));
  }

  @Override
  public boolean abortRequest(GridUserInterface user, TRequestToken token,
    String explanation) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    if (store.hasEntryForToken(token)) {
      LOGGER.debug("abortRequest in memory for token {}", token);

      try {

        return (store.update(token, new TReturnStatus(TStatusCode.SRM_ABORTED,
          explanation)) != 0);

      } catch (UnknownSurlException e) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }

    }

    return super.abortRequest(user, token, explanation);
  }

  @Override
  public boolean abortRequestForSURL(GridUserInterface user,
    TRequestToken token, TSURL surl, String explanation) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    if (store.hasEntryForToken(token)) {
      LOGGER.debug("abortRequestForSURL in memory for token {} and surl {}",
        token);

      try {

        return (store.update(token, surl, new TReturnStatus(
          TStatusCode.SRM_ABORTED, explanation)) != 0);

      } catch (UnknownSurlException e) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    return super.abortRequestForSURL(user, token, surl, explanation);
  }

  @Override
  public boolean failRequestForSURL(GridUserInterface user,
    TRequestToken token, TSURL surl, TStatusCode code, String explanation) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    if (store.hasEntryForToken(token)) {
      LOGGER.debug("failRequestForSURL in memory: token={}, surl={}, code={}, "
        + "explanation={}", token, surl, code, explanation);

      try {

        return (store.update(token, surl, new TReturnStatus(code, explanation)) != 0);

      } catch (UnknownSurlException e) {
        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);
      }
    }

    LOGGER.debug("failRequestForSURL on DB: token={}, surl={}, code={}, "
      + "explanation={}", token, surl, code, explanation);

    return super.failRequestForSURL(user, token, surl, code, explanation);
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, List<TSURL> surls) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    Map<TSURL, TReturnStatus> surlsMap = store.getPinnedSURLsForUser(user,
      null, surls);

    if (!surlsMap.isEmpty()) {
      LOGGER.debug("getPinnedSURLsForUser user={}, surls={} got {}", user,
        surls, surlsMap);
      return surlsMap;
    }

    return super.getPinnedSURLsForUser(user, surls);
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(
    GridUserInterface user, TRequestToken token, List<TSURL> surls) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    Map<TSURL, TReturnStatus> surlsMap = store.getPinnedSURLsForUser(user,
      token, surls);

    if (!surlsMap.isEmpty()) {
      LOGGER.debug("getPinnedSURLsForUser user={}, token={}, surls={} got {}",
        user, surls, surlsMap);
      return surlsMap;
    }

    return super.getPinnedSURLsForUser(user, token, surls);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user, 
    TRequestToken token) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;
    if (store.hasEntryForToken(token)) {
      LOGGER.debug("getSURLStatuses from memory for token {}", token);
      return store.getSurlStatuses(user, token);
    }

    LOGGER.debug("getSURLStatuses from DB for token {}", token);
    return super.getSURLStatuses(user, token);
  }

  @Override
  public Map<TSURL, TReturnStatus> getSURLStatuses(GridUserInterface user,
    TRequestToken token,
    List<TSURL> surls) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    if (store.hasEntryForToken(token)) {
      LOGGER.debug("getSURLStatuses from memory for token {} and SURLs {}",
        token, surls);
      return store.getSurlStatuses(user,token, surls);
    }

    LOGGER.debug("getSURLStatuses from DB for token {} and SURLs {}", token,
      surls);

    return super.getSURLStatuses(user, token, surls);
  }

  private Map<TRequestToken, TReturnStatus> getSURLStatusesExcludingToken(
    TRequestToken token, TSURL surl) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    Map<TRequestToken, TReturnStatus> statusMap = null;

    try {
      statusMap = store.getSurlPerTokenStatuses(surl);
      statusMap.remove(token);
    } catch (IllegalArgumentException e) {
      LOGGER.warn(e.getMessage(), e);
    } catch (UnknownSurlException e) {
      LOGGER.debug(e.getMessage());
    }

    return statusMap;

  }

  private Collection<TReturnStatus> getSURLStatusList(GridUserInterface user,
    TSURL surl) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    Collection<TReturnStatus> statusList = null;

    try {
      statusList = store.getSurlStatuses(user, surl);
    } catch (IllegalArgumentException e) {
      LOGGER.warn(e.getMessage(), e);
    } catch (UnknownSurlException e) {
      LOGGER.debug(e.getMessage());
    }

    return statusList;
  }

  @Override
  public boolean isSURLBusy(TRequestToken requestTokenToExclude, TSURL surl) {

    final Map<TRequestToken, TReturnStatus> statusMap = getSURLStatusesExcludingToken(
      requestTokenToExclude, surl);

    if (statusMap != null && !statusMap.isEmpty()) {

      final boolean busyInMemory = isSURLBusy(surl, statusMap);

      if (busyInMemory) {
        LOGGER.debug("SURL {} BUSY in memory store.", surl);
        return true;
      }
    }

    final boolean busyOnDB = super.isSURLBusy(requestTokenToExclude, surl);

    if (busyOnDB) {
      LOGGER.debug("SURL {} BUSY on database.", surl);
    }

    return busyOnDB;

  }

  @Override
  public boolean isSURLBusy(TSURL surl) {

    final Collection<TReturnStatus> statusList = getSURLStatusList(null,surl);

    if (statusList != null && (!statusList.isEmpty())) {

      final boolean busyInMemory = isSURLBusy(surl, statusList);
      if (busyInMemory) {
        LOGGER.debug("SURL {} BUSY in memory store.", surl);
        return true;
      }
    }

    final boolean busyOnDB = super.isSURLBusy(surl);
    if (busyOnDB) {
      LOGGER.debug("SURL {} BUSY on database.", surl);
    }
    return busyOnDB;
  }

  private boolean isSURLBusy(TSURL surl, Collection<TReturnStatus> statusList) {

    for (TReturnStatus status : statusList) {
      if (busyStatuses.contains(status.getStatusCode())) {
        return true;
      }
    }
    return false;
  }

  private boolean isSURLBusy(TSURL surl,
    Map<TRequestToken, TReturnStatus> statusMap) {

    for (TReturnStatus status : statusMap.values()) {
      if (busyStatuses.contains(status.getStatusCode())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isSURLPinned(TSURL surl) {

    final Collection<TReturnStatus> statusList = getSURLStatusList(null,surl);

    if (statusList != null && !statusList.isEmpty()) {

      final boolean pinnedInMemory = isSURLPinned(surl, statusList);

      if (pinnedInMemory) {
        LOGGER.debug("SURL {} PINNED in memory store.", surl);
        return true;
      }
    }

    final boolean pinnedOnDB = super.isSURLPinned(surl);

    if (pinnedOnDB) {
      LOGGER.debug("SURL {} PINNED on database.", surl);
    }
    return pinnedOnDB;
  }

  private boolean isSURLPinned(TSURL surl, Collection<TReturnStatus> statusList) {

    for (TReturnStatus status : statusList) {
      if (pinnedStatuses.contains(status.getStatusCode())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void markSURLsReadyForRead(TRequestToken token, List<TSURL> surls) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    if (store.hasEntryForToken(token)) {
      LOGGER.debug("PutDone on SURLs on in memory cache for token {}", token);

      try {
        store.checkedUpdate(token, surls, TStatusCode.SRM_SPACE_AVAILABLE,
          TStatusCode.SRM_SUCCESS, "Put done. SURL ready.");

        return;

      } catch (Throwable e) {

        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);

      }
    }

    LOGGER.debug("PutDone on SURLs on DB for token {}", token);
    super.markSURLsReadyForRead(token, surls);
  }

  @Override
  public void releaseSURLs(GridUserInterface user, List<TSURL> surls) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    boolean statusUpdated = (store.checkedUpdate(user, surls,
      TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_RELEASED, "File released") != 0);

    if (statusUpdated) {
      LOGGER.debug("SURLs released in memory cache for user {}", user);
      return;
    }

    LOGGER.debug("Releasing SURLs on DB for user {}", user);
    super.releaseSURLs(user, surls);

  }

  @Override
  public void releaseSURLs(TRequestToken token, List<TSURL> surls) {

    SURLStatusStoreIF store = SURLStatusStore.INSTANCE;

    if (store.hasEntryForToken(token)) {
      LOGGER.debug("Releasing SURLs on in memory cache for token {}", token);

      try {

        store.checkedUpdate(token, surls, TStatusCode.SRM_FILE_PINNED,
          TStatusCode.SRM_RELEASED, "File released succesfully.");

        return;

      } catch (Throwable e) {

        LOGGER.error(e.getMessage(), e);
        throw new RuntimeException(e.getMessage(), e);

      }

    }

    LOGGER.debug("Releasing SURLs on DB for token {}", token);
    super.releaseSURLs(token, surls);
  }

}
