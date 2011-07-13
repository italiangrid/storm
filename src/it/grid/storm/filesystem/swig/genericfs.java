/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.29
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package it.grid.storm.filesystem.swig;

public class genericfs {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected genericfs(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(genericfs obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      posixapi_interfaceJNI.delete_genericfs(swigCPtr);
    }
    swigCPtr = 0;
  }

  public long get_free_space() throws it.grid.storm.filesystem.FilesystemError {
    return posixapi_interfaceJNI.genericfs_get_free_space(swigCPtr);
  }

  public long get_size(String filename) throws it.grid.storm.filesystem.FilesystemError {
    return posixapi_interfaceJNI.genericfs_get_size(swigCPtr, filename);
  }

  public long get_last_modification_time(String pathname) throws it.grid.storm.filesystem.FilesystemError {
    return posixapi_interfaceJNI.genericfs_get_last_modification_time(swigCPtr, pathname);
  }

  public long get_exact_size(String filename) throws it.grid.storm.filesystem.FilesystemError {
    return posixapi_interfaceJNI.genericfs_get_exact_size(swigCPtr, filename);
  }

  public long get_exact_last_modification_time(String pathname) throws it.grid.storm.filesystem.FilesystemError {
    return posixapi_interfaceJNI.genericfs_get_exact_last_modification_time(swigCPtr, pathname);
  }

  public int truncate_file(String filename, long desired_size) throws it.grid.storm.filesystem.FilesystemError {
    return posixapi_interfaceJNI.genericfs_truncate_file(swigCPtr, filename, desired_size);
  }

  public fs_acl new_acl() throws it.grid.storm.filesystem.FilesystemError {
    long cPtr = posixapi_interfaceJNI.genericfs_new_acl(swigCPtr);
    return (cPtr == 0) ? null : new fs_acl(cPtr, false);
  }

}
