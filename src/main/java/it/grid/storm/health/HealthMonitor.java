/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import org.slf4j.Logger;

public class HealthMonitor {

  private Logger HEARTLOG = HealthDirector.HEARTLOG;
  private Logger PERFLOG = HealthDirector.getPerformanceLogger();

  private Timer healthTimer = null;
  private Hashtable<String, BookKeeper> bookKeepers;

  public static int perfGlanceTimeInterval = 15; // 15 sec

  public HealthMonitor(int delay, int period) {

    healthTimer = new Timer();
    this.heartbeat(delay * 1000, period * 1000);

    // Create the Book Keepers
    bookKeepers = new Hashtable<String, BookKeeper>();

    // Add the Simple BookKeeper
    bookKeepers.put(SimpleBookKeeper.KEY, new SimpleBookKeeper());

    HEARTLOG.info("HEART MONITOR Initialized");
  }

  public void initializePerformanceMonitor(int logTimeInterval, int defaultGlangeTimeInterval) {

    if (defaultGlangeTimeInterval > logTimeInterval) {
      HealthDirector.getPerformanceLogger()
          .warn("WARNING: Log Book has the time " + "interval lower than Glance time interval!");
    }
    // Add the Performance BookKeeper
    PerformanceBookKeeper pbk =
        new PerformanceBookKeeper(logTimeInterval, defaultGlangeTimeInterval);
    bookKeepers.put(PerformanceBookKeeper.KEY, pbk);

    long pulseTimeInterval = pbk.getGlanceWindowInMSec();
    // this.perfEnabled = true;
    healthTimer.scheduleAtFixedRate(new PerformancePulse(), 0, pulseTimeInterval);
    PERFLOG.info("Set PERFORMANCE MONITOR in Timer Task (PERIOD:{})", perfGlanceTimeInterval);

    PERFLOG.info("--- PERFORMANCE MONITOR Initialized");
  }

  public ArrayList<BookKeeper> getBookKeepers() {

    return new ArrayList<BookKeeper>(bookKeepers.values());
  }

  public PerformanceBookKeeper getPerformanceBookKeeper() {

    if (bookKeepers.containsKey(PerformanceBookKeeper.KEY)) {
      return (PerformanceBookKeeper) bookKeepers.get(PerformanceBookKeeper.KEY);
    } else {
      return null;
    }
  }

  public SimpleBookKeeper getSimpleBookKeeper() {

    if (bookKeepers.containsKey(SimpleBookKeeper.KEY)) {
      return (SimpleBookKeeper) bookKeepers.get(SimpleBookKeeper.KEY);
    } else {
      return null;
    }
  }

  public void heartbeat(int delay, int period) {

    healthTimer.scheduleAtFixedRate(new Hearthbeat(), delay, period);
    HEARTLOG.info("Set HEARTHBEAT in Timer Task (DELAY: {}, PERIOD: {})", delay, period);
  }
}
