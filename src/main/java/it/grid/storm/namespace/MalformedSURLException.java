/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents an Exception throws if TDirOptionData is not well
 * formed. *
 * 
 * @author Magnoni Luca
 * @author Cnaf - INFN Bologna
 * @date
 * @version 1.0
 */

package it.grid.storm.namespace;

import it.grid.storm.srm.types.*;

public class MalformedSURLException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TSURL surl = null;

	public MalformedSURLException(TSURL surl, String message) {

		super(message);
		this.surl = surl;
	}

	public String toString() {

		return String.format("MalformedSURLException for SURL='%s': %s", this.surl,
			this.getMessage());
	}

}
