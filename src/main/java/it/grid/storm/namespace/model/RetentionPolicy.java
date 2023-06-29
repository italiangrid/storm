/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public class RetentionPolicy {

	/**
	 * <xs:simpleType> <xs:restriction base="xs:string"> <xs:enumeration
	 * value="custodial"/> <xs:enumeration value="output"/> <xs:enumeration
	 * value="replica"/> </xs:restriction> </xs:simpleType>
	 **/
	private String retentionPolicy;
	private String stringSchema;

	public final static RetentionPolicy CUSTODIAL = new RetentionPolicy(
		"CUSTODIAL", "custodial");
	public final static RetentionPolicy OUTPUT = new RetentionPolicy("OUTPUT",
		"output");
	public final static RetentionPolicy REPLICA = new RetentionPolicy("REPLICA",
		"replica");
	public final static RetentionPolicy UNKNOWN = new RetentionPolicy("UNKNOWN",
		"Retention policy UNKNOWN!");

	private RetentionPolicy(String retentionPolicy, String stringSchema) {

		this.retentionPolicy = retentionPolicy;
		this.stringSchema = stringSchema;
	}

	// Only get method for Name
	public String getRetentionPolicyName() {

		return retentionPolicy;
	}

	// Only get method for Schema
	public String toString() {

		return this.stringSchema;
	}

	public static RetentionPolicy getRetentionPolicy(String retentionPolicy) {

		if (retentionPolicy.equals(RetentionPolicy.CUSTODIAL.toString()))
			return RetentionPolicy.CUSTODIAL;
		if (retentionPolicy.equals(RetentionPolicy.OUTPUT.toString()))
			return RetentionPolicy.OUTPUT;
		if (retentionPolicy.equals(RetentionPolicy.REPLICA.toString()))
			return RetentionPolicy.REPLICA;
		return RetentionPolicy.UNKNOWN;
	}

}
