/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project.
 * 
 * This class represents the Mkdir Input Data associated with the SRM request,
 * that is it contains info about: ...,ecc.
 * 
 * Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * @author lucamag
 * @date May 27, 2008
 * 
 */

public class IdentityMkdirInputData extends AnonymousMkdirInputData implements
	IdentityInputData {

	private final GridUserInterface auth;

	public IdentityMkdirInputData(GridUserInterface auth, TSURL surl)
		throws IllegalArgumentException {

		super(surl);
		if (auth == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: auth=" + auth);
		}
		this.auth = auth;
	}

	@Override
	public GridUserInterface getUser() {

		return this.auth;
	}

	@Override
	public String getPrincipal() {

		return this.auth.getDn();
	}
}
