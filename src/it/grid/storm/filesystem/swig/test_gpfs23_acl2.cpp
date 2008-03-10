#include "gpfs.hpp"

#include <string>
#include <iostream>
#include <pthread.h>
#include <sstream>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


using namespace std;

string _fs("/storage/egrid");
string prefix("/storage/egrid/antonio.d/cpptest.");

void    *iteration(void *ptr){
    int i = *((int *)ptr);
    ostringstream str;
    str<<prefix;
    str<<i;

    string file(str.str());
    creat(file.c_str(),0644);

//     gpfs23_acl::set_mmgetacl("/bin/cat /tmp/acl.txt #");
//     gpfs23_acl::set_mmputacl(">/dev/null /usr/bin/md5sum #");
    try{
        fs::gpfs f(_fs);
        fs_acl *a = f.new_acl();        
        a->load(file);
        
        fs_acl::permission_t perm = fs_acl::PERM_TRAVERSE_DIRECTORY;
        a->grant_user_perm((uid_t)502,perm);
        a->enforce(file);
    }
    catch (fs::error e){
        cout << "Exception fs::error: "<< e.what()<<endl;
    }
    catch (std::exception e){
        cout << "Exception std::exception: "<< e.what()<<endl;
    }
    catch(...){
        cout << "Unknown exception"<<endl;
    }

    pthread_exit(NULL);
    return NULL ;
}


void *iteration2(void *ptr){
    int i = *((int *)ptr);
    ostringstream str;
    str<<prefix;
    str<<i;

    string file(str.str());
//    creat(file.c_str(),0644);
    
    string cmdline("./test_gpfs.sh");
    cmdline+=" ";
    cmdline+=file;
    system(cmdline.c_str());

}

main(int argc, char **argv){

    int nthread=10;
    if(argc>1)
        nthread=atoi(argv[1]);
    pthread_t *p = new pthread_t[nthread];
    if(NULL == p){
        cerr<<"Unable to allocate memory!"<<endl;
        return -1;
    }
    for(int i=0;i<nthread;i++){
        pthread_create(&p[i],NULL,iteration,&i);
    }
//    sleep(10);
     for(int i=0;i<nthread;i++){
         pthread_join(p[i],NULL);
     }
    

}
