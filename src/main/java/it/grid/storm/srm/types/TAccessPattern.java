/*
 * 
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
      PROCESSING_MODE = new TAccessPattern("PROCESSING_MODE"), EMPTY = new TAccessPattern("EMPTY");

  private TAccessPattern(String accessPattern) {

    this.accessPattern = accessPattern;
  }

  public final static TAccessPattern getTAccessPattern(int idx) {

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
  public final static TAccessPattern decode(Map<String, Object> inputParam, String fieldName) {

    Integer val;

    val = (Integer) inputParam.get(fieldName);
    if (val == null)
      return EMPTY;

    return TAccessPattern.getTAccessPattern(val.intValue());
  }

  /**
   * encode() method creates structured parameter representing this object. It is passed to the FE.
   * 
   * @param outputParam map structure
   * @param fieldName field name
   */
  public void encode(Map<String, Object> outputParam, String fieldName) {

    Integer value = null;

    if (this.equals(TAccessPattern.TRANSFER_MODE))
      value = Integer.valueOf(0);
    if (this.equals(TAccessPattern.PROCESSING_MODE))
      value = Integer.valueOf(1);

    outputParam.put(fieldName, value);
  }

  public String toString() {

    return accessPattern;
  }

  public String getValue() {

    return accessPattern;
  }
}
