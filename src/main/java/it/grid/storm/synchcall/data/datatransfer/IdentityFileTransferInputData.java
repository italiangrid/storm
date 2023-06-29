/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.synchcall.data.IdentityInputData;

/**
 * @author Michele Dibenedetto
 * 
 */
public class IdentityFileTransferInputData extends
	AnonymousFileTransferInputData implements IdentityInputData {

	protected final GridUserInterface user;

	/**
	 * @param user
	 * @param surl
	 * @param transferProtocols
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public IdentityFileTransferInputData(GridUserInterface user, TSURL surl,
		TURLPrefix transferProtocols) throws IllegalArgumentException,
		IllegalStateException {

		super(surl, transferProtocols);
		if (user == null) {
			throw new IllegalArgumentException(
				"Unable to create the object. Received nul parameters: user = " + user);
		}
		this.user = user;
	}

	@Override
	public GridUserInterface getUser() {

		return user;
	}

	@Override
	public String getPrincipal() {

		return user.getDn();
	}

}
