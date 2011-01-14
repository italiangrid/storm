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
 * @file   gpfs31_acl.cpp
 *
 * Implementation of the gpfs31_acl class.
 * This implementation uses the GPFS library functions to get and put ACLs
 * This functions are available only from GPFS version 3.1.
 * @file gpfs31_acl.cpp contains the implementation for the previous versions
 * of GPFS. 
 *
 * @author Alberto Forti <alberto.forti@cnaf.infn.it>
 */


#define __USE_LARGEFILE64
#define _LARGEFILE_SOURCE
#define _LARGEFILE64_SOURCE

#include "gpfs31_acl.hpp"

#include <gpfs.h>
#include <errno.h>
#include <string>
#include <cassert>
#include <unistd.h>
#include <sstream>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>

using std::string;

/** Convert a permission bitfield from gpfs_acePerm_t to fs_acl::permission_t
 *  format.
 *
 * @param perm GPFS permission bits.
 */
fs_acl::permission_t
gpfs31_acl::gpfs_perm_to_permission_t(gpfs_acePerm_t perm)
{
  permission_t result = PERM_NONE;

  if (perm & ACL_PERM_READ) { 
    result |= PERM_READ_DATA | PERM_LIST_DIRECTORY | PERM_READ_ACL;
  }
  if (perm & ACL_PERM_WRITE) {
    result |= PERM_WRITE_DATA | PERM_CREATE_FILE | PERM_CREATE_SUBDIRECTORY | PERM_DELETE_CHILD;
  }
  if (perm & ACL_PERM_EXECUTE) { 
    result |= PERM_EXECUTE | PERM_TRAVERSE_DIRECTORY;
  }
  if (perm & ACL_PERM_CONTROL) {
    result |= PERM_WRITE_ACL;
  }
  
  return result;
}

/** Convert a permission bitfield from fs_acl::permission_t to gpfs_acePerm_t
 *  format.
 *
 * If @em any of the permission bits @c PERM_READ_DATA, @c
 * PERM_READ_ACL or @c PERM_LIST_DIRECTORY is set, then will set the
 * "read" permission (@c ACL_PERM_READ) in the output.
 *
 * If @em any of the permission bits @c PERM_WRITE_DATA, @c
 * PERM_WRITE_ACL, @c PERM_CREATE_FILE, @c PERM_CREATE_SUBDIRECTORY,
 * @c PERM_DELETE or @c PERM_DELETE_CHILD is set, then will set the
 * "write" permission (@c ACL_PERM_WRITE) in the output.
 *
 * If @em any of the permission bits @c PERM_EXECUTE or @c
 * PERM_TRAVERSE_DIRECTORY is set, then will set the "execute/search"
 * permission (@c ACL_PERM_EXECUTE) in the output.
 *
 * @param perm fs_acl::permission_t bitfield of permissions
 */
gpfs_acePerm_t
gpfs31_acl::permission_t_to_gpfs_perm(const fs_acl::permission_t perm)
{
  gpfs_acePerm_t result = 0;

  if (perm & (PERM_READ_DATA | PERM_LIST_DIRECTORY)) {
    result |= ACL_PERM_READ;
  }
  // TODO: Check what should be considered to enable the WRITE permission!!!
  if (perm & (PERM_WRITE_DATA)) {// | PERM_CREATE_FILE | PERM_CREATE_SUBDIRECTORY | PERM_DELETE_CHILD)) {
    result |= ACL_PERM_WRITE;
  }
  if (perm & (PERM_EXECUTE | PERM_TRAVERSE_DIRECTORY)) {
    result |= ACL_PERM_EXECUTE;
  }
  if (perm & PERM_WRITE_ACL) {
    result |= ACL_PERM_CONTROL;
  }
  
  return result;
}

/** Load ACL from the specified filesystem entry (file or directory).
 * Uses the GPFS library function gpfs_getacl(), available only from
 * GPFS version 3.1, to read the whole ACL off a file or directory.
 *
 * @em Note: load()ing an ACL will @em overwrite existing owner, group
 * owner and mask ACEs, but will @em add per-user and per-group ACEs
 * to the ones already stored in this object.
 *
 * @em Note: the DELETE permission flag is not set unless the given @a
 * delete_permission_too parameter is @c true; since checks to ensure
 * validity of the DELETE permission flag are costly, they are
 * disabled by default.
 *
 * @param  path  Pathname to the inode to load the ACL from.
 *
 * @param delete_permission_too If @c true, then perform the steps
 * necessary to ensure the DELETE permission bit is valid; since these
 * checks are @em disabled by default.
 *
 * @todo FIXME: implement the delete permission check!!
 */
void
gpfs31_acl::load(const string& path, const bool delete_permission_too)
  throw(fs::error, fs::invalid_path, 
        fs::permission_denied, fs::acl_not_supported, 
        std::exception)
{
  gpfs_acl_t *acl;
  void *buffer;
  int status = -1;
  
  int stoploop = 0;   // Just a little trick to write the code only once.
  int bufsize = 150;  // Size in bytes of the ACLs buffer (enough for 10 entries).
  // The while loop is executed only one or two times. If it is executed two
  // times it is because the first time the 'bufsize' was too small.
  while (stoploop < 2) {
    buffer = calloc(bufsize, 1);
    if (buffer == NULL) {
      posixfs_acl::throw_error(ENOMEM, path, "[gpfs31_acl.cpp]gpfs_getacl");
    }
    acl = (gpfs_acl_t*) buffer;
    
    acl->acl_len = bufsize;
    acl->acl_level = 0;
    acl->acl_version = 0;
    acl->acl_type = GPFS_ACL_TYPE_ACCESS;
    acl->acl_nace = 0;
    
    // Invoke the GPFS library function gpfs_getACL()
    status = gpfs_getacl(const_cast<char*>(path.c_str()), GPFS_GETACL_STRUCT, buffer);
    if (status != 0) {
      if (errno == ENOSPC) {
        // The buffer is too small and gpfs_getacl() has responed with the needed
        // size in bytes to hold the entire ACL structure.
        stoploop++;  // Retry (only one more time) with the right bufsize
        bufsize = acl->acl_len;  // Set by gpfs_getacl()
      } else {
        stoploop += 2;
      }
      free(buffer);
    } else { // ACLs succefully retrieved.
        stoploop += 2;
    }
  }
  if (status != 0) {
    // buffer was deallocated inside the while loop.
    posixfs_acl::throw_error(errno, path, "[gpfs31_acl.cpp]gpfs_getacl");
  }
  
  /* "base" ACL info is gotten via the std UNIX permission bits */
  //posixfs_acl::load_permission_bits(path);

/* NEW 
 * To avoid the problem on gpfs for file larger than 2 GB:
 * 
 * the posixfs_acl::load_permission_bits() fails on gpfs for bigger file, since the it relies on the posix sys/stat.h, that for some
 * unknonwn reason fails on file on GPFS larger than 2 GB.
 * This brunch of code just replace the load_permission operation using the gpfs_stat ()*/ 
 
//start load_permission
  
   stat64_t statbuf;
  
   int rc = gpfs_stat(const_cast<char*>(path.c_str()), &statbuf);
  
   if (-1 == rc) {
    if (EINVAL == errno) {
      std::ostringstream msg;
      msg << "No GPFS filesystem on '" << path << "'";
      throw fs::wrong_filesystem_type(msg.str());
    } else { // ENOSYS, ENOENT, EPERM
      std::ostringstream msg;
      msg << __FILE__ 
          << ": gpfs_stat(" << path << ") failed";
     throw fs::wrong_filesystem_type(msg.str());
    }
   }
  
  if (S_ISDIR(statbuf.st_mode))
    loaded_from_directory = true;
  else 
    loaded_from_directory = false;

  const permission_t owner_perm = to_permission_t(statbuf.st_mode>>6);
  const permission_t group_owner_perm = to_permission_t(statbuf.st_mode>>3);
  const permission_t other_perm = to_permission_t(statbuf.st_mode);

  set_owner(statbuf.st_uid, owner_perm);
  set_group_owner(statbuf.st_gid, group_owner_perm);
  set_other_perm(other_perm);

//end load_permission 
  
  // Now the ACL is in the 'acl' buffer and must be converted in our
  // internal format. The following for loop converts the retrieved
  // ACEs (Access Control Entries).
  int nace = acl->acl_nace;         // Number of ACL entries
  gpfs_ace_v1_t* ace = acl->ace_v1; // Array of ACEs entries
  for (int i=0; i<nace; i++) {
    gpfs_aceType_t  type = ace[i].ace_type;
    gpfs_uid_t       who = ace[i].ace_who;
    gpfs_acePerm_t  perm = ace[i].ace_perm;
    switch (type) {
      case GPFS_ACL_USER_OBJ:
        set_owner_perm(gpfs_perm_to_permission_t(perm));
        break;
      case GPFS_ACL_GROUP_OBJ:
        set_group_owner_perm(gpfs_perm_to_permission_t(perm));
        break;
      case GPFS_ACL_OTHER:
        set_other_perm(gpfs_perm_to_permission_t(perm));
        break;
      case GPFS_ACL_MASK:
        set_mask(gpfs_perm_to_permission_t(perm));
        break;
      case GPFS_ACL_USER:
        set_user_perm((uid_t) who, gpfs_perm_to_permission_t(perm));
        break;
      case GPFS_ACL_GROUP:
        set_group_perm((gid_t) who, gpfs_perm_to_permission_t(perm));
        break;
      default:
        /* unhandled case, fatal logic error */
        assert(type == GPFS_ACL_USER_OBJ
               || type == GPFS_ACL_GROUP_OBJ
               || type == GPFS_ACL_OTHER
               || type == GPFS_ACL_MASK
               || type == GPFS_ACL_USER
               || type == GPFS_ACL_GROUP);
        break;
    }
  }
  free(buffer);
  if (delete_permission_too) {
    if (path == "/")
      ; // no-one can delete '/'
    else
      load_delete_permission(path);
  }
}

/** Load the @c DELETE permissions, inspecting the parent directory
 *  ACL. This can be a costly operation, so is factored out of the
 *  main @c load() code.
 */
void
gpfs31_acl::load_delete_permission(const string& pathname)
{
  // hunt for the delete permission in the parent directory
  const size_t last_slash = pathname.find_last_of('/');
  const string dirname(pathname, 0, last_slash);
  if (! dirname.empty()) {
    struct stat statbuf;
    int rc = stat(dirname.c_str(), &statbuf);
    if (-1 == rc) {
      // error!
      throw_error(errno, pathname, "stat");
    }

    fs_acl* parent_acl = new_same_class();
    parent_acl->load(dirname, false); // avoid recursion up to root dir.
    
    if (statbuf.st_mode & S_ISVTX) {
      // "sticky" bit set, only owner can delete
      if (parent_acl->has_user_perm(get_owner_uid())
          && (parent_acl->get_user_effective_perm(get_owner_uid()) & PERM_DELETE_CHILD))
      {
        grant_owner_perm(PERM_DELETE_CHILD);
      }
      if (parent_acl->get_user_effective_perm(get_owner_uid()) & PERM_DELETE_CHILD) {
        grant_user_perm(get_owner_uid(), PERM_DELETE);
      }
    }
    else {
      // no "sticky" bit, everyone having write access to parent dir can delete.
      if (parent_acl->has_user_perm(get_owner_uid())
          && (parent_acl->get_user_effective_perm(get_owner_uid()) & PERM_DELETE_CHILD))
      {
        grant_owner_perm(PERM_DELETE);
      }
      if (parent_acl->has_group_perm(get_owner_uid())
          && (parent_acl->get_group_effective_perm(get_group_owner_gid()) & PERM_DELETE_CHILD))
      {
        grant_group_owner_perm(PERM_DELETE);
      }

      for (user_acl_t::const_iterator i=parent_acl->user_acl_begin(); i!=parent_acl->user_acl_end(); ++i) {
        if (parent_acl->has_user_perm(i->first)
            && (parent_acl->get_user_effective_perm(i->first) & PERM_DELETE_CHILD)) 
        {
          grant_user_perm(i->first, PERM_DELETE);
        }
      }

      for (group_acl_t::const_iterator i = parent_acl->group_acl_begin(); i!=parent_acl->group_acl_end(); ++i) {
        if (parent_acl->has_group_perm(i->first)
            && (parent_acl->get_group_effective_perm(i->first) & PERM_DELETE_CHILD))
        {
          grant_group_perm(i->first, PERM_DELETE);
        }
      }

      if (statbuf.st_mode & S_IWOTH) {
        grant_other_perm(PERM_DELETE);
      }
    }
    delete parent_acl;
  }
}

/** Make a new instance of the same class of this object. 
 *
 * <p> Needed in @c load_delete_permission() to instanciate the right
 * kind of object for classes inheriting from this one. 
 */
fs_acl*
gpfs31_acl::new_same_class() const
{
  return new gpfs31_acl();
}

/** Set ACL on the specified filesystem entry (file or directory).
 * Invokes the gpfs_putacl() GPFS library function, available only
 * from GPFS version 3.1, to set the whole ACL onto a file or
 * directory.
 *
 * @param path file or directory whose ACL is to be set to the
 * contents of this object.
 */
void
gpfs31_acl::enforce(const string& path)
  const
  throw(fs::error, fs::invalid_path, 
        fs::permission_denied, fs::acl_not_supported, 
        std::exception)
{
  void *buffer = NULL;
  gpfs_acl_t *acl;
  gpfs_ace_v1_t *ace;
  int nace = size() + 3; // Number of ACEs entries (over estimated)
  int bufsize = sizeof(gpfs_acl_t) + (sizeof(gpfs_ace_v1_t) * nace);

  buffer = calloc(bufsize, 1);
  if (buffer == NULL) {
    posixfs_acl::throw_error(ENOMEM, path, "[gpfs31_acl.cpp]gpfs_putacl");
  }
  acl = (gpfs_acl_t*) buffer;
  ace = acl->ace_v1;
  
  ace[0].ace_type = GPFS_ACL_USER_OBJ;
  ace[0].ace_who  = get_owner_uid();
  ace[0].ace_perm = permission_t_to_gpfs_perm(get_owner_perm());
  
  ace[1].ace_type = GPFS_ACL_GROUP_OBJ;
  ace[1].ace_who  = get_group_owner_gid();
  ace[1].ace_perm = permission_t_to_gpfs_perm(get_group_owner_perm());
  
  ace[2].ace_type = GPFS_ACL_OTHER;
  ace[2].ace_who  = 0;
  ace[2].ace_perm = permission_t_to_gpfs_perm(get_other_perm());
  
  int idx = 3;
  for (user_acl_t::const_iterator user=user_acl_begin(); user!=user_acl_end(); ++user) {
    ace[idx].ace_type = GPFS_ACL_USER;
    ace[idx].ace_who  = user->first;
    ace[idx].ace_perm = permission_t_to_gpfs_perm(user->second);
    idx++;
  }
  for (group_acl_t::const_iterator group=group_acl_begin(); group!=group_acl_end(); ++group) {
    ace[idx].ace_type = GPFS_ACL_GROUP;
    ace[idx].ace_who  = group->first;
    ace[idx].ace_perm = permission_t_to_gpfs_perm(group->second);
    idx++;
  }
  
  // Add mask if there's any ACE beyond the standard UNIX permissions,
  // or if it has been explicitly set
  if (has_extended_acl() || has_explicit_mask()) {
    ace[idx].ace_type = GPFS_ACL_MASK;
    ace[idx].ace_who = 0;
    ace[idx].ace_perm = permission_t_to_gpfs_perm(get_mask());
    idx++;
  }
  // Should have built a correct ACL
  assert(idx <= nace);

  acl->acl_level   = 0;
  acl->acl_version = GPFS_ACL_VERSION_POSIX;
  acl->acl_type    = GPFS_ACL_TYPE_ACCESS;  
  // The buffer length MUST be EXACT, otherwise the function gpfs_putacl() fails!
  acl->acl_len     = (sizeof(gpfs_acl_t)-sizeof(gpfs_ace_v4_t)) + ((idx) * sizeof(gpfs_ace_v1_t));
  acl->acl_nace    = idx;
  
  int status = gpfs_putacl(const_cast<char*>(path.c_str()), GPFS_PUTACL_STRUCT, buffer);
  free(buffer);
  if (status != 0) {
    posixfs_acl::throw_error(errno, path, "[gpfs31_acl.cpp]gpfs_putacl");
  }
}
