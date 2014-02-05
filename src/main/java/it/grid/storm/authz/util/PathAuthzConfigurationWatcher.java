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
package it.grid.storm.authz.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ritz
 */
public class PathAuthzConfigurationWatcher extends ConfigurationWatcher {

	private static final Logger log = LoggerFactory.getLogger(PathAuthzConfigurationWatcher.class);

	/**
	 * @param file
	 */
	public PathAuthzConfigurationWatcher(File file) {

		super(file);
		log.debug("Watcher manages the configuration file: {}", file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.authz.util.ConfigurationWatcher#onChange()
	 */
	@Override
	protected void onChange() {

		log.info("Path Authorization DB is changed! Going to reload it");
		// Force the reload of the configuration file

	}

}
