#include <sys/types.h>
#include <stddef.h>
#include <errno.h>
#include <string.h>
#include "dmapi.h"

#define FILE_NOT_FOUND 1000
#define ATTRIBUTE_NOT_FOUND 1001

char* error_string(int err) {
    if (err == FILE_NOT_FOUND) {
        return "File not found";
    } else if (err == ATTRIBUTE_NOT_FOUND) {
        return "Attribute name not found";
    } else {
        return strerror(err);
    }
}

/*
 * Retrieve all of a file's data management attributes.
*/
int dmutil_get_dmattr(const char* filename, const char* attribute_name, char** attribute_value, int* attribute_value_size) {

    size_t default_buffer_size = 16;
    size_t buffer_size;
    size_t attribute_name_size;
    void* buffer = NULL;
    void* dmhandle = NULL;
    size_t dmhandle_len = 0;
    dm_sessid_t sid = DM_NO_SESSION;
    dm_attrname_t attrname;
    int ret;

    if ((filename == NULL) || (attribute_name == NULL)) {
        return EINVAL;
    }

    attribute_name_size = strlen(attribute_name);
    if (attribute_name_size > DM_ATTR_NAME_SIZE) {
        return EINVAL;
    }

    if (dm_create_session(DM_NO_SESSION, "dmattr", &sid) != 0) {
        return errno;
    }

    if (dm_path_to_handle((char *) filename, (void**) &dmhandle, &dmhandle_len) != 0) {

        dm_destroy_session(sid);

        if (errno == ENOENT) {
            return FILE_NOT_FOUND;
        }

        return errno;
    }

    memset((void *) attrname.an_chars, 0, DM_ATTR_NAME_SIZE);
    memcpy((void *) attrname.an_chars, attribute_name, attribute_name_size);

    buffer = malloc(default_buffer_size);

    ret = dm_get_dmattr(sid, dmhandle, dmhandle_len, DM_NO_TOKEN, &attrname, default_buffer_size, buffer, &buffer_size);

    if (ret != 0 ) {
        if (errno == E2BIG) {
            free(buffer);
            buffer = malloc(buffer_size);
            ret = dm_get_dmattr(sid, dmhandle, dmhandle_len, DM_NO_TOKEN, &attrname, buffer_size, buffer, &buffer_size);
        }

        if (ret != 0) {
            free(buffer);
            dm_handle_free(dmhandle, dmhandle_len);
            dm_destroy_session(sid);
            if (errno == ENOENT) {
                return ATTRIBUTE_NOT_FOUND;
            }
            return errno;
        }
    }

    *attribute_value_size = buffer_size;
    *attribute_value = (char *) buffer;

    dm_handle_free(dmhandle, dmhandle_len);
    dm_destroy_session(sid);

    return 0;
}



int dmutil_set_dmattr(char* filename, char* attribute_name, char* attribute_value) {

    void* dmhandle = NULL;
    size_t dmhandle_len = 0;
    size_t attribute_name_size;
    dm_sessid_t sid = DM_NO_SESSION;
    dm_attrname_t attrname;
    int ret;

    if ((filename == NULL) || (attribute_name == NULL)) {
        return EINVAL;
    }

    attribute_name_size = strlen(attribute_name);
    if (attribute_name_size > DM_ATTR_NAME_SIZE) {
        return EINVAL;
    }

    if (dm_create_session(DM_NO_SESSION, "dmattr", &sid) != 0) {
        return errno;
    }

    if (dm_path_to_handle(filename, (void**) &dmhandle, &dmhandle_len) != 0) {

        dm_destroy_session(sid);

        if (errno == ENOENT) {
            return FILE_NOT_FOUND;
        }

        return errno;
    }

    memset((void *) attrname.an_chars, 0, DM_ATTR_NAME_SIZE);
    memcpy((void *) attrname.an_chars, attribute_name, attribute_name_size);

    ret = dm_set_dmattr(sid, dmhandle, dmhandle_len, DM_NO_TOKEN, &attrname, 0, strlen(attribute_value),
            attribute_value);

    if (ret != 0) {
        dm_handle_free(dmhandle, dmhandle_len);
        dm_destroy_session(sid);
        return errno;
    }

    dm_handle_free(dmhandle, dmhandle_len);
    dm_destroy_session(sid);

    return 0;
}



int dmutil_rm_dmattr(char* filename, char *attribute_name) {

    void *dmhandle = NULL;
    size_t dmhandle_len = 0;
    size_t attribute_name_size;
    dm_sessid_t sid = DM_NO_SESSION;
    dm_attrname_t attrname;
    int ret;

    if ((filename == NULL) || (attribute_name == NULL)) {
        return EINVAL;
    }

    attribute_name_size = strlen(attribute_name);
    if (attribute_name_size > DM_ATTR_NAME_SIZE) {
        return EINVAL;
    }

    if (dm_create_session(DM_NO_SESSION, "dmattr", &sid) != 0) {
        return errno;
    }

    if (dm_path_to_handle(filename, (void**) &dmhandle, &dmhandle_len) != 0) {

        dm_destroy_session(sid);

        if (errno == ENOENT) {
            return FILE_NOT_FOUND;
        }

        return errno;
    }

    memset((void *) attrname.an_chars, 0, DM_ATTR_NAME_SIZE);
    memcpy((void *) attrname.an_chars, attribute_name, attribute_name_size);

    ret = dm_remove_dmattr(sid, dmhandle, dmhandle_len, DM_NO_TOKEN, 0, &attrname);

    if (ret != 0) {
        dm_handle_free(dmhandle, dmhandle_len);
        dm_destroy_session(sid);
        if (errno == ENOENT) {
            return ATTRIBUTE_NOT_FOUND;
        }
        return errno;
    }

    dm_handle_free(dmhandle, dmhandle_len);
    dm_destroy_session(sid);

    return 0;
}

//char* dmutil_getall_dmattr(char *filename) {
//
//    int i;
//    dm_attrlist_t *bufp;
//    dm_attrlist_t *sbufp;
//
//    char* output = (char*) malloc(sizeof(char *) * 16 * 16);
//    void* dmhandle = NULL;
//    size_t dmhandle_len = 0;
//    size_t rlen = 0;
//    dm_attrname_t dmname;
//    int ret, len;
//    dm_vardata_t* varp;
//    dm_sessid_t sid = DM_NO_SESSION;
//
//    if (filename == NULL) {
//        return NULL;
//    }
//
//    if (dm_create_session(DM_NO_SESSION, "dmattr", &sid) != 0) {
//        fprintf(stderr, "dm_create_session: failed, %s\n", strerror(errno));
//        return NULL;
//    }
//
//    if (dm_path_to_handle(filename, &dmhandle, &dmhandle_len) != 0) {
//        fprintf(stderr,"dm_path_to_handle: failed, %s\n", strerror(errno));
//        dm_destroy_session(sid);
//        return NULL;
//    }
//
//    bufp = (dm_attrlist_t *) malloc(sizeof(dm_attrlist_t) * 16);
//    ret = dm_getall_dmattr(sid, dmhandle, dmhandle_len, DM_NO_TOKEN, sizeof(bufp), bufp, &rlen);
//    if (ret != 0) {
//        if (errno == E2BIG) {
//            bufp = (dm_attrlist_t *) malloc(rlen);
//            ret = dm_getall_dmattr(sid, dmhandle, dmhandle_len, DM_NO_TOKEN, sizeof(bufp), bufp, &rlen);
//        }
//        if (ret != 0) {
//            free(bufp);
//            fprintf(stderr,"dm_getall_dmattr: failed, %s\n", strerror(errno));
//            dm_handle_free(dmhandle, dmhandle_len);
//            dm_destroy_session(sid);
//            return NULL;
//        }
//    }
//
//    sbufp = bufp;
//    while (sbufp != NULL) {
//       printf("%s\n",sbufp->al_name.an_chars);
//       sprintf(strchr(output, 0), "%s ",sbufp->al_name.an_chars);
//       varp = DM_GET_VALUE(sbufp, al_data, dm_vardata_t*);
//       len=DM_GET_LEN(sbufp, al_data);
//       for(i=0;i<len;i++) {
//          unsigned char c=((unsigned char*)varp)[i];
//          printf("%02x",c);
//          sprintf(strchr(output,0),"%01c",c);
//       }
//       printf("\n");
//       sprintf(strchr(output,0)," ");
//       sbufp = DM_STEP_TO_NEXT(sbufp, dm_attrlist_t *);
//    }
//
//    dm_handle_free(dmhandle, dmhandle_len);
//    dm_destroy_session(sid);
//    sprintf(strchr(output,0),"\n");
//    printf("output: %s", output);
//    return output;
//}
//
//





