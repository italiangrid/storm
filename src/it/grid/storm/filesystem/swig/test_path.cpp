/**
 * @file   test_path.cpp
 * @author Riccardo Murri <riccardo.murri@ictp.it>
 *
 * Unit tests of the fs::path class.
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


