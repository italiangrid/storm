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
			log.debug("turlString used to build the TURL : {}", turlString);
			turl = TTURL.makeFromString(turlString);
		} catch (InvalidTTURLAttributesException ex) {
			log.error("Error while constructing TURL with Authority '{}': {}",
			  authority, ex.getMessage(), ex);
		}
		return turl;
	}

	public static TTURL buildFileTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForFile = Configuration.getInstance()
			.getExtraSlashesForFileTURL();
		return buildTURL(Protocol.FILE, authority, extraSlashesForFile, physicalFN);
	}

	public static TTURL buildGsiftpTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForGSIFTP = Configuration.getInstance()
			.getExtraSlashesForGsiFTPTURL();
		return buildTURL(Protocol.GSIFTP, authority, extraSlashesForGSIFTP,
			physicalFN);
	}

	public static TTURL buildRFIOTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForRFIO = Configuration.getInstance()
			.getExtraSlashesForRFIOTURL();
		return buildTURL(Protocol.RFIO, authority, extraSlashesForRFIO, physicalFN);
	}

	public static TTURL buildROOTTURL(Authority authority, PFN physicalFN) {

		String extraSlashesForROOT = Configuration.getInstance()
			.getExtraSlashesForROOTTURL();
		return buildTURL(Protocol.ROOT, authority, extraSlashesForROOT, physicalFN);
	}
	
	public static TTURL buildXROOTTURL(Authority authority, PFN physicalFN) {

		return buildROOTTURL(authority, physicalFN);
	}
}