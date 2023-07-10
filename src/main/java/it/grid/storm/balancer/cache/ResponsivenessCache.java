/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.balancer.cache;

import com.google.common.collect.Maps;
import it.grid.storm.balancer.Node;
import it.grid.storm.config.Configuration;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ResponsivenessCache {
  INSTANCE(Configuration.getInstance().getServerPoolStatusCheckTimeout());

  private static final Logger log = LoggerFactory.getLogger(ResponsivenessCache.class);

  private Map<Node, ResponsivenessCacheEntry> cache;
  private long entryLifetime;

  private ResponsivenessCache(long entryLifetimeMillisec) {
    this.entryLifetime = entryLifetimeMillisec;
    cache = Maps.newHashMap();
  }

  public void invalidate() {
    cache.clear();
  }

  public Responsiveness getResponsiveness(Node node) {
    Optional<ResponsivenessCacheEntry> entry = getEntry(node);
    if (entry.isPresent()) {
      ResponsivenessCacheEntry e = entry.get();
      if (e.isExpired()) {
        log.debug("Cache entry {} expired. Refreshing..", e);
        return e.refresh();
      }
      log.debug("Found valid cache entry for {}", e);
      return e.getStatus();
    } else {
      log.debug("Missing cache entry for {}. Adding and refreshing..", node);
      ResponsivenessCacheEntry e = new ResponsivenessCacheEntry(node, entryLifetime);
      log.debug("Adding cache entry {} for node {} ..", e, node);
      cache.put(node, e);
      return e.getStatus();
    }
  }

  public boolean isCached(Node n) {
    return getEntry(n).isPresent();
  }

  private Optional<ResponsivenessCacheEntry> getEntry(Node node) {
    return Optional.ofNullable(cache.get(node));
  }
}
