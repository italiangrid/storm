/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.namespace.model;

public class ExpirationMode {

  /**
   * <xs:simpleType> <xs:restriction base="xs:string"> <xs:enumeration value="neverExpire"/>
   * <xs:enumeration value="warnWhenExpire"/> <xs:enumeration value="releaseWhenExpire"/>
   * </xs:restriction> </xs:simpleType>
   */
  private String expirationMode;

  private String stringSchema;

  public static final ExpirationMode NEVER_EXPIRE =
      new ExpirationMode("NEVER_EXPIRE", "neverExpire");
  public static final ExpirationMode WARN_WHEN_EXPIRE =
      new ExpirationMode("WARN_WHEN_EXPIRE", "warnWhenExpire");
  public static final ExpirationMode RELEASE_WHEN_EXPIRE =
      new ExpirationMode("RELEASE_WHEN_EXPIRE", "releaseWhenExpire");
  public static final ExpirationMode UNKNOWN =
      new ExpirationMode("UNKNOWN", "Expiration mode UNKNOWN!");

  private ExpirationMode(String expirationMode, String stringSchema) {

    this.expirationMode = expirationMode;
    this.stringSchema = stringSchema;
  }

  // Only get method for Name
  public String getExpirationModeName() {

    return expirationMode;
  }

  // Only get method for Schema
  public String toString() {

    return this.stringSchema;
  }

  public static ExpirationMode getExpirationMode(String expMode) {

    if (expMode.equals(NEVER_EXPIRE.toString())) return ExpirationMode.NEVER_EXPIRE;
    if (expMode.equals(WARN_WHEN_EXPIRE.toString())) return ExpirationMode.WARN_WHEN_EXPIRE;
    if (expMode.equals(RELEASE_WHEN_EXPIRE.toString())) return ExpirationMode.RELEASE_WHEN_EXPIRE;
    return ExpirationMode.UNKNOWN;
  }
}
