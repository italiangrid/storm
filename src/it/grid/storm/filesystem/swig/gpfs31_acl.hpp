/**
 * @file   gpfs31_acl.hpp
 *
 * Interface of the @c gpfs31_acl class.
 *
 * @author Alberto Forti <alberto.forti@cnaf.infn.it>
 * @version $Revision: 1.1 $
 */
/*
 * Copyright (c) 2007 Alberto Forti <alberto.forti@cnaf.infn.it>
 * for the INFN-CNAF/EGRID joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 *
 * Documentation for this class and its methods is in the fs_acl.cpp
 * file.
 */
#ifndef GPFS31_ACL_HPP_
#define GPFS31_ACL_HPP_

#include "fs_errors.hpp"
#include "posixfs_acl.hpp"

#include <gpfs.h>
#include <string>
#include <stdexcept>

/** Get and set ACLs on GPFS filesystem entries.  This is useful only
 * under Linux kernels =< 2.4 and GPFS version 3.1; since 2.6, GPFS
 * supports the standard Linux @c getxattr()/setxattr() interface,
 * so the POSIX @c libacl functions should be used instead.
 * 
 * <p> Actual ACL getting/setting is done by invoking the GPFS library
 * functions available only with GPFS version 3.1. Prefer the use of the
 * POSIX @c libacl class, if applicable (kernel 2.6).
 * 
 * @todo FIXME: GPFS can be configured to accept either NFSv4 ACLs or
 * POSIX-like ACLs; this class assumes the filesystem is configured
 * to use POSIX-like ACLs, and does not perform any check that
 * this is the actual setup.
 *
 * @author Alberto Forti <alberto.forti@cnaf.infn.it>
 */
class gpfs31_acl : public posixfs_acl {

 public:
  /** Default constructor. */
  gpfs31_acl() : posixfs_acl() { }

  /** Load ACL from the specified filesystem entry (file or directory). */
  virtual void load(const std::string& path, 
                    const bool delete_permission_too = false)
    throw (fs::error, fs::invalid_path, 
           fs::permission_denied, fs::acl_not_supported, 
           std::exception);

  /** Set ACL on the specified filesystem entry (file or directory). */
  virtual void enforce(const std::string& path) 
    const
    throw (fs::error, fs::invalid_path, 
           fs::permission_denied, fs::acl_not_supported, 
           std::exception);

#ifndef SWIG  // SWIG interface generator ought ignore this

  /** Convert a permission bitfield from gpfs_acePerm_t to fs_acl::permission_t
   *  format.
   */
  static permission_t gpfs_perm_to_permission_t(gpfs_acePerm_t perm);
  
  /** Convert a permission bitfield from fs_acl::permission_t to gpfs_acePerm_t
      format. 
   */
  static gpfs_acePerm_t permission_t_to_gpfs_perm(const fs_acl::permission_t perm);
  
 protected:
 
  /** Load the @c DELETE permissions, inspecting the parent directory
      ACL. */
  virtual void load_delete_permission(const std::string& pathname);

  /** Make a new instance of the same class of this object.
   *
   * Needed in @c load_delete_permission() to instanciate the right
   * kind of object for classes inheriting from this one.
   */
  virtual fs_acl* new_same_class() const;
  
#endif // #ifndef SWIG

};

#endif /*GPFS31_ACL_HPP_*/
