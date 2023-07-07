/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.namespace.model.Protocol;

/**
 * Package private auxiliary class used to convert between the DB raw data
 * representation and StoRM s Object model list of transfer protocols.
 * 
 */

class TransferProtocolListConverter {

	/**
	 * Method that returns a List of upper-case Strings used in the DB to represent
	 * the given TURLPrefix. An empty List is returned in case the conversion does
	 * not succeed, a null TURLPrefix is supplied, or its size is 0.
	 */
	public static List<String> toDB(TURLPrefix turlPrefix) {

		List<String> result = new ArrayList<String>();
		Protocol protocol;
		for (Iterator<Protocol> it = turlPrefix.getDesiredProtocols().iterator(); it
			.hasNext();) {
			protocol = it.next();
			result.add(protocol.getSchema());
		}
		return result;
	}

	/**
	 * Method that returns a TURLPrefix of transfer protocol. If the translation
	 * cannot take place, a TURLPrefix of size 0 is returned. Likewise if a null
	 * List is supplied.
	 */
	public static TURLPrefix toSTORM(List<String> listOfProtocol) {

		TURLPrefix turlPrefix = new TURLPrefix();
		for (Iterator<String> i = listOfProtocol.iterator(); i.hasNext();) {
		  turlPrefix.addProtocol(Protocol.valueOf(i.next()));
		}
		return turlPrefix;
	}
}
