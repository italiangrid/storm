/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
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
