/* 
 * InvalidFqanSyntax
 *
 * Copyright (c) 2005, Riccardo Murri <riccardo.murri@ictp.it>.
 * 
 * You may copy, distribute and modify this file under the terms of
 * the INFN GRID licence.
 *
 * $Id: InvalidFqanSyntax.java,v 1.1 2005/12/02 12:40:46 rmurri Exp $
 *
 */
package it.grid.storm.griduser;

/**
 * Thrown when a invalid FQAN is detected by the {@link it.grid.storm.griduser.VomsGridUser#VomsGridUser(String,String[])} constructor.
 * Holds and returns the offending FQAN string.
 */
public class InvalidFqanSyntax 
	extends GridUserException {
	
	/** The FQAN string that does not match the FQAN regexp */
	protected final String _offendingFqan;

	/** 
	 * Constructor, with the offending FQAN and a separate exception
	 * message.
	 */
	public InvalidFqanSyntax (String offendingFqan, String message) {
		super(message);
		
		assert (null == offendingFqan) 
			: "Null string passed to InvalidFqanSyntax constructor";

		_offendingFqan = offendingFqan;
	}

	/** 
	 * Constructor, specifying the offending FQAN only.
	 * A standard message is constructed.
	 */
	public InvalidFqanSyntax (String offendingFqan) {
		// damn Java syntax, we cannot check offendingFqan before this...
		super("Invalid FQAN: " + offendingFqan);
		
		_offendingFqan = offendingFqan;
	}

	public String getOffendingFqan() {
		return _offendingFqan;
	}
}
