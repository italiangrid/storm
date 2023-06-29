/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.catalogs.timertasks;

public class TGarbageData {

	private final int nPurgedRequests;
	private final int nPurgedRecalls;

	public static final TGarbageData EMPTY = new TGarbageData(0, 0);

	public TGarbageData(int nPurgedRequests, int nPurgedRecalls) {
		this.nPurgedRequests = nPurgedRequests;
		this.nPurgedRecalls = nPurgedRecalls;
	}

	public int getTotalPurged() {

		return nPurgedRequests + nPurgedRecalls;
	}

	public int getTotalPurgedRequests() {

		return nPurgedRequests;
	}

	public int getTotalPurgedRecalls() {

		return nPurgedRecalls;
	}
}