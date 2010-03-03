#define __USE_LARGEFILE64
#define _LARGEFILE_SOURCE
#define _LARGEFILE64_SOURCE

#include <sys/types.h>
#include <sys/stat.h>
#include <grp.h>
#include <stdio.h>
#include <gpfs.h> 

long long stat_get_blocks_size(const char* filename) {
    struct stat64 stat_data;

//    int ret = stat(filename, &stat_data);
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
