/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.discovery;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project. This class represents the Ping Input
 * Data
 * 
 * Copyright: Copyright (c) 2008 Company: INFN-CNAF and ICTP/EGRID project
 * 
 * 
 * @author lucamag
 * @author Alberto Forti
 * @date May 28, 2008
 * 
 */

public class IdentityPingInputData extends AnonymousPingInputData implements
	IdentityInputData {

	private final GridUserInterface requestor;

	public IdentityPingInputData(GridUserInterface gridUser,
		String authorizationID) throws IllegalArgumentException {

		super(authorizationID);
		if (gridUser == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: gridUser=" + gridUser);
		}
		this.requestor = gridUser;
	}

	/**
	 * Get the Requestor
	 * 
	 * @return GridUserInterface
	 */
	public GridUserInterface getRequestor() {

		return this.requestor;
	}

	@Override
	public String getPrincipal() {

		return this.requestor.getDn();
	}

	@Override
	public GridUserInterface getUser() {

		return requestor;
	}
}
