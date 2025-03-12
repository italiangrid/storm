/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import java.util.Arrays;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"uid", "pgid_list", "npgid", "sgid_list", "nsgid", "poolindex"})
public class LcmapsAccountInfoT extends Structure {

  /**
   * The user id of the local account
   */
  public int uid;

  /**
   * The list of primary group id
   */
  public Pointer pgid_list;

  /**
   * The number of primary group id found
   */
  public int npgid;

  /**
   * The list of secondary group id
   */
  public Pointer sgid_list;

  /**
   * The number of secondary group id found
   */
  public int nsgid;

  /**
   * The pool index
   */
  public String poolindex;

  public static class ByReference extends LcmapsAccountInfoT implements Structure.ByReference {
  }

  public static class ByValue extends LcmapsAccountInfoT implements Structure.ByValue {
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append("LcmapsAccountInfoT [uid=");
    b.append(uid);
    b.append(", pgid_list=");
    if (pgid_list != null) {
      b.append(Arrays.toString(pgid_list.getIntArray(0, npgid)));
    } else {
      b.append("null");
    }
    b.append(", npgid=");
    b.append(npgid);
    b.append(", sgid_list=");
    if (sgid_list != null) {
      b.append(Arrays.toString(sgid_list.getIntArray(0, nsgid)));
    } else {
      b.append("null");
    }
    b.append(", nsgid=");
    b.append(nsgid);
    b.append(", poolindex=");
    b.append(poolindex);
    b.append("]");
    return b.toString();
  }

}
