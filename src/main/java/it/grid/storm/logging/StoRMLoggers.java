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
