/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LcmapsPoolindexInterface extends Library {

  public static final String JNA_LIBRARY_NAME = "/usr/lib64/liblcmaps_return_poolindex_without_gsi.so";
  public static final LcmapsPoolindexInterface INSTANCE =
      (LcmapsPoolindexInterface) Native.load(JNA_LIBRARY_NAME, LcmapsPoolindexInterface.class);

  int lcmaps_return_account_without_gsi(String user_dn, String[] fqan_list, int nfqan,
      LcmapsAccountInfoT plcmaps_account);
}
