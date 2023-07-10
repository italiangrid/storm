/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

import com.sun.jna.Library;
import com.sun.jna.Native;
import java.nio.IntBuffer;

/** @author Michele Dibenedetto */
public interface StormLcmapsLibrary extends Library {

  public static final java.lang.String JNA_LIBRARY_NAME = "storm_lcmaps";
  public static final StormLcmapsLibrary INSTANCE =
      (StormLcmapsLibrary)
          Native.loadLibrary(StormLcmapsLibrary.JNA_LIBRARY_NAME, StormLcmapsLibrary.class);

  /**
   * @param logfile the lcmaps log file
   * @param user_dn the user DN
   * @param fqan_list the array of FQAN
   * @param nfqan the number of FQAN in fqan_list
   * @param uid the mapped user id
   * @param gid the mapped group id
   * @return 0 if mapping is performed correctly, an int greater than 0 otherwise
   */
  int map_user(
      String logfile, String user_dn, String[] fqan_list, int nfqan, IntBuffer uid, IntBuffer gid);

  public enum Errors {
    INIT_FAILURE(1, "lcmaps initialization failed"),
    ACCOUNT_INITIALIZATION_FAILURE(2, "lcmaps_account object creation failed"),
    RETURN_ACCOUNT_FAILED(3, "lcmaps_return_account_without_gsi call failed"),
    NO_GIDS_RETURNED(4, "no gids provided by the lcmaps_return_account_without_gsi call"),
    UNREACHIBLE_CODE(5, "unexpected condition, this code should be nor reachable"),
    UNKNOW_ERROR(-1, "error unknown");

    private final int errorCode;
    private final String errorMessage;

    /**
     * @param errorCode
     * @param errorMessage
     */
    private Errors(int errorCode, String errorMessage) {

      this.errorCode = errorCode;
      this.errorMessage = errorMessage;
    }

    /**
     * @param errorCode
     * @return
     */
    public static Errors getError(int errorCode) {

      for (Errors error : Errors.values()) {
        if (error.errorCode == errorCode) {
          return error;
        }
      }
      return UNKNOW_ERROR;
    }

    /** @return */
    public String getMessage() {

      return errorMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    public String toString() {

      return super.toString()
          + ": <errorCode="
          + errorCode
          + " ; errorMessage="
          + errorMessage
          + ">";
    }
  }
}
