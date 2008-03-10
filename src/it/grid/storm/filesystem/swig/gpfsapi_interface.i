// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
//
// gpfsapi_interface.i -- SWIG interface module for StoRM GPFS module
//
// Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
// for the EGRID/INFN joint project StoRM.
//
// You may copy, modify and distribute this code under the terms 
// in the StoRM LICENSE.txt file.
//
// $Id: gpfsapi_interface.i,v 1.8 2007/04/13 14:08:42 aforti Exp $
//
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------

%module gpfsapi_interface


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
LOAD_NATIVE_LIB_IN_JNICLASS(gpfsapi_interface)



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

       
// gpfs.hpp: interface for GPFS operations
// (GPFS version 3.1).
//
%import "genericfs.hpp"
%{
#include "gpfs.hpp"
%}
%include "gpfs.hpp"

// gpfs31_acl: class for ACL manipulation using GPFS'
// library functions (GPFS v3.1).

// parent classes need to be known to SWIG
%import "fs_acl.hpp"
%import "posixfs_acl.hpp"

%{
#include "gpfs31_acl.hpp"
%}
%include "gpfs31_acl.hpp"


// gpfs23.hpp: interface for GPFS operations
// (GPFS version 2.3).
//
%import "genericfs.hpp"
%{
#include "gpfs23.hpp"
%}
%include "gpfs23.hpp"

// gpfs23_acl: class for ACL manipulation using GPFS' 
// "mmgetacl" and "mmputacl" commands
//
// only the subset of the full interface that is needed
// for the Java part is defined here; notably, only the public
// part of the class needs to be exposed to SWIG.
//

// parent classes need to be known to SWIG
%import "fs_acl.hpp"
%import "posixfs_acl.hpp"

%{
#include "gpfs23_acl.hpp"
%}
%include "gpfs23_acl.hpp"

