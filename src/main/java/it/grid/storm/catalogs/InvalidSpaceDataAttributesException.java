/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSpaceToken;

/**
 * This class represents an Exception throws if SpaceResData is not well formed.
 * *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

public class InvalidSpaceDataAttributesException extends Exception {

	private static final long serialVersionUID = -5317879266114702669L;

	private boolean nullAuth = true;
	private boolean nullToken = true;

	public InvalidSpaceDataAttributesException(GridUserInterface guser) {

		nullAuth = (guser == null);
	}

	public InvalidSpaceDataAttributesException(TSpaceToken token) {

		nullToken = (token == null);
	}

	public String toString() {

		return "null-Auth=" + nullAuth + "nullToken=" + nullToken;
	}

}
