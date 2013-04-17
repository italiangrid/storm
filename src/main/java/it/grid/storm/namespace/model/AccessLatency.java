/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
