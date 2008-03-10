package it.grid.storm.namespace.model;

public class QuotaType {

  private int ordinalNumber;
  private String quotaType;
  private String stringSchema;

  private String value;

  public final static QuotaType FILESET = new QuotaType("FILESET", "filesetID", 0);
  public final static QuotaType USR = new QuotaType("USR", "userID", 1);
  public final static QuotaType GRP = new QuotaType("GRP", "groupID", 2);
  public final static QuotaType UNKNOWN = new QuotaType("UNKNOWN", "Quota Type UNKNOWN!", -1);

  private QuotaType(String quotaType, String stringSchema, int ord) {
    this.quotaType = quotaType;
    this.stringSchema = stringSchema;
    this.ordinalNumber = ord;
  }


  public static QuotaType buildQuotaType(QuotaType quotaType) {
     String schema = quotaType.stringSchema;
     int ord = quotaType.getOrdinalNumber();
     String type = quotaType.quotaType;
     return new QuotaType(type,schema,ord);
  }

  /***************************************
   * WRITE METHODS
   */
  public void setValue(String value) {
    this.value = value;
  }


  /***************************************
   * READ METHODS
   */

  //Only get method for Name
  public String getQuotaType() {
    return quotaType;
  }

  //Only get method for Schema
  public static String string(int ord) {
    return QuotaType.getQuotaType(ord).toString();
  }

  //Only get method for Ordinal Number
  public int getOrdinalNumber() {
    return this.ordinalNumber;
  }

  //Only get method for Ordinal Number
  public String getValue() {
    return this.value;
  }

  //Only get method for Schema
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("QuotaType:").append(this.quotaType).append("=<").append(this.value).append(">");
    return sb.toString();
  }

  /**
   *
   * @param quotaType String
   * @return QuotaType
   */
  public static QuotaType getQuotaType(String quotaType) {
    if (quotaType.equals(FILESET.toString())) {
      return QuotaType.FILESET;
    }
    if (quotaType.equals(USR.toString())) {
      return QuotaType.USR;
    }
    if (quotaType.equals(GRP.toString())) {
      return QuotaType.GRP;
    }
    return QuotaType.UNKNOWN;
  }

  /**
   *
   * @param quotaType String
   * @return QuotaType
   */
  public static QuotaType getQuotaType(int quotaOrd) {
    if (quotaOrd == 0) {
      return QuotaType.FILESET;
    }
    if (quotaOrd == 1) {
      return QuotaType.USR;
    }
    if (quotaOrd == 2) {
      return QuotaType.GRP;
    }
    return QuotaType.UNKNOWN;
  }

  public int hashCode() {
    return this.ordinalNumber;
  }

  public boolean equals(Object other) {
    boolean result = false;
    if (other instanceof QuotaType) {
      QuotaType qt = (QuotaType) other;
      if (qt.ordinalNumber == this.ordinalNumber) {
        result = true;
      }
    }
    return result;
  }

}
