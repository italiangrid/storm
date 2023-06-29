/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.srm.types.TRequestToken;

/**
 * Class that represents an Exception thrown by the ReservedSpaceCatalog when it
 * finds no data for the specified request.
 * 
 * @author: EGRID ICTP
 * @version: 1.0
 * @date: June 2005
 */
public class NoDataFoundException extends Exception {

	private static final long serialVersionUID = -718255813130266566L;

	private TRequestToken requestToken;

	/**
	 * Constructor tha trequires the attributes that caused the exception to be
	 * thrown.
	 */
	public NoDataFoundException(TRequestToken requestToken) {

		this.requestToken = requestToken;
	}

	public String toString() {

		return "NoDataFoundException: requestToken=" + requestToken;
	}

}
