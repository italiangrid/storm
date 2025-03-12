/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MySqlFormat implements SQLFormat {

  private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  /**
   * Create a string value of fields
   * 
   * @param value Object
   * @return String
   */
  public String format(Object value) {

    if (value == null) {
      return null;
    }
    if (value instanceof Date) {
      return dateFormat.format(value);
    }
    return value.toString();
  }

}
