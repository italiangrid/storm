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

import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.DirectorException;
import it.grid.storm.https.HTTPPluginManager;
import it.grid.storm.https.HTTPSPluginInterface;
import it.grid.storm.info.SpaceInfoManager;
import it.grid.storm.logging.LoggingReloadTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zappi
 */
public class Bootstrap {

	private static final Timer reloadTasks = new Timer(true);
	private static Logger log = LoggerFactory.getLogger(Bootstrap.class);

	/**
	 * Initializes the logging system and starts the process to watch for config
	 * file changes.
	 * 
	 * @param loggingConfigFilePath
	 *          path to the logging configuration file
	 * @param reloadTasks
	 *          timer controlling the reloading of tasks
	 */
	public static void initializeLogging(String loggingConfigFilePath) {

		LoggingReloadTask reloadTask = new LoggingReloadTask(loggingConfigFilePath);
		int refreshPeriod = 5 * 60 * 1000; // check/reload every 5 minutes
		reloadTask.run();
		reloadTasks.scheduleAtFixedRate(reloadTask, refreshPeriod, refreshPeriod);
	}

	public static void initializePathAuthz(String pathAuthzDBFileName)
		throws BootstrapException {

		try {
			AuthzDirector.initializePathAuthz(pathAuthzDBFileName);
		} catch (DirectorException e) {
			log.error("Unable to initialize the AuthzDirector : " + e);
			throw new BootstrapException("Unable to initialize the AuthzDirector");
		}
	}

	public static void initializeUsedSpace() {

		int numberOfSpaceInitializated = SpaceInfoManager.getInstance()
			.initSpaceFromINIFile();
		log.info("Initializated '" + numberOfSpaceInitializated
			+ "' SA from 'used-space.ini'");

		int failures = SpaceInfoManager.getInstance().updateSpaceUsed();
		int numberQuotas = SpaceInfoManager.getInstance().howManyQuotas();
		log.info("Computed '" + numberQuotas + "' GPFS quotas.");
		if (failures > 0) {
			log.error("Some QUOTA was failed! Check logs!");
		}
		int numberOfBgDU = SpaceInfoManager.howManyBackgroundDU();
		log.info("Submitted '" + numberOfBgDU + "' background DU tasks.");
	}

	/**
	 * @param httpsInterfaceClassName
	 * @param log
	 */
	public static void initializeAclManager(String httpsInterfaceClassName,
		Logger log) {

		log.debug("Obtaining an instance of class " + httpsInterfaceClassName);
		Class httpsInterfaceClass = null;
		try {
			httpsInterfaceClass = Class.forName(httpsInterfaceClassName);
		} catch (ClassNotFoundException e) {
			log.error("Unable to load https plugin class" + httpsInterfaceClassName
				+ " . ClassNotFoundException : " + e.getMessage());
			return;
		}
		if (!HTTPSPluginInterface.class.isAssignableFrom(httpsInterfaceClass)) {
			log
				.error("The class specified \'"
					+ httpsInterfaceClassName
					+ "\' does not implements the Interface HTTPSPluginInterface. Unable to load the https plugin");
			return;
		}
		Constructor c = null;
		try {
			c = httpsInterfaceClass.getConstructor(null);
		} catch (SecurityException e) {
			log.error("Unable to load a no argument constructor for class "
				+ httpsInterfaceClassName + " SecurityException : " + e.getMessage());
			return;
		} catch (NoSuchMethodException e) {
			log.error("Unable to load a no argument constructor for class "
				+ httpsInterfaceClassName + " NoSuchMethodException : "
				+ e.getMessage());
			return;
		}
		Class[] parameters = c.getParameterTypes();
		if (parameters != null && parameters.length > 0) {
			log.error("Unable to construct an instance of class " + parameters.length
				+ " It does not provides a no argument constructor");
			return;
		}
		HTTPSPluginInterface httpsInterface = null;
		try {
			httpsInterface = (HTTPSPluginInterface) c.newInstance();
		} catch (IllegalArgumentException e) {
			log.error("Unable to isntantiate class " + httpsInterfaceClassName
				+ " IllegalArgumentException : " + e.getMessage());
			return;
		} catch (InstantiationException e) {
			log.error("Unable to isntantiate class " + httpsInterfaceClassName
				+ " InstantiationException : " + e.getMessage());
			return;
		} catch (IllegalAccessException e) {
			log.error("Unable to isntantiate class " + httpsInterfaceClassName
				+ " IllegalAccessException : " + e.getMessage());
			return;
		} catch (InvocationTargetException e) {
			log.error("Unable to isntantiate class " + httpsInterfaceClassName
				+ " InvocationTargetException : " + e.getMessage());
			return;
		}
		log.info("Initializing AclManager");
		HTTPPluginManager.init(httpsInterface);
		log.info("ACL manager initialization completed");
	}
}
