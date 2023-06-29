/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

/**
 * Signal that something went wrong during the LCMAPS call.
 */
public class CannotMapUserException extends GridUserException {

	public CannotMapUserException() {

		super();
	}

	public CannotMapUserException(String message) {

		super(message);
	}

	public CannotMapUserException(String message, Throwable cause) {

		super(message, cause);
	}

	public CannotMapUserException(Throwable cause) {

		super(cause);
	}

}
