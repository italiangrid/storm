package it.grid.storm.namespace.model;

import org.apache.commons.logging.Log;
import it.grid.storm.namespace.PropertyInterface;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;

import it.grid.storm.common.types.SizeUnit;

public class Property  implements PropertyInterface {

  private Log log = NamespaceDirector.getLogger();
  private TSizeInBytes totalOnlineSize = SizeUnitType.UNKNOWN.getInBytes();
  private TSizeInBytes totalNearlineSize = SizeUnitType.UNKNOWN.getInBytes();
  private RetentionPolicy retentionPolicy = RetentionPolicy.UNKNOWN;
  private ExpirationMode expirationMode = ExpirationMode.UNKNOWN;
  private AccessLatency accessLatency = AccessLatency.UNKNOWN;

  public TSizeInBytes getTotalOnlineSize() {
    return totalOnlineSize;
  }

  public TSizeInBytes getTotalNearlineSize() {
    return totalNearlineSize;
  }

  public RetentionPolicy getRetentionPolicy() {
    return retentionPolicy;
  }

  public ExpirationMode getExpirationMode() {
    return expirationMode;
  }

  public AccessLatency getAccessLatency() {
    return accessLatency;
  }

  public void setTotalOnlineSize(String unitType, long onlineSize) throws NamespaceException {
    try {
      this.totalOnlineSize = SizeUnitType.getInBytes(unitType, onlineSize);
    }
    catch (InvalidTSizeAttributesException ex1) {
      log.error("TotalOnlineSize parameter is wrong ");
      throw new NamespaceException("'TotalOnlineSize' invalid argument in Namespace configuration.", ex1);
    }
  }

  public void setTotalNearlineSize(String unitType, long nearlineSize) throws NamespaceException {
    try {
      this.totalNearlineSize = SizeUnitType.getInBytes(unitType, nearlineSize);
    }
    catch (InvalidTSizeAttributesException ex1) {
      log.error("TotalOnlineSize parameter is wrong ");
      throw new NamespaceException("'TotalOnlineSize' invalid argument in Namespace configuration.", ex1);
    }
  }

  public void setRetentionPolicy(String retentionPolicy) throws NamespaceException {
    this.retentionPolicy = RetentionPolicy.getRetentionPolicy(retentionPolicy);
  }

  public void setAccessLatency(String accessLatency) throws NamespaceException {
    this.accessLatency = AccessLatency.getAccessLatency(accessLatency);
  }

  public void setExpirationMode(String expirationMode) throws NamespaceException {
    this.expirationMode = ExpirationMode.getExpirationMode(expirationMode);
  }


  /******************************************
   *           VERSION 1.4                  *
  *******************************************/

  public boolean isOnlineSpaceLimited() {
        /** @todo IMPLEMENT */
    return false;
  }

  /**
   *
   * <p>Title: </p>
   *
   * <p>Description: </p>
   *
   * <p>Copyright: Copyright (c) 2007</p>
   *
   * <p>Company: </p>
   *
   * @author not attributable
   * @version 1.0
   */
  public static class SizeUnitType {

    /**
       <xs:simpleType>
       <xs:restriction base="xs:string">
          <xs:enumeration value="online"/>
          <xs:enumeration value="nearline"/>
          <xs:enumeration value="offline"/>
       </xs:restriction>
       </xs:simpleType>
     **/

    private String sizeTypeName;
    private int ordinal;
    private long size;

    public final static SizeUnitType BYTE = new SizeUnitType("Byte", 0, 1);
    public final static SizeUnitType KB = new SizeUnitType("KB", 1, 1024);
    public final static SizeUnitType MB = new SizeUnitType("MB", 2, 1048576);
    public final static SizeUnitType GB = new SizeUnitType("GB", 3, 1073741824);
    public final static SizeUnitType UNKNOWN = new SizeUnitType("UNKNOWN", -1, -1);

    private SizeUnitType(String sizeTypeName, int ordinal, long size) {
      this.sizeTypeName = sizeTypeName;
      this.size = size;
      this.ordinal = ordinal;
    }

    public String getTypeName() {
      return this.sizeTypeName;
    }

    private static SizeUnitType makeUnitType(String unitType) {
      SizeUnitType result = SizeUnitType.UNKNOWN;
      if (unitType.equals(SizeUnitType.BYTE.sizeTypeName)) result = SizeUnitType.BYTE;
      if (unitType.equals(SizeUnitType.KB.sizeTypeName)) result = SizeUnitType.KB;
      if (unitType.equals(SizeUnitType.MB.sizeTypeName)) result = SizeUnitType.MB;
      if (unitType.equals(SizeUnitType.GB.sizeTypeName)) result = SizeUnitType.GB;
      return result;
    }

    public static TSizeInBytes getInBytes(String unitType, long value) throws InvalidTSizeAttributesException {
      TSizeInBytes result = TSizeInBytes.makeEmpty();
      SizeUnitType sizeUnitType = makeUnitType(unitType);
      if (! (sizeUnitType.getTypeName().equals(SizeUnitType.UNKNOWN.getTypeName()))) {
        result = TSizeInBytes.make(value * sizeUnitType.size, SizeUnit.BYTES);
      }
      return result;
    }

    public TSizeInBytes getInBytes() {
      TSizeInBytes result = TSizeInBytes.makeEmpty();
      try {
        result = TSizeInBytes.make(this.size, SizeUnit.BYTES);
      }
      catch (InvalidTSizeAttributesException ex) {
      }
      return result;
    }

  }
}
