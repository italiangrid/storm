/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

/**
 * Class that represents an Exception thrown when making an SFN from a String
 * representation.
 * 
 * @author EGRID - ICTP Trieste
 * @version 1.0
 * @date September, 2006
 */
public class ParsingSFNAttributesException extends
	InvalidSFNAttributesException {

	private String explanation = "";
	private String sfn = "";

	/**
	 * Constructor that requires the String that caused the exception to be
	 * thrown, and an explanation String that describes the problem encountered.
	 */
	public ParsingSFNAttributesException(String sfn, String explanation) {

		if ((sfn != null) && (explanation != null)) {
			this.sfn = sfn;
			this.explanation = explanation;
		}
	}

	public String toString() {

		return sfn + " is malformed: " + explanation;
	}
}
