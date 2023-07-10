/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

import it.grid.storm.common.types.SizeUnit;
import it.grid.storm.namespace.NamespaceDirector;
import it.grid.storm.namespace.NamespaceException;
import it.grid.storm.namespace.PropertyInterface;
import it.grid.storm.srm.types.InvalidTSizeAttributesException;
import it.grid.storm.srm.types.TSizeInBytes;
import org.slf4j.Logger;

public class Property implements PropertyInterface {

  private Logger log = NamespaceDirector.getLogger();
  private TSizeInBytes totalOnlineSize = TSizeInBytes.makeEmpty();
  private TSizeInBytes totalNearlineSize = TSizeInBytes.makeEmpty();
  private RetentionPolicy retentionPolicy = RetentionPolicy.UNKNOWN;
  private ExpirationMode expirationMode = ExpirationMode.UNKNOWN;
  private AccessLatency accessLatency = AccessLatency.UNKNOWN;
  private boolean hasLimitedSize = false;

  public static Property from(PropertyInterface other) {

    Property property = new Property();
    property.accessLatency = other.getAccessLatency();
    property.expirationMode = other.getExpirationMode();
    property.hasLimitedSize = other.hasLimitedSize();
    property.retentionPolicy = other.getRetentionPolicy();
    property.totalNearlineSize = other.getTotalNearlineSize();
    property.totalOnlineSize = other.getTotalOnlineSize();
    return property;
  }

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

  @Override
  public boolean hasLimitedSize() {

    return hasLimitedSize;
  }

  public void setTotalOnlineSize(String unitType, long onlineSize) throws NamespaceException {

    try {
      this.totalOnlineSize = SizeUnitType.getInBytes(unitType, onlineSize);
    } catch (InvalidTSizeAttributesException ex1) {
      log.error("TotalOnlineSize parameter is wrong ");
      throw new NamespaceException(
          "'TotalOnlineSize' invalid argument in Namespace configuration.", ex1);
    }
  }

  public void setTotalNearlineSize(String unitType, long nearlineSize) throws NamespaceException {

    try {
      this.totalNearlineSize = SizeUnitType.getInBytes(unitType, nearlineSize);
    } catch (InvalidTSizeAttributesException ex1) {
      log.error("TotalOnlineSize parameter is wrong ");
      throw new NamespaceException(
          "'TotalOnlineSize' invalid argument in Namespace configuration.", ex1);
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

  public void setLimitedSize(boolean limitedSize) throws NamespaceException {

    this.hasLimitedSize = limitedSize;
  }

  /**
   * **************************************** VERSION 1.4 *
   * *****************************************
   */
  public boolean isOnlineSpaceLimited() {

    return hasLimitedSize;
  }

  /**
   * Title:
   *
   * <p>Description:
   *
   * <p>Copyright: Copyright (c) 2007
   *
   * <p>Company:
   *
   * @author not attributable
   * @version 1.0
   */
  public static class SizeUnitType {

    private Logger log = NamespaceDirector.getLogger();

    /**
     * <xs:simpleType> <xs:restriction base="xs:string"> <xs:enumeration value="online"/>
     * <xs:enumeration value="nearline"/> <xs:enumeration value="offline"/> </xs:restriction>
     * </xs:simpleType>
     */
    private String sizeTypeName;

    private int ordinal;
    private long size;

    public static final SizeUnitType BYTE = new SizeUnitType("Byte", 0, 1);
    public static final SizeUnitType KB = new SizeUnitType("KB", 1, 1000);
    public static final SizeUnitType MB = new SizeUnitType("MB", 2, 1000000);
    public static final SizeUnitType GB = new SizeUnitType("GB", 3, 1000000000);
    public static final SizeUnitType TB = new SizeUnitType("TB", 4, 1000000000000L);
    public static final SizeUnitType UNKNOWN = new SizeUnitType("UNKNOWN", -1, -1);

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
      if (unitType.equals(SizeUnitType.BYTE.sizeTypeName)) {
        result = SizeUnitType.BYTE;
      }
      if (unitType.equals(SizeUnitType.KB.sizeTypeName)) {
        result = SizeUnitType.KB;
      }
      if (unitType.equals(SizeUnitType.MB.sizeTypeName)) {
        result = SizeUnitType.MB;
      }
      if (unitType.equals(SizeUnitType.GB.sizeTypeName)) {
        result = SizeUnitType.GB;
      }
      if (unitType.equals(SizeUnitType.TB.sizeTypeName)) {
        result = SizeUnitType.TB;
      }
      return result;
    }

    public static TSizeInBytes getInBytes(String unitType, long value)
        throws InvalidTSizeAttributesException {

      TSizeInBytes result = TSizeInBytes.makeEmpty();
      SizeUnitType sizeUnitType = makeUnitType(unitType);
      if (!(sizeUnitType.getTypeName().equals(SizeUnitType.UNKNOWN.getTypeName()))) {
        result = TSizeInBytes.make(value * sizeUnitType.size, SizeUnit.BYTES);
      }
      return result;
    }

    public TSizeInBytes getInBytes() {

      TSizeInBytes result = TSizeInBytes.makeEmpty();
      try {
        result = TSizeInBytes.make(this.size, SizeUnit.BYTES);
      } catch (InvalidTSizeAttributesException ex) {
        log.error("Size '" + this.size + "'are invalid. Use empty size: '" + result + "'." + ex);
      }
      return result;
    }
  }
}
