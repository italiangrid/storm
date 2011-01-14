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
 * @file   test_path.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Unit tests of the fs::path class.
 */



#include "path.hpp"


// STL
#include <string>
#include <vector>

// Boost.Test
//
// see http://www.boost.org/libs/test for the library home page.
//
#define BOOST_AUTO_TEST_MAIN
#include <boost/test/auto_unit_test.hpp>



// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_path_env )
{
  fs::path PATH;
  std::string sh;

  BOOST_CHECK_EQUAL( true, PATH.search("sh", sh) );
  BOOST_CHECK_EQUAL("/bin/sh", sh);
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_path_bogus )
{
  fs::path PATH("/some:/very:/bogus/path");
  std::string sh;

  BOOST_CHECK_EQUAL( false, PATH.search("sh", sh) );
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_path_nonexistent_command )
{
  fs::path PATH;
  std::string sh;

  BOOST_CHECK_EQUAL( false, PATH.search("a-nonexistent-command", sh) );
}


