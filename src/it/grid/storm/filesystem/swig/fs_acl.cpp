/**
 * @file   fs_acl.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * A class for storing a POSIX-like ACL.
 *
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 */


/** CVS Id */
static const char* const
RCSID = "$Id: fs_acl.cpp,v 1.22 2006/03/31 13:35:01 rmurri Exp $";


/* --- include files --- */

#include "fs_acl.hpp"
#include "users_and_groups.hpp"

#include <cassert>
#include <errno.h>
#include <sys/types.h>
#include <vector>
#include <pwd.h>
#include <grp.h>
#include <unistd.h>


using std::pair;
using std::string;
using std::vector;



/* --- public methods --- */


/** Reset objet to newly-constructed state, clearing the stored
 *  ACL. The @c clear() operation does only affect this object's
 *  internal status, not the ACL on any file it has been @c
 *  enforce()'d on. 
 *
 * <p> The @c clear() method allows for recycling the same object for
 * many @c load() / @c enforce() operations. 
 *
 * @see fs_acl::fs_acl()
 */
void
fs_acl::clear() 
{
  // clear basic ACL
  set_owner(0,PERM_NONE);
  set_group_owner(0,PERM_NONE);
  set_other_perm(PERM_NONE);
  set_mask(PERM_ALL);
  mask_is_set = false;

  // clear extended ACL
  user_acl.clear();
  group_acl.clear();
}


/**
 * Return <code>true</code> if the specified user would be granted the
 * specified access, based on the ACL stored in this object.
 *
 * The algorithm implemented here is taken from the Linux man page
 * <code>acl(5)</code>, and corresponds to the one used in the Linux
 * kernel when ACLs are enabled (see @c posix_acl_check() in @c
 * linux/fs/posix_acl.c ).
 *
 * @param mode  The requested access mode
 *
 * @param uid   The UID of the requesting user
 *
 * @param gids  The list of primary and supplementary GIDs
 *              of the requesting user
 *
 * @return  True if the specified (UID, GIDs) combination
 *          has the requested access mode according to the
 *          ACL stored in this object.
 */
bool
fs_acl::access(const fs_acl::permission_t mode,
               const uid_t uid,
               const vector<gid_t> gids)
  const
{
  // if uid matches the owner ACE, then use that
  if (uid == get_owner_uid())
    return ((mode & get_owner_perm()) == mode);

  const fs_acl::permission_t mask = get_mask();

  // if uid matches a named user ACE, then use that
  if (has_user_perm(uid))
    return ((mode & mask & user_acl[uid]) == mode);

  // check that a group in gids list has the correct access permissions
  bool group_ace_matched = false;
  for(vector<gid_t>::const_iterator i = gids.begin();
      i != gids.end();
      ++i)
    {
    // if a gid matches the group owner ACE...
    if(*i == get_group_owner_gid())
      {
        group_ace_matched = true;
        // ...then return if the *non-effective* permission matches
        // 
        // NOTE: we test for the *non-effective* permission, but
        // return true or false depending on the *effective*
        // permission.  The rationale seems to be: if I _could_ have
        // access according to the unmasked permission, but I don't
        // get access because of the masked one, then it's not worth
        // searching any further, as _all_ group permissions are
        // masked, so I won't get access by any other ACE.  So it's
        // just an optimization shortcut.
        //
        // And this is what the kernel does anyway, so we just return
        // the same answer that the kernel would give. Period.
        //
        if ((mode & get_group_owner_perm()) == mode)
          return ((mode & mask & get_group_owner_perm()) == mode);
      }
    // if a gid matches a named group ACE...
    if (has_group_perm(*i)) 
      {
        group_ace_matched = true;
        // ...then return if the *non-effective* permission matches
        if ((mode & group_acl[*i]) == mode)
          return ((mode & mask & group_acl[*i]) == mode);
      }
    }
  
  if (group_ace_matched)
    // if a group ACE matched, then don't check the "other" permission
    return false;
  else
    // if all else failed, check 'other' permission
    return ((mode & get_other_perm()) == mode);
}
