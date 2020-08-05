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

package it.grid.storm.authz.sa.conf;

import it.grid.storm.authz.SpaceAuthzInterface;
import it.grid.storm.authz.sa.AuthzDBInterface;
import it.grid.storm.authz.sa.AuthzDBReaderInterface;

import java.util.Observable;
import java.util.Observer;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAuthzDBParser implements Observer {

	private FileAuthzDBReader fileReader;
	private final PropertiesConfiguration authzFileDBProperties;
	private final AuthzDBInterface authzFileDB;
	private SpaceAuthzInterface spaceAuthz = null;

	private static final Logger log = LoggerFactory.getLogger(FileAuthzDBParser.class);

	public FileAuthzDBParser(SpaceAuthzInterface spaceAuthz,
		AuthzDBReaderInterface reader, boolean verboseLogging) {

		this.spaceAuthz = spaceAuthz;

		// //////TEMPORARY FIX
		// ////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
		authzFileDBProperties = null;
		// authzFileDBProperties = reader.getAuthzDB();

		if (reader instanceof FileAuthzDBReader) {
			fileReader = (FileAuthzDBReader) reader;
			// ////// TEMPORARY FIX
			// ////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
			// fileReader.setObserver(this);
		} else {
			log.error("FileAuthzDBParser initialized with a invalid Reader.");
		}
		authzFileDB = parseAuthzDBFile(authzFileDBProperties);
		spaceAuthz.setAuthzDB(authzFileDB);
	}

	/**
	 * parseAuthzDBFile
	 * 
	 * @param authzFileDBProperties
	 *          PropertiesConfiguration
	 * @return AuthzDBInterface
	 */
	private AuthzDBInterface parseAuthzDBFile(
		PropertiesConfiguration authzFileDBProperties) {

		log.debug("PARSING AUTHZ DB FILE");
		return null;
	}

	public AuthzDBInterface getAuthzDB() {

		return this.authzFileDB;
	}

	public void update(Observable observed, Object arg) {

		log.debug("{} Refreshing Namespace Memory Cache .. ", arg);
		// //////TEMPORARY FIX
		// ////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
		// FileAuthzDBReader reader = (FileAuthzDBReader) observed;

		/**
		 * @todo: Refreshing della copia Cache di AuthzDB memorizzata in SpaceAuthz
		 */
		// //////TEMPORARY FIX
		// ////// THIS METHOD HAS BEEN COMMENTED TO MAKE EVERYTHING COMPILE
		// reader.setNotifyManaged();
		log.debug(" ... Cache Refreshing ended");
	}

}
