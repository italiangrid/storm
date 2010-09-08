/**

  @file load_native_lib_in_jniclass.i
  @author Riccardo Murri <riccardo.murri@ictp.it>
    
  A SWIG macro to put a module's native code loading statement 
  in the module JNI class.  Uses Log4J to log failures; tries
  to give informational and diagnostic errors.

  <p> Usage: (substitute @a libname with your native code library
  name!)
  <pre>

     %include load_native_lib_in_jniclass.i
     LOAD_NATIVE_LIB_IN_JNICLASS(libname)
  
   </pre>

   <p> This is a more elaborate version of an example found in the
   SWIG users' manual.

**/
%define LOAD_NATIVE_LIB_IN_JNICLASS(LIBNAME)
// ---- load native code in JNI class initialization ---- //
//
// This pragma will instruct SWIG code to load
// the native code library when the JNI-proxy class
// is loaded by the JVM.
//
%pragma(java) jniclasscode=%{
  static {
    try {
      System.loadLibrary(#LIBNAME);
    } catch (UnsatisfiedLinkError e) {
	  final String libfile 
			= "'" + System.mapLibraryName(#LIBNAME) + "'";
	  final org.slf4j.Logger log 
			= org.slf4j.LoggerFactory.getLogger(LIBNAME##JNI.class);
      log.error("Native code library " + libfile
				 + " failed to load: " + e.getMessage());
	  // give some informational details, may be useful when debugging
	  final String ldpath = System.getProperty("java.library.path");
	  if (null != ldpath) 
	    log.info("Java VM searched for " + libfile 
            + "in java.library.path: " + ldpath);
   	  else
		log.info("Java VM library search path is null!");
      log.info("Add the library location to the environment variable LD_LIBRARY_PATH or to the Java property java.library.path");
	  throw new UnsatisfiedLinkError("Native code library " + libfile
				 + " failed to load: " + e.getMessage());
    }
  }
%}
%enddef
