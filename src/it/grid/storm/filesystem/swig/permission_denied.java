/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.29
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package it.grid.storm.filesystem.swig;

public class permission_denied extends error {
  private long swigCPtr;

  protected permission_denied(long cPtr, boolean cMemoryOwn) {
    super(posixapi_interfaceJNI.SWIGpermission_deniedUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(permission_denied obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      posixapi_interfaceJNI.delete_permission_denied(swigCPtr);
    }
    swigCPtr = 0;
    super.delete();
  }

  public permission_denied(String reason) {
    this(posixapi_interfaceJNI.new_permission_denied(reason), true);
  }

}
