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

import it.grid.storm.common.types.PFN;
import it.grid.storm.config.Configuration;
import it.grid.storm.filesystem.LocalFile;
import it.grid.storm.https.HTTPPluginManager;
import it.grid.storm.https.HTTPSPluginException;
import it.grid.storm.namespace.model.Authority;
import it.grid.storm.namespace.model.Protocol;
import it.grid.storm.srm.types.InvalidTTURLAttributesException;
import it.grid.storm.srm.types.TTURL;

import org.slf4j.Logger;

public class TURLBuilder {

	private static Logger log = NamespaceDirector.getLogger();

	public TURLBuilder() {

		super();
	}

	private static TTURL buildTURL(Protocol protocol, Authority authority,
		String extraSlashes, PFN physicalFN) {

		TTURL turl = null;
		String turlString = null;
		try {
			turlString = protocol.getProtocolPrefix() + authority.toString()
				+ extraSlashes + physicalFN.getValue();
			log.debug("turlString used to build the TURL : " + turlString);
			turl = TTURL.makeFromString(turlString);
		} catch (InvalidTTURLAttributesException ex) {
			log.error("Error while constructing TURL with Authority :'" + authority
				+ "'; EXCEP: " + ex);
		}
		return turl;
	}

	/**
	 * buildFileTURL
	 * 
	 * @return TTURL
	 */
	public static TTURL buildFileTURL(Authority authority, PFN physicalFN) {

		// Authority for Protocol File is empty
		String extraSlashesForFile = Configuration.getInstance()
			.getExtraSlashesForFileTURL();
		return buildTURL(Protocol.FILE, authority, extraSlashesForFile, physicalFN);
	}

	/**
	 * buildGsiftpTURL
	 * 
	 * @return TTURL
	 */
	public static TTURL buildGsiftpTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForGSIFTP = Configuration.getInstance()
			.getExtraSlashesForGsiFTPTURL();
		return buildTURL(Protocol.GSIFTP, authority, extraSlashesForGSIFTP,
			physicalFN);
	}

	/**
	 * buildRFIOTURL
	 * 
	 * @return TTURL
	 */
	public static TTURL buildRFIOTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForRFIO = Configuration.getInstance()
			.getExtraSlashesForRFIOTURL();
		return buildTURL(Protocol.RFIO, authority, extraSlashesForRFIO, physicalFN);
	}

	/**
	 * buildROOTTURL
	 * 
	 * @return TTURL
	 */
	public static TTURL buildROOTTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForROOT = Configuration.getInstance()
			.getExtraSlashesForROOTTURL();
		return buildTURL(Protocol.ROOT, authority, extraSlashesForROOT, physicalFN);
	}

	/**
	 * buildHHTPTURL
	 * 
	 * @param authority
	 * @param physicalFN
	 * @return
	 * @throws Exception
	 * @throws IllegalStateException
	 */
	// TODO HTTPS TURL
	public static TTURL buildHTTPTURL(Authority authority, LocalFile localFile)
		throws HTTPSPluginException {

		// ????? NEEDED ??? String extraSlashesFor?? =
		// Configuration.getInstance().getExtraSlashesFor??PTURL();
		// return buildTURL(Protocol.HTTP, authority, extraSlashesForGSIFTP,
		// physicalFN) ;

		String serviceRelativePath = HTTPPluginManager
			.getHTTPSPluginInstance()
			.mapLocalPath(authority.getServiceHostname(), localFile.getAbsolutePath());// HTTPSPluginInterface.MapLocalPath(localFile.getAbsolutePath());
		return buildTURL(Protocol.HTTP, authority, "", serviceRelativePath);
	}

	/**
	 * buildHHTPTURL
	 * 
	 * @param authority
	 * @param physicalFN
	 * @return
	 * @throws Exception
	 * @throws IllegalStateException
	 */
	// TODO HTTPS TURL
	public static TTURL buildHTTPSTURL(Authority authority, LocalFile localFile)
		throws HTTPSPluginException {

		// ????? NEEDED ??? String extraSlashesFor?? =
		// Configuration.getInstance().getExtraSlashesFor??PTURL();
		// return buildTURL(Protocol.HTTP, authority, extraSlashesForGSIFTP,
		// physicalFN) ;

		// String servicePath =
		// HTTPPluginManager.getHTTPSPluginInstance().getServicePath(); //
		// HTTPSPluginInterface.getServicePath();
		String serviceRelativePath = HTTPPluginManager
			.getHTTPSPluginInstance()
			.mapLocalPath(authority.getServiceHostname(), localFile.getAbsolutePath());// HTTPSPluginInterface.MapLocalPath(localFile.getAbsolutePath());
		return buildTURL(Protocol.HTTPS, authority, "", serviceRelativePath);
	}

	private static TTURL buildTURL(Protocol protocol, Authority authority,
		String extraSlashes, String serviceRelativePath) {

		TTURL turl = null;
		String turlString = null;
		try {
			turlString = protocol.getProtocolPrefix() + authority.toString()
				+ extraSlashes + serviceRelativePath;
			log.debug("turlString used to build the TURL : " + turlString);
			turl = TTURL.makeFromString(turlString);
		} catch (InvalidTTURLAttributesException ex) {
			log.error("Error while constructing TURL with Authority :'" + authority
				+ "'; EXCEP: " + ex);
		}
		return turl;
	}

	// TODO MICHELE HTTPS here add the method that builds the HTTPS turl using the
	// installed connector

}
