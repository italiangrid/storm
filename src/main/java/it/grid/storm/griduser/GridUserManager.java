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

/*
 * You may copy, distribute and modify this file under the terms of the INFN
 * GRID licence. For a copy of the licence please visit
 * 
 * http://www.cnaf.infn.it/license.html
 * 
 * Original design made by Riccardo Zappi <riccardo.zappi@cnaf.infn.it.it>, 2007
 * 
 * $Id: GridUserManager.java 3604 2007-05-22 11:16:27Z rzappi $
 */

package it.grid.storm.griduser;

import it.grid.storm.config.Configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridUserManager {

	static final Logger log = LoggerFactory.getLogger(GridUserManager.class);
	static Configuration config = Configuration.getInstance();
	static GridUserFactory userFactory = null;

	static {
		log.debug("Initializing Grid User Director...");
		userFactory = initializeFactory();
	}

	private GridUserManager() {

		super();
	}

	private static GridUserFactory initializeFactory() {

		return GridUserFactory.getInstance();
	}

	public static String getMapperClassName() {

		return config.getGridUserMapperClassname();
	}

	public static GridUserInterface makeVOMSGridUser(String dn, String proxy,
		FQAN[] fqans) throws IllegalArgumentException {

		if (proxy == null || dn == null || fqans == null || fqans.length == 0) {
			throw new IllegalArgumentException(
				"Unable to make VomsGridUser. Inavlid arguments: dn=\'" + dn
					+ "\' fqans=\'" + fqans + "\' proxy=\'" + proxy + "\'");
		}
		GridUserInterface gridUser = null;
		try {
			gridUser = userFactory.createGridUser(dn, fqans, proxy);
		} catch (IllegalArgumentException e) {
		  log.error(e.getMessage(), e);
		}
		return gridUser;

	}

	public static GridUserInterface makeVOMSGridUser(String dn, String vo)
		throws IllegalArgumentException {

		if (vo == null || dn == null) {
			throw new IllegalArgumentException(
				"Unable to make VomsGridUser. Inavlid arguments: dn=\'" + dn
					+ "\' vo=\'" + vo + "\'");
		}
		GridUserInterface gridUser = null;
		FQAN[] fqans = new FQAN[1];
		fqans[0] = FQAN.makeVoFQAN(vo);
		try {
			gridUser = userFactory.createGridUser(dn, fqans);
		} catch (IllegalArgumentException e) {
		  log.error(e.getMessage(), e);
		}
		return gridUser;
	}

	public static GridUserInterface makeVOMSGridUser(String dn,
		String[] fqansString) throws IllegalArgumentException {

		if (fqansString == null || fqansString.length == 0) {
			throw new IllegalArgumentException(
				"Unable to make VomsGridUser. Invalid fqansString argument: "
					+ fqansString);
		}
		FQAN[] fqans = new FQAN[fqansString.length];
		for (int i = 0; i < fqansString.length; i++) {
			fqans[i] = new FQAN(fqansString[i]);
		}
		return userFactory.createGridUser(dn, fqans);
	}

	public static GridUserInterface makeVOMSGridUser(String dn, FQAN[] fqans)
		throws IllegalArgumentException {

		if (fqans == null || fqans.length == 0) {
			throw new IllegalArgumentException(
				"Unable to make VomsGridUser. Invalid fqans argument: " + fqans);
		}
		return userFactory.createGridUser(dn, fqans);
	}

	public static GridUserInterface makeGridUser(String dn) {

		return userFactory.createGridUser(dn);
	}

	public static GridUserInterface makeGridUser(String dn, String proxy) {

		return userFactory.createGridUser(dn, proxy);
	}

	public static GridUserInterface makeSAGridUser() {
		GridUserInterface result = null;
		String dn = "/DC=it/DC=infngrid/OU=Services/CN=storm";
		result = userFactory.createGridUser(dn);
		return result;
	}

	public static GridUserInterface decode(Map<String, Object> inputParam) {

		return userFactory.decode(inputParam);
	}
}