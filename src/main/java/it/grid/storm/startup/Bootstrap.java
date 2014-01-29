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
import java.lang.reflect.InvocationTargetException;
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

			System.err.println(e + "\n" + e.getCause());
			
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
			log.error("Unable to initialize the AuthzDirector : " + e);
			throw new BootstrapException("Unable to initialize the AuthzDirector");
		}
	}

	public static void initializeUsedSpace() {

		int numberOfSpaceInitialized = SpaceInfoManager.getInstance()
			.initSpaceFromINIFile();
		
		log.info("Initialized '" + numberOfSpaceInitialized
			+ "' SA from 'used-space.ini'");
		SpaceInfoManager.getInstance().updateSpaceUsed();
		
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
