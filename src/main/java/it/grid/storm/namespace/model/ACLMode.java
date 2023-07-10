/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import it.grid.storm.namespace.NamespaceException;

public class ACLMode {

  public static final ACLMode JUST_IN_TIME = new ACLMode("JiT");
  public static final ACLMode AHEAD_OF_TIME = new ACLMode("AoT");
  public static final ACLMode UNDEF = new ACLMode("UNDEF");

  private String aclMode;

  private ACLMode(String mode) {

    this.aclMode = mode;
  }

  public static ACLMode makeFromString(String aclMode) throws NamespaceException {

    ACLMode result = ACLMode.UNDEF;
    if (aclMode.toLowerCase().equals(ACLMode.AHEAD_OF_TIME.toString().toLowerCase())) {
      result = ACLMode.AHEAD_OF_TIME;
    } else if (aclMode.toLowerCase().equals(ACLMode.JUST_IN_TIME.toString().toLowerCase())) {
      result = ACLMode.JUST_IN_TIME;
    } else {
      throw new NamespaceException("ACL Mode is not recognized!");
    }
    return result;
  }

  @Override
  public String toString() {

    return aclMode;
  }

  @Override
  public boolean equals(Object obj) {

    if (obj == null) {
      return false;
    }
    if (obj instanceof ACLMode) {
      ACLMode aclMode = (ACLMode) obj;
      if (aclMode.toString().toLowerCase().equals(this.toString().toLowerCase())) {
        return true;
      }
    } else {
      return false;
    }
    return false;
  }

  @Override
  public int hashCode() {

    int result = 17;
    result = 31 * result + (aclMode != null ? aclMode.hashCode() : 0);
    return result;
  }
}
