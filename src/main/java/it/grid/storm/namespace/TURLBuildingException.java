/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace;

public class TURLBuildingException extends Exception {

  private static final long serialVersionUID = 1L;

  public TURLBuildingException() {
	}

	public TURLBuildingException(String message) {
		super(message);
	}

	public TURLBuildingException(Throwable cause) {
		super(cause);
	}

	public TURLBuildingException(String message, Throwable cause) {
		super(message, cause);
	}

}
