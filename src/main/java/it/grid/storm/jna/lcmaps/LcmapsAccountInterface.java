/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LcmapsAccountInterface extends Library {

  public static final java.lang.String JNA_LIBRARY_NAME = "/usr/lib64/liblcmaps_without_gsi.so";
  public static final LcmapsAccountInterface INSTANCE =
      (LcmapsAccountInterface) Native.load(JNA_LIBRARY_NAME, LcmapsAccountInterface.class);

  int lcmaps_account_info_init(lcmaps_account_info_t plcmaps_account);

  int lcmaps_account_info_clean(lcmaps_account_info_t plcmaps_account);
}
