/**
 * @file   fs_errors.hpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface of exception classes in the @c fs namespace.
 */
/*
 * Copyright (c) 2005 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * as StoRM itself.
 */

#ifndef __FS_ERRORS_H
#define __FS_ERRORS_H

// --- #include files --- //

#include <string>
#include <exception>


namespace fs {

  // --- class interface --- //

  /** Base class for %fs_acl exceptions. */
  class error 
#ifndef SWIG // SWIG must map this to a Java exception
    : public std::exception 
#endif // SWIG
  {
  public:
    /** Constructor, taking error message as @c std::string parameter. */
    error(const std::string& reason) : errmsg(reason) { }

    /** Destructor.  Does nothing, but declared here because the @c
        std::exception one has a no-throw clause. */
    virtual ~error() throw() { }

    /** Return error message as <code>const char*</code>. */
    virtual const char* what() const throw() { return errmsg.c_str(); }

  protected:

    /** Default (no parameter) constructor, to be used only for subclassing. */
    error() { }

    /** Set error message (takes @c std::string parameter). */
    void set_message(const std::string& msg) { errmsg.assign(msg); }

  private:

    /** Stored error message. It's a @c std::string so that its
        constructor will make a copy of the passed parameters, and its
        destructor will free the resources being held when the
        exception is caught and dealt with. */
    std::string errmsg;
  };


  /** Thrown to signal a system call failure. */
  class system_error : public error {
  public:

    /** Constructor, taking error message as @c std::string parameter. */
    system_error(const std::string& msg, const int errnum=0);

    /** Destructor.  Does nothing, but declared here because the @c
        std::exception one has a no-throw clause. */
    virtual ~system_error() throw() { }

    /** Return error message as <code>const char*</code>. */
    virtual const char* what() const throw() { return errmsg.c_str(); }

    /** Returns error description, given the error code. */
    std::string error_message (const int errnum);

  private:

    /** System error code (@c errno) that originated this exception. */
    int errcode;

    /** Stored error message. It's a @c std::string so that its
        constructor will make a copy of the passed parameters, and its
        destructor will free the resources being held when the
        exception is caught and dealt with. */
    std::string errmsg;
  };


  /** Thrown when system calls to get ACL are not supported. */
  class acl_not_supported : public error {
  public:
    /** Default constructor; sets arror message to "ACLs not supported
        on filesystem". */
    acl_not_supported() : error("ACLs not supported on filesystem.") {}
    /** Constructor, taking error message. */
    acl_not_supported(const std::string& reason) : error(reason) {}
  };


  /** Thrown when the pathname is deemed invalid by system calls. */
  class invalid_path : public error {
  public:
    /** Constructor, taking error message. */
    invalid_path(const std::string& reason) : error(reason) {}
  };

  
  /** Thrown when the filesystem entry ACL cannot be read. */
  class permission_denied : public error {
  public:
    /** Constructor, taking error message. */
    permission_denied(const std::string& reason) : error(reason) {}
  };

  /** Thrown by descendants of fs::genericfs when the filesystem type
      does not match that of class. */
  class wrong_filesystem_type : public error {
  public:
    /** Constructor, taking error message. */
    wrong_filesystem_type(const std::string& reason) : error(reason) {}
  };

}; // namespace fs


#endif //  __FS_ERRORS_H
