/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.24
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package it.grid.storm.filesystem.swig;

public class gpfs extends genericfs {
  private long swigCPtr;

  protected gpfs(long cPtr, boolean cMemoryOwn) {
    super(gpfsapi_interfaceJNI.SWIGgpfsUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(gpfs obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected gpfs() {
    this(0, false);
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      gpfsapi_interfaceJNI.delete_gpfs(swigCPtr);
    }
    swigCPtr = 0;
    super.delete();
  }

  public gpfs(String mntpath) {
    this(gpfsapi_interfaceJNI.new_gpfs(mntpath), true);
  }

  public void prealloc(String filename, long size) {
    gpfsapi_interfaceJNI.gpfs_prealloc(swigCPtr, filename, size);
  }

  public long get_exact_size(String filename) {
    return gpfsapi_interfaceJNI.gpfs_get_exact_size(swigCPtr, filename);
  }

  public long get_exact_last_modification_time(String pathname) {
    return gpfsapi_interfaceJNI.gpfs_get_exact_last_modification_time(swigCPtr, pathname);
  }

  public int truncate_file(String filename, long desired_size) {
    return gpfsapi_interfaceJNI.gpfs_truncate_file(swigCPtr, filename, desired_size);
  }

  public fs_acl new_acl() {
    long cPtr = gpfsapi_interfaceJNI.gpfs_new_acl(swigCPtr);
    return (cPtr == 0) ? null : new fs_acl(cPtr, false);
  }

}
