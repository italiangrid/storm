/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data.directory;

import it.grid.storm.srm.types.ArrayOfSURLs;
import it.grid.storm.synchcall.data.AbstractInputData;

public class AnonymousRmInputData extends AbstractInputData implements
	RmInputData {

	private final ArrayOfSURLs surlArray;

	public AnonymousRmInputData(ArrayOfSURLs array)
		throws IllegalArgumentException {

		if (array == null) {
			throw new IllegalArgumentException(
				"Unable to create the object, invalid arguments: array=" + array);
		}
		surlArray = array;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.grid.storm.synchcall.data.directory.RmInputData#getSurlArray()
	 */
	@Override
	public ArrayOfSURLs getSurlArray() {

		return surlArray;
	}
}
