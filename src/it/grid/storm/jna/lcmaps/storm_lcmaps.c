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

int init_lcmaps() {

	char* log_file_name = getenv(LCMAPS_LOG_FILE);
	unsetenv(LCMAPS_LOG_FILE);

	int rc = lcmaps_init_and_logfile(log_file_name, NULL, (unsigned short) 0x0001);

	return rc;
}

int map_user(const char *user_dn, const char **fqan_list, int nfqan, int *uid,
		void **gids, int *ngids) {

	*uid = 14;
	*gids = (int *) malloc(sizeof(int) * 10);

	int i;
	for (i = 0; i < 10; i++) {
		*gids[i] = i;
	}

	lcmaps_account_info_t plcmaps_account;
	lcmaps_account_info_init(&plcmaps_account);

	int rc = lcmaps_return_poolindex_without_gsi(const_cast<char*>(user_dn), const_cast<char**>(fqan_list), nfqan, &plcmaps_account);

	printf("Result code: %d\n", rc);
	printf("uid: %d\n", plcmaps_account.uid);

	lcmaps_account_info_clean(&plcmaps_account);

	return rc;
}

void free_gids(void **p) {
	free(*p);
}
