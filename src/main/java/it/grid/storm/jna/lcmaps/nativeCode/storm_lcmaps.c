/*
 * storm_lcmaps.c
 *
 *  Created on: 14/lug/2011
 *      Author: Michele Dibenedetto
 */


#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>

#include "storm_lcmaps.h"

#include <lcmaps/lcmaps.h>
#include <lcmaps/lcmaps_account.h>
#include <lcmaps/lcmaps_return_poolindex_without_gsi.h>


signed int map_user(char* logfile, char *user_dn, char **fqan_list, int nfqan, int *uid, int *gid) {

	//initialize lcmaps logging (must be done at each mapping)
	int rc = lcmaps_init_and_logfile(logfile, NULL,
							(unsigned short) 0x0003);
	if (rc != 0)
	{
			//error, lcmaps initialization failed
			return 1;
	}
	//create an lcmaps_account object for the mapping
	lcmaps_account_info_t lcmaps_account;
	rc = lcmaps_account_info_init(&lcmaps_account);
	if(rc != 0)
	{
		//error, lcmaps_account object creation failed
		return 2;
	}

	//map the user with the provided user_dn and fqan_list in the lcmaps_account object
//      rc = lcmaps_return_account_without_gsi(const_cast<char*>(user_dn), const_cast<char**>(fqan_list), nfqan, 0, &lcmaps_account);
	rc = lcmaps_return_account_without_gsi(user_dn, fqan_list, nfqan, 0, &lcmaps_account);
	if (rc != 0) {
		//error, lcmaps_return_account_without_gsi call failed
		//clean the lcmaps_account object
		lcmaps_account_info_clean(&lcmaps_account);
		return 3;
	}

        // Set output data uid and gid
	*uid = lcmaps_account.uid;

	//check that at least one gid (primary or secondary) is available
	if ((lcmaps_account.npgid + lcmaps_account.nsgid) == 0) {
		//error, no gids provided by the lcmaps_return_account_without_gsi call
		//clean the lcmaps_account object and gid parameter
		gid = NULL;
		lcmaps_account_info_clean(&lcmaps_account);
		return 4;
    }

	//check if a primary gid is available
	if (lcmaps_account.npgid > 0) {
		//set the gid parameter to the first primary gid available
		*gid = ((int*) lcmaps_account.pgid_list)[0];
	}
	else
	{
		//check if a secondary gid is available
		if (lcmaps_account.nsgid > 0) {
			//set the gid parameter to the first secondary gid available
			*gid = ((int*) lcmaps_account.sgid_list)[0];
		}
		else
		{
			//error, unexpected condition, this code should be nor reachable
			gid = NULL;
			lcmaps_account_info_clean(&lcmaps_account);
			return 5;
		}
	}
	//clean the lcmaps_account object and free its fields memory
	lcmaps_account_info_clean(&lcmaps_account);
	//return success
	return 0;
}
