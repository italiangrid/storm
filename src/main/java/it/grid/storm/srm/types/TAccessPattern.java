/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TTransferParameters SRM type.
 *
 * @author Alberto Forti
 * @author Cnaf -INFN Bologna
 * @date July, 2006
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.Map;

public class TAccessPattern {

  public static String PNAME_accessPattern = "accessPattern";

  private String accessPattern = null;

  public static final TAccessPattern TRANSFER_MODE = new TAccessPattern("TRANSFER_MODE"),
      PROCESSING_MODE = new TAccessPattern("PROCESSING_MODE"),
      EMPTY = new TAccessPattern("EMPTY");

  private TAccessPattern(String accessPattern) {

    this.accessPattern = accessPattern;
  }

  public static final TAccessPattern getTAccessPattern(int idx) {

    switch (idx) {
      case 0:
        return TRANSFER_MODE;
      case 1:
        return PROCESSING_MODE;
      default:
        return EMPTY;
    }
  }

  /**
   * decode() method creates a TAccessPattern object from the inforation contained into the
   * structured parameter received from the FE.
   *
   * @param inputParam map structure
   * @param fieldName field name
   * @return
   */
  public static final TAccessPattern decode(Map inputParam, String fieldName) {

    Integer val;

    val = (Integer) inputParam.get(fieldName);
    if (val == null) return EMPTY;

    return TAccessPattern.getTAccessPattern(val.intValue());
  }

  /**
   * encode() method creates structured parameter representing this ogbject. It is passed to the FE.
   *
   * @param outputParam map structure
   * @param fieldName field name
   */
  public void encode(Map outputParam, String fieldName) {

    Integer value = null;

    if (this.equals(TAccessPattern.TRANSFER_MODE)) value = Integer.valueOf(0);
    if (this.equals(TAccessPattern.PROCESSING_MODE)) value = Integer.valueOf(1);

    outputParam.put(fieldName, value);
  }

  public String toString() {

    return accessPattern;
  }

  public String getValue() {

    return accessPattern;
  }
}
