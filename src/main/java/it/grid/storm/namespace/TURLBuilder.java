/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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