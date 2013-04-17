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

	public static int timeToLiveLogEventInSec = Configuration.getInstance()
		.getPerformanceLogbookTimeInterval();

	/**
	 * 
	 * @param testingMode
	 *          boolean
	 */
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
		performanceMonitorEnabled = Configuration.getInstance()
			.getPerformanceMeasuring();
		if (performanceMonitorEnabled) {
			// configurePerformanceMonitor(testingMode);
			int glanceTimeInterval = Configuration.getInstance()
				.getPerformanceGlanceTimeInterval();

			LOGGER.debug("----- Performance GLANCE Time Interval = "
				+ glanceTimeInterval);
			LOGGER.debug("----- Performance LOGBOOK Time Interval = "
				+ timeToLiveLogEventInSec);

			healthMonitorIstance.initializePerformanceMonitor(
				timeToLiveLogEventInSec, glanceTimeInterval);
			// healthMonitorIstance.setPerformanceEnabled(true);
		}
		// else
		// {
		// healthMonitorIstance.setPerformanceEnabled(false);
		// }

		initialized = true;

	}

	/**
	 * 
	 * @param testingMode
	 *          boolean
	 */

	/**
	 * private static void configureHealthLog(boolean testingMode) { FileAppender
	 * healthFileAppender = null; PatternLayout layout = new
	 * PatternLayout(getHealthPatternLayout()); //Recovery of HEALTH File Name
	 * String healthFile =
	 * Configuration.getInstance().getHealthElectrocardiogramFile(); try {
	 * healthFileAppender = new FileAppender(layout, healthFile);
	 * healthFileAppender.activateOptions();
	 * HEARTLOG.addAppender(healthFileAppender); HEARTLOG.setAdditivity(false); if
	 * (testingMode) { HEARTLOG.setLevel( Level.DEBUG); } else {
	 * HEARTLOG.setLevel( Level.INFO); } } catch (IOException ex) {
	 * LOGGER.error("Unable to configure Health log." + ex.getMessage()); } }
	 **/

	/**
	 * 
	 * @param testingMode
	 *          boolean
	 */

	/**
	 * private static void configureBookKeeping(boolean testingMode) {
	 * bookKeepingConfigured = true; DailyRollingFileAppender bookKeepingAppender
	 * = null; PatternLayout layout = new
	 * PatternLayout(getBookKeppingPatternLayout()); //Recovery of BOOK KEEPING
	 * LOG File Name String bookKeepingFile =
	 * Configuration.getInstance().getBookKeepingLogFile(); String datePattern =
	 * "'.'yyyy-MM-dd"; try { bookKeepingAppender = new
	 * DailyRollingFileAppender(layout, bookKeepingFile, datePattern);
	 * BOOKKEEPING.addAppender(bookKeepingAppender);
	 * BOOKKEEPING.setAdditivity(false); bookKeepingAppender.activateOptions(); if
	 * (testingMode) { BOOKKEEPING.setLevel( Level.DEBUG); } else {
	 * BOOKKEEPING.setLevel( Level.INFO); } } catch (IOException ex) {
	 * LOGGER.error("Unable to configure Book Keeping log." + ex.getMessage());
	 * bookKeepingConfigured = false; } }
	 **/

	/**
	 * health.performance.mesauring.enabled health.performance.log.filename
	 * health.performance.log.verbosity health.performance.glance.timeInterval
	 * health.performance.logbook.timeInterval
	 * 
	 * @param testingMode
	 *          boolean
	 */
	/**
	 * private static void configurePerformanceMonitor(boolean testingMode) {
	 * performanceMonitorConfigured = true; DailyRollingFileAppender
	 * performanceAppender = null; PatternLayout layout = new
	 * PatternLayout(getPerformanceMonitoringPatternLayout()); //Recovery of
	 * PERFORMANCE MONITOR LOG File Name String performanceFile =
	 * Configuration.getInstance().getPerformanceMonitoringLogFile(); String
	 * datePattern = "'.'yyyy-MM-dd"; try { performanceAppender = new
	 * DailyRollingFileAppender(layout, performanceFile, datePattern);
	 * PERFLOG.addAppender(performanceAppender); PERFLOG.setAdditivity(false);
	 * performanceAppender.activateOptions(); if (testingMode) { PERFLOG.setLevel(
	 * Level.DEBUG); } else { PERFLOG.setLevel( Level.INFO); }
	 * 
	 * 
	 * } catch (IOException ex) {
	 * LOGGER.error("Unable to configure Performance Monitor log." +
	 * ex.getMessage()); performanceMonitorConfigured = false; }
	 * PERFLOG.debug("--- PERF LOGGER Configured ---"); }
	 **/

	/**
	 * 
	 * @return ArrayList
	 */
	/**
	 * private static ArrayList<FileAppender> getFileAppenders() {
	 * ArrayList<FileAppender> fileAppenders = new ArrayList<FileAppender>();
	 * Enumeration en = LOGGER.getAllAppenders(); int nbrOfAppenders = 0; while
	 * (en.hasMoreElements()) { nbrOfAppenders++; Object appender =
	 * en.nextElement(); if (appender instanceof FileAppender) {
	 * fileAppenders.add((FileAppender)appender); } } return fileAppenders; }
	 **/

	/**
	 * @return String
	 */

	private static String getHealthPatternLayout() {

		/**
		 * @todo : Retrieve Patter Layout from Configuration ..
		 */
		String pattern = "[%d{ISO8601}]: %m%n";
		return pattern;
	}

	/**
	 * @return String
	 */
	private static String getBookKeppingPatternLayout() {

		/**
		 * @todo : Retrieve Patter Layout from Configuration ..
		 */
		String pattern = "[%d{ISO8601}]: %-5p [%t] %x -%m%n";
		return pattern;
	}

	/**
	 * @return String
	 */
	private static String getPerformanceMonitoringPatternLayout() {

		/**
		 * @todo : Retrieve Patter Layout from Configuration ..
		 */
		String pattern = "[%d{ISO8601}]: %m%n";
		return pattern;
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
			initializeDirector(false);
		}
		return healthMonitorIstance;
	}

	/**
	 * 
	 * @return Namespace
	 */
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
