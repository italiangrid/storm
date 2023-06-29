/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.rest.metadata.service;

public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}

}
