package it.grid.storm.synchcall.surl;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public enum SURLStatusStore implements SURLStatusStoreIF {

  INSTANCE;

  static class Entry {

    public static Entry from(TRequestToken token, GridUserInterface usr,
      Map<TSURL, TReturnStatus> statuses) {

      return new Entry(token, usr, statuses);
    }

    final TRequestToken token;
    final GridUserInterface user;

    final Map<TSURL, TReturnStatus> surlStatuses;

    private Entry(TRequestToken tok, GridUserInterface usr,
      Map<TSURL, TReturnStatus> statuses) {

      this.token = tok;
      this.user = usr;
      this.surlStatuses = statuses;
    }

    @Override
    public boolean equals(Object obj) {

      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Entry other = (Entry) obj;
      if (surlStatuses == null) {
        if (other.surlStatuses != null)
          return false;
      } else if (!surlStatuses.equals(other.surlStatuses))
        return false;
      if (token == null) {
        if (other.token != null)
          return false;
      } else if (!token.equals(other.token))
        return false;
      return true;
    }

    @Override
    public int hashCode() {

      final int prime = 31;
      int result = 1;
      result = prime * result
        + ((surlStatuses == null) ? 0 : surlStatuses.hashCode());
      result = prime * result + ((token == null) ? 0 : token.hashCode());
      return result;
    }

    @Override
    public String toString() {

      return "SURLStore.Entry [token=" + token + ", user=" + user
        + ", surlStatuses=" + surlStatuses + "]";
    }

  }

  final Logger logger = LoggerFactory.getLogger(SURLStatusStore.class);

  final Cache<TRequestToken, Entry> statusStore;

  final EnumSet<TStatusCode> interestingStatuses = EnumSet.of(
    TStatusCode.SRM_SPACE_AVAILABLE, TStatusCode.SRM_FILE_BUSY,
    TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_REQUEST_QUEUED,
    TStatusCode.SRM_REQUEST_INPROGRESS);

  final ScheduledExecutorService cacheStatsLoggerService = Executors
    .newScheduledThreadPool(1);

  private SURLStatusStore() {

    statusStore = CacheBuilder.newBuilder().maximumSize(1000).recordStats()
      .expireAfterWrite(10L, TimeUnit.MINUTES).concurrencyLevel(1)
      .removalListener(new RemovalListener<TRequestToken, Entry>() {

        @Override
        public void onRemoval(
          RemovalNotification<TRequestToken, Entry> notification) {

          if (notification.wasEvicted()) {
            logger.debug("SURLStatusStore: Entry {} evicted. Cause: {}",
              notification.getValue(), notification.getCause());
          }
        }
      }).build();

    Runnable cacheStatsLogger = new Runnable() {

      @Override
      public void run() {

        logger.debug("SURLStatusStore status: size={}, stats={}",
          statusStore.size(), statusStore.stats());

      }
    };

    cacheStatsLoggerService.scheduleWithFixedDelay(cacheStatsLogger, 1L, 1L,
      TimeUnit.MINUTES);
  }

  @Override
  public void checkedUpdate(TRequestToken requestToken, List<TSURL> surls,
    TStatusCode requiredStatusCode, TStatusCode newStatusCode,
    String explanation) throws IllegalArgumentException, UnknownTokenException,
    ExpiredTokenException, UnknownSurlException {

    logger.debug("checkedUpdate: token={}, surls={}, requiredStatusCode={}, "
      + "newStatusCode={}, explanation={}", requestToken, surls,
      requiredStatusCode, newStatusCode, explanation);

    Entry e = statusStore.getIfPresent(requestToken);

    if (e == null) {
      logger.warn("Token not found in store: {}", requestToken);
      throw new UnknownSurlException("Token not found in store: "
        + requestToken.getValue());
    }

    for (TSURL s : surls) {
      if (!e.surlStatuses.containsKey(s)) {
        throw new UnknownSurlException(String.format(
          "SURL %s not linked to request token %s", s, requestToken));
      }

      TStatusCode inCacheStatus = e.surlStatuses.get(s).getStatusCode();

      if (!inCacheStatus.equals(requiredStatusCode)) {
        logger.warn("checkedUpdate: status not updated for surl {}. "
          + "inCacheStatus does not match requiredStatus. {} != {}", s,
          inCacheStatus, requiredStatusCode);
      } else {
        e.surlStatuses.put(s, new TReturnStatus(newStatusCode, explanation));
      }
    }

    if (!hasInterestingStatus(e.surlStatuses)) {
      logger.debug("Evicting entry {}. No interesting statuses.", e);
      statusStore.invalidate(requestToken);
    }

  }

  @Override
  public Map<TRequestToken, TReturnStatus> getSurlPerTokenStatuses(TSURL surl)
    throws IllegalArgumentException, UnknownSurlException {

    Map<TRequestToken, TReturnStatus> statusMap = new HashMap<TRequestToken, TReturnStatus>();

    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {
      if (e.getValue().surlStatuses.containsKey(surl)) {
        statusMap.put(e.getValue().token, e.getValue().surlStatuses.get(surl));
      }

    }

    return statusMap;
  }

  @Override
  public Map<TSURL, TReturnStatus> getSurlStatuses(TRequestToken token) {

    Entry e = statusStore.getIfPresent(token);

    if (e != null) {
      return e.surlStatuses;
    }
    return null;

  }

  @Override
  public Map<TSURL, TReturnStatus> getSurlStatuses(TRequestToken token,
    List<TSURL> surls) {

    return getSurlStatuses(token);
  }

  @Override
  public Collection<TReturnStatus> getSurlStatuses(TSURL surl)
    throws UnknownSurlException, IllegalArgumentException {

    List<TReturnStatus> statuses = new ArrayList<TReturnStatus>();

    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {
      if (e.getValue().surlStatuses.containsKey(surl)) {
        statuses.add(e.getValue().surlStatuses.get(surl));
      }
    }

    return statuses;
  }

  @Override
  public boolean hasEntryForToken(TRequestToken requestToken) {

    return statusStore.getIfPresent(requestToken) != null;
  }

  private boolean hasInterestingStatus(Map<TSURL, TReturnStatus> surlStatuses) {

    for (TReturnStatus s : surlStatuses.values()) {
      if (interestingStatuses.contains(s.getStatusCode())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void store(TRequestToken requestToken, GridUserInterface user,
    HashMap<TSURL, TReturnStatus> surlStatuses)
    throws IllegalArgumentException, TokenDuplicationException {

    Entry e = Entry.from(requestToken, user, surlStatuses);

    if (!hasInterestingStatus(surlStatuses)) {
      logger.debug("SURLStatusStore. Refusing to store: {}. Useless statuses.");
      return;
    }

    logger.debug("SURLStatusStore. Storing: {}", e);
    statusStore.put(requestToken, e);
  }

  @Override
  public void store(TRequestToken requestToken,
    HashMap<TSURL, TReturnStatus> surlStatuses)
    throws IllegalArgumentException, TokenDuplicationException {

    store(requestToken, null, surlStatuses);

  }

  @Override
  public void update(TRequestToken requestToken, List<TSURL> surls,
    TStatusCode newStatusCode, String explanation)
    throws IllegalArgumentException, UnknownTokenException,
    ExpiredTokenException, UnknownSurlException {

    logger.debug(
      "update: token={}, surls={}, newStatusCode={}, explanation={}",
      requestToken, surls, newStatusCode, explanation);

    Entry e = statusStore.getIfPresent(requestToken);

    if (e == null) {
      logger.warn("Token not found in store: {}", requestToken);
      throw new UnknownSurlException("Token not found in store: "
        + requestToken.getValue());
    }

    for (TSURL s : surls) {
      if (!e.surlStatuses.containsKey(s)) {
        throw new UnknownSurlException(String.format(
          "SURL %s not linked to request token %s", s, requestToken));
      }
      e.surlStatuses.put(s, new TReturnStatus(newStatusCode, explanation));
    }

    if (!hasInterestingStatus(e.surlStatuses)) {
      logger.debug("Evicting entry {}. No interesting statuses.", e);
      statusStore.invalidate(requestToken);
    }

  }

  @Override
  public void update(TRequestToken requestToken, TStatusCode newStatusCode,
    String explanation) throws UnknownSurlException {

    logger.debug("update: token={}, newStatusCode={}, explanation={}",
      requestToken, newStatusCode, explanation);

    Entry e = statusStore.getIfPresent(requestToken);

    if (e == null) {
      logger.warn("Token not found in store: {}", requestToken);
      throw new UnknownSurlException("Token not found in store: "
        + requestToken.getValue());
    }

    for (TSURL s : e.surlStatuses.keySet()) {
      e.surlStatuses.put(s, new TReturnStatus(newStatusCode, explanation));
    }

    if (!hasInterestingStatus(e.surlStatuses)) {
      logger.debug("Evicting entry {}. No interesting statuses.", e);
      statusStore.invalidate(requestToken);
    }

  }

  @Override
  public void update(TRequestToken requestToken, TSURL surl,
    TStatusCode newStatusCode) throws IllegalArgumentException,
    UnknownTokenException, ExpiredTokenException, UnknownSurlException {

    update(requestToken, Arrays.asList(surl), newStatusCode, null);
  }

  @Override
  public void update(TRequestToken requestToken, TSURL surl,
    TStatusCode newStatusCode, String explanation)
    throws IllegalArgumentException, UnknownTokenException,
    ExpiredTokenException, UnknownSurlException {

    update(requestToken, Arrays.asList(surl), newStatusCode, explanation);

  }

  @Override
  public int abortAllRequestForSURL(TSURL surl) {

    final List<TRequestToken> toBeRemoved = new ArrayList<TRequestToken>();

    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {
      if (e.getValue().surlStatuses.containsKey(surl)) {

        e.getValue().surlStatuses.remove(surl);

        if (e.getValue().surlStatuses.isEmpty()) {
          toBeRemoved.add(e.getKey());

        }
      }
    }

    final int numAbortedRequests = toBeRemoved.size();

    for (TRequestToken t : toBeRemoved) {
      statusStore.invalidate(t);
    }

    return numAbortedRequests;
  }

}
