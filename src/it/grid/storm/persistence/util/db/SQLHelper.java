package it.grid.storm.persistence.util.db;

public abstract class SQLHelper {

  public String dbmsVendor;
  private SQLFormat formatter;

  protected SQLHelper(String dbmsVendor) {
    this.dbmsVendor = dbmsVendor;
    this.formatter = DataBaseStrategy.getInstance(dbmsVendor).getFormatter();
  }

  public String buildSQL(SQLBuilder builder) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(builder.getCommand());
    buffer.append(builder.getTable());
    buffer.append(builder.getWhat());
    buffer.append(builder.getCriteria());
    return buffer.toString();
  }

  public String format(Object value) {
    return formatter.format(value);
  }

  /**
   *
   * @param value boolean
   * @return String
   */
  public String format(boolean value) {
    String result = null;
    Boolean boolValue = new Boolean(value);
    result = formatter.format(boolValue);
    return result;
  }

  /**
   *
   * @param value int
   * @return String
   */
  public String format(int value) {
    String result = null;
    Integer intValue = null;
    try {
      intValue = new Integer(value);
    }
    catch (NumberFormatException nfe) {
      nfe.printStackTrace();
    }
    result = formatter.format(intValue);
    return result;
  }

  /**
   *
   * @param value long
   * @return String
   */
  public String format(long value) {
    String result = null;
    Long longValue = null;
    try {
      longValue = new Long(value);
    }
    catch (NumberFormatException nfe) {
      nfe.printStackTrace();
    }
    result = formatter.format(longValue);
    return result;
  }

  /**
   *
   * @param date Date
   * @return String
   */
  public String format(java.util.Date date) {

    return formatter.format(date);
  }

}
