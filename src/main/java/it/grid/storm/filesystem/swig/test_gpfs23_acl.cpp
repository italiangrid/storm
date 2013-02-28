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




#include "fs_acl.hpp"
#include "gpfs23_acl.hpp"


// STL
#include <iostream>
#include <sstream>
#include <string>

// Boost.Test
//
// see http://www.boost.org/libs/test for the library home page.
//
#define BOOST_AUTO_TEST_MAIN
#include <boost/test/auto_unit_test.hpp>


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

// reports 'fatal error in "account_test::test_deposit": test m_account.balance() >= 100.0 failed' on error
//BOOST_REQUIRE( m_account.balance() >= 100.0 );

// reports 'fatal error in "account_test::test_deposit": Balance should be more than 500.1, was actual_value' on error
//BOOST_REQUIRE_MESSAGE( m_account.balance() > 500.1,
//                      "Balance should be more than 500.1, was " << m_account.balance());


/// Re-export methods from %gpfs23_acl with 'public' visibility,
/// for testing purposes. 
class __gpfs23_acl : public gpfs23_acl {
public:
  virtual void load_from_mmgetacl(std::istream& mmgetacl_stdout)
    throw(fs::error, std::exception)
  { gpfs23_acl::load_from_mmgetacl(mmgetacl_stdout); }

  virtual void enforce_with_mmputacl(std::ostream& mmputacl_stdin)
    throw(fs::error, std::exception)
  { gpfs23_acl::enforce_with_mmputacl(mmputacl_stdin); }
};


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_text_to_permission )
{
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::text_to_permission("r---"), 
                    fs_acl::PERM_READ_DATA
                    |fs_acl::PERM_LIST_DIRECTORY
                    |fs_acl::PERM_READ_ACL
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::text_to_permission("-w--"), 
                     fs_acl::PERM_WRITE_DATA
                     |fs_acl::PERM_CREATE_SUBDIRECTORY
                     |fs_acl::PERM_CREATE_FILE
                     |fs_acl::PERM_DELETE_CHILD
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::text_to_permission("--x-"), 
                     fs_acl::PERM_EXECUTE
                     |fs_acl::PERM_TRAVERSE_DIRECTORY
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::text_to_permission("---c"), 
                     fs_acl::PERM_WRITE_ACL
                    );
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_permission_to_text )
{
  std::string result("XXXX");

  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_READ_DATA, result),
                    std::string("r---")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_WRITE_DATA, result), 
                    std::string("-w--")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_EXECUTE, result), 
                    std::string("--x-")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_WRITE_ACL, result), 
                    std::string("---c")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_LIST_DIRECTORY, result), 
                    std::string("r---")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_TRAVERSE_DIRECTORY, result), 
                    std::string("--x-")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_CREATE_FILE, result), 
                    std::string("-w--")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_CREATE_SUBDIRECTORY, result), 
                    std::string("-w--")
                    );
  BOOST_CHECK_EQUAL( 
                    gpfs23_acl::permission_to_text(fs_acl::PERM_DELETE_CHILD, result), 
                    std::string("-w--")
                    );
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_load_from_mmgetacl1 )
{
  __gpfs23_acl acl;

  // fake mmgetacl output
  std::istringstream mmgetacl_out(
    "#owner:root\n"
    "#group:root\n"
    "user::rw-c\n"
    "group::r---\n"
    "other::r---\n");

  // load ACL
  acl.load_from_mmgetacl(mmgetacl_out);

  // test if set correctly
  BOOST_CHECK_EQUAL(0, acl.get_owner_uid());
  BOOST_CHECK_EQUAL(0, acl.get_group_owner_gid());

  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA
                                           |fs_acl::PERM_WRITE_DATA
                                           |fs_acl::PERM_READ_ACL
                                           |fs_acl::PERM_WRITE_ACL,
                                           acl.get_owner_perm()));
  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA, 
                                           acl.get_group_owner_perm()));
  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA, 
                                           acl.get_other_perm()));
  BOOST_CHECK_EQUAL(false, acl.has_explicit_mask());
  BOOST_CHECK_EQUAL(false, acl.has_extended_acl());
  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA
                                           |fs_acl::PERM_WRITE_DATA
                                           |fs_acl::PERM_READ_ACL
                                           |fs_acl::PERM_LIST_DIRECTORY
                                           |fs_acl::PERM_TRAVERSE_DIRECTORY
                                           |fs_acl::PERM_DELETE_CHILD
                                           |fs_acl::PERM_CREATE_FILE
                                           |fs_acl::PERM_CREATE_SUBDIRECTORY
                                           |fs_acl::PERM_WRITE_ACL,
                                           acl.get_mask()));
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_load_from_mmgetacl2 )
{
  __gpfs23_acl acl;

  // fake mmgetacl output
  std::istringstream mmgetacl_out(
    "#owner:root\n"
    "#group:root\n"
    "user::rw-c\n"
    "group::r---\n"
    "other::r---\n"
    "mask::rwx-\n"
    "user:1000:r--c\n"
    "group:1001:r--c\n"
    "user:nobody:----\n"
    );

  // load ACL
  acl.load_from_mmgetacl(mmgetacl_out);

  // test if set correctly
  BOOST_CHECK_EQUAL(0, acl.get_owner_uid());
  BOOST_CHECK_EQUAL(0, acl.get_group_owner_gid());

  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA
                                           |fs_acl::PERM_WRITE_DATA
                                           |fs_acl::PERM_READ_ACL
                                           |fs_acl::PERM_WRITE_ACL,
                                           acl.get_owner_perm()));
  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA, 
                                           acl.get_group_owner_perm()));
  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA, 
                                           acl.get_other_perm()));
  BOOST_CHECK_EQUAL(true, acl.has_explicit_mask());
  BOOST_CHECK_EQUAL(true, acl.has_extended_acl());
  BOOST_CHECK(fs_acl::is_permission_subset(fs_acl::PERM_READ_DATA
                                           |fs_acl::PERM_WRITE_DATA
                                           |fs_acl::PERM_READ_ACL
                                           |fs_acl::PERM_LIST_DIRECTORY
                                           |fs_acl::PERM_TRAVERSE_DIRECTORY
                                           |fs_acl::PERM_DELETE_CHILD
                                           |fs_acl::PERM_CREATE_FILE
                                           |fs_acl::PERM_CREATE_SUBDIRECTORY,
                                           acl.get_mask()));
  BOOST_REQUIRE(acl.has_user_perm(1000));
  BOOST_REQUIRE(acl.has_group_perm(1001));
  BOOST_CHECK_EQUAL(acl.get_user_effective_perm(1000),
                    fs_acl::PERM_READ_DATA
                    |fs_acl::PERM_READ_ACL
                    |fs_acl::PERM_LIST_DIRECTORY);
  BOOST_CHECK_EQUAL(acl.get_group_effective_perm(1001),
                    fs_acl::PERM_READ_DATA
                    |fs_acl::PERM_READ_ACL
                    |fs_acl::PERM_LIST_DIRECTORY);

  BOOST_REQUIRE(acl.has_user_perm(65534)); // nobody
  BOOST_CHECK_EQUAL(acl.get_user_effective_perm(65534),
                    fs_acl::PERM_NONE);
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_enforce1 )
{
  __gpfs23_acl acl;

  
  // fake mmgetacl output
  const char* const in =
    "#owner:root\n"
    "#group:root\n"
    "user::rw-c\n"
    "group::r---\n"
    "other::r---\n"
    ;

  std::istringstream mmgetacl_out(in);

  // load ACL
  acl.load_from_mmgetacl(mmgetacl_out);

  // test if output correctly
  std::ostringstream out;
  acl.enforce_with_mmputacl(out);

  BOOST_CHECK(0 == strcmp(out.str().c_str(), in));
}
