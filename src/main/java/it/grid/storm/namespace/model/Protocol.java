/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public enum Protocol {

  FILE, GSIFTP, RFIO, SRM, ROOT, XROOT, HTTP, HTTPS, DAV, DAVS;
  
  public String getProtocolName() {

    return name();
  }

  public String getSchema() {

    return name().toLowerCase();
  }

  public String getProtocolPrefix() {

    return getSchema() + "://";
  }
}
