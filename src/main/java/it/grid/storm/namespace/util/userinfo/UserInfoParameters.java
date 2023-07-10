/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.util.userinfo;

import java.util.Iterator;
import java.util.List;

public class UserInfoParameters {

  private List<String> parameters = null;

  public UserInfoParameters(List<String> parameters) {

    this.parameters = parameters;
  }

  /** @return List */
  public List<String> getParameters() {

    return this.parameters;
  }

  /** @return String */
  public String toString() {

    if (parameters == null) return "NULL parameters";
    if (parameters.isEmpty()) return "EMPTY parameters";
    StringBuilder result = new StringBuilder();
    Iterator<String> scan = parameters.iterator();
    while (scan.hasNext()) {
      result.append(scan.next());
      result.append(" ");
    }
    return result.toString();
  }
}
