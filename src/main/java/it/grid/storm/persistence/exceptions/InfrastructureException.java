/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.exceptions;

/**
 * This exception is used to mark (fatal) failures in infrastructure and system
 * code.
 * 
 * @author Christian Bauer <christian@hibernate.org>
 */
public class InfrastructureException extends RuntimeException {

	public InfrastructureException() {

	}

	public InfrastructureException(String message) {

		super(message);
	}

	public InfrastructureException(String message, Throwable cause) {

		super(message, cause);
	}

	public InfrastructureException(Throwable cause) {

		super(cause);
	}
}
