/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LcmapsInterface extends Library {

  public static final String JNA_LIBRARY_NAME = "lcmaps";
  public static final LcmapsInterface INSTANCE =
      (LcmapsInterface) Native.load(JNA_LIBRARY_NAME, LcmapsInterface.class);

  int lcmaps_init_and_logfile(String logfile, com.sun.jna.Pointer fp, short logtype);
}
