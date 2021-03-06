package it.grid.storm.synchcall.surl;

import it.grid.storm.authz.AuthzException;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TStatusCode;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

    private Entry(TRequestToken tok, GridUserInterface usr, Map<TSURL, TReturnStatus> statuses) {

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
      result = prime * result + ((surlStatuses == null) ? 0 : surlStatuses.hashCode());
      result = prime * result + ((token == null) ? 0 : token.hashCode());
      return result;
    }

    @Override
    public String toString() {

      return "SURLStore.Entry [token=" + token + ", user=" + user + ", surlStatuses=" + surlStatuses
          + "]";
    }

  }

  final Logger logger = LoggerFactory.getLogger(SURLStatusStore.class);

  final Cache<TRequestToken, Entry> statusStore;

  final EnumSet<TStatusCode> interestingStatuses = EnumSet.of(TStatusCode.SRM_SPACE_AVAILABLE,
      TStatusCode.SRM_FILE_BUSY, TStatusCode.SRM_FILE_PINNED, TStatusCode.SRM_REQUEST_QUEUED,
      TStatusCode.SRM_REQUEST_INPROGRESS);

  final ScheduledExecutorService cacheStatsLoggerService = Executors.newScheduledThreadPool(1);

  private SURLStatusStore() {

    statusStore = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .recordStats()
      .expireAfterWrite(10L, TimeUnit.MINUTES)
      .concurrencyLevel(1)
      .removalListener(new RemovalListener<TRequestToken, Entry>() {

        @Override
        public void onRemoval(RemovalNotification<TRequestToken, Entry> notification) {

          if (notification.wasEvicted()) {
            logger.debug("SURLStatusStore: Entry {} evicted. Cause: {}", notification.getValue(),
                notification.getCause());
          }
        }
      })
      .build();

    Runnable cacheStatsLogger = new Runnable() {

      @Override
      public void run() {

        logger.debug("SURLStatusStore status: size={}, stats={}", statusStore.size(),
            statusStore.stats());

      }
    };

    cacheStatsLoggerService.scheduleWithFixedDelay(cacheStatsLogger, 1L, 1L, TimeUnit.MINUTES);
  }

  @Override
  public int abortAllRequestForSURL(TSURL surl) {

    final List<TRequestToken> toBeRemoved = Lists.newArrayList();

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

  public void authzCheck(Entry e, GridUserInterface user) {

    if (user != null && e.user != null) {

      if (!user.getDn().equals(e.user.getDn())) {
        String errorMsg = String.format("User %s is not authorized to access request with token %s",
            user.getDn(), e.token);

        throw new AuthzException(errorMsg);
      }
    }

    if (user == null && e.user != null) {
      throw new AuthzException("Anonymous user cannot access request with token " + e.token);
    }
  }

  @Override
  public int checkedUpdate(GridUserInterface user, List<TSURL> surls,
      TStatusCode requiredStatusCode, TStatusCode newStatusCode, String explanation) {

    logger.debug(
        "checkedUpdate: user={}, surls={}, requiredStatusCode={}, "
            + "newStatusCode={}, explanation={}",
        user, surls, requiredStatusCode, newStatusCode, explanation);

    int updateCount = 0;
    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {

      Entry entry = e.getValue();

      if (entry.user != null) {

        if (e.getValue().user.getDn().equals(user.getDn())) {

          for (TSURL s : surls) {
            if (entry.surlStatuses.containsKey(s)) {

              TStatusCode inCacheStatus = entry.surlStatuses.get(s).getStatusCode();

              if (!requiredStatusCode.equals(inCacheStatus)) {

                logger.warn(
                    "checkedUpdate: status not updated for surl {}. "
                        + "inCacheStatus does not match requiredStatus. {} != {}",
                    s, inCacheStatus, requiredStatusCode);

              } else {

                entry.surlStatuses.put(s, new TReturnStatus(newStatusCode, explanation));
                updateCount++;
              }

            }
          }

          if (!hasInterestingStatus(entry.surlStatuses)) {
            logger.debug("Evicting entry {}. No interesting statuses.", entry);
            statusStore.invalidate(entry.token);
          }
        }
      }
    }

    return updateCount;
  }

  @Override
  public int checkedUpdate(TRequestToken requestToken, List<TSURL> surls,
      TStatusCode requiredStatusCode, TStatusCode newStatusCode, String explanation)
      throws UnknownTokenException, ExpiredTokenException,
      UnknownSurlException {

    logger.debug(
        "checkedUpdate: token={}, surls={}, requiredStatusCode={}, "
            + "newStatusCode={}, explanation={}",
        requestToken, surls, requiredStatusCode, newStatusCode, explanation);

    Entry e = statusStore.getIfPresent(requestToken);

    int updateCount = 0;

    if (e == null) {
      logger.warn("Token not found in store: {}", requestToken);
      throw new UnknownSurlException("Token not found in store: " + requestToken.getValue());
    }

    for (TSURL s : surls) {
      if (!e.surlStatuses.containsKey(s)) {
        throw new UnknownSurlException(
            String.format("SURL %s not linked to request token %s", s, requestToken));
      }

      TStatusCode inCacheStatus = e.surlStatuses.get(s).getStatusCode();

      if (!inCacheStatus.equals(requiredStatusCode)) {
        logger.warn(
            "checkedUpdate: status not updated for surl {}. "
                + "inCacheStatus does not match requiredStatus. {} != {}",
            s, inCacheStatus, requiredStatusCode);
      } else {
        e.surlStatuses.put(s, new TReturnStatus(newStatusCode, explanation));
        updateCount++;
      }
    }

    if (!hasInterestingStatus(e.surlStatuses)) {
      logger.debug("Evicting entry {}. No interesting statuses.", e);
      statusStore.invalidate(requestToken);
    }

    return updateCount;

  }

  private boolean entryUserMatchesRequestUser(Entry e, GridUserInterface user) {

    if (user == null && e.user == null) {
      return true;
    }

    return (user != null && e.user != null && user.getDn().equals(e.user.getDn()));
  }

  @Override
  public Map<TSURL, TReturnStatus> getPinnedSURLsForUser(GridUserInterface user,
      TRequestToken token, List<TSURL> surls) {

    Map<TSURL, TReturnStatus> statusMap = Maps.newHashMap();

    if (token != null) {
      Entry entry = statusStore.getIfPresent(token);

      if (entry != null) {
        if (entry.user.getDn().equals(user.getDn())) {

          for (TSURL s : surls) {
            if (entry.surlStatuses.containsKey(s)
                && entry.surlStatuses.get(s).getStatusCode().equals(TStatusCode.SRM_FILE_PINNED)) {
              statusMap.put(s, entry.surlStatuses.get(s));
            }
          }
        }
      }

      return statusMap;
    }

    // No token passed, we have to lookup all requests for the user
    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {
      Entry entry = e.getValue();
      if (entry.user.getDn().equals(user.getDn())) {

        for (TSURL s : surls) {

          if (entry.surlStatuses.containsKey(s)
              && entry.surlStatuses.get(s).getStatusCode().equals(TStatusCode.SRM_FILE_PINNED)) {
            statusMap.put(s, entry.surlStatuses.get(s));
          }
        }
      }
    }

    return statusMap;
  }

  @Override
  public Map<TRequestToken, TReturnStatus> getSurlPerTokenStatuses(TSURL surl)
      throws UnknownSurlException {

    Map<TRequestToken, TReturnStatus> statusMap = Maps.newHashMap();

    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {
      if (e.getValue().surlStatuses.containsKey(surl)) {
        statusMap.put(e.getValue().token, e.getValue().surlStatuses.get(surl));
      }

    }

    return statusMap;
  }

  @Override
  public Map<TSURL, TReturnStatus> getSurlStatuses(GridUserInterface user, TRequestToken token) {

    Entry e = statusStore.getIfPresent(token);

    if (e != null) {
      authzCheck(e, user); // throws exception if check fails.
      return e.surlStatuses;
    }
    return null;

  }

  @Override
  public Map<TSURL, TReturnStatus> getSurlStatuses(GridUserInterface user, TRequestToken token,
      List<TSURL> surls) {

    return getSurlStatuses(user, token);
  }

  @Override
  public Collection<TReturnStatus> getSurlStatuses(GridUserInterface user, TSURL surl)
      throws UnknownSurlException {

    List<TReturnStatus> statuses = Lists.newArrayList();

    for (Map.Entry<TRequestToken, Entry> e : statusStore.asMap().entrySet()) {
      Entry entry = e.getValue();
      if (entry.surlStatuses.containsKey(surl)) {

        if (entryUserMatchesRequestUser(entry, user)) {
          statuses.add(e.getValue().surlStatuses.get(surl));
        }
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
      Map<TSURL, TReturnStatus> surlStatuses) {

    Entry e = Entry.from(requestToken, user, surlStatuses);

    if (!hasInterestingStatus(surlStatuses)) {
      logger.debug("SURLStatusStore. Refusing to store: {}. Useless statuses.");
      return;
    }

    logger.debug("SURLStatusStore. Storing: {}", e);
    statusStore.put(requestToken, e);
  }

  @Override
  public int update(TRequestToken requestToken, List<TSURL> surls, TReturnStatus status)
      throws UnknownTokenException, ExpiredTokenException, UnknownSurlException {

    logger.debug("update: token={}, surls={}, status={}", requestToken, surls, status);

    Entry e = statusStore.getIfPresent(requestToken);
    int updateCount = 0;

    if (e == null) {
      logger.warn("Token not found in store: {}", requestToken);
      throw new UnknownSurlException("Token not found in store: " + requestToken.getValue());
    }

    for (TSURL s : surls) {
      if (!e.surlStatuses.containsKey(s)) {
        throw new UnknownSurlException(
            String.format("SURL %s not linked to request token %s", s, requestToken));
      }
      e.surlStatuses.put(s, status);
      updateCount++;
    }

    if (!hasInterestingStatus(e.surlStatuses)) {
      logger.debug("Evicting entry {}. No interesting statuses.", e);
      statusStore.invalidate(requestToken);
    }

    return updateCount;

  }

  @Override
  public int update(TRequestToken requestToken, TReturnStatus status) throws UnknownSurlException {

    logger.debug("update: token={}, status={}", requestToken, status);

    Entry e = statusStore.getIfPresent(requestToken);
    int updateCount = 0;

    if (e == null) {
      logger.warn("Token not found in store: {}", requestToken);
      throw new UnknownSurlException("Token not found in store: " + requestToken.getValue());
    }

    for (TSURL s : e.surlStatuses.keySet()) {
      e.surlStatuses.put(s, status);
      updateCount++;
    }

    if (!hasInterestingStatus(e.surlStatuses)) {
      logger.debug("Evicting entry {}. No interesting statuses.", e);
      statusStore.invalidate(requestToken);
    }

    return updateCount;
  }

  @Override
  public int update(TRequestToken requestToken, TSURL surl, TReturnStatus status)
      throws UnknownTokenException, ExpiredTokenException, UnknownSurlException {

    return update(requestToken, Arrays.asList(surl), status);
  }

}
