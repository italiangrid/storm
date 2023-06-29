/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the general Abort Input Data associated with the SRM
 * request Abort
 * 
 * @author Magnoni Luca
 * @author CNAF -INFN Bologna
 * @date Dec 2006
 * @version 1.0
 */

package it.grid.storm.synchcall.data.datatransfer;

import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.synchcall.data.AbstractInputData;

public abstract class AnonymousAbortGeneralInputData extends AbstractInputData
	implements AbortInputData {

	private final AbortType type;

	private final TRequestToken reqToken;

	protected AnonymousAbortGeneralInputData(TRequestToken reqToken,
		AbortType type) throws IllegalArgumentException {

		if (reqToken == null || type == null) {
			throw new IllegalArgumentException(
				"Unable to build the object. null arguments: reqToken=" + reqToken
					+ " type=" + type);
		}
		this.reqToken = reqToken;
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.grid.storm.synchcall.data.datatransfer.AbortInputData#getRequestToken()
	 */
	@Override
	public TRequestToken getRequestToken() {

		return reqToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.datatransfer.AbortInputData#getType()
	 */
	@Override
	public AbortType getType() {

		return type;
	}

}
