/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.startup;

public class BootstrapException extends Exception {

	private static final long serialVersionUID = -3495820491163614689L;

	public BootstrapException() {

	}

	public BootstrapException(String message) {

		super(message);
	}

	public BootstrapException(Throwable cause) {

		super(cause);
	}

	public BootstrapException(String message, Throwable cause) {

		super(message, cause);
	}

}
