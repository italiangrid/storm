/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.checksum;

public class ChecksumRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -6992922355763921291L;

	public ChecksumRuntimeException() {
	}

	public ChecksumRuntimeException(String message) {

		super(message);
	}

	public ChecksumRuntimeException(Throwable cause) {

		super(cause);
	}

	public ChecksumRuntimeException(String message, Throwable cause) {

		super(message, cause);
	}

}
