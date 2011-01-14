/**
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @file   gpfs23.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Implementation of the gpfs23 class (driver for GPFS version 2.3).
 *
 */


/// CVS Revision info
static const char* const RCSID="$Id: gpfs23.cpp,v 1.3 2007/04/13 14:07:15 aforti Exp $";

#include "gpfs23.hpp"
#include "fs_errors.hpp"

#include <cassert>
#include <errno.h>
#include <fcntl.h>
#include <gpfs.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/vfs.h>
#include <sstream>
#include <stdexcept>

#include <iostream>

// --- module local functions --- //

/** Wrapper around gpfs_stat() call from libgpfs; throws a
 *  %system_error exception to signal an error condition.
 *
 * @param pathname pathname of the file or directory to stat
 *
 * @param output a @c{struct stat64_t} whose fields are to be filled
 * with information on @a pathname
 *
 * @return exact stat information on GPFS filesystem entry @a pathname
 *
 * @throw %system_error containing error code of failed @c gpfs_stat
 * call.
 */
static void
xgpfs_stat(const std::string& pathname, 
           stat64_t& output)
  throw(fs::error)
{
  int rc = gpfs_stat(const_cast<char*>(pathname.c_str()), &output);
  if (-1 == rc)
    {
      std::ostringstream msg;
      msg << __FILE__ 
          << ": gpfs_stat(" << pathname << ") failed";
      throw fs::system_error(msg.str(), errno);
    }
}



// --- exported functions --- //

/** Constructor, taking pathname of the filesystem mount point.
 * 
 * @throw fs::system_error if a system call fails;
 * fs::wrong_filesystem_type if given path does not point to a GPFS
 * filesystem
 */
fs::gpfs23::gpfs23(const std::string& mntpath) 
  throw(fs::wrong_filesystem_type, fs::error)
  : genericfs(mntpath)
{
  // check if given mountpoint is gpfs, by calling 'gpfs_stat'
  // on it, and checking if we get an error in return
  stat64_t st;
  int rc = gpfs_stat(const_cast<char*>(mntpath.c_str()), &st);
  if (-1 == rc) {
    if (EINVAL == errno) {
      std::ostringstream msg;
      msg << "No GPFS filesystem on '" << mntpath << "'";
      throw fs::wrong_filesystem_type(msg.str());
    } else { // ENOSYS, ENOENT, EPERM
      int err = errno;
      std::ostringstream msg;
      msg << __FILE__ 
          << ": gpfs_stat(" << mntpath << ") failed";
      throw system_error(msg.str(), err);
    }
  } else if (0 != rc) {
    // should not happen!
    assert (-1 == rc || 0 == rc);
  }
  std::cout << "Using GPFS v2.3 driver for: " << mntpath << std::endl;
}


/** Preallocate filesystem blocks on a inode.  The file is created, if
 * it does not exist.
 *
 * The @a size parameter indicates the number of bytes to allocate,
 * from the @em start of the file; if a file has non-zero size, then
 * only (size - actual length of file) bytes are effectively
 * preallocated.
 *
 * @param  filename  Pathname to the inode.
 * @param  size      Number of bytes to preallocate.
 *
 * @throw  fs::error, if a system call fails.
 */
void
fs::gpfs23::prealloc(const std::string& filename, 
                     const alloc_size_t size) 
  throw(fs::error)
{
  int fd = open (filename.c_str(), 
                 O_WRONLY|O_CREAT|O_LARGEFILE, 
                 S_IRUSR|S_IWUSR);
  if (-1 == fd)
    {
      int err = errno;
      std::ostringstream msg;
      msg << __FILE__ 
          << ": open(" << filename 
          << ",O_WRONLY|O_CREAT|O_LARGEFILE,S_IRUSR|S_IWUSR) failed";
      throw system_error(msg.str(), err);
    }

  int rc = gpfs_prealloc (fd, (gpfs_off64_t)0, size);
  if (-1 == rc)
    {
      int err = errno;
      std::ostringstream msg;
      msg << __FILE__ 
          << ": gpfs_prealloc(" << filename << "," << size << ") failed";
      close(fd);
     throw system_error(msg.str(), err);
    }

  close(fd);
}


/**
 * Return the named file size in bytes.  If the passed path points not
 * to a regular file, then throws a @c std::logic_error exception.
 *
 * <em>Note:</em> As opposed to the <code>get_size()</code> in
 * <code>posixapi_interface</code>, this function is guaranteed to
 * return accurate data, at the cost of forcing an update of the
 * metadata in the cluster, so it may incur in a sensible performance
 * penalty.
 *
 * @param  filename  Pathname of the file whose size is to be returned.
 *
 * @return           Size of the file, or negative value if some error 
 *                   occurred.
 *
 * @throw fs::error, if a system call fails; std::logic_error if
 * argument is not a regular file.
 */
size_t
fs::gpfs23::get_exact_size(const std::string& filename)
  throw(fs::error, std::logic_error)
{
  stat64_t st;
  xgpfs_stat (filename, st); /* dereference symlinks */
  
  // when debugging, stop at this point 
  // if called with a directory name
  assert(S_ISREG(st.st_mode));
  
  if (S_ISREG(st.st_mode)) 
    return st.st_size;
  else
    {
      // should not be called with a directory name,
      // throw @c std::logic_error
      std::ostringstream msg;
      msg << "gpfs_get_size(" << filename 
          << "): argument is a directory;" 
        "cannot return size of a directory.";
      throw std::logic_error(msg.str());
    }
}


/**
 * Return the last modification time (as UNIX epoch value) of the
 * passed file or directory.
 *
 * <em>Note:</em> As opposed to the <code>get_size()</code> in
 * <code>posixapi_interface</code>, this function is guaranteed to
 * return accurate data, at the cost of forcing an update of the
 * metadata in the cluster, so it may incur in a sensible performance
 * penalty.
 *
 * @param pathname Pathname of the file or directory whose last
 *                 modification time is to be returned.
 *
 * @return         Last modification time (as UNIX epoch value) or
 *                 negative value if some error occured.
 *
 * @throw  fs::error, if a system call fails.
 */
time_t
fs::gpfs23::get_exact_last_modification_time(const std::string& pathname) 
  throw(fs::error)
{
  stat64_t st;
  xgpfs_stat(pathname, st); /* dereference symlinks */
  return st.st_mtime;
}
