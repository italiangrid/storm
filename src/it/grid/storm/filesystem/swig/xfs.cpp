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
 * @file   xfs.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Implementation of the xfs class.
 *
 */


/// CVS Revision info
static const char* const RCSID="$Id: xfs.cpp,v 1.3 2006/05/26 09:24:58 amessina Exp $";


#include "xfs.hpp"
#include "fs_errors.hpp"

#include <cassert>
#include <errno.h>
#include <fcntl.h>
#include <sstream>
#include <stdexcept>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/vfs.h>
//#include <xfs/libxfs.h>

#define XFS_BIG_INUMS   1
#define XFS_BIG_BLKNOS  1

#include <xfs/platform_defs.h>

#include <xfs/list.h>
#include <xfs/cache.h>
#include <xfs/bitops.h>
#include <xfs/kmem.h>
#include <xfs/swab.h>

#include <xfs/xfs_fs.h>
#include <xfs/xfs_types.h>
#include <xfs/xfs_arch.h>
#include <xfs/xfs_bit.h>
#include <xfs/xfs_inum.h>
#include <xfs/xfs_sb.h>
#include <xfs/xfs_ag.h>
#include <xfs/xfs_dir2.h>
#include <xfs/xfs_mount.h>
#include <xfs/xfs_da_btree.h>
#include <xfs/xfs_bmap_btree.h>
#include <xfs/xfs_alloc_btree.h>
#include <xfs/xfs_ialloc_btree.h>
#include <xfs/xfs_dir_sf.h>
#include <xfs/xfs_dir2_sf.h>
#include <xfs/xfs_attr_sf.h>
#include <xfs/xfs_dinode.h>
#include <xfs/xfs_inode.h>
#include <xfs/xfs_buf_item.h>
#include <xfs/xfs_inode_item.h>
#include <xfs/xfs_alloc.h>
#include <xfs/xfs_btree.h>
#include <xfs/xfs_btree_trace.h>
#include <xfs/xfs_bmap.h>



// --- exported functions --- //

/** Constructor, taking pathname of the filesystem mount point.
 * 
 * @throw fs::system_error if a system call fails;
 * fs::wrong_filesystem_type if given path does not point to a XFS
 * filesystem
 */
fs::xfs::xfs (const std::string& mntpath) 
  throw(fs::wrong_filesystem_type, fs::error)
  : posixfs(mntpath)
{
  // check if given mountpoint is xfs
  int rc = platform_test_xfs_path(mntpath.c_str());
  if (-1 == rc)
    {
      std::ostringstream msg;
      msg << "No XFS filesystem on '" << mntpath << "'";
      throw fs::wrong_filesystem_type(msg.str());
    }
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
fs::xfs::prealloc (const std::string& filename, 
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

  // prepare parameters to pass to xfsctl()
  xfs_flock64_t   fl;
  fl.l_whence = 0;
  fl.l_start = 0;
  fl.l_len = size;

  // reserve blocks
  int rc = xfsctl (filename.c_str(), fd, XFS_IOC_RESVSP64, &fl);
  if (-1 == rc)
    {
      int err = errno;
      std::ostringstream msg;
      msg << __FILE__ 
          << ": xfs_prealloc(" << filename << "," << size << ") failed";
      throw system_error(msg.str(), err);
    }

  close(fd);
}
