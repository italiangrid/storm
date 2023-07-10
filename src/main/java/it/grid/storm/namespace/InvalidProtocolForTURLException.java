/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

import it.grid.storm.srm.types.InvalidTTURLAttributesException;

public class InvalidProtocolForTURLException extends
	InvalidTTURLAttributesException {

	private static final long serialVersionUID = 1L;
  private String protocolSchema;

	public InvalidProtocolForTURLException(String protocolSchema) {

		super("Invalid protocol schema");
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
