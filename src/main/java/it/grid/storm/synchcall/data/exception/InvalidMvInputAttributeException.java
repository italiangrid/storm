/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if MvInputData is not well formed.
 * *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.TSURL;

public class InvalidMvInputAttributeException extends Exception {

	private boolean nullFromSurl = true;
	private boolean nullToSurl = true;

	public InvalidMvInputAttributeException(TSURL fromSURL, TSURL toSURL) {

		nullFromSurl = (fromSURL == null);
		nullToSurl = (toSURL == null);
	}

	public String toString() {

		return "nullFromSurl = " + nullFromSurl + " , nullToSURL = " + nullToSurl;
	}
}
