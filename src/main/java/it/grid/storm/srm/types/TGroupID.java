/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TGroupID in Srm request.
 *
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.Map;

public class TGroupID {

  public static String NAME_GROUPID = "groupID";
  private String groupID;

  // To COMPLETE with Exception if string null specified

  public TGroupID(String id) {

    groupID = id;
  }

  public String toString() {

    return groupID;
  }

  public String getValue() {

    return groupID;
  }

  public void encode(Map param, String name) {

    param.put(name, groupID);
  }
};
