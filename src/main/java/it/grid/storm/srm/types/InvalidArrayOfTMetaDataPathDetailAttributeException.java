/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents an Exception thrown when the constructor for SpaceToken
 * is invoked with a null String.
 * 
 */

import it.grid.storm.srm.types.TMetaDataPathDetail;

public class InvalidArrayOfTMetaDataPathDetailAttributeException extends
	Exception {

	private boolean nullArray;

	public InvalidArrayOfTMetaDataPathDetailAttributeException(
		TMetaDataPathDetail[] metaDataArray) {

		nullArray = metaDataArray == null;
	}

	public String toString() {

		return "Invalid TMetaDataPathDetail[]: nullArray = " + nullArray;
	}
}
