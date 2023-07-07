/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import java.util.List;

import org.slf4j.Logger;

import com.google.common.collect.Lists;

public abstract class BookKeeper {

  protected Logger bookKeepingLog = HealthDirector.getBookKeepingLogger();
  protected Logger performanceLog = HealthDirector.getPerformanceLogger();

  protected List<LogEvent> logbook = Lists.newArrayList();

  public abstract void addLogEvent(LogEvent logEvent);

  public synchronized void cleanLogBook() {
    logbook.clear();
  }

  protected void logDebug(String msg) {

    if (HealthDirector.isBookKeepingEnabled()) {
      bookKeepingLog.debug("BK: {}", msg);
    }
  }

  protected void logInfo(String msg) {

    if (HealthDirector.isBookKeepingEnabled()) {
      bookKeepingLog.info(msg);
    }
  }

}
