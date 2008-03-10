/**
 * @file   posixfs.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Implementation of the posixfs class.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * contained in the StoRM backend license file LICENSE.txt
 */

/// CVS Revision info
static const char* const RCSID="$Id: posixfs.cpp,v 1.1 2006/05/26 09:24:58 amessina Exp $";


#include "posixfs.hpp"
#include "fs_errors.hpp"

#include <cassert>
#include <errno.h>
#include <fcntl.h>
#include <sstream>
#include <stdexcept>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/vfs.h>



// --- exported functions --- //

/** Constructor, taking pathname of the filesystem mount point.
 * 
 * @throw fs::system_error if a system call fails;
 * fs::wrong_filesystem_type if given path does not point to a POSIXFS
 * filesystem
 */
fs::posixfs::posixfs (const std::string& mntpath) 
  throw(fs::acl_not_supported, fs::error)
  : genericfs(mntpath)
{
  acl_t acl;

  acl = acl_get_file (mntpath.c_str(), ACL_TYPE_ACCESS);
  if (NULL == acl) 
    if (ENOTSUP == errno || ENOSYS == errno)
      /* no ACL support, either in kernel or on filesystem */
      {
        std::ostringstream msg;
        msg << "POSIX ACL not supported on filesystem '"
            << mntpath << "'";
        throw fs::acl_not_supported(msg.str());
      }
    else
      /* some other error occurred */
      {
        int err = errno;
        std::ostringstream msg;
        msg << __FILE__
            << "acl_get_file(" << mntpath 
            << ",ACL_TYPE_ACCESS) failed";
        throw system_error(msg.str(), err);
      }
  
  acl_free(acl);
}



