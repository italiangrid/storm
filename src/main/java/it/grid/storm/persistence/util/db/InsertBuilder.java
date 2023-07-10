/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.persistence.util.db;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InsertBuilder extends SQLBuilder {

  private String table;
  private Map<String, Object> columnsAndData = new HashMap<String, Object>();

  public void setTable(String table) {

    this.table = table;
  }

  public String getTable() {

    return table;
  }

  public String getCommand() {

    return "INSERT INTO ";
  }

  public String getCriteria() {

    return "";
  }

  public String getWhat() {

    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();
    StringBuilder what = new StringBuilder();

    String columnName = null;
    Iterator<String> iter = columnsAndData.keySet().iterator();
    while (iter.hasNext()) {
      columnName = iter.next();
      columns.append(columnName);
      values.append(columnsAndData.get(columnName));
      if (iter.hasNext()) {
        columns.append(',');
        values.append(',');
      }
    }

    what.append(" (");
    what.append(columns);
    what.append(") VALUES (");
    what.append(values);
    what.append(") ");
    return what.toString();
  }

  public void addColumnAndData(String columnName, Object value) {

    if (value != null) {
      columnsAndData.put(columnName, value);
    }
  }
}
