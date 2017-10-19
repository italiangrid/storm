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

public class QuotaType {

	private final int ordinalNumber;
	private final String quotaType;
	private final String stringSchema;

	private String value;

	public final static QuotaType FILESET = new QuotaType("FILESET",
		"filesetName", 0);
	public final static QuotaType USR = new QuotaType("USR", "userName", 1);
	public final static QuotaType GRP = new QuotaType("GRP", "groupName", 2);
	public final static QuotaType UNKNOWN = new QuotaType("UNKNOWN",
		"Quota Type UNKNOWN!", -1);

	private QuotaType(String quotaType, String stringSchema, int ord) {

		this.quotaType = quotaType;
		this.stringSchema = stringSchema;
		ordinalNumber = ord;
	}

	public static QuotaType buildQuotaType(QuotaType quotaType) {

		String schema = quotaType.stringSchema;
		int ord = quotaType.getOrdinalNumber();
		String type = quotaType.quotaType;
		return new QuotaType(type, schema, ord);
	}

	/***************************************
	 * WRITE METHODS
	 */
	public void setValue(String value) {

		this.value = value;
	}

	/***************************************
	 * READ METHODS
	 */

	// Only get method for Name
	public String getQuotaType() {

		return quotaType;
	}

	// Only get method for Schema
	public static String string(int ord) {

		return QuotaType.getQuotaType(ord).toString();
	}

	// Only get method for Ordinal Number
	public int getOrdinalNumber() {

		return ordinalNumber;
	}

	// Only get method for value (that is filesetName or userName or groupName)
	public String getValue() {

		return value;
	}

	// Only get method for Schema
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("QuotaType:").append(quotaType).append("=<").append(value)
			.append(">");
		return sb.toString();
	}

	/**
	 * 
	 * @param quotaType
	 *          String
	 * @return QuotaType
	 */
	public static QuotaType getQuotaType(String quotaType) {

		if (quotaType.equals(FILESET.toString())) {
			return QuotaType.FILESET;
		}
		if (quotaType.equals(USR.toString())) {
			return QuotaType.USR;
		}
		if (quotaType.equals(GRP.toString())) {
			return QuotaType.GRP;
		}
		return QuotaType.UNKNOWN;
	}

	/**
	 * 
	 * @param quotaType
	 *          String
	 * @return QuotaType
	 */
	public static QuotaType getQuotaType(int quotaOrd) {

		if (quotaOrd == 0) {
			return QuotaType.FILESET;
		}
		if (quotaOrd == 1) {
			return QuotaType.USR;
		}
		if (quotaOrd == 2) {
			return QuotaType.GRP;
		}
		return QuotaType.UNKNOWN;
	}

	@Override
	public int hashCode() {

		return ordinalNumber;
	}

	@Override
	public boolean equals(Object other) {

		boolean result = false;
		if (other instanceof QuotaType) {
			QuotaType qt = (QuotaType) other;
			if (qt.ordinalNumber == ordinalNumber) {
				result = true;
			}
		}
		return result;
	}

}
