/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN).
 * SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.helper;

public abstract class SQLHelper {

  private final SQLFormat formatter = new MySqlFormat();

  public String format(Object value) {

    return formatter.format(value);
  }

  public String format(boolean value) {

    return formatter.format(Boolean.valueOf(value));
  }

  public String format(int value) throws NumberFormatException {

    return formatter.format(Integer.valueOf(value));
  }

  public String format(long value) throws NumberFormatException {

    return formatter.format(Long.valueOf(value));
  }

  public String format(java.util.Date date) {

    return formatter.format(date);
  }

}
