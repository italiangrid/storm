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

public class SAAuthzType {

	private int authzTypeIndex = -1;
	private String authzTypeName = "UNKNOWN";

	public final static SAAuthzType FIXED = new SAAuthzType(1, "FIXED");
	public final static SAAuthzType AUTHZDB = new SAAuthzType(2, "AUTHZDB");
	public final static SAAuthzType UNKNOWN = new SAAuthzType(-1, "UNKNOWN");

	private SAAuthzType(int authzTypeIndex, String authzTypeName) {

		this.authzTypeIndex = authzTypeIndex;
		this.authzTypeName = authzTypeName;
	}

	public String getSAAuthzTypeName() {

		return this.authzTypeName;
	}

	public static SAAuthzType getSAType(String saType) {

		if (saType.toLowerCase().replaceAll(" ", "")
			.equals(FIXED.getSAAuthzTypeName().toLowerCase())) {
			return FIXED;
		}
		if (saType.toLowerCase().replaceAll(" ", "")
			.equals(AUTHZDB.getSAAuthzTypeName().toLowerCase())) {
			return AUTHZDB;
		}
		return UNKNOWN;
	}

	public int getSAAuthzTypeIndex() {

		return authzTypeIndex;
	}

	public int hashCode() {

		return authzTypeIndex;
	}

	public boolean equals(Object o) {

		boolean result = false;
		if (o instanceof SAAuthzType) {
			SAAuthzType other = (SAAuthzType) o;
			if (other.getSAAuthzTypeIndex() == this.authzTypeIndex) {
				result = true;
			}
		}
		return result;
	}

	public String toString() {

		StringBuilder buf = new StringBuilder();
		buf.append(this.getSAAuthzTypeName());
		return buf.toString();
	}

}
