/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.command.datatransfer;

/**
 * @author Michele Dibenedetto
 * 
 */
public class CommandException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = -2644088500951303729L;

	public CommandException() {

	}

	public CommandException(String message) {

		super(message);
	}

	public CommandException(Throwable cause) {

		super(cause);
	}

	public CommandException(String message, Throwable cause) {

		super(message, cause);
	}

}
