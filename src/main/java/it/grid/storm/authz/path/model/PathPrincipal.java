/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
