/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**

typedef struct lcmaps_account_info_s
{
    uid_t          uid;         // the uid of the local account
    gid_t *        pgid_list;   // the list of primary gids
    int            npgid;       // the number of primary gids found
    gid_t *        sgid_list;   // the list of secondary gids
    int            nsgid;       // the number of secondary gids found
    char *         poolindex;   // the pool index
} lcmaps_account_info_t;

*/

@FieldOrder({"uid", "pgid_list", "npgid", "sgid_list", "nsgid", "poolindex"})
public class LcmapsAccountInfoT extends Structure {

  public int uid;
  public Pointer pgid_list;
  public int npgid;
  public Pointer sgid_list;
  public int nsgid;
  public String poolindex;

  public static class ByReference extends LcmapsAccountInfoT implements Structure.ByReference {

  };

  public static class ByValue extends LcmapsAccountInfoT implements Structure.ByValue {

  }

  @Override
  public String toString() {
    return "LcmapsAccountInfoT [uid=" + uid + ", pgid_list=" + pgid_list + ", npgid=" + npgid
        + ", sgid_list=" + sgid_list + ", nsgid=" + nsgid + ", poolindex=" + poolindex + "]";
  }

}
