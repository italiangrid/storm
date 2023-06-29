/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.ea;

public class ExtendedAttributesException extends RuntimeException {

	private static final long serialVersionUID = 1484068785050730930L;

	public ExtendedAttributesException() {

	}

	public ExtendedAttributesException(String message) {

		super(message);
	}

	public ExtendedAttributesException(Throwable cause) {

		super(cause);
	}

	public ExtendedAttributesException(String message, Throwable cause) {

		super(message, cause);
	}

}
