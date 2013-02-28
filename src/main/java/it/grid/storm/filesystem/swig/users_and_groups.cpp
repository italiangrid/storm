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


/// CVS Revsion info
static const char* const 
RCSID = "$Id: users_and_groups.cpp,v 1.8 2006/03/29 16:32:56 rmurri Exp $";



/* --- include files --- */

#include "users_and_groups.hpp"
#include "string_to_number.hpp"
#include "fs_errors.hpp"

#include <cassert>
#include <cstdlib>
#include <errno.h>
#include <grp.h>
#include <pwd.h>
#include <sys/types.h>
#include <sstream>
#include <string>
#include <unistd.h>



/* --- module-local functions --- */

/** Maximum length of a buffer for <code>getpwnam_r</code> */
static const size_t GETPW_MAX = sysconf (_SC_GETPW_R_SIZE_MAX);

/** Maximum length of a buffer for <code>getpwnam_r</code> */
static const size_t GETGR_MAX = sysconf (_SC_GETGR_R_SIZE_MAX);



// --- module constants --- //

/** Represents a UID that will never be assigned to a real user. */
const uid_t invalid_uid = static_cast<uid_t>(-1);

/** Represents a GID that will never be assigned to a real group. */
const gid_t invalid_gid = static_cast<gid_t>(-1);



/* --- public functions ---- */

/** Return the UID associated with the given user name, or throw an
 * exception if not found.
 *
 * @throw fs::system_error, if a system call failed;
 * std::runtime_error, if the named user was not found in the system
 * users database.
 */
uid_t 
uid_from_username (const std::string& name) 
  throw(fs::system_error, std::runtime_error)
{ 
  assert (! name.empty());

  // is "name" a UID number?
  try {
    uid_t uid = xstrtoul (name, 0);
    // successful conversion
    return uid;
  }
  catch (...) {
    // unsuccessful conversion, proceed
  }

  // else, map it using system user database
  struct passwd *result = NULL;
  struct passwd pwd = {
    NULL, // char    *pw_name;
    NULL, // char    *pw_passwd;
    invalid_uid,    // uid_t   pw_uid;
    invalid_gid,    // gid_t   pw_gid;
    NULL, // char    *pw_gecos;
    NULL, // char    *pw_dir;
    NULL  // char    *pw_shell;
  };
  char buf[GETPW_MAX+1];
  
  int rc = getpwnam_r (name.c_str(), &pwd,
                       buf, GETPW_MAX, &result);
  if (0 != rc)
    throw fs::system_error(__FILE__ 
                           "cannot map username to UID: getpwnam_r() failed", 
                           errno);

  if (NULL == result) {
    // user not found
    std::ostringstream msg;
    msg << "User '" << name  << "' not found in system user database.";
    throw not_found(msg.str());
  }

  return pwd.pw_uid;
}


/** Return the GID associated with the given group name, or throw an
 * exception if not found.
 *
 * @throw fs::system_error, if a system call failed;
 * std::runtime_error, if the named user was not found in the system
 * users database.
 */
gid_t 
gid_from_groupname (const std::string& name) 
  throw(fs::system_error, std::runtime_error)
{
  assert (! name.empty());

  // is "name" a GID number?
  try {
    gid_t gid = xstrtoul (name, 0);
    // successful conversion
    return gid;
  }
  catch (...) {
    // unsuccessful conversion, proceed
  }

  struct group *result = NULL;
  struct group grp = {
    NULL, // char    *gr_name;
    NULL, // char    *gr_passwd;
    invalid_gid,   // gid_t   gr_gid;
    NULL  // char    *gr_mem;
  };
  char buf[GETGR_MAX+1];
  
  int rc = getgrnam_r (name.c_str(), &grp,
                       buf, GETGR_MAX, &result);
  if (0 != rc)
    throw fs::system_error(__FILE__ "cannot map group name to GID: getgrnam_r() failed", errno);

  if (NULL == result) {
    // group not found
    std::ostringstream msg;
    msg << "Group '" << name << "' not found in system group database.";
    throw not_found(msg.str());
  }
  
  return grp.gr_gid;
}


/** Return the user name associated with the given @a uid.
 *
 * @param uid The UID whose group name is to be looked for.
 *
 * @return A string containing the name of the user whose UID is @a
 * uid, or a printed representation of @a uid, if that user is not in
 * the system users database.
 *
 * @throw fs::system_error, if a system call failed
 */
std::string
username_from_uid (const uid_t uid)
  throw(fs::system_error)
{
  struct passwd *result = NULL;
  struct passwd pwd = {
    NULL, // char    *pw_name;
    NULL, // char    *pw_passwd;
    invalid_uid,    // uid_t   pw_uid;
    invalid_gid,    // gid_t   pw_gid;
    NULL, // char    *pw_gecos;
    NULL, // char    *pw_dir;
    NULL  // char    *pw_shell;
  };
  char buf[GETPW_MAX+1];
  
  int rc = getpwuid_r (uid, &pwd, buf, GETPW_MAX, &result);
  if (0 != rc)
    throw fs::system_error(__FILE__ "cannot map UID to user name: getpwuid_r() failed", errno);

  if (NULL == result) {
    std::ostringstream output;
    output << uid;
    return std::string(output.str());
  }
  else 
    return std::string(pwd.pw_name);
}


/** Return the group name associated with the given @a gid.
 *
 * @param gid The GID whose group name is to be looked for.
 *
 * @return A string containing the name of the group whose GID is
 * @a, or a printed representation of @a gid, if that group is not
 * in the system groups database.
 *
 * @throw fs::system_error, if a system call failed
 */
std::string
groupname_from_gid (const gid_t gid)
  throw(fs::system_error)
{
  struct group *result = NULL;
  struct group grp = {
    NULL, // char    *gr_name;
    NULL, // char    *gr_passwd;
    invalid_gid,   // gid_t   gr_gid;
    NULL  // char    *gr_mem;
  };
  char buf[GETGR_MAX+1];
  
  int rc = getgrgid_r (gid, &grp, buf, GETGR_MAX, &result);
  if (0 != rc)
    throw fs::system_error(__FILE__ "cannot map GID to group name: getgrgid_r() failed", errno);

  if (NULL == result) {
    std::ostringstream output;
    output << gid;
    return std::string(output.str());
  }
  else 
    return std::string(grp.gr_name);
}
