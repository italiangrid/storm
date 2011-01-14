/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package it.grid.storm.filesystem.swig;

public class gpfs31_acl extends posixfs_acl {
  private long swigCPtr;

  protected gpfs31_acl(long cPtr, boolean cMemoryOwn) {
    super(gpfsapi_interfaceJNI.SWIGgpfs31_aclUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(gpfs31_acl obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      gpfsapi_interfaceJNI.delete_gpfs31_acl(swigCPtr);
    }
    swigCPtr = 0;
    super.delete();
  }

  public gpfs31_acl() {
    this(gpfsapi_interfaceJNI.new_gpfs31_acl(), true);
  }

  public void load(String path, boolean delete_permission_too) {
    gpfsapi_interfaceJNI.gpfs31_acl_load__SWIG_0(swigCPtr, path, delete_permission_too);
  }

  public void load(String path) {
    gpfsapi_interfaceJNI.gpfs31_acl_load__SWIG_1(swigCPtr, path);
  }

  public void enforce(String path) {
    gpfsapi_interfaceJNI.gpfs31_acl_enforce(swigCPtr, path);
  }

}
