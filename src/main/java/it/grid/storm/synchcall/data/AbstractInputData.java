/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.synchcall.data;

import java.util.Map;

public abstract class AbstractInputData implements InputData {

  private static final String sepBegin = "(";
  private static final String sepEnd = ")";
  private static final String arrow = "->";

  @Override
  public String display(Map<?, ?> map) {

    StringBuilder sb = new StringBuilder("[");
    for (Object object : map.keySet()) {
      sb.append(sepBegin)
          .append(object.toString())
          .append(arrow)
          .append(map.get(object).toString())
          .append(sepEnd);
    }
    return sb.append("]").toString();
  }
}
