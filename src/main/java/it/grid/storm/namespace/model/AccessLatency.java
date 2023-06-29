/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public class AccessLatency {

	/**
	 * <xs:simpleType> <xs:restriction base="xs:string"> <xs:enumeration
	 * value="online"/> <xs:enumeration value="nearline"/> <xs:enumeration
	 * value="offline"/> </xs:restriction> </xs:simpleType>
	 **/

	private String accessLatency;
	private String stringSchema;

	public final static AccessLatency ONLINE = new AccessLatency("ONLINE",
		"online");
	public final static AccessLatency NEARLINE = new AccessLatency("NEARLINE",
		"nearline");
	public final static AccessLatency OFFLINE = new AccessLatency("OFFLINE",
		"offline");
	public final static AccessLatency UNKNOWN = new AccessLatency("UNKNOWN",
		"Access Latency UNKNOWN!");

	private AccessLatency(String accessLatency, String stringSchema) {

		this.accessLatency = accessLatency;
		this.stringSchema = stringSchema;
	}

	// Only get method for Name
	public String getAccessLatencyName() {

		return accessLatency;
	}

	// Only get method for Schema
	public String toString() {

		return this.stringSchema;
	}

	public static AccessLatency getAccessLatency(String accessLatency) {

		if (accessLatency.equals(AccessLatency.ONLINE.toString()))
			return AccessLatency.ONLINE;
		if (accessLatency.equals(AccessLatency.NEARLINE.toString()))
			return AccessLatency.NEARLINE;
		if (accessLatency.equals(AccessLatency.OFFLINE.toString()))
			return AccessLatency.OFFLINE;
		return AccessLatency.UNKNOWN;
	}

}
