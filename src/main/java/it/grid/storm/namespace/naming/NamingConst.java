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
