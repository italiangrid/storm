/*
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package it.grid.storm.namespace.remote;

/**
 * @author Michele Dibenedetto
 */
public class Constants {

	public static final String ENCODING_SCHEME = "UTF-8";
	public static final String RESOURCE = "configuration";
	public static final String VERSION = "1.4";
	public static final String LIST_ALL_KEY = "StorageAreaList";
	public static final char VFS_LIST_SEPARATOR = ':';
	public static final String VFS_NAME_KEY = "name";
	public static final char VFS_FIELD_MATCHER = '=';
	public static final char VFS_FIELD_SEPARATOR = '&';
	public static final String VFS_ROOT_KEY = "root";
	public static final String VFS_STFN_ROOT_KEY = "stfnRoot";
	public static final char VFS_STFN_ROOT_SEPARATOR = ';';
	public static final String VFS_ENABLED_PROTOCOLS_KEY = "protocols";
	public static final char VFS_ENABLED_PROTOCOLS_SEPARATOR = ';';
	public static final String VFS_ANONYMOUS_PERMS_KEY = "anonymous";
	public static final String LIST_ALL_VFS = "VirtualFSList";
	
	public static enum HttpPerms { NOREAD, READ, READWRITE };

}
