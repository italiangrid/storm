/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.TSpaceToken;
import it.grid.storm.griduser.GridUserInterface;

/**
 * This class represents an Exception throws if SpaceResData is not well formed.
 * *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidReleaseSpaceAttributesException extends Exception {

	private boolean nullAuth = true;
	private boolean nullToken = true;

	public InvalidReleaseSpaceAttributesException(GridUserInterface guser,
		TSpaceToken token) {

		nullAuth = (guser == null);
		nullToken = (token == null);
	}

	public String toString() {

		return "null-Auth=" + nullAuth + " and null-token=" + nullToken;
	}
}
