/**
 * @file   posixfs.hpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface for the fs::posixfs class.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * contained in the StoRM backend license file LICENSE.txt
 */
#ifndef __POSIXFS_HPP
#define __POSIXFS_HPP


#include "genericfs.hpp"
#include "posixfs_acl.hpp"
#include "fs_errors.hpp"

#include <string>
#include <stdexcept>
#include <sys/types.h>


namespace fs {

  /** Interface to POSIXFS-specific functions. */
  class posixfs : public fs::genericfs {
  public:
    /** Constructor, taking pathname of the filesystem mount point. */
      posixfs(const std::string& mntpath)
      throw(fs::acl_not_supported, fs::error);

    /** Return a new instance of an fs_acl (sub)class suitable for
        manipulating the ACLs on this filesystem. */
    virtual fs_acl *new_acl() const
      throw(fs::error);

  }; // class posixfs

  
}; // namespace fs



// --- inlined implementation --- //

/** Factory method: Return a new instance of a fs_acl (sub)class that
    is suitable for manipulating ACLs on this filesystem.  Actually,
    POSIXFS implements the Linux kernel POSIX ACL supoprt. */
inline fs_acl*
fs::posixfs::new_acl() const
  throw(fs::error)
{
  return new posixfs_acl();
}


#endif // #ifndef __POSIXFS_HPP
