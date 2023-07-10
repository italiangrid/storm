/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/**
 * This class represents the TGroupPermission in Srm request.
 *
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.Map;

public class TGroupPermission {

  private TGroupID groupID;
  private TPermissionMode permMode;

  public static String PNAME_GROUPPERMISSION = "groupPermission";

  public TGroupPermission(TGroupID groupID, TPermissionMode permMode) {

    this.groupID = groupID;
    this.permMode = permMode;
  }

  public TGroupID getGroupID() {

    return groupID;
  }

  public TPermissionMode getPermissionMode() {

    return permMode;
  }

  public static TGroupPermission makeDirectoryDefault() {

    return new TGroupPermission(new TGroupID("undef"), TPermissionMode.NONE);
  }

  public static TGroupPermission makeFileDefault() {

    return new TGroupPermission(new TGroupID("undef"), TPermissionMode.NONE);
  }

  /**
   * Encode method use to provide a represnetation of this object into a structures paramter for
   * communication to FE component.
   *
   * @param param
   * @param name
   */
  public void encode(Map param, String name) {

    Map paramStructure = new HashMap();
    if ((groupID != null) && (permMode != null)) {
      groupID.encode(paramStructure, TGroupID.NAME_GROUPID);
      permMode.encode(paramStructure, TPermissionMode.PNAME_MODE);
      param.put(name, paramStructure);
    }
  }
}
