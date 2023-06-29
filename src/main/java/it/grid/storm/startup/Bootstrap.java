/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.startup;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import it.grid.storm.authz.AuthzDirector;
import it.grid.storm.authz.DirectorException;
import it.grid.storm.info.SpaceInfoManager;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		SpaceInfoManager.getInstance().initializeUsedSpace();
	}
}
