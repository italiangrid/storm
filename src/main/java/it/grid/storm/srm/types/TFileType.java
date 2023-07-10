/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.util.Map;

/**
 * This class represents the TFileType of a File Area managed by Srm.
 *
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
public class TFileType {

  private String fileType = null;

  public static String PNAME_TYPE = "type";
  public static final TFileType FILE = new TFileType("File");
  public static final TFileType DIRECTORY = new TFileType("Directory");
  public static final TFileType LINK = new TFileType("Link");

  private TFileType(String fileType) {

    this.fileType = fileType;
  }

  public String toString() {

    return fileType;
  }

  public String getValue() {

    return fileType;
  }

  public static TFileType getTFileType(String type) {

    if (type.equals(FILE.getValue())) return FILE;
    if (type.equals(DIRECTORY.getValue())) return DIRECTORY;
    if (type.equals(LINK.getValue())) return LINK;
    return null;
  }

  /**
   * Encode method use to represnts in a structured paramter this objects, for communication to FE
   * component.
   *
   * @param param
   * @param name
   */
  public void encode(Map param, String name) {

    Integer value = null;
    if (this.equals(TFileType.FILE)) value = Integer.valueOf(0);
    if (this.equals(TFileType.DIRECTORY)) value = Integer.valueOf(1);
    if (this.equals(TFileType.LINK)) value = Integer.valueOf(2);
    param.put(name, value);
  }
}
