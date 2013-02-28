// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
//
// posixapi_interface.i -- SWIG interface module for posixapi_interface.c
//
// Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
// for the EGRID/INFN joint project StoRM.
//
// You may copy, modify and distribute this code under the terms 
// in the StoRM LICENSE.txt file.
//
// $Id: posixapi_interface.i,v 1.16 2006/05/26 09:44:06 amessina Exp $
//
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------

%module posixapi_interface


// ---- use Java language constants ---- //
//
%javaconst(1); 



// ---- load native code in JNI class initialization ---- //
//
// This pragma will instruct SWIG code to load
// the native code library when the JNI-proxy class
// is loaded by the JVM.
//
%include load_native_lib_in_jniclass.i
LOAD_NATIVE_LIB_IN_JNICLASS(posixapi_interface)



// --- standard types mapping --- //

%include "types.i"


// --- exception handling --- //
// 
// This code catches the C++ exceptions and re-throws them
// as proper Java exceptions.
//
// See http://www.swig.org/Doc1.3/Java.html#exception_typemap
//

// include standard SWIG exception code
%include "exception.i"

// set base class for all exceptions thrown by this package
// (also look in fs_errors.hpp)
%typemap(javabase) fs::error                 "it.grid.storm.filesystem.FilesystemError";

// expected C++ method name to expected Java method name
%rename(getMessage) what;

// fs_errors: exception classes for this package;
// they will all inherit from fs::error, which is made into
// a Java excpetion by the commands above.
//
%{
#include "fs_errors.hpp"
%}
%include "fs_errors.hpp"


%typemap(throws, throws="it.grid.storm.filesystem.WrongFilesystemType") fs::wrong_filesystem_type {
  jclass excep = jenv->FindClass("it/grid/storm/filesystem/WrongFilesystemType");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}

%typemap(throws, throws="it.grid.storm.filesystem.AclNotSupported") fs::acl_not_supported {
  jclass excep = jenv->FindClass("it/grid/storm/filesystem/AclNotSupported");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}

%typemap(throws, throws="it.grid.storm.filesystem.InvalidPathException") fs::invalid_path {
  jclass excep = jenv->FindClass("it/grid/storm/filesystem/InvalidPathException");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}

%typemap(throws, throws="it.grid.storm.filesystem.InvalidPermissionOnFileException") fs::permission_denied {
  jclass excep = jenv->FindClass("it/grid/storm/filesystem/InvalidPathException");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}

%typemap(throws, throws="it.grid.storm.filesystem.FilesystemError") fs::error {
  jclass excep = jenv->FindClass("it/grid/storm/filesystem/FilesystemError");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}

%typemap(throws, throws="java.lang.RuntimeException") std::exception {
  jclass excep = jenv->FindClass("java/lang/RuntimeException");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}



// --- interface definition --- //
//
// This section contains the code for which a wrapping interface will
// be generated.
//

// all wrapped objects are read-only, from the Java point of view
%immutable;


// useful standard POSIX system calls from libc
//

%{
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
%}

int chmod(const char *path, mode_t mode);
int chown(const char *path, uid_t owner, gid_t group);
int creat(const char *pathname, mode_t mode);
int mkdir(const char *pathname, mode_t mode);
int setuid(uid_t uid);
int unlink(const char *pathname);

mode_t umask(mode_t mask);

uid_t geteuid(void);	
uid_t getuid(void);



// genericfs: interface for generic POSIX filesystem operations
//
%{
#include "genericfs.hpp"
%}
%include "genericfs.hpp"

// posixfs: interface for generic POSIX filesystem with ACL operations
//
%import "genericfs.hpp"
%{
#include "posixfs.hpp"
%}
%include "posixfs.hpp"


// fs_acl: abstract base class for ACL manipulation
//
// only the subset of the full interface that is needed
// for the Java part is defined here; notably, only the public
// part of the class needs to be exposed to SWIG.
//
%include "pass_vector_by_value_java.i"
JAVA_STD_VECTOR(gid_t, jint, Int, Int)

// wrap fs_acl::permission_flags as integer constants
%include "enumtypeunsafe.swg"

%{
#include "fs_acl.hpp"
%}
%include "fs_acl.hpp"


// posixfs_acl: class for ACL manipulation using POSIX libacl.so
//
%{
#include "posixfs_acl.hpp"
%}
%include "posixfs_acl.hpp"
