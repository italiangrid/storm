/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

/**
 * Root class for errors arising with the GridUser instanciation.
 */
public class GridUserException extends Exception {

	public GridUserException() {

		super();
	}

	public GridUserException(String message) {

		super(message);
	}

	public GridUserException(String message, Throwable cause) {

		super(message, cause);
	}

	public GridUserException(Throwable cause) {

		super(cause);
	}

}
