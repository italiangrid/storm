/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for SpaceToken
 * is invoked with a null String.
 * 
 * @author Ezio Corso
 * @author EGRID - ICTP Trieste
 * @date March 23rd, 2005
 * @version 1.0
 */
public class InvalidTSpaceTokenAttributesException extends Exception {

	public InvalidTSpaceTokenAttributesException() {

	}

	public String toString() {

		return "Invalid TSpaceToken Attribute: null String!";
	}
}
