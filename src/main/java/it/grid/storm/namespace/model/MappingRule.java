/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;


public class MappingRule {

	private final String ruleName;
	private final String stfnRoot;
	private final VirtualFS mappedFS;

	public MappingRule(String ruleName, String stfn_root/* , String mapped_fs */,
		VirtualFS vfs) {

		this.ruleName = ruleName;
		this.stfnRoot = stfn_root;
		this.mappedFS = vfs;
	}

	public String getRuleName() {

		return this.ruleName;
	}

	public String getStFNRoot() {

		return this.stfnRoot;
	}

	public VirtualFS getMappedFS() {

		return this.mappedFS;
	}

	public String toString() {

		StringBuilder sb = new StringBuilder();
		String sep = System.getProperty("line.separator");
		sb.append(sep + "   Mapping rule name       : " + this.ruleName + sep);
		sb.append("      StFN-Root            : " + this.stfnRoot + sep);
		sb.append("      mapped-FS             : " + this.mappedFS + sep);
		return sb.toString();
	}

}
