/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>

extern "C" {
#include <lcmaps.h>
#include <lcmaps_types.h>
#include <lcmaps_log.h>
#include <lcmaps_return_poolindex_without_gsi.h>
}

#include <stdlib.h>
#include "storm_lcmaps.h"

const char* LCMAPS_LOG_FILE = "LCMAPS_LOG_FILE";

/******************************************************************************
 * Description:
 * Initializes the lcmaps library. This function relies on lcmaps environment
 * variables, specifically: LCMAPS_DB_FILE and LCMAPS_LOG_FILE.
 *
 * Returns:
 * 0: success
 * any value other than 0: failure
 ******************************************************************************/
int init_lcmaps() {

	char* log_file_name = getenv(LCMAPS_LOG_FILE);
	unsetenv(LCMAPS_LOG_FILE);

	int rc = lcmaps_init_and_logfile(log_file_name, NULL,
			(unsigned short) 0x0001);

	return rc;
}

/******************************************************************************
 * Description:
 * Maps Grid credentials to a local user.
 *
 * Input parameters:
 * user_dn: DN
 * fqan_list: FQAN list
 * nfqan: number of elements of the fqan_list list.
 *
 * Output parameters:
 * uid: uid of the local user.
 * gids: list of primary and secondary gids of the local user.
 * ngids: number of elements of the gids list.
 *
 * Returns:
 * 0: success
 * any value other than 0: failure
 ******************************************************************************/
int map_user(const char *user_dn, const char **fqan_list, int nfqan, int *uid,
		void **gids, int *ngids) {

	lcmaps_account_info_t lcmaps_account;
	lcmaps_account_info_init(&lcmaps_account);

	int rc = lcmaps_return_account_without_gsi(const_cast<char*>(user_dn), const_cast<char**>(fqan_list), nfqan, 0, &lcmaps_account);
	if (rc != 0) {
		lcmaps_account_info_clean(&lcmaps_account);
		return rc;
	}

	int npgid = lcmaps_account.npgid;
	int nsgid = lcmaps_account.nsgid;
	int arraySize = npgid + nsgid;

	// Set output data uid and ngids
	*uid = lcmaps_account.uid;
	*ngids = arraySize;

	if (arraySize == 0) {
		*gids = NULL;
		lcmaps_account_info_clean(&lcmaps_account);
		return rc;
	}

	// Alloc memory for the array of gids in the output data gids
	*gids = malloc(sizeof(int) * arraySize);

	int* gidArray = (int*) *gids;

	// Copy primary gids first
	int index = 0;
	int* sourceArray;

	if (npgid > 0) {
		sourceArray = (int*) lcmaps_account.pgid_list;
		for (; index < npgid; index++) {
			gidArray[index] = sourceArray[index];
		}
	}

	if (nsgid > 0) {
		sourceArray = (int*) lcmaps_account.sgid_list;
		for (int i = 0; index < arraySize; index++, i++) {
			gidArray[index] = sourceArray[i];
		}
	}

	lcmaps_account_info_clean(&lcmaps_account);
	return rc;
}

/******************************************************************************
 * Description:
 * Frees the list of gids allocated by the map_user function.
 ******************************************************************************/
void free_gids(void **p) {
	free(*p);
}
