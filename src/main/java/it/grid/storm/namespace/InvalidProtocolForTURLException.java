/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import it.grid.storm.srm.types.InvalidTTURLAttributesException;

public class InvalidProtocolForTURLException extends
	InvalidTTURLAttributesException {

	private String protocolSchema;

	public InvalidProtocolForTURLException(String protocolSchema) {

		super();
		this.protocolSchema = protocolSchema;
	}

	public InvalidProtocolForTURLException(Throwable cause, String protocolSchema) {

		super(cause);
		this.protocolSchema = protocolSchema;
	}

	public String toString() {

		return ("Impossible to build TURL with the protocol schema '"
			+ protocolSchema + "'");
	}

}
