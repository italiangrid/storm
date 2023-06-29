/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project This class represents the Mv Input
 * Data associated with the SRM request, that is it contains info about:
 * ...,ecc.
 * 
 * @author lucamag
 * @date May 28, 2008
 */
public class IdentityMvInputData extends AnonymousMvInputData implements
	IdentityInputData {

	private final GridUserInterface auth;

	public IdentityMvInputData(GridUserInterface auth, TSURL fromSURL,
		TSURL toSURL) throws IllegalArgumentException {

		super(fromSURL, toSURL);
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
