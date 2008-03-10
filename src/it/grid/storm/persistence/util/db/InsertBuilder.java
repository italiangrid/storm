package it.grid.storm.persistence.util.db;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class InsertBuilder extends SQLBuilder {
  private String table;
  private Map columnsAndData = new HashMap();

  public void setTable( String table ) {
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
    StringBuffer columns = new StringBuffer();
    StringBuffer values = new StringBuffer();
    StringBuffer what = new StringBuffer();

    String columnName = null;
    Iterator iter = columnsAndData.keySet().iterator();
    while (iter.hasNext()) {
      columnName = (String) iter.next();
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

  public void addColumnAndData( String columnName, Object value ) {
    if( value != null ) {
      columnsAndData.put( columnName, value );
    }
  }






}
