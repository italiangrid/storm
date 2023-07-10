/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.health;

import it.grid.storm.config.Configuration;
import it.grid.storm.logging.StoRMLoggers;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;

public class HealthDirector {

  public static final Logger LOGGER = StoRMLoggers.getHBLogger();
  public static final Logger HEARTLOG = StoRMLoggers.getHBLogger();
  private static final Logger BOOKKEEPING = StoRMLoggers.getBKLogger();
  private static final Logger PERFLOG = StoRMLoggers.getPerfLogger();

  private static boolean initialized = false;
  private static HealthMonitor healthMonitorIstance = null;
  private static boolean bookKeepingConfigured = false;
  private static boolean bookKeepingEnabled = false;

  private static boolean performanceMonitorConfigured = false;
  private static boolean performanceMonitorEnabled = false;

  private static long bornInstant = -1L;
  private static String bornInstantStr = null;

  public static int timeToLiveLogEventInSec =
      Configuration.getInstance().getPerformanceLogbookTimeInterval();

  /** @param testingMode boolean */
  public static void initializeDirector(boolean testingMode) {

    // configureHealthLog(testingMode);

    bookKeepingEnabled = Configuration.getInstance().getBookKeepingEnabled();
    if (bookKeepingEnabled) {
      // configureBookKeeping(testingMode);
      bookKeepingConfigured = true;
    }

    int statusPeriod = Configuration.getInstance().getHearthbeatPeriod();
    if (testingMode) {
      statusPeriod = 5;
    }

    // Record the born of StoRM instance
    bornInstant = System.currentTimeMillis();
    Date date = new Date(bornInstant);
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH.mm.ss");
    bornInstantStr = formatter.format(date);

    healthMonitorIstance = new HealthMonitor(1, statusPeriod); // Start after 1
    // sec

    // Setting performance rate
    performanceMonitorEnabled = Configuration.getInstance().getPerformanceMeasuring();
    if (performanceMonitorEnabled) {
      // configurePerformanceMonitor(testingMode);
      int glanceTimeInterval = Configuration.getInstance().getPerformanceGlanceTimeInterval();

      LOGGER.debug("----- Performance GLANCE Time Interval = " + glanceTimeInterval);
      LOGGER.debug("----- Performance LOGBOOK Time Interval = " + timeToLiveLogEventInSec);

      healthMonitorIstance.initializePerformanceMonitor(
          timeToLiveLogEventInSec, glanceTimeInterval);
    }

    initialized = true;
  }

  private static String getHealthPatternLayout() {

    /** @todo : Retrieve Patter Layout from Configuration .. */
    String pattern = "[%d{ISO8601}]: %m%n";
    return pattern;
  }

  /** @return String */
  private static String getBookKeppingPatternLayout() {

    /** @todo : Retrieve Patter Layout from Configuration .. */
    String pattern = "[%d{ISO8601}]: %-5p [%t] %x -%m%n";
    return pattern;
  }

  /** @return String */
  private static String getPerformanceMonitoringPatternLayout() {

    /** @todo : Retrieve Patter Layout from Configuration .. */
    String pattern = "[%d{ISO8601}]: %m%n";
    return pattern;
  }

  /** @return Logger */
  public static Logger getLogger() {

    return LOGGER;
  }

  /** @return Logger */
  public static Logger getHealthLogger() {

    return HEARTLOG;
  }

  /** @return Logger */
  public static Logger getBookkeepingLogger() {

    return BOOKKEEPING;
  }

  /** @return Logger */
  public static Logger getPerformanceLogger() {

    return PERFLOG;
  }

  public static boolean isBookKeepingConfigured() {

    return bookKeepingConfigured;
  }

  public static boolean isBookKeepingEnabled() {

    return bookKeepingEnabled;
  }

  public static boolean isPerformanceMonitorConfigured() {

    return performanceMonitorConfigured;
  }

  public static boolean isPerformanceMonitorEnabled() {

    return performanceMonitorEnabled;
  }

  /** @return Logger */
  public static Logger getBookKeepingLogger() {

    return BOOKKEEPING;
  }

  /** @return Namespace */
  public static HealthMonitor getHealthMonitor() {

    if (!(initialized)) {
      initializeDirector(false);
    }
    return healthMonitorIstance;
  }

  /** @return Namespace */
  public static HealthMonitor getHealthMonitor(boolean testingMode) {

    if (!(initialized)) {
      initializeDirector(testingMode);
    }
    return healthMonitorIstance;
  }

  public static long getBornInstant(boolean testingMode) {

    if (!(initialized)) {
      initializeDirector(testingMode);
    }
    return bornInstant;
  }

  public static String getBornInstantStr(boolean testingMode) {

    if (!(initialized)) {
      initializeDirector(testingMode);
    }
    return bornInstantStr;
  }

  public static long getBornInstant() {

    if (!(initialized)) {
      initializeDirector(false);
    }
    return bornInstant;
  }

  public static String getBornInstantStr() {

    if (!(initialized)) {
      initializeDirector(false);
    }
    return bornInstantStr;
  }
}
