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

/**
 * @file   test_helper_command.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Unit tests of the helper_command class.
 */
/*
 * Copyright (C) 2006 by Antonio Messina <antonio.messina@ictp.it>,
 * Copyright (C) 2006 by Riccardo Murri <riccardo.murri@ictp.it> for
 * the ICTP project EGRID.
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation; either version 2 of the License, or (at your
 *  option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */


#include "helper_command.hpp"


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



// --------------------------------------------------------------------- //
BOOST_AUTO_UNIT_TEST( test_helper_command_stdout )
{
  helper_command cmd("/bin/echo", "test");

  std::istream& cmd_out = cmd.run_and_return_stdout();

  char line[16];
  cmd_out.getline(line, 16, '\n');

  BOOST_CHECK(0 == strcmp(line, "test"));
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_helper_command_return_value_ok )
{
  helper_command cmd("/bin/true", "");

  cmd.run_and_return_stdout();
  cmd.done();

  BOOST_REQUIRE(cmd.terminated_normally());
  BOOST_REQUIRE(cmd.terminated_successfully());
  BOOST_CHECK_EQUAL(0, cmd.get_exit_code());
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_helper_command_return_value_not_ok )
{
  helper_command cmd("/bin/false", "");

  cmd.run_and_return_stdout();
  cmd.done();

  BOOST_REQUIRE(cmd.terminated_normally());
  BOOST_CHECK_EQUAL(false, cmd.terminated_successfully());
  BOOST_CHECK_EQUAL(1, cmd.get_exit_code());
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_bad_termination_ctor )
{
  helper_command cmd("/bin/false", "");

  cmd.run_and_return_stdout();
  cmd.done();

  helper_command::bad_termination x(cmd);

  BOOST_CHECK_EQUAL(0, strcmp("Command '/bin/false ' exited with code 1", 
                              x.what()));
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_helper_command_failed_exec)
{
  helper_command cmd("/some/non/existent/command", "");

  cmd.run_and_return_stdout();
  cmd.done();

  BOOST_CHECK_EQUAL(cmd.get_exit_code(),
                    // on failed execlp(), run_*() performs an exit(-errno)
                    // in the child process; but the exit code of a process
                    // is an 8-bit unsigned value...
                    static_cast<unsigned int>(-ENOENT) & 0xff);
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_escape_metacharacters )
{
  std::string line;
  helper_command::escape_shell_metacharacters(" $!~'\"*&<>;#%()|`?\\", 
                                              line);

  BOOST_CHECK_EQUAL(line, "\\ \\$\\!\\~\\'\\\"\\*\\&\\<\\>\\;\\#\\%\\(\\)\\|\\`\\?\\\\");
}

// --------------------------------------------------------------------- //
BOOST_AUTO_UNIT_TEST( test_helper_command_stdout_with_insane_arg )
{
  helper_command cmd("/bin/echo", "${PATH};${RANDOM}");

  std::istream& cmd_out = cmd.run_and_return_stdout();

  char line[32];
  cmd_out.getline(line, 32, '\n');

  BOOST_CHECK(0 == strcmp(line, "${PATH};${RANDOM}"));
}
