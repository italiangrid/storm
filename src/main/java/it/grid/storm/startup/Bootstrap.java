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

import java.io.File;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * @author zappi
 */
public class Bootstrap {
	
	private static Logger log = LoggerFactory.getLogger(Bootstrap.class);


	public static void configureLogging(String loggingConfigFilePath) {
		
		log.info("Configuring logging from {}", loggingConfigFilePath);
		
		File f = new File(loggingConfigFilePath);
		
		if (!f.exists() || !f.canRead()) {
			
			String message = String.format("Error loading logging configuration: "
				+ "'%s' does not exist or is not readable.",loggingConfigFilePath);
			
			log.error(message);
			
			throw new RuntimeException(message);
		} 
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		JoranConfigurator configurator = new JoranConfigurator();

		configurator.setContext(lc);
		lc.reset();

		try {
			configurator.doConfigure(loggingConfigFilePath);

		} catch (JoranException e) {

			throw new RuntimeException(e);

		} finally {
		
			StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
		
		}
	}
	
	public static void initializePathAuthz(String pathAuthzDBFileName)
		throws BootstrapException {

		try {
			AuthzDirector.initializePathAuthz(pathAuthzDBFileName);
		} catch (DirectorException e) {

			log.error("Unable to initialize the AuthzDirector: {}", 
			  e.getMessage(), e);

			throw new BootstrapException("Unable to initialize the AuthzDirector",e);
		}
	}

	public static void initializeUsedSpace() {

		int numberOfSpaceInitialized = SpaceInfoManager.getInstance()
			.initSpaceFromINIFile();
		
		log.info("Initialized {} SA from used-space.ini", numberOfSpaceInitialized);
		SpaceInfoManager.getInstance().updateSpaceUsed();
		
		int numberOfBgDU = SpaceInfoManager.howManyBackgroundDU();
		log.info("Submitted {} background DU tasks.", numberOfBgDU);
	}

	/**
	 * @param httpsInterfaceClassName
	 * @param log
	 */
	public static void initializeAclManager(String httpsInterfaceClassName,
		Logger log) {

		log.debug("ACL manager. httpsInterfaceClassName: {}", 
		  httpsInterfaceClassName);

		Class httpsInterfaceClass = null;
		try {
			httpsInterfaceClass = Class.forName(httpsInterfaceClassName);
		} catch (ClassNotFoundException e) {
		  log.error("Unable to load https plugin class {}. Message: {}", 
		    httpsInterfaceClassName, e.getMessage(), e);
			return;
		}
		if (!HTTPSPluginInterface.class.isAssignableFrom(httpsInterfaceClass)) {
		  log.error("Unable to load the https plugin. "
		    + "The specified class '{}' does not implement "
		    + "the HTTPSPluginInterface.",
		    httpsInterfaceClassName);
			return;
		}

		Constructor c = null;

		try {
			c = httpsInterfaceClass.getConstructor(null);
		} catch (Throwable e) {
		  log.error("Error instantiating https plugin: {}", e.getMessage(),e);
			return;
		}

		Class[] parameters = c.getParameterTypes();

		if (parameters != null && parameters.length > 0) {
		  log.error("Invalid number of arguments for https plugin constructor.");
			return;
		}

		HTTPSPluginInterface httpsInterface = null;
		try {
			httpsInterface = (HTTPSPluginInterface) c.newInstance();
		} catch (Throwable e) {
		  log.error("Error instantiating HTTPS plugin {}: {}", 
		    httpsInterfaceClassName,
		    e.getMessage(), e);
			return;
		}

		log.info("Initializing ACL manager");
		HTTPPluginManager.init(httpsInterface);
		log.info("ACL manager initialization completed");
	}
}
