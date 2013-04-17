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
package it.grid.storm.startup;

import it.grid.storm.logging.LoggingReloadTask;

import java.util.Timer;

/**
 * @author zappi
 * 
 */
public class InitLoggers {

	public static void main(String[] args) throws Exception {

		final Timer taskTimer = new Timer(true);
		String fileSeparator = System.getProperty("file.separator");
		String logFile = System.getProperty("user.dir") + fileSeparator + "conf"
			+ fileSeparator + "new-logging.xml";
		System.out.println("LogFile = " + logFile);
		initializeLogging(logFile, taskTimer);

	}

	/**
	 * Initializes the logging system and starts the process to watch for config
	 * file changes.
	 * 
	 * @param loggingConfigFilePath
	 *          path to the logging configuration file
	 * @param reloadTasks
	 *          timer controlling the reloading of tasks
	 */
	private static void initializeLogging(String loggingConfigFilePath,
		Timer reloadTasks) {

		LoggingReloadTask reloadTask = new LoggingReloadTask(loggingConfigFilePath);
		int refreshPeriod = 5 * 60 * 1000; // check/reload every 5 minutes
		reloadTask.run();
		reloadTasks.scheduleAtFixedRate(reloadTask, refreshPeriod, refreshPeriod);
	}

}
