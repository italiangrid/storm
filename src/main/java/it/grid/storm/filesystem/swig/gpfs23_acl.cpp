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
 * @file   gpfs23_acl.cpp
 *
 * Implementation of the gpfs23_acl class.
 * To use with GPFS version 2.3 (but works also with GPFS version 3.1).
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.1 $
 */
/*
 * Copyright (c) 2005, 2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 */


#include "gpfs23_acl.hpp"
#include "path.hpp"
#include "split2.hpp"
#include "users_and_groups.hpp"

#include <cassert>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>


using std::string;
using std::vector;
using std::endl;



// --- static members initialization ---

std::string gpfs23_acl::mmgetacl = "mmgetacl";
std::string gpfs23_acl::mmputacl = "mmputacl";

const fs::path gpfs23_acl::PATH;


// --- gpfs23_acl class implementation --- //

/** Convert a permission string in GPFS format ("rwxc") to an @c
 * permission_t object. The supplied @a rwxc string should hold at
 * least four characters; but only the first four characters of @a
 * rwxc are ever inspected.
 *
 * Since no parameter is passed, telling whether the permission string
 * applies to a file or directory, then the returned permission_t
 * value may have bits set that are meaningful for directories only
 * (e.g., fs_acl::PERM_CREATE_SUBDIRECTORY) and for files only (e.g.,
 * fs_acl::PERM_WRITE_DATA) at the same time.
 * 
 * @param rwxc A string where the textual representation of the
 * permission is stored; must be at least 4 character long.
 *
 * @return An fs_acl::permission_t value corresponding to the supplied
 * @a rwxc textual representation.
 */
fs_acl::permission_t
gpfs23_acl::text_to_permission(const string& rwxc)
{
  fs_acl::permission_t perm = fs_acl::PERM_NONE;

  if ('r' == rwxc[0])
    perm |= 
      PERM_READ_DATA
      |PERM_READ_ACL
      |PERM_LIST_DIRECTORY;
  if ('w' == rwxc[1])
    perm |= 
      PERM_WRITE_DATA
      |PERM_CREATE_FILE
      |PERM_CREATE_SUBDIRECTORY
      |PERM_DELETE_CHILD;
  if ('x' == rwxc[2])
    perm |= 
      PERM_EXECUTE
      |PERM_TRAVERSE_DIRECTORY;
  if ('c' == rwxc[3])
    perm |= PERM_WRITE_ACL;

  return perm;
}


/** Convert an @c permission_t object to the textual
 *  representation. The supplied @a rwxc string should hold at least
 *  four characters.
 * 
 * @param output A string where the textual representation of the
 * permission is stored; must be at least 4 character long.
 *
 * @todo FIXME: Should throw some exception (or otherwise alert
 * caller), when a permission is passed that has no representation in
 * GPFS' "rwxc" format (for instance, when PERM_READ_ACL is set but
 * PERM_READ_DATA is not)?
 * 
 * @return  Textual representation of the GPFS permission.
 */
const std::string&
gpfs23_acl::permission_to_text(const permission_t perm, std::string& output)
{
  output[0] = (((PERM_READ_DATA|PERM_LIST_DIRECTORY) & perm) ? 'r' : '-');
  output[1] = (((PERM_WRITE_DATA
                 |PERM_CREATE_FILE
                 |PERM_CREATE_SUBDIRECTORY
                 |PERM_DELETE_CHILD)
                & perm) ? 'w' : '-');
  output[2] = (((PERM_EXECUTE|PERM_TRAVERSE_DIRECTORY) & perm) ? 'x' : '-');
  output[3] = ((PERM_WRITE_ACL & perm) ?  'c' : '-');
  return output;
}



/** Load ACL from the specified filesystem entry (file or directory).
 * Invokes the @c mmgetacl command (or the command set with @c
 * set_mmgetacl()) to read the whole ACL off a file or directory.
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
 *
 * @exception helper_command::error if @c mmgetacl failed; the
 * error message contains the exit code.
 */
void
gpfs23_acl::load(const string& path, const bool delete_permission_too)
  throw(fs::error, std::exception)
{
  helper_command getacl_cmd(mmgetacl, path);

  // dump ACL with mmgetacl and parse output
  try 
    {
  load_from_mmgetacl(getacl_cmd.run_and_return_stdout());
    }
  catch (bad_input x) 
    {
      throw bad_input(x.what() + string(" from '") + mmgetacl + " " + path +"'");
    }

  // wait for mmgetacl termination
  getacl_cmd.done();

  // check return status
  if (! getacl_cmd.terminated_successfully())
    throw helper_command::bad_termination(getacl_cmd);  
}


/** Used in gpfs23_acl::load_from_mmgetacl() for parsing
    mmgetacl/mmputacl output. */
typedef vector<string> word_container_t;

/** Load ACL from the given stream.  The passed stream is assumed to
 * be formatted exactly like the output of the @c mmgetacl command.
 *
 * @em Note: this operation will @em overwrite existing owner, group
 * owner and mask ACEs, but will @em add per-user and per-group ACEs
 * to the ones already stored in this object.
 *
 * @em Note2: because of the java/cpp interaction over signal
 * handling, we are unable to know the exit status of the mmgetacl
 * command (cfr. @c sigchld.cpp documentation). Therefore we check
 * only for consistency of the output received from the command. At
 * present, we check only if we get at last 5 valid lines from the
 * command.
 *
 * @em UPDATED: it is enough to set the default signal handler (cfr @c
 * sigchld.cpp) to obtain the exact behaviour in both java anc C++
 * test client: this solves the problem! (thnx to Ezio, Riccardo
 * M. and Antonio)
 *
 * @em Note3: this function ignores the optionally present @c
 * '#effective: ----' field in the mmgetacl output. This is not a
 * problem, because @c fs_acl class has its own methods to get the
 * effective permissions.
 *
 * @param mmgetacl_stdout  stream to read ACL contents from.
 *
 * @throws gpfs23_acl::bad_input if it cannot read at least the data
 * regarding standard UNIX permissions (owner, group, other).
 */
void 
gpfs23_acl::load_from_mmgetacl(std::istream& mmgetacl_stdout)
  throw(fs::error, std::exception)
{
  // main loop
  default_acl.clear();
  std::string owner;
  std::string group_owner;
  std::string line;
  unsigned int lines_read = 0;
  bool skip_line=false;
  while (mmgetacl_stdout) 
    {
      line.clear();
      mmgetacl_stdout >> line;

      // We ignores the '#effective: ****' word, but line contains
      // "words", not "lines" of input, so we must check in the
      // following switch() if we have got this "line" and ignore also
      // the following "line"
      if(skip_line && ! line.empty()){
          line.assign(""); // why line.clear() does not work on the
                           // last line of input???
          mmgetacl_stdout >> line;
          skip_line=false;
      }
      if(line.empty())
        continue;

      ++lines_read;
      // break mmgetacl output line into words at ':'
      word_container_t words;
      split2(words, line, ":");

      switch(words[0][0]) 
        {
        case '#': // initial comment holds owner and group owner name...
          switch(words[0][1])
            {
            case 'o': // #owner:...
              assert (! words[1].empty());
              owner.assign(words[1]);
              break;
            case 'g': // #group:...
              assert (! words[1].empty());
              group_owner.assign(words[1]);
              break;              
            default:  // how to manage the line '[...] #effective: ****' ???
                if("#effective" == words[0])
                    skip_line=true; // skip the following word.
            };
          break;
        case 'u': // user:...
          if (words[1].empty()) {
            // owner entry
            assert (! owner.empty());
            assert (! words[2].empty());
            set_owner(uid_from_username(owner), 
                      text_to_permission(words[2]));
          }
          else {
            // named user entry
            assert (! words[2].empty());
            set_user_perm(uid_from_username(words[1]), 
                          text_to_permission(words[2]));
          }
          break;
        case 'g': // group:...
          if (words[1].empty()) {
            // group owner entry
            assert (! group_owner.empty());
            try {
              set_group_owner(gid_from_groupname(group_owner), 
                              text_to_permission(words[2]));
            }
            catch (not_found& x) {
              // hmm... this can only happen if a user
              // was deleted from /etc/passwd in the time
              // that we read the output line from mmgetacl...
              // so unlikely, indeed, that we can ignore it!
              continue;
            }
          }
          else {
            // named group entry
            assert (! words[2].empty());
            try {
              set_group_perm(gid_from_groupname(words[1]), 
                             text_to_permission(words[2]));
            }
            catch (not_found& x) {
              // hmm... this can only happen if a group
              // was deleted from /etc/group in the time
              // that we read the output line from mmgetacl...
              // so unlikely, indeed, that we can ignore it!
              continue;
            }
          }
          break;
        case 'm': // mask:...
          assert (! words[2].empty());
          set_mask(text_to_permission(words[2]));
          break;
        case 'o': // other:...
          assert (! words[2].empty());
          set_other_perm(text_to_permission(words[2]));
          break;
        case 'd': // default:...
          // save default ACLs, because we need to restore them in enforce()
          default_acl.push_back(line);
          break;
        default:
          // should not happen!
          assert ('#' == words[0][0]
                  || 'u' == words[0][0]
                  || 'g' == words[0][0]
                  || 'd' == words[0][0]
                  || 'm' == words[0][0]
                  || 'o' == words[0][0]);
        }
    }
  // rough check that we have at least read
  // the five lines '#owner:..., #group:..., 
  // user::.., group::..., other::...
  // a more fine-grained check could be performed,
  // but this may suffice for now
  if (5 > lines_read) 
    throw bad_input("Insufficient or malformed output");
}


/** Set ACL on the specified filesystem entry (file or directory).
 * Invokes the @c mmputacl command (or the command set with @c
 * set_mmputacl()) to set the whole ACL onto a file or directory.
 *
 * Assumes @c mmputacl can handle numeric UID/GIDs in its input.
 *
 * @todo FIXME: check return status from @c mmputacl
 *
 * @param path file or directory whose ACL is to be set to the
 * contents of this object.
 *
 * @exception fs::helper_command_error if @c mmgetacl failed; the
 * error message contains the exit code.
 */
void
gpfs23_acl::enforce(const string& path)
  const
  throw(fs::error, std::exception)
{
  helper_command putacl_cmd(mmputacl, path);

  // dump ACL to mmputacl's STDIN
  enforce_with_mmputacl(putacl_cmd.run_and_return_stdin());

  // wait for mmgetacl termination
  putacl_cmd.done();

  // check return status
  if (! putacl_cmd.terminated_successfully())
    throw helper_command::bad_termination(putacl_cmd);
}


/** Dump ACL in a format suitable to be passed as input to GPFS' @c
 * mmputacl command.
 *
 * Assumes @c mmputacl can handle numeric UID/GIDs in its input.
 *
 * @param mmputacl_stdin  stream to dump ACL contents to
 */
void 
gpfs23_acl::enforce_with_mmputacl(std::ostream& mmputacl_stdin) 
  const
  throw(fs::error, std::exception)
{
  std::string rwxc("----"); 

  mmputacl_stdin << "#owner:" << username_from_uid(get_owner_uid()) << std::endl;
  mmputacl_stdin << "#group:" << groupname_from_gid(get_group_owner_gid()) << std::endl;

  mmputacl_stdin << "user::" << permission_to_text(get_owner_perm(), rwxc) << endl;
  mmputacl_stdin << "group::" << permission_to_text(get_group_owner_perm(), rwxc) << endl;
  mmputacl_stdin << "other::" << permission_to_text(get_other_perm(), rwxc) << endl;
  if (has_extended_acl() || has_explicit_mask())
    mmputacl_stdin << "mask::" << permission_to_text(get_mask(), rwxc) << endl;

  // output named user ACL
  for(user_acl_t::const_iterator i = user_acl_begin();
      i != user_acl_end();
      ++i)
    mmputacl_stdin << "user:" << i->first << ":" 
                   << permission_to_text(i->second, rwxc) << endl;

  // output named group ACL
  for(group_acl_t::const_iterator i = group_acl_begin();
      i != group_acl_end();
      ++i)
    mmputacl_stdin << "group:" << i->first << ":" 
                   << permission_to_text(i->second, rwxc) << endl;

  // output default ACLs
  for(vector<string>::const_iterator i = default_acl_begin();
      i != default_acl_end();
      ++i)
    mmputacl_stdin << *i;
}


/** Reset objet to newly-constructed state, clearing the stored
 *  ACL. 
 * 
 * @see fs_acl::clear()
 */
void
gpfs23_acl::clear()
{
  // clear attributes in the super class
  posixfs_acl::clear();

  // clear default ACL (new in this class)
  default_acl.clear();
}



// --- protected data structures manipulation ---

/** Set explicit path to the @c mmgetacl command.
 *
 * Note that the path to commands @c mmgetacl and @c mmputacl is @em
 * static, so, if you change it in one instance, it is changed for all
 * instances of %gpfs23_acl
 *
 * <p> If you set this to a non-absolute path, then the command is
 * searched in the system PATH (as determined from the environment
 * variable PATH).
 *
 * @return @c true if the command path was correctly set (an absolute
 * path was passed, or a command with that name was found in the
 * system PATH); @c false if the passed name was not found in PATH.
 */
bool
gpfs23_acl::set_mmgetacl(const string& path)
{
  if ('/' == path[0]) {
    mmgetacl.assign(path);
    return true;
  }
  else 
    // non-absolute path, search in environment PATH
    return PATH.search(path, mmgetacl);
}


/** Set explicit path to the @c mmputacl command. 
 *
 * Note that the path to commands @c mmgetacl and @c mmputacl is @em
 * static, so, if you change it in one instance, it is changed for all
 * instances of %gpfs23_acl
 *
 * <p> If you set this to a non-absolute path, then the command is
 * searched in the system PATH (as determined from the environment
 * variable PATH).
 *
 * @return @c true if the command path was correctly set (an absolute
 * path was passed, or a command with that name was found in the
 * system PATH); @c false if the passed name was not found in PATH.
 */
bool
gpfs23_acl::set_mmputacl(const string& path)
{
  if ('/' == path[0]) {
    mmputacl.assign(path);
    return true;
  }
  else 
    // non-absolute path, search in environment PATH
    return PATH.search(path, mmputacl);
}
