/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.jna.lcmaps;

public enum LcmapsError {

  // @formatter:off
  INIT_FAILURE(1, "lcmaps initialization failed"),
  ACCOUNT_INITIALIZATION_FAILURE(2, "lcmaps_account object creation failed"),
  RETURN_ACCOUNT_FAILED(3, "lcmaps_return_account_without_gsi call failed"),
  NO_GIDS_RETURNED(4, "no gids provided by the lcmaps_return_account_without_gsi call"),
  UNREACHIBLE_CODE(5, "unexpected condition, this code should be nor reachable"),
  UNKNOW_ERROR(-1, "error unknown");
  // @formatter:on

  private final int errorCode;
  private final String errorMessage;

  /**
   * @param errorCode
   * @param errorMessage
   */
  private LcmapsError(int errorCode, String errorMessage) {

    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * @param errorCode
   * @return
   */
  public static LcmapsError getError(int errorCode) {

    for (LcmapsError error : LcmapsError.values()) {
      if (error.errorCode == errorCode) {
        return error;
      }
    }
    return UNKNOW_ERROR;
  }

  /**
   * @return
   */
  public String getMessage() {

    return errorMessage;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  public String toString() {

    return super.toString() + ": <errorCode=" + errorCode + " ; errorMessage=" + errorMessage + ">";
  }
}
