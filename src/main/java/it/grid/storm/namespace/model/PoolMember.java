/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public class PoolMember {

	private final int memberID;
	private final int memberWeight; // -1 means undefined
	private final TransportProtocol memberProtocol;

	public PoolMember(int memberID, TransportProtocol protocol) {

		this(memberID, protocol, -1);
	}

	public PoolMember(int memberID, TransportProtocol protocol, int weight) {

		this.memberID = memberID;
		this.memberProtocol = protocol;
		this.memberWeight = weight;
	}

	public int getMemberID() {

		return this.memberID;
	}

	public int getMemberWeight() {

		return this.memberWeight;
	}

	public TransportProtocol getMemberProtocol() {

		return this.memberProtocol;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(memberProtocol + " --> Weight: " + this.memberWeight);
		return sb.toString();
	}

}
