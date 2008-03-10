// -----------------------------------------------------------------------
// -----------------------------------------------------------------------
//
// lcmaps_interface.i -- SWIG interface module for lcmaps_interface.c
//
// Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
// for the EGRID/INFN joint project StoRM.
//
// You may copy, modify and distribute this code under the terms of
// the INFN GRID licence.
//
// $Id: lcmaps_interface.i,v 1.12 2007/05/16 09:50:04 lmagnoni Exp $
//
// -----------------------------------------------------------------------
// -----------------------------------------------------------------------

%module lcmaps_interface


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
LOAD_NATIVE_LIB_IN_JNICLASS(lcmaps_interface)


// --- additional include files for the generated C code --- //
//
// This section is copied verbatim into the generated C wrapper code;
// it should include all declarations that are needed in the C code
// compilation, and it should match the part that is repeated below in
// the SWIG interface directives.
//
%{
#include "lcmaps_interface.h"
#include <exception> // for catching constructor exceptions
#include <cassert>   // robust exception throwing code
%}


// --- interface definition: lcmaps_interface.h --- //
//
// This section contains the code for which a wrapping interface will
// be generated.
//

// uid_t, gid_t and size_t are in sys/types.h,
// but <sys/types.h> cannot be parsed because of
// gcc-specific constructs...
typedef int uid_t;
typedef int gid_t;
typedef int size_t;

// map 'char **fqan_list' and 'char **vo_list' to a Java String[]
//
// The following include files were extracted from the ``various.i``
// library file in SWIG 1.3.24; the same library file from 1.3.19 has
// different names; at some point in time, they should be removed from
// here...
//

%include "string_array.i"; // map ``char**`` to Java ``String[]``
%apply char **STRING_ARRAY { char **fqan_list};



// map 'gid_t *gid_list' to a Java int[]
// 
// This turned out to be trickier than String[]:
//
// 1. swig-1.3.19 does not have support for arrays of unspecified
//    size in ``arrays_java.i``, so I got the ``arrays_java.1.3.24.i``
//    from swig-1.3.24
// 2. even that will not compile out-of-the-box: compilation spits out
//    an error message - a line needs to be replaced; we do that
//    by adding the ``%typemap`` directive below, which is a copy
//    of the original SWIG-produce code, with the placeholder value
//    substituted with the actual one.  BEWARE!! If the type changes,
//    then the typemap code should change accordingly.
// 3. for some reason, the C++ compiler won't cast a (gid_t*)
//    to an (int*); so, we force the cast ...
//
// BEWARE!! it is not a true Java array, so no referencing
// int[].length, no bounds checking, etc... (This notice was taken from
// the SWIG docs... I don't think this is any longer valid, as we
// are putting in the correct size. -- RM)
//
%include "arrays_java.1.3.24.i"
%typemap(out) int[] %{
	/* next line comes from lcmaps_interface.i */
	$result = SWIG_JavaArrayOutInt(jenv, (int*)$1, (size_t) ((arg1)->ngids)); 
%}
%apply int[] { gid_t *gids }

// catch exceptions throw by the localuser_info ctor,
// and rethrow them as Java exceptions
//
//%javaexception("it.grid.storm.griduser.CannotMapUserException") {
%exception localuser_info::localuser_info %{
  try {
     $action
  } 
  catch (std::exception& x) {
	// find corresponding Java excpetion class
    jclass clazz = 
		jenv->FindClass("it/grid/storm/griduser/CannotMapUserException");
	// fail miserably if to-throw Java exception class cannot be found
	assert (NULL != clazz); 
	// throw excpetion into JVM
    jenv->ThrowNew(clazz, x.what());
    return $null;
   }
%}

// all wrapped objects are read-only, from the Java point of view
%immutable;

// C++ interface
%include lcmaps_interface.h
