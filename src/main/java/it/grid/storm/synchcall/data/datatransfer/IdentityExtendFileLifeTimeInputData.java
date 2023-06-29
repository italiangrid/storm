/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * 
 * This class is part of the StoRM project. Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class represents the ExtendFileLifeTime Input Data.
 * 
 * Authors:
 * 
 * @author = lucamag luca.magnoniATcnaf.infn.it
 * @author Alberto Forti
 * 
 * @date = Oct 10, 2008
 * 
 */
public class IdentityExtendFileLifeTimeInputData extends
	AnonymousExtendFileLifeTimeInputData implements IdentityInputData {

	private final GridUserInterface auth;

	public IdentityExtendFileLifeTimeInputData(GridUserInterface auth,
		TRequestToken reqToken, ArrayOfSURLs surlArray,
		TLifeTimeInSeconds newFileLifetime, TLifeTimeInSeconds newPinLifetime)
		throws IllegalArgumentException {

		super(reqToken, surlArray, newFileLifetime, newPinLifetime);
		if (auth == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: auth=" + auth);
		}
		this.auth = auth;
	}

	@Override
	public GridUserInterface getUser() {

		return auth;
	}

	@Override
	public String getPrincipal() {

		return this.auth.getDn();
	}
}
