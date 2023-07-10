/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.common.types;

public enum TransferProtocol {

  file, gsiftp, rfio, root, xroot, http, https, dav, davs;

  public String getValue() {

    return name().toLowerCase();
  }

  public String toString() {

    return getValue();
  }

}
