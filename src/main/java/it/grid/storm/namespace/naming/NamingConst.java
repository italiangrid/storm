/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.naming;

import it.grid.storm.config.Configuration;

public class NamingConst {

	/**
	 * The separator character used in file paths.
	 */
	public static final char SEPARATOR_CHAR = '/';

	/**
	 * The separator used in file paths.
	 */
	public static final String SEPARATOR = "/";

	/**
	 * The absolute path of the root of a file system.
	 */
	public static final String ROOT_PATH = "/";

	private static NamingConst instance = new NamingConst();

	private final Configuration config;

	private NamingConst() {

		config = Configuration.getInstance();
	}

	public static String getServiceDefaultHost() {

		return instance.config.getServiceHostname();
	}

	public static int getServicePort() {

		return instance.config.getServicePort();
	}

	public static String getServiceSFNQueryPrefix() {

		return "SFN";
	}
}
