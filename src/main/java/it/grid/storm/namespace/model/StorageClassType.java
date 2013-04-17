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

public enum StorageClassType {

	T0D0("T0D0", "T0D0"), T0D1("T0D1", "T0D1"), T1D0("T1D0", "T1D0"), T1D1(
		"T1D1", "T1D1"), UNKNOWN("UNKNOWN", "Storage Class Type UNKNOWN!");

	private String storageClassTypeString;
	private String stringSchema;

	private StorageClassType(String storageClassTypeString, String stringSchema) {

		this.storageClassTypeString = storageClassTypeString;
		this.stringSchema = stringSchema;

	}

	/**
	 * 
	 * @param storageClassTypeString
	 *          String
	 * @return StorageClassType
	 */
	public static StorageClassType getStorageClassType(
		String storageClassTypeString) {

		for (StorageClassType sct : StorageClassType.values()) {
			if (sct.getStorageClassTypeString().equals(storageClassTypeString)) {
				return sct;
			}
		}

		return UNKNOWN;
	}

	/**
	 * Returns the String representation of this storage class type instance.
	 * 
	 * @return the String representation of this storage class type instance.
	 */
	public String getStorageClassTypeString() {

		return storageClassTypeString;
	}

	public String getStringSchema() {

		return stringSchema;
	}

	public boolean isTapeEnabled() {

		if (this.equals(T1D0) || this.equals(T1D1)) {
			return true;
		}

		return false;
	}

	// Only get method for Schema
	public String toString() {

		return this.stringSchema;
	}
}
