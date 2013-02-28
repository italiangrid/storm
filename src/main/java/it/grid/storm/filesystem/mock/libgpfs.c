#define __USE_LARGEFILE64
#define _LARGEFILE_SOURCE
#define _LARGEFILE64_SOURCE

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <gpfs.h>

int
gpfs_stat(char *pathname,
          stat64_t *buffer)
{
  return 0;
}

int
gpfs_prealloc(gpfs_file_t fileDesc,
              gpfs_off64_t startOffset,
              gpfs_off64_t bytesToPrealloc)
{
  return 0;
}

int gpfs_getacl(char *pathname,
                int   flags,
                void *acl)
{
  return 0;
}

int gpfs_putacl(char *pathname,
                int   flags,
                void *acl)
{
  return 0;
}
