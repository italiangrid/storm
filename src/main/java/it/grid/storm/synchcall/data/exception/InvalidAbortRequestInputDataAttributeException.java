/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if Abort Request input data is not
 * well formed. *
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.exception;

import it.grid.storm.srm.types.TRequestToken;

public class InvalidAbortRequestInputDataAttributeException extends Exception {

	private boolean nullTokenInfo = true;

	public InvalidAbortRequestInputDataAttributeException(TRequestToken token) {

		nullTokenInfo = (token == null);
	}

	public String toString() {

		return "nullTokenInfo = " + nullTokenInfo;
	}
}
