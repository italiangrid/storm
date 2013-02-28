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
 * @file    posixfs_acl.cpp
 * @author  Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Implementation of the @c posixfs_acl class.
 */



/** CVS Id. */
static const char* const
RCSID = "$Id: posixfs_acl.cpp,v 1.28 2007/04/13 11:28:24 aforti Exp $";


/* --- include files --- */

#include "fs_errors.hpp"
#include "posixfs_acl.hpp"
#include "util.hpp"

#include <acl/libacl.h>
#include <cassert>
#include <errno.h>
#include <sstream>
#include <stdexcept>
#include <string>
#include <sys/acl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <string.h>

using std::string;



/* --- public methods --- */


/** Convert a permission bitfield from fs_acl::permission_t to POSIX
 *  libacl format.
 *
 * If @em any of the permission bits @c PERM_READ_DATA, @c
 * PERM_READ_ACL or @c PERM_LIST_DIRECTORY is set, then will set the
 * "read" permission (@c ACL_READ) in the output.
 *
 * If @em any of the permission bits @c PERM_WRITE_DATA, @c
 * PERM_WRITE_ACL, @c PERM_CREATE_FILE, @c PERM_CREATE_SUBDIRECTORY,
 * @c PERM_DELETE or @c PERM_DELETE_CHILD is set, then will set the
 * "write" permission (@c ACL_WRITE) in the output.
 *
 * If @em any of the permission bits @c PERM_EXECUTE or @c
 * PERM_TRAVERSE_DIRECTORY is set, then will set the "execute/search"
 * permission (@c ACL_EXECUTE) in the output.
 *
 * @param perm fs_acl::permission_t bitfield of permissions
 */
acl_perm_t
posixfs_acl::from_permission_t(const fs_acl::permission_t perm)
{
  acl_perm_t result = 0;

  if (perm &
      (PERM_READ_DATA
       |PERM_LIST_DIRECTORY))
    result |= ACL_READ;

  if (perm &
      (PERM_WRITE_DATA))
    result |= ACL_WRITE;

  if (perm &
      (PERM_EXECUTE
       |PERM_TRAVERSE_DIRECTORY))
    result |= ACL_EXECUTE;

  return result;
}


/** Convert a permission bitfield from POSIX to fs_acl::permission_t
 *  format.  Note that the given permission @a perm is assumed to be
 *  the permission @em one user/group has on @em one file; therefore
 *  it's expressed as a combination of constants @c S_IROTH, @c
 *  S_IWOTH and @c S_IXOTH.
 *
 * @param perm UNIX permission bits as a combination of @c
 * S_IROTH|S_IWOTH|S_IXOTH
 */
fs_acl::permission_t
posixfs_acl::to_permission_t(const unsigned short perm)
{
  permission_t result = PERM_NONE;

  if (perm & S_IROTH)
    result |=
      PERM_READ_DATA
      |PERM_LIST_DIRECTORY;
  if (perm & S_IWOTH)
    result |=
      PERM_WRITE_DATA
      |PERM_CREATE_SUBDIRECTORY
      |PERM_DELETE_CHILD;
  if (perm & S_IXOTH)
    result |=
      PERM_EXECUTE
      |PERM_TRAVERSE_DIRECTORY;

  return result;
}


/** Make a new instance of the same class of this object.
 *
 * <p> Needed in @c load_delete_permission() to instanciate the right
 * kind of object for classes inheriting from this one.
 */
fs_acl*
posixfs_acl::new_same_class() const
{
  return new posixfs_acl();
}


/** Load the ACL resulting from the UNIX permission bits.  This is
 *  really part of the @c load() code, that is factored out for
 *  readability.
 *
 * @param  path  Pathname to the inode to load the ACL from.
 *
 * @throws %system_error if some system call failed.
 */
void
posixfs_acl::load_permission_bits(const string& pathname)
{
  struct stat64 statbuf;
  fs::xstat(pathname.c_str(), statbuf);


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
}



/** Load the @c DELETE permissions, inspecting the parent directory
 *  ACL. This can be a costly operation, so is factored out of the
 *  main @c load() code.
 */
void
posixfs_acl::load_delete_permission(const string& pathname)
{
  // hunt for the delete permission in the parent directory
  const size_t last_slash = pathname.find_last_of('/');
  const string dirname(pathname, 0, last_slash);
  if (! dirname.empty()) {
    struct stat64 statbuf;
    int rc = stat64(dirname.c_str(), &statbuf);
    if(-1 == rc){
      // error!
      throw_error(errno, pathname, "stat");
    }

    fs_acl* parent_acl = new_same_class();
    parent_acl->load(dirname, false); // avoid recursion up to root dir.

    if (statbuf.st_mode & S_ISVTX) {
      // "sticky" bit set, only owner can delete
      if(parent_acl->has_user_perm(get_owner_uid())
         && (parent_acl->get_user_effective_perm(get_owner_uid()) & PERM_DELETE_CHILD))
        grant_owner_perm(PERM_DELETE_CHILD);

      if(parent_acl->get_user_effective_perm(get_owner_uid()) & PERM_DELETE_CHILD)
        grant_user_perm (get_owner_uid(), PERM_DELETE);
    }
    else {
      // no "sticky" bit, everyone having write access to parent dir can delete.
      if(parent_acl->has_user_perm(get_owner_uid())
         && (parent_acl->get_user_effective_perm(get_owner_uid()) & PERM_DELETE_CHILD))
        grant_owner_perm(PERM_DELETE);

      if(parent_acl->has_group_perm(get_owner_uid())
         && (parent_acl->get_group_effective_perm(get_group_owner_gid()) & PERM_DELETE_CHILD))
        grant_group_owner_perm(PERM_DELETE);

      for (user_acl_t::const_iterator i = parent_acl->user_acl_begin();
           i != parent_acl->user_acl_end();
           ++i)
        if(parent_acl->has_user_perm(i->first)
           && (parent_acl->get_user_effective_perm(i->first) & PERM_DELETE_CHILD))
          grant_user_perm (i->first, PERM_DELETE);

      for (group_acl_t::const_iterator i = parent_acl->group_acl_begin();
           i != parent_acl->group_acl_end();
           ++i)
        if(parent_acl->has_group_perm(i->first)
           && (parent_acl->get_group_effective_perm(i->first) & PERM_DELETE_CHILD))
          grant_group_perm (i->first, PERM_DELETE);

      if (statbuf.st_mode & S_IWOTH)
        grant_other_perm(PERM_DELETE);
    }
    delete parent_acl;
  }
}


/**
 * Return an <code>acl_perm_t</code> containing only those bits from
 * <code>ACL_READ</code>, <code>ACL_WRITE</code>,
 * <code>ACL_EXECUTE</code>, that are set in the given permset.
 *
 * <p> @em FIXME: This call <em>assumes</em> that the given
 * permset is valid, it is the caller's responsibility to check that
 * this is the case; else, the result is undefined.
 *
 * @todo FIXME: this is non-portable, as the
 * <code>acl_get_perm(3)</code> call is Linux-specific, but I have yet
 * to understand how am I supposed to inspect an @c acl_permset_t to
 * check which permissions are encoded in it...  This function is
 * provided so to isolate the non-portable code.
 *
 * @param  permset  A valid <code>acl_permset_t</code> object.
 *
 * @return  Permission bits extracted from the given permset.
 */
static mode_t
make_perm_from_permset (const acl_permset_t &permset)
{
  mode_t perm = 0;

  if (acl_get_perm (permset, ACL_READ))
    perm |= S_IROTH;
  if (acl_get_perm (permset, ACL_WRITE))
    perm |= S_IWOTH;
  if (acl_get_perm (permset, ACL_EXECUTE))
    perm |= S_IXOTH;

  return perm;
}


/**
 * Load the ACL from the specified file or directory, and store the
 * permissions in this object's fields (inherited from
 * <code>fs_acl</code>).
 *
 * <p>The <code>fs_acl</code> fields are filled even if the file or
 * directory has no "extended" ACL (i.e., only standard UNIX permissions).
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
 * @throws fs::system_error if some system call failed.
 */
void
posixfs_acl::load (const string& pathname, const bool delete_permission_too)
  throw(fs::error, fs::invalid_path,
        fs::permission_denied, fs::acl_not_supported,
        std::exception)
{
  const char *const path = pathname.c_str();

  acl_t acl = acl_get_file (path, ACL_TYPE_ACCESS);
  if (NULL == acl)
    throw_error(errno, pathname, "acl_get_file");

  /* "base" ACL info is gotten via the std UNIX permission bits */
  load_permission_bits(pathname);

  /* look for "extended" ACL info */
  acl_entry_t entry;
  int rc;
  rc = acl_get_entry(acl, ACL_FIRST_ENTRY, &entry);
  if (-1 == rc)
    /* error occurred, throw exception */
    throw_error(errno, pathname, "acl_get_entry");
  else if (0 == rc)
    /* nothing more to do */
    return;
  else if (1 == rc) {
    /* scan list of ACL entries */
    do {
      acl_tag_t tag;
      acl_permset_t permset;

      acl_get_tag_type(entry, &tag);
      acl_get_permset(entry, &permset);
      mode_t perm = make_perm_from_permset(permset);

      if(ACL_USER_OBJ == tag)
        set_owner_perm (to_permission_t(perm));
      else if(ACL_GROUP_OBJ == tag)
        set_group_owner_perm (to_permission_t(perm));
      else if (ACL_OTHER == tag)
        set_other_perm(to_permission_t(perm));
      else if (ACL_MASK == tag)
        set_mask(to_permission_t(perm));
      else if(ACL_USER == tag)
        {
          uid_t *uid_p = (uid_t*)acl_get_qualifier (entry);
          set_user_perm (*uid_p, to_permission_t(perm));
          acl_free(uid_p);
        }
      else if(ACL_GROUP == tag)
        {
          gid_t *gid_p = (gid_t*)acl_get_qualifier (entry);
          set_group_perm (*gid_p, to_permission_t(perm));
          acl_free(gid_p);
        }
      else
        /* unhandled case, fatal logic error */
        assert(ACL_USER_OBJ == tag
               || ACL_GROUP_OBJ == tag
               || ACL_OTHER == tag
               || ACL_MASK == tag
               || ACL_USER == tag
               || ACL_GROUP == tag);
    } while (1 == acl_get_entry (acl, ACL_NEXT_ENTRY, &entry));

    acl_free(acl);
  }
  else {
    // logic error: unknown return code from acl_get_entry()
    assert (rc == -1 || rc == 0 || rc == 1);
  }

  if (delete_permission_too) {
    if(pathname == "/")
      ; // no-one can delete '/'
    else
      load_delete_permission(pathname);
  }
}


/** React on errors in add_to_acl() throwing appropriate exceptions.
    This is really code from the body of add_to_acl(), factored out for clarity. */
static void
add_to_acl__throw(const int err)
{
  // EINVAL should be debugged out of the code
  assert (EINVAL != err);

  // but if we're running w/o assertions, throw logic_error
  if (EINVAL == errno)
    throw std::logic_error(__FILE__
                           ": add_to_acl: invalid acl_p parameter");

  // little to do if ENOMEM happens, still...
  if (ENOMEM == errno)
    throw std::runtime_error(__FILE__ ": add_to_acl: out of memory");

  // again, it's a programming error if something else occurs...
  assert ((EINVAL==errno)||(ENOMEM==errno));

  // yet, throw logic_error if we're running w/o asserts
  throw std::logic_error(__FILE__
                         ": add_to_acl: unexpected system error");
}


/** Convenience function to add an ACL entry to a libacl @c acl_t
 * object.
 *
 * @throw fs::system_error, with @c errno as set by
 * <code>acl_create_entry</code> or <code>acl_set_qualifier</code>:
 *
 * - @c EINVAL: parameter @a acl_p does not point to a valid @c acl_t
 *      object;
 * - @c ENOMEM: out of memory.
 */
template<typename uid_or_gid_t>
static void
add_to_acl (acl_t *acl_p,
            const acl_tag_t tag,
            const uid_or_gid_t subject,
            const acl_perm_t perm)
{
  acl_entry_t entry;
  int rc;

  rc = acl_create_entry (acl_p, &entry); // EINVAL, ENOMEM
  if (-1 == rc)
    add_to_acl__throw(errno);

  acl_set_tag_type (entry, tag);

  if (ACL_USER == tag || ACL_GROUP == tag) {
    rc = acl_set_qualifier (entry, &subject); // EINVAL, ENOMEM
    if (-1 == rc)
      add_to_acl__throw(errno);
  }

  acl_permset_t permset;
  rc = acl_get_permset(entry, &permset); // EINVAL
  if (-1 == rc)
    add_to_acl__throw(errno);

  acl_clear_perms (permset);
  acl_add_perm (permset, perm);
}


/** Set the given ACL on a file or directory; any previous ACL on the
 * destination path is overwritten.
 *
 * @throws  integer error code from <code>errno</code>,
 *          if some system call failed.
 */
void
posixfs_acl::enforce(const string& pathname)
  const
  throw(fs::error, fs::invalid_path,
        fs::permission_denied, fs::acl_not_supported,
        std::exception)
{
  // create an acl_t (suitable for passing to the libacl functions)
  // containing the ACL specified stored in this object
  acl_t acl = acl_init(size() + 1);

  add_to_acl(&acl, ACL_USER_OBJ,
             get_owner_uid(), from_permission_t(get_owner_perm()));
  add_to_acl(&acl, ACL_GROUP_OBJ,
             get_group_owner_gid(),
             from_permission_t(get_group_owner_perm()));
  add_to_acl(&acl, ACL_OTHER,
             0, from_permission_t(get_other_perm()));

  for (user_acl_t::const_iterator user = user_acl_begin();
       user != user_acl_end();
       ++user)
    add_to_acl (&acl, ACL_USER, user->first,
                from_permission_t(user->second));

  for (group_acl_t::const_iterator group = group_acl_begin();
       group != group_acl_end();
       ++group)
    add_to_acl (&acl, ACL_GROUP, group->first,
                from_permission_t(group->second));

  // add mask if there's any ACE beyond the standard UNIX permissions,
  // or if it has been explicitly set
  if (has_extended_acl() || has_explicit_mask())
    add_to_acl(&acl, ACL_MASK, 0, from_permission_t(get_mask()));

  // should have built a correct ACL
  assert (0 == acl_valid(acl));

  // set ACL on pathname
  int rc = acl_set_file(pathname.c_str(), ACL_TYPE_ACCESS, acl);
  acl_free(acl);
  if (-1 == rc)
    throw_error(errno, pathname, "acl_set_file");

}



/* --- protected methods --- */

/** Throw an exception based on the @c errno system error code. */
void
posixfs_acl::throw_error(const int err,
                         const std::string& pathname,
                         const char* const funcname) const
{
  switch (err) {
  case EINVAL:
  case ENOENT:
  case ENOTDIR:
  case ENAMETOOLONG:
    {
      std::ostringstream msg;
      msg << funcname << ": Invalid filesystem entry '"
          << pathname << "': " << strerror(err);
      throw fs::invalid_path(msg.str());
    }
  case EACCES:
  case EPERM:
    {
      std::ostringstream msg;
      msg << funcname << ": Cannot read ACL of filesystem entry '"
          << pathname << "': " << strerror(err);
      throw fs::permission_denied(msg.str());
    }
  case ENOTSUP:
  case ENOSYS:
    {
      std::ostringstream msg;
      msg << funcname << ": No support for ACLs on the filesystem where '"
          << pathname << "' is located: " << strerror(err);
      throw fs::acl_not_supported(msg.str());
    }
  case ENOSPC:
    {
      std::ostringstream msg;
      msg << funcname << ": Cannot write ACL on '"
          << pathname << "': " << strerror(err)
          << " (either the filesystem is full, "
             "or the maximum number of ACLs was reached.)";
      throw fs::error(msg.str());  // FIXME: need specific error?
    }
  case ENOMEM:
    {
      std::ostringstream msg;
      msg << funcname << ": Memory allocation error (ACL buffer) for'"
          << pathname << "': " << strerror(err);
      throw fs::error(msg.str());
    }
  default:
    {
      std::ostringstream msg;
      msg << funcname << ": BUG: unknown error in " __FILE__
        " at line " <<  __LINE__  << ": "
        << strerror(err);
      throw std::logic_error(msg.str());
    }
  }
}
