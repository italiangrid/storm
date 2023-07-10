/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.xmlrpc.converter;

import it.grid.storm.srm.types.ArrayOfSURLs;
import java.util.Map;

public class ParameterDisplayHelper {

  private static final String sepBegin = "(";
  private static final String sepEnd = ")";
  private static final String arrow = "->";

  public static String display(Map<?, ?> map) {

    StringBuilder sb = new StringBuilder("[");
    for (Object mapKey : map.keySet()) {
      String mapKeyStr = mapKey.toString();
      sb.append(sepBegin).append(mapKeyStr);
      if ((mapKeyStr.equals("userFQANS")) || (mapKeyStr.equals(ArrayOfSURLs.ARRAY_OF_SURLS))) {
        sb.append(arrow).append("[");
        Object[] mapKeyValues = (Object[]) map.get(mapKey);
        for (int i = 0; i < mapKeyValues.length - 1; i++) {
          sb.append(mapKeyValues[i].toString()).append(",");
        }
        sb.append(mapKeyValues[mapKeyValues.length - 1]).append("]");

      } else {
        String mapKeyValue = "'" + (map.get(mapKey)).toString() + "'";
        sb.append(arrow).append(mapKeyValue).append("]");
      }
    }

    return sb.append("]").toString();
  }
}
