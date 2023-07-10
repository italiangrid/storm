/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.authz.sa.model;

public class AceType {

  public static final AceType ALLOW = new AceType("ALLOW");
  public static final AceType DENY = new AceType("DENY");
  public static final AceType UNKNOWN = new AceType("UNKNOWN");

  private String aceType;

  private AceType(String aceType) {

    this.aceType = aceType;
  }

  public static AceType getAceType(String aceTp) {

    if (aceTp.toUpperCase().equals(ALLOW.toString())) {
      return AceType.ALLOW;
    }
    if (aceTp.toUpperCase().equals(DENY.toString())) {
      return AceType.DENY;
    }
    return AceType.UNKNOWN;
  }

  public String toString() {

    return aceType;
  }
}
