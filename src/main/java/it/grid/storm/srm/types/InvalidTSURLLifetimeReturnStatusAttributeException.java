/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throwed if TSURLLifetimeReturnStatus is
 * not well formed. *
 * 
 * @author Alberto Forti
 * @author CNAF-INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import it.grid.storm.srm.types.TSURL;

public class InvalidTSURLLifetimeReturnStatusAttributeException extends
	Exception {

	private boolean nullSurl = true;

	public InvalidTSURLLifetimeReturnStatusAttributeException(TSURL surl) {

		nullSurl = (surl == null);
	}

	public String toString() {

		return "nullSurl = " + nullSurl;
	}
}
