/**
 * @file   xfs.hpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface for the fs::xfs class.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * contained in the StoRM backend license file LICENSE.txt
 */
#ifndef __XFS_HPP
#define __XFS_HPP


#include "posixfs.hpp"
#include "posixfs_acl.hpp"
#include "fs_errors.hpp"

#include <string>
#include <stdexcept>
#include <sys/types.h>


namespace fs {

  /** Interface to XFS-specific functions. */
  class xfs : public fs::posixfs {
  public:
    /** Constructor, taking pathname of the filesystem mount point. */
    xfs(const std::string& mntpath)
      throw(fs::wrong_filesystem_type, fs::error);

    /** Preallocate filesystem blocks on a inode.  The file is created, if
        it does not exist. */
    virtual void prealloc (const std::string& filename, const alloc_size_t size)
      throw(fs::error);

    /** Return a new instance of an fs_acl (sub)class suitable for
        manipulating the ACLs on this filesystem. */
    virtual fs_acl *new_acl() const
      throw(fs::error);

  }; // class xfs

  
}; // namespace fs



// --- inlined implementation --- //

/** Factory method: Return a new instance of a fs_acl (sub)class that
    is suitable for manipulating ACLs on this filesystem.  Actually,
    XFS implements the Linux kernel POSIX ACL supoprt. */
inline fs_acl *
fs::xfs::new_acl() const
  throw(fs::error)
{
  return new posixfs_acl();
}


#endif // #ifndef __XFS_HPP
