/**
 * @file   gpfs23_acl.hpp
 *
 * Interface of the @c gpfs23_acl class.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 *
 * Documentation for this class and its methods is in the fs_acl.cpp
 * file.
 */

#ifndef __GPFS23_ACL_H
#define __GPFS23_ACL_H

#include "fs_errors.hpp"
#include "helper_command.hpp"
#include "path.hpp"
#include "posixfs_acl.hpp"

#include <string>
#include <stdexcept>
#include <sys/acl.h>
#include <vector>


/** Get and set ACLs on GPFS filesystem entries.  This is useful only
 * under Linux kernels =< 2.4; since 2.6, GPFS supports the standard
 * Linux @c getxattr()/setxattr() interface, so the POSIX @c libacl
 * functions should be used instead.
 * 
 * <p> Actual ACL getting/setting is done by forking and invoking the
 * GPFS @c mmgetacl and @c mmputacl commands.  Prefer the use of the
 * POSIX @c libacl class, if applicable.
 * 
 * Important: if the GPFS version 3.1 is used with Linux Kernels =< 2.4,
 * then prefer to use the implemetation in gpfs31_acl because it uses
 * the GPFS library functions to set/get ACLs (functions not available
 * with GPFS version 2.3).
 * 
 * @todo FIXME: GPFS can be configured to accept either NFSv4 ACLs or
 * POSIX-like ACLs, via the @c -k option to @c mmchfs; this class
 * assumes the filesystem is configured to use POSIX-like ACLs, and
 * does not perform any check that this is the actual setup.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
class gpfs23_acl : public posixfs_acl {

 public:
  /** Default constructor. */
  gpfs23_acl() : posixfs_acl(), default_acl(4) { }

  /** Reset objet to newly-constructed state, clearing the stored
      ACL. */
  virtual void clear();

  /** Load ACL from the specified filesystem entry (file or directory). */
  virtual void load(const std::string& path, 
                    const bool delete_permission_too = false)
    throw(fs::error, std::exception);

  /** Set ACL on the specified filesystem entry (file or directory). */
  virtual void enforce(const std::string& path) 
    const
    throw(fs::error, std::exception);

  /** Set explicit path to the @c mmgetacl command. */
  static bool set_mmgetacl(const std::string& path);

  /** Set explicit path to the @c mmputacl command. */
  static bool set_mmputacl(const std::string& path);


  /** Thrown by load_from_mmgetacl when input is malformed, insufficient,
      or cannot be read at all. */
  class bad_input : public fs::error {
  public:
    /** Constructor, taking error message */
    bad_input (const std::string& msg) : fs::error(msg) { }
    /** Destructor.  Does nothing, but declared here because the @c
        std::exception one has a no-throw clause. */
    ~bad_input() throw() { }
  protected:
    /** Default constructor, for subclassing setting the error message
        otherwise. */
    bad_input() : fs::error() { }
  };

#ifndef SWIG  // SWIG interface generator ought ignore this

  /** Convert a permission string in GPFS format ("rwxc") to an @c
      permission_t object. Only the first four characters of @a rwxc are
      ever inspected. */
  static permission_t  text_to_permission (const std::string& rwxc);

  /** Convert an @c permission_t object to the textual representation. */
  static const std::string& permission_to_text (const permission_t perm, 
                                                std::string& output);

 protected:

  /** Load ACL from the given stream.  The passed stream is assumed to
      be formatted exactly like the output of the @c mmgetacl command. */
  virtual void load_from_mmgetacl(std::istream& mmgetacl_stdout)
    throw(fs::error, std::exception);

  /** Dump ACL in a format suitable to be passed as input to GPFS' @c
      mmputacl command. */
  virtual void enforce_with_mmputacl(std::ostream& mmputacl_stdin) 
    const
    throw(fs::error, std::exception);

  /** Return a (read-only) iterator pointing at the start the default ACL. */
  std::vector<std::string>::const_iterator default_acl_begin() const
    { return default_acl.begin(); }

  /** Return a (read-only) iterator pointing at the end of the default ACL. */
  std::vector<std::string>::const_iterator default_acl_end() const
    { return default_acl.end(); }

  /** Make a new instance of the same class of this object.
   *
   * Needed in @c load_delete_permission() to instanciate the right
   * kind of object for classes inheriting from this one.
   */
  virtual fs_acl *new_same_class() const { return new gpfs23_acl(); }

 private:
  /** Path to the @c mmgetacl command. Defaults to searching a command
      named @c mmgetacl in the system PATH. */
  static std::string mmgetacl;

  /** Path to the @c mmputacl command. Defaults to searching a command
      named @c mmputacl in the system PATH. */
  static std::string mmputacl;

  /** Search path for #c mmgetacl / @c mmputacl commands. */
  static const fs::path PATH;

  /** List of "default" ACEs. Need to save those as @c mmputacl will
      overwrite @em both the access ACL and the default ACL. */
  mutable std::vector<std::string> default_acl;
#endif // #ifndef SWIG
};
#endif // #ifndef __GPFS_ACL_H
