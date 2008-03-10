/**
 * @file   test_split2.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Unit tests of the spli2() function.
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



