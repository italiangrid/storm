/**
 * @file   genericfs.hpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Interface to fs::genericfs class.
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * contained in the StoRM backend license file LICENSE.txt
 */
#ifndef __GENERICFS_H
#define __GENERICFS_H

typedef unsigned long alloc_size_t;

#include "fs_errors.hpp"
#include "posixfs_acl.hpp"

#include <string>
#include <stdexcept>
#include <sys/types.h>


namespace fs {

  /** Encapsulates common filesystem operations on a generic (POSIX)
      filesystem. */
  class genericfs {
  public:
    /** Constructor, taking path to the filesystem mount point. */
    genericfs(const std::string& mountpoint) 
      throw(fs::acl_not_supported, fs::error);

    /** Destructor. */
    virtual ~genericfs();

    /** Return size of the available space on this filesystem (in
        bytes). */
    virtual size_t get_free_space () throw(fs::error);
  
    /** Return the named file size in bytes; cached information may be
        returned, which can be not up-to-date. */
    virtual size_t get_size (const std::string& filename) 
      throw(fs::error, std::logic_error);
  
    /** Return the last modification time (as UNIX epoch value) of the
        given file or directory; cached information may be returned, which
        can be not up-to-date. */
    virtual time_t get_last_modification_time (const std::string& pathname) 
      throw(fs::error);
  
    /** Return the named file size in bytes; up-to-date information is
        returned, at the cost of forcing a cluster-wide flushing of
        metadata. */
    virtual size_t get_exact_size (const std::string& filename) 
      throw(fs::error, std::logic_error);
  
    /** Return the last modification time (as UNIX epoch value) of the
        passed file or directory; up-to-date information is returned, at
        the cost of forcing a cluster-wide flushing of metadata. */
    virtual time_t get_exact_last_modification_time (const std::string& pathname) 
      throw(fs::error);

	/** Truncate the specified file to the desired size in bytes.
        On success, zero is returned. On error, an exception is thrown.  */
    virtual int truncate_file(const std::string&  filename, size_t desired_size)
      throw(fs::error);
     
    /** Return a new instance of an %fs_acl subclass suitable for
        manipulating the ACLs on this filesystem. */
    virtual fs_acl *new_acl() const
      throw(fs::error) = 0;

  private:
    /** Path to the mount point of this filesystem. */
    std::string mountpoint; 
  };

};  // namespace fs



// --- inlined implementation --- //

inline
fs::genericfs::~genericfs() {
  // no resources to release
}

/**
 * Return the (guaranteed up-to-date) named file size in bytes.  If
 * the passed path points to a directory, an exception is thrown.
 *
 * This is the same as get_size() in a generic filesystem, but it may
 * trigger a metadata update on filesystem (GPFS, for instance) that
 * perform metedata caching.
 *
 * @param filename  Pathname of the file whose size is to be returned.
 *
 * @return Size of the file, in bytes.  If @c path points to a
 * directory, then the result is undefined.
 *
 * @throw fs::error, if a system call fails; std::logic_error if
 * argument is not a regular file.
 */
inline size_t 
fs::genericfs::get_exact_size (const std::string& filename) 
     throw(fs::error, std::logic_error)
{
  // no metadata caching for the generic filesystem
  return get_size(filename); 
}

/**
 * Return the (guaranteed up-to-date) last modification time (as UNIX
 * epoch value) of the passed file or directory.
 *
 * This is the same as get_last_modification_time() in a generic
 * filesystem, but it may trigger a metadata update on filesystem
 * (GPFS, for instance) that perform metedata caching.
 *
 * @param pathname Pathname of the file or directory whose last
 *                 modification time is to be returned.
 *
 * @return         Last modification time (as UNIX epoch value).
 *
 * @throw  fs::error, if a system call fails.
 */
inline time_t 
fs::genericfs::get_exact_last_modification_time(const std::string& pathname)
     throw(fs::error)
{
  // no metadata caching for the generic filesystem
  return get_last_modification_time(pathname);
}

#endif // #ifndef __GENERICFS_H
