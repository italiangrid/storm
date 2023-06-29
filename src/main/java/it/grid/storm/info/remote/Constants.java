/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.info.remote;

/**
 * @author Michele Dibenedetto
 * 
 */
public class Constants {

	public static final String ENCODING_SCHEME = "UTF-8";

	public static final String RESOURCE = "info/status";

	// public static final String VERSION = "1.0";

	public static final String UPDATE_OPERATION = "update";

	public static final String TOTAL_SPACE_KEY = "total";

	public static final String USED_SPACE_KEY = "used";

	public static final String RESERVED_SPACE_KEY = "reserved";

	public static final String UNAVALILABLE_SPACE_KEY = "unavailable";

	/*
	 * get: /RESOURCE/alias put:
	 * /RESOURCE/alias/UPDATE_OPERATION?TOTAL_SPACE_KEY=total
	 * &USED_SPACE_KEY=used&RESERVED_SPACE_KEY
	 * =reserved&UNAVALILABLE_SPACE_KEY=unavailable put:
	 * /RESOURCE/alias/UPDATE_OPERATION
	 * ?USED_SPACE_KEY=used&RESERVED_SPACE_KEY=reserved
	 * &UNAVALILABLE_SPACE_KEY=unavailable put:
	 * /RESOURCE/alias/UPDATE_OPERATION?USED_SPACE_KEY
	 * =used&RESERVED_SPACE_KEY=reserved put:
	 * /RESOURCE/alias/UPDATE_OPERATION?USED_SPACE_KEY
	 * =used&UNAVALILABLE_SPACE_KEY=unavailable put:
	 * /RESOURCE/alias/UPDATE_OPERATION
	 * ?RESERVED_SPACE_KEY=reserved&UNAVALILABLE_SPACE_KEY=unavailable put:
	 * /RESOURCE/alias/UPDATE_OPERATION?USED_SPACE_KEY=used put:
	 * /RESOURCE/alias/UPDATE_OPERATION?RESERVED_SPACE_KEY=reserved put:
	 * /RESOURCE/alias/UPDATE_OPERATION?UNAVALILABLE_SPACE_KEY=unavailable
	 */
}
