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

package it.grid.storm.srm.types;

import java.util.Map;

import it.grid.storm.config.model.v2.StorageType;

/**
 * This class represents the TFileStorageType of an Srm request.
 * 
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
public class TFileStorageType {

  private String fileType = null;
  public final static String PNAME_FILESTORAGETYPE = "fileStorageType";

  public static final TFileStorageType VOLATILE = new TFileStorageType("Volatile");
  public static final TFileStorageType DURABLE = new TFileStorageType("Durable");
  public static final TFileStorageType PERMANENT = new TFileStorageType("Permanent");
  public static final TFileStorageType EMPTY = new TFileStorageType("Empty");

  private TFileStorageType(String fileType) {

    this.fileType = fileType;
  }

  public String toString() {

    return fileType;
  }

  public String getValue() {

    return fileType;
  }

  /**
   * Facility method to obtain a TFileStorageType object given its String representation. If an
   * invalid String is supplied, then an EMPTY TFileStorageType is returned.
   */
  public static TFileStorageType getTFileStorageType(String type) {

    if (type.toLowerCase().trim().equals(VOLATILE.getValue().toLowerCase())) {
      return VOLATILE;
    }
    if (type.toLowerCase().trim().equals(PERMANENT.getValue().toLowerCase())) {
      return PERMANENT;
    }
    if (type.toLowerCase().trim().equals(DURABLE.getValue().toLowerCase())) {
      return DURABLE;
    } else {
      return EMPTY;
    }
  }

  public static TFileStorageType getTFileStorageType(StorageType type) {

    switch (type) {
      case V:
        return VOLATILE;
      case P:
        return PERMANENT;
      case D:
        return DURABLE;
      default:
        return EMPTY;
    }
  }

  /**
   * Facility method to obtain a TFileStorageType object given its String representation. If an
   * invalid String is supplied, then an EMPTY TFileStorageType is returned.
   */
  public static TFileStorageType getTFileStorageType(int type) {

    switch (type) {
      case 0:
        return VOLATILE;
      case 1:
        return DURABLE;
      case 2:
        return PERMANENT;
      default:
        return EMPTY;
    }
  }

  /**
   * Decode method use to create a TFileStorageType object from the information contain into
   * structured parameter receive from FE.
   * 
   * @param inputParam
   * @param name
   * @return
   */

  public static TFileStorageType decode(Map<?, ?> inputParam, String name) {

    Integer fileType = (Integer) inputParam.get(name);
    if (fileType != null)
      return TFileStorageType.getTFileStorageType(fileType.intValue());
    else
      return TFileStorageType.EMPTY;
  }

  /**
   * Encode method use to Create a structured parameter that represents this object, used for pass
   * information to FE.
   * 
   * @param param
   * @param name
   */
  public void encode(Map<String, Object> param, String name) {

    Integer value = null;
    if (this.equals(TFileStorageType.VOLATILE))
      value = Integer.valueOf(0);
    if (this.equals(TFileStorageType.DURABLE))
      value = Integer.valueOf(1);
    if (this.equals(TFileStorageType.PERMANENT))
      value = Integer.valueOf(2);
    param.put(name, value);
  }

}
