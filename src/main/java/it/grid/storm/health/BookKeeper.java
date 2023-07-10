/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import java.util.ArrayList;
import org.slf4j.Logger;

public abstract class BookKeeper {

  protected Logger bookKeepingLog = HealthDirector.getBookKeepingLogger();
  protected Logger performanceLog = HealthDirector.getPerformanceLogger();

  protected ArrayList<LogEvent> logbook = new ArrayList<LogEvent>();

  public abstract void addLogEvent(LogEvent logEvent);

  public synchronized void cleanLogBook() {
    logbook.clear();
  }

  protected void logDebug(String msg) {

    if ((HealthDirector.isBookKeepingConfigured()) && (HealthDirector.isBookKeepingEnabled())) {
      bookKeepingLog.debug("BK: {}", msg);
    }
  }

  protected void logInfo(String msg) {

    if ((HealthDirector.isBookKeepingConfigured()) && (HealthDirector.isBookKeepingEnabled())) {
      bookKeepingLog.info(msg);
    }
  }
}
