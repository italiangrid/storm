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




#include "split2.hpp"


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

BOOST_AUTO_UNIT_TEST( test_split2 )
{
  std::vector<std::string> v;

  split2(v, "a b\tc");

  BOOST_REQUIRE(v.size() == 3);
  BOOST_CHECK_EQUAL("a", v[0]);
  BOOST_CHECK_EQUAL("b", v[1]);
  BOOST_CHECK_EQUAL("c", v[2]);
}



// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_split2_with_delimiter )
{
  std::vector<std::string> v;

  split2(v, ".:/bin:/usr/bin", ":");

  BOOST_REQUIRE(v.size() == 3);
  BOOST_CHECK_EQUAL(".", v[0]);
  BOOST_CHECK_EQUAL("/bin", v[1]);
  BOOST_CHECK_EQUAL("/usr/bin", v[2]);
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_split2_with_delimiter2 )
{
  std::vector<std::string> v;

  split2(v, "a: b:\tc", ":");

  BOOST_REQUIRE(v.size() == 3);
  BOOST_CHECK_EQUAL("a", v[0]);
  BOOST_CHECK_EQUAL("b", v[1]);
  BOOST_CHECK_EQUAL("c", v[2]);
}


// --------------------------------------------------------------------- //

BOOST_AUTO_UNIT_TEST( test_split2_with_delimiter_and_null_ignore_class )
{
  std::vector<std::string> v;

  split2(v, "a: b:\tc", ":", NULL);

  BOOST_REQUIRE(v.size() == 3);
  BOOST_CHECK_EQUAL("a", v[0]);
  BOOST_CHECK_EQUAL(" b", v[1]);
  BOOST_CHECK_EQUAL("\tc", v[2]);
}



