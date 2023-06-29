/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * 
 */
package it.grid.storm.authz.path.model;

/**
 * @author zappi
 * 
 */
public class PathPrincipal {

  public static final String prefix = "@";

  private String localGroupName;
  private boolean principalCategory = false;

  public PathPrincipal(String principal) {

    principalCategory = principal.startsWith(prefix);
    localGroupName = principal;
  }

  public boolean isLocalGroup() {

    return !principalCategory;
  }

  public String getLocalGroupName() {

    return localGroupName;
  }

  public boolean equals(Object o) {

    if (o instanceof PathPrincipal) {
      PathPrincipal op = (PathPrincipal) o;
      if (op.isLocalGroup() && (isLocalGroup())) {
        return (op.getLocalGroupName().equals(getLocalGroupName()));
      }
    }
    return false;
  }

  @Override
  public int hashCode() {

    int result = 17;
    result = 31 * result + (localGroupName != null ? localGroupName.hashCode() : 0);
    result = 31 * result + (principalCategory ? 1 : 0);
    return result;
  }

}
