/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if AbortFiles input data is not
 * well formed. *
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.ArrayOfTSURLReturnStatus;

public class InvalidAbortGeneralOutputDataAttributeException extends Exception {

	private boolean nullSurlStatus = true;

	public InvalidAbortGeneralOutputDataAttributeException(
		ArrayOfTSURLReturnStatus surlStatus) {

		nullSurlStatus = (surlStatus == null);
	}

	public String toString() {

		return "nullSurlStatusArray = " + nullSurlStatus;
	}

}
