/**
 * @file   gpfs23.hpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface for the fs::gpfs23 class.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * contained in the StoRM backend license file LICENSE.txt
 *
 * Documentation for these functions is in the gpfsapi_interface.cpp
 * file.
 */
#ifndef GPFS23_HPP_
#define GPFS23_HPP_

#include "genericfs.hpp"
#include "gpfs23_acl.hpp"
#include "fs_errors.hpp"

#include <string>
#include <stdexcept>
#include <sys/types.h>

namespace fs {

  /** Interface to GPFS-specific functions. */
  class gpfs23 : public fs::genericfs {
  public:
    /** Constructor, taking pathname of the filesystem mount point. */
    gpfs23(const std::string& mntpath)
      throw(fs::wrong_filesystem_type, fs::error);

    /** Preallocate filesystem blocks on a inode.  The file is created, if
        it does not exist. */
    virtual void prealloc(const std::string& filename, const alloc_size_t size)
      throw(fs::error);

    /** Return the named file size in bytes; up-to-date information is
        returned, at the cost of forcing a cluster-wide flushing of
        metadata. */
    virtual size_t get_exact_size(const std::string& filename)
      throw(fs::error, std::logic_error);
    
    /** Return the last modification time (as UNIX epoch value) of the
        passed file or directory; up-to-date information is returned, at
        the cost of forcing a cluster-wide flushing of metadata. */
    virtual time_t get_exact_last_modification_time(const std::string& pathname)
      throw(fs::error);

    /** Return a new instance of an %fs_acl subclass suitable for
        manipulating the ACLs on this filesystem. */
    virtual fs_acl * new_acl() const
      throw(fs::error);

  }; // class gpfs23
 
}; // namespace fs

// --- inlined implementation --- //

/** Factory method: Return a new instance of an fs::gpfs23_acl class
    that is suitable for manipulating ACLs on this filesystem. */
inline fs_acl*
fs::gpfs23::new_acl() const
  throw(fs::error)
{
  return new gpfs23_acl();
}

#endif /*GPFS23_HPP_*/
