/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

/**
 * This class represents the ReqType of an SRM request. It is a simple
 * application of the TypeSafe Enum Pattern.
 * 
 * @author EGRID ICTP Trieste / CNAF Bologna
 * @date March 18th, 2005
 * @version 3.0
 */
public enum TRequestType {

	PREPARE_TO_GET("PrepareToGet"), PREPARE_TO_PUT("PrepareToPut"), COPY("Copy"), BRING_ON_LINE(
		"BringOnLine"), EMPTY("Empty"), UNKNOWN("Unknown");

	private final String value;

	private TRequestType(String value) {

		this.value = value;
	}

	public String getValue() {

		return value;
	}

	/**
	 * Facility method that returns a TRequestType object given its String
	 * representation. If no TRequestType is found for the given String, an
	 * IllegalArgumentException is thrown.
	 */
	public static TRequestType getTRequestType(String type)
		throws IllegalArgumentException {

		for (TRequestType requestType : TRequestType.values()) {
			if (requestType.getValue().equals(type)) {
				return requestType;
			}
		}
		return UNKNOWN;
	}

	public boolean isEmpty() {

		return this.equals(EMPTY);
	}

	public String toString() {

		return value;
	}
}
