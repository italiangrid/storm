/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.space;

import java.io.Serializable;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfTSpaceToken;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * This class is part of the StoRM project. Copyright: Copyright (c) 2008
 * Company: INFN-CNAF and ICTP/EGRID project
 * 
 * This class represents the SpaceReservationData associated with the SRM
 * request, that is it contains info about: UserID, spaceType, SizeDesired,
 * SizeGuaranteed,ecc. Number of files progressing, Number of files finished,
 * and whether the request is currently suspended.
 * 
 * @author lucamag
 * @date May 29, 2008
 * 
 */

public class IdentityGetSpaceMetaDataInputData extends
	AnonymousGetSpaceMetaDataInputData implements Serializable, IdentityInputData {

	/**
     * 
     */
	private static final long serialVersionUID = -7823169083758886055L;
	private final GridUserInterface auth;

	public IdentityGetSpaceMetaDataInputData(GridUserInterface auth,
		ArrayOfTSpaceToken tokenArray) throws IllegalArgumentException {

		super(tokenArray);
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
