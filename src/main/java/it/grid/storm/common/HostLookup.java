/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostLookup {

	private static final Logger log = LoggerFactory.getLogger(HostLookup.class);

	public HostLookup() {

	}

	public String lookup(String hostname) throws UnknownHostException {

		InetAddress ia = InetAddress.getByName(hostname);
		log.debug("Lookup for hostname: {} resulted in {}",
		  hostname,
		  ia.getHostAddress());
		return ia.getHostAddress();
	}

}
