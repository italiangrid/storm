/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.db;

import java.text.SimpleDateFormat;

public class MySqlFormat implements SQLFormat {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Create a string value of fields insertable into the query
   *
   * @param value Object
   * @return String
   */
  public String format(Object value) {

    if (value == null) {
      return null;
    }
    Class clazz = value.getClass();
    if (Character.class.equals(clazz) || char.class.equals(clazz)) {
      value = value.toString();
    }
    if (value instanceof String) {
      return value.toString();
    }
    if (value instanceof java.util.Date) {
      return dateFormat.format(value);
    }
    return value.toString();
  }
}
