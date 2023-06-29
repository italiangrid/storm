/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.griduser;

/**
 * Thrown when a invalid subject DN is detected by the
 * {@link it.grid.storm.griduser.VomsGridUser#VomsGridUser(String,String[])}
 * constructor. Holds and returns the offending subject DN string.
 */
public class InvalidSubjectDnSyntax extends GridUserException {

	/** The FQAN string that does not match the FQAN regexp */
	protected final String _offendingSubjectDn;

	/**
	 * Constructor, with the offending FQAN and a separate exception message.
	 */
	public InvalidSubjectDnSyntax(String offendingSubjectDn, String message) {

		super(message);

		assert (null == offendingSubjectDn) : "Null string passed to InvalidSubjectDnSyntax constructor";

		_offendingSubjectDn = offendingSubjectDn;
	}

	/**
	 * Constructor, specifying the offending FQAN only. A standard message is
	 * constructed.
	 */
	public InvalidSubjectDnSyntax(String offendingSubjectDn) {

		// damn Java syntax, we cannot check offendingSubjectDn before this...
		super("Invalid FQAN: " + offendingSubjectDn);

		_offendingSubjectDn = offendingSubjectDn;
	}

	public String getOffendingSubjectDn() {

		return _offendingSubjectDn;
	}
}
