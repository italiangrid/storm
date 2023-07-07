/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import java.util.Hashtable;
import java.util.List;
import java.util.Timer;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

import it.grid.storm.config.Configuration;

public class HealthMonitor {

  private static HealthMonitor instance = null;

  private Logger HEARTLOG = HealthDirector.HEARTLOG;
  private Logger PERFLOG = HealthDirector.getPerformanceLogger();

  private Timer healthTimer;
  private Hashtable<String, BookKeeper> bookKeepers;

  private long period;
  private long bornInstant;

  public synchronized static HealthMonitor getInstance() {

    if (instance == null) {
      instance = new HealthMonitor(Configuration.getInstance());
    }
    return instance;
  }

  public static void init() {
    instance = new HealthMonitor(Configuration.getInstance());
  }

  private HealthMonitor(Configuration config) {

    healthTimer = new Timer();
    bookKeepers = new Hashtable<String, BookKeeper>();

    if (!config.isHearthbeatBookkeepingEnabled() && !config.isHearthbeatPerformanceMeasuringEnabled()) {
      return;
    }

    if (config.isHearthbeatPerformanceMeasuringEnabled()) {

      int logTimeInterval = config.getHearthbeatPerformanceLogbookTimeInterval();
      int defaultGlangeTimeInterval= config.getHearthbeatPerformanceGlanceTimeInterval();

      if (defaultGlangeTimeInterval > logTimeInterval) {
        HealthDirector.getPerformanceLogger()
          .warn("WARNING: Log Book has the time interval lower than Glance time interval!");
      }
      PerformanceBookKeeper pbk =
          new PerformanceBookKeeper(logTimeInterval, defaultGlangeTimeInterval);
      period = pbk.getGlanceWindowInMSec();
      healthTimer.scheduleAtFixedRate(new PerformancePulse(), 0, period);
      PERFLOG.info("Set PERFORMANCE MONITOR in Timer Task (PERIOD:{})", period);
      bookKeepers.put(PerformanceBookKeeper.KEY, pbk);
      PERFLOG.info("--- PERFORMANCE MONITOR Initialized");

    }
    
    if (config.isHearthbeatPerformanceMeasuringEnabled()) {

      period = config.getHearthbeatPeriod();
      healthTimer.scheduleAtFixedRate(new Hearthbeat(), 1000L, period * 1000L);
      HEARTLOG.info("Set HEARTHBEAT in Timer Task (DELAY: {}, PERIOD: {})", 1000L, period);
      bookKeepers.put(SimpleBookKeeper.KEY, new SimpleBookKeeper());
      HEARTLOG.info("HEART MONITOR Initialized");
    }

    bornInstant = System.currentTimeMillis();
  }

  public List<BookKeeper> getBookKeepers() {

    return Lists.newArrayList(bookKeepers.values());
  }

  public PerformanceBookKeeper getPerformanceBookKeeper() {

    if (bookKeepers.containsKey(PerformanceBookKeeper.KEY)) {
      return (PerformanceBookKeeper) bookKeepers.get(PerformanceBookKeeper.KEY);
    }
    return null;
  }

  public SimpleBookKeeper getSimpleBookKeeper() {

    if (bookKeepers.containsKey(SimpleBookKeeper.KEY)) {
      return (SimpleBookKeeper) bookKeepers.get(SimpleBookKeeper.KEY);
    }
    return null;
  }

  public long getPeriod() {

    return period;
  }

  public long getBornInstant() {

    return bornInstant;
  }
}
