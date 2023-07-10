/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
package it.grid.storm.srm.types;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the TUserPermission in Srm request.
 *
 * @author Magnoni Luca
 * @author CNAF - INFN Bologna
 * @date Avril, 2005
 * @version 1.0
 */
public class TUserPermission {

  private TUserID userID;
  private TPermissionMode permissionMode;

  public static String PNAME_OWNERPERMISSION = "ownerPermission";

  public TUserPermission(TUserID userID, TPermissionMode permMode) {

    this.userID = userID;
    this.permissionMode = permMode;
  }

  public static TUserPermission makeEmpty() {

    return new TUserPermission(TUserID.makeEmpty(), TPermissionMode.NONE);
  }

  public TUserID getUserID() {

    return userID;
  }

  public TPermissionMode getPermissionMode() {

    return permissionMode;
  }

  public static TUserPermission makeDirectoryDefault() {

    return new TUserPermission(TUserID.makeEmpty(), TPermissionMode.X);
  }

  public static TUserPermission makeFileDefault() {

    return new TUserPermission(TUserID.makeEmpty(), TPermissionMode.R);
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
    if ((userID != null) && (permissionMode != null)) {
      userID.encode(paramStructure, TUserID.PNAME_USERID);
      permissionMode.encode(paramStructure, TPermissionMode.PNAME_MODE);
      param.put(name, paramStructure);
    }
  }
}
