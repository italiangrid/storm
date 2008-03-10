/**
 * @file users_and_groups.hpp
 *
 * Functions for mapping user and group names to UIDs and GIDs and
 * vice-versa.
 *
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 * @version $Revision: 1.2 $
 */
/*
 * Copyright (c) 2005 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the terms
 * in the accompanying file LICENCE.txt
 */

#ifndef __USERS_AND_GROUPS_H
#define __USERS_AND_GROUPS_H


#include "fs_errors.hpp"

#include <sys/types.h>
#include <string>
#include <stdexcept>


/** Return the UID associated with the given user name, or throw an
	exception if not found. */
uid_t uid_from_username (const std::string& name)
	 throw(fs::system_error, std::runtime_error);

/** Return the GID associated with the given group name, or throw an
	exception if not found. */
gid_t gid_from_groupname (const std::string& name)
	 throw(fs::system_error, std::runtime_error);
  
/** Append the user name associated with the given UID to the string
	pointed by @a output. */
std::string username_from_uid (const uid_t uid)
	 throw(fs::system_error);
  
/** Append the group name associated with the given GID to the
	string pointed by @a output.  */
std::string groupname_from_gid (const gid_t gid)
	 throw(fs::system_error);


/** Thrown when any of the above functions cannot map a username to
	UID, groupname to GID or vice-versa. */
class not_found : public std::runtime_error {
public:
  not_found(const std::string& msg) : std::runtime_error(msg) { }
};
  

#endif /* #ifndef __USERS_AND_GROUPS_H */
