/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

/** @author dibenedetto_m */
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

public class lcmaps_account_info_t extends Structure {

  // / < the uid of the local account
  public int uid;
  /**
   * < the list of primary gids<br>
   * C type : int*
   */
  public IntByReference pgid_list;
  // / < the number of primary gids found
  public int npgid;
  /**
   * < the list of secondary gids<br>
   * C type : int*
   */
  public IntByReference sgid_list;
  // / < the number of secondary gids found
  public int nsgid;
  /**
   * < the pool index<br>
   * C type : char*
   */
  public String poolindex;

  public lcmaps_account_info_t() {

    super();
  }

  /**
   * @param uid < the uid of the local account<br>
   * @param pgid_list < the list of primary gids<br>
   *     C type : int*<br>
   * @param npgid < the number of primary gids found<br>
   * @param sgid_list < the list of secondary gids<br>
   *     C type : int*<br>
   * @param nsgid < the number of secondary gids found<br>
   * @param poolindex < the pool index<br>
   *     C type : char*
   */
  public lcmaps_account_info_t(
      int uid,
      IntByReference pgid_list,
      int npgid,
      IntByReference sgid_list,
      int nsgid,
      String poolindex) {

    super();
    this.uid = uid;
    this.pgid_list = pgid_list;
    this.npgid = npgid;
    this.sgid_list = sgid_list;
    this.nsgid = nsgid;
    this.poolindex = poolindex;
  }

  public static class ByReference extends lcmaps_account_info_t implements Structure.ByReference {};

  public static class ByValue extends lcmaps_account_info_t implements Structure.ByValue {};
}
