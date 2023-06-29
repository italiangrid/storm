/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.tape.recalltable.persistence;

/**
 * @author zappi
 * 
 */
public class TapeRecallBuilder {

	/**
	 * { "filename":"<file-name>"; "dn":"<DN>"; "fqans":["fqan":"<FQAN>",
	 * "fqan":"<FQAN>"]; "vo-name":"<vo-name>" }
	 **/

	public static final String TASK_START = "{";
	public static final String TASK_END = "}";
	public static final String ELEMENT_SEP = " # ";
	public static final String FN_PREFIX = "filename";
	public static final String DN_PREFIX = "dn";
	public static final String FQANS_PREFIX = "fqans";
	public static final String FQANS_ARRAY_START = "[";
	public static final String FQANS_ARRAY_END = "]";
	public static final String FQAN_PREFIX = "fqan";
	public static final String FQAN_SEP = ",";
	public static final String VONAME_PREFIX = "vo-name";
	public static final String USERID_PREFIX = "userId";
	public static final String EQUAL_CHAR = "=";

	private TapeRecallBuilder() {}
}
