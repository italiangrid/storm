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

public class ExpirationMode {

	/**
	 * <xs:simpleType> <xs:restriction base="xs:string"> <xs:enumeration
	 * value="neverExpire"/> <xs:enumeration value="warnWhenExpire"/>
	 * <xs:enumeration value="releaseWhenExpire"/> </xs:restriction>
	 * </xs:simpleType>
	 **/

	private String expirationMode;
	private String stringSchema;

	public final static ExpirationMode NEVER_EXPIRE = new ExpirationMode(
		"NEVER_EXPIRE", "neverExpire");
	public final static ExpirationMode WARN_WHEN_EXPIRE = new ExpirationMode(
		"WARN_WHEN_EXPIRE", "warnWhenExpire");
	public final static ExpirationMode RELEASE_WHEN_EXPIRE = new ExpirationMode(
		"RELEASE_WHEN_EXPIRE", "releaseWhenExpire");
	public final static ExpirationMode UNKNOWN = new ExpirationMode("UNKNOWN",
		"Expiration mode UNKNOWN!");

	private ExpirationMode(String expirationMode, String stringSchema) {

		this.expirationMode = expirationMode;
		this.stringSchema = stringSchema;
	}

	// Only get method for Name
	public String getExpirationModeName() {

		return expirationMode;
	}

	// Only get method for Schema
	public String toString() {

		return this.stringSchema;
	}

	public static ExpirationMode getExpirationMode(String expMode) {

		if (expMode.equals(NEVER_EXPIRE.toString()))
			return ExpirationMode.NEVER_EXPIRE;
		if (expMode.equals(WARN_WHEN_EXPIRE.toString()))
			return ExpirationMode.WARN_WHEN_EXPIRE;
		if (expMode.equals(RELEASE_WHEN_EXPIRE.toString()))
			return ExpirationMode.RELEASE_WHEN_EXPIRE;
		return ExpirationMode.UNKNOWN;
	}
}
