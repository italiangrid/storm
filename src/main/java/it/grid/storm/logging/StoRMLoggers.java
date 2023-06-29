/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

/**
 * @author zappi
 * 
 */
public class StoRMLoggers {

	private static Logger bookkeepingLogger;
	private static Logger heartbeatLogger;
	private static Logger performanceLogger;
	private static Logger stdoutLogger;
	private static Logger stderrLogger;
	private static boolean initDone = false;

	private static void initLoggers() {

		LoggerContext loggerContext = (LoggerContext) LoggerFactory
			.getILoggerFactory();
		bookkeepingLogger = loggerContext.getLogger("bookkeeping");
		heartbeatLogger = loggerContext.getLogger("health");
		performanceLogger = loggerContext.getLogger("performance");
		stdoutLogger = loggerContext.getLogger("system.out");
		stderrLogger = loggerContext.getLogger("system.err");
	}

	public static Logger getBKLogger() {

		if (!initDone) {
			initLoggers();
		}
		return bookkeepingLogger;
	}

	public static Logger getHBLogger() {

		if (!initDone) {
			initLoggers();
		}
		return heartbeatLogger;
	}

	public static Logger getPerfLogger() {

		if (!initDone) {
			initLoggers();
		}
		return performanceLogger;
	}

	public static Logger getStdoutLogger() {

		if (!initDone) {
			initLoggers();
		}
		return stdoutLogger;
	}

	public static Logger getStderrLogger() {

		if (!initDone) {
			initLoggers();
		}
		return stderrLogger;
	}

}
