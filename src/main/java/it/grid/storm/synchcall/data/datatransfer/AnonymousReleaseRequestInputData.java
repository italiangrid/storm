/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousReleaseRequestInputData extends AbstractInputData
	implements ManageFileTransferRequestInputData {

	private final TRequestToken requestToken;

	public AnonymousReleaseRequestInputData(TRequestToken requestToken) {

		if (requestToken == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: requestToken="
					+ requestToken);
		}
		this.requestToken = requestToken;
	}

	@Override
	public TRequestToken getRequestToken() {

		return requestToken;
	}

}
