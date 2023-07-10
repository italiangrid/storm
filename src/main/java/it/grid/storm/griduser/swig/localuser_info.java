/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). SPDX-License-Identifier: Apache-2.0
 */
/*
 * ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org). Version
 * 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * -----------------------------------------------------------------------------
 */

package it.grid.storm.griduser.swig;

public class localuser_info {

  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected localuser_info(long cPtr, boolean cMemoryOwn) {

    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(localuser_info obj) {

    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected localuser_info() {

    this(0, false);
  }

  protected void finalize() {

    delete();
  }

  public void delete() {

    if (swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      lcmaps_interfaceJNI.delete_localuser_info(swigCPtr);
    }
    swigCPtr = 0;
  }

  public int getUid() {

    return lcmaps_interfaceJNI.get_localuser_info_uid(swigCPtr);
  }

  public int[] getGids() {

    return lcmaps_interfaceJNI.get_localuser_info_gids(swigCPtr);
  }

  public long getNgids() {

    return lcmaps_interfaceJNI.get_localuser_info_ngids(swigCPtr);
  }

  public localuser_info(String user_dn, String[] fqan_list) {

    this(lcmaps_interfaceJNI.new_localuser_info(user_dn, fqan_list), true);
  }
}
