/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * This class represents an Exception throw nwhen attempting to create a
 * PathName with a null or empty String, or with a String that does not begin a
 * /.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 25th, 2005
 * @version 1.0
 */
public class InvalidPFNAttributeException extends Exception {

	private boolean nullName; // boolean true if the supplied String is null
	private boolean emptyName; // boolean true if the supplied String is empty
	private boolean wrong = false; // boolean true if the supplied String does not
																	// begin with a /

	/**
	 * Constructor requiring the String that caused the exception to be thrown.
	 */
	public InvalidPFNAttributeException(String name) {

		this.nullName = (name == null);
		this.emptyName = (name.equals(""));
		if (!nullName && !emptyName)
			this.wrong = (name.charAt(0) != '/');
	}

	public String toString() {

		return "Attempt to create PFN with invalid attributes: nullName="
			+ nullName + "; emptyName=" + emptyName + "; not-beginning-with-/="
			+ wrong;
	}

}
