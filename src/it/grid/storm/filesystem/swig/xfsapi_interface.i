// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
//
// xfsapi_interface.i -- SWIG interface module for StoRM XFS module
//
// Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
// for the EGRID/INFN joint project StoRM.
//
// You may copy, modify and distribute this code under the terms 
// in the StoRM LICENSE.txt file.
//
// $Id: xfsapi_interface.i,v 1.2 2006/11/10 13:26:44 lmagnoni Exp $
//
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------

%module xfsapi_interface


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
LOAD_NATIVE_LIB_IN_JNICLASS(xfsapi_interface)



// --- standard types mapping --- //

%include "types.i"



// --- exception handling --- //

// include standard SWIG exception code
%include "exception.i"

// fs_errors: exception classes 
//
%{
#include "fs_errors.hpp"
%}
%import "fs_errors.hpp"



// --- interface definition --- //
//
// This section contains the code for which a wrapping interface will
// be generated.
//

// all wrapped objects are read-only, from the Java point of view
%immutable;

       
// XFS uses POSIX ACLs; import definition of 
// posixfs_acl class and its ancestors.
//
%import "fs_acl.hpp"
%import "posixfs_acl.hpp"


// xfs.hpp: interface for XFS operations
//
%import "genericfs.hpp"
%import "posixfs.hpp"
%{
#include "xfs.hpp"
%}
%include "xfs.hpp"
