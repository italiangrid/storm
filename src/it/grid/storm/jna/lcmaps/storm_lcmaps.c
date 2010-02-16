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

extern "C" void map_user(const char *user_dn, const char **fqan_list, int nfqan, int *uid, int **gids, int *ngids) {

   *uid=14;
   *gids = (int *) malloc(sizeof(int) * 10);

    int i;
    for (i=0; i<10; i++) {
        *gids[i] = i;
    }

/*
    lcmaps_init_and_logfile("alb-lcmaps.log", NULL, (unsigned short)0x0001);
    lcmaps_account_info_t plcmaps_account;
    lcmaps_account_info_init(&plcmaps_account);

    int rc = lcmaps_return_poolindex_without_gsi(const_cast<char*>(user_dn), const_cast<char**>(fqan_list), nfqan, &plcmaps_account);

    printf("Result code: %d\n", rc);
    printf("uid: %d\n", plcmaps_account.uid);

    lcmaps_account_info_clean(&plcmaps_account);
*/

}

extern "C" void prova(void **gids) {

    *gids =  malloc(sizeof(int) * 10);

     int *p = (int*) *gids;

     printf("2");

     int i;
    for (i=0; i<10; i++) {
        p[i] = 1000-i;
    }
}

