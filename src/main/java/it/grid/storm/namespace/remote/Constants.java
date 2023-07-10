/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.remote;

/** @author Michele Dibenedetto */
public class Constants {

  public static final String ENCODING_SCHEME = "UTF-8";
  public static final String RESOURCE = "configuration";
  public static final String VERSION_1_0 = "1.0";
  public static final String VERSION_1_1 = "1.1";
  public static final String VERSION_1_2 = "1.2";
  public static final String VERSION_1_3 = "1.3";
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

  public static enum HttpPerms {
    NOREAD,
    READ,
    READWRITE
  };
}
