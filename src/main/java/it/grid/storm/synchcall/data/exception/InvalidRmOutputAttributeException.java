/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if SpaceResData is not well formed.
 * *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;

import java.util.Vector;

public class InvalidRmOutputAttributeException extends Exception {

	private boolean nullSurlStatus = true;

	public InvalidRmOutputAttributeException(ArrayOfTSURLReturnStatus surlStatus) {

		nullSurlStatus = (surlStatus == null);
	}

	public String toString() {

		return "nullSurlStatusArray = " + nullSurlStatus;
	}
}
