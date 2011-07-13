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

#define __USE_LARGEFILE64
#define _LARGEFILE_SOURCE
#define _LARGEFILE64_SOURCE

#include <sys/types.h>
#include <sys/stat.h>
#include <grp.h>
#include <stdio.h>
#include <gpfs.h>

long long stat_get_blocks(const char* filename) {
    struct stat64 stat_data;

    int ret = stat(filename, &stat_data);

    if (ret != 0) {
        return -1;
    }

    long long blocks = stat_data.st_blocks;
    return blocks;
}


long long gpfs_stat_get_blocks_size(const char* filename) {
    struct stat64 stat_data;

    int ret = gpfs_stat((char*) filename, &stat_data);

    if (ret != 0) {
        return -1;
    }

    long long blocks_size = 512 * stat_data.st_blocks;
    return blocks_size;
}



int set_file_group(const char* filename, const char* groupname) {
    struct group *gr;

    gr = getgrnam(groupname);

    if (gr == NULL) {
        return -1;
    }

    int ret = chown(filename, -1, gr->gr_gid);

    return ret;
}
