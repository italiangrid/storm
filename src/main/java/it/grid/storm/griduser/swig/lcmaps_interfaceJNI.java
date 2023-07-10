/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/*
 * ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org). Version
 * 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * -----------------------------------------------------------------------------
 */

package it.grid.storm.griduser.swig;

class lcmaps_interfaceJNI {

  static {
    try {
      System.loadLibrary("lcmaps_interface");
    } catch (UnsatisfiedLinkError e) {
      final String libfile = "'" + System.mapLibraryName("lcmaps_interface") + "'";
      final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(lcmaps_interfaceJNI.class);

      log.error("Native code library {} failed to load: {}", libfile, e.getMessage());

      final String ldpath = System.getProperty("java.library.path");
      if (null != ldpath)
        log.error("Java VM searched for {} in java.library.path: {}", libfile, ldpath);
      else log.error("Java VM library search path is null!");
      log.error(
          "Add the library location to the environment variable LD_LIBRARY_PATH or to the Java property java.library.path");
      throw new UnsatisfiedLinkError(
          "Native code library " + libfile + " failed to load: " + e.getMessage());
    }
  }

  public static final native int get_localuser_info_uid(long jarg1);

  public static final native int[] get_localuser_info_gids(long jarg1);

  public static final native long get_localuser_info_ngids(long jarg1);

  public static final native long new_localuser_info(String jarg1, String[] jarg2);

  public static final native void delete_localuser_info(long jarg1);
}
