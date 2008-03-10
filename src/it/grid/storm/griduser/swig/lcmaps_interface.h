/**
 * lcmaps_interface.h
 *
 * Utility functions for interfacing with LCMAPS.
 *
 * These bindings are tailored to provide only the subset of
 * functionalities needed by the StoRM ``GridUser`` object, namely,
 * extract LCMAPS FQANs from a certificate and map a GridUser to a local
 * UID/GID based on the DN and FQANs.
 *
 *
 * Copyright (c) 2005 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 *
 * You may copy, modify and distribute this file under the terms of
 * the INFN Grid Licence; see the accompanying file LICENCE.
 * 
 *
 * This file use DOC++ (see http://docpp.sourceforge.net/ ) for inline
 * documentation; in short, DOC++ uses the same syntax as JavaDoc.
 *
 * $Id: lcmaps_interface.h,v 1.3 2006/03/28 08:22:32 rmurri Exp $
 *
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
