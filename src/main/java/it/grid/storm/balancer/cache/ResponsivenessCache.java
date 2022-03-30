package it.grid.storm.balancer.cache;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import it.grid.storm.balancer.Node;
import it.grid.storm.config.Configuration;

public enum ResponsivenessCache {

  INSTANCE(Configuration.getInstance().getServerPoolStatusCheckTimeout());

  private static final Logger log = LoggerFactory.getLogger(ResponsivenessCache.class);

  private Map<Node, ResponsivenessCacheEntry> cache = Maps.newHashMap();
  private long entryLifetime;

  private ResponsivenessCache(long entryLifetimeMillisec) {
    this.entryLifetime = entryLifetimeMillisec;
  }

  public Responsiveness getResponsiveness(Node node) {
    Optional<ResponsivenessCacheEntry> entry = getEntry(node);
    if (entry.isPresent()) {
      if (entry.get().isExpired()) {
        log.debug("Cache entry {} expired. Refreshing..", entry.toString());
        return entry.get().refresh();
      }
      log.debug("Found valid cache entry for {}", entry.toString());
      return entry.get().getStatus();
    } else {
      log.debug("Missing cache entry for {}. Adding and refreshing..", node);
      ResponsivenessCacheEntry e = new ResponsivenessCacheEntry(node, entryLifetime);
      cache.put(node, e);
      return e.getStatus();
    }
  }

  private Optional<ResponsivenessCacheEntry> getEntry(Node node) {
    return Optional.ofNullable(cache.get(node));
  }


}
