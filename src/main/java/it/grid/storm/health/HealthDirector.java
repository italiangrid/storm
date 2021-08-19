/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package it.grid.storm.health;

import org.slf4j.Logger;

import it.grid.storm.config.Configuration;
import it.grid.storm.logging.StoRMLoggers;

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

  public static int timeToLiveLogEventInSec =
      Configuration.getInstance().getPerformanceLogbookTimeInterval();

  public static void initializeDirector() {

    bookKeepingEnabled = Configuration.getInstance().getBookKeepingEnabled();
    if (bookKeepingEnabled) {
      bookKeepingConfigured = true;
    }

    int statusPeriod = Configuration.getInstance().getHearthbeatPeriod();

    bornInstant = System.currentTimeMillis();
    healthMonitorIstance = new HealthMonitor(1, statusPeriod);

    // Setting performance rate
    performanceMonitorEnabled = Configuration.getInstance().getPerformanceMeasuring();
    if (performanceMonitorEnabled) {
      int glanceTimeInterval = Configuration.getInstance().getPerformanceGlanceTimeInterval();

      LOGGER.debug("----- Performance GLANCE Time Interval = {}", glanceTimeInterval);
      LOGGER.debug("----- Performance LOGBOOK Time Interval = {}", timeToLiveLogEventInSec);

      healthMonitorIstance.initializePerformanceMonitor(timeToLiveLogEventInSec,
          glanceTimeInterval);

    }

    initialized = true;

  }

  /**
   * 
   * @return Logger
   */
  public static Logger getLogger() {

    return LOGGER;
  }

  /**
   * 
   * @return Logger
   */
  public static Logger getHealthLogger() {

    return HEARTLOG;
  }

  /**
   * 
   * @return Logger
   */
  public static Logger getBookkeepingLogger() {

    return BOOKKEEPING;
  }

  /**
   * 
   * @return Logger
   */
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

  /**
   * 
   * @return Logger
   */
  public static Logger getBookKeepingLogger() {

    return BOOKKEEPING;
  }

  /**
   * 
   * @return Namespace
   */
  public static HealthMonitor getHealthMonitor() {

    if (!(initialized)) {
      initializeDirector();
    }
    return healthMonitorIstance;
  }

  public static long getBornInstant() {

    if (!(initialized)) {
      initializeDirector();
    }
    return bornInstant;
  }

}
