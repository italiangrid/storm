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
