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
 * @file   lcmaps_interface.c
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Utility functions for mapping grid user credentials to a local
 * account with LCMAPS.
 *
 * <p> These bindings are tailored to provide only the subset of
 * functionalities needed by the StoRM ``GridUser`` object, namely,
 * get a local user UID and groups from the certificate subject DN and
 * the VOMS FQANs.
 *
 * <p> Thanks to Martijn Steenbakkers for explanations on LCMAPS!
 */
/*
 * Copyright (c) 2005,2006 Riccardo Murri <riccardo.murri@ictp.it>
 * for the EGRID/INFN joint project StoRM.
 *
 * You may copy, modify and distribute this file under the same terms
 * expressed in the accompanying file LICENCE.
 * 
 * This file use Doxygen (see http://www.doxygen.org/ ) for inline
 * documentation; in short, Doxygen uses the same syntax as JavaDoc.
 */

static const char* const 
RCSID = "$Id: lcmaps_interface.cpp,v 1.4 2007/05/16 09:50:04 lmagnoni Exp $";


#ifndef DEBUG
#define __DEBUG__(x) /* ignore */
#else
#include <iostream>
#define __DEBUG__(x) x
#endif



/* --- include files --- */

#include "lcmaps_interface.h"

#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <sys/types.h>
#include <stdexcept>
#include <sstream>

/* LCMAPS */
extern "C" {
#include <lcmaps_return_poolindex_without_gsi.h>
}



/* --- module-local functions --- */

/** Count the number of elements in a NULL-terminated list of strings.
 *
 * @return  The number of elements in the list (not counting the final NULL).
 */
static inline size_t
count_fqans ( const char** fqans) 
{
  size_t i = 0;
  for ( const char** p = fqans; *p; p++)
    i++;

  return i;  
}

//SET FQAN
void set_fqan(char** fqans) {
  char** p = fqans;
  for (int i=0;i<3;i++) {
  	*p="pippo";
  	p++;
  }
}


/* --- global functions, exported to Java --- */

/** Destructor.  If the <code>ngids</code> field is > 0, free the
 * pointer <code>gids</code>; otherwise frees only the parameter
 * <code>x</code>.
 */
localuser_info::~localuser_info()
{
	//fprintf(stderr, "Destructor start\n");
  /* preconditions */
  assert ((ngids > 0) ? (NULL != gids) : 1);

  if (NULL != gids)
    delete[] gids;
    //fprintf(stderr, "Destructor ends\n");
}


/** Constructor, taking user's certificate subject DN and a (possibly
 * empty) list of VOMS FQANs.  Call LCMAPS to map the passed
 * credentials to a local user, and fill member fields with the
 * returned local user credentials.
 *
 * @param user_dn the user certificate subject DN; cannot be NULL nor
 * an empty string.
 *
 * @param fqan_list a NULL-terminated array of VOMS FQANs; can be
 * NULL, to pass an empty FQAN list.
 */
//localuser_info::localuser_info (const char *user_dn,  const char* fqan)
localuser_info::localuser_info (const char *user_dn, const char **fqan_list)
  : uid(0), ngids(0), gids(NULL)
{

	// std::cout << "##### localuser_info DEBUG  ^^^^ START ^^^^ ####" << std::endl;

// fprintf(stderr, "start0\n");
//  char** fqan_list = (char**) malloc(sizeof(char**));
// *fqan_list=(char*)fqan;
// fqan_list[1]=NULL;
//set_fqan(fqan_list);
//const char* user_dn= "pippo";
//const char* user_dn=NULL;

  
  /* preconditions */
  assert (NULL != user_dn);
  assert ('\0' != *user_dn);

  int nfqans;
  if (NULL != fqan_list)
    nfqans = count_fqans (fqan_list);
  else 
    nfqans = 0;

  lcmaps_account_info_t lcmaps_account;
  int rc = lcmaps_account_info_init (&lcmaps_account);
  if (0 != rc) {
    std::ostringstream msg;
    msg << "lcmaps_account_info_init() returned " << rc;
    throw std::runtime_error(msg.str().c_str());
  }
  
//DEBUG PRINT
  
//  fprintf(stderr, "number of fqans: %i\n", nfqans);
//   for(int i = 0; i<nfqans; ++i) {
//  		fprintf(stderr, "FQAN: %s\n", fqan_list[i]);
//   }

  // std::cout << "##### debug info ####" << std::endl;
  // std::cout << " DN = " << user_dn << std::endl;
  // std::cout << " FQAN size = " << nfqans << std::endl;
  // for(int i = 0; i<nfqans; ++i) {
	 //  std::cout << "   FQAN[" << i << "]" << fqan_list[i] << std::endl;
  // }
  // std::cout << "   uid  before " << uid << std::endl;
  // std::cout << "   ngid before " << ngids << std::endl;

  rc = lcmaps_return_account_without_gsi (const_cast<char*>(user_dn), 
                                          const_cast<char**>(fqan_list), 
                                          nfqans,
                                          0,
                                          &lcmaps_account);
 
                                        
  if (0 != rc) {
    lcmaps_account_info_clean(&lcmaps_account);

    // build error message
    std::ostringstream msg;
    msg << "lcmaps_return_poolindex_without_gsi('"
        << user_dn <<"',{";
    for(int i = 0; i<nfqans; ++i) {
      msg << "'" << fqan_list[i] << "'";
      if (i < nfqans -1)
        msg << ",";
    }
    msg << "}) returned " << rc;
    throw std::runtime_error(msg.str().c_str());
  }


  uid = lcmaps_account.uid;
  // std::cout << "   uid  after " << uid << std::endl;
  // std::cout << "   ngid after " << ngids << std::endl;
  
  if (lcmaps_account.nsgid > 0) 
    ngids = lcmaps_account.npgid + lcmaps_account.nsgid;
  else
    ngids = lcmaps_account.npgid;
  
  // std::cout << " lcmaps_account.uid = " << lcmaps_account.uid  << std::endl;
  // std::cout << " lcmaps_account.npgid (primary)  = " << lcmaps_account.npgid << std::endl;
  // std::cout << " lcmaps_account.nsgid (secondary)= " << lcmaps_account.nsgid  << std::endl;
  // std::cout << " number of GIDs (sum) = " << ngids  << std::endl;

  /* copy GIDs, primary first */
  
  gids = new gid_t[ngids];
  gid_t *q = gids;
  gid_t *p = lcmaps_account.pgid_list;
  for (int i = 0; i < lcmaps_account.npgid; i++)
    q[i] = p[i];
  if (lcmaps_account.nsgid > 0) {
    p = lcmaps_account.sgid_list;
    for (int i = 0; i < lcmaps_account.nsgid; i++)
      q[i + lcmaps_account.npgid] = p[i];
  }

  // std::cout << "##### RETURN VALUES ####" << std::endl;
  // std::cout << "   uid  = " << uid << std::endl;
  // std::cout << "   ngid = " << ngids << std::endl;
  // for(int i = 0; i<ngids; ++i) {
  //	  std::cout << "   gid[" << i << "]" << gids[i] << std::endl;
  // }
  // std::cout << "##### ^^^^^^^^^^^^^ ####" << std::endl;

  /* XXX: need to check return value? */
  lcmaps_account_info_clean (&lcmaps_account);
  
  // std::cout << "##### localuser_info DEBUG  ---- END ---- ####" << std::endl;

}
