/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.check.sanity.filesystem;

/** @author Michele Dibenedetto */
public enum SupportedFSType {
  EXT3,
  GPFS;

  /**
   * Parses the provided fsString and returns the matching SupportedFSType
   *
   * @param fsString
   * @return
   * @throws IllegalArgumentException if the provided fsString does not match any SupportedFSType
   */
  public static SupportedFSType parseFS(String fsString) throws IllegalArgumentException {

    if (fsString.trim().equals("gpfs")) {
      return SupportedFSType.GPFS;
    }
    if (fsString.trim().equals("ext3")) {
      return SupportedFSType.EXT3;
    }
    throw new IllegalArgumentException(
        "Unable to parse file system string \'" + fsString + "\' No matching value available");
  }
}
