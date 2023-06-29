/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityManageFileTransferFilesInputData extends
	AnonymousManageFileTransferFilesInputData implements IdentityInputData {

	private final GridUserInterface auth;

	public IdentityManageFileTransferFilesInputData(GridUserInterface auth,
		ArrayOfSURLs arrayOfSURLs) throws IllegalArgumentException {

		super(arrayOfSURLs);
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
