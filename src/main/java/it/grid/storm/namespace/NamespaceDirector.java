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

package it.grid.storm.namespace;

import it.grid.storm.config.Configuration;
import it.grid.storm.namespace.config.NamespaceLoader;
import it.grid.storm.namespace.config.NamespaceParser;
import it.grid.storm.namespace.config.xml.XMLNamespaceLoader;
import it.grid.storm.namespace.config.xml.XMLNamespaceParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamespaceDirector {

	private static final Logger log = LoggerFactory
		.getLogger(NamespaceDirector.class);;
	private static NamespaceInterface namespaceIstance = null;

	private static NamespaceLoader loader;
	private static NamespaceParser parser;

	private static boolean initialized = false;

	private NamespaceDirector() {}

	public static void initializeDirector() {

		log.info("NAMESPACE : Initializing ...");
		Configuration config = Configuration.getInstance();

		log.info(" +++++++++++++++++++++++ ");
		log.info("    Production Mode      ");
		log.info(" +++++++++++++++++++++++ ");

		String configurationPATH = config.namespaceConfigPath();
		String namespaceConfigFileName = config.getNamespaceConfigFilename();
		int refreshInSeconds = config.getNamespaceConfigRefreshRateInSeconds();
		loader = new XMLNamespaceLoader(configurationPATH, namespaceConfigFileName, refreshInSeconds);

			// Check the validity of namespace.
			if (loader instanceof XMLNamespaceLoader) {
				XMLNamespaceLoader xmlLoader = (XMLNamespaceLoader) loader;
				if (!(xmlLoader.schemaValidity)) {
					// Error into the validity ckeck of namespace
					log.error("Namespace configuration is not conformant with namespae grammar.");
					log.error("Please validate namespace configuration file.");
					System.exit(0);
				}
			}

		log.debug("Namespace Configuration PATH : {}" , configurationPATH);
		log.debug("Namespace Configuration FILENAME : {}" , namespaceConfigFileName);
		log.debug("Namespace Configuration GLANCE RATE : {}" , refreshInSeconds);

		parser = new XMLNamespaceParser(loader);
		namespaceIstance = new Namespace(parser);

		log.debug("NAMESPACE INITIALIZATION : ... done!");
		initialized = true;

	}

	/**
	 * 
	 * @return Namespace
	 */
	public static NamespaceInterface getNamespace() {

		if (!(initialized)) {
			initializeDirector();
		}
		return namespaceIstance;
	}

	/**
	 * 
	 * @return Namespace
	 */
	public static NamespaceParser getNamespaceParser() {

		if (!(initialized)) {
			initializeDirector();
		}
		return parser;
	}

	/**
	 * 
	 * @return Namespace
	 */
	public static NamespaceLoader getNamespaceLoader() {

		if (!(initialized)) {
			initializeDirector();
		}
		return loader;
	}

	public static Logger getLogger() {

		return log;
	}

}
