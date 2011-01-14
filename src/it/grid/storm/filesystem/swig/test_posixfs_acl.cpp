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




// compile with:
//    g++ url.cpp test_url_boost.cpp -lboost_unit_test_framework
// possible options:
// --log_level=all 
// --report_level=detailed

#include "posixfs_acl.hpp"

// Boost.Test
#include <boost/test/unit_test.hpp>
using boost::unit_test::test_suite;
using boost::unit_test::test_case;

// STL
#include <functional>
#include <iostream>
#include <memory>
#include <vector>

#include <fcntl.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>

#include <utility>

using namespace std;

inline bool bitwise_leq(const int j, const int i){ return (((i&j)==j)?true:false);}

struct posixfs_acl_test {
  // simple test fixture

  posixfs_acl file_acl;
  posixfs_acl dir_acl;
  string filename;
  string dirname;
  typedef vector<pair<fs_acl::permission_t, acl_perm_t> > conversion_test_results_t  ;
  conversion_test_results_t conversion_test;

  ~posixfs_acl_test(){
    unlink(filename.c_str());
  }

  void test_init()
  {
    // different kinds of non-critical tests
    // they report the error and continue

    // standard assertion
    // reports 'error in "account_test::test_init": test m_account.balance() >= 0.0 failed' on error
    //BOOST_CHECK( m_account.balance() >= 0.0 );

    // customized assertion
    // reports 'error in "account_test::test_init": Initial balance should be more then 1, was actual_value' on error
    //BOOST_CHECK_MESSAGE( m_account.balance() > 1.0,
    //                     "Initial balance should be more then 1, was " << m_account.balance() );

    // equality assertion (not very wise idea use equlality check on floating point values)
    // reports 'error in "account_test::test_init": test m_account.balance() == 5.0 failed [actual_value != 5]' on error
    //BOOST_CHECK_EQUAL( m_account.balance(), 5.0 );

    // closeness assertion for floating-point numbers (symbol (==) used to mark closeness, (!=) to mark non closeness )
    // reports 'error in "account_test::test_init": test m_account.balance() (==) 10.0 failed [actual_value (!=) 10 (1e-010)]' on error
    //BOOST_CHECK_CLOSE( m_account.balance(), 10.0, /* tolerance */ 1e-10 );

    // reports 'fatal error in "account_test::test_deposit": test m_account.balance() >= 100.0 failed' on error
    //BOOST_REQUIRE( m_account.balance() >= 100.0 );

    // reports 'fatal error in "account_test::test_deposit": Balance should be more than 500.1, was actual_value' on error
    //BOOST_REQUIRE_MESSAGE( m_account.balance() > 500.1,
    //                      "Balance should be more than 500.1, was " << m_account.balance());

    // reports 'fatal error in "account_test::test_deposit": test std::not_equal_to<double>()(m_account.balance(), 999.9) failed
    //          for (999.9, 999.9)' on error
    //BOOST_REQUIRE_PREDICATE( std::not_equal_to<double>(), 2, (m_account.balance(), 999.9) );

    char *ftmp = strdup("TEST.F.XXXXXX");
    filename=mktemp(ftmp);
    mode_t fmode=S_IFREG | 0600;
    if(0 != mknod(filename.c_str(),fmode,(dev_t)0))
      throw string("Unable to create file "+filename);

    cout << "Created file "<<filename<<endl;

    conversion_test.push_back(make_pair(fs_acl::PERM_READ_DATA, 04));
    conversion_test.push_back(make_pair(fs_acl::PERM_WRITE_DATA, 02));
    conversion_test.push_back(make_pair(fs_acl::PERM_EXECUTE, 01));
    conversion_test.push_back(make_pair(fs_acl::PERM_READ_ACL, 04));
    conversion_test.push_back(make_pair(fs_acl::PERM_WRITE_ACL, 02));
    conversion_test.push_back(make_pair(fs_acl::PERM_TRAVERSE_DIRECTORY, 01));
    conversion_test.push_back(make_pair(fs_acl::PERM_LIST_DIRECTORY, 04));
    conversion_test.push_back(make_pair(fs_acl::PERM_CREATE_FILE, 02));
    conversion_test.push_back(make_pair(fs_acl::PERM_CREATE_SUBDIRECTORY, 02));
    conversion_test.push_back(make_pair(fs_acl::PERM_DELETE_CHILD, 02));
    conversion_test.push_back(make_pair(fs_acl::PERM_NONE, 00));
    // In posix non ha senso PERM_DELETE, quindi la to_permission_t
    // NON mappa indietro PERM_DELETE
    conversion_test.push_back(make_pair(fs_acl::PERM_ALL & ~fs_acl::PERM_DELETE, 07));


  }


  void test_posixfs_to_permission(){
    for(conversion_test_results_t::const_iterator i = conversion_test.begin();
        i != conversion_test.end();
        ++i){
      BOOST_CHECK_EQUAL(i->first, (posixfs_acl::to_permission_t(i->second) & i->first)); 
    }
  }

  void test_posixfs_from_permission(){
    for(conversion_test_results_t::const_iterator i = conversion_test.begin();
        i != conversion_test.end();
        ++i)
      BOOST_CHECK_EQUAL(posixfs_acl::from_permission_t(i->first), i->second); 
  }


  void directory_permission(){
    char *ftmp = strdup("TEST.D.XXXXXX");
    string dirname=mktemp(ftmp);
    if(0 != mkdir(dirname.c_str(),(mode_t)0700))
      throw string("Unable to create directory "+dirname);
    
    uid_t uid = getuid();
    vector<gid_t> gid;
    gid.push_back(getgid());

    // Check create permission
    dir_acl.clear();
    dir_acl.load(dirname);
    BOOST_CHECK_EQUAL(true,dir_acl.access(fs_acl::PERM_CREATE_FILE,uid,gid));

    // create a file and check delete permission
    string f(dirname+"/a");
    if(0 != mknod(f.c_str(),(mode_t)(S_IFREG | 0600),(dev_t)0))
      throw string("Unable to create file "+f);

    // Can delete file in dirname?
    dir_acl.clear();
    dir_acl.load(dirname);
    BOOST_CHECK_EQUAL(true,dir_acl.access(fs_acl::PERM_DELETE_CHILD,uid,gid));

    // file f can be deleted?
    file_acl.clear();
    file_acl.load(f,true);
    BOOST_CHECK_EQUAL(true,file_acl.access(fs_acl::PERM_DELETE,uid,gid));

    // Same check but with directory permission == 0100
    if(-1 == chmod(dirname.c_str(),(mode_t)0100))
        throw string("unable to chmod() directory "+dirname);
    
    // sould be impossible to delete the file
    file_acl.clear();
    file_acl.load(f,true);
    BOOST_CHECK_EQUAL(false,file_acl.access(fs_acl::PERM_DELETE,uid,gid));

    // sould be impossible to delete file inside 'dirname'
    dir_acl.clear();
    dir_acl.load(dirname);
    BOOST_CHECK_EQUAL(false,dir_acl.access(fs_acl::PERM_DELETE_CHILD,uid,gid));

    // sould be impossible to create file inside 'dirname'
    dir_acl.clear();
    dir_acl.load(dirname);
    BOOST_CHECK_EQUAL(false,dir_acl.access(fs_acl::PERM_CREATE_FILE,uid,gid));
    
    chmod(dirname.c_str(),(mode_t)0700);
    unlink(f.c_str());
    rmdir(dirname.c_str());

  }

  void unix_user_read_perm(){
    // Change permission bit of the file for the user using chmod.
    // read permission bit of the file using file_acl.get_owner_perm()
    umask((mode_t)000);
    for(mode_t i=0; i < 8; ++i){
      if(0 != chmod(filename.c_str(),i<<6))
        throw string("unable to chmod() file "+filename);
      file_acl.load(filename);
      BOOST_CHECK_EQUAL(file_acl.get_owner_perm(), posixfs_acl::to_permission_t(i));
    }
  }

  void unix_user_write_perm(){
    // change permissions of the file using file_acl.enforce()
    // read permissions with stat()
    umask((mode_t)000);
    chmod(filename.c_str(),0);      
    for(mode_t i=0; i < 8; ++i){
      file_acl.clear();
      file_acl.set_owner_perm(i);
      file_acl.set_group_owner_perm(0);
      file_acl.set_other_perm(0);
      file_acl.enforce(filename);
      
      // read file permissions back
      struct stat buf;
      stat(filename.c_str(),&buf);

      // check that the permission rad via stat() is exactly what fs_acl should have set
      BOOST_CHECK_EQUAL(i, (buf.st_mode & S_IRWXU)>>6);
    }
  }

  void unix_user_access(){
    // Set user permissions with chmod(). Check access with file_acl.access()
    umask((mode_t)000);
    for(mode_t i=0; i < 8; ++i){
      if(0 != chmod(filename.c_str(),i<<6))
        throw string("unable to chmod() file "+filename);

      file_acl.clear();
      file_acl.load(filename);

      // test file access by user/group owner
      vector<gid_t> gid;
      gid.push_back(file_acl.get_group_owner_gid());
      vector<gid_t> othergid;
      othergid.push_back(file_acl.get_group_owner_gid()+1);

      for(int j=0;j<8;j++){

        // can user owner access with permission j<i (bitwise <) ?
        if(bitwise_leq(j,i))
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid(), gid));
        else
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid(), gid));
          
        // can group owner access with permission j?        
        BOOST_CHECK_EQUAL((j==0? true: false), 
                          file_acl.access(j, file_acl.get_owner_uid()+1, gid));


        if(bitwise_leq(j,i))
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid(), othergid));
        else
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid(), othergid));
        
        // can not user/group-owner access with permission j?
        BOOST_CHECK_EQUAL((j==0? true: false), 
                          file_acl.access(j, file_acl.get_owner_uid()+1, othergid));
      }
    }
  }

  void unix_group_read_perm(){
    // Change permission bit of the file for the user using chmod.
    // read permission bit of the file using file_acl.get_owner_perm()
    //    umask((mode_t)000);
    for(mode_t i=0; i < 8; ++i){
      if(0 != chmod(filename.c_str(),i<<3))
        throw string("unable to chmod() file "+filename);
      file_acl.load(filename); 
      BOOST_CHECK_EQUAL(file_acl.get_group_owner_perm(), posixfs_acl::to_permission_t(i));
    }
  }

  void unix_group_write_perm(){
    // change permissions of the file using file_acl.set_user_perm()
    // read permissions with stat()
      chmod(filename.c_str(),(mode_t)000);
      for(mode_t i=0; i < 8; ++i){
        file_acl.clear();
        file_acl.load(filename);
        file_acl.set_owner_perm(0);
        file_acl.set_group_owner_perm(i);
        file_acl.set_other_perm(0);
        file_acl.enforce(filename);
        //      memset(&buf,0,sizeof(struct stat));
        struct stat buf;
        stat(filename.c_str(),&buf);
        BOOST_CHECK_EQUAL(i, (buf.st_mode & S_IRWXG)>>3);
      }
  }

  void unix_group_access(){
    // Set user permissions with chmod(). Check access with file_acl.access()
    umask((mode_t)000);
    for(mode_t i=0; i < 8; ++i){
      if(0 != chmod(filename.c_str(),i<<3))
        throw string("unable to chmod() file "+filename);

      file_acl.clear();
      file_acl.load(filename);

      // test file access by user/group owner
      vector<gid_t> gid;
      gid.push_back(file_acl.get_group_owner_gid());
      vector<gid_t> othergid;
      othergid.push_back(file_acl.get_group_owner_gid()+1);

      for(int j=0;j<8;j++){

        // can user owner access with permission j<i (bitwise <) ?
        if(j==0) // User permissions takes precedence over group
                 // permissions
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid(), gid));
        else
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid(), gid));
          
        if(bitwise_leq(j,i))
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid()+1, gid));
        else
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid()+1, gid));
        
        // can not user/group-owner access with permission j?
        BOOST_CHECK_EQUAL((j==0? true: false), 
                          file_acl.access(j, file_acl.get_owner_uid()+1, othergid));

        // can group not-owner access with permission j?        
        BOOST_CHECK_EQUAL((j==0? true: false), 
                          file_acl.access(j, file_acl.get_owner_uid(), othergid));

      }
    }
  }

  void unix_other_read_perm(){
    // Change permission bit of the file for the user using chmod.
    // read permission bit of the file using file_acl.get_owner_perm()
    // change permissions of the file using file_acl.set_user_perm()
    // read permissions with stat()
    umask((mode_t)000);
    for(mode_t i=0; i < 8; ++i){
      if(0 != chmod(filename.c_str(),i))
        throw string("unable to chmod() file "+filename);
      file_acl.load(filename);
      BOOST_CHECK_EQUAL(file_acl.get_other_perm(), posixfs_acl::to_permission_t(i));
    }
  }

  void unix_other_write_perm(){
    // change permissions of the file using file_acl.set_user_perm()
    // read permissions with stat()
      chmod(filename.c_str(),(mode_t)000);
      for(mode_t i=0; i < 8; ++i){
        file_acl.clear();
        file_acl.set_owner_perm(0);
        file_acl.set_group_owner_perm(0);
        file_acl.set_other_perm(i);
        file_acl.enforce(filename);
        struct stat buf;
        stat(filename.c_str(),&buf);
        BOOST_CHECK_EQUAL(i, (buf.st_mode & S_IRWXO));
    }
  }

  void unix_other_access(){
    umask((mode_t)000);
    for(mode_t i=0; i < 8; ++i){
      if(0 != chmod(filename.c_str(),i))
        throw string("unable to chmod() file "+filename);

      file_acl.clear();
      file_acl.load(filename);

      // test file access by user/group owner
      vector<gid_t> gid;
      gid.push_back(file_acl.get_group_owner_gid());
      vector<gid_t> othergid;
      othergid.push_back(file_acl.get_group_owner_gid()+1);

      for(int j=0;j<8;j++){

        if(j==0){ // special case. We can always access to the file
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid(), gid));
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid(), othergid));
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid()+1, gid));
        }else{
          // Only not-owner and not-group-owner can access.
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid(), gid));
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid(), othergid));
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid()+1, gid));
        }

        if(bitwise_leq(j,i))
          BOOST_CHECK_EQUAL((true), 
                            file_acl.access(j, file_acl.get_owner_uid()+1, othergid));
        else
          BOOST_CHECK_EQUAL((false), 
                            file_acl.access(j, file_acl.get_owner_uid()+1, othergid));
        
      }
    
    }
  }

  // ACL 
  void acl_user_read(){
    for(int perm=0; perm < 8; ++perm){
      std::string command("setfacl -b "+filename);
      if(-1 == system(command.c_str()))
        throw std::string("cannot execute command '"+command+"'");

      ostringstream cmd;
      uid_t uid=getuid()+1;
      cmd << "setfacl -m u:" << uid 
          << ":" << perm << " " << filename;

      system(cmd.str().c_str());
      
      file_acl.clear();
      file_acl.load(filename);
      
      BOOST_CHECK_EQUAL(true,bitwise_leq(file_acl.get_user_effective_perm(uid), posixfs_acl::to_permission_t(perm)));
      BOOST_CHECK_EQUAL(false,!bitwise_leq(file_acl.get_user_effective_perm(uid), posixfs_acl::to_permission_t(perm)));
    }
  }

  void acl_user_write(){
  }
  void acl_user_maks_read(){
  }
  void acl_user_mask_write(){
  }
  void acl_user_access(){
    for(int perm=0; perm < 8; ++perm){
      if(0 != chmod(filename.c_str(),(mode_t)0000))
        throw string("unable to chmod() directory "+dirname);

      std::string command("setfacl -b "+filename);
      if(-1 == system(command.c_str()))
        throw std::string("cannot execute command '"+command+"'");

      vector<gid_t> gid;
      gid.push_back(getgid()+1);

      vector<gid_t> othergid(gid);
      othergid[0]++;

      uid_t uid=getuid()+1;

      ostringstream cmd;
      cmd << "setfacl -m u:" << uid
          << ":" << perm << " " << filename;
      system(cmd.str().c_str());
      
      file_acl.clear();
      file_acl.load(filename);

      fs_acl::permission_t fperm = posixfs_acl::to_permission_t(perm);

      for(int i=0; i<8; i++){
        fs_acl::permission_t pperm = posixfs_acl::to_permission_t(i);

        // check for low-level bug...
        BOOST_REQUIRE(true  == file_acl.has_user_perm(uid));
        BOOST_REQUIRE(false == file_acl.has_group_perm(gid[0]));
        BOOST_REQUIRE(fperm == file_acl.get_user_effective_perm(uid));
        BOOST_REQUIRE(0     == file_acl.get_group_effective_perm(gid[0]));
        BOOST_REQUIRE(0     == file_acl.get_group_effective_perm(getuid()));

        BOOST_CHECK_EQUAL(bitwise_leq(pperm,fperm), file_acl.access(pperm, uid, gid));
        BOOST_CHECK_EQUAL(bitwise_leq(pperm,fperm), file_acl.access(pperm, uid, othergid));

        // false because the user acl (file owner permission, in this
        // case) has precedence over group acl
        BOOST_CHECK_EQUAL(bitwise_leq(pperm,0), file_acl.access(pperm, getuid(), gid));
        BOOST_CHECK_EQUAL(bitwise_leq(pperm,0), file_acl.access(pperm, getuid(), othergid));
      }
    }
  }

  void acl_group_read(){
    for(int perm=0; perm < 8; ++perm){
      std::string command("setfacl -b "+filename);
      if(-1 == system(command.c_str()))
        throw std::string("cannot execute command '"+command+"'");

      ostringstream cmd;
      gid_t gid=getgid()+1;
      cmd << "setfacl -m g:" << gid 
          << ":" << perm << " " << filename;

      system(cmd.str().c_str());
      
      file_acl.clear();
      file_acl.load(filename);
      
      BOOST_CHECK_EQUAL(true,bitwise_leq(file_acl.get_group_effective_perm(gid), posixfs_acl::to_permission_t(perm)));
      BOOST_CHECK_EQUAL(false,!bitwise_leq(file_acl.get_group_effective_perm(gid), posixfs_acl::to_permission_t(perm)));
    }
  }
  void acl_group_write(){
  }
  void acl_group_maks_read(){
  }
  void acl_group_mask_write(){
  }

  void acl_group_access(){
    for(int perm=0; perm < 8; ++perm){
      if(0 != chmod(filename.c_str(),(mode_t)0000))
        throw string("unable to chmod() directory "+dirname);

      std::string command("setfacl -b "+filename);
      if(-1 == system(command.c_str()))
        throw std::string("cannot execute command '"+command+"'");

      vector<gid_t> gid;
      gid.push_back(getgid()+1);

      vector<gid_t> othergid(gid);
      othergid[0]++;

      uid_t uid=getuid()+1;

      ostringstream cmd;
      cmd << "setfacl -m g:" << gid[0]
          << ":" << perm << " " << filename;
      system(cmd.str().c_str());
      
      file_acl.clear();
      file_acl.load(filename);
      
      fs_acl::permission_t fperm = posixfs_acl::to_permission_t(perm);

      for(int i=0; i<8; i++){
        fs_acl::permission_t pperm = posixfs_acl::to_permission_t(i);

        // check for low-level bug...
        BOOST_REQUIRE(false == file_acl.has_user_perm(uid));
        BOOST_REQUIRE(true  == file_acl.has_group_perm(gid[0]));
        BOOST_REQUIRE(fperm == file_acl.get_group_effective_perm(gid[0]));
        BOOST_REQUIRE(0     == file_acl.get_user_effective_perm(uid));
        BOOST_REQUIRE(0     == file_acl.get_user_effective_perm(getuid()));

        BOOST_CHECK_EQUAL(bitwise_leq(pperm,fperm), file_acl.access(pperm, uid, gid));
        BOOST_CHECK_EQUAL(bitwise_leq(pperm,0), file_acl.access(pperm, uid, othergid));

        // false because the user acl (file owner permission, in this
        // case) has precedence over group acl
        BOOST_CHECK_EQUAL(bitwise_leq(pperm,0), file_acl.access(pperm, getuid(), gid));
        BOOST_CHECK_EQUAL(bitwise_leq(pperm,0), file_acl.access(pperm, getuid(), othergid));
      }
    }
  }
  
};



struct posixfs_acl_test_suite : public test_suite {
  posixfs_acl_test_suite()  : test_suite("posixfs_acl_test_suite") {
    
    set_terminate (__gnu_cxx::__verbose_terminate_handler);
    // add member function test cases to a test suite
    boost::shared_ptr<posixfs_acl_test> instance( new posixfs_acl_test( ) );
    
    // Initialization
    test_case* init_test_case = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::test_init, instance );
    
    add( init_test_case );

    /***** UNIX permission tests ********/

    // USER permission test case
    test_case* user_read_perm_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_user_read_perm, instance );
    user_read_perm_case->depends_on( init_test_case );

    test_case* user_write_perm_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_user_write_perm, instance );
    user_write_perm_case->depends_on( init_test_case );

    test_case* user_access_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_user_access, instance );
    user_access_case->depends_on( user_read_perm_case );

    add( user_read_perm_case );
    add( user_write_perm_case );
    add( user_access_case );

    // GROUP permission test case
    test_case* group_read_perm_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_group_read_perm, instance );
    group_read_perm_case->depends_on( init_test_case );

    test_case* group_write_perm_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_group_write_perm, instance );
    group_write_perm_case->depends_on( init_test_case );

    test_case* group_access_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_group_access, instance );
    group_access_case->depends_on( group_read_perm_case );

    add( group_read_perm_case );
    add( group_write_perm_case );
    add( group_access_case );

    // OTHER permission test case
    test_case* other_read_perm_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_other_read_perm, instance );
    other_read_perm_case->depends_on( init_test_case );

    test_case* other_write_perm_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_other_write_perm, instance );
    other_write_perm_case->depends_on( init_test_case );

    test_case* other_access_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::unix_other_access, instance );
    other_access_case->depends_on( other_read_perm_case );
    
    add( other_read_perm_case );
    add( other_write_perm_case );
    add( other_access_case );

    /***** ACEs TESTS ****/

    // User ACL Read
    test_case* acl_user_read_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::acl_user_read, instance );
    acl_user_read_case->depends_on( user_read_perm_case );

    add( acl_user_read_case );

    // User ACL access
    test_case* acl_user_access_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::acl_user_access, instance );
    acl_user_access_case->depends_on( user_access_case );

    add( acl_user_access_case );

    // Group ACL Read
    test_case* acl_group_read_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::acl_group_read, instance );
    acl_group_read_case->depends_on( group_read_perm_case );

    add( acl_group_read_case );

    // Group ACL access
    test_case* acl_group_access_case  = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::acl_group_access, instance );
    acl_group_access_case->depends_on( group_access_case );

    add( acl_group_access_case );



    // Check {to,from}_permission_t
    test_case *to_permission_t_case = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::test_posixfs_to_permission, instance );
    to_permission_t_case->depends_on( init_test_case );

    add( to_permission_t_case );

    test_case *from_permission_t_case = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::test_posixfs_from_permission, instance );
    from_permission_t_case->depends_on( init_test_case );

    add( from_permission_t_case );


    // Check directory permission to create/delete file
    test_case *directory_permission_case = 
      BOOST_CLASS_TEST_CASE( &posixfs_acl_test::directory_permission, instance );
    acl_group_read_case->depends_on( to_permission_t_case );
    acl_group_read_case->depends_on( from_permission_t_case );

    add( directory_permission_case );


  }
};

test_suite*
init_unit_test_suite( int argc, char * argv[] ) {
  test_suite* test( BOOST_TEST_SUITE( "Unit test example 3" ) );

  test->add( new posixfs_acl_test_suite() );

  return test;
}

// EOF


/***   TEST DA FARE    ***\
  Prereq
    creazione file  

  Lettura
    1.(D) imposto mode con chmod(); leggo lo stesso modo con fs_acl.load()
    
    2.(D) aggiungo ACL per utente con system("setfacl -m"); leggo lo stesso risultato con fs_acl.load()
    3.(D) aggiungo ACL per gruppo con system("setfacl -m"); leggo lo stesso risultato con fs_acl.load()
    4. modifico maschera con system("setfacl -m"); leggo lo stesso valore con fs_acl.load();

 
  Modifica
    1.(D) imposto con fs_acl.enforce(); leggo lo stesso modo con stat()

    2. aggiungo ACL per utente con fs_acl.enforce(); controllo con system("getfacl|grep");
    3. aggiungo ACL per gruppo con fs_acl.enforce(); controllo con system("getfacl|grep");
    4. modifico maschera con fs_acl.enforce(); controllo con system("getfacl|grep");
 

  Accesso (dipende da lettura)
  ============================
  Posto y i permessi con cui richiediamo l'accesso:
    1.(D) imposto con chmod(x00); controllo che:
       1. (utente giusto, gruppo giusto)       ==>  accede <==> y <= x;
       2. (utente sbagliato, gruppo giusto)    ==>  accede <==> y == 0;
       3. (utente giusto, gruppo sbagliato)    ==>  accede <==> y <= x;
       4. (utente sbagliato, gruppo sbagliato) ==>  accede <==> y == 0;

    2.(D) imposto con chmod(0x0); controllo che:
       1. (utente giusto, gruppo giusto)       ==>  accede <==> y == 0;
       2. (utente sbagliato, gruppo giusto)    ==>  accede <==> y <= x;
       3. (utente giusto, gruppo sbagliato)    ==>  accede <==> y == 0;
       4. (utente sbagliato, gruppo sbagliato) ==>  accede <==> y == 0;

    3.(D) imposto con chmod(00x); controllo che:
       1. (utente giusto, gruppo giusto)       ==>  accede <==> y == 0;
       2. (utente sbagliato, gruppo giusto)    ==>  accede <==> y == 0;
       3. (utente giusto, gruppo sbagliato)    ==>  accede <==> y == 0;
       4. (utente sbagliato, gruppo sbagliato) ==>  accede <==> y <= x;

    4.(D) imposto chmod(000); imposto singola ACL per utente X: "setfacl -m u:X:Y". Controllo che:
       1. (X, qualunue gruppo)        ==>  accede <==>  Z <= Y;
       2. (non-X, qualunque gruppo)   ==>  accede <==>  Z == 0;

    5.(D) imposto chmod(000); imposto singola ACL per gruppo X: "setfacl -m g:X:Y". Controllo che:
       1. (utente not owner, X)       ==>  accede <==>  Z <= Y;
       2. (owner, X)                  ==>  accede <==>  Z == 0;
       2. (qualunque utente, non-X)   ==>  accede <==>  Z == 0;

    6.(D) Creo una directory scrivibile all'utente e verifico con
       access() di poter creare e cancellare file.

    7.(D) Creo una directory NON scrivibile all'utente e verifico con
       access() di NON poter creare o cancellare file.


  Nota: i precedenti confronti vanno intesi in senso bitwise, quindi
  la condizione y <= x e' verificata se e solo se ((x & y) == y). La
  funzione bitwise_leq e' pensata per semplificare tale controllo.

- la acl del ut. proprietario ha precedenza su tutto il resto
- la acl per un utente ha precedenza sulle acl di gruppo
- la acl del gruppo proprietario ha precedenza sulle acl degli altri gruppi
- la acl di other viene controllata solo se non ci sono match per per i gruppi

  ACL:
  true:
    acl utente
    acl gruppo
    acl other

  false:
    acl utente
    acl gruppo
    acl other


***/

