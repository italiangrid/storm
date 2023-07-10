/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.util;

/** @author baltico */
public class GPFSSizeHelper {

  private GPFSSizeHelper() {}

  public static long getBytesFromKIB(long kibiBytes) {

    if (kibiBytes < 0) {
      throw new IllegalArgumentException("Invalid kibiBytes number: " + kibiBytes);
    }
    return kibiBytes * 1024;
  }
}
