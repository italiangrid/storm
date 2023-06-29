/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs;

import it.grid.storm.common.types.TURLPrefix;
import it.grid.storm.griduser.GridUserInterface;
import it.grid.storm.srm.types.TDirOption;
import it.grid.storm.srm.types.TLifeTimeInSeconds;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSURL;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;
import it.grid.storm.synchcall.data.IdentityInputData;

public class IdentityPtGData extends AnonymousPtGData implements
	IdentityInputData {

	private final GridUserInterface auth;

	/**
	 * @param requestToken
	 * @param fromSURL
	 * @param lifeTime
	 * @param dirOption
	 * @param desiredProtocols
	 * @param fileSize
	 * @param status
	 * @param transferURL
	 * @throws InvalidPtGDataAttributesException
	 */
	public IdentityPtGData(GridUserInterface auth, TSURL SURL,
		TLifeTimeInSeconds lifeTime, TDirOption dirOption,
		TURLPrefix desiredProtocols, TSizeInBytes fileSize, TReturnStatus status,
		TTURL transferURL) throws InvalidPtGDataAttributesException,
		InvalidFileTransferDataAttributesException,
		InvalidSurlRequestDataAttributesException, IllegalArgumentException {

		super(SURL, lifeTime, dirOption, desiredProtocols, fileSize, status,
			transferURL);
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
