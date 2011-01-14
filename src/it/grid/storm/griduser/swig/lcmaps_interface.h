/*
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

#ifndef __LCMAPS_INTERFACE_H
#define __LCMAPS_INTERFACE_H

/* --- include files --- */
#include <sys/types.h>


/* --- data structures definition --- */

/**
 * Account information that StoRM needs back.  The
 * <code>gid_list</code> is a list of all the groups returned by
 * evaluation of LCMAPS plugins; by convention, the primary GID is
 * first in list, although this does not matter in StoRM, as we use
 * group membership only for access rights checking.
 *
 */
class localuser_info {
 public:
  /** Numerical UID of the local user. */
  uid_t uid;

  /** Number of elements in the <code>gid_list</code> array. */
  gid_t *gids;

  /** List of (numerical) GIDs the local user belongs to. */
  size_t ngids;

  /** Constructor, taking user DN and FQANs list. */
  localuser_info (const char *user_dn, const char **fqan_list);

  /** Destructor, frees all memory allocated to the gids list. */
  ~localuser_info();
};


#endif /* __LCMAPS_INTERFACE_H */
