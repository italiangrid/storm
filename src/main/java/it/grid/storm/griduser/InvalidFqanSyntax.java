/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

/**
 * Thrown when a invalid FQAN is detected by the
 * {@link it.grid.storm.griduser.VomsGridUser#VomsGridUser(String,String[])}
 * constructor. Holds and returns the offending FQAN string.
 */
public class InvalidFqanSyntax extends GridUserException {

	/** The FQAN string that does not match the FQAN regexp */
	protected final String _offendingFqan;

	/**
	 * Constructor, with the offending FQAN and a separate exception message.
	 */
	public InvalidFqanSyntax(String offendingFqan, String message) {

		super(message);

		assert (null == offendingFqan) : "Null string passed to InvalidFqanSyntax constructor";

		_offendingFqan = offendingFqan;
	}

	/**
	 * Constructor, specifying the offending FQAN only. A standard message is
	 * constructed.
	 */
	public InvalidFqanSyntax(String offendingFqan) {

		// damn Java syntax, we cannot check offendingFqan before this...
		super("Invalid FQAN: " + offendingFqan);

		_offendingFqan = offendingFqan;
	}

	public String getOffendingFqan() {

		return _offendingFqan;
	}
}
